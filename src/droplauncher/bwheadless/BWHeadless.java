/*
 * Copyright (C) 2017 Adakite
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package droplauncher.bwheadless;

import adakite.debugging.Debugging;
import adakite.exception.InvalidArgumentException;
import adakite.exception.InvalidStateException;
import droplauncher.util.KillableTask;
import adakite.ini.Ini;
import adakite.ini.exception.IniParseException;
import adakite.util.AdakiteUtils;
import droplauncher.bwapi.BWAPI;
import droplauncher.bwapi.bot.exception.InvalidBotTypeException;
import adakite.process.CommandBuilder;
import adakite.settings.Settings;
import droplauncher.util.process.CustomProcess;
import adakite.windows.task.Task;
import adakite.windows.task.TaskTracker;
import adakite.windows.task.Tasklist;
import adakite.windows.task.exception.TasklistParseException;
import droplauncher.bwapi.bot.Bot;
import droplauncher.bwapi.bot.exception.MissingBotFileException;
import droplauncher.bwapi.bot.exception.MissingBotNameException;
import droplauncher.bwapi.bot.exception.MissingBotRaceException;
import droplauncher.bwapi.bot.exception.MissingBwapiDllException;
import droplauncher.bwheadless.exception.MissingBotException;
import droplauncher.starcraft.Starcraft;
import droplauncher.starcraft.exception.MissingStarcraftExeException;
import droplauncher.DropLauncher;
import droplauncher.bwheadless.exception.MissingBWHeadlessExeException;
import droplauncher.mvc.view.ConsoleOutputWrapper;
import droplauncher.mvc.view.View;
import droplauncher.util.process.exception.ClosePipeException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Locale;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

/**
 * Class for handling execution and communication with bwheadless.
 */
public class BWHeadless {

  private enum PropertyKey {

    /* Specific to bwheadless.exe */
    STARCRAFT_EXE("starcraft_exe"),
    NETWORK_PROVIDER("network_provider"),
    CONNECT_MODE("connect_mode"), /* join or host */
    GAME_NAME("game_name"), /* game name when hosting */
    MAP("map"), /* map when hosting */

    /* Specific to this class */
    BWHEADLESS_EXE("bwheadless_exe")
    ;

    private final String str;

    private PropertyKey(String str) {
      this.str = str;
    }

    @Override
    public String toString() {
      return this.str;
    }

  }

  /**
   * Enum for passable arguments to bwheadless.exe.
   */
  public enum Argument {

    STARCRAFT_EXE("-e"), /* requires second string */
    HOST("-h"),
    GAME_NAME("-g"), /* requires second string */
    JOIN_GAME("-j"),
    MAP("-m"), /* requires second string */
    BOT_NAME("-n"), /* requires second string */
    BOT_RACE("-r"), /* requires second string */
    LOAD_DLL("-l"), /* requires second string */
    ENABLE_LAN("--lan"),
    STARCRAFT_INSTALL_PATH("--installpath") /* requires second string */
    ;

    private final String str;

    private Argument(String str) {
      this.str = str;
    }

    @Override
    public String toString() {
      return str;
    }

  }

  public static final String DEFAULT_EXE_FILENAME = "bwheadless.exe";

  private Settings settings;
  private CustomProcess bwheadlessProcess;
  private CustomProcess botProcess;
  private Bot bot;
  private ConsoleOutputWrapper consoleOutput;
  private TaskTracker taskTracker;

  public BWHeadless() {
    this.settings = new Settings();
    this.bwheadlessProcess = new CustomProcess();
    this.botProcess = new CustomProcess();
    this.bot = new Bot();
    this.consoleOutput = null;
    this.taskTracker = new TaskTracker();

    this.settings.set(PropertyKey.BWHEADLESS_EXE.toString(), DEFAULT_EXE_FILENAME);
  }

  /**
   * Returns the path to the bwheadless.exe.
   *
   * @throws MissingBWHeadlessExeException if path is not set
   */
  public Path getPath() throws MissingBWHeadlessExeException {
    if (!this.settings.hasValue(PropertyKey.BWHEADLESS_EXE.toString())) {
      throw new MissingBWHeadlessExeException();
    }
    String val = this.settings.getValue(PropertyKey.BWHEADLESS_EXE.toString());
    return Paths.get(val);
  }

  /**
   * Sets the specified path to bwheadlesse.exe.
   *
   * @param path specified path
   * @throws InvalidArgumentException if the specified path is null
   */
  public BWHeadless setPath(Path path) throws InvalidArgumentException {
    if (path == null) {
      throw new InvalidArgumentException(Debugging.cannotBeNull("path"));
    }
    this.settings.set(PropertyKey.BWHEADLESS_EXE.toString(), path.toString());
    return this;
  }

