/* BwHeadless.java */

package droplauncher.bwheadless;

import droplauncher.MainWindow;
import droplauncher.config.ConfigFile;
import droplauncher.debugging.Debugging;
import droplauncher.tools.MainTools;
import droplauncher.tools.ProcessPipe;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Singleton class for handling communication with "bwheadless.exe" and
 * starting the bot client if present.
 *
 * @author Adakite Systems
 * @author adakitesystems@gmail.com
 */
public class BwHeadless {

  public static final BwHeadless INSTANCE = new BwHeadless();

  private static HashMap map = new HashMap();
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

  private static ProcessPipe bwHeadlessPipe; /* required */
  private static ProcessPipe botClientPipe;  /* required only when DLL is absent */
  private static String starcraftExe;        /* required */
  private static String bwapiDll;            /* required */
  private static String detectedBwapiDll;    /* not required */
  private static String botName;             /* required */
  private static String botDllPath;          /* required only when client is absent */
  private static String botClientPath;       /* required only when DLL is absent, *.exe or *.jar */
  private static Race botRace;               /* required */
  private static GameType gameType;          /* required */

  public static ArrayList<File> droppedFiles;

  private BwHeadless() {
    BwHeadless.bwHeadlessPipe   = new ProcessPipe();
    BwHeadless.botClientPipe    = new ProcessPipe();
    BwHeadless.starcraftExe     = null;
    BwHeadless.bwapiDll         = null;
    BwHeadless.detectedBwapiDll = null;
    BwHeadless.botName          = DEFAULT_BOT_NAME;
    BwHeadless.botDllPath       = null;
    BwHeadless.botClientPath    = null;
    BwHeadless.botRace          = Race.RANDOM;
    BwHeadless.gameType         = GameType.LAN;
    BwHeadless.droppedFiles     = new ArrayList<>();

    /* Create or check for settings config file. */
    /* ... */
    /* Code moved to MainWindow.java constructor so this class
       object gets initialized before displaying information
       to the MainWindow form. */
    /* ... */

    /* Create or check for checksums config file. */
    BwHeadless.bwapiDllChecksums = new ConfigFile();
    if (MainTools.doesFileExist(BWAPI_DLL_FILE)) {
      if (bwapiDllChecksums.open(BWAPI_DLL_FILE)) {
        logger.info("Read BWAPI DLL checksum config file: " + BWAPI_DLL_FILE);
      }
    } else {
      if (bwapiDllChecksums.create(BWAPI_DLL_FILE)) {
        logger.info("Created BWAPI DLL checksum config file: " + BWAPI_DLL_FILE);
        boolean status =
            bwapiDllChecksums.createVariable(
                "BWAPI.dll 3.7.4",
                Checksum.BWAPI_DLL_374.toString())
            && bwapiDllChecksums.createVariable(
                "BWAPI.dll 3.7.5",
                Checksum.BWAPI_DLL_375.toString())
            && bwapiDllChecksums.createVariable(
                "BWAPI.dll 4.0.1b",
                Checksum.BWAPI_DLL_401B.toString())
            && bwapiDllChecksums.createVariable(
                "BWAPI.dll 4.1.0b",
                Checksum.BWAPI_DLL_410B.toString())
            && bwapiDllChecksums.createVariable(
                "BWAPI.dll 4.1.1b",
                Checksum.BWAPI_DLL_411B.toString())
            && bwapiDllChecksums.createVariable(
                "BWAPI.dll 4.1.2",
                Checksum.BWAPI_DLL_412.toString());
        if (!status) {
          logger.warn("Unable to create all variables: " + BwHeadless.DEFAULT_CFG_FILE);
        }
      }
    }
  }

  public static BwHeadless getInstance() {
    return INSTANCE;
  }

  /**
   * Tests whether all required information has been processed and
   * program is ready to launch the bot.
   *
   * @return
   *     null if program is ready to launch the bot,
   *     otherwise a non-empty string
   */
  public static String getReadyError() {
    if (!MainTools.doesFileExist(BW_HEADLESS_PATH)) {
      return "missing bwheadless.exe";
    }
    if ((BwHeadless.botDllPath == null || !MainTools.doesFileExist(BwHeadless.botDllPath))
        && (BwHeadless.botClientPath == null || !MainTools.doesFileExist(BwHeadless.botClientPath))) {
      return "missing bot file";
    }
    if (BwHeadless.bwapiDll == null || !MainTools.doesFileExist(BwHeadless.bwapiDll)) {
      return "missing BWAPI.dll";
    }
    if (BwHeadless.starcraftExe == null || !MainTools.doesFileExist(BwHeadless.starcraftExe)) {
      return "missing StarCraft.exe";
    }
    return null;
  }

