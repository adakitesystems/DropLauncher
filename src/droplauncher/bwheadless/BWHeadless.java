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
import droplauncher.process.KillableTask;
import adakite.ini.exception.IniParseException;
import adakite.util.AdakiteUtils;
import droplauncher.bwapi.BWAPI;
import droplauncher.bwapi.bot.exception.InvalidBotTypeException;
import adakite.process.CommandBuilder;
import adakite.settings.Settings;
import droplauncher.process.CustomProcess;
import adakite.windows.task.Task;
import adakite.windows.task.TaskTracker;
import adakite.windows.task.Tasklist;
import adakite.windows.task.exception.TasklistParseException;
import droplauncher.DropLauncher;
import droplauncher.bwapi.BwapiDirectory;
import droplauncher.bwapi.bot.Bot;
import droplauncher.bwapi.bot.exception.MissingBotFileException;
import droplauncher.bwapi.bot.exception.MissingBotNameException;
import droplauncher.bwapi.bot.exception.MissingBotRaceException;
import droplauncher.bwapi.bot.exception.MissingBwapiDllException;
import droplauncher.bwheadless.exception.MissingBotException;
import droplauncher.starcraft.Starcraft;
import droplauncher.starcraft.exception.MissingStarcraftExeException;
import droplauncher.bwheadless.exception.MissingBWHeadlessExeException;
import droplauncher.jre.JRE;
import droplauncher.mvc.model.Model;
import droplauncher.mvc.view.ConsoleOutputWrapper;
import droplauncher.mvc.view.View;
import droplauncher.starcraft.exception.UnsupportedStarcraftVersionException;
import droplauncher.process.exception.ClosePipeException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;
import org.apache.commons.io.FilenameUtils;

/**
 * Class for handling execution and communication with bwheadless.
 */
public class BWHeadless {

  private enum PropertyKey {

    /**********************************************************************/
    /* Specific to bwheadless.exe */
    /**********************************************************************/

    /**
     * Network provider. e.g. LAN, LocalPC
     */
    NETWORK_PROVIDER("network_provider"),

    /**
     * Connect mode. e.g. Join or host a game.
     */
    CONNECT_MODE("connect_mode"), /* join or host */

    /**
     * Game name when hosting.
     */
    GAME_NAME("game_name"),

    /**
     * Map name when hosting.
     */
    MAP("map"),

    /**********************************************************************/
    /* Specific to this class */
    /**********************************************************************/

    /**
     * Path to "bwheadless.exe".
     */
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
  public enum RuntimeArgument {

    /**
     * Set the path to "StarCraft.exe". Requires a second string.
     */
    STARCRAFT_EXE("-e"),

    /**
     * Host a game.
     */
    HOST("-h"),

    /**
     * Set the game name when hosting. Requires a second string.
     */
    GAME_NAME("-g"),

    /**
     * Join the first found open game.
     */
    JOIN_GAME("-j"),

    /**
     * Set the map when hosting. Requires a second string.
     */
    MAP("-m"),

    /**
     * Set the bot name. Requires a second string.
     */
    BOT_NAME("-n"),

    /**
     * Set the bot race. Requires a second string.
     */
    BOT_RACE("-r"),

    /**
     * Set the path to the DLL to inject. Requires a second string.
     */
    LOAD_DLL("-l"),

    /**
     * Enable LAN. Default is LocalPC.
     */
    ENABLE_LAN("--lan"),

    /**
     * Set the StarCraft installation directory. Requires a second string.
     */
    STARCRAFT_INSTALL_PATH("--installpath")

    ;

    private final String str;

    private RuntimeArgument(String str) {
      this.str = str;
    }

    @Override
    public String toString() {
      return str;
    }

  }

  public static final String BINARY_FILENAME = "bwheadless.exe";

