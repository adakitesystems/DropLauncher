package adakite.process;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Class for running a program and logging its output. This class does not
 * handle errors such that if the specified program does not successfully
 * terminate itself.
 */
public class SimpleProcess {

  private List<String> log;

  public SimpleProcess() {
    this.log = new ArrayList<>();
  }

  public List<String> getLog() {
    return this.log;
  }

  public void run(Path path, String[] args) throws UnsupportedEncodingException,
                                                   IOException {
    this.log.clear();

    CommandBuilder command = new CommandBuilder();
    command.setPath(path);
    if (args != null) {
      command.setArgs(args);
    }
    Process process = new ProcessBuilder(command.get()).start();

    InputStream is = process.getInputStream();
    try (BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
      String line;
      while ((line = br.readLine()) != null) {
        this.log.add(line);
      }
    }
  }

}
