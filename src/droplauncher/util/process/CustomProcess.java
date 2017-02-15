package droplauncher.util.process;

import adakite.debugging.Debugging;
import adakite.util.AdakiteUtils;
import droplauncher.mvc.view.ConsoleOutput;
import droplauncher.util.StreamGobbler;
import java.io.IOException;
import java.nio.file.Path;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Container class for starting and stopping a new process.
 */
public class CustomProcess {

  private static final Logger LOGGER = LogManager.getLogger();

  private Process process;
  private Path cwd;
  private StreamGobbler gobblerStdout;
  private StreamGobbler gobblerStderr;
  private String processName;

  public CustomProcess() {
    this.process = null;
    this.cwd = null;
    this.gobblerStdout = null;
    this.gobblerStderr = null;
    this.processName = null;
  }

  /**
   * Sets the current working directory for the executable.
   *
   * @param path specified current working directory
   */
  public CustomProcess setCWD(Path path) {
    this.cwd = path;
    return this;
  }

  public CustomProcess setProcessName(String name) {
    this.processName = name;
    return this;
  }

  /**
   * Creates and opens the pipe to the specified executable.
   *
   * @param args specified command and arguments to run
   * @throws IOException if an I/O error occurs
   */
  public void start(String[] args, ConsoleOutput co) throws IOException {
    if (args == null) {
      throw new IllegalArgumentException(Debugging.nullObject("args"));
    }

    ProcessBuilder pb = new ProcessBuilder(args);
    if (this.cwd != null && AdakiteUtils.directoryExists(this.cwd)) {
      /* Set current working directory for the new process. */
      pb.directory(this.cwd.toFile());
    }

    this.process = pb.start();

    this.gobblerStdout = new StreamGobbler()
        .setInputStream(this.process.getInputStream())
        .setConsoleOutput(co)
        .setStreamName(this.processName);
    this.gobblerStderr = new StreamGobbler()
        .setInputStream(this.process.getErrorStream())
        .setConsoleOutput(co)
        .setStreamName(this.processName);
    this.gobblerStdout.start();
    this.gobblerStderr.start();
  }

  /**
   * Attempts to close the pipe.
   */
  public void stop() {
    this.gobblerStdout.interrupt();
    this.gobblerStdout.interrupt();
    this.process.destroy();
    if (this.process.isAlive()) {
      LOGGER.warn("process is still alive after destroy attempt");
    }
  }

}
