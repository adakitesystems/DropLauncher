package droplauncher.config;

import adakite.bihashmap.StringBiHashMap;
import adakite.exception.DuplicateMappingException;
import droplauncher.bwheadless.PredefinedVariable;
import droplauncher.util.Constants;
import java.io.File;
import java.util.logging.Logger;

public class ConfigFile {

  private static final Logger LOGGER = Logger.getLogger(ConfigFile.class.getName());
  private static final boolean CLASS_DEBUG = (Constants.DEBUG && true);

  private StringBiHashMap variables;
  private File file;

  public ConfigFile() {
    init();
  }

  public ConfigFile(File file) {
    init();
    this.file = file;
  }

  private void init() {
    this.variables = new StringBiHashMap();
    this.file = null;
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

  public void writeVariablesToFile(File file) {
    File writeFile;
    if (file == null) {
      writeFile = this.file;
    } else {
      writeFile = file;
    }
    for (String str : this.variables.getKeySet()) {
      System.out.println(str + " = " + variables.get(str));
    }
  }

  public void writeVariablesToFile() {
    writeVariablesToFile(null);
  }

}
