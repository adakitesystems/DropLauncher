package droplauncher.bwheadless;

import adakite.ini.INI;
import adakite.util.AdakiteUtils;
import droplauncher.bwapi.BWAPI;
import droplauncher.exception.InvalidBotTypeException;
import droplauncher.starcraft.Race;
import droplauncher.starcraft.Starcraft;
import droplauncher.util.ProcessPipe;
import droplauncher.util.SettingsKey;
import droplauncher.util.windows.Windows;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Class for handling execution and communication with bwheadless.
 */
public class BWHeadless {

  private static final Logger LOGGER = LogManager.getLogger();

  public static final Path BWHEADLESS_EXE_PATH = Paths.get("bwheadless.exe");
  public static final String BWHEADLESS_INI_SECTION = "bwheadless";

  public static final String DEFAULT_BOT_NAME = "BOT";
  public static final Race DEFAULT_BOT_RACE = Race.RANDOM;
  public static final NetworkProvider DEFAULT_NETWORK_PROVIDER = NetworkProvider.LAN;
  public static final ConnectMode DEFAULT_CONNECT_MODE = ConnectMode.JOIN;

  private static final String BWH_STREAM_NAME = "bwh";
  private static final String CLIENT_STREAM_NAME = "client";

  private INI ini;

  private ProcessPipe bwheadlessPipe;
  private ProcessPipe botPipe;

  private BotFile botFile;

  private ArrayList<Path> extraBotFiles;

  public BWHeadless() {
    this.ini = null;

    this.bwheadlessPipe = new ProcessPipe();
    this.botPipe = new ProcessPipe();

    this.botFile = new BotFile();

    this.extraBotFiles = new ArrayList<>();
  }

  public INI getINI() {
    return this.ini;
  }

