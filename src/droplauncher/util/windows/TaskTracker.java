package droplauncher.util.windows;

import droplauncher.util.Constants;
import java.util.ArrayList;
import java.util.logging.Logger;

/**
 * Class for tracking newly created tasks in the Windows Tasklist.
 */
public class TaskTracker {

  private static final Logger LOGGER = Logger.getLogger(TaskTracker.class.getName());
  private static final boolean CLASS_DEBUG = (Constants.DEBUG && true);

  private Tasklist previousTasklist;
  private Tasklist currentTasklist;
  private ArrayList<Task> newTasks;

  public TaskTracker() {
    this.previousTasklist = new Tasklist();
    this.currentTasklist = new Tasklist();
    this.newTasks = new ArrayList<>();

    update();
  }

  public Tasklist getCurrentTasklist() {
    return this.currentTasklist;
  }

  public Tasklist getPreviousTasklist() {
    return this.previousTasklist;
  }

  public ArrayList<Task> getNewTasks() {
    return this.newTasks;
  }

  /**
   * Updates both the current and previous tasklists.
   */
  public void update() {
    this.previousTasklist.update();
    this.currentTasklist.update();
  }

  /**
   * Updates the current tasklist and compares it against the previous
   * tasklist to determine which currently running tasks are new.
   */
  public void scanForNewTasks() {
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
