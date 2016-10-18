package droplauncher.bwheadless;

import droplauncher.MainWindow;
import droplauncher.bwapi.Bwapi;
import droplauncher.config.ConfigFile;
import droplauncher.debugging.Debugging;
import droplauncher.filedroplist.FileDropList;
import droplauncher.starcraft.Race;
import droplauncher.starcraft.Starcraft;
import droplauncher.tools.MainTools;
import droplauncher.tools.ProcessPipe;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.util.ArrayList;

/**
 * Singleton class for handling communication with "bwheadless.exe" and
 * starting the bot client if present.
 *
 * @author Adakite Systems
 * @author adakitesystems@gmail.com
 */
public class BwHeadless {

  private static final Logger LOGGER = LogManager.getRootLogger();

  public static final File BW_HEADLESS_EXE = new File("bwheadless.exe");

  public static final String DEFAULT_CFG_FILE =
      "settings" + ConfigFile.FILE_EXTENSION;

  public static final String DEFAULT_BOT_NAME = "BOT";

  private ProcessPipe bwHeadlessPipe; /* required */
  private ProcessPipe botClientPipe;  /* required only when DLL is absent */
  private File        starcraftExe;   /* required */
  private File        bwapiDll;       /* required */
  private String      botName;        /* required */
  private File        botDll;         /* required only when client is absent */
  private File        botClient;      /* required only when DLL is absent, *.exe or *.jar */
  private Race       botRace;        /* required */
  private GameType   gameType;       /* required */

  /**
   * Intialize class members.
   */
  public BwHeadless() {
    this.bwHeadlessPipe   = new ProcessPipe();
    this.botClientPipe    = new ProcessPipe();
    this.starcraftExe     = null;
    this.bwapiDll         = null;
    this.botName          = DEFAULT_BOT_NAME;
    this.botDll           = null;
    this.botClient        = null;
    this.botRace          = Race.RANDOM;
    this.gameType         = GameType.LAN;

    ensureDefaultConfigFile();
  }

  /**
   * Ensure the default configuration file exists.
   *
   * @return
   *     true if it exists or has been created successfully,
   *     otherwise false
   */
  public boolean ensureDefaultConfigFile() {
    File file = new File(BwHeadless.DEFAULT_CFG_FILE);
    if (!MainTools.doesFileExist(file)) {
      ConfigFile cf = new ConfigFile();
      if (!cf.open(file)) {
        LOGGER.error(Debugging.createFail(file));
        return false;
      }
      cf.setVariable(PredefinedVariables.STARCRAFT_EXE.toString(), null);
      cf.setVariable(PredefinedVariables.BWAPI_DLL.toString(), null);
      cf.setVariable(PredefinedVariables.GAME_TYPE.toString(), this.gameType.toString());
      cf.setVariable(PredefinedVariables.BOT_NAME.toString(), this.botName);
      cf.setVariable(PredefinedVariables.BOT_RACE.toString(), this.botRace.toString());
      cf.setVariable(PredefinedVariables.BOT_DLL.toString(), null);
      cf.setVariable(PredefinedVariables.BOT_CLIENT.toString(), null);
    }
    return readConfigFile(file);
  }

  /**
   * Read specified configuration file.
   *
   * @param file specified configuration file.
   * @return
   *     true if configuration file has been read and class members have
   *         been set,
   *     otherwise false
   */
  public boolean readConfigFile(File file) {
    LOGGER.info("loading config file: " + file.getAbsolutePath());
    ConfigFile cf = new ConfigFile();
    if (!cf.open(file)) {
      LOGGER.error(Debugging.openFail(file));
      return false;
    }

    String tmpValue;
    if (!MainTools.isEmpty(tmpValue = cf.getValue(PredefinedVariables.STARCRAFT_EXE.toString()))) {
      /* StarCraft.exe */
      setStarcraftExe(new File(tmpValue));
    }
    if (!MainTools.isEmpty(tmpValue = cf.getValue(PredefinedVariables.BWAPI_DLL.toString()))) {
      /* BWAPI.dll */
      setBwapiDll(new File(tmpValue));
    }
    if (!MainTools.isEmpty(tmpValue = cf.getValue(PredefinedVariables.GAME_TYPE.toString()))) {
      /* Game Type */
      if (tmpValue.equalsIgnoreCase(GameType.LAN.toString())) {
        setGameType(GameType.LAN);
      }
    }
    if (!MainTools.isEmpty(tmpValue = cf.getValue(PredefinedVariables.BOT_NAME.toString()))) {
      /* Bot name */
      setBotName(tmpValue);
    }
    if (!MainTools.isEmpty(tmpValue = cf.getValue(PredefinedVariables.BOT_RACE.toString()))) {
      /* Race */
      if (tmpValue.equalsIgnoreCase(Race.TERRAN.toString())) {
        setBotRace(Race.TERRAN);
      } else if (tmpValue.equalsIgnoreCase(Race.ZERG.toString())) {
        setBotRace(Race.ZERG);
      } else if (tmpValue.equalsIgnoreCase(Race.PROTOSS.toString())) {
        setBotRace(Race.PROTOSS);
      } else if (tmpValue.equalsIgnoreCase(Race.RANDOM.toString())) {
        setBotRace(Race.RANDOM);
      }
    }
    if (!MainTools.isEmpty(tmpValue = cf.getValue(PredefinedVariables.BOT_DLL.toString()))) {
      /* Bot DLL */
      setBotDll(new File(tmpValue));
    }
    if (!MainTools.isEmpty(tmpValue = cf.getValue(PredefinedVariables.BOT_CLIENT.toString()))) {
      /* Bot client */
      setBotClient(new File(tmpValue));
    }

    return true;
  }

