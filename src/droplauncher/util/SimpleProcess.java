package droplauncher.util;

import adakite.debugging.Debugging;
import adakite.utils.AdakiteUtils;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.logging.Logger;

/**
 * Class for running a program and logging its output. The specified program
 * should successfully terminate itself after writing to stdout.
 */
public class SimpleProcess {

  private static final Logger LOGGER = Logger.getLogger(SimpleProcess.class.getName());
  private static final boolean CLASS_DEBUG = (Constants.DEBUG && true);

  private ArrayList<String> log;

  public SimpleProcess() {
    this.log = new ArrayList<>();
  }

  public ArrayList<String> getLog() {
    return this.log;
  }

  public boolean run(File file, String[] args) {
    this.log.clear();

    if (file == null) {
      if (CLASS_DEBUG) {
        LOGGER.log(Debugging.getLogLevel(), Debugging.nullObject("file"));
      }
      return false;
    } else if (!AdakiteUtils.fileExists(file.toPath())) {
      if (CLASS_DEBUG) {
        LOGGER.log(Debugging.getLogLevel(), Debugging.fileDoesNotExist(file));
      }
      return false;
    }

    try {
      String[] command;
      if (args == null) {
        command = new String[1];
      } else {
        command = new String[args.length + 1];
        System.arraycopy(args, 0, command, 1, args.length);
      }

      command[0] = file.getAbsolutePath();
      Process process = new ProcessBuilder(command).start();

      InputStream is = process.getInputStream();
      BufferedReader br = new BufferedReader(new InputStreamReader(is));

      String line;
      while ((line = br.readLine()) != null) {
        this.log.add(line);
      }
    } catch (Exception ex) {
      if (CLASS_DEBUG) {
        LOGGER.log(Debugging.getLogLevel(), null, ex);
      }
      return false;
    }

    return true;
  }

}
