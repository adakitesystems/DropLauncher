package droplauncher.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.ArrayList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Class for running a program and logging its output. This class does not
 * handle errors such that if the specified program does not successfully
 * terminate itself.
 */
public class SimpleProcess {

  private static final Logger LOGGER = LogManager.getLogger();

  private ArrayList<String> log;

  public SimpleProcess() {
    this.log = new ArrayList<>();
  }

  public ArrayList<String> getLog() {
    return this.log;
  }

  public void run(Path path, String[] args) {
    this.log.clear();

    try {
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
      BufferedReader br = new BufferedReader(new InputStreamReader(is));

      String line;
      while ((line = br.readLine()) != null) {
        this.log.add(line);
      }
    } catch (Exception ex) {
      LOGGER.error(ex);
    }
  }

}
