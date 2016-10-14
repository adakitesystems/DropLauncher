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

/**
 * Singleton class for handling communication with "bwheadless.exe" and
 * starting the bot client if present.
 *
 * @author Adakite Systems
 * @author adakitesystems@gmail.com
 */
public class BwHeadless {

  public static final BwHeadless INSTANCE = new BwHeadless();

  private static final Logger LOGGER = LogManager.getRootLogger();

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

  public static enum Checksum {
    Bwapi_Dll_374("6e940dc6acc76b6e459b39a9cdd466ae"),
    Bwapi_Dll_375("5e590ea55c2d3c66a36bf75537f8655a"),
    Bwapi_Dll_401b("84f413409387ae80a4b4acc51fed3923"),
    Bwapi_Dll_410b("4814396fba36916fdb7cf3803b39ab51"),
    Bwapi_Dll_411b("5d5128709ba714aa9c6095598bcf4624"),
    Bwapi_Dll_412("1364390d0aa085fba6ac11b7177797b0")
    ;

    private final String data;

    private Checksum(final String data) {
      this.data = data;
    }

    @Override
    public String toString() {
      return data;
    }
  }

  /*
   * Checksums are used for aesthetic purposes in possibly detecting
   * which BWAPI DLL is loaded. Tampering with the checksums or the DLLs
   * does not change the result of the program.
   */
  public static enum Argument {
    StarCraft_EXE("-e"), /* requires second string */
    Join("-j"),
    Bot_Name("-n"), /* requires second argument */
    Bot_Race("-r"), /* requires second argument */
    Load_DLL("-l"), /* requires second argument */
    Enable_LAN("--lan"),
    Enable_Local_PC("--localpc"),
    StarCraft_Install_Path("--installpath") /* requires second argument */
    ;

    private final String data;

    private Argument(final String data) {
      this.data = data;
    }

    @Override
    public String toString() {
      return data;
    }
  }

  public static enum ConfigVariable {
    StarCraft_EXE("starcraft_exe"),
    Bwapi_Dll("bwapi_dll"),
    Bot_Name("bot_name"),
    Bot_DLL("bot_dll"),
    Bot_Client("bot_client"),
    Bot_Race("bot_race"),
    Game_Type("game_type")
    ;

    private final String data;

    private ConfigVariable(final String data) {
      this.data = data;
    }

    @Override
    public String toString() {
      return data;
    }
  }

  public static final String BW_HEADLESS_PATH = "bwheadless.exe";
  public static final String BWAPI_DIR = "bwapi-data";

  public static final String DEFAULT_CFG_FILE =
      "settings" + ConfigFile.FILE_EXTENSION;

  public static final String BWAPI_DLL_FILE =
      "checksums" + ConfigFile.FILE_EXTENSION;

  public static final String DEFAULT_BOT_NAME = "BOT";
  /* Maximum profile name length in Broodwar 1.16.1 */
  public static final int MAX_BOT_NAME_LENGTH = 24;

  public ConfigFile bwapiDllChecksums;

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

