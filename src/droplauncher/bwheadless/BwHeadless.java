/* BwHeadless.java */

package droplauncher.bwheadless;

import droplauncher.MainWindow;
import droplauncher.config.ConfigFile;
import droplauncher.tools.MainTools;
import droplauncher.tools.ProcessPipe;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
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

  public static final String BW_HEADLESS_PATH = "bwheadless.exe";
  public static final String ARG_STARCRAFT_EXE = "-e"; /* requires second string */
  public static final String ARG_JOIN = "-j";
  public static final String ARG_BOT_NAME = "-n"; /* requires second argument */
  public static final String ARG_BOT_RACE = "-r"; /* requires second argument */
  public static final String ARG_LOAD_DLL = "-l"; /* requires second argument */
  public static final String ARG_ENABLE_LAN = "--lan";
  public static final String ARG_ENABLE_LOCAL_PC = "--localpc";
  public static final String ARG_STARCRAFT_INSTALL_PATH =
      "--installpath"; /* requires second argument */

  public static final String DEFAULT_CFG_FILE =
      "settings" + ConfigFile.FILE_EXTENSION;
  public static final String CFG_STARCRAFT_EXE = "starcraft_exe";
  public static final String CFG_BWAPI_DLL = "bwapi_dll";
  public static final String CFG_BOT_NAME = "bot_name";
  public static final String CFG_BOT_DLL = "bot_dll";
  public static final String CFG_BOT_CLIENT = "bot_client";
  public static final String CFG_BOT_RACE = "bot_race";
  public static final String CFG_GAME_TYPE = "game_type";

  /* Checksums are used just for aesthetic purposes in possibly detecting
     which BWAPI DLL is loaded. Tampering with the checksums or the DLLs
     does not change the result of the program. */
  public static final String BWAPI_DLL_FILE = "checksums" + ConfigFile.FILE_EXTENSION;
  public static final String BWAPI_DLL_374_SUM  = "6e940dc6acc76b6e459b39a9cdd466ae";
  public static final String BWAPI_DLL_375_SUM  = "5e590ea55c2d3c66a36bf75537f8655a";
  public static final String BWAPI_DLL_401b_SUM = "84f413409387ae80a4b4acc51fed3923";
  public static final String BWAPI_DLL_410b_SUM = "4814396fba36916fdb7cf3803b39ab51";
  public static final String BWAPI_DLL_411b_SUM = "5d5128709ba714aa9c6095598bcf4624";
  public static final String BWAPI_DLL_412_SUM  = "1364390d0aa085fba6ac11b7177797b0";
  public ConfigFile bwapiDllChecksums;

  public static final String BWAPI_DIR = "bwapi-data";

  public static final String DEFAULT_BOT_NAME = "BOT";
  /* Maximum profile name length in Broodwar 1.16.1 */
  public static final int MAX_BOT_NAME_LENGTH = 24;

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

  public ArrayList<File> droppedFiles;

  private BwHeadless() {
    this.bwHeadlessPipe   = new ProcessPipe();
    this.botClientPipe    = new ProcessPipe();
    this.starcraftExe     = null;
    this.bwapiDll         = null;
    this.detectedBwapiDll = null;
    this.botName          = DEFAULT_BOT_NAME;
    this.botDllPath       = null;
    this.botClientPath    = null;
    this.botRace          = Race.Random;
    this.gameType         = GameType.lan;
    this.droppedFiles     = new ArrayList<>();

    /* Create or check for settings config file. */
    /* ... */
    /* Code moved to MainWindow.java constructor so this class
       object gets initialized before displaying information
       to the MainWindow form. */
    /* ... */

    /* Create or check for checksum config file. */
    this.bwapiDllChecksums = new ConfigFile();
    if (MainTools.doesFileExist(BWAPI_DLL_FILE)) {
      if (bwapiDllChecksums.open(BWAPI_DLL_FILE)) {
        if (CLASS_DEBUG) {
          System.out.println("Read BWAPI DLL checksum config file: " + BWAPI_DLL_FILE);
        }
      }
    } else {
      if (bwapiDllChecksums.create(BWAPI_DLL_FILE)) {
        boolean status;
        status = bwapiDllChecksums.createVariable("BWAPI.dll 3.7.4", BWAPI_DLL_374_SUM);
        status = bwapiDllChecksums.createVariable("BWAPI.dll 3.7.5", BWAPI_DLL_375_SUM);
        status = bwapiDllChecksums.createVariable("BWAPI.dll 4.0.1b", BWAPI_DLL_401b_SUM);
        status = bwapiDllChecksums.createVariable("BWAPI.dll 4.1.0b", BWAPI_DLL_410b_SUM);
        status = bwapiDllChecksums.createVariable("BWAPI.dll 4.1.1b", BWAPI_DLL_411b_SUM);
        status = bwapiDllChecksums.createVariable("BWAPI.dll 4.1.2", BWAPI_DLL_412_SUM);
        if (CLASS_DEBUG) {
          System.out.println("Created BWAPI DLL checksum config file: " + BWAPI_DLL_FILE);
        }
      }
    }
  }

  /**
   * Tests whether all required information has been processed and
   * program is ready to launch the bot.
   *
   * @return
   *     null if program is ready to launch the bot,
   *     otherwise a non-empty string
   */
  public String getReadyError() {
    /* Missing bwheadless.exe */
    if (!MainTools.doesFileExist(BW_HEADLESS_PATH)) {
      return "missing bwheadless.exe";
    }
    /* Missing bot files. */
    if ((this.botDllPath == null || !MainTools.doesFileExist(this.botDllPath))
        && (this.botClientPath == null || !MainTools.doesFileExist(this.botClientPath))) {
      return "missing bot file";
    }
    /* Missing BWAPI DLL file. */
    if (this.bwapiDll == null || !MainTools.doesFileExist(this.bwapiDll)) {
      return "missing BWAPI.dll";
    }
    /* Missing StarCraft.exe */
    if (this.starcraftExe == null || !MainTools.doesFileExist(this.starcraftExe)) {
      return "missing StarCraft.exe";
    }
    /* Ready to launch bot. */
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
    /* StarCraft.exe */
    args.add(ARG_STARCRAFT_EXE);
    args.add(this.starcraftExe);
    /* Whether the bot should join or host. */
    args.add(ARG_JOIN);
    /* Bot name */
    args.add(ARG_BOT_NAME);
    args.add(this.botName);
    /* Bot race */
    args.add(ARG_BOT_RACE);
    args.add(this.botRace.toString());
    /* BWAPI.dll */
    args.add(ARG_LOAD_DLL);
    args.add(this.bwapiDll);
    /* Where the game should be played. E.g. over LAN or Local PC. */
    switch (this.gameType) {
      case lan:
        args.add(ARG_ENABLE_LAN);
        break;
      case localpc:
        args.add(ARG_ENABLE_LOCAL_PC);
        break;
      default: break;
    }
    /* StarCraft install directory where "bwapi-data/" should be located. */
    args.add(ARG_STARCRAFT_INSTALL_PATH);
    args.add(MainTools.getParentDirectory(this.starcraftExe));

    /* Start bwheadless.exe */
    boolean status = this.bwHeadlessPipe.open(BW_HEADLESS_PATH, MainTools.toStringArray(args));

    /* Start bot client if present. */
    if (this.botClientPath != null) {
      if (!this.botClientPipe.open(this.botClientPath, null)) {
        if (CLASS_DEBUG) {
          LOGGER.log(Level.SEVERE, "failed to start bot client");
        }
        return false;
      }
    }

    if (status) {
      if (CLASS_DEBUG) {
        System.out.println("Launch!");
        System.out.println(BW_HEADLESS_PATH + " " + args.toString());
      }
    }

    return status;
  }

  public boolean eject() {
    boolean status = true;
    if (this.botClientPath != null) {
      status = this.botClientPipe.close();
    }
    status &= this.bwHeadlessPipe.close();
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

    cf.createVariable(CFG_STARCRAFT_EXE, "");
    cf.createVariable(CFG_BWAPI_DLL, "");
    cf.createVariable(CFG_GAME_TYPE, "lan");
    cf.createVariable(CFG_BOT_DLL, "");
    cf.createVariable(CFG_BOT_CLIENT, "");
    cf.createVariable(CFG_BOT_RACE, "Random");
    cf.createVariable(CFG_BOT_NAME, BwHeadless.DEFAULT_BOT_NAME);

    if (CLASS_DEBUG) {
      System.out.println("Created default config: " + BwHeadless.DEFAULT_CFG_FILE);
    }

    return true;
  }

  /**
   * Returns the path to the Starcraft executable.
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

    this.starcraftExe = path;

    if (CLASS_DEBUG) {
      System.out.println("StarCraft.exe: " + this.starcraftExe);
    }

    checkReady();

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
      if (CLASS_DEBUG) {
        LOGGER.log(Level.WARNING, "file inaccessible or does not exist" + path);
      }
      return false;
    }

    this.bwapiDll = path;

    if (CLASS_DEBUG) {
      System.out.println("BWAPI.dll: " + this.bwapiDll);
    }

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
    if (str.length() > MAX_BOT_NAME_LENGTH) {
      str = str.substring(0, MAX_BOT_NAME_LENGTH);
    }

    this.botName = str;

    if (CLASS_DEBUG) {
      System.out.println("Bot name: " + this.botName);
    }
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

    this.botDllPath = path;

    if (CLASS_DEBUG) {
      System.out.println("Bot dll: " + this.botDllPath);
    }

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

    if (CLASS_DEBUG) {
      System.out.println("Bot client: " + this.botClientPath);
    }

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
    if (CLASS_DEBUG) {
      System.out.println("Bot race: " + this.botRace.toString());
    }
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
    if (CLASS_DEBUG) {
      System.out.println("Game type: " + this.gameType.toString());
    }
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
//    this.droppedFiles.add(file);
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
//        tmpFile = this.droppedFiles.get(i);
//        tmpName = tmpFile.getName();
//
//        if (CLASS_DEBUG) {
//          System.out.println("Dropped file: " + tmpName);
//        }
//
//        /* Test if the dropped file is a BWAPI.dll. */
//        if (tmpName.equalsIgnoreCase("BWAPI.dll")) {
//          try {
//            this.bwapiDll = tmpFile.getCanonicalPath();
//          } catch (IOException ex) {
//            if (CLASS_DEBUG) {
//              LOGGER.log(Level.SEVERE, null, ex);
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
//            this.detectedBwapiDll = bwapiDllVersion;
//          }
//
//        /* Test if dropped file is a DLL file. */
//        } else if (tmpName.toLowerCase().endsWith(".dll")) {
//          try {
//            /* Disable the bot client file. */
//            this.botClientPath = null;
//            /* Assume this is the bot DLL file. */
//            this.botDllPath = tmpFile.getCanonicalPath();
//          } catch (IOException ex) {
//            if (CLASS_DEBUG) {
//              LOGGER.log(Level.SEVERE, null, ex);
//            }
//          }
//
//        /* Test if dropped file is an executable. */
//        } else if (tmpName.toLowerCase().endsWith(".exe")) {
//          try {
//            /* Disable the bot DLL file. */
//            this.botDllPath = null;
//            /* Assume this is the bot client file. */
//            this.botClientPath = tmpFile.getCanonicalPath();
//          } catch (IOException ex) {
//            if (CLASS_DEBUG) {
//              LOGGER.log(Level.SEVERE, null, ex);
//            }
//          }
//        }
//      }
//    }
//
//    checkReady();
//
//    /* Reset array after drop. */
//    this.droppedFiles.clear();
//  }

  /**
   * Tests whether the program has the required information to
   * launch the bot and sets the UI components accordingly.
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