  /**
   * Write all class members such as {@link #botClient} and
   * {@link #starcraftExe} to the default configuration file.
   *
   * @return
   *     true if variables have been written to configuration file,
   *     otherwise false
   */
  public boolean writeDefaultConfigFile() {
    ConfigFile cf = new ConfigFile();
    if (!cf.open(new File(BwHeadless.DEFAULT_CFG_FILE))) {
      return false;
    }

    /* Set variables which are not null. */
    if (this.botClient != null) {
      cf.setVariable(
          PredefinedVariables.BOT_CLIENT.toString(),
//          MainTools.getFullPath(this.botClient)
          this.botClient.getAbsolutePath()
      );
    } else {
      cf.setVariable(PredefinedVariables.BOT_CLIENT.toString(), null);
    }
    if (this.botDll != null) {
      cf.setVariable(
          PredefinedVariables.BOT_DLL.toString(),
//          MainTools.getFullPath(this.botDll)
          this.botDll.getAbsolutePath()
      );
    } else {
      cf.setVariable(PredefinedVariables.BOT_DLL.toString(), null);
    }
    cf.setVariable(
        PredefinedVariables.BOT_NAME.toString(),
        this.botName
    );
    cf.setVariable(
        PredefinedVariables.BOT_RACE.toString(),
        this.botRace.toString()
    );
    if (this.bwapiDll != null) {
      cf.setVariable(
          PredefinedVariables.BWAPI_DLL.toString(),
//          MainTools.getFullPath(this.bwapiDll)
          this.bwapiDll.getAbsolutePath()
      );
    } else {
      cf.setVariable(PredefinedVariables.BWAPI_DLL.toString(), null);
    }
    cf.setVariable(
        PredefinedVariables.GAME_TYPE.toString(),
        this.gameType.toString()
    );
    if (this.starcraftExe != null) {
      cf.setVariable(
          PredefinedVariables.STARCRAFT_EXE.toString(),
//          MainTools.getFullPath(this.starcraftExe)
          this.starcraftExe.getAbsolutePath()
      );
    } else {
      cf.setVariable(PredefinedVariables.STARCRAFT_EXE.toString(), null);
    }

    return true;
  }

  /**
   * Tests whether all required data is known.
   *
   * @return
   *     null if all required data is known,
   *     otherwise a string indicating missing data
   */
  public String getNotReadyError() {
    if (this.starcraftExe == null) {
      return "missing StarCraft.exe";
    } else if (this.bwapiDll == null) {
      return "missing BWAPI.dll";
    } else if (MainTools.isEmpty(this.botName)) {
      return "missing bot name";
    } else if (this.botDll == null
        && this.botClient == null) {
      return "missing bot files";
    }
    return null;
  }

  /**
   * Tests whether the bot is ready for launch.
   *
   * @return
   *     true if bot is ready,
   *     otherwise false
   */
  public boolean isReady() {
    return (MainTools.isEmpty(getNotReadyError()));
  }

  /**
   * Returns the Starcraft executable.
   *
   * @return the Starcraft executable
   */
  public File getStarcraftExe() {
    return this.starcraftExe;
  }

