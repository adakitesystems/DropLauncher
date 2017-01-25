/*
> bwheadless.exe --help
Usage: bwheadless.exe [option]...
A tool to start StarCraft: Brood War as a console application, with no graphics, sound or user input.

  -e, --exe         The exe file to launch. Default 'StarCraft.exe'.
  -h, --host        Host a game instead of joining.
  -j, --join        Join instead of hosting. The first game that is found
                    will be joined.
  -n, --name NAME   The player name. Default 'playername'.
  -g, --game NAME   The game name when hosting. Defaults to the player name.
                    If this option is specified when joining, then only games
                    with the specified name will be joined.
  -m, --map FILE    The map to use when hosting.
  -r, --race RACE   Zerg/Terran/Protoss/Random/Z/T/P/R (case insensitive).
  -l, --dll DLL     Load DLL into StarCraft. This option can be
                    specified multiple times to load multiple dlls.
      --networkprovider NAME  Use the specified network provider.
                              'UDPN' is LAN (UDP), 'SMEM' is Local PC (provided
                              by BWAPI). Others are provided by .snp files and
                              may or may not work. Default SMEM.
      --lan         Sets the network provider to LAN (UDP).
      --localpc     Sets the network provider to Local PC (this is default).
      --lan-sendto IP  Overrides the IP that UDP packets are sent to. This
                       can be used together with --lan to connect to a
                       specified IP-address instead of broadcasting for games
                       on LAN (The ports used is 6111 and 6112).
      --installpath PATH  Overrides the InstallPath value that would usually
                          be read from the registry. This is used by BWAPI to
                          locate bwapi-data/bwapi.ini.
*/

package droplauncher.bwheadless;

import adakite.utils.AdakiteUtils;
import adakite.utils.FileOperation;
import droplauncher.bwapi.BWAPI;
import droplauncher.ini.IniFile;
import droplauncher.starcraft.Race;
import droplauncher.starcraft.Starcraft;
import droplauncher.util.Constants;
import droplauncher.util.ProcessPipe;
import droplauncher.util.Util;
import java.io.File;
import java.io.IOException;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class for handling execution and communication with the
 * bwheadless.exe process.
 */
public class BWHeadless {

  private static final Logger LOGGER = Logger.getLogger(BWHeadless.class.getName());
  private static final boolean CLASS_DEBUG = (Constants.DEBUG && true);

//  public static final File BW_HEADLESS_EXE = new File("bwheadless.exe");
  public static final String BW_HEADLESS_INI_SECTION = "bwheadless";

  public static final String DEFAULT_BOT_NAME = "BOT";
  public static final Race DEFAULT_BOT_RACE = Race.TERRAN;
  public static final NetworkProvider DEFAULT_NETWORK_PROVIDER = NetworkProvider.LAN;
  public static final JoinMode DEFAULT_JOIN_MODE = JoinMode.JOIN;

  private ProcessPipe pipe;

  private File starcraftExe; /* required */
  private File bwapiDll; /* required */
  private String botName; /* required */
  private File botDll; /* required only when client is absent */
  private File botClient; /* *.exe or *.jar, required only when DLL is absent  */
  private Race botRace; /* required */
  private NetworkProvider networkProvider; /* required */
  private JoinMode joinMode; /* required */

  private IniFile iniFile;

  public BWHeadless() {
    this.pipe = new ProcessPipe();

    this.starcraftExe = null;
    this.bwapiDll = null;
    this.botName = DEFAULT_BOT_NAME;
    this.botDll = null;
    this.botClient = null;
    this.botRace = DEFAULT_BOT_RACE;
    this.networkProvider = DEFAULT_NETWORK_PROVIDER;
    this.joinMode = DEFAULT_JOIN_MODE;

    this.iniFile = null;
  }

  public boolean isReady() {
    return (getReadyStatus() == ReadyStatus.READY);
  }

  public ReadyStatus getReadyStatus() {
    if (this.starcraftExe == null
        || !(new FileOperation(this.starcraftExe).doesFileExist())) {
      return ReadyStatus.STARTCRAFT_EXE;
    } else if (this.bwapiDll == null
        || !(new FileOperation(this.bwapiDll).doesFileExist())) {
      return ReadyStatus.BWAPI_DLL;
    } else if (AdakiteUtils.isNullOrEmpty(this.botName, true)
        || this.botName.length() > Starcraft.MAX_PROFILE_NAME_LENGTH) {
      return ReadyStatus.BOT_NAME;
    } else if (
        (this.botDll == null || !(new FileOperation(this.botDll).doesFileExist()))
        && (this.botClient == null || !(new FileOperation(this.botClient).doesFileExist()))
    ) {
      /* If both the bot DLL and bot client fields are missing. */
      return ReadyStatus.BOT_FILE;
    } else if (this.botRace == null) {
      return ReadyStatus.BOT_RACE;
    } else if (this.networkProvider == null) {
      return ReadyStatus.NETWORK_PROVIDER;
    } else if (this.joinMode == null) {
      return ReadyStatus.JOIN_MODE;
    } else {
      return ReadyStatus.READY;
    }
  }