  public void setINI(INI ini) {
    this.ini = ini;
  }

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
    if (!AdakiteUtils.fileExists(BWHEADLESS_EXE_PATH)) {
      return ReadyError.BWHEADLESS_EXE;
    } else if (!this.ini.hasValue(BWHEADLESS_INI_SECTION, SettingsKey.STARCRAFT_EXE.toString())
        || !AdakiteUtils.fileExists(Paths.get(this.ini.getValue(BWHEADLESS_INI_SECTION, SettingsKey.STARCRAFT_EXE.toString())))) {
      return ReadyError.STARTCRAFT_EXE;
    } else if (!this.ini.hasValue(BWHEADLESS_INI_SECTION, SettingsKey.BWAPI_DLL.toString())
        || !AdakiteUtils.fileExists(Paths.get(this.ini.getValue(BWHEADLESS_INI_SECTION, SettingsKey.BWAPI_DLL.toString())))) {
      return ReadyError.BWAPI_DLL;
    } else if (!this.ini.hasValue(BWHEADLESS_INI_SECTION, SettingsKey.BOT_NAME.toString())) {
      return ReadyError.BOT_NAME;
    } else if (this.botFile.getType() == BotFile.Type.UNKNOWN
        || !AdakiteUtils.fileExists(this.botFile.getPath())) {
      /* If both the bot DLL and bot client fields are missing. */
      return ReadyError.BOT_FILE;
    } else if (!this.ini.hasValue(BWHEADLESS_INI_SECTION, SettingsKey.BOT_RACE.toString())) {
      return ReadyError.BOT_RACE;
    } else if (!this.ini.hasValue(BWHEADLESS_INI_SECTION, SettingsKey.NETWORK_PROVIDER.toString())) {
      return ReadyError.NETWORK_PROVIDER;
    } else if (!this.ini.hasValue(BWHEADLESS_INI_SECTION, SettingsKey.CONNECT_MODE.toString())) {
      return ReadyError.CONNECT_MODE;
    } else {
      return ReadyError.NONE;
    }
  }

  /**
   * Tests if the pipe is open between this program and bwheadless.
   */
  public boolean isRunning() {
    return this.bwheadlessPipe.isOpen();
  }

  /**
   * Attempts to start bwheadless after configuring and checking settings.
   */
  public void start() throws IOException, InvalidBotTypeException {
    if (isRunning() || !isReady()) {
      //TODO: Throw a built-in or custom exception.
      return;
    }

    configureBwapi();

    Path starcraftDirectory = AdakiteUtils.getParentDirectory(Paths.get(this.ini.getValue(BWHEADLESS_INI_SECTION, SettingsKey.STARCRAFT_EXE.toString()))).toAbsolutePath();

    /* Compile bwheadless arguments. */
    ArrayList<String> bwhArgs = new ArrayList<>(); /* bwheadless arguments */
    bwhArgs.add(Argument.STARCRAFT_EXE.toString());
    bwhArgs.add(this.ini.getValue(BWHEADLESS_INI_SECTION, SettingsKey.STARCRAFT_EXE.toString()));
    bwhArgs.add(Argument.JOIN_GAME.toString());
    bwhArgs.add(Argument.BOT_NAME.toString());
    bwhArgs.add(this.ini.getValue(BWHEADLESS_INI_SECTION, SettingsKey.BOT_NAME.toString()));
    bwhArgs.add(Argument.BOT_RACE.toString());
    bwhArgs.add(this.ini.getValue(BWHEADLESS_INI_SECTION, SettingsKey.BOT_RACE.toString()));
    bwhArgs.add(Argument.LOAD_DLL.toString());
    bwhArgs.add(this.ini.getValue(BWHEADLESS_INI_SECTION, SettingsKey.BWAPI_DLL.toString()));
    bwhArgs.add(Argument.ENABLE_LAN.toString());
    bwhArgs.add(Argument.STARCRAFT_INSTALL_PATH.toString());
    bwhArgs.add(starcraftDirectory.toString());
    String[] bwhArgsArray = AdakiteUtils.toStringArray(bwhArgs);

    /* Start bwheadless. */
    this.bwheadlessPipe.open(BWHEADLESS_EXE_PATH.toAbsolutePath(), bwhArgsArray, starcraftDirectory.toString(), BWH_STREAM_NAME);

    /* Start bot client in a command prompt. */
    if (this.botFile.getType() == BotFile.Type.CLIENT) {
      ArrayList<String> clArgs = new ArrayList<>(); /* client arguments */
      if (AdakiteUtils.getFileExtension(this.botFile.getPath()).equalsIgnoreCase(Windows.FileType.EXE.toString())) {
        clArgs.add(this.botFile.toString());
        String[] clArgsArray = AdakiteUtils.toStringArray(clArgs);
        this.botPipe.open(this.botFile.getPath().toAbsolutePath(), clArgsArray, starcraftDirectory.toString(), CLIENT_STREAM_NAME);
      }
    }
  }

  public void stop() {
    this.bwheadlessPipe.close();
  }

  /**
   * Configures BWAPI settings and related files.
   *
   * @throws IOException
   */
  private void configureBwapi() throws IOException,
                                       InvalidBotTypeException {
    /* Determine StarCraft directory from StarCraft.exe path. */
    Path starcraftDirectory = AdakiteUtils.getParentDirectory(Paths.get(this.ini.getValue(BWHEADLESS_INI_SECTION, SettingsKey.STARCRAFT_EXE.toString()))).toAbsolutePath();

    /* Configure BWAPI INI file. */
    INI bwapiIni = new INI();
    bwapiIni.open(Paths.get(starcraftDirectory.toString(), BWAPI.BWAPI_DATA_INI_PATH.toString()));
    if (this.botFile.getType() == BotFile.Type.DLL) {
      bwapiIni.set("ai", "ai", BWAPI.BWAPI_DATA_AI_PATH + AdakiteUtils.FILE_SEPARATOR + this.botFile.getPath().getFileName().toString());
    } else {
      bwapiIni.disableVariable("ai", "ai");
    }
    /* Not tested yet whether it matters if ai_dbg is enabled. Disable anyway. */
    bwapiIni.disableVariable("ai", "ai_dbg");

    /* Prepare to copy bot files to StarCraft directory. */
    Path src = null;
    Path dest = null;
    switch (this.botFile.getType()) {
      case DLL:
        /* Prepare to copy DLL to bwapi-data directory. */
        src = this.botFile.getPath();
        dest = Paths.get(starcraftDirectory.toString(), BWAPI.BWAPI_DATA_AI_PATH.toString(), Paths.get(this.botFile.toString()).getFileName().toString());
        this.botFile.setPath(dest);
        break;
      case CLIENT:
        /* Prepare to copy client to StarCraft root directory. */
        src = this.botFile.getPath();
        dest = Paths.get(starcraftDirectory.toString(), this.botFile.getPath().getFileName().toString());
        this.botFile.setPath(dest);
        break;
      default:
        throw LOGGER.throwing(new InvalidBotTypeException());
    }
    /* Copy files. */
    AdakiteUtils.createDirectory(dest.getParent());
    Files.copy(src, dest, StandardCopyOption.REPLACE_EXISTING);

    /* Copy misc files to common bot I/O directories. */
    Path readPath = Paths.get(starcraftDirectory.toString(), BWAPI.BWAPI_DATA_READ_PATH.toString()).toAbsolutePath();
    Path writePath = Paths.get(starcraftDirectory.toString(), BWAPI.BWAPI_DATA_WRITE_PATH.toString()).toAbsolutePath();
    Path aiPath = Paths.get(starcraftDirectory.toString(), BWAPI.BWAPI_DATA_AI_PATH.toString()).toAbsolutePath();
    AdakiteUtils.createDirectory(readPath);
    AdakiteUtils.createDirectory(writePath);
    AdakiteUtils.createDirectory(aiPath);
    for (Path path : this.extraBotFiles) {
      Files.copy(path, Paths.get(readPath.toString(), path.getFileName().toString()), StandardCopyOption.REPLACE_EXISTING);
      Files.copy(path, Paths.get(writePath.toString(), path.getFileName().toString()), StandardCopyOption.REPLACE_EXISTING);
      Files.copy(path, Paths.get(aiPath.toString(), path.getFileName().toString()), StandardCopyOption.REPLACE_EXISTING);
    }
  }

  public void setStarcraftExe(String starcraftExe) {
    LOGGER.info(starcraftExe);

    this.ini.set(BWHEADLESS_INI_SECTION, SettingsKey.STARCRAFT_EXE.toString(), starcraftExe);
  }

  public void setBwapiDll(String bwapiDll) {
    LOGGER.info(bwapiDll);

    this.ini.set(BWHEADLESS_INI_SECTION, SettingsKey.BWAPI_DLL.toString(), bwapiDll);
  }

  public void setBotName(String botName) {
    LOGGER.info(botName);

    String cleaned = Starcraft.cleanProfileName(botName);
    if (cleaned.equals(this.ini.getValue(BWHEADLESS_INI_SECTION, SettingsKey.BOT_NAME.toString()))) {
      return;
    }

    if (AdakiteUtils.isNullOrEmpty(cleaned)) {
      this.ini.set(BWHEADLESS_INI_SECTION, SettingsKey.BOT_NAME.toString(), DEFAULT_BOT_NAME);
    } else {
      this.ini.set(BWHEADLESS_INI_SECTION, SettingsKey.BOT_NAME.toString(), cleaned);
    }
  }

  public BotFile getBotFile() {
    return this.botFile;
  }

  public void setBotFile(String botFile) {
    LOGGER.info(botFile);

    this.extraBotFiles.clear();
    Path path = Paths.get(botFile);
    if (AdakiteUtils.fileExists(path)) {
      this.botFile.setPath(path);
      this.ini.set(BWHEADLESS_INI_SECTION, SettingsKey.BOT_FILE.toString(), botFile);
    }
  }

  public void setBotRace(Race botRace) {
    LOGGER.info(botRace);

    this.ini.set(BWHEADLESS_INI_SECTION, SettingsKey.BOT_RACE.toString(), botRace.toString());
  }

  public void setNetworkProvider(NetworkProvider networkProvider) {
    LOGGER.info(networkProvider);

    this.ini.set(BWHEADLESS_INI_SECTION, SettingsKey.NETWORK_PROVIDER.toString(), networkProvider.toString());
  }

  public void setConnectMode(ConnectMode connectMode) {
    LOGGER.info(connectMode);

    this.ini.set(BWHEADLESS_INI_SECTION, SettingsKey.CONNECT_MODE.toString(), connectMode.toString());
  }

  /**
   * Reads the specified INI and sets class member variables accordingly.
   *
   * @param ini specified INI object
   */
  public void parseSettings(INI ini) {
    String val;
    if (!AdakiteUtils.isNullOrEmpty(val = ini.getValue(BWHEADLESS_INI_SECTION, SettingsKey.STARCRAFT_EXE.toString()))) {
      setStarcraftExe(val);
    }
    if (!AdakiteUtils.isNullOrEmpty(val = ini.getValue(BWHEADLESS_INI_SECTION, SettingsKey.BWAPI_DLL.toString()))) {
      setBwapiDll(val);
    }
    if (!AdakiteUtils.isNullOrEmpty(val = ini.getValue(BWHEADLESS_INI_SECTION, SettingsKey.BOT_NAME.toString()))) {
      setBotName(val);
    } else {
      setBotName(DEFAULT_BOT_NAME);
    }
    if (!AdakiteUtils.isNullOrEmpty(val = ini.getValue(BWHEADLESS_INI_SECTION, SettingsKey.BOT_FILE.toString()))) {
      setBotFile(val);
    }
    if (!AdakiteUtils.isNullOrEmpty(val = ini.getValue(BWHEADLESS_INI_SECTION, SettingsKey.BOT_RACE.toString()))) {
      if (val.equalsIgnoreCase(Race.TERRAN.toString())) {
        setBotRace(Race.TERRAN);
      } else if (val.equalsIgnoreCase(Race.ZERG.toString())) {
        setBotRace(Race.ZERG);
      } else if (val.equalsIgnoreCase(Race.PROTOSS.toString())) {
        setBotRace(Race.PROTOSS);
      } else if(val.equalsIgnoreCase((Race.RANDOM.toString()))) {
        setBotRace(Race.RANDOM);
      } else {
        /* Unrecognized Race. */
        setBotRace(DEFAULT_BOT_RACE);
      }
    } else {
      /* Race wasn't set. */
      setBotRace(DEFAULT_BOT_RACE);
    }
    if (AdakiteUtils.isNullOrEmpty(val = ini.getValue(BWHEADLESS_INI_SECTION, SettingsKey.NETWORK_PROVIDER.toString()))) {
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
    if (AdakiteUtils.isNullOrEmpty(val = ini.getValue(BWHEADLESS_INI_SECTION, SettingsKey.CONNECT_MODE.toString()))) {
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
