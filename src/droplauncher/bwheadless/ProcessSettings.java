/*
TODO: Possibly rename this class or move this functionality to BWHeadless.java.
*/

package droplauncher.bwheadless;

import adakite.debugging.Debugging;
import adakite.utils.AdakiteUtils;
import adakite.utils.FileOperation;
import droplauncher.ini.IniFile;
import droplauncher.starcraft.Race;
import droplauncher.starcraft.Starcraft;
import droplauncher.util.Constants;
import java.io.File;
import java.util.logging.Logger;

/**
 * Class to handle settings/arguments required to run a bot with a
 * bwheadless.exe process.
 */
public class ProcessSettings {

  private static final Logger LOGGER = Logger.getLogger(ProcessSettings.class.getName());
  private static final boolean CLASS_DEBUG = (Constants.DEBUG && true);

  public static final File BW_HEADLESS_EXE = new File("bwheadless.exe");
  public static final File DROP_LAUNCHER_INI = new File("droplauncher.ini");
  public static final String BW_HEADLESS_INI_SECTION = "bwheadless";
  public static final String DEFAULT_BOT_NAME = "BOT";

  private File starcraftExe; /* required */
  private File bwapiDll; /* required */
  private String botName; /* required */
  private File botDll; /* required only when client is absent */
  private File botClient; /* *.exe or *.jar, required only when DLL is absent  */
  private Race botRace; /* required */
  private GameType gameType; /* required */
  private JoinMode joinMode; /* required */

  private IniFile ini;

