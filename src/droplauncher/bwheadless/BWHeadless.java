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
import adakite.debugging.Debugging;
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
import adakite.util.windows.Task;
import adakite.util.windows.TaskTracker;
import adakite.util.windows.Tasklist;
import droplauncher.bot.Bot;
import droplauncher.mvc.view.View;
import droplauncher.starcraft.Starcraft.Race;
import droplauncher.util.DropLauncher;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Locale;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

/**
 * Class for handling execution and communication with bwheadless.
 */
public class BWHeadless {

  private static final Logger LOGGER = Logger.getLogger(BWHeadless.class.getName());

  public enum Property {

    STARCRAFT_EXE("starcraft_exe"),
    NETWORK_PROVIDER("network"),
    CONNECT_MODE("connect_mode"),
    GAME_NAME("game_name"),
    MAP("map"),
    ;

    private final String str;

    private Property(String str) {
      this.str = str;
    }

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

  /**
   * Enum for how the bot should connect to a game lobby.
   */
  public enum ConnectMode {

    JOIN("join"),
    HOST("host")
    ;

    private final String str;

    private ConnectMode(String str) {
      this.str = str;
    }

    /**
     * Returns the corresponding ConnectMode object.
     *
     * @param str specified string
     * @return
     *     the corresponding ConnectMode object,
     *     otherwise null if no match was found
     */
    public static ConnectMode get(String str) {
      str = str.toLowerCase(Locale.US);
      for (ConnectMode val : ConnectMode.values()) {
        if (str.equals(val.toString().toLowerCase(Locale.US))) {
          return val;
        }
      }
      throw new IllegalArgumentException("ConnectMode not found: " + str);
    }

    @Override
    public String toString() {
      return this.str;
    }

  }

  //TODO: Double-check if LocalPC requires admin privs.
  /**
   * Enum for the passable network argument to the bwheadless process.
   * Currently, only LAN is supported since LocalPC requires admin privileges
   * and a modified SNP file.
   */
  public enum NetworkProvider {

    LAN("lan")
    ;

    private final String str;

    private NetworkProvider(String str) {
      this.str = str;
    }

    /**
     * Returns the corresponding NetworkProvider object.
     *
     * @param str specified string
     * @return
     *     the corresponding NetworkProvider object,
     *     otherwise null if no match was found
     */
    public static NetworkProvider get(String str) {
      str = str.toLowerCase(Locale.US);
      for (NetworkProvider val : NetworkProvider.values()) {
        if (str.equals(val.toString().toLowerCase(Locale.US))) {
          return val;
        }
      }
      throw new IllegalArgumentException("NetworkProvider not found: " + str);
    }

    @Override
    public String toString() {
      return this.str;
    }

  }

  /**
   * Enum for reporting the launch status of bwheadless.
   */
  public enum ReadyError {

    NONE("OK"),
    BWHEADLESS_EXE("unable to read/locate bwheadless.exe"),
    STARTCRAFT_EXE("unable to read/locate StarCraft.exe"),
    BWAPI_DLL("unable to read/locate BWAPI.dll"),
    BOT_NAME("invalid bot name"),
    BOT_FILE("unable to read/locate a bot file (*.dll, *.exe, *.jar)"),
    BOT_RACE("invalid bot race"),
    NETWORK_PROVIDER("invalid network provider"),
    CONNECT_MODE("invalid connect mode"),
    BWAPI_INSTALL("corrupt or missing BWAPI installation"),
    JRE_INSTALL("corrupt or missing JRE installation")
    ;

    private final String name;

    private ReadyError(String name) {
      this.name = name;
    }

    @Override
    public String toString() {
      return this.name;
    }

  }

  public static final Path DEFAULT_EXE_PATH = Paths.get("bwheadless.exe");

  public static final Prefs PREF_ROOT = DropLauncher.PREF_ROOT.getChild("bwheadless");

  public static final String DEFAULT_BOT_NAME = "BOT";
  public static final Race DEFAULT_BOT_RACE = Race.RANDOM;
  public static final NetworkProvider DEFAULT_NETWORK_PROVIDER = NetworkProvider.LAN;
  public static final ConnectMode DEFAULT_CONNECT_MODE = ConnectMode.JOIN;

  private Settings settings;

  private CustomProcess bwheadlessProcess;
  private CustomProcess botProcess;

  private Bot bot;

  private TaskTracker taskTracker;

  public BWHeadless() {
    this.settings = new Settings();

    this.bwheadlessProcess = new CustomProcess();
    this.botProcess = new CustomProcess();

    this.bot = new Bot();

    this.taskTracker = new TaskTracker();
  }

