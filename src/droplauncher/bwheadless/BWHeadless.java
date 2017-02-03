package droplauncher.bwheadless;

import adakite.utils.AdakiteUtils;
import droplauncher.bwapi.BWAPI;
import droplauncher.ini.IniFile;
import droplauncher.starcraft.Race;
import droplauncher.starcraft.Starcraft;
import droplauncher.util.Constants;
import droplauncher.util.ProcessPipe;
import droplauncher.util.Util;
import droplauncher.util.windows.Windows;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.logging.Logger;

/**
 * Class for handling execution and communication with the
 * bwheadless.exe process.
 */
public class BWHeadless {

  /**
   * Enum for INI keys related to bwheadless.
   */
  public enum PredefinedVariable {

    STARCRAFT_EXE("starcraft_exe"),
    BWAPI_DLL("bwapi_dll"),
    BOT_NAME("bot_name"),
    BOT_MODULE("bot_module"),
    BOT_RACE("bot_race"),
    NETWORK_PROVIDER("network"),
    JOIN_MODE("connect_mode"),
    GAME_NAME("game_name"),
    MAP("map")
    ;

    private String str;

    private PredefinedVariable(String str) {
      this.str = str;
    }

    @Override
    public String toString() {
      return this.str;
    }

  }

  private static final Logger LOGGER = Logger.getLogger(BWHeadless.class.getName());
  private static final boolean CLASS_DEBUG = (Constants.DEBUG && true);
  private static final boolean SET_DEBUG = true;

  public static final String BWHEADLESS_EXE = "bwheadless.exe";
  public static final String BWHEADLESS_INI_SECTION = "bwheadless";

  public static final String DEFAULT_BOT_NAME = "BOT";
  public static final Race DEFAULT_BOT_RACE = Race.RANDOM;
  public static final NetworkProvider DEFAULT_NETWORK_PROVIDER = NetworkProvider.LAN;
  public static final ConnectMode DEFAULT_JOIN_MODE = ConnectMode.JOIN;

  private ProcessPipe bwheadlessPipe;
  private ProcessPipe botPipe;

  private String starcraftExe; /* required */
  private String bwapiDll; /* required */
  private String botName; /* required */
  private BotModule botModule; /* required */
  private Race botRace; /* required */
  private NetworkProvider networkProvider; /* required */
  private ConnectMode joinMode; /* required */

  private ArrayList<Path> miscFiles;

  private IniFile iniFile;

  private Path javaPath;

  public BWHeadless() {
    this.bwheadlessPipe = new ProcessPipe();
    this.botPipe = new ProcessPipe();

    this.starcraftExe = null;
    this.bwapiDll = null;
    this.botName = DEFAULT_BOT_NAME;
    this.botModule = new BotModule();
    this.botRace = DEFAULT_BOT_RACE;
    this.networkProvider = DEFAULT_NETWORK_PROVIDER;
    this.joinMode = DEFAULT_JOIN_MODE;

    this.miscFiles = new ArrayList<>();

    this.iniFile = null;

    this.javaPath = null;
  }

  public ArrayList<Path> getMiscFiles() {
    return this.miscFiles;
  }

  public Path getJavaPath() {
    return this.javaPath;
  }

  public void setJavaPath(Path path) {
    this.javaPath = path;
  }

  /**
   * Tests if the program has sufficient information to run bwheadless.
   *
   * @see #getReadyStatus()
   * @return
   *     true if ready,
   *     otherwise false
   */
  public boolean isReady() {
    return (getReadyStatus() == ReadyStatus.READY);
  }

