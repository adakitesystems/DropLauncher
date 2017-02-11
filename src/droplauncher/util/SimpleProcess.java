package droplauncher.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;

/**
 * Class for running a program and logging its output. This class does not
 * handle errors such that if the specified program does not successfully
 * terminate itself.
 */
public class SimpleProcess {

  private ArrayList<String> log;

  public SimpleProcess() {
    this.log = new ArrayList<>();
  }

  public ArrayList<String> getLog() {
    return this.log;
  }

  public void run(Path path, String[] args) throws UnsupportedEncodingException,
                                                   IOException {
    this.log.clear();

    String[] command;
    if (args == null) {
      command = new String[1];
    } else {
      command = new String[args.length + 1];
      System.arraycopy(args, 0, command, 1, args.length);
    }

    command[0] = path.toString();
    Process process = new ProcessBuilder(command).start();

    InputStream is = process.getInputStream();
    try (BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
      String line;
      while ((line = br.readLine()) != null) {
        this.log.add(line);
      }
    }
  }

}
