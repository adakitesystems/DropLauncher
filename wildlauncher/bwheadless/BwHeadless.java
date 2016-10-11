/* BwHeadless.java */

package wildlauncher.bwheadless;

import wildlauncher.tools.MainTools;
import wildlauncher.tools.ProcessPipe;

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

  public static final String DEFAULT_BOT_NAME = "BOT";
  public static final int MAX_NAME_LENGTH = 24;

  private ProcessPipe _bwHeadlessPipe;
  private ProcessPipe _botClientPipe;

  private String _starcraftExe;
  private String _botName;
  private String _botDllPath;
  private String _botClientPath; /* e.g. EXE or JAR bot client */

  private Race _botRace;
  private GameType _gameType;

  private BwHeadless() {
    _bwHeadlessPipe = new ProcessPipe();
    _botClientPipe = new ProcessPipe();
    _starcraftExe = null;
    _botName = null;
    _botRace = null;
    _botDllPath = null;
    _botClientPath = null;
    _gameType = GameType.lan;
  }

  public String getStarcraftExe() {
    return _starcraftExe;
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

    _starcraftExe = path;

    return true;
  }

  public String getBotName() {
    return _botName;
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

    _botName = str;

    if (CLASS_DEBUG) {
      System.out.println("Bot name: " + _botName);
    }
  }

  /**
   * Returns the path to the BWAPI DLL file.
   */
  public String getBotDll() {
    return _botDllPath;
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

    _botDllPath = path;

    return true;
  }

  /**
   * Returns the path to the bot client file.
   */
  public String getBotClient() {
    return _botClientPath;
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

    _botClientPath = path;

    return true;
  }

  public Race getBotRace() {
    return _botRace;
  }

  /**
   * Sets the race of the bot.
   *
   * @param race specified bot race
   */
  public void setBotRace(Race race) {
    _botRace = race;
    if (CLASS_DEBUG) {
      System.out.println("Bot race: " + _botRace.toString());
    }
  }

  public GameType getGameType() {
    return _gameType;
  }

  /**
   * Sets the game type.
   */
  public void setGameType(GameType gameType) {
    _gameType = gameType;
    if (CLASS_DEBUG) {
      System.out.println("Game type: " + _gameType.toString());
    }
  }

}
