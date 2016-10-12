/* BwHeadless.java */

package droplauncher.bwheadless;

import droplauncher.config.ConfigFile;
import droplauncher.tools.MainTools;
import droplauncher.tools.ProcessPipe;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Singleton class for handling communication with "bwheadless.exe" and
 * starting the bot client if present.
 *
 * @author Adakite Systems
 * @author adakitesystems@gmail.com
 */
public class BwHeadless {

  public static enum GameType {
    lan,
    localpc
  }

  public static enum Race {
    Terran,
    Zerg,
    Protoss,
    Random
  }

  public static final BwHeadless INSTANCE = new BwHeadless();

  private static final Logger LOGGER = Logger.getLogger(BwHeadless.class.getName());
  private static final boolean CLASS_DEBUG = (MainTools.DEBUG && true);

  public static final String ARG_STARCRAFT_EXE = "-e"; /* requires second string */
  public static final String ARG_JOIN = "-j";
  public static final String ARG_BOT_NAME = "-n"; /* requires second string */
  public static final String ARG_BOT_RACE = "-r"; /* requires second string */
  public static final String ARG_LOAD_DLL = "-l"; /* requires second string */
  public static final String ARG_ENABLE_LAN = "--lan";
  public static final String ARG_ENABLE_LOCAL_PC = "--localpc";
  public static final String ARG_STARCRAFT_INSTALL_PATH =
      "--installpath"; /* requires second string */

  public static final String CFG_STARCRAFT_EXE = "starcraft_exe";
  public static final String CFG_BOT_NAME = "bot_name";
  public static final String CFG_BOT_DLL = "bot_dll";
  public static final String CFG_BOT_CLIENT = "bot_client";
  public static final String CFG_BOT_RACE = "bot_race";
  public static final String CFG_GAME_TYPE = "game_type";



  public static final String DEFAULT_CFG_FILE = "settings.cfg";

  public static final String DEFAULT_BOT_NAME = "BOT";
  public static final int MAX_NAME_LENGTH = 24;

  private ProcessPipe bwHeadlessPipe;
  private ProcessPipe botClientPipe;

  private String starcraftExe;
  private String botName;
  private String botDllPath;
  private String botClientPath; /* e.g. EXE or JAR bot client */

  private Race botRace;
  private GameType gameType;

  private BwHeadless() {
    this.bwHeadlessPipe = new ProcessPipe();
    this.botClientPipe = new ProcessPipe();
    this.starcraftExe = null;
    this.botName = DEFAULT_BOT_NAME;
    this.botRace = Race.Random;
    this.botDllPath = null;
    this.botClientPath = null;
    this.gameType = GameType.lan;
  }

  /**
   * Create a default config layout file.
   */
  public void createDefaultConfig() {
    ConfigFile cf = new ConfigFile();
    if (cf.create(BwHeadless.DEFAULT_CFG_FILE)) {
      cf.createVariable("starcraft_exe", "");
      cf.createVariable("game_type", "lan");
      cf.createVariable("bot_dll", "");
      cf.createVariable("bot_client", "");
      cf.createVariable("bot_race", "Random");
      cf.createVariable("bot_client", "");
      cf.createVariable("bot_name", BwHeadless.DEFAULT_BOT_NAME);
    }
  }

  public String getStarcraftExe() {
    return this.starcraftExe;
  }

  /**
   * Sets the path to the Starcraft executable if the specified path
   * is valid.
   *
   * @param path specified path to set as Starcraft executable
   * @return
   *     true if path appears to valid,
   *     otherwise false
   */
  public boolean setStarcraftExe(String path) {
    /* Validate parameters. */
    if (MainTools.isEmpty(path)) {
      if (CLASS_DEBUG) {
        LOGGER.log(Level.WARNING, MainTools.EMPTY_STRING);
      }
      return false;
    }
    if (!MainTools.doesFileExist(path)) {
      if (CLASS_DEBUG) {
        LOGGER.log(Level.WARNING, "file inaccessbile or does not exist: " + path);
      }
      return false;
    }

    this.starcraftExe = path;

    if (CLASS_DEBUG) {
      System.out.println("StarCraft.exe: " + this.starcraftExe);
    }

    return true;
  }

  public String getBotName() {
    return this.botName;
  }

  /**
   * Sets the name of the bot. The name length is capped at
   * {@link #MAX_NAME_LENGTH}. Characters not matching A-Z, a-z, 0-9, or
   * standard parenthesis will be removed. If a null or empty name
   * is specified, the name will be set too {@link #DEFAULT_BOT_NAME}.
   *
   * @param str string to set as bot name
   */
  public void setBotName(String str) {
    /* Validate parameters. */
    if (MainTools.isEmpty(str)
        || (str = MainTools.onlyLettersNumbers(str)) == null) {
      str = DEFAULT_BOT_NAME;
    }
    if (str.length() > MAX_NAME_LENGTH) {
      str = str.substring(0, MAX_NAME_LENGTH);
    }

    this.botName = str;

    if (CLASS_DEBUG) {
      System.out.println("Bot name: " + this.botName);
    }
  }

  /**
   * Returns the path to the BWAPI DLL file.
   */
  public String getBotDll() {
    return this.botDllPath;
  }

  /**
   * Sets the bot dll to the specified path.
   *
   * @param path specified path
   * @return
   *     true if path appears to valid,
   *     otherwise false
   */
  public boolean setBotDll(String path) {
    /* Validate parameters. */
    if (MainTools.isEmpty(path)) {
      if (CLASS_DEBUG) {
        LOGGER.log(Level.WARNING, MainTools.EMPTY_STRING);
      }
      return false;
    }
    if (!MainTools.doesFileExist(path)) {
      if (CLASS_DEBUG) {
        LOGGER.log(Level.WARNING, "file inaccessbile or does not exist: " + path);
      }
      return false;
    }

    this.botDllPath = path;

    return true;
  }

  /**
   * Returns the path to the bot client file.
   */
  public String getBotClient() {
    return this.botClientPath;
  }

  /**
   * Sets the bot client path to the specified path. Bot clients are usually
   * standalone EXE or JAR files.
   *
   * @param path specified path
   * @return
   *     true if path appears to be valid,
   *     otherwise false
   */
  public boolean setBotClient(String path) {
    /* Validate parameters. */
    if (MainTools.isEmpty(path)) {
      if (CLASS_DEBUG) {
        LOGGER.log(Level.WARNING, MainTools.EMPTY_STRING);
      }
      return false;
    }
    if (!MainTools.doesFileExist(path)) {
      if (CLASS_DEBUG) {
        LOGGER.log(Level.WARNING, "file inaccessible or does not exist: " + path);
      }
      return false;
    }

    this.botClientPath = path;

    return true;
  }

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
    if (CLASS_DEBUG) {
      System.out.println("Bot race: " + this.botRace.toString());
    }
  }

  public GameType getGameType() {
    return this.gameType;
  }

  /**
   * Sets the game type.
   */
  public void setGameType(GameType gameType) {
    this.gameType = gameType;
    if (CLASS_DEBUG) {
      System.out.println("Game type: " + this.gameType.toString());
    }
  }

}