  public boolean start() {
    if (isReady()) {
      //DEBUG ---
      if (this.pipe.isOpen()) {
        stop();
        return true;
      }
      //---
      System.out.println("BWH: Ready");
      //DEBUG ---
      String starcraftDir = new FileOperation(starcraftExe).getParentDirectory().getAbsolutePath();
      IniFile bwapiIni = new IniFile();
      bwapiIni.open(new File(starcraftDir + File.separator + BWAPI.BWAPI_DATA_INI));
      bwapiIni.setVariable("ai", "ai", "bwapi-data/AI/" + getBotDll().getName());
      try {
        Files.copy(getBotDll().toPath(), new File(starcraftDir + File.separator + BWAPI.BWAPI_DATA_AI_DIR + File.separator + getBotDll().getName()).toPath(), StandardCopyOption.REPLACE_EXISTING);
      } catch (IOException ex) {
        Logger.getLogger(BWHeadless.class.getName()).log(Level.SEVERE, null, ex);
      }
      //---
//      ArrayList<String> args = new ArrayList<>();
//      args.add(Argument.STARCRAFT_EXE.toString());
//      args.add(getStarcraftExe().getAbsolutePath());
//      args.add(Argument.JOIN_GAME.toString());
//      args.add(Argument.BOT_NAME.toString());
//      args.add(getBotName());
//      args.add(Argument.BOT_RACE.toString());
//      args.add(getBotRace().toString());
//      args.add(Argument.LOAD_DLL.toString());
//      args.add(getBwapiDll().getAbsolutePath());
//      args.add(Argument.ENABLE_LAN.toString());
//      args.add(Argument.STARCRAFT_INSTALL_PATH.toString());
//      args.add("C:\\StarCraft");
//      String[] cmdArgs = Util.toStringArray(args);
//      this.pipe.open(new File("bwheadless.exe"), cmdArgs);
      return true;
    } else {
      System.out.println("BWH: Not Ready");
      return false;
    }
  }

  public void stop() {
    System.out.println("BWH: Stop");
    this.pipe.close();
  }

  public IniFile getIniFile() {
    return this.iniFile;
  }

  public void setIniFile(IniFile iniFile) {
    this.iniFile = iniFile;
  }

  public File getStarcraftExe() {
    return this.starcraftExe;
  }

  public void setStarcraftExe(File starcraftExe) {
    if (!(new FileOperation(starcraftExe)).doesFileExist()) {
      this.starcraftExe = null;
      updateSettingsFile(PredefinedVariable.STARCRAFT_EXE.toString(), "");
    } else {
      this.starcraftExe = starcraftExe;
      updateSettingsFile(PredefinedVariable.STARCRAFT_EXE.toString(), this.starcraftExe.getAbsolutePath());
    }
  }

  public File getBwapiDll() {
    return this.bwapiDll;
  }

  public void setBwapiDll(File bwapiDll) {
    if (!(new FileOperation(bwapiDll)).doesFileExist()) {
      this.bwapiDll = null;
      updateSettingsFile(PredefinedVariable.BWAPI_DLL.toString(), "");
    } else {
      this.bwapiDll = bwapiDll;
      updateSettingsFile(PredefinedVariable.BWAPI_DLL.toString(), this.bwapiDll.getAbsolutePath());
    }
  }

  public String getBotName() {
    return this.botName;
  }

  public void setBotName(String botName) {
    String cleaned = Starcraft.cleanProfileName(botName);
    this.botName = cleaned;
    if (AdakiteUtils.isNullOrEmpty(this.botName)) {
      this.botName = DEFAULT_BOT_NAME;
    }
    updateSettingsFile(PredefinedVariable.BOT_NAME.toString(), this.botName);
  }

  public File getBotDll() {
    return this.botDll;
  }

  public void setBotDll(File botDll) {
    this.botClient = null;
    updateSettingsFile(PredefinedVariable.BOT_CLIENT.toString(), "");

    if (!(new FileOperation(botDll)).doesFileExist()) {
      this.botDll = null;
      updateSettingsFile(PredefinedVariable.BOT_DLL.toString(), "");
    } else {
      this.botDll = botDll;
      updateSettingsFile(PredefinedVariable.BOT_DLL.toString(), this.botDll.getAbsolutePath());
    }
  }

  public File getBotClient() {
    return this.botClient;
  }

