package adakite.util.windows;

import adakite.util.AdakiteUtils;
import adakite.util.process.SimpleProcess;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.StringTokenizer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Class for getting and storing a list of processes using
 * the Windows Tasklist program.
 */
public class Tasklist {

  private static final Logger LOGGER = LogManager.getLogger();

  private ArrayList<Task> tasks;

  public Tasklist() {
    this.tasks = new ArrayList<>();
  }

  //TODO: Use a CommandBuilder object.
  /**
   * Kill the task indicated by the specified process ID.
   *
   * @param pid specified process ID
   * @throws IOException if an I/O error occurs
   */
  public void kill(String pid) throws IOException {
    String[] args = new String[Windows.Program.TASKKILL.getPredefinedArgs().length + 1];
    System.arraycopy(Windows.Program.TASKKILL.getPredefinedArgs(), 0, args, 0, Windows.Program.TASKKILL.getPredefinedArgs().length);
    args[args.length - 1] = pid;
    SimpleProcess process = new SimpleProcess();
    process.run(Windows.Program.TASKKILL.getPath().toAbsolutePath(), args);
  }

  /**
   * Returns a list of Task objects.
   *
   * @param update whether to update the list before returning
   *
   * @throws IOException if an I/O error occurs
   */
  public ArrayList<Task> getTasks(boolean update) throws IOException {
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
   * @throws IOException if an I/O error occurs
   */
  public ArrayList<Task> getTasks() throws IOException {
    return getTasks(false);
  }

  /**
   * Runs the Windows Tasklist program and updates the internal tasklist.
   *
   * @throws IOException if an I/O error occurs
   */
  public void update() throws IOException {
    this.tasks.clear();

    SimpleProcess process = new SimpleProcess();
    process.run(Windows.Program.TASKLIST.getPath().toAbsolutePath(), Windows.Program.TASKLIST.getPredefinedArgs());

    /* Find beginning of process list. */
    int index;
    for (index = 0; index < process.getLog().size(); index++) {
      String line = process.getLog().get(index);
      if (line.startsWith("=")) {
        break;
      }
    }
    if (index >= process.getLog().size()) {
      //TODO: Throw built-in or custom exception.
      LOGGER.error("error parsing Tasklist output");
      return;
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
        //TODO: Throw built-in or custom exception.
        LOGGER.error("error parsing task entry line");
        return;
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
    } else if (this == obj) {
      return true;
    } else {
      try {
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
      } catch (IOException ex) {
        LOGGER.error(ex);
        return false;
      }
    }
    return true;
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.tasks);
  }

}
