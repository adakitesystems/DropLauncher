package droplauncher.util;

import adakite.debugging.Debugging;
import adakite.utils.AdakiteUtils;
import adakite.utils.ReadFile;
import adakite.utils.WriteFile;
import java.io.File;
import java.util.ArrayList;
import java.util.logging.Logger;

/**
 * Class for storing an entire plain text file in memory as an ArrayList of
 * String objects for each line.
 */
public class MemoryFile  {

  private static final Logger LOGGER = Logger.getLogger(MemoryFile.class.getName());
  private static final boolean CLASS_DEBUG = (Constants.DEBUG && true);

  private File file;
  private ArrayList<String> lines;

  public MemoryFile() {
    this.file = null;
    this.lines = new ArrayList<>();
  }

  public void clear() {
    this.file = null;
    this.lines.clear();
  }

  public File getFile() {
    return this.file;
  }

  public ArrayList<String> getLines() {
    return this.lines;
  }

  public boolean open(File file) throws Exception {
    clear();

    if (file == null) {
      return false;
    }

    this.file = file;
    AdakiteUtils.createFile(file.toPath());

    ReadFile rf = new ReadFile();
    if (!rf.open(this.file)) {
      if (CLASS_DEBUG) {
        LOGGER.log(Debugging.getLogLevel(), Debugging.openFail(file));
      }
      clear();
      return false;
    }
    String line;
    while ((line = rf.getNextLine()) != null) {
      this.lines.add(line);
    }
    rf.close();

    return true;
  }

  public boolean dumpToFile(File file) {
    if (file == null) {
      if (CLASS_DEBUG) {
        LOGGER.log(Debugging.getLogLevel(), Debugging.nullObject());
      }
      return false;
    }

    WriteFile wf = new WriteFile();
    if (!wf.open(file)) {
      if (CLASS_DEBUG) {
        LOGGER.log(Debugging.getLogLevel(), Debugging.openFail(file));
      }
      return false;
    }
    for (String line : this.lines) {
      wf.writeLine(line);
    }
    wf.close();

    return true;
  }

  public boolean dumpToFile() {
    return this.dumpToFile(this.file);
  }

  public void dumpToConsole() {
    if (this.lines.size() < 1) {
      return;
    }
    StringBuilder sb = new StringBuilder();
    for (String line : this.lines) {
      sb.append(line).append(System.lineSeparator());
    }
    System.out.print(sb.toString());
  }

}
