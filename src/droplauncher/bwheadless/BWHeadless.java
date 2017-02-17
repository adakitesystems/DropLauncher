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
import adakite.ini.Ini;
import adakite.util.AdakiteUtils;
import droplauncher.bwapi.BWAPI;
import droplauncher.exception.InvalidBotTypeException;
import droplauncher.mvc.view.ConsoleOutput;
import droplauncher.mvc.view.MessagePrefix;
import droplauncher.starcraft.Race;
import droplauncher.starcraft.Starcraft;
import adakite.util.process.CommandBuilder;
import droplauncher.util.process.CustomProcess;
import droplauncher.util.SettingsKey;
import adakite.util.windows.Task;
import adakite.util.windows.TaskTracker;
import adakite.util.windows.Tasklist;
import droplauncher.util.Constants;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.logging.Logger;

/**
 * Class for handling execution and communication with bwheadless.
 */
public class BWHeadless {

  private static final Logger LOGGER = Logger.getLogger(BWHeadless.class.getName());

  public static final Path DEFAULT_EXE_PATH = Paths.get("bwheadless.exe");
  public static final String DEFAULT_INI_SECTION_NAME = "bwheadless";

  public static final String DEFAULT_BOT_NAME = "BOT";
  public static final Race DEFAULT_BOT_RACE = Race.RANDOM;
  public static final NetworkProvider DEFAULT_NETWORK_PROVIDER = NetworkProvider.LAN;
  public static final ConnectMode DEFAULT_CONNECT_MODE = ConnectMode.JOIN;

  private Ini ini;

  private CustomProcess bwheadlessProcess;
  private CustomProcess botProcess;

  private BotFile botFile;
  private ArrayList<Path> extraBotFiles; /* config files, e.g.: .json, .txt */

  private TaskTracker taskTracker;

  public BWHeadless() {
    this.ini = null;

    this.bwheadlessProcess = new CustomProcess();
    this.botProcess = new CustomProcess();

    this.botFile = new BotFile();
    this.extraBotFiles = new ArrayList<>();

    this.taskTracker = new TaskTracker();
  }

  public Ini getINI() {
    return this.ini;
  }

  public void setINI(Ini ini) {
    this.ini = ini;
  }

  /**
   * Returns a list of extra bot files such as config files, JSON files,
   * TXT files, etc. Any file that may be required to run the bot.
   */
  public ArrayList<Path> getExtraBotFiles() {
    return this.extraBotFiles;
  }

  /**
   * Tests if the program has sufficient information to run bwheadless.
   *
   * @see #getReadyError()
   */
  public boolean isReady() {
    return (getReadyError() == ReadyError.NONE);
  }

  /**
   * Returns an error/OK response depending on whether the program is ready
   * to run bwheadless.
   *
   * @return
   *     {@link ReadyError#NONE} if ready,
   *     otherwise the corresponding value that is preventing status
   *     from being ready. E.g. {@link ReadyError#STARTCRAFT_EXE}
   */
  public ReadyError getReadyError() {
    if (!AdakiteUtils.fileExists(DEFAULT_EXE_PATH)) {
      return ReadyError.BWHEADLESS_EXE;
    } else if (!this.ini.hasValue(DEFAULT_INI_SECTION_NAME, SettingsKey.STARCRAFT_EXE.toString())
        || !AdakiteUtils.fileExists(Paths.get(this.ini.getValue(DEFAULT_INI_SECTION_NAME, SettingsKey.STARCRAFT_EXE.toString())))) {
      return ReadyError.STARTCRAFT_EXE;
    } else if (!this.ini.hasValue(DEFAULT_INI_SECTION_NAME, SettingsKey.BWAPI_DLL.toString())
        || !AdakiteUtils.fileExists(Paths.get(this.ini.getValue(DEFAULT_INI_SECTION_NAME, SettingsKey.BWAPI_DLL.toString())))) {
      return ReadyError.BWAPI_DLL;
    } else if (!this.ini.hasValue(DEFAULT_INI_SECTION_NAME, SettingsKey.BOT_NAME.toString())) {
      return ReadyError.BOT_NAME;
    } else if (this.botFile.getType() == BotFile.Type.UNKNOWN
        || !AdakiteUtils.fileExists(this.botFile.getPath())) {
      /* If both the bot DLL and bot client fields are missing. */
      return ReadyError.BOT_FILE;
    } else if (!this.ini.hasValue(DEFAULT_INI_SECTION_NAME, SettingsKey.BOT_RACE.toString())) {
      return ReadyError.BOT_RACE;
    } else if (!this.ini.hasValue(DEFAULT_INI_SECTION_NAME, SettingsKey.NETWORK_PROVIDER.toString())) {
      return ReadyError.NETWORK_PROVIDER;
    } else if (!this.ini.hasValue(DEFAULT_INI_SECTION_NAME, SettingsKey.CONNECT_MODE.toString())) {
      return ReadyError.CONNECT_MODE;
    } else if (!AdakiteUtils.directoryExists(getBwapiDirectory()) || !AdakiteUtils.fileExists(getBwapiDirectory().resolve(BWAPI.BWAPI_DATA_INI_PATH.getFileName()))) {
      /* If the BWAPI.ini file is not found at "StarCraft/bwapi-data/bwapi.ini". */
      return ReadyError.BWAPI_INSTALL;
    } else if (AdakiteUtils.getFileExtension(this.botFile.getPath()).equalsIgnoreCase("jar")
        && !AdakiteUtils.fileExists(Constants.JRE_EXE)) {
      return ReadyError.JRE_INSTALL;
    } else {
      return ReadyError.NONE;
    }
  }

