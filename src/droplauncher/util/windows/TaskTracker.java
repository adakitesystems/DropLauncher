package droplauncher.util.windows;

import java.util.ArrayList;

/**
 * Class for tracking newly created tasks in the Windows Tasklist.
 */
public class TaskTracker {

  private Tasklist previousTasklist;
  private Tasklist currentTasklist;
  private ArrayList<Task> newTasks;

  public TaskTracker() {
    this.previousTasklist = new Tasklist();
    this.currentTasklist = new Tasklist();
    this.newTasks = new ArrayList<>();
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
   *
   * @param updatePreviousTasks whether to update the previous
   *     tasklist after comparison
   */
  public void updateNewTasks(boolean updatePreviousTasks) {
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

    if (updatePreviousTasks && this.newTasks.size() > 0) {
      this.previousTasklist.update();
    }
  }

  /**
   * Updates the current tasklist and compares it against the previous
   * tasklist to determine which currently running tasks are new. Note:
   * this call does NOT update the previous tasklist.
   *
   * @see #updateNewTasks(boolean)
   */
  public void updateNewTasks() {
    updateNewTasks(false);
  }

}