  /**
   * Returns a response depending on whether the program is ready
   * to run bwheadless.
   *
   * @return
   *     {@link ReadyStatus#READY} if ready,
   *     otherwise the corresponding value that is preventing status
   *     from being ready. E.g. {@link ReadyStatus#STARTCRAFT_EXE}
   */
  public ReadyStatus getReadyStatus() {
    if (!AdakiteUtils.fileExists(Paths.get(BWHEADLESS_EXE))) {
      return ReadyStatus.BWHEADLESS_EXE;
    } else if (AdakiteUtils.isNullOrEmpty(this.starcraftExe, true)
        || !AdakiteUtils.fileExists(Paths.get(this.starcraftExe))) {
      return ReadyStatus.STARTCRAFT_EXE;
    } else if (AdakiteUtils.isNullOrEmpty(this.bwapiDll, true)
        || !AdakiteUtils.fileExists(Paths.get(this.bwapiDll))) {
      return ReadyStatus.BWAPI_DLL;
    } else if (AdakiteUtils.isNullOrEmpty(this.botName, true)
        || this.botName.length() > Starcraft.MAX_PROFILE_NAME_LENGTH) {
      return ReadyStatus.BOT_NAME;
    } else if (this.botModule.getType() == BotModule.Type.UNKNOWN
        || !AdakiteUtils.fileExists(this.botModule.getPath())) {
      /* If both the bot DLL and bot client fields are missing. */
      return ReadyStatus.BOT_FILE;
    } else if (this.botRace == null || this.botRace == Race.NONE) {
      return ReadyStatus.BOT_RACE;
    } else if (this.networkProvider == null) {
      return ReadyStatus.NETWORK_PROVIDER;
    } else if (this.joinMode == null) {
      return ReadyStatus.CONNECT_MODE;
    } else {
      return ReadyStatus.READY;
    }
  }

  /**
   * Tests is the pipe is open between this program and bwheadless.
   *
   * @return
   *     true if the pipe is open,
   *     otherwise false
   */
  public boolean isRunning() {
    return this.bwheadlessPipe.isOpen();
  }

