package droplauncher.bwheadless;

import adakite.utils.AdakiteUtils;
import adakite.utils.FileOperation;
import droplauncher.starcraft.Race;
import droplauncher.starcraft.Starcraft;
import droplauncher.util.Constants;
import java.io.File;
import java.util.logging.Logger;

/**
 * Class to handle all settings/arguments required to run a bot with a
 * bwheadless.exe process.
 */
public class Settings {

  private static final Logger LOGGER = Logger.getLogger(Settings.class.getName());
  private static final boolean CLASS_DEBUG = (Constants.DEBUG && true);

  public static final File BW_HEADLESS_EXE = new File("bwheadless.exe");
  public static final String DEFAULT_BOT_NAME = "BOT";

  private File starcraftExe; /* required */
  private File bwapiDll; /* required */
  private String botName; /* required */
  private File botDll; /* required only when client is absent */
  private File botClient; /* *.exe or *.jar, required only when DLL is absent  */
  private Race botRace; /* required */
  private GameType gameType; /* required */
  private JoinMode joinMode; /* required */

  public Settings() {
    this.starcraftExe = null;
    this.bwapiDll = null;
    this.botName = null;
    this.botDll = null;
    this.botClient = null;
    this.botRace = null;
    this.gameType = null;
    this.joinMode = null;
  }

  public ReadyStatus getReadyStatus() {
    if (this.starcraftExe == null
        || !(new FileOperation(this.starcraftExe).doesFileExist())) {
      return ReadyStatus.STARTCRAFT_EXE;
    } else if (this.bwapiDll == null
        || !(new FileOperation(this.bwapiDll).doesFileExist())) {
      return ReadyStatus.BWAPI_DLL;
    } else if (AdakiteUtils.isNullOrEmpty(botName, true)
        || botName.length() > Starcraft.MAX_PROFILE_NAME_LENGTH) {
      //TODO: Also check for invalid characters.
      return ReadyStatus.BOT_NAME;
    } else if (this.botDll == null
        || !(new FileOperation(this.botDll).doesFileExist())) {
      return ReadyStatus.BOT_DLL;
    } else if  (this.botClient == null
        || !(new FileOperation(this.botClient).doesFileExist())) {
      return ReadyStatus.BOT_CLIENT;
    } else if (this.botRace == null) {
      return ReadyStatus.BOT_RACE;
    } else if (this.gameType == null) {
      return ReadyStatus.GAME_TYPE;
    } else if (this.joinMode == null) {
      return ReadyStatus.JOIN_MODE;
    } else {
      return ReadyStatus.READY;
    }
  }

  public boolean isReady() {
    return (getReadyStatus() == ReadyStatus.READY);
  }

  public File getStarcraftExe() {
    return this.starcraftExe;
  }

  public boolean setStarcraftExe(File starcraftExe) {
    if (!(new FileOperation(starcraftExe)).doesFileExist()) {
      this.starcraftExe = null;
      return false;
    }
    this.starcraftExe = starcraftExe;
    return true;
  }

  public File getBwapiDll() {
    return this.bwapiDll;
  }

  public boolean setBwapiDll(File bwapiDll) {
    if (!(new FileOperation(bwapiDll)).doesFileExist()) {
      this.bwapiDll = null;
      return false;
    }
    this.bwapiDll = bwapiDll;
    return true;
  }

  public String getBotName() {
    return this.botName;
  }

  public boolean setBotName(String botName) {
    this.botName = Starcraft.cleanProfileName(botName);
    return true;
  }

  public File getBotDll() {
    return this.botDll;
  }

  public boolean setBotDll(File botDll) {
    if (!(new FileOperation(botDll)).doesFileExist()) {
      this.botClient = null;
      this.botDll = null;
      return false;
    }
    this.botClient = null;
    this.botDll = botDll;
    return true;
  }

  public File getBotClient() {
    return this.botClient;
  }

  public boolean setBotClient(File botClient) {
    if (!(new FileOperation(botClient)).doesFileExist()) {
      this.botClient = null;
      this.botDll = null;
      return false;
    }
    this.botDll = null;
    this.botClient = botClient;
    return true;
  }

  public Race getBotRace() {
    return this.botRace;
  }

  public boolean setBotRace(Race botRace) {
    if (botRace == null) {
      this.botRace = null;
      return false;
    }
    this.botRace = botRace;
    return true;
  }

  public GameType getGameType() {
    return this.gameType;
  }

  public boolean setGameType(GameType gameType) {
    if (gameType == null) {
      this.gameType = null;
      return false;
    }
    this.gameType = gameType;
    return true;
  }

  public JoinMode getJoinMode() {
    return this.joinMode;
  }

  public boolean setJoinMode(JoinMode joinMode) {
    if (joinMode == null) {
      this.joinMode = null;
      return false;
    }
    this.joinMode = joinMode;
    return true;
  }

}
