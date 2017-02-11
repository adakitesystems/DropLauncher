package droplauncher.util;

import adakite.debugging.Debugging;
import adakite.util.AdakiteUtils;
import java.io.IOException;
import java.nio.file.Path;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Generic class for handling communication between the main program and
 * a process.
 */
public class ProcessPipe {

  private static final Logger LOGGER = LogManager.getLogger();

  private Process process;
  private String[] args;
  private Path cwd;
  private StreamGobbler gobblerStdout;
  private StreamGobbler gobblerStderr;

  public ProcessPipe() {
    this.process = null;
    this.args = null;
    this.cwd = null;
    this.gobblerStdout = null;
    this.gobblerStderr = null;
  }

  public String[] getArgs() {
    return this.args;
  }

  public void setArgs(String[] args) {
    this.args = args;
  }

  public Path getCWD() {
    return this.cwd;
  }

  public void setCWD(Path path) {
    this.cwd = path;
  }

  public void open(String[] args) throws IOException {
    if (args == null) {
      throw new IllegalArgumentException(Debugging.nullObject("args"));
    }

    this.args = args;

    ProcessBuilder pb = new ProcessBuilder(this.args);
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

  public void close() {
    this.gobblerStdout.interrupt();
    this.gobblerStdout.interrupt();
    this.process.destroy();
    if (this.process.isAlive()) {
      LOGGER.warn("process is still alive after attempting to destroy");
    }
  }

}