  /**
   * Launch the bot process.
   *
   * @return
   *     true if bot process appears to be running,
   *     otherwise false
   */
  public boolean launch() {
    if (!checkReady()) {
      return false;
    }

    /*
     * Prepare runtime arguments.
     */
    ArrayList<String> args = new ArrayList<>();
    args.add(Argument.STARCRAFT_EXE.toString());
    args.add(BwHeadless.starcraftExe);
    args.add(Argument.JOIN_GAME.toString());
    args.add(Argument.BOT_NAME.toString());
    args.add(BwHeadless.botName);
    args.add(Argument.BOT_RACE.toString());
    args.add(BwHeadless.botRace.toString());
    args.add(Argument.LOAD_DLL.toString());
    args.add(BwHeadless.bwapiDll);
    if (BwHeadless.gameType == GameType.LAN) {
      args.add(Argument.ENABLE_LAN.toString());
    } else if (BwHeadless.gameType == GameType.LOCAL_PC) {
      args.add(Argument.ENABLE_LOCAL_PC.toString());
    }
    /* StarCraft install directory where "bwapi-data/" should be located. */
    args.add(Argument.STARCRAFT_INSTALL_PATH.toString());
    args.add(MainTools.getParentDirectory(BwHeadless.starcraftExe));

    /* Start bwheadless.exe */
    boolean status = BwHeadless.bwHeadlessPipe.open(BW_HEADLESS_PATH, MainTools.toStringArray(args));

    /* Start bot client if present. */
    if (BwHeadless.botClientPath != null) {
      if (!BwHeadless.botClientPipe.open(BwHeadless.botClientPath, null)) {
        logger.error("failed to start bot client");
        return false;
      }
    }

    if (status) {
      logger.info("Launch: OK: " + BW_HEADLESS_PATH + " "
          + MainTools.arrayListToString(args));
    } else {
      logger.error("Launch: FAIL " + BW_HEADLESS_PATH + " "
          + MainTools.arrayListToString(args));
    }

    return status;
  }

  /**
   * Kills the bwheadless.exe and bot processes.
   *
   * @return
   *     true if processes were closed succesfully,
   *     otherwise false
   */
  public boolean eject() {
    boolean status = true;
    if (BwHeadless.botClientPath != null) {
      status = BwHeadless.botClientPipe.close();
    }
    status &= BwHeadless.bwHeadlessPipe.close();

    if (status) {
      logger.info("Eject: OK");
    } else {
      logger.error("Eject: FAIL ");
    }

    return status;
  }

  /**
   * Creates the default config file {@link #DEFAULT_CFG_FILE}.
   *
   * @return
   *     true if default config file was created,
   *     otherwise false
   */
  public boolean createDefaultConfig() {
    ConfigFile cf = new ConfigFile();

    if (!cf.create(BwHeadless.DEFAULT_CFG_FILE)) {
      return false;
    }

    logger.info("Created default config: " + BwHeadless.DEFAULT_CFG_FILE);
    boolean status =
        cf.createVariable(Variable.STARCRAFT_EXE.toString(), "")
        && cf.createVariable(Variable.BWAPI_DLL.toString(), "")
        && cf.createVariable(Variable.GAME_TYPE.toString(), GameType.LAN.toString())
        && cf.createVariable(Variable.BOT_DLL.toString(), "")
        && cf.createVariable(Variable.BOT_CLIENT.toString(), "")
        && cf.createVariable(Variable.BOT_RACE.toString(), Race.RANDOM.toString())
        && cf.createVariable(Variable.BOT_NAME.toString(), BwHeadless.DEFAULT_BOT_NAME);
    if (!status) {
      logger.warn("Unable to create all variables: " + BwHeadless.DEFAULT_CFG_FILE);
    }

    return true;
  }

  /**
   * Returns the path to the Starcraft executable.
   *
   * @return the path to the Starcraft executable
   */
  public String getStarcraftExe() {
    return BwHeadless.starcraftExe;
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

    BwHeadless.starcraftExe = path;

    logger.info("StarCraft.exe: " + BwHeadless.starcraftExe);

    checkReady();

    return true;
  }

  /**
   * Returns the path to the BWAPI DLL file.
   *
   * @return the path to the BWAPI DLL file
   */
  public String getBwapiDll() {
    return BwHeadless.bwapiDll;
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

    BwHeadless.bwapiDll = path;

    logger.info("BWAPI.dll: " + BwHeadless.bwapiDll);

    return true;
  }

  /**
   * Returns the name of this bot.
   *
   * @return the name of this bot
   */
  public String getBotName() {
    return BwHeadless.botName;
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

    BwHeadless.botName = str;

    logger.info("Bot name: " + BwHeadless.botName);
  }

  /**
   * Returns the path to the BWAPI DLL file.
   *
   * @return
   *     the path to the BWAPI DLL file
   */
  public String getBotDll() {
    return BwHeadless.botDllPath;
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

    BwHeadless.botDllPath = path;

    logger.info("Bot dll: " + BwHeadless.botDllPath);

    return true;
  }

