package adakite.process;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Class for running a program and logging its output. This class does not
 * handle errors such that if the specified program does not successfully
 * terminate itself.
 */
public class SimpleProcess {

  private List<String> log;
  private List<String> errorLog;

  public SimpleProcess() {
    this.log = new ArrayList<>();
    this.errorLog = new ArrayList<>();
  }

  /**
   * Returns a copy of the internal output log.
   */
  public List<String> getLog() {
    List<String> log = new ArrayList<>();
    Collections.copy(log, this.log);
    return log;
  }

  /**
   * Returns a copy of the internal output error log.
   */
  public List<String> getErrorLog() {
    List<String> errorLog = new ArrayList<>();
    Collections.copy(errorLog, this.errorLog);
    return errorLog;
  }

  /**
   * Executes the specified path with arguments.
   *
   * @param path specified path to file to execute
   * @param args specified arguments
   * @throws UnsupportedEncodingException
   * @throws IOException
   */
  public void run(Path path, String[] args) throws UnsupportedEncodingException,
                                                   IOException {
    this.log.clear();
    this.errorLog.clear();

    CommandBuilder command = new CommandBuilder();
    command.setPath(path);
    if (args != null) {
      command.setArgs(args);
    }
    Process process = new ProcessBuilder(command.get()).start();

    InputStream is;

    is = process.getErrorStream();
    try (BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
      String line;
      while ((line = br.readLine()) != null) {
        this.errorLog.add(line);
      }
    }

    is = process.getInputStream();
    try (BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
      String line;
      while ((line = br.readLine()) != null) {
        this.log.add(line);
      }
    }
  }

  /**
   * @see #run(java.nio.file.Path, java.lang.String[])
   */
  public void run(Path path) throws UnsupportedEncodingException,
                                    IOException {
    run(path, null);
  }

}
