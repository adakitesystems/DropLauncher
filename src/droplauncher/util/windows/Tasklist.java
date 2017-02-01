package droplauncher.util.windows;

import adakite.utils.AdakiteUtils;
import droplauncher.util.Constants;
import droplauncher.util.SimpleProcess;
import java.util.ArrayList;
import java.util.Objects;
import java.util.StringTokenizer;
import java.util.logging.Logger;

/**
 * Class for getting and storing a list of processes using
 * the Windows Tasklist program.
 */
public class Tasklist {

  private static final Logger LOGGER = Logger.getLogger(Tasklist.class.getName());
  private static final boolean CLASS_DEBUG = (Constants.DEBUG && true);

  private ArrayList<Task> tasks;

  public Tasklist() {
    this.tasks = new ArrayList<>();
  }

  public void kill(String pid) {
    String[] args = new String[Windows.DEFAULT_TASKKILL_ARGS.length + 1];
    System.arraycopy(Windows.DEFAULT_TASKKILL_ARGS, 0, args, 0, Windows.DEFAULT_TASKKILL_ARGS.length);
    args[args.length - 1] = pid;
    SimpleProcess process = new SimpleProcess();
    process.run(Windows.TASKKILL_EXE, args);
  }

  /**
   * Returns a list of Task objects.
   *
   * @param update whether to update the list before returning
   * @return
   *     a list of Task objects
   */
  public ArrayList<Task> getTasks(boolean update) {
    if (update) {
      update();
    }
    return this.tasks;
  }

  /**
   * Returns a list of Task objects. Does NOT update the task list
   * before returning.
   *
   * @see #getTasks(boolean)
   * @return
   *     a list of Task objects
   */
  public ArrayList<Task> getTasks() {
    return getTasks(false);
  }

  public boolean update() {
    this.tasks.clear();

    SimpleProcess process = new SimpleProcess();
    process.run(Windows.TASKLIST_EXE, Windows.DEFAULT_TASKLIST_ARGS);

    /* Find beginning of process list. */
    int index;
    for (index = 0; index < process.getLog().size(); index++) {
      String line = process.getLog().get(index);
      if (line.startsWith("=")) {
        break;
      }
    }
    if (index >= process.getLog().size()) {
      if (CLASS_DEBUG) {
        LOGGER.log(Constants.DEFAULT_LOG_LEVEL, "error parsing Tasklist output");
      }
      return false;
    }

    /* Determine length of each column. */
    ArrayList<Integer> colLengths = new ArrayList<>();
    String colLine = process.getLog().get(index);
    StringTokenizer st = new StringTokenizer(colLine);
    while (st.hasMoreTokens()) {
      String token = st.nextToken();
      colLengths.add(token.length());
    }

    /* Go to first line with a task entry. */
    index++;

    /* Parse remaining lines. */
    for (int i = index; i < process.getLog().size(); i++) {
      String line = process.getLog().get(i);
      if (AdakiteUtils.isNullOrEmpty(line, true)) {
        continue;
      }

      /* Tokenize line using the column lengths. */
      ArrayList<String> tokens = tokenizeTaskEntry(line, colLengths);
      if (tokens.size() < TasklistTitle.values().length) {
        if (CLASS_DEBUG) {
          LOGGER.log(Constants.DEFAULT_LOG_LEVEL, "error parsing task entry line");
        }
        return false;
      }

      /* Set task information. */
      Task task = new Task();
      int tokenIndex = 0;
      task.setImageName(tokens.get(tokenIndex++));
      task.setPID(tokens.get(tokenIndex++));
      task.setSessionName(tokens.get(tokenIndex++));
      task.setSessionNumber(tokens.get(tokenIndex++));
      task.setMemUsage(tokens.get(tokenIndex++));
      task.setStatus(tokens.get(tokenIndex++));
      task.setUsername(tokens.get(tokenIndex++));
      task.setCpuTime(tokens.get(tokenIndex++));
      task.setWindowTitle(tokens.get(tokenIndex++));

      /* Add task to main task list. */
      this.tasks.add(task);
    }

    return true;
  }

  private ArrayList<String> tokenizeTaskEntry(String str, ArrayList<Integer> colLengths) {
    ArrayList<String> ret = new ArrayList<>();

    int colIndex = 0;
    for (int i = 0; i < colLengths.size(); i++) {
      String tmp = str.substring(colIndex, colIndex + colLengths.get(i)).trim();
      ret.add(tmp);
      colIndex += colLengths.get(i) + 1;
    }

    return ret;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null || !(obj instanceof Tasklist)) {
      return false;
    } else if (obj == this) {
      return true;
    } else {
      Tasklist tasklist = (Tasklist) obj;
      for (Task task1 : tasklist.getTasks()) {
        boolean isFound = false;
        for (Task task2 : this.getTasks()) {
          if (task1.getPID().equals(task2.getPID())) {
            isFound = true;
            break;
          }
        }
        if (!isFound) {
          return false;
        }
      }
    }
    return true;
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.tasks);
  }

}
