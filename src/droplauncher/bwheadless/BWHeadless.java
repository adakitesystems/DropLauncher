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

import droplauncher.util.KillableTask;
import adakite.exception.InvalidStateException;
import adakite.ini.Ini;
import adakite.ini.IniParseException;
import adakite.prefs.Prefs;
import adakite.settings.Settings;
import adakite.util.AdakiteUtils;
import droplauncher.bwapi.BWAPI;
import droplauncher.exception.InvalidBotTypeException;
import droplauncher.mvc.view.ConsoleOutput;
import adakite.util.process.CommandBuilder;
import droplauncher.util.process.CustomProcess;
import adakite.util.windows.task.Task;
import adakite.util.windows.task.TaskTracker;
import adakite.util.windows.task.Tasklist;
import droplauncher.bot.Bot;
import droplauncher.mvc.model.Model;
import droplauncher.mvc.view.View;
import droplauncher.starcraft.Starcraft;
import droplauncher.starcraft.Starcraft.Race;
import droplauncher.util.DropLauncher;
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

  public enum Property {

    NETWORK_PROVIDER("network"),
    CONNECT_MODE("connect_mode"),
    GAME_NAME("game_name"),
    MAP("map"),
    ;

    private final String str;

    private Property(String str) {
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

  public static final Path DEFAULT_EXE_PATH = Paths.get("bwheadless.exe");

  public static final Prefs PREF_ROOT = DropLauncher.PREF_ROOT.getChild("bwheadless");

  public static final String DEFAULT_BOT_NAME = "BOT";
  public static final Race DEFAULT_BOT_RACE = Race.RANDOM;
//  public static final NetworkProvider DEFAULT_NETWORK_PROVIDER = NetworkProvider.LAN; //TODO: Delete?
//  public static final ConnectMode DEFAULT_CONNECT_MODE = ConnectMode.JOIN; //TODO: Delete?

  private Settings settings;

  private CustomProcess bwheadlessProcess;
  private CustomProcess botProcess;

  private Bot bot;

  private TaskTracker taskTracker;

  public BWHeadless() {
    this.settings = new Settings();

    this.bwheadlessProcess = new CustomProcess();
    this.botProcess = new CustomProcess();

    this.bot = null;

    this.taskTracker = new TaskTracker();
  }

  public Settings getSettings() {
    return this.settings;
  }

  public void setSettings(Settings settings) {
    this.settings = settings;
  }

  public void setBot(Bot bot) {
    this.bot = bot;
  }

  /**
   * Starts bwheadless after configuring and checking settings.
   *
   * @param co specified ConsoleOutput to display process output stream
   * @throws IOException if an I/O error occurs
   * @throws InvalidBotTypeException if the bot type is not recognized
   * @throws adakite.ini.IniParseException
   * @throws adakite.exception.InvalidStateException if a call to a Bot getter method fails
   */
  public void start(ConsoleOutput co) throws IOException,
                                             InvalidBotTypeException,
                                             IniParseException,
                                             InvalidStateException {
    if (this.bot == null) {
      throw new InvalidStateException("bot object not set");
    }

    this.taskTracker.reset();

    Path starcraftPath = Starcraft.getPath();

    configureBwapi();

    /* Compile bwheadless arguments. */
    CommandBuilder bwhCommand = new CommandBuilder();
    bwhCommand.setPath(DEFAULT_EXE_PATH.toAbsolutePath());
    bwhCommand.addArg(Argument.STARCRAFT_EXE.toString());
    bwhCommand.addArg(Model.getPref(Starcraft.Property.STARCRAFT_EXE.toString()));
    bwhCommand.addArg(Argument.JOIN_GAME.toString());
    bwhCommand.addArg(Argument.BOT_NAME.toString());
    bwhCommand.addArg(this.bot.getName());
    bwhCommand.addArg(Argument.BOT_RACE.toString());
    bwhCommand.addArg(this.bot.getRace());
    bwhCommand.addArg(Argument.LOAD_DLL.toString());
    bwhCommand.addArg(this.bot.getBwapiDll());
    bwhCommand.addArg(Argument.ENABLE_LAN.toString());
    bwhCommand.addArg(Argument.STARCRAFT_INSTALL_PATH.toString());
    bwhCommand.addArg(starcraftPath.toString());

    /* Start bwheadless. */
    this.bwheadlessProcess
        .setCWD(starcraftPath)
        .setProcessName(View.MessagePrefix.BWHEADLESS.toString());
    this.bwheadlessProcess.start(bwhCommand.get(), co);

    /* Start bot client. */
    if (this.bot.getType() == Bot.Type.CLIENT) {
      /* Compile bot client arguments. */
      CommandBuilder clientCommand = new CommandBuilder();
      switch (FilenameUtils.getExtension(this.bot.getPath()).toLowerCase(Locale.US)) {
        case "exe":
          clientCommand.setPath(Paths.get(this.bot.getPath()));
          break;
        case "jar":
          if (!AdakiteUtils.fileExists(DropLauncher.JRE_EXE)) {
            throw new FileNotFoundException(DropLauncher.JRE_EXE.toString());
          }
          clientCommand.setPath(DropLauncher.JRE_EXE.toAbsolutePath());
          clientCommand.addArg("-jar");
          clientCommand.addArg(this.bot.getPath());
          break;
        default:
          throw new InvalidBotTypeException("bot file type is not EXE or JAR");
      }
      this.botProcess
          .setCWD(starcraftPath)
          .setProcessName(View.MessagePrefix.CLIENT.toString());
      this.botProcess.start(clientCommand.get(), co);
    }
  }

  /**
   * Stops the bwheadless and bot processes.
   *
   * @throws IOException if an I/O error occurs
   * @throws InvalidStateException
   * @throws ClosePipeException
   */
  public void stop() throws IOException,
                            InvalidStateException,
                            ClosePipeException {
    /* Kill new tasks that were started with bwheadless. */
    String botName = FilenameUtils.getBaseName(this.bot.getPath());
    this.taskTracker.update();
    for (Task task : this.taskTracker.getNewTasks()) {
      /* Kill bot client. */
      if (this.bot.getType() == Bot.Type.CLIENT && botName.contains(task.getImageName())) {
        Tasklist.kill(task.getPID());
        continue;
      }
      /* Only kill tasks whose names match known associated tasks. */
      for (KillableTask kt : KillableTask.values()) {
        if (kt.toString().equalsIgnoreCase(task.getImageName())) {
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
   * @param starcraftPath path to the specified StarCraft directory
   * @throws IOException if an I/O error occurs
   * @throws InvalidBotTypeException if the bot type is not recognized
   */
  private void configureBwapi() throws IOException,
                                                         InvalidBotTypeException,
                                                         IniParseException,
                                                         InvalidStateException {
    /* Create common BWAPI paths. */
    Path starcraftPath = Starcraft.getPath();
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
      /* If bwapi.ini is not found, exract the modified BWAPI 4.2.0 bwapi.ini. */
      URL url = getClass().getResource("/droplauncher/bwapi/files/" + BWAPI.ExtractableFile.BWAPI_INI.toString());
      FileUtils.copyURLToFile(url, bwapiIniPath.toFile());
    }
    /* Read the bwapi.ini file. */
    Ini bwapiIni = new Ini();
    bwapiIni.parse(bwapiIniPath);

    /* Check for the Broodwar.map file. */
    if (!AdakiteUtils.fileExists(bwapiBroodwarMap)) {
      /* If Broodwar.map is not found, extract it. */
      URL url = getClass().getResource("/droplauncher/bwapi/files/" + BWAPI.ExtractableFile.BROODWAR_MAP.toString());
      FileUtils.copyURLToFile(url, bwapiBroodwarMap.toFile());
    }

    /* Check for DLL dependencies. */
    for (BWAPI.ExtractableDll val : BWAPI.ExtractableDll.values()) {
      /* If dependency is not found, extract it. */
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
        src = Paths.get(this.bot.getPath());
        dest = Paths.get(starcraftPath.toString(), BWAPI.DATA_AI_PATH.toString(), FilenameUtils.getBaseName(this.bot.getPath()));
        AdakiteUtils.createDirectory(dest.getParent());
        Files.copy(src, dest, StandardCopyOption.REPLACE_EXISTING);
//        this.botFile.setPath(dest);
        bwapiIni.set("ai", "ai", BWAPI.DATA_AI_PATH.toString() + AdakiteUtils.FILE_SEPARATOR + FilenameUtils.getBaseName(this.bot.getPath()));
        break;
      case CLIENT:
        /* Copy client to StarCraft root directory. */
        src = Paths.get(this.bot.getPath());
        dest = Paths.get(starcraftPath.toString(), FilenameUtils.getBaseName(this.bot.getPath()));
        Files.copy(src, dest, StandardCopyOption.REPLACE_EXISTING);
//        this.botFile.setPath(dest);
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
      Files.copy(Paths.get(path), Paths.get(bwapiAiPath.toString(), FilenameUtils.getBaseName(path)), StandardCopyOption.REPLACE_EXISTING);
    }
  }

}
