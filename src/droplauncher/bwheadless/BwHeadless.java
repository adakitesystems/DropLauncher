/* BwHeadless.java */

package droplauncher.bwheadless;

import droplauncher.config.ConfigFile;
import droplauncher.debugging.Debugging;
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

  private static Logger logger = LogManager.getRootLogger();

  public static final String BW_HEADLESS_PATH = "bwheadless.exe";
  public static final String BWAPI_DIR = "bwapi-data";

  public static final String DEFAULT_CFG_FILE =
      "settings" + ConfigFile.FILE_EXTENSION;

  public static final String BWAPI_DLL_FILE =
      "checksums" + ConfigFile.FILE_EXTENSION;

  public static final String DEFAULT_BOT_NAME = "BOT";
  /* Maximum profile name length in Broodwar 1.16.1 */
  public static final int MAX_BOT_NAME_LENGTH = 24;

  public static ConfigFile bwapiDllChecksums;

  private ProcessPipe bwHeadlessPipe; /* required */
  private ProcessPipe botClientPipe;  /* required only when DLL is absent */
  private String starcraftExe;        /* required */
  private String bwapiDll;            /* required */
  private String detectedBwapiDll;    /* not required */
  private String botName;             /* required */
  private String botDllPath;          /* required only when client is absent */
  private String botClientPath;       /* required only when DLL is absent, *.exe or *.jar */
  private Race botRace;               /* required */
  private GameType gameType;          /* required */

  public static ArrayList<File> droppedFiles;

  /**
   * Intialize class members.
   */
  public BwHeadless() {
    this.bwHeadlessPipe   = new ProcessPipe();
    this.botClientPipe    = new ProcessPipe();
    this.starcraftExe     = null;
    this.bwapiDll         = null;
    this.detectedBwapiDll = null;
    this.botName          = DEFAULT_BOT_NAME;
    this.botDllPath       = null;
    this.botClientPath    = null;
    this.botRace          = Race.RANDOM;
    this.gameType         = GameType.LAN;
    this.droppedFiles     = new ArrayList<>();
  }

  /**
   * Returns the path to the Starcraft executable.
   *
   * @return the path to the Starcraft executable
   */
  public String getStarcraftExe() {
    return this.starcraftExe;
  }

  /**
   * Sets the path to the Starcraft executable.
   *
   * @param path specified path to set as Starcraft executable
   * @return
   *     true if path appears to valid,
   *     otherwise false
   */
  public boolean setStarcraftExe(String path) {
    if (MainTools.isEmpty(path)) {
      logger.warn(Debugging.EMPTY_STRING);
      return false;
    }
    if (!MainTools.doesFileExist(path)) {
      logger.warn("file inaccessible or does not exist: " + path);
      return false;
    }

    this.starcraftExe = path;

    logger.info("StarCraft.exe: " + this.starcraftExe);

    return true;
  }

  /**
   * Returns the path to the BWAPI DLL file.
   *
   * @return the path to the BWAPI DLL file
   */
  public String getBwapiDll() {
    return this.bwapiDll;
  }

  /**
   * Sets the BWAPI DLL path.
   *
   * @param path specified path
   * @return
   *     true if path appears to valid,
   *     otherwise false
   */
  public boolean setBwapiDll(String path) {
    if (!MainTools.doesFileExist(path)) {
      logger.warn("file inaccessible or does not exist" + path);
      return false;
    }

    this.bwapiDll = path;

    logger.info("BWAPI.dll: " + this.bwapiDll);

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
   * {@link #MAX_BOT_NAME_LENGTH}. Characters not matching A-Z, a-z, 0-9, or
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
    if (str.length() > MAX_BOT_NAME_LENGTH) {
      str = str.substring(0, MAX_BOT_NAME_LENGTH);
    }

    this.botName = str;

    logger.info("Bot name: " + this.botName);
  }

  /**
   * Returns the path to the BWAPI DLL file.
   *
   * @return
   *     the path to the BWAPI DLL file
   */
  public String getBotDll() {
    return this.botDllPath;
  }

  /**
   * Sets the bot DLL to the specified path.
   *
   * @param path specified path
   * @return
   *     true if path appears to valid,
   *     otherwise false
   */
  public boolean setBotDll(String path) {
    if (MainTools.isEmpty(path)) {
      logger.warn(Debugging.EMPTY_STRING);
      return false;
    }
    if (!MainTools.doesFileExist(path)) {
      logger.warn("file inaccessible or does not exist: " + path);
      return false;
    }

    this.botDllPath = path;

    logger.info("Bot dll: " + this.botDllPath);

    return true;
  }

  /**
   * Returns the path to the bot client file.
   *
   * @return
   *     the path to the bot client file.
   */
  public String getBotClient() {
    return this.botClientPath;
  }

  /**
   * Sets the bot client path to the specified path. Bot clients are usually
   * standalone EXE or JAR files.
   *
   * @param path specified path to client file
   * @return
   *     true if path appears to be valid,
   *     otherwise false
   */
  public boolean setBotClient(String path) {
    if (MainTools.isEmpty(path)) {
      logger.warn(Debugging.EMPTY_STRING);
      return false;
    }
    if (!MainTools.doesFileExist(path)) {
      logger.warn("file inaccessible or does not exist: " + path);
      return false;
    }

    this.botClientPath = path;

    logger.info("Bot client: " + this.botClientPath);

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
    logger.info("Bot race: " + this.botRace.toString());
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
    logger.info("Game type: " + this.gameType.toString());
  }

}
