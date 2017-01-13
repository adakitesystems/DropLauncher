package droplauncher.config;

import adakite.bihashmap.StringBiHashMap;
import adakite.exception.DuplicateMappingException;
import adakite.utils.AdakiteUtils;
import adakite.utils.ReadFile;
import adakite.utils.WriteFile;
import droplauncher.util.Constants;
import java.io.File;
import java.util.logging.Logger;

public class Settings {

  private static final Logger LOGGER = Logger.getLogger(Settings.class.getName());
  private static final boolean CLASS_DEBUG = (Constants.DEBUG && true);

  public static final String DEFAULT_FILE_EXTENSION = ".ini";
  public static final String VARIABLE_DELIMITER = "=";
  public static final String COMMENT_DELIMITER = ";";

  private StringBiHashMap variables;
  private File file;

  public Settings() {
    init();
  }

  public Settings(File file) {
    init();
    this.file = file;
  }

  private void init() {
    this.variables = new StringBiHashMap();
    this.file = null;
  }

  public File getFile() {
    return this.file;
  }

  public void setFile(File file) {
    this.file = file;
  }

  public StringBiHashMap getVariables() {
    return this.variables;
  }

  public boolean setVariable(ConfigVariable var) {
    if (this.variables.contains(var.getKey())) {
      this.variables.remove(var.getKey());
    }
    try {
      this.variables.put(var.getKey(), var.getValue());
    } catch (DuplicateMappingException ex) {
      if (CLASS_DEBUG) {
        LOGGER.log(Constants.DEFAULT_LOG_LEVEL, null, ex);
      }
      return false;
    }
    return true;
  }

  public void readVariablesFromFile(File file) {
    File f = file;
    if (f == null && this.file == null) {
      return;
    }
    if (f == null) {
      f = this.file;
    }

    ReadFile rf = new ReadFile();
    if (!rf.open(file)) {
      //TODO: Display/handle error message.
      return;
    }
    String line;
    while ((line = rf.getNextLine()) != null) {
      line = line.trim();
      if (AdakiteUtils.isNullOrEmpty(line)) {
        continue;
      }
      int sepIndex = line.indexOf(VARIABLE_DELIMITER);
      String key = line.substring(0, sepIndex).trim();
      String val = line.substring(sepIndex + VARIABLE_DELIMITER.length(), line.length()).trim();
      setVariable(new ConfigVariable(key, val));
      //DEBUG ---
      System.out.println("Read variable: " + key + " = " + val);
      //---
    }
    rf.close();
  }

  public void writeVariablesToFile(File file) {
    File f = file;
    if (f == null && this.file == null) {
      return;
    }
    if (f == null) {
      f = this.file;
    }

    WriteFile wf = new WriteFile();
    if (!wf.open(f)) {
      //TODO: Display/handle error message.
      return;
    }
    for (String str : this.variables.getKeySet()) {
      String line = str + " = " + variables.get(str);
      System.out.println("Writing to: " + f + ": " + line);
      if (!wf.writeLine(line)) {
        //TODO: Display/handle error message.
        return;
      }
    }
    wf.close();
  }

  public void writeVariablesToFile() {
    writeVariablesToFile(null);
  }

}