  /**
   * Returns the path to the bot client file.
   *
   * @return
   *     the path to the bot client file.
   */
  public String getBotClient() {
    return BwHeadless.botClientPath;
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

    BwHeadless.botClientPath = path;

    logger.info("Bot client: " + BwHeadless.botClientPath);

    return true;
  }

  /**
   * Returns the race of the specified bot.
   *
   * @return
   *     the race of the specified bot
   */
  public Race getBotRace() {
    return BwHeadless.botRace;
  }

  /**
   * Sets the race of the bot.
   *
   * @param race specified bot race
   */
  public void setBotRace(Race race) {
    BwHeadless.botRace = race;
    logger.info("Bot race: " + BwHeadless.botRace.toString());
  }

  /**
   * Returns the game type.
   *
   * @return
   *     the game type
   */
  public GameType getGameType() {
    return BwHeadless.gameType;
  }

  /**
   * Sets the game type.
   *
   * @param gameType specified game type
   */
  public void setGameType(GameType gameType) {
    BwHeadless.gameType = gameType;
    logger.info("Game type: " + BwHeadless.gameType.toString());
  }

  /**
   * Processes the dropped files from the MainWindow component.
   *
   * @param file file or directory
   */
//  public void dropFile(File file) {
//    /* ************************************************************ */
//    /* This can probably be deleted if it really isn't required. Initially,
//       it was used in the case we keep previously dropped files in an array.
//       Now, we are just resetting the array after every drop. */
//    int countBefore = droppedFiles.size();
//    BwHeadless.droppedFiles.add(file);
//    int countAfter = droppedFiles.size();
//    /* ************************************************************ */
//
//    File tmpFile;
//    String tmpName;
//    String tmpFileMd5;
//    String bwapiDllVersion;
//
//    if (countAfter > countBefore) {
//      /* Loop through newly dropped files. */
//      for (int i = countBefore; i < countAfter; i++) {
//        tmpFile = BwHeadless.droppedFiles.get(i);
//        tmpName = tmpFile.getName();
//
//        if (CLASS_DEBUG) {
//          System.out.println("Dropped file: " + tmpName);
//        }
//
//        /* Test if the dropped file is a BWAPI.dll. */
//        if (tmpName.equalsIgnoreCase("BWAPI.dll")) {
//          try {
//            BwHeadless.bwapiDll = tmpFile.getCanonicalPath();
//          } catch (IOException ex) {
//            if (CLASS_DEBUG) {
//              logger.log(Level.SEVERE, null, ex);
//            }
//          }
//          tmpFileMd5 = MainTools.getMD5Checksum(tmpName);
//          bwapiDllVersion = BwHeadless.INSTANCE.bwapiDllChecksums.getName(tmpFileMd5);
//          /* DLL is detected as a known BWAPI.dll. */
//          if (bwapiDllVersion != null) {
//            /* TODO: update/do something to the UI that displays this value */
//            if (CLASS_DEBUG) {
//              System.out.println("Detected BWAPI DLL version: " + bwapiDllVersion);
//            }
//            BwHeadless.detectedBwapiDll = bwapiDllVersion;
//          }
//
//        /* Test if dropped file is a DLL file. */
//        } else if (tmpName.toLowerCase().endsWith(".dll")) {
//          try {
//            /* Disable the bot client file. */
//            BwHeadless.botClientPath = null;
//            /* Assume this is the bot DLL file. */
//            BwHeadless.botDllPath = tmpFile.getCanonicalPath();
//          } catch (IOException ex) {
//            if (CLASS_DEBUG) {
//              logger.log(Level.SEVERE, null, ex);
//            }
//          }
//
//        /* Test if dropped file is an executable. */
//        } else if (tmpName.toLowerCase().endsWith(".exe")) {
//          try {
//            /* Disable the bot DLL file. */
//            BwHeadless.botDllPath = null;
//            /* Assume this is the bot client file. */
//            BwHeadless.botClientPath = tmpFile.getCanonicalPath();
//          } catch (IOException ex) {
//            if (CLASS_DEBUG) {
//              logger.log(Level.SEVERE, null, ex);
//            }
//          }
//        }
//      }
//    }
//
//    checkReady();
//
//    /* Reset array after drop. */
//    BwHeadless.droppedFiles.clear();
//  }

  /**
   * Tests whether the program has the required information to
   * launch the bot and sets the UI components accordingly.
   *
   * @return
   *     true if program is ready to launch bot,
   *     otherwise false
   */
  public boolean checkReady() {
    if (BwHeadless.INSTANCE.getReadyError() == null) {
      MainWindow.mainWindow.setBoxDropFile(true);
      return true;
    } else {
      MainWindow.mainWindow.setBoxDropFile(false);
      return false;
    }
  }

}
