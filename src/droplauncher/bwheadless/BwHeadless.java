/* BwHeadless.java */

package droplauncher.bwheadless;

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

  public static final String BW_HEADLESS_PATH = "bwheadless.exe";

  public static final String DEFAULT_CFG_FILE =
      "settings" + ConfigFile.FILE_EXTENSION;

  public static final String DEFAULT_BOT_NAME = "BOT";

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
  }

  /**
   * Tests whether all required data is known.
   *
   * @return
   *     null if all required data is known,
   *     otherwise a string containing missing data
   */
  public String getReadyError() {
    if (MainTools.isEmpty(this.starcraftExe)) {
      return "missing StarCraft.exe path";
    } else if (MainTools.isEmpty(this.bwapiDll)) {
      return "missing BWAPI.dll";
    } else if (MainTools.isEmpty(this.botName)) {
      return "missing bot name";
    } else if (MainTools.isEmpty(this.botDllPath)
        && MainTools.isEmpty(this.botClientPath)) {
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
    return (getReadyError() == null);
  }

  /**
   * Returns the path to the Starcraft executable.
   *
   * @return the path to the Starcraft executable
   */
  public String getStarcraftExe() {
    return this.starcraftExe;
  }

  public boolean launch() {
    if (!isReady()) {
      LOGGER.warn("not ready to launch: " + getReadyError());
    }

    ArrayList<String> args = new ArrayList<>();

    args.add(Arguments.STARCRAFT_EXE.toString());
    args.add(this.starcraftExe);

    args.add(Arguments.JOIN_GAME.toString());

    args.add(Arguments.BOT_NAME.toString());
    args.add(this.botName);

    args.add(Arguments.BOT_RACE.toString());
    args.add(this.botRace.toString());

    args.add(Arguments.LOAD_DLL.toString());
    args.add(this.bwapiDll);

    args.add(Arguments.ENABLE_LAN.toString());

    args.add(Arguments.STARCRAFT_INSTALL_PATH.toString());
    args.add(MainTools.getParentDirectory(this.starcraftExe));

    String[] command = new String[args.size() + 1];
    command[0] = BW_HEADLESS_PATH;
    System.arraycopy(MainTools.toStringArray(args), 0, command, 1, args.size());



    return true;
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
      LOGGER.warn(Debugging.EMPTY_STRING);
      return false;
    }
    if (!MainTools.doesFileExist(path)) {
      LOGGER.warn("file inaccessible or does not exist: " + path);
      return false;
    }

    this.starcraftExe = path;

    LOGGER.info("StarCraft.exe: " + this.starcraftExe);

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
      LOGGER.warn("file inaccessible or does not exist" + path);
      return false;
    }

    this.bwapiDll = path;

    LOGGER.info("BWAPI.dll: " + this.bwapiDll);

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
      this.botDllPath = null;
      return true;
    }
    if (!MainTools.doesFileExist(path)) {
      LOGGER.warn("file inaccessible or does not exist: " + path);
      return false;
    }

    this.botDllPath = path;
    this.botClientPath = null;

    LOGGER.info("Bot dll: " + this.botDllPath);

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
      this.botClientPath = null;
      return true;
    }
    if (!MainTools.doesFileExist(path)) {
      LOGGER.warn("file inaccessible or does not exist: " + path);
      return false;
    }

    this.botClientPath = path;
    this.botDllPath = null;

    LOGGER.info("Bot client: " + this.botClientPath);

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
        setBwapiDll(MainTools.getFullPath(tmpFile));
      } else if (tmpNameLower.endsWith(".dll")) {
        setBotDll(MainTools.getFullPath(tmpFile));
      } else if (tmpNameLower.endsWith(".exe")) {
        setBotClient(MainTools.getFullPath(tmpFile));
      }
    }
  }

}
