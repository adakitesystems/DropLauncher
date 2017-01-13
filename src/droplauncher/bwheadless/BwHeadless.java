/*
> bwheadless.exe --help
Usage: C:\projects\DropLauncher\DropLauncher\bwheadless.exe [option]...
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

import droplauncher.config.Settings;
import droplauncher.util.Constants;
import droplauncher.util.ProcessPipe;
import java.io.File;
import java.util.logging.Logger;

/**
 * Class for handling execution and communication with the
 * bwheadless.exe process.
 */
public class BWHeadless {

  private static final Logger LOGGER = Logger.getLogger(BWHeadless.class.getName());
  private static final boolean CLASS_DEBUG = (Constants.DEBUG && true);

  private ProcessPipe pipe;
  private ProcessSettings settings;
  private Settings configFile;

  public BWHeadless() {
    this.pipe = new ProcessPipe();
    this.settings = new ProcessSettings();
    this.configFile = new Settings();

    this.configFile.setFile(new File("config_file.cfg"));
    this.settings.setConfigFile(configFile);
    this.configFile.readVariablesFromFile(new File("config_file.cfg"));
  }

  public ProcessSettings getSettings() {
    return this.settings;
  }

  public void setSettings(ProcessSettings settings) {
    this.settings = settings;
  }

  public Settings getConfigFile() {
    return this.configFile;
  }

  public void setConfigFile(Settings configFile) {
    this.configFile = configFile;
  }

  public ReadyStatus getReadyStatus() {
    return this.settings.getReadyStatus();
  }

  public boolean isRunning() {
    return this.pipe.isOpen();
  }

  public boolean isReady() {
    return this.settings.isReady();
  }

  public boolean start() {
    if (isReady()) {
      System.out.println("BWH: Ready");
    } else {
      System.out.println("BWH: Not Ready");
    }
    return false;
  }

  public void stop() {
    //TODO
    System.out.println("BWH: Stop");
  }

}
