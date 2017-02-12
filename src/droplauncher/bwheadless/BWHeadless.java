package droplauncher.bwheadless;

import adakite.ini.INI;
import adakite.util.AdakiteUtils;
import droplauncher.bwapi.BWAPI;
import droplauncher.exception.InvalidBotTypeException;
import droplauncher.starcraft.Race;
import droplauncher.starcraft.Starcraft;
import droplauncher.util.CommandBuilder;
import droplauncher.util.CustomProcess;
import droplauncher.util.SettingsKey;
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

  public static final Path DEFAULT_EXE_PATH = Paths.get("bwheadless.exe");
  public static final String DEFAULT_INI_SECTION_NAME = "bwheadless";

  public static final String DEFAULT_BOT_NAME = "BOT";
  public static final Race DEFAULT_BOT_RACE = Race.RANDOM;
  public static final NetworkProvider DEFAULT_NETWORK_PROVIDER = NetworkProvider.LAN;
  public static final ConnectMode DEFAULT_CONNECT_MODE = ConnectMode.JOIN;

  private INI ini;

  private CustomProcess bwheadlessProcess;
  private CustomProcess botProcess;

  private BotFile botFile;
  private ArrayList<Path> extraBotFiles;

  public BWHeadless() {
    this.ini = null;

    this.bwheadlessProcess = new CustomProcess();
    this.botProcess = new CustomProcess();

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
    } else {
      return ReadyError.NONE;
    }
  }

  /**
   * Attempts to start bwheadless after configuring and checking settings.
   */
  public void start() throws IOException, InvalidBotTypeException {
    Path starcraftDirectory = AdakiteUtils.getParentDirectory(Paths.get(this.ini.getValue(DEFAULT_INI_SECTION_NAME, SettingsKey.STARCRAFT_EXE.toString()))).toAbsolutePath();

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
    this.bwheadlessProcess.setCWD(starcraftDirectory);
    this.bwheadlessProcess.start(bwhCommand.get());

    if (this.botFile.getType() == BotFile.Type.CLIENT) {
      /* Compile bot client arguments. */
      CommandBuilder clientCommand = new CommandBuilder();
      clientCommand.setPath(this.botFile.getPath().toAbsolutePath());
      /* Start bot client. */
      this.botProcess.setCWD(starcraftDirectory);
      this.botProcess.start(clientCommand.get());
    }
  }

  public void stop() {
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
  private void configureBwapi(Path starcraftDirectory)
      throws IOException,
             InvalidBotTypeException {
    /* Configure BWAPI INI file. */
    INI bwapiIni = new INI();
    bwapiIni.open(Paths.get(starcraftDirectory.toString(), BWAPI.BWAPI_DATA_INI_PATH.toString()));
    if (this.botFile.getType() == BotFile.Type.DLL) {
      bwapiIni.set("ai", "ai", BWAPI.BWAPI_DATA_AI_PATH.toString() + AdakiteUtils.FILE_SEPARATOR + this.botFile.getPath().getFileName().toString());
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
        throw new InvalidBotTypeException();
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

    this.ini.set(DEFAULT_INI_SECTION_NAME, SettingsKey.STARCRAFT_EXE.toString(), starcraftExe);
  }

  public void setBwapiDll(String bwapiDll) {
    LOGGER.info(bwapiDll);

    this.ini.set(DEFAULT_INI_SECTION_NAME, SettingsKey.BWAPI_DLL.toString(), bwapiDll);
  }

  public void setBotName(String botName) {
    LOGGER.info(botName);

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

  public BotFile getBotFile() {
    return this.botFile;
  }

  public void setBotFile(String botFile) {
    LOGGER.info(botFile);

    this.extraBotFiles.clear();
    Path path = Paths.get(botFile);
    if (AdakiteUtils.fileExists(path)) {
      this.botFile.setPath(path);
      this.ini.set(DEFAULT_INI_SECTION_NAME, SettingsKey.BOT_FILE.toString(), botFile);
    }
  }

  public void setBotRace(Race botRace) {
    LOGGER.info(botRace);

    this.ini.set(DEFAULT_INI_SECTION_NAME, SettingsKey.BOT_RACE.toString(), botRace.toString());
  }

  public void setNetworkProvider(NetworkProvider networkProvider) {
    LOGGER.info(networkProvider);

    this.ini.set(DEFAULT_INI_SECTION_NAME, SettingsKey.NETWORK_PROVIDER.toString(), networkProvider.toString());
  }

  public void setConnectMode(ConnectMode connectMode) {
    LOGGER.info(connectMode);

    this.ini.set(DEFAULT_INI_SECTION_NAME, SettingsKey.CONNECT_MODE.toString(), connectMode.toString());
  }

  /**
   * Reads the specified INI and sets class member variables accordingly.
   *
   * @param ini specified INI object
   */
  public void parseSettings(INI ini) {
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