  /**
   * Returns the path to the target StarCraft.exe.
   *
   * @throws MissingStarcraftExeException if the path to StarCraft.exe is not set
   */
  private Path getStarcraftExe() throws MissingStarcraftExeException {
    if (!this.settings.hasValue(PropertyKey.STARCRAFT_EXE.toString())) {
      throw new MissingStarcraftExeException();
    }
    String val = this.settings.getValue(PropertyKey.STARCRAFT_EXE.toString());
    return Paths.get(val);
  }

  /**
   * Returns the path to the StarCraft directory.
   *
   * @see #getStarcraftExe()
   * @throws MissingStarcraftExeException
   * @throws IOException
   */
  private Path getStarcraftPath() throws MissingStarcraftExeException,
                                         IOException {
    Path parent = AdakiteUtils.getParentDirectory(getStarcraftExe());
    if (parent == null) {
      throw new IOException();
    }
    return parent;
  }

  /**
   * Sets the specified path to StarCraft.exe.
   *
   * @param starcraftExe specified path
   * @throws InvalidArgumentException if the specified path is null
   */
  public BWHeadless setStarcraftExe(Path starcraftExe) throws InvalidArgumentException {
    if (starcraftExe == null) {
      throw new InvalidArgumentException(Debugging.cannotBeNull("starcraftExe"));
    }
    this.settings.set(PropertyKey.STARCRAFT_EXE.toString(), starcraftExe.toString());
    return this;
  }

  /**
   * Returns the internal bot object which will later be used to
   * run with bwheadless.exe.
   */
  public Bot getBot() {
    return this.bot;
  }

  /**
   * Sets the internal bot object to the specified bot object.
   *
   * @throws InvalidArgumentException if the specified bot object is null
   */
  public BWHeadless setBot(Bot bot) throws InvalidArgumentException {
    if (bot == null) {
      throw new InvalidArgumentException(Debugging.cannotBeNull("bot"));
    }
    this.bot = bot;
    return this;
  }

  /**
   * Enables and sets the internal console output UI object to which to print.
   *
   * @param consoleOutput specified UI object
   */
  public BWHeadless enableConsoleOutput(ConsoleOutputWrapper consoleOutput) {
    if (consoleOutput == null) {
      throw new IllegalArgumentException(Debugging.cannotBeNull("consoleOutput"));
    }
    this.consoleOutput = consoleOutput;
    return this;
  }

  /**
   * Disables printing to the internal console output UI object by setting it
   * to null.
   */
  public BWHeadless disableConsoleOutput() {
    this.consoleOutput = null;
    return this;
  }