  public ProcessSettings() {
    this.starcraftExe = null;
    this.bwapiDll = null;
    this.botName = DEFAULT_BOT_NAME;
    this.botDll = null;
    this.botClient = null;
    this.botRace = Race.TERRAN;
    this.gameType = GameType.LAN;
    this.joinMode = JoinMode.JOIN;

    this.ini = new IniFile();
    if (ini.open(DROP_LAUNCHER_INI)) {
      readSettingsFile();
    } else {
      if (CLASS_DEBUG) {
        LOGGER.log(Constants.DEFAULT_LOG_LEVEL, Debugging.openFail(DROP_LAUNCHER_INI));
      }
    }
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
    } else if (
        (this.botDll == null || !(new FileOperation(this.botDll).doesFileExist()))
        && (this.botClient == null || !(new FileOperation(this.botClient).doesFileExist()))
    ) {
      /* If both the bot DLL and bot client fields are missing. */
      return ReadyStatus.BOT_FILE;
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
      updateSettingsFile(PredefinedVariable.STARCRAFT_EXE.toString(), "");
      return false;
    }
    this.starcraftExe = starcraftExe;
    updateSettingsFile(PredefinedVariable.STARCRAFT_EXE.toString(), this.starcraftExe.getAbsolutePath());
    return true;
  }

  public File getBwapiDll() {
    return this.bwapiDll;
  }

  public boolean setBwapiDll(File bwapiDll) {
    if (!(new FileOperation(bwapiDll)).doesFileExist()) {
      this.bwapiDll = null;
      updateSettingsFile(PredefinedVariable.BWAPI_DLL.toString(), "");
      return false;
    }
    this.bwapiDll = bwapiDll;
    updateSettingsFile(PredefinedVariable.BWAPI_DLL.toString(), this.bwapiDll.getAbsolutePath());
    return true;
  }

  public String getBotName() {
    return this.botName;
  }

  public boolean setBotName(String botName) {
    this.botName = Starcraft.cleanProfileName(botName);
    updateSettingsFile(PredefinedVariable.BOT_NAME.toString(), this.botName);
    return true;
  }

  public File getBotDll() {
    return this.botDll;
  }

  public boolean setBotDll(File botDll) {
    if (!(new FileOperation(botDll)).doesFileExist()) {
      this.botClient = null;
      this.botDll = null;
      updateSettingsFile(PredefinedVariable.BOT_CLIENT.toString(), "");
      updateSettingsFile(PredefinedVariable.BOT_DLL.toString(), "");
      return false;
    }
    this.botClient = null;
    this.botDll = botDll;
    updateSettingsFile(PredefinedVariable.BOT_CLIENT.toString(), "");
    updateSettingsFile(PredefinedVariable.BOT_DLL.toString(), this.botDll.getAbsolutePath());
    return true;
  }

  public File getBotClient() {
    return this.botClient;
  }

  public boolean setBotClient(File botClient) {
    if (!(new FileOperation(botClient)).doesFileExist()) {
      this.botClient = null;
      this.botDll = null;
      updateSettingsFile(PredefinedVariable.BOT_CLIENT.toString(), "");
      updateSettingsFile(PredefinedVariable.BOT_DLL.toString(), "");
      return false;
    }
    this.botDll = null;
    this.botClient = botClient;
    updateSettingsFile(PredefinedVariable.BOT_DLL.toString(), "");
    updateSettingsFile(PredefinedVariable.BOT_CLIENT.toString(), this.botClient.getAbsolutePath());
    return true;
  }

  public Race getBotRace() {
    return this.botRace;
  }

  public boolean setBotRace(Race botRace) {
    if (botRace == null) {
      this.botRace = null;
      updateSettingsFile(PredefinedVariable.BOT_RACE.toString(), "");
      return false;
    }
    this.botRace = botRace;
    updateSettingsFile(PredefinedVariable.BOT_RACE.toString(), this.botRace.toString());
    return true;
  }

  public GameType getGameType() {
    return this.gameType;
  }

  public boolean setGameType(GameType gameType) {
    if (gameType == null) {
      this.gameType = null;
      updateSettingsFile(PredefinedVariable.GAME_TYPE.toString(), "");
      return false;
    }
    this.gameType = gameType;
    updateSettingsFile(PredefinedVariable.GAME_TYPE.toString(), this.gameType.toString());
    return true;
  }

  public JoinMode getJoinMode() {
    return this.joinMode;
  }

  public boolean setJoinMode(JoinMode joinMode) {
    if (joinMode == null) {
      this.joinMode = null;
      updateSettingsFile(PredefinedVariable.JOIN_MODE.toString(), "");
      return false;
    }
    this.joinMode = joinMode;
    updateSettingsFile(PredefinedVariable.JOIN_MODE.toString(), this.joinMode.toString());
    return true;
  }

  public void updateSettingsFile(String key, String val) {
    this.ini.setVariable(BW_HEADLESS_INI_SECTION, key, val);
  }

  private void readSettingsFile() {
    if (this.ini == null) {
      return;
    }
    String val;
    if (!AdakiteUtils.isNullOrEmpty(val = this.ini.getValue(BW_HEADLESS_INI_SECTION, PredefinedVariable.STARCRAFT_EXE.toString()))) {
      setStarcraftExe(new File(val));
    }
    if (!AdakiteUtils.isNullOrEmpty(val = this.ini.getValue(BW_HEADLESS_INI_SECTION, PredefinedVariable.BWAPI_DLL.toString()))) {
      setBwapiDll(new File(val));
    }
    if (!AdakiteUtils.isNullOrEmpty(val = this.ini.getValue(BW_HEADLESS_INI_SECTION, PredefinedVariable.BOT_NAME.toString()))) {
      setBotName(val);
    } else {
      setBotName(DEFAULT_BOT_NAME);
    }
    if (!AdakiteUtils.isNullOrEmpty(val = this.ini.getValue(BW_HEADLESS_INI_SECTION, PredefinedVariable.BOT_DLL.toString()))) {
      setBotDll(new File(val));
    }
    if (!AdakiteUtils.isNullOrEmpty(val = this.ini.getValue(BW_HEADLESS_INI_SECTION, PredefinedVariable.BOT_CLIENT.toString()))) {
      setBotClient(new File(val));
    }
    if (!AdakiteUtils.isNullOrEmpty(val = this.ini.getValue(BW_HEADLESS_INI_SECTION, PredefinedVariable.BOT_RACE.toString()))) {
      if (val.equalsIgnoreCase(Race.TERRAN.toString())) {
        setBotRace(Race.TERRAN);
      } else if (val.equalsIgnoreCase(Race.ZERG.toString())) {
        setBotRace(Race.ZERG);
      } else if (val.equalsIgnoreCase(Race.PROTOSS.toString())) {
        setBotRace(Race.PROTOSS);
      } else {
        setBotRace(Race.TERRAN);
      }
    } else {
      setBotRace(Race.TERRAN);
    }
    if (!AdakiteUtils.isNullOrEmpty(val = this.ini.getValue(BW_HEADLESS_INI_SECTION, PredefinedVariable.GAME_TYPE.toString()))) {
      if (val.equalsIgnoreCase(GameType.LAN.toString())) {
        setGameType(GameType.LAN);
      }
    }
    if (!AdakiteUtils.isNullOrEmpty(val = this.ini.getValue(BW_HEADLESS_INI_SECTION, PredefinedVariable.JOIN_MODE.toString()))) {
      if (val.equalsIgnoreCase(JoinMode.JOIN.toString())) {
        setJoinMode(JoinMode.JOIN);
      }
      //TODO: Support other join modes.
    }
  }

}
