package droplauncher.util;

import adakite.debugging.Debugging;
import adakite.util.AdakiteUtils;
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

  public CustomProcess() {
    this.process = null;
    this.cwd = null;
    this.gobblerStdout = null;
    this.gobblerStderr = null;
  }

  /**
   * Sets the current working directory for the executable.
   *
   * @param path specified current working directory
   */
  public void setCWD(Path path) {
    this.cwd = path;
  }

  /**
   * Creates and opens the pipe to the specified executable.
   *
   * @param args specified command and arguments to run
   * @throws IOException if an I/O error occurs
   */
  public void start(String[] args) throws IOException {
    if (args == null) {
      throw new IllegalArgumentException(Debugging.nullObject("args"));
    }

    ProcessBuilder pb = new ProcessBuilder(args);
    if (this.cwd != null && AdakiteUtils.directoryExists(this.cwd)) {
      /* Set current working directory for the new process. */
      pb.directory(this.cwd.toFile());
    }

    this.process = pb.start();

    this.gobblerStdout = new StreamGobbler(this.process.getInputStream());
    this.gobblerStderr = new StreamGobbler(this.process.getErrorStream());
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