  public void setBotClient(File botClient) {
    this.botDll = null;
    updateSettingsFile(PredefinedVariable.BOT_DLL.toString(), "");

    if (!(new FileOperation(botClient)).doesFileExist()) {
      this.botClient = null;
      updateSettingsFile(PredefinedVariable.BOT_CLIENT.toString(), "");
    } else {
      this.botClient = botClient;
      updateSettingsFile(PredefinedVariable.BOT_CLIENT.toString(), this.botClient.getAbsolutePath());
    }
  }

  public Race getBotRace() {
    return this.botRace;
  }

  public void setBotRace(Race botRace) {
    if (botRace == null) {
      this.botRace = DEFAULT_BOT_RACE;
    } else {
      this.botRace = botRace;
    }
    updateSettingsFile(PredefinedVariable.BOT_RACE.toString(), this.botRace.toString());
  }

  public NetworkProvider getNetworkProvider() {
    return this.networkProvider;
  }

  public void setNetworkProvider(NetworkProvider networkProvider) {
    if (networkProvider == null) {
      this.networkProvider = DEFAULT_NETWORK_PROVIDER;
    } else {
      this.networkProvider = networkProvider;
    }
    updateSettingsFile(PredefinedVariable.NETWORK_PROVIDER.toString(), this.networkProvider.toString());
  }

  public JoinMode getJoinMode() {
    return this.joinMode;
  }

  public void setJoinMode(JoinMode joinMode) {
    if (joinMode == null) {
      this.joinMode = DEFAULT_JOIN_MODE;
      updateSettingsFile(PredefinedVariable.JOIN_MODE.toString(), this.joinMode.toString());
    } else {
      this.joinMode = joinMode;
      updateSettingsFile(PredefinedVariable.JOIN_MODE.toString(), this.joinMode.toString());
    }
  }

  private void updateSettingsFile(String name, String key, String val) {
    this.iniFile.setVariable(name, key, val);
  }

  private void updateSettingsFile(String key, String val) {
    updateSettingsFile(BW_HEADLESS_INI_SECTION, key, val);
  }

  public void readSettingsFile(IniFile ini) {
    if (ini == null) {
      return;
    }
    String val;
    if (!AdakiteUtils.isNullOrEmpty(val = ini.getValue(BW_HEADLESS_INI_SECTION, PredefinedVariable.STARCRAFT_EXE.toString()))) {
      setStarcraftExe(new File(val));
    }
    if (!AdakiteUtils.isNullOrEmpty(val = ini.getValue(BW_HEADLESS_INI_SECTION, PredefinedVariable.BWAPI_DLL.toString()))) {
      setBwapiDll(new File(val));
    }
    if (!AdakiteUtils.isNullOrEmpty(val = ini.getValue(BW_HEADLESS_INI_SECTION, PredefinedVariable.BOT_NAME.toString()))) {
      setBotName(val);
    } else {
      setBotName(DEFAULT_BOT_NAME);
    }
    if (!AdakiteUtils.isNullOrEmpty(val = ini.getValue(BW_HEADLESS_INI_SECTION, PredefinedVariable.BOT_DLL.toString()))) {
      setBotDll(new File(val));
    }
    if (!AdakiteUtils.isNullOrEmpty(val = ini.getValue(BW_HEADLESS_INI_SECTION, PredefinedVariable.BOT_CLIENT.toString()))) {
      setBotClient(new File(val));
    }
    if (!AdakiteUtils.isNullOrEmpty(val = ini.getValue(BW_HEADLESS_INI_SECTION, PredefinedVariable.BOT_RACE.toString()))) {
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
      setBotRace(DEFAULT_BOT_RACE);
    }
    if (!AdakiteUtils.isNullOrEmpty(val = ini.getValue(BW_HEADLESS_INI_SECTION, PredefinedVariable.NETWORK_PROVIDER.toString()))) {
      if (val.equalsIgnoreCase(NetworkProvider.LAN.toString())) {
        setNetworkProvider(NetworkProvider.LAN);
      } else {
        setNetworkProvider(DEFAULT_NETWORK_PROVIDER);
      }
    } else {
//      setNetworkProvider(DEFAULT_NETWORK_PROVIDER);
    }
    if (!AdakiteUtils.isNullOrEmpty(val = ini.getValue(BW_HEADLESS_INI_SECTION, PredefinedVariable.JOIN_MODE.toString()))) {
      if (val.equalsIgnoreCase(JoinMode.JOIN.toString())) {
        setJoinMode(JoinMode.JOIN);
      } else {
        setJoinMode(DEFAULT_JOIN_MODE);
      }
    } else {
//      setJoinMode(DEFAULT_JOIN_MODE);
    }
  }

}