  /**
   * Attempts to start bwheadless after configuring and checking settings.
   */
  public void start() {
    if (isRunning() || !isReady()) {
      return;
    }

    try {
      configureBwapi();
    } catch (IOException ex) {
      LOGGER.log(Constants.DEFAULT_LOG_LEVEL, null, ex);
    }

    String starcraftDirectory = AdakiteUtils.getParentDirectory(Paths.get(this.starcraftExe)).toAbsolutePath().toString();

    /* Compile bwheadless arguments. */
    ArrayList<String> bargs = new ArrayList<>(); /* bot arguments */
    bargs.add(Argument.STARCRAFT_EXE.toString());
    bargs.add(getStarcraftExe());
    bargs.add(Argument.JOIN_GAME.toString());
    bargs.add(Argument.BOT_NAME.toString());
    bargs.add(getBotName());
    bargs.add(Argument.BOT_RACE.toString());
    bargs.add(getBotRace().toString());
    bargs.add(Argument.LOAD_DLL.toString());
    bargs.add(getBwapiDll());
    bargs.add(Argument.ENABLE_LAN.toString());
    bargs.add(Argument.STARCRAFT_INSTALL_PATH.toString());
    bargs.add(starcraftDirectory);
    String[] bargsArray = Util.toStringArray(bargs);

    /* Start bwheadless. */
    this.bwheadlessPipe.open(Paths.get(BWHEADLESS_EXE), bargsArray, starcraftDirectory);

    /* Start bot client in a command prompt. */
    if (this.botModule.getType() == BotModule.Type.CLIENT) {
      ArrayList<String> cargs = new ArrayList<>(); /* client arguments */
      cargs.add("/c");
      cargs.add("start");
      if (AdakiteUtils.getFileExtension(this.botModule.getPath()).equalsIgnoreCase("jar")) {
        cargs.add(this.javaPath.toAbsolutePath().toString());
        for (String arg : Windows.DEFAULT_JAR_ARGS) {
          cargs.add(arg);
        }
      }
      cargs.add(this.botModule.toString());
      String[] cargsArray = Util.toStringArray(cargs);
      this.botPipe.open(Windows.CMD_EXE, cargsArray, starcraftDirectory);
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
  private void configureBwapi() throws IOException {
    /* Determine StarCraft directory from StarCraft.exe path. */
    String starcraftDirectory =
        AdakiteUtils.getParentDirectory(Paths.get(this.starcraftExe)).toAbsolutePath().toString();

    /* Configure BWAPI INI file. */
    IniFile bwapiIni = new IniFile();
    bwapiIni.open(Paths.get(starcraftDirectory, BWAPI.BWAPI_DATA_INI));
    if (this.botModule.getType() == BotModule.Type.DLL) {
      bwapiIni.setVariable("ai",
          "ai",
          BWAPI.BWAPI_DATA_AI_DIR + File.separator + this.botModule.getPath().getFileName().toString()
      );
    } else {
      bwapiIni.disableVariable("ai", "ai");
    }
    bwapiIni.disableVariable("ai", "ai_dbg");

    /* Prepare to copy bot files to StarCraft directory. */
    Path src = null;
    Path dest = null;
    if (this.botModule.getType() == BotModule.Type.DLL) {
      /* Prepare to copy DLL to bwapi-data directory. */
      src = this.botModule.getPath();
      dest = Paths.get(starcraftDirectory,
          BWAPI.BWAPI_DATA_AI_DIR,
          Paths.get(this.botModule.toString()).getFileName().toString()
      );
      this.botModule.setPath(dest);
    } else if (this.botModule.getType() == BotModule.Type.CLIENT) {
      /* Prepare to copy client to StarCraft root directory. */
      src = this.botModule.getPath();
      dest = Paths.get(starcraftDirectory,
          this.botModule.getPath().getFileName().toString()
      );
      this.botModule.setPath(dest);
    }
    /* Copy. */
    AdakiteUtils.createDirectory(dest.getParent());
    Files.copy(src, dest, StandardCopyOption.REPLACE_EXISTING);

    /* Copy misc files to common paths. */
    Path readPath = Paths.get(starcraftDirectory, BWAPI.BWAPI_DATA_DIR_READ);
    Path writePath = Paths.get(starcraftDirectory, BWAPI.BWAPI_DATA_DIR_WRITE);
    Path aiPath = Paths.get(starcraftDirectory, BWAPI.BWAPI_DATA_AI_DIR);
    AdakiteUtils.createDirectory(readPath);
    AdakiteUtils.createDirectory(writePath);
    AdakiteUtils.createDirectory(aiPath);
    for (Path path : this.miscFiles) {
      Files.copy(path, Paths.get(readPath.toAbsolutePath().toString(), path.getFileName().toString()), StandardCopyOption.REPLACE_EXISTING);
      Files.copy(path, Paths.get(writePath.toAbsolutePath().toString(), path.getFileName().toString()), StandardCopyOption.REPLACE_EXISTING);
      Files.copy(path, Paths.get(aiPath.toAbsolutePath().toString(), path.getFileName().toString()), StandardCopyOption.REPLACE_EXISTING);
    }
  }

  public IniFile getIniFile() {
    return this.iniFile;
  }

  public void setIniFile(IniFile iniFile) {
    this.iniFile = iniFile;
  }

  public String getStarcraftExe() {
    return this.starcraftExe;
  }

  public void setStarcraftExe(String starcraftExe) {
    this.starcraftExe = starcraftExe;
    updateSettingsFile(PredefinedVariable.STARCRAFT_EXE.toString(), this.starcraftExe);

    if (SET_DEBUG) {
      System.out.println("setStarcraftExe = " + this.starcraftExe);
    }
  }

  public String getBwapiDll() {
    return this.bwapiDll;
  }

  public void setBwapiDll(String bwapiDll) {
    this.bwapiDll = bwapiDll;
    updateSettingsFile(PredefinedVariable.BWAPI_DLL.toString(), this.bwapiDll);

    if (SET_DEBUG) {
      System.out.println("setBwapiDll = " + bwapiDll);
    }
  }

  public String getBotName() {
    return this.botName;
  }

  public void setBotName(String botName) {
    String cleaned = Starcraft.cleanProfileName(botName);
    if (cleaned.equals(this.botName)) {
      return;
    }

    this.botName = cleaned;
    if (AdakiteUtils.isNullOrEmpty(this.botName)) {
      this.botName = DEFAULT_BOT_NAME;
    }
    updateSettingsFile(PredefinedVariable.BOT_NAME.toString(), this.botName);

    if (SET_DEBUG) {
      System.out.println("setBotName = " + this.botName);
    }
  }

  public BotModule getBotModule() {
    return this.botModule;
  }

  public void setBotModule(String botModule) {
    this.miscFiles.clear();
    this.botModule.setPath(botModule);
    updateSettingsFile(PredefinedVariable.BOT_MODULE.toString(), this.botModule.toString());

    if (SET_DEBUG) {
      System.out.println("setBotModule = " + botModule);
    }
  }

  public Race getBotRace() {
    return this.botRace;
  }

  public void setBotRace(Race botRace) {
    this.botRace = botRace;
    updateSettingsFile(PredefinedVariable.BOT_RACE.toString(), this.botRace.toString());

    if (SET_DEBUG) {
      System.out.println("setBotRace = " + botRace.toString());
    }
  }

  public NetworkProvider getNetworkProvider() {
    return this.networkProvider;
  }

  public void setNetworkProvider(NetworkProvider networkProvider) {
    this.networkProvider = networkProvider;
    updateSettingsFile(PredefinedVariable.NETWORK_PROVIDER.toString(), this.networkProvider.toString());

    if (SET_DEBUG) {
      System.out.println("setNetworkProvider = " + networkProvider.toString());
    }
  }

  public ConnectMode getConnectMode() {
    return this.joinMode;
  }

  public void setConnectMode(ConnectMode connectMode) {
    this.joinMode = connectMode;
    updateSettingsFile(PredefinedVariable.JOIN_MODE.toString(), this.joinMode.toString());

    if (SET_DEBUG) {
      System.out.println("setConnectMode = " + connectMode.toString());
    }
  }

  /**
   * Sets the specified variable and updates the class INI file.
   *
   * Catches and reports all exceptions thrown by
   * {@link droplauncher.ini.IniFile#setVariable(java.lang.String, java.lang.String, java.lang.String)}.
   *
   * @param name specified section name
   * @param key specified key
   * @param val specified value
   */
  private void updateSettingsFile(String name, String key, String val) {
    try {
      this.iniFile.setVariable(name, key, val);
    } catch (Exception ex) {
      if (CLASS_DEBUG) {
        LOGGER.log(Constants.DEFAULT_LOG_LEVEL, null, ex);
      }
    }
  }

  /**
   * Sets the specified variable and updates the class INI file. The default
   * section name is {@link #BWHEADLESS_INI_SECTION}.
   *
   * @param key specified key
   * @param val specified value
   *
   * @see #updateSettingsFile(java.lang.String, java.lang.String, java.lang.String)
   */
  private void updateSettingsFile(String key, String val) {
    updateSettingsFile(BWHEADLESS_INI_SECTION, key, val);
  }

  /**
   * Reads the specified IniFile and sets class member variables accordingly.
   *
   * @param iniFile specified IniFile object
   */
  public void readSettingsFile(IniFile iniFile) {
    String val;
    if (!AdakiteUtils.isNullOrEmpty(val = iniFile.getValue(BWHEADLESS_INI_SECTION, PredefinedVariable.STARCRAFT_EXE.toString()))) {
      setStarcraftExe(val);
    }
    if (!AdakiteUtils.isNullOrEmpty(val = iniFile.getValue(BWHEADLESS_INI_SECTION, PredefinedVariable.BWAPI_DLL.toString()))) {
      setBwapiDll(val);
    }
    if (!AdakiteUtils.isNullOrEmpty(val = iniFile.getValue(BWHEADLESS_INI_SECTION, PredefinedVariable.BOT_NAME.toString()))) {
      setBotName(val);
    } else {
      setBotName(DEFAULT_BOT_NAME);
    }
    if (!AdakiteUtils.isNullOrEmpty(val = iniFile.getValue(BWHEADLESS_INI_SECTION, PredefinedVariable.BOT_MODULE.toString()))) {
      setBotModule(val);
    }
    if (!AdakiteUtils.isNullOrEmpty(val = iniFile.getValue(BWHEADLESS_INI_SECTION, PredefinedVariable.BOT_RACE.toString()))) {
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
    if (AdakiteUtils.isNullOrEmpty(val = iniFile.getValue(BWHEADLESS_INI_SECTION, PredefinedVariable.NETWORK_PROVIDER.toString()))) {
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
    if (AdakiteUtils.isNullOrEmpty(val = iniFile.getValue(BWHEADLESS_INI_SECTION, PredefinedVariable.JOIN_MODE.toString()))) {
      if (val.equalsIgnoreCase(ConnectMode.JOIN.toString())) {
        setConnectMode(ConnectMode.JOIN);
      } else {
        /* Unrecognized JoinMode. */
        setConnectMode(DEFAULT_JOIN_MODE);
      }
    } else {
      /* JoinMode wasn't set. */
      setConnectMode(DEFAULT_JOIN_MODE);
    }
  }

}
