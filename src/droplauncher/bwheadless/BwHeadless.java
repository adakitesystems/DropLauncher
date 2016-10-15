/* BwHeadless.java */

package droplauncher.bwheadless;

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

  public static ConfigFile bwapiDllChecksums;

  private ProcessPipe bwHeadlessPipe; /* required */
  private ProcessPipe botClientPipe;  /* required only when DLL is absent */
  private File        starcraftExe;   /* required */
  private File        bwapiDll;       /* required */
  private String      botName;        /* required */
  private File        botDll;         /* required only when client is absent */
  private File        botClient;      /* required only when DLL is absent, *.exe or *.jar */
  private Race        botRace;        /* required */
  private GameType    gameType;       /* required */

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
    return (getNotReadyError() == null);
  }

  /**
   * Returns the Starcraft executable
   *
   * @return the Starcraft executable
   */
  public File getStarcraftExe() {
    return this.starcraftExe;
  }

  public boolean launch() {
    if (!isReady()) {
      LOGGER.warn("not ready to launch: " + getNotReadyError());
    }

    ConfigFile ini = new ConfigFile();
    String starcraftDir = MainTools.getParentDirectory(this.starcraftExe);
    ini.open(starcraftDir + File.separator + Bwapi.BWAPI_DATA_INI);

    /* Enable or disable "ai" variable in "BWAPI.ini". */
    if (this.botDll != null) {
      ini.enableVariable("ai");
      ini.setVariable("ai", MainTools.getFullPath(this.botDll));
    } else if (this.botClient != null) {
      ini.disableVariable("ai");
    }

    ArrayList<String> bwheadlessArgs = new ArrayList<>();

    bwheadlessArgs.add(Arguments.STARCRAFT_EXE.toString());
    bwheadlessArgs.add(MainTools.getFullPath(this.starcraftExe));

    bwheadlessArgs.add(Arguments.JOIN_GAME.toString());

    bwheadlessArgs.add(Arguments.BOT_NAME.toString());
    bwheadlessArgs.add(this.botName);

    bwheadlessArgs.add(Arguments.BOT_RACE.toString());
    bwheadlessArgs.add(this.botRace.toString());

    bwheadlessArgs.add(Arguments.LOAD_DLL.toString());
    bwheadlessArgs.add(MainTools.getFullPath(this.bwapiDll));

    if (this.gameType == GameType.LAN) {
      bwheadlessArgs.add(Arguments.ENABLE_LAN.toString());
    } else if (this.gameType == GameType.LOCAL_PC) {
      bwheadlessArgs.add(Arguments.ENABLE_LOCAL_PC.toString());
    }

    bwheadlessArgs.add(Arguments.STARCRAFT_INSTALL_PATH.toString());
    bwheadlessArgs.add(MainTools.getParentDirectory(this.starcraftExe));

    if (this.botClient != null
        && !this.botClientPipe.open(this.botClient, null)) {
      LOGGER.error(
          "failed to start bot client: "
          + this.botClient.getAbsolutePath()
      );
      return false;
    }

    if (!this.bwHeadlessPipe.open(BwHeadless.BW_HEADLESS_EXE,
        MainTools.toStringArray(bwheadlessArgs))) {
      LOGGER.error(
          "failed to start: " + BwHeadless.BW_HEADLESS_EXE.getAbsolutePath()
          + " " + MainTools.toString(bwheadlessArgs)
      );
      return false;
    }

    return true;
  }

  public void eject() {
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
      LOGGER.warn(Debugging.NULL_OBJECT);
      return false;
    }
    if (!MainTools.doesFileExist(file)) {
      LOGGER.warn("file inaccessible or does not exist: " + file.getAbsolutePath());
      return false;
    }

    this.starcraftExe = file;

    LOGGER.info("StarCraft.exe: " + MainTools.getFullPath(this.starcraftExe));

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
      LOGGER.warn(Debugging.NULL_OBJECT);
      return false;
    }
    if (!MainTools.doesFileExist(file)) {
      LOGGER.warn("file inaccessible or does not exist" + file.getAbsolutePath());
      return false;
    }

    this.bwapiDll = file;

    LOGGER.info("BWAPI.dll: " + MainTools.getFullPath(this.bwapiDll));

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

    LOGGER.info("Bot name: " + this.botName);
  }

  /**
   * Returns the BWAPI DLL file.
   *
   * @return the BWAPI DLL file
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
      return true;
    }
    if (!MainTools.doesFileExist(file)) {
      LOGGER.warn("file inaccessible or does not exist: " + file.getAbsolutePath());
      return false;
    }

    this.botDll = file;
    this.botClient = null;

    LOGGER.info("Bot dll: " + MainTools.getFullPath(this.botDll));

    return true;
  }

  /**
   * Returns the bot client file.
   *
   * @return
   *     the bot client file
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
      return true;
    }
    if (!MainTools.doesFileExist(file)) {
      LOGGER.warn("file inaccessible or does not exist: " + file.getAbsolutePath());
      return false;
    }

    this.botClient = file;
    this.botDll = null;

    LOGGER.info("Bot client: " + MainTools.getFullPath(this.botClient));

    return true;
  }

  /**
   * Returns the race of the specified bot.
   *
   * @return
   *     the race of the specified bot
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
    LOGGER.info("Bot race: " + this.botRace.toString());
  }

  /**
   * Returns the game type.
   *
   * @return
   *     the game type
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
    this.gameType = gameType;
    LOGGER.info("Game type: " + this.gameType.toString());
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
      } else if (tmpNameLower.endsWith(".exe")) {
        setBotClient(tmpFile);
      }
    }
  }

}
