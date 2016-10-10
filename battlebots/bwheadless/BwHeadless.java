/* BwHeadless.java */

package battlebots.bwheadless;

import battlebots.tools.MainTools;
import battlebots.tools.ProcessPipe;

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

  public static final BwHeadless INSTANCE = new BwHeadless();

  private static final Logger LOGGER = Logger.getLogger(BwHeadless.class.getName());

  public static final String ARG_STARCRAFT_EXE = "-e"; /* requires second string */
  public static final String ARG_JOIN = "-j";
  public static final String ARG_BOT_NAME = "-n"; /* requires second string */
  public static final String ARG_BOT_RACE = "-r"; /* requires second string */
  public static final String ARG_LOAD_DLL = "-l"; /* requires second string */
  public static final String ARG_ENABLE_LAN = "--lan";
  public static final String ARG_ENABLE_LOCAL_PC = "--localpc";
  public static final String ARG_STARCRAFT_INSTALL_PATH =
      "--installpath"; /* requires second string */

  public static final String RACE_TERRAN = "Terran";
  public static final String RACE_ZERG = "Zerg";
  public static final String RACE_PROTOSS = "Protoss";

  private ProcessPipe _bwHeadlessPipe;
  private ProcessPipe _botClientPipe;
  private String _starcraftExe;
  private String _botName;
  private String _botRace;
  private String _botDllPath;
  private String _botClientPath; /* e.g. EXE or JAR bot client */

  private BwHeadless() {
    _bwHeadlessPipe = new ProcessPipe();
    _botClientPipe = new ProcessPipe();
    _starcraftExe = null;
    _botName = null;
    _botRace = null;
    _botDllPath = null;
    _botClientPath = null;
  }

  public String getStarcraftExe() {
    return _starcraftExe;
  }

  /**
   * Set the path to the Starcraft executable if the specified path
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
      if (MainTools.DEBUG) {
        LOGGER.log(Level.WARNING, MainTools.EMPTY_STRING);
      }
      return false;
    }
    if (!MainTools.doesFileExist(path)) {
      if (MainTools.DEBUG) {
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

  public void setBotName(String str) {
    _botName = str;
  }

  public String getBotRace() {
    return _botRace;
  }

  /**
   * Set the bot race if the specified race is valid.
   *
   * @param race specified bot race
   */
  public void setBotRace(String race) {
    switch (race) {
      case "T":
      case "Terran":
        _botRace = RACE_TERRAN;
        break;
      case "Z":
      case "Zerg":
        _botRace = RACE_ZERG;
        break;
      case "P":
      case "Protoss":
        _botRace = RACE_PROTOSS;
        break;
      case "R":
      case "Random":
        /* When a bot joins a game, its race will already be Random.
           Setting this to null will omit the argument passed to bwheadless. */
        _botRace = null;
        break;
      default:
        if (MainTools.DEBUG) {
          LOGGER.log(Level.WARNING, "unknown race");
        }
        break;
    }
  }

  /**
   * Returns the path to the BWAPI DLL file.
   */
  public String getBwapiDll() {
    return _botDllPath;
  }

  /**
   * Set the bot dll to the specified path.
   *
   * @param path specified path
   * @return
   *     true if path appears to valid,
   *     otherwise false
   */
  public boolean setBwapiDll(String path) {
    /* Validate parameters. */
    if (MainTools.isEmpty(path)) {
      if (MainTools.DEBUG) {
        LOGGER.log(Level.WARNING, MainTools.EMPTY_STRING);
      }
      return false;
    }
    if (!MainTools.doesFileExist(path)) {
      if (MainTools.DEBUG) {
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
   * Set the bot client path to the specified path. Bot clients are usually
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
      if (MainTools.DEBUG) {
        LOGGER.log(Level.WARNING, MainTools.EMPTY_STRING);
      }
      return false;
    }
    if (!MainTools.doesFileExist(path)) {
      if (MainTools.DEBUG) {
        LOGGER.log(Level.WARNING, "file inaccessible or does not exist: " + path);
      }
      return false;
    }

    _botClientPath = path;

    return true;
  }

}