  public Settings getSettings() {
    return this.settings;
  }

  public void setSettings(Settings settings) {
    this.settings = settings;
  }

  public Bot getBot() {
    return this.bot;
  }

  public void setBot(Bot bot) {
    this.bot = bot;
  }

//  /**
//   * Tests if the program has sufficient information to run bwheadless.
//   *
//   * @see #checkReady()
//   */
//  public boolean isReady(){
//    try {
//      checkReady();
//      return true;
//    } catch (Exception ex) {
//      return false;
//    }
//  }

//  /**
//   * Checks required fields.
//   *
//   * @throws InvalidStateException if any required field is missing or incorrect
//   */
//  public void checkReady() throws InvalidStateException {
//    if (!AdakiteUtils.fileExists(DEFAULT_EXE_PATH)) {
//      throw new InvalidStateException(Debugging.fileDoesNotExist(Paths.get("bwheadless.exe")));
//    } else if (!this.settings.hasValue(Property.STARCRAFT_EXE.toString())
//        || !AdakiteUtils.fileExists(Paths.get(this.settings.getValue(Property.STARCRAFT_EXE.toString())))) {
//      throw new InvalidStateException(Debugging.fileDoesNotExist(Paths.get("StarCraft.exe")));
//    } else if (!AdakiteUtils.fileExists(Paths.get(this.bot.getBwapiDll()))) {
//      throw new InvalidStateException(Debugging.fileDoesNotExist(Paths.get("BWAPI.dll")));
//    } else if (AdakiteUtils.isNullOrEmpty(this.bot.getName())) {
//      throw new InvalidStateException(Debugging.emptyString("bot name"));
//    } else if (this.bot.getType() == Bot.Type.UNKNOWN) {
//      throw new InvalidStateException("bot type is not supported");
//    } else if (!AdakiteUtils.fileExists(Paths.get(this.bot.getPath()))) {
//      throw new InvalidStateException(Debugging.fileDoesNotExist(Paths.get(this.bot.getPath())));
//    } else if (!Starcraft.Race.isValid(this.bot.getRace())) {
//      throw new InvalidStateException("bot race is invalid");
//    } else if (!this.settings.hasValue(Property.NETWORK_PROVIDER.toString())) {
//      throw new InvalidStateException("NetworkProvider is not set");
//    } else if (!this.settings.hasValue(Property.CONNECT_MODE.toString())) {
//      throw new InvalidStateException("ConnectMode is not set");
////    } else if (!AdakiteUtils.directoryExists(getBwapiDirectory())
////        || !AdakiteUtils.fileExists(getBwapiDirectory().resolve(BWAPI.DATA_INI_PATH.getFileName()))) {
////      /* If the bwapi.ini file is not found at "StarCraft/bwapi-data/bwapi.ini". */
////      return ReadyError.BWAPI_INSTALL;
////    } else if (AdakiteUtils.getFileExtension(this.botFile.getPath()).equalsIgnoreCase("jar")
////        && !AdakiteUtils.fileExists(DropLauncher.JRE_EXE)) {
////      return ReadyError.JRE_INSTALL;
//    }
//  }

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
    this.taskTracker.reset();

    Path starcraftDirectory = getStarcraftDirectory();

    configureBwapi(starcraftDirectory);

    /* Compile bwheadless arguments. */
    CommandBuilder bwhCommand = new CommandBuilder();
    bwhCommand.setPath(DEFAULT_EXE_PATH.toAbsolutePath());
    bwhCommand.addArg(Argument.STARCRAFT_EXE.toString());
    bwhCommand.addArg(this.settings.getValue(Property.STARCRAFT_EXE.toString()));
    bwhCommand.addArg(Argument.JOIN_GAME.toString());
    bwhCommand.addArg(Argument.BOT_NAME.toString());
    bwhCommand.addArg(this.bot.getName());
    bwhCommand.addArg(Argument.BOT_RACE.toString());
    bwhCommand.addArg(this.bot.getRace());
    bwhCommand.addArg(Argument.LOAD_DLL.toString());
    bwhCommand.addArg(this.bot.getBwapiDll());
    bwhCommand.addArg(Argument.ENABLE_LAN.toString());
    bwhCommand.addArg(Argument.STARCRAFT_INSTALL_PATH.toString());
    bwhCommand.addArg(starcraftDirectory.toString());

