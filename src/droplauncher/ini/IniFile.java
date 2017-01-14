package droplauncher.ini;

import adakite.utils.AdakiteUtils;
import droplauncher.util.MemoryFile;
import droplauncher.util.Settings;
import java.io.File;
import java.util.ArrayList;

public class IniFile {

  public static final String FILE_EXTENSION = ".ini";
  public static final String VARIABLE_DELIMITER = "=";
  public static final String COMMENT_DELIMITER = ";";

  private MemoryFile memoryFile;
  private Settings settings;

  public IniFile() {
    this.memoryFile = new MemoryFile();
    this.settings = new Settings();
  }

  public Settings getSettings() {
    return this.settings;
  }

  public void setSettings(Settings settings) {
    this.settings = settings;
  }

  public int getIndexByKey(String key) {
    for (int i = 0; i < this.memoryFile.getLines().size(); i++) {
      String line = this.memoryFile.getLines().get(i);
      if (line.startsWith(key)) {
        return i;
      }
    }
    return -1;
  }

  public boolean open(File file) {
    if (file == null) {
      //TODO: Display an error message.
      return false;
    }

    if (!this.memoryFile.open(file)) {
      //TODO: Display an error message.
      return false;
    }

    int index;
    String key;
    String val;
    ArrayList<String> lines = this.memoryFile.getLines();
    for (String line : lines) {
      line = line.trim();
      if (AdakiteUtils.isNullOrEmpty(line) || line.startsWith(COMMENT_DELIMITER)) {
        continue;
      }

      index = line.indexOf(COMMENT_DELIMITER);
      if (index >= 0) {
        line = line.substring(0, index);
      }

      index = line.indexOf(VARIABLE_DELIMITER);
      if (index < 0) {
        continue;
      }
      key = line.substring(0, index).trim();
      val = line.substring(index + VARIABLE_DELIMITER.length(), line.length()).trim();
      this.settings.setVariable(key, val);

      //DEBUG ---
      System.out.println("Read variable: " + key + " = " + val);
      //---
    }

    return true;
  }

  public boolean refresh() {
    return open(this.memoryFile.getFile());
  }

  public boolean setVariable(String key, String val, String comment) {
    if (!this.settings.isVariableSet(key)) {
      return false;
    }

    this.settings.setVariable(key, val);

    int lineIndex = getIndexByKey(key);
    int commentIndex;
    String line = this.memoryFile.getLines().get(lineIndex);
    String tmpComment;

    if (comment != null) {
      if (comment.length() > 0) {
        tmpComment = " " + COMMENT_DELIMITER + " " + comment;
      } else {
        tmpComment = "";
      }
    } else if ((commentIndex = line.indexOf(COMMENT_DELIMITER)) >= 0) {
      tmpComment = " " + line.substring(commentIndex, line.length());
    } else {
      tmpComment = "";
    }

    String entry = key + " " + VARIABLE_DELIMITER + " " + val + tmpComment;
    this.memoryFile.getLines().set(lineIndex, entry);
    this.memoryFile.dumpToFile();
    refresh();

    return true;
  }

  public boolean setVariable(String key, String val) {
    return setVariable(key, val, null);
  }

  public boolean enableVariable(String key) {
    if (this.settings.isVariableSet(key)) {
      return true;
    }
    for (int i = 0; i < this.memoryFile.getLines().size(); i++) {
      String line = this.memoryFile.getLines().get(i);
      if (line.contains(COMMENT_DELIMITER)
          && line.contains(key)
          && line.contains(VARIABLE_DELIMITER)
          && line.indexOf(COMMENT_DELIMITER) < line.indexOf(key)
          && line.indexOf(key) < line.indexOf(VARIABLE_DELIMITER)
      ) {
        int commentIndex = line.indexOf(COMMENT_DELIMITER);
        line = line.substring(commentIndex + COMMENT_DELIMITER.length(), line.length()).trim();
        this.memoryFile.getLines().set(i, line);
        this.memoryFile.dumpToFile();
        refresh();
        return true;
      }
    }
    return false;
  }

  public void disableVariable(String key) {
    if (!this.settings.isVariableSet(key)) {
      return;
    }
    int lineIndex = getIndexByKey(key);
    String line = this.memoryFile.getLines().get(lineIndex);
    line = COMMENT_DELIMITER + line;
    this.memoryFile.getLines().set(lineIndex, line);
    this.memoryFile.dumpToFile();
    this.settings.getVariables().remove(key);
    refresh();
  }

}
