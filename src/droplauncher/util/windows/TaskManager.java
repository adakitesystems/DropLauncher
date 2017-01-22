package droplauncher.util.windows;

import droplauncher.util.Constants;
import java.util.ArrayList;
import java.util.logging.Logger;

public class TaskManager {

  private static final Logger LOGGER = Logger.getLogger(TaskManager.class.getName());
  private static final boolean CLASS_DEBUG = (Constants.DEBUG && true);

  private Tasklist previousTasklist;
  private Tasklist currentTasklist;
  private ArrayList<Task> newTasks;

  public TaskManager() {
    this.previousTasklist = new Tasklist();
    this.currentTasklist = new Tasklist();
    this.newTasks = new ArrayList<>();

    update();
  }

  public void update() {
    this.previousTasklist.update();
    this.currentTasklist.update();
  }

  public void updateNewTasks() {
    this.newTasks.clear();

    this.currentTasklist.update();
    if (!this.currentTasklist.equals(this.previousTasklist)) {
      for (Task currTask : this.currentTasklist.getTasks()) {
        boolean isFound = false;
        for (Task prevTask : this.previousTasklist.getTasks()) {
          if (currTask.getPID().equals(prevTask.getPID())) {
            isFound = true;
            break;
          }
        }
        if (!isFound) {
          this.newTasks.add(new Task(currTask));
        }
      }
    }

    if (this.newTasks.size() > 0) {
      this.previousTasklist.update();
    }
  }

}