    /* Start bwheadless. */
    this.bwheadlessProcess
        .setCWD(starcraftDirectory)
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
            LOGGER.log(Debugging.getLogLevel(), "JRE not found");
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
          .setCWD(starcraftDirectory)
          .setProcessName(View.MessagePrefix.CLIENT.toString());
      this.botProcess.start(clientCommand.get(), co);
    }
  }

  /**
   * Stops the bwheadless and bot processes.
   *
   * @throws IOException if an I/O error occurs
   */
  public void stop() throws IOException,
                            InvalidStateException {
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
   * @param starcraftDirectory path to the specified StarCraft directory
   * @throws IOException if an I/O error occurs
   * @throws InvalidBotTypeException if the bot type is not recognized
   */
  private void configureBwapi(Path starcraftDirectory) throws IOException,
                                                              InvalidBotTypeException,
                                                              IniParseException,
                                                              InvalidStateException {
    /* Create common BWAPI paths. */
    Path bwapiAiPath = starcraftDirectory.resolve(BWAPI.DATA_AI_PATH);
    Path bwapiReadPath = starcraftDirectory.resolve(BWAPI.DATA_READ_PATH);
    Path bwapiWritePath = starcraftDirectory.resolve(BWAPI.DATA_WRITE_PATH);
    Path bwapiDataDataPath = starcraftDirectory.resolve(BWAPI.DATA_DATA_PATH);
    Path bwapiIniPath = starcraftDirectory.resolve(BWAPI.DATA_INI_PATH);
    Path bwapiBroodwarMap = bwapiDataDataPath.resolve(BWAPI.ExtractableFile.BROODWAR_MAP.toString());
    AdakiteUtils.createDirectory(bwapiAiPath);
    AdakiteUtils.createDirectory(bwapiReadPath);
    AdakiteUtils.createDirectory(bwapiWritePath);
    AdakiteUtils.createDirectory(bwapiDataDataPath);

    /* Create BWTA/BWTA2 paths. */
    Path bwtaPath = starcraftDirectory.resolve(BWAPI.DATA_PATH).resolve("BWTA");
    Path bwta2Path = starcraftDirectory.resolve(BWAPI.DATA_PATH).resolve("BWTA2");
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
      Path dll = starcraftDirectory.resolve(val.toString());
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
        dest = Paths.get(starcraftDirectory.toString(), BWAPI.DATA_AI_PATH.toString(), FilenameUtils.getBaseName(this.bot.getPath()));
        AdakiteUtils.createDirectory(dest.getParent());
        Files.copy(src, dest, StandardCopyOption.REPLACE_EXISTING);
//        this.botFile.setPath(dest);
        bwapiIni.set("ai", "ai", BWAPI.DATA_AI_PATH.toString() + AdakiteUtils.FILE_SEPARATOR + FilenameUtils.getBaseName(this.bot.getPath()));
        break;
      case CLIENT:
        /* Copy client to StarCraft root directory. */
        src = Paths.get(this.bot.getPath());
        dest = Paths.get(starcraftDirectory.toString(), FilenameUtils.getBaseName(this.bot.getPath()));
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

    /* Copy misc files to common bot I/O directories. */
    for (String path : this.bot.getExtraFiles()) {
//      Files.copy(path, Paths.get(bwapiReadPath.toString(), path.getFileName().toString()), StandardCopyOption.REPLACE_EXISTING);
//      Files.copy(path, Paths.get(bwapiWritePath.toString(), path.getFileName().toString()), StandardCopyOption.REPLACE_EXISTING);
      Files.copy(Paths.get(path), Paths.get(bwapiAiPath.toString(), FilenameUtils.getBaseName(path)), StandardCopyOption.REPLACE_EXISTING);
    }
  }

  /**
   * Returns the path to the StarCraft directory determined by
   * {@link #setStarcraftExe(java.lang.String)}.
   */
  public Path getStarcraftDirectory() {
    if (this.settings.hasValue(Property.STARCRAFT_EXE.toString())) {
      return AdakiteUtils.getParentDirectory(Paths.get(this.settings.getValue(Property.STARCRAFT_EXE.toString())));
    } else {
      return null;
    }
  }

  /**
   * Returns the path to the "StarCraft/bwapi-data/" directory.
   * The StarCraft directory is determined by {@link #getStarcraftDirectory()}
   * which is set by {@link #setStarcraftExe(java.lang.String)}.
   *
   * @see #getStarcraftDirectory()
   */
  public Path getBwapiDirectory() {
    Path starcraftDirectory = getStarcraftDirectory();
    return (starcraftDirectory == null) ? null : starcraftDirectory.resolve(BWAPI.DATA_PATH);
  }

}