  //TODO: Test: After the files have been loaded, delete them and try to start.
  /**
   * Starts bwheadless after configuring and checking settings.
   *
   * @throws IOException if an I/O error occurs
   * @throws MissingBotException if the bot object is not set
   * @throws InvalidBotTypeException if the bot type is not recognized
   * @throws IniParseException
   * @throws MissingBotNameException
   * @throws MissingBotRaceException
   * @throws MissingBotFileException
   * @throws MissingBwapiDllException
   * @throws MissingStarcraftExeException
   * @throws InvalidArgumentException
   * @throws InvalidStateException
   * @throws TasklistParseException
   * @throws MissingBWHeadlessExeException
   */
  public void start() throws IOException,
                             MissingBotException,
                             InvalidBotTypeException,
                             IniParseException,
                             MissingBotNameException,
                             MissingBotRaceException,
                             MissingBotFileException,
                             MissingBwapiDllException,
                             MissingStarcraftExeException,
                             InvalidArgumentException,
                             InvalidStateException,
                             TasklistParseException,
                             MissingBWHeadlessExeException {
    this.taskTracker.reset();

    /* Check for StarCraft.exe */
    Path starcraftPath = getStarcraftPath();
    if (!AdakiteUtils.fileReadable(starcraftPath)) {
      throw new IOException("failed to access " + Starcraft.DEFAULT_EXE_FILENAME + ": " + starcraftPath.toString());
    }

    configureBwapi(starcraftPath);


    /* Compile bwheadless arguments. */
    CommandBuilder bwhCommand = new CommandBuilder();
    bwhCommand.setPath(getPath().toAbsolutePath());
    bwhCommand.addArg(Argument.STARCRAFT_EXE.toString());
    bwhCommand.addArg(getStarcraftExe().toAbsolutePath().toString());
    bwhCommand.addArg(Argument.JOIN_GAME.toString());
    bwhCommand.addArg(Argument.BOT_NAME.toString());
    bwhCommand.addArg(this.bot.getName());
    bwhCommand.addArg(Argument.BOT_RACE.toString());
    bwhCommand.addArg(this.bot.getRace());
    bwhCommand.addArg(Argument.LOAD_DLL.toString());
    bwhCommand.addArg(this.bot.getBwapiDll().toAbsolutePath().toString());
    bwhCommand.addArg(Argument.ENABLE_LAN.toString());
    bwhCommand.addArg(Argument.STARCRAFT_INSTALL_PATH.toString());
    bwhCommand.addArg(starcraftPath.toString());

    /* Start bwheadless. */
    this.bwheadlessProcess
        .setCWD(starcraftPath)
        .setProcessName(DEFAULT_EXE_FILENAME)
        .setConsoleOutput(this.consoleOutput);
    this.bwheadlessProcess.start(bwhCommand.get());

    /* Start bot client. */
    if (this.bot.getType() == Bot.Type.CLIENT) {
      /* Compile bot client arguments. */
      CommandBuilder clientCommand = new CommandBuilder();
      switch (AdakiteUtils.getFileExtension(this.bot.getPath()).toLowerCase(Locale.US)) {
        case "exe":
          clientCommand.setPath(this.bot.getPath());
          break;
        case "jar":
          if (!AdakiteUtils.fileExists(DropLauncher.JRE_EXE)) {
            throw new FileNotFoundException(DropLauncher.JRE_EXE.toAbsolutePath().toString());
          }
          clientCommand.setPath(DropLauncher.JRE_EXE);
          clientCommand.addArg("-jar");
          clientCommand.addArg(this.bot.getPath().toAbsolutePath().toString());
          break;
        default:
          throw new InvalidBotTypeException(FilenameUtils.getName(this.bot.getPath().toString()));
      }
      this.botProcess
          .setCWD(starcraftPath)
          .setProcessName(View.MessagePrefix.BOT.toString())
          .setConsoleOutput(this.consoleOutput);
      this.botProcess.start(clientCommand.get());
    }
  }

  /**
   * Stops the bwheadless and bot processes.
   *
   * @throws IOException if an I/O error occurs
   * @throws ClosePipeException
   * @throws MissingBotFileException
   * @throws TasklistParseException
   */
  public void stop() throws IOException,
                            ClosePipeException,
                            MissingBotFileException,
                            TasklistParseException {
    /* Kill new tasks that were started after bwheadless. */
    String botName = FilenameUtils.getBaseName(this.bot.getPath().toString());
    this.taskTracker.update();
    for (Task task : this.taskTracker.getNewTasks()) {
      /* Kill bot client. */
      if (this.bot.getType() == Bot.Type.CLIENT && botName.contains(task.getImageName())) {
        println(View.MessagePrefix.DROPLAUNCHER.get() + View.MessagePrefix.KILL.get() + task.getPID() + " " + task.getImageName());
        Tasklist.kill(task.getPID());
        continue;
      }
      /* Only kill tasks whose names start with known associated tasks. */
      for (KillableTask kt : KillableTask.values()) {
        if (task.getImageName().toLowerCase(Locale.US).startsWith(FilenameUtils.getBaseName(kt.toString()).toLowerCase(Locale.US))) {
          println(View.MessagePrefix.DROPLAUNCHER.get() + View.MessagePrefix.KILL.get() + task.getPID() + " " + task.getImageName());
          Tasklist.kill(task.getPID());
          break;
        }
      }
    }

    this.bwheadlessProcess.stop();
    if (this.bot.getType() == Bot.Type.CLIENT) {
      this.botProcess.stop();
    }
  }

