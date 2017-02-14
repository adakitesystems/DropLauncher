package droplauncher.mvc.model;

import adakite.ini.INI;
import adakite.util.AdakiteUtils;
import droplauncher.bwheadless.BWHeadless;
import droplauncher.bwheadless.BotFile;
import droplauncher.bwheadless.KillableTask;
import droplauncher.exception.InvalidBotTypeException;
import droplauncher.util.Constants;
import droplauncher.util.windows.Task;
import droplauncher.util.windows.TaskTracker;
import droplauncher.util.windows.Tasklist;
import java.io.IOException;
import java.util.ArrayList;
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
      this.ini.read(Constants.DROPLAUNCHER_INI_PATH);
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
    boolean isClient = this.bwheadless.getBotType() == BotFile.Type.CLIENT;
    String botName = this.bwheadless.getBotPath().getFileName().toString();
    for (Task task : tasks) {
      /* Kill bot client. */
      if (isClient && botName.contains(task.getImageName())) {
        LOGGER.info("Kill: " + task.getPID() + ":" + task.getImageName());
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
  }

}