  private Settings settings;
  private CustomProcess bwheadlessProcess;
  private CustomProcess botProcess;
  private BwapiDirectory bwapiDirectory;
  private Bot bot;
  private ConsoleOutputWrapper consoleOutput;
  private TaskTracker taskTracker;

  public BWHeadless() {
    this.settings = new Settings();
    this.bwheadlessProcess = new CustomProcess();
    this.botProcess = new CustomProcess();
    this.bwapiDirectory = new BwapiDirectory();
    this.bot = new Bot();
    this.consoleOutput = null;
    this.taskTracker = new TaskTracker();

    this.settings.set(PropertyKey.BWHEADLESS_EXE.toString(), DropLauncher.BINARY_DIRECTORY.resolve(BWHeadless.BINARY_FILENAME).toString());
  }

  /**
   * Returns the path to the bwheadless.exe.
   *
   * @throws MissingBWHeadlessExeException if path is not set
   */
  public Path getFile() throws MissingBWHeadlessExeException {
    if (!this.settings.hasValue(PropertyKey.BWHEADLESS_EXE.toString())) {
      throw new MissingBWHeadlessExeException();
    }
    String val = this.settings.getValue(PropertyKey.BWHEADLESS_EXE.toString());
    return Paths.get(val);
  }

  /**
   * Sets the specified path to bwheadlesse.exe.
   *
   * @param file specified path
   * @throws InvalidArgumentException if the specified path is null
   */
  public BWHeadless setFile(Path file) throws InvalidArgumentException {
    if (file == null) {
      throw new InvalidArgumentException(Debugging.cannotBeNull("file"));
    }
    this.settings.set(PropertyKey.BWHEADLESS_EXE.toString(), file.toString());
    return this;
  }

  /**
   * Returns the path to the target StarCraft.exe.
   *
   * @throws MissingStarcraftExeException if the path to StarCraft.exe is not set
   */
  private Path getStarcraftExe() throws MissingStarcraftExeException {
    if (!this.settings.hasValue(Starcraft.PropertyKey.STARCRAFT_EXE.toString())) {
      throw new MissingStarcraftExeException();
    }
    String val = this.settings.getValue(Starcraft.PropertyKey.STARCRAFT_EXE.toString());
    return Paths.get(val);
  }

  /**
   * Sets the internal path of StarCraft.exe to the specified path.
   *
   * @param file specified path
   * @throws InvalidArgumentException if the specified path is null
   */
  public BWHeadless setStarcraftExe(Path file) throws InvalidArgumentException {
    if (file == null) {
      throw new InvalidArgumentException(Debugging.cannotBeNull("starcraftExe"));
    }

    this.settings.set(Starcraft.PropertyKey.STARCRAFT_EXE.toString(), file.toString());

    Path parent = file.getParent();
    if (parent == null) {
      parent = Paths.get("");
    }
    this.bwapiDirectory.setDirectory(parent.resolve(BWAPI.ROOT_DIRECTORY));

    return this;
  }

  /**
   * Returns the path to the StarCraft directory.
   *
   * @see #getStarcraftExe()
   * @throws MissingStarcraftExeException
   * @throws IOException
   */
  private Path getStarcraftDirectory() throws MissingStarcraftExeException, IOException {
    Path parent = AdakiteUtils.getParentDirectory(getStarcraftExe());
    if (parent == null) {
      throw new IOException();
    }
    return parent;
  }

  /**
   * Returns the internal object which represents the BWAPI directory structure.
   */
  public BwapiDirectory getBwapiDirectory() {
    return this.bwapiDirectory;
  }