  /**
   * Launch bot processes.
   *
   * @return
   *     true if bot process appear to be running,
   *     otherwise false
   */
  public boolean launch() {
    LOGGER.info("launch(): ACK");
    eject();

    String notReadyError = getNotReadyError();
    if (!MainTools.isEmpty(notReadyError)) {
      LOGGER.warn("not ready to launch: " + notReadyError);
      return false;
    }

    ConfigFile ini = new ConfigFile();
    String starcraftDir = MainTools.getParentDirectory(this.starcraftExe);
    ini.open(new File(starcraftDir + File.separator + Bwapi.BWAPI_DATA_INI));

    /* Enable or disable "ai" variable in "BWAPI.ini". */
    if (this.botDll != null) {
      String dllDest =
          Bwapi.BWAPI_DATA_DIR + File.separator
          + "AI" + File.separator
          + this.botDll.getName();
      ini.enableVariable("ai");
      ini.setVariable("ai", dllDest);
    } else if (this.botClient != null) {
      ini.disableVariable("ai");
    }

    ArrayList<String> bwheadlessArgs = new ArrayList<>();

    bwheadlessArgs.add(Argument.STARCRAFT_EXE.toString());
    bwheadlessArgs.add(MainTools.getFullPath(this.starcraftExe));

    bwheadlessArgs.add(Argument.JOIN_GAME.toString());

    bwheadlessArgs.add(Argument.BOT_NAME.toString());
    bwheadlessArgs.add(this.botName);

    bwheadlessArgs.add(Argument.BOT_RACE.toString());
    bwheadlessArgs.add(this.botRace.toString());

    bwheadlessArgs.add(Argument.LOAD_DLL.toString());
    bwheadlessArgs.add(MainTools.getFullPath(this.bwapiDll));

    if (this.gameType == GameType.LAN) {
      bwheadlessArgs.add(Argument.ENABLE_LAN.toString());
    }

    bwheadlessArgs.add(Argument.STARCRAFT_INSTALL_PATH.toString());
    bwheadlessArgs.add(starcraftDir);

    if (this.botClient != null
        && !this.botClientPipe.open(this.botClient, null)) {
      LOGGER.error(
          "failed to start bot client: "
          + this.botClient.getAbsolutePath()
      );
      return false;
    }

    String command =
        BwHeadless.BW_HEADLESS_EXE.getAbsolutePath()
        + " " + MainTools.toString(bwheadlessArgs);
    if (!this.bwHeadlessPipe.open(BwHeadless.BW_HEADLESS_EXE,
        MainTools.toStringArray(bwheadlessArgs))) {
      LOGGER.error("failed to start bwheadless.exe: " + command);
      return false;
    }

    LOGGER.info("Command: " + command);

    return true;
  }

  public void eject() {
    LOGGER.info("eject(): ACK");
    this.bwHeadlessPipe.close();
    this.botClientPipe.close();
  }

  /**
   * Sets the Starcraft executable.
   *
   * @param file Starcraft executable
   * @return
   *     true if file appears to valid,
   *     otherwise false
   */
  public boolean setStarcraftExe(File file) {
    if (file == null) {
      this.starcraftExe = null;
      LOGGER.warn("set StarCraft.exe: null");
    }
    if (file != null && !MainTools.doesFileExist(file)) {
      LOGGER.warn(Debugging.fileDoesNotExist(file));
      return false;
    }

    this.starcraftExe = file;

    writeDefaultConfigFile();

    LOGGER.info("set StarCraft.exe: " + MainTools.getFullPath(this.starcraftExe));

    return true;
  }

  /**
   * Returns the BWAPI DLL file.
   *
   * @return the BWAPI DLL file
   */
  public File getBwapiDll() {
    return this.bwapiDll;
  }

  /**
   * Sets the BWAPI DLL.
   *
   * @param file BWAPI DLL
   * @return
   *     true if file appears to valid,
   *     otherwise false
   */
  public boolean setBwapiDll(File file) {
    if (file == null) {
      this.bwapiDll = null;
      LOGGER.info("set BWAPI.dll file reset");
    }
    if (file != null && !MainTools.doesFileExist(file)) {
      LOGGER.warn(Debugging.fileDoesNotExist(file));
      return false;
    }

    this.bwapiDll = file;

    writeDefaultConfigFile();

    LOGGER.info("set BWAPI.dll: " + MainTools.getFullPath(this.bwapiDll));

    return true;
  }

  /**
   * Returns the name of this bot.
   *
   * @return the name of this bot
   */
  public String getBotName() {
    return this.botName;
  }

