package droplauncher.util;

import adakite.debugging.Debugging;
import adakite.utils.FileOperation;
import adakite.utils.ReadFile;
import adakite.utils.WriteFile;
import java.io.File;
import java.util.ArrayList;
import java.util.logging.Logger;

public class MemoryFile  {

  private static final Logger LOGGER = Logger.getLogger(MemoryFile.class.getName());
  private static final boolean CLASS_DEBUG = (Constants.DEBUG && true);

  private File file;
  private ArrayList<String> lines;

  public MemoryFile() {
    this.file = null;
    this.lines = new ArrayList<>();
  }

  public void reset() {
    this.file = null;
    this.lines.clear();
  }

  public File getFile() {
    return this.file;
  }

  public ArrayList<String> getLines() {
    return this.lines;
  }

  public boolean open(File file) {
    reset();

    if (file == null) {
      return false;
    }

    this.file = file;
    if (!(new FileOperation(file).doesFileExist()) && !(new FileOperation(file).createFile())) {
      if (CLASS_DEBUG) {
        LOGGER.log(Constants.DEFAULT_LOG_LEVEL, Debugging.createFail(file));
      }
      reset();
      return false;
    }

    ReadFile rf = new ReadFile();
    if (!rf.open(this.file)) {
      if (CLASS_DEBUG) {
        LOGGER.log(Constants.DEFAULT_LOG_LEVEL, Debugging.openFail(file));
      }
      reset();
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
        LOGGER.log(Constants.DEFAULT_LOG_LEVEL, Debugging.nullObject());
      }
      return false;
    }

    WriteFile wf = new WriteFile();
    if (!wf.open(file)) {
      if (CLASS_DEBUG) {
        LOGGER.log(Constants.DEFAULT_LOG_LEVEL, Debugging.openFail(file));
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
    StringBuilder sb = new StringBuilder();
    for (String line : this.lines) {
      sb.append(line).append(System.lineSeparator());
    }
    System.out.print(sb.toString());
  }

}