  /**
   * Configures BWAPI in the specified StarCraft directory.
   *
   * @param starcraftPath specified path to the StarCraft directory
   * @throws IOException
   * @throws MissingStarcraftExeException
   * @throws IniParseException
   * @throws MissingBotFileException
   * @throws InvalidBotTypeException
   * @throws InvalidArgumentException
   */
  private void configureBwapi(Path starcraftPath) throws IOException,
                                                         MissingStarcraftExeException,
                                                         IniParseException,
                                                         MissingBotFileException,
                                                         InvalidBotTypeException,
                                                         InvalidArgumentException {
    /* Create common BWAPI paths. */
    Path bwapiAiPath = starcraftPath.resolve(BWAPI.DATA_AI_PATH);
    Path bwapiReadPath = starcraftPath.resolve(BWAPI.DATA_READ_PATH);
    Path bwapiWritePath = starcraftPath.resolve(BWAPI.DATA_WRITE_PATH);
    Path bwapiDataDataPath = starcraftPath.resolve(BWAPI.DATA_DATA_PATH);
    Path bwapiIniPath = starcraftPath.resolve(BWAPI.DATA_INI_PATH);
    Path bwapiBroodwarMap = bwapiDataDataPath.resolve(BWAPI.ExtractableFile.BROODWAR_MAP.toString());
    AdakiteUtils.createDirectory(bwapiAiPath);
    AdakiteUtils.createDirectory(bwapiReadPath);
    AdakiteUtils.createDirectory(bwapiWritePath);
    AdakiteUtils.createDirectory(bwapiDataDataPath);

    /* Create BWTA/BWTA2 paths. */
    Path bwtaPath = starcraftPath.resolve(BWAPI.DATA_PATH).resolve("BWTA");
    Path bwta2Path = starcraftPath.resolve(BWAPI.DATA_PATH).resolve("BWTA2");
    AdakiteUtils.createDirectory(bwtaPath);
    AdakiteUtils.createDirectory(bwta2Path);

    /* Check for bwapi.ini existence. */
    if (!AdakiteUtils.fileExists(bwapiIniPath)) {
      /* If bwapi.ini is not found in the target BWAPI directory, extract it from this archive. */
      URL url = getClass().getResource("/droplauncher/bwapi/files/" + BWAPI.ExtractableFile.BWAPI_INI.toString());
      FileUtils.copyURLToFile(url, bwapiIniPath.toFile());
    }
    /* Read the bwapi.ini file. */
    Ini bwapiIni = new Ini();
    bwapiIni.parse(bwapiIniPath);

    /* Check for the Broodwar.map file. */
    if (!AdakiteUtils.fileExists(bwapiBroodwarMap)) {
      /* If Broodwar.map is not found in the target BWAPI directory, extract it from this archive. */
      URL url = getClass().getResource("/droplauncher/bwapi/files/" + BWAPI.ExtractableFile.BROODWAR_MAP.toString());
      FileUtils.copyURLToFile(url, bwapiBroodwarMap.toFile());
    }

    /* Check for DLL dependencies. */
    for (BWAPI.ExtractableDll val : BWAPI.ExtractableDll.values()) {
      /* If dependency is not found in the target StarCraft directory, extract it from this archive. */
      Path dll = starcraftPath.resolve(val.toString());
      if (!AdakiteUtils.fileExists(dll)) {
        URL url = getClass().getResource("/droplauncher/bwapi/dll/" + val.toString());
        FileUtils.copyURLToFile(url, dll.toFile());
      }
    }

    Path src;
    Path dest;
    switch (this.bot.getType()) {
      case DLL:
        /* Copy DLL to "bwapi-data/AI/" directory. */
        src = this.bot.getPath();
        dest = starcraftPath.resolve(BWAPI.DATA_AI_PATH).resolve(FilenameUtils.getName(this.bot.getPath().toString()));
        AdakiteUtils.createDirectory(dest.getParent());
        Files.copy(src, dest, StandardCopyOption.REPLACE_EXISTING);
        this.bot.setPath(dest);
        Path iniAiPath = BWAPI.DATA_AI_PATH.resolve(FilenameUtils.getName(this.bot.getPath().toString()));
        bwapiIni.set("ai", "ai", iniAiPath.toString());
        break;
      case CLIENT:
        /* Copy client to StarCraft root directory. */
        src = this.bot.getPath();
        dest = starcraftPath.resolve(FilenameUtils.getName(this.bot.getPath().toString()));
        Files.copy(src, dest, StandardCopyOption.REPLACE_EXISTING);
        this.bot.setPath(dest);
        bwapiIni.commentVariable("ai", "ai");
        break;
      default:
        throw new InvalidBotTypeException();
    }
    /* Not tested yet whether it matters if ai_dbg is enabled. Disable anyway. */
    bwapiIni.commentVariable("ai", "ai_dbg");

    /* Update bwapi.ini file. */
    bwapiIni.store(bwapiIniPath);

    /* Copy extra files to common bot I/O directories. */
    for (String path : this.bot.getExtraFiles()) {
      Files.copy(Paths.get(path), Paths.get(bwapiAiPath.toString(), FilenameUtils.getName(path)), StandardCopyOption.REPLACE_EXISTING);
    }
  }

  private void print(String str) {
    if (this.consoleOutput != null) {
      this.consoleOutput.print(str);
    }
  }

  private void println(String line) {
    if (this.consoleOutput != null) {
      this.consoleOutput.println(line);
    }
  }

}
