/*
TODO: Change ArrayList to List.
*/

package adakite.util.windows.task;

import java.io.IOException;
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
   * Resets both the current and previous tasklists.
   *
   * @throws IOException if an I/O error occurs
   */
  public void reset() throws IOException {
    this.newTasks.clear();
    this.previousTasklist.update();
    this.currentTasklist.update();
  }

  /**
   * Updates the current tasklist and compares it against the previous
   * tasklist to determine which currently running tasks are new.
   *
   * @throws IOException if an I/O error occurs
   * @see #getNewTasks()
   */
  public void update() throws IOException {
    this.newTasks.clear();
    this.currentTasklist.update();
    for (Task currTask : this.currentTasklist.getTasks()) {
      if (!this.previousTasklist.getTasks().contains(currTask)) {
        this.newTasks.add(currTask);
      }
    }
  }

}