    /* Create or check for checksums config file. */
    this.bwapiDllChecksums = new ConfigFile();
    if (MainTools.doesFileExist(BWAPI_DLL_FILE)) {
      if (bwapiDllChecksums.open(BWAPI_DLL_FILE)) {
        LOGGER.info("Read BWAPI DLL checksum config file: " + BWAPI_DLL_FILE);
      }
    } else {
      if (bwapiDllChecksums.create(BWAPI_DLL_FILE)) {
        LOGGER.info("Created BWAPI DLL checksum config file: " + BWAPI_DLL_FILE);
        boolean status =
            bwapiDllChecksums.createVariable(
                "BWAPI.dll 3.7.4",
                Checksum.Bwapi_Dll_374.toString())
            && bwapiDllChecksums.createVariable(
                "BWAPI.dll 3.7.5",
                Checksum.Bwapi_Dll_375.toString())
            && bwapiDllChecksums.createVariable(
                "BWAPI.dll 4.0.1b",
                Checksum.Bwapi_Dll_401b.toString())
            && bwapiDllChecksums.createVariable(
                "BWAPI.dll 4.1.0b",
                Checksum.Bwapi_Dll_410b.toString())
            && bwapiDllChecksums.createVariable(
                "BWAPI.dll 4.1.1b",
                Checksum.Bwapi_Dll_411b.toString())
            && bwapiDllChecksums.createVariable(
                "BWAPI.dll 4.1.2",
                Checksum.Bwapi_Dll_412.toString());
        if (!status) {
          LOGGER.warn("Unable to create all variables: " + BwHeadless.DEFAULT_CFG_FILE);
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
    args.add(Argument.StarCraft_EXE.toString());
    args.add(this.starcraftExe);
    /* Whether the bot should join or host. */
    args.add(Argument.Join.toString());
    /* Bot name */
    args.add(Argument.Bot_Name.toString());
    args.add(this.botName);
    /* Bot race */
    args.add(Argument.Bot_Race.toString());
    args.add(this.botRace.toString());
    /* BWAPI.dll */
    args.add(Argument.Load_DLL.toString());
    args.add(this.bwapiDll);
    /* Where the game should be played. E.g. over LAN or Local PC. */
    switch (this.gameType) {
      case lan:
        args.add(Argument.Enable_LAN.toString());
        break;
      case localpc:
        args.add(Argument.Enable_Local_PC.toString());
        break;
      default: break;
    }
    /* StarCraft install directory where "bwapi-data/" should be located. */
    args.add(Argument.StarCraft_Install_Path.toString());
    args.add(MainTools.getParentDirectory(this.starcraftExe));

    /* Start bwheadless.exe */
    boolean status = this.bwHeadlessPipe.open(BW_HEADLESS_PATH, MainTools.toStringArray(args));

    /* Start bot client if present. */
    if (this.botClientPath != null) {
      if (!this.botClientPipe.open(this.botClientPath, null)) {
        LOGGER.error("failed to start bot client");
        return false;
      }
    }

    if (status) {
      LOGGER.info("Launch: OK: " + BW_HEADLESS_PATH + " "
          + MainTools.arrayListToString(args));
    } else {
      LOGGER.error("Launch: FAIL " + BW_HEADLESS_PATH + " "
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
    if (this.botClientPath != null) {
      status = this.botClientPipe.close();
    }
    status &= this.bwHeadlessPipe.close();

    if (status) {
      LOGGER.info("Eject: OK");
    } else {
      LOGGER.error("Eject: FAIL ");
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

    LOGGER.info("Created default config: " + BwHeadless.DEFAULT_CFG_FILE);
    boolean status =
        cf.createVariable(ConfigVariable.StarCraft_EXE.toString(), "")
        && cf.createVariable(ConfigVariable.Bwapi_Dll.toString(), "")
        && cf.createVariable(ConfigVariable.Game_Type.toString(), GameType.lan.toString())
        && cf.createVariable(ConfigVariable.Bot_DLL.toString(), "")
        && cf.createVariable(ConfigVariable.Bot_Client.toString(), "")
        && cf.createVariable(ConfigVariable.Bot_Race.toString(), Race.Random.toString())
        && cf.createVariable(ConfigVariable.Bot_Name.toString(), BwHeadless.DEFAULT_BOT_NAME);
    if (!status) {
      LOGGER.warn("Unable to create all variables: " + BwHeadless.DEFAULT_CFG_FILE);
    }

    return true;
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
    /* Validate parameters. */
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
   * {@link #MAX_BOT_NAME_LENGTH}. Characters not matching A-Z, a-z, 0-9, or
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
    /* Validate parameters. */
    if (MainTools.isEmpty(path)) {
      LOGGER.warn(Debugging.EMPTY_STRING);
      return false;
    }
    if (!MainTools.doesFileExist(path)) {
      LOGGER.warn("file inaccessible or does not exist: " + path);
      return false;
    }

    this.botDllPath = path;

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
    /* Validate parameters. */
    if (MainTools.isEmpty(path)) {
      LOGGER.warn(Debugging.EMPTY_STRING);
      return false;
    }
    if (!MainTools.doesFileExist(path)) {
      LOGGER.warn("file inaccessible or does not exist: " + path);
      return false;
    }

    this.botClientPath = path;

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
