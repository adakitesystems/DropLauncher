package droplauncher.bwheadless;

import adakite.utils.AdakiteUtils;
import droplauncher.bwapi.BWAPI;
import droplauncher.ini.IniFile;
import droplauncher.starcraft.Race;
import droplauncher.starcraft.Starcraft;
import droplauncher.util.Constants;
import droplauncher.util.ProcessPipe;
import droplauncher.util.Util;
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

  private static final Logger LOGGER = Logger.getLogger(BWHeadless.class.getName());
  private static final boolean CLASS_DEBUG = (Constants.DEBUG && true);

  public static final String BW_HEADLESS_EXE = "bwheadless.exe";
  public static final String BW_HEADLESS_INI_SECTION = "bwheadless";

  public static final String DEFAULT_BOT_NAME = "BOT";
  public static final Race DEFAULT_BOT_RACE = Race.NONE;
  public static final NetworkProvider DEFAULT_NETWORK_PROVIDER = NetworkProvider.LAN;
  public static final JoinMode DEFAULT_JOIN_MODE = JoinMode.JOIN;

  private ProcessPipe bwheadlessPipe;
  private ProcessPipe botPipe;

  private String starcraftExe; /* required */
  private String bwapiDll; /* required */
  private String botName; /* required */
  private String botDll; /* required only when client is absent */
  private String botClient; /* *.exe or *.jar, required only when DLL is absent  */
  private Race botRace; /* required */
  private NetworkProvider networkProvider; /* required */
  private JoinMode joinMode; /* required */

  private ArrayList<Path> miscFiles;

  private IniFile iniFile;

  public BWHeadless() {
    this.bwheadlessPipe = new ProcessPipe();
    this.botPipe = new ProcessPipe();

    this.starcraftExe = null;
    this.bwapiDll = null;
    this.botName = DEFAULT_BOT_NAME;
    this.botDll = null;
    this.botClient = null;
    this.botRace = DEFAULT_BOT_RACE;
    this.networkProvider = DEFAULT_NETWORK_PROVIDER;
    this.joinMode = DEFAULT_JOIN_MODE;

    this.miscFiles = new ArrayList<>();

    this.iniFile = null;
  }

  public ArrayList<Path> getMiscFiles() {
    return this.miscFiles;
  }

  public boolean isReady() {
    return (getReadyStatus() == ReadyStatus.READY);
  }

  /*
  TODO: Possibly remove or improve this.
  TODO: Redo this.
  */
  public ReadyStatus getReadyStatus() {
    if (AdakiteUtils.isNullOrEmpty(this.starcraftExe, true)
        || !AdakiteUtils.fileExists(Paths.get(this.starcraftExe))) {
      return ReadyStatus.STARTCRAFT_EXE;
    } else if (AdakiteUtils.isNullOrEmpty(this.bwapiDll, true)
        || !AdakiteUtils.fileExists(Paths.get(this.bwapiDll))) {
      return ReadyStatus.BWAPI_DLL;
    } else if (AdakiteUtils.isNullOrEmpty(this.botName, true)
        || this.botName.length() > Starcraft.MAX_PROFILE_NAME_LENGTH) {
      return ReadyStatus.BOT_NAME;
    } else if (
        (AdakiteUtils.isNullOrEmpty(this.botDll, true) || !AdakiteUtils.fileExists(Paths.get(this.botDll)))
        && (AdakiteUtils.isNullOrEmpty(this.botClient, true) || !AdakiteUtils.fileExists(Paths.get(this.botClient)))
    ) {
      /* If both the bot DLL and bot client fields are missing. */
      return ReadyStatus.BOT_FILE;
    } else if (this.botRace == null || this.botRace == Race.NONE) {
      return ReadyStatus.BOT_RACE;
    } else if (this.networkProvider == null) {
      return ReadyStatus.NETWORK_PROVIDER;
    } else if (this.joinMode == null) {
      return ReadyStatus.JOIN_MODE;
    } else {
      return ReadyStatus.READY;
    }
  }

  public boolean isRunning() {
    return this.bwheadlessPipe.isOpen();
  }

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
    this.bwheadlessPipe.open(new File(BW_HEADLESS_EXE), bargsArray, starcraftDirectory);

    /* Start bot client in a command prompt. */
    if (!AdakiteUtils.isNullOrEmpty(this.botClient)) {
      ArrayList<String> cargs = new ArrayList<>(); /* client arguments */
      cargs.add("/c");
      cargs.add("start");
      if (AdakiteUtils.getFileExtension(new File(this.botClient)).equalsIgnoreCase("jar")) {
        cargs.add("C:\\ProgramData\\Oracle\\Java\\javapath\\java.exe");
        cargs.add("-jar");
      }
      cargs.add(this.botClient);
      String[] cargsArray = Util.toStringArray(cargs);
      this.botPipe.open(new File("C:\\Windows\\System32\\cmd.exe"), cargsArray, starcraftDirectory);
    }
  }

  public void stop() {
    this.bwheadlessPipe.close();
  }

  private void configureBwapi() throws IOException {
    /* Determine StarCraft directory from StarCraft.exe path. */
    String starcraftDirectory =
        AdakiteUtils.getParentDirectory(Paths.get(this.starcraftExe)).toAbsolutePath().toString();

    /* Configure BWAPI INI file. */
    IniFile bwapiIni = new IniFile();
    bwapiIni.open(Paths.get(starcraftDirectory + File.separator + BWAPI.BWAPI_DATA_INI).toFile());
    if (!AdakiteUtils.isNullOrEmpty(this.botDll)) {
      bwapiIni.setVariable(
          "ai",
          "ai",
          BWAPI.BWAPI_DATA_AI_DIR + File.separator + Paths.get(this.botDll).getFileName().toString()
      );
    } else {
      bwapiIni.disableVariable("ai", "ai");
    }
    bwapiIni.disableVariable("ai", "ai_dbg");

    /* Prepare to copy bot files to StarCraft directory. */
    Path src = null;
    Path dest = null;
    if (!AdakiteUtils.isNullOrEmpty(this.botDll)) {
      /* Prepare to copy DLL to bwapi-data directory. */
      src = Paths.get(this.botDll);
      dest = Paths.get(starcraftDirectory,
          BWAPI.BWAPI_DATA_AI_DIR,
          Paths.get(this.botDll).getFileName().toString()
      );
      this.botDll = dest.toAbsolutePath().toString();
    } else if (!AdakiteUtils.isNullOrEmpty(this.botClient)) {
      /* Prepare to copy client to StarCraft root directory. */
      src = Paths.get(this.botClient);
      dest = Paths.get(starcraftDirectory,
          Paths.get(this.botClient).getFileName().toString()
      );
      this.botClient = dest.toAbsolutePath().toString();
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
  }

  public String getBwapiDll() {
    return this.bwapiDll;
  }

  public void setBwapiDll(String bwapiDll) {
    this.bwapiDll = bwapiDll;
    updateSettingsFile(PredefinedVariable.BWAPI_DLL.toString(), this.bwapiDll);
  }

  public String getBotName() {
    return this.botName;
  }

  public void setBotName(String botName) {
    String cleaned = Starcraft.cleanProfileName(botName);
    this.botName = cleaned;
    if (AdakiteUtils.isNullOrEmpty(this.botName)) {
      this.botName = DEFAULT_BOT_NAME;
    }
    updateSettingsFile(PredefinedVariable.BOT_NAME.toString(), this.botName);
  }

  public String getBotDll() {
    return this.botDll;
  }

  public void setBotDll(String botDll) {
    /* Disable any previous bot clients. */
    this.miscFiles.clear();
    this.botClient = null;
    updateSettingsFile(PredefinedVariable.BOT_CLIENT.toString(), "");

    /* Set bot DLL. */
    this.botDll = botDll;
    String name = AdakiteUtils.removeFileExtension(new File(this.botDll).getName());
    setBotName(name);
    updateSettingsFile(PredefinedVariable.BOT_DLL.toString(), this.botDll);
  }

  public String getBotClient() {
    return this.botClient;
  }

  public void setBotClient(String botClient) {
    /* Disable any previous bot DLL. */
    this.miscFiles.clear();
    this.botDll = null;
    updateSettingsFile(PredefinedVariable.BOT_DLL.toString(), "");

    /* Set bot client. */
    this.botClient = botClient;
    String name = AdakiteUtils.removeFileExtension(new File(this.botClient).getName());
    setBotName(name);
    updateSettingsFile(PredefinedVariable.BOT_CLIENT.toString(), this.botClient);
  }

  public Race getBotRace() {
    return this.botRace;
  }

  public void setBotRace(Race botRace) {
    this.botRace = botRace;
    updateSettingsFile(PredefinedVariable.BOT_RACE.toString(), this.botRace.toString());
  }

  public NetworkProvider getNetworkProvider() {
    return this.networkProvider;
  }

  public void setNetworkProvider(NetworkProvider networkProvider) {
    this.networkProvider = networkProvider;
    updateSettingsFile(PredefinedVariable.NETWORK_PROVIDER.toString(), this.networkProvider.toString());
  }

  public JoinMode getJoinMode() {
    return this.joinMode;
  }

  public void setJoinMode(JoinMode joinMode) {
    this.joinMode = joinMode;
    updateSettingsFile(PredefinedVariable.JOIN_MODE.toString(), this.joinMode.toString());
  }

  private void updateSettingsFile(String name, String key, String val) {
    try {
      this.iniFile.setVariable(name, key, val);
    } catch (Exception ex) {
      if (CLASS_DEBUG) {
        LOGGER.log(Constants.DEFAULT_LOG_LEVEL, null, ex);
      }
    }
  }

  private void updateSettingsFile(String key, String val) {
    updateSettingsFile(BW_HEADLESS_INI_SECTION, key, val);
  }

  public void readSettingsFile(IniFile ini) {
    String val;
    if (!AdakiteUtils.isNullOrEmpty(val = ini.getValue(BW_HEADLESS_INI_SECTION, PredefinedVariable.STARCRAFT_EXE.toString()))) {
      setStarcraftExe(val);
    }
    if (!AdakiteUtils.isNullOrEmpty(val = ini.getValue(BW_HEADLESS_INI_SECTION, PredefinedVariable.BWAPI_DLL.toString()))) {
      setBwapiDll(val);
    }
    if (!AdakiteUtils.isNullOrEmpty(val = ini.getValue(BW_HEADLESS_INI_SECTION, PredefinedVariable.BOT_NAME.toString()))) {
      setBotName(val);
    } else {
      setBotName(DEFAULT_BOT_NAME);
    }
    if (!AdakiteUtils.isNullOrEmpty(val = ini.getValue(BW_HEADLESS_INI_SECTION, PredefinedVariable.BOT_DLL.toString()))) {
      setBotDll(val);
    }
    if (!AdakiteUtils.isNullOrEmpty(val = ini.getValue(BW_HEADLESS_INI_SECTION, PredefinedVariable.BOT_CLIENT.toString()))) {
      setBotClient(val);
    }
    if (!AdakiteUtils.isNullOrEmpty(val = ini.getValue(BW_HEADLESS_INI_SECTION, PredefinedVariable.BOT_RACE.toString()))) {
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
    if (AdakiteUtils.isNullOrEmpty(val = ini.getValue(BW_HEADLESS_INI_SECTION, PredefinedVariable.NETWORK_PROVIDER.toString()))) {
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
    if (AdakiteUtils.isNullOrEmpty(val = ini.getValue(BW_HEADLESS_INI_SECTION, PredefinedVariable.JOIN_MODE.toString()))) {
      if (val.equalsIgnoreCase(JoinMode.JOIN.toString())) {
        setJoinMode(JoinMode.JOIN);
      } else {
        /* Unrecognized JoinMode. */
        setJoinMode(DEFAULT_JOIN_MODE);
      }
    } else {
      /* JoinMode wasn't set. */
      setJoinMode(DEFAULT_JOIN_MODE);
    }
  }

}