  public BWHeadless setBwapiDirectory(BwapiDirectory bwapiDirectory) throws InvalidArgumentException {
    if (bwapiDirectory == null) {
      throw new InvalidArgumentException(Debugging.cannotBeNull("bwapi"));
    }
    this.bwapiDirectory = bwapiDirectory;
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
   * @throws UnsupportedStarcraftVersionException
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
                             MissingBWHeadlessExeException,
                             UnsupportedStarcraftVersionException {
    this.bwapiDirectory.backupIniFile();

    this.taskTracker.reset();

    /* Check for StarCraft.exe */
    if (!AdakiteUtils.fileReadable(getStarcraftExe())) {
      throw new IOException("failed to access " + Starcraft.BINARY_FILENAME + ": " + getStarcraftExe().toAbsolutePath().toString());
    }

    /* Check StarCraft.exe version. */
    if (Model.getSettings().isEnabled(Starcraft.PropertyKey.CHECK_FOR_SUPPORTED_VERSION.toString())
        && !Starcraft.isBroodWar1161(getStarcraftExe())) {
      throw new UnsupportedStarcraftVersionException();
    }

    this.bwapiDirectory.configure(getStarcraftDirectory(), this.bot);

    /* Compile bwheadless arguments. */
    CommandBuilder bwhCommand = new CommandBuilder();
    bwhCommand.setPath(getFile().toAbsolutePath());
    bwhCommand.addArg(RuntimeArgument.STARCRAFT_EXE.toString(), getStarcraftExe().toAbsolutePath().toString());
    bwhCommand.addArg(RuntimeArgument.JOIN_GAME.toString());
    bwhCommand.addArg(RuntimeArgument.BOT_NAME.toString(), this.bot.getName());
    bwhCommand.addArg(RuntimeArgument.BOT_RACE.toString(), this.bot.getRace());
    bwhCommand.addArg(RuntimeArgument.LOAD_DLL.toString(), this.bot.getBwapiDll().toAbsolutePath().toString());
    bwhCommand.addArg(RuntimeArgument.ENABLE_LAN.toString());
    bwhCommand.addArg(RuntimeArgument.STARCRAFT_INSTALL_PATH.toString(), getStarcraftDirectory().toString());

    /* Start bwheadless. */
    this.bwheadlessProcess
        .setCWD(getStarcraftDirectory())
        .setProcessName(BINARY_FILENAME)
        .setConsoleOutput(this.consoleOutput);
    this.bwheadlessProcess.start(bwhCommand.get());

    /* Start bot client. */
    if (this.bot.getType() == Bot.Type.CLIENT) {
      /* Compile bot client arguments. */
      CommandBuilder clientCommand = new CommandBuilder();
      String ext = AdakiteUtils.getFileExtension(this.bot.getFile());
      if (AdakiteUtils.isNullOrEmpty(ext)) {
        throw new IllegalArgumentException("bot file does not have a file extension: " + this.bot.getFile().toString());
      }
      switch (ext) {
        case "exe":
          clientCommand.setPath(this.bot.getFile());
          break;
        case "jar":
          if (!AdakiteUtils.fileExists(JRE.BINARY_FILE)) {
            throw new FileNotFoundException(JRE.BINARY_FILE.toAbsolutePath().toString());
          }
          clientCommand.setPath(JRE.BINARY_FILE);
          clientCommand.addArg("-jar");
          clientCommand.addArg(this.bot.getFile().toAbsolutePath().toString());
          break;
        default:
          throw new InvalidBotTypeException(FilenameUtils.getName(this.bot.getFile().toString()));
      }
      this.botProcess
          .setCWD(getStarcraftDirectory())
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
    this.bwheadlessProcess.stop();
    if (this.bot.getType() == Bot.Type.CLIENT) {
      this.botProcess.stop();
    }

    /* Kill new tasks that were started after bwheadless. */
    String botFilename = FilenameUtils.getBaseName(this.bot.getFile().toString());
    this.taskTracker.update();
    for (Task task : this.taskTracker.getNewTasks()) {
      /* Kill bot client. */
      if (this.bot.getType() == Bot.Type.CLIENT && botFilename.contains(task.getImageName())) {
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

    this.bwapiDirectory.restoreIniFile();
  }

  private void println(String line) {
    if (this.consoleOutput != null) {
      this.consoleOutput.println(line);
    }
  }

}
