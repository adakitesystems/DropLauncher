package droplauncher.bwheadless;

import droplauncher.config.ConfigFile;
import droplauncher.config.ConfigVariable;
import droplauncher.starcraft.Race;
import droplauncher.util.Constants;
import droplauncher.util.ProcessPipe;
import java.io.File;
import java.util.logging.Logger;

/**
 * Class for handling execution and communication with the
 * bwheadless.exe process.
 */
public class BwHeadless {

  private static final Logger LOGGER = Logger.getLogger(BwHeadless.class.getName());
  private static final boolean CLASS_DEBUG = (Constants.DEBUG && true);

  public static final File BW_HEADLESS_EXE = new File("bwheadless.exe");

  public static final String DEFAULT_BOT_NAME = "BOT";

  private ProcessPipe bwHeadlessPipe; /* required */
  private ProcessPipe botClientPipe;  /* required only when DLL is absent */
  private File        starcraftExe;   /* required */
  private File        bwapiDll;       /* required */
  private String      botName;        /* required */
  private File        botDll;         /* required only when client is absent */
  private File        botClient;      /* *.exe or *.jar, required only when DLL is absent  */
  private Race        botRace;        /* required */
  private GameType    gameType;       /* required */

  public BwHeadless() {
    this.bwHeadlessPipe = new ProcessPipe();

    ConfigFile cf = new ConfigFile();
    cf.setVariable(new ConfigVariable("starcraft_exe", "StarCraft.exe"));
    cf.setVariable(new ConfigVariable("bwapi_dll", "BWAPI.dll"));
    cf.setVariable(new ConfigVariable("network", "lan"));
    cf.setVariable(new ConfigVariable("bot_name", "BOT"));
    cf.setVariable(new ConfigVariable("bot_race", "Terran"));
    cf.setVariable(new ConfigVariable("bot_dll", "bot.dll"));
    cf.setVariable(new ConfigVariable("bot_client", "client.exe"));
    cf.writeVariablesToFile();
  }

  public boolean launch() {
    return this.bwHeadlessPipe.open(new File("C:\\Windows\\notepad.exe"));
  }

  public void close() {
    this.bwHeadlessPipe.close();
  }

  public ProcessPipe getPipe() {
    return this.bwHeadlessPipe;
  }

}