  /**
   * Sets the name of the bot. The name length is capped at
   * {@link droplauncher.starcraft.Starcraft#MAX_PROFILE_NAME_LENGTH}.
   * Characters not matching A-Z, a-z, 0-9, or
   * standard parenthesis will be removed. If a null or empty name
   * is specified, the name will be set too {@link #DEFAULT_BOT_NAME}.
   *
   * @param str string to set as bot name
   */
  public void setBotName(String str) {
    if (MainTools.isEmpty(str)
        || (str = MainTools.onlyLettersNumbers(str)) == null) {
      str = DEFAULT_BOT_NAME;
    }
    if (str.length() > Starcraft.MAX_PROFILE_NAME_LENGTH) {
      str = str.substring(0, Starcraft.MAX_PROFILE_NAME_LENGTH);
    }

    this.botName = str;

    writeDefaultConfigFile();

    LOGGER.info("set bot name: " + this.botName);
  }

  /**
   * Returns the bot DLL file.
   *
   * @return the bot DLL file
   */
  public File getBotDll() {
    return this.botDll;
  }

  /**
   * Sets the bot DLL to the specified file.
   *
   * @param file specified file
   * @return
   *     true if file appears to valid,
   *     otherwise false
   */
  public boolean setBotDll(File file) {
    if (file == null) {
      this.botDll = null;
      LOGGER.info("set bot DLL:  null");
    }
    if (file != null && !MainTools.doesFileExist(file)) {
      LOGGER.warn(Debugging.fileDoesNotExist(file));
      return false;
    }

    this.botClient = null;
    this.botDll = file;

    writeDefaultConfigFile();

    LOGGER.info("set bot DLL: " + MainTools.getFullPath(this.botDll));

    return true;
  }

  /**
   * Returns the bot client file.
   *
   * @return the bot client file
   */
  public File getBotClient() {
    return this.botClient;
  }

  /**
   * Sets the bot client. Bot clients are usually standalone EXE or JAR files.
   *
   * @param file specified client file
   * @return
   *     true if path appears to be valid,
   *     otherwise false
   */
  public boolean setBotClient(File file) {
    if (file == null) {
      this.botClient = null;
      LOGGER.info("set bot client path reset");
    }
    if (file != null && !MainTools.doesFileExist(file)) {
      LOGGER.warn(Debugging.fileDoesNotExist(file));
      return false;
    }

    this.botClient = file;
    this.botDll = null;

    writeDefaultConfigFile();

    LOGGER.info("set bot client: " + MainTools.getFullPath(this.botClient));

    return true;
  }

  /**
   * Returns the race of the specified bot.
   *
   * @return the race of the specified bot
   */
  public Race getBotRace() {
    return this.botRace;
  }

  /**
   * Sets the race of the bot.
   *
   * @param race specified bot race
   */
  public void setBotRace(Race race) {
    this.botRace = race;
    writeDefaultConfigFile();
    LOGGER.info("set bot race: " + this.botRace.toString());
  }

  /**
   * Returns the game type.
   *
   * @return the game type
   */
  public GameType getGameType() {
    return this.gameType;
  }

  /**
   * Sets the game type.
   *
   * @param gameType specified game type
   */
  public void setGameType(GameType gameType) {
    this.gameType = GameType.LAN; /* force game type as LAN UDP */
    writeDefaultConfigFile();
    LOGGER.info("set game type: " + this.gameType.toString());
  }

  /**
   * Read dropped files from FileDropList.
   */
  public void readDroppedFiles() {
    ArrayList<File> droppedFiles = FileDropList.INSTANCE.getFiles();
    String tmpName;
    String tmpNameLower;
    for (File tmpFile : droppedFiles) {
      tmpName = tmpFile.getName();
      tmpNameLower = tmpName.toLowerCase();
      if (tmpNameLower.equals("bwapi.dll")) {
        setBwapiDll(tmpFile);
      } else if (tmpNameLower.endsWith(".dll")) {
        setBotDll(tmpFile);
        String botName = MainTools.removeFileExtension(tmpName);
        if (!botName.toLowerCase().contains("bot")) {
          botName += " BOT";
        }
        setBotName(botName);
      } else if (tmpNameLower.endsWith(".exe")) {
        setBotClient(tmpFile);
        String botName = MainTools.removeFileExtension(tmpName);
        if (!botName.toLowerCase().contains("bot")) {
          botName += " BOT";
        }
        setBotName(botName);
      }
    }
    MainWindow.mainWindow.updateInfo();
  }

}
