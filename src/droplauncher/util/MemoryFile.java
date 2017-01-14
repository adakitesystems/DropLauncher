package droplauncher.util;

import adakite.utils.FileOperation;
import adakite.utils.ReadFile;
import adakite.utils.WriteFile;
import java.io.File;
import java.util.ArrayList;

public class MemoryFile  {

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
      //TODO: error message
      this.file = null;
      return false;
    }

    ReadFile rf = new ReadFile();
    if (!rf.open(this.file)) {
      //TODO: error message
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
      //TODO: error
      return false;
    }

    WriteFile wf = new WriteFile();
    if (!wf.open(file)) {
      //TODO: error
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
