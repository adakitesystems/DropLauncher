package droplauncher.util.windows;

import droplauncher.util.Constants;
import java.util.logging.Logger;

public class TaskManager {

  private static final Logger LOGGER = Logger.getLogger(TaskManager.class.getName());
  private static final boolean CLASS_DEBUG = (Constants.DEBUG && true);

  private Tasklist previousTasklist;
  private Tasklist currentTasklist;

  public TaskManager() {
    this.previousTasklist = new Tasklist();
    this.currentTasklist = new Tasklist();

    this.previousTasklist.update();
    this.currentTasklist.update();
  }

}
