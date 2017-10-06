package adakite.process;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Class for running a program and logging its output. This class does not
 * handle errors such that if the specified program does not successfully
 * terminate itself.
 */
public class SimpleProcess implements Runnable {

  private Path file;
  private String[] args;
  private StreamGobbler stdoutGobbler;
  private StreamGobbler stderrGobbler;

  public SimpleProcess(Path file, String[] args) {
    this.file = file;
    this.args = args;
  }

  public List<String> getStdoutLog() {
    return (this.stdoutGobbler == null)
        ? new ArrayList<>()
        : new ArrayList<>(this.stdoutGobbler.getOutput());
  }

  public List<String> getStderrLog() {
    return (this.stderrGobbler == null)
        ? new ArrayList<>()
        : new ArrayList<>(this.stderrGobbler.getOutput());
  }

  @Override
  public void run() {
    CommandBuilder command = new CommandBuilder();
    command.setPath(this.file);
    if (args != null) {
      command.setArgs(args);
    }
    Process process;
    try {
      process = new ProcessBuilder(command.get()).start();
    } catch (IOException ex) {
      //TODO: Handle error.
      return;
    }

    this.stdoutGobbler = new StreamGobbler(process.getInputStream());
    this.stderrGobbler = new StreamGobbler(process.getErrorStream());

    Thread stdoutThread = new Thread(this.stdoutGobbler);
    Thread stderrThread = new Thread(this.stderrGobbler);

    stdoutThread.start();
    stderrThread.start();

    try {
      stdoutThread.join();
    } catch (InterruptedException ex) {
      /* Do nothing. */
    }

    try {
      stderrThread.join();
    } catch (InterruptedException ex) {
      /* Do nothing. */
    }
  }

}