  /**
   * Starts bwheadless after configuring and checking settings.
   *
   * @param co specified ConsoleOutput to display process output stream
   * @throws IOException if an I/O error occurs
   * @throws InvalidBotTypeException if the bot type is not recognized
   */
  public void start(ConsoleOutput co) throws IOException, InvalidBotTypeException {
    this.taskTracker.reset();

    Path starcraftDirectory = getStarcraftDirectory();

    configureBwapi(starcraftDirectory);

    /* Compile bwheadless arguments. */
    CommandBuilder bwhCommand = new CommandBuilder();
    bwhCommand.setPath(DEFAULT_EXE_PATH.toAbsolutePath());
    bwhCommand.addArg(Argument.STARCRAFT_EXE.toString());
    bwhCommand.addArg(this.ini.getValue(DEFAULT_INI_SECTION_NAME, SettingsKey.STARCRAFT_EXE.toString()));
    bwhCommand.addArg(Argument.JOIN_GAME.toString());
    bwhCommand.addArg(Argument.BOT_NAME.toString());
    bwhCommand.addArg(this.ini.getValue(DEFAULT_INI_SECTION_NAME, SettingsKey.BOT_NAME.toString()));
    bwhCommand.addArg(Argument.BOT_RACE.toString());
    bwhCommand.addArg(this.ini.getValue(DEFAULT_INI_SECTION_NAME, SettingsKey.BOT_RACE.toString()));
    bwhCommand.addArg(Argument.LOAD_DLL.toString());
    bwhCommand.addArg(this.ini.getValue(DEFAULT_INI_SECTION_NAME, SettingsKey.BWAPI_DLL.toString()));
    bwhCommand.addArg(Argument.ENABLE_LAN.toString());
    bwhCommand.addArg(Argument.STARCRAFT_INSTALL_PATH.toString());
    bwhCommand.addArg(starcraftDirectory.toString());

    /* Start bwheadless. */
    this.bwheadlessProcess
        .setCWD(starcraftDirectory)
        .setProcessName(MessagePrefix.BWHEADLESS.toString());
    this.bwheadlessProcess.start(bwhCommand.get(), co);

    /* Start bot client. */
    if (this.botFile.getType() == BotFile.Type.CLIENT) {
      /* Compile bot client arguments. */
      CommandBuilder clientCommand = new CommandBuilder();
      switch (AdakiteUtils.getFileExtension(this.botFile.getPath())) {
        case "exe":
          clientCommand.setPath(this.botFile.getPath().toAbsolutePath());
          break;
        case "jar":
          if (!AdakiteUtils.fileExists(Constants.JRE_EXE)) {
            LOGGER.log(Debugging.getLogLevel(), "JRE not found");
            throw new FileNotFoundException(Constants.JRE_EXE.toString());
          }
          clientCommand.setPath(Constants.JRE_EXE.toAbsolutePath());
          clientCommand.addArg("-jar");
          clientCommand.addArg(this.botFile.getPath().toAbsolutePath().toString());
          break;
        default:
          throw new InvalidBotTypeException("bot file is not EXE or JAR type");
      }
      this.botProcess.setCWD(starcraftDirectory);
      this.botProcess.setProcessName(MessagePrefix.CLIENT.toString());
      this.botProcess.start(clientCommand.get(), co);
    }
  }

