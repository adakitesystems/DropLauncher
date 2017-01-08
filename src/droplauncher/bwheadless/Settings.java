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

  public ReadyStatus getStatus() {
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
    return (getStatus() == ReadyStatus.READY);
  }

  public File getStarcraftExe() {
    return this.starcraftExe;
  }

  public void setStarcraftExe(File starcraftExe) {
    this.starcraftExe = starcraftExe;
  }

  public File getBwapiDll() {
    return this.bwapiDll;
  }

  public void setBwapiDll(File bwapiDll) {
    this.bwapiDll = bwapiDll;
  }

  public String getBotName() {
    return this.botName;
  }

  public void setBotName(String botName) {
    this.botName = botName;
  }

  public File getBotDll() {
    return this.botDll;
  }

  public void setBotDll(File botDll) {
    this.botClient = null;
    this.botDll = botDll;
  }

  public File getBotClient() {
    return this.botClient;
  }

  public void setBotClient(File botClient) {
    this.botDll = null;
    this.botClient = botClient;
  }

  public Race getBotRace() {
    return this.botRace;
  }

  public void setBotRace(Race botRace) {
    this.botRace = botRace;
  }

  public GameType getGameType() {
    return this.gameType;
  }

  public void setGameType(GameType gameType) {
    this.gameType = gameType;
  }

  public JoinMode getJoinMode() {
    return this.joinMode;
  }

  public void setJoinMode(JoinMode joinMode) {
    this.joinMode = joinMode;
  }

}
