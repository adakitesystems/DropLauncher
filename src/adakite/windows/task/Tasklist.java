package adakite.windows.task;

import adakite.debugging.Debugging;
import adakite.process.CommandBuilder;
import adakite.util.AdakiteUtils;
import adakite.process.SimpleProcess;
import adakite.windows.Windows;
import adakite.windows.task.exception.TasklistParseException;
import java.io.IOException;
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

  /**
   * Enum class containing column headers for output of "tasklist.exe" in
   * combination of using
   * {@link droplauncher.util.windows.Windows#DEFAULT_TASKLIST_ARGS}.
   */
  public enum ColumnHeader {

    IMAGE_NAME("Image Name"),
    PID("PID"),
    SESSION_NAME("Session Name"),
    SESSION_NUMBER("Session Number"),
    MEM_USAGE("Mem Usage"),
    STATUS("Status"),
    USERNAME("User Name"),
    CPU_TIME("CPU Time"),
    WINDOW_TITLE("Window Title")
    ;

    private final String str;

    private ColumnHeader(String str) {
      this.str = str;
    }

    @Override
    public String toString() {
      return this.str;
    }

  }

  private ArrayList<Task> tasks;

  public Tasklist() {
    this.tasks = new ArrayList<>();
  }

  /**
   * Kill the task indicated by the specified process ID.
   *
   * @param pid specified process ID
   * @throws IOException if an I/O error occurs
   */
  public static void kill(String pid) throws IOException {
//    String[] args = new String[Windows.Program.TASKKILL.getPredefinedArgs().length + 1];
//    System.arraycopy(Windows.Program.TASKKILL.getPredefinedArgs(), 0, args, 0, Windows.Program.TASKKILL.getPredefinedArgs().length);
//    args[args.length - 1] = pid;
    CommandBuilder command = new CommandBuilder()
        .setPath(Windows.Program.TASKKILL.getPath().toAbsolutePath())
        .setArgs(Windows.Program.TASKKILL.getPredefinedArgs())
        .addArg(pid);
    SimpleProcess process = new SimpleProcess();
    process.run(command.getPath(), command.getArgs());
  }

  /**
   * Returns a list of Task objects.
   *
   * @param update whether to update the list before returning
   *
   * @throws IOException if an I/O error occurs
   * @throws TasklistParseException
   */
  public ArrayList<Task> getTasks(boolean update) throws IOException, TasklistParseException {
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
  public ArrayList<Task> getTasks() throws IOException, TasklistParseException {
    return getTasks(false);
  }

  /**
   * Runs the Windows Tasklist program and updates the internal tasklist.
   *
   * @throws IOException if an I/O error occurs
   * @throws TasklistParseException
   */
  public void update() throws IOException, TasklistParseException {
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
      throw new TasklistParseException("error parsing Tasklist output");
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
      if (tokens.size() < ColumnHeader.values().length) {
        //TODO: Throw built-in or custom exception.
        LOGGER.log(Debugging.getLogLevel(), "error parsing task entry line");
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
      } catch (Exception ex) {
        LOGGER.log(Debugging.getLogLevel(), null, ex);
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