  /**
   * Stops the bwheadless and bot processes.
   *
   * @throws IOException if an I/O error occurs
   */
  public void stop() throws IOException {
    /* Kill new tasks that were started with bwheadless. */
    this.taskTracker.update();
    ArrayList<Task> tasks = this.taskTracker.getNewTasks();
    Tasklist tasklist = new Tasklist();
    boolean isClient = getBotType() == BotFile.Type.CLIENT;
    String botName = getBotPath().getFileName().toString();
    for (Task task : tasks) {
      /* Kill bot client. */
      if (isClient && botName.contains(task.getImageName())) {
        tasklist.kill(task.getPID());
        continue;
      }
      /* Only kill tasks whose names match known associated tasks. */
      for (KillableTask kt : KillableTask.values()) {
        if (kt.toString().equalsIgnoreCase(task.getImageName())) {
          tasklist.kill(task.getPID());
          break;
        }
      }
    }

    this.bwheadlessProcess.stop();
    if (this.botFile.getType() == BotFile.Type.CLIENT) {
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
  private void configureBwapi(Path starcraftDirectory) throws IOException, InvalidBotTypeException {
    /* Create common BWAPI paths. */
    Path bwapiAiPath = starcraftDirectory.resolve(BWAPI.BWAPI_DATA_AI_PATH);
    Path bwapiReadPath = starcraftDirectory.resolve(BWAPI.BWAPI_DATA_READ_PATH);
    Path bwapiWritePath = starcraftDirectory.resolve(BWAPI.BWAPI_DATA_WRITE_PATH);
    AdakiteUtils.createDirectory(bwapiAiPath);
    AdakiteUtils.createDirectory(bwapiReadPath);
    AdakiteUtils.createDirectory(bwapiWritePath);

    /* Read the BWAPI.ini file. */
    Path bwapiIniPath = starcraftDirectory.resolve(BWAPI.BWAPI_DATA_INI_PATH);
    Ini bwapiIni = new Ini();
    bwapiIni.read(bwapiIniPath);

    Path src;
    Path dest;
    switch (this.botFile.getType()) {
      case DLL:
        /* Copy DLL to bwapi-data directory. */
        src = this.botFile.getPath();
        dest = Paths.get(starcraftDirectory.toString(), BWAPI.BWAPI_DATA_AI_PATH.toString(), this.botFile.getPath().getFileName().toString());
        AdakiteUtils.createDirectory(dest.getParent());
        Files.copy(src, dest, StandardCopyOption.REPLACE_EXISTING);
        this.botFile.setPath(dest);
        bwapiIni.set("ai", "ai", BWAPI.BWAPI_DATA_AI_PATH.toString() + AdakiteUtils.FILE_SEPARATOR + this.botFile.getPath().getFileName().toString());
        break;
      case CLIENT:
        /* Copy client to StarCraft root directory. */
        src = this.botFile.getPath();
        dest = Paths.get(starcraftDirectory.toString(), this.botFile.getPath().getFileName().toString());
        Files.copy(src, dest, StandardCopyOption.REPLACE_EXISTING);
        this.botFile.setPath(dest);
        bwapiIni.commentVariable("ai", "ai");
        break;
      default:
        throw new InvalidBotTypeException();
    }
    /* Not tested yet whether it matters if ai_dbg is enabled. Disable anyway. */
    bwapiIni.commentVariable("ai", "ai_dbg");

    /* Update BWAPI.ini file. */
    bwapiIni.saveTo(bwapiIniPath);

    /* Copy misc files to common bot I/O directories. */
    for (Path path : this.extraBotFiles) {
      Files.copy(path, Paths.get(bwapiReadPath.toString(), path.getFileName().toString()), StandardCopyOption.REPLACE_EXISTING);
      Files.copy(path, Paths.get(bwapiWritePath.toString(), path.getFileName().toString()), StandardCopyOption.REPLACE_EXISTING);
      Files.copy(path, Paths.get(bwapiAiPath.toString(), path.getFileName().toString()), StandardCopyOption.REPLACE_EXISTING);
    }
  }

  /**
   * Returns the path to the StarCraft directory determined by
   * {@link #setStarcraftExe(java.lang.String)}.
   */
  public Path getStarcraftDirectory() {
    if (this.ini.hasValue(DEFAULT_INI_SECTION_NAME, SettingsKey.STARCRAFT_EXE.toString())) {
      return AdakiteUtils.getParentDirectory(Paths.get(this.ini.getValue(DEFAULT_INI_SECTION_NAME, SettingsKey.STARCRAFT_EXE.toString())));
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
    if (starcraftDirectory != null) {
      return starcraftDirectory.resolve(BWAPI.BWAPI_DATA_PATH);
    } else {
      return null;
    }
  }

  public void setStarcraftExe(String starcraftExe) {
    this.ini.set(DEFAULT_INI_SECTION_NAME, SettingsKey.STARCRAFT_EXE.toString(), starcraftExe);
  }

  public void setBwapiDll(String bwapiDll) {
    this.ini.set(DEFAULT_INI_SECTION_NAME, SettingsKey.BWAPI_DLL.toString(), bwapiDll);
  }

  public void setBotName(String botName) {
    String cleaned = Starcraft.cleanProfileName(botName);
    if (cleaned.equals(this.ini.getValue(DEFAULT_INI_SECTION_NAME, SettingsKey.BOT_NAME.toString()))) {
      return;
    }

    if (AdakiteUtils.isNullOrEmpty(cleaned)) {
      this.ini.set(DEFAULT_INI_SECTION_NAME, SettingsKey.BOT_NAME.toString(), DEFAULT_BOT_NAME);
    } else {
      this.ini.set(DEFAULT_INI_SECTION_NAME, SettingsKey.BOT_NAME.toString(), cleaned);
    }
  }

  public BotFile.Type getBotType() {
    return this.botFile.getType();
  }

  public Path getBotPath() {
    return this.botFile.getPath();
  }

  public void setBotFile(String botFile) {
    this.extraBotFiles.clear();
    Path path = Paths.get(botFile);
    if (AdakiteUtils.fileExists(path)) {
      this.botFile.setPath(path);
      this.ini.set(DEFAULT_INI_SECTION_NAME, SettingsKey.BOT_FILE.toString(), botFile);
    }
  }

  public void setBotRace(Race botRace) {
    this.ini.set(DEFAULT_INI_SECTION_NAME, SettingsKey.BOT_RACE.toString(), botRace.toString());
  }

  public void setNetworkProvider(NetworkProvider networkProvider) {
    this.ini.set(DEFAULT_INI_SECTION_NAME, SettingsKey.NETWORK_PROVIDER.toString(), networkProvider.toString());
  }

  public void setConnectMode(ConnectMode connectMode) {
    this.ini.set(DEFAULT_INI_SECTION_NAME, SettingsKey.CONNECT_MODE.toString(), connectMode.toString());
  }

  /**
   * Reads the specified INI and sets class member variables accordingly.
   *
   * @param ini specified INI object
   */
  public void parseSettings(Ini ini) {
    String val;
    if (!AdakiteUtils.isNullOrEmpty(val = ini.getValue(DEFAULT_INI_SECTION_NAME, SettingsKey.STARCRAFT_EXE.toString()))) {
      setStarcraftExe(val);
    }
    if (!AdakiteUtils.isNullOrEmpty(val = ini.getValue(DEFAULT_INI_SECTION_NAME, SettingsKey.BWAPI_DLL.toString()))) {
      setBwapiDll(val);
    }
    if (!AdakiteUtils.isNullOrEmpty(val = ini.getValue(DEFAULT_INI_SECTION_NAME, SettingsKey.BOT_NAME.toString()))) {
      setBotName(val);
    } else {
      /* Name wasn't set. */
      setBotName(DEFAULT_BOT_NAME);
    }
    if (!AdakiteUtils.isNullOrEmpty(val = ini.getValue(DEFAULT_INI_SECTION_NAME, SettingsKey.BOT_FILE.toString()))) {
      setBotFile(val);
    }
    if (!AdakiteUtils.isNullOrEmpty(val = ini.getValue(DEFAULT_INI_SECTION_NAME, SettingsKey.BOT_RACE.toString()))) {
      if (val.equalsIgnoreCase(Race.TERRAN.toString())) {
        setBotRace(Race.TERRAN);
      } else if (val.equalsIgnoreCase(Race.ZERG.toString())) {
        setBotRace(Race.ZERG);
      } else if (val.equalsIgnoreCase(Race.PROTOSS.toString())) {
        setBotRace(Race.PROTOSS);
      } else if (val.equalsIgnoreCase((Race.RANDOM.toString()))) {
        setBotRace(Race.RANDOM);
      } else {
        /* Unrecognized Race. */
        setBotRace(DEFAULT_BOT_RACE);
      }
    } else {
      /* Race wasn't set. */
      setBotRace(DEFAULT_BOT_RACE);
    }
    if (!AdakiteUtils.isNullOrEmpty(val = ini.getValue(DEFAULT_INI_SECTION_NAME, SettingsKey.NETWORK_PROVIDER.toString()))) {
      if (val.equalsIgnoreCase(NetworkProvider.LAN.toString())) {
        setNetworkProvider(NetworkProvider.LAN);
      } else {
        /* Unrecognized NetworkProvider. */
        setNetworkProvider(DEFAULT_NETWORK_PROVIDER);
      }
    } else {
      /* NetworkProvider wasn't set. */
      setNetworkProvider(DEFAULT_NETWORK_PROVIDER);
    }
    if (!AdakiteUtils.isNullOrEmpty(val = ini.getValue(DEFAULT_INI_SECTION_NAME, SettingsKey.CONNECT_MODE.toString()))) {
      if (val.equalsIgnoreCase(ConnectMode.JOIN.toString())) {
        setConnectMode(ConnectMode.JOIN);
      } else {
        /* Unrecognized JoinMode. */
        setConnectMode(DEFAULT_CONNECT_MODE);
      }
    } else {
      /* JoinMode wasn't set. */
      setConnectMode(DEFAULT_CONNECT_MODE);
    }
  }

}
