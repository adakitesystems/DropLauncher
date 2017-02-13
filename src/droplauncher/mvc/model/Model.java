package droplauncher.mvc.model;

import adakite.ini.INI;
import adakite.util.AdakiteUtils;
import droplauncher.bwapi.BWAPI;
import droplauncher.bwheadless.BWHeadless;
import droplauncher.bwheadless.BotFile;
import droplauncher.bwheadless.KillableTask;
import droplauncher.exception.InvalidBotTypeException;
import droplauncher.util.Constants;
import droplauncher.util.SettingsKey;
import droplauncher.util.windows.Task;
import droplauncher.util.windows.TaskTracker;
import droplauncher.util.windows.Tasklist;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Model {

  private static final Logger LOGGER = LogManager.getLogger();

  private INI ini;
  private BWHeadless bwheadless;
  private TaskTracker taskTracker;

  public Model() {
    this.ini = new INI();
    this.bwheadless = new BWHeadless();
    this.taskTracker = new TaskTracker();

    this.bwheadless.setINI(this.ini);
    try {
      if (!AdakiteUtils.fileExists(Constants.DROPLAUNCHER_INI_PATH)) {
        AdakiteUtils.createFile(Constants.DROPLAUNCHER_INI_PATH);
      }
      this.ini.open(Constants.DROPLAUNCHER_INI_PATH);
      this.bwheadless.parseSettings(this.ini);
    } catch (IOException ex) {
      LOGGER.error(ex);
    }
  }

  public INI getINI() {
    return this.ini;
  }

  public BWHeadless getBWHeadless() {
    return this.bwheadless;
  }

  public void startBWHeadless() throws IOException, InvalidBotTypeException {
    this.taskTracker.reset();
    this.bwheadless.start();
  }

  public void stopBWHeadless() throws IOException {
    /* Kill new tasks that were started with bwheadless. */
    this.taskTracker.update();
    ArrayList<Task> tasks = this.taskTracker.getNewTasks();
    Tasklist tasklist = new Tasklist();
    boolean isClient = this.bwheadless.getBotFile().getType() == BotFile.Type.CLIENT;
    String botName = this.bwheadless.getBotFile().getPath().getFileName().toString();
    for (Task task : tasks) {
      /* Kill bot client. */
      if (isClient && botName.contains(task.getImageName())) {
        LOGGER.info("Killing: " + task.getPID() + ":" + task.getImageName());
        tasklist.kill(task.getPID());
        continue;
      }
      /* Only kill tasks whose names match known associated tasks. */
      for (KillableTask kt : KillableTask.values()) {
        if (kt.toString().equalsIgnoreCase(task.getImageName())) {
          LOGGER.info("Kill: " + task.getPID() + ":" + task.getImageName());
          tasklist.kill(task.getPID());
          break;
        }
      }
    }
    this.bwheadless.stop();

    /* Copy contents of "bwapi-data/write/" to "bwapi-data/read/". */
    Path starcraftDirectory = AdakiteUtils.getParentDirectory(Paths.get(this.bwheadless.getINI().getValue(BWHeadless.DEFAULT_INI_SECTION_NAME, SettingsKey.STARCRAFT_EXE.toString())));
    if (starcraftDirectory != null) {
      Path src = starcraftDirectory.resolve(BWAPI.BWAPI_DATA_WRITE_PATH);
      Path dest = starcraftDirectory.resolve(BWAPI.BWAPI_DATA_READ_PATH);
      if (AdakiteUtils.directoryExists(src)) {
        LOGGER.info("Copy: \"" + src.toString() + "\" -> \"" + dest.toString() + "\"");
        FileUtils.copyDirectory(src.toFile(), dest.toFile());
      }
    } else {
      LOGGER.warn("Unable to copy \"StarCraft/bwapi-data/write/\" to \"StarCraft/bwapi-data/read/\"");
    }
  }

}
