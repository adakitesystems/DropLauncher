/* ConfigFile.java */

package droplauncher.config;

import droplauncher.debugging.Debugging;
import droplauncher.tools.MainTools;
import droplauncher.tools.MemoryFile;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.lang.NullPointerException;
import java.util.ArrayList;

/**
 * Class for handling configuration files.
 *
 * @author Adakite Systems
 * @author adakitesystems@gmail.com
 */
public class ConfigFile {

  private static final Logger LOGGER = LogManager.getRootLogger();

  public static final String FILE_EXTENSION = ".cfg";
  public static final String VARIABLE_DELIMITER = "=";
  public static final String COMMENT_DELIMITER = ";";

  private MemoryFile memoryFile;
  private ArrayList<ConfigVariable> variables;

  /**
   * Initialize class variables.
   */
  public ConfigFile() {
    this.memoryFile = new MemoryFile();
    this.variables = new ArrayList<>();
  }

  public File getFile() {
    return memoryFile.getFile();
  }

  public void reset() {
    this.variables.clear();
  }

  /**
   * Create a file if it does not already exist.
   *
   * @param file specified file to create
   * @return
   *     true if file was created successfully or already exists,
   *     otherwise false
   */
  public boolean create(File file) {
    reset();
    return this.memoryFile.create(file);
  }

  /**
   * Opens the configuration file and read in its variables.
   *
   * @param file specified configuration file to read
   * @return
   *     true if file has been opened successfully,
   *     otherwise false
   */
  public boolean open(File file) {
    reset();

    if (file == null) {
      LOGGER.warn(Debugging.nullObject());
    }

    if (!this.memoryFile.readIntoMemory(file)) {
      LOGGER.warn(Debugging.openFail(file));
      return false;
    }

    ArrayList<String> lines = this.memoryFile.getLines();
    int len = lines.size();

    /* Read was successful but file is empty. */
    if (len < 1) {
      return true;
    }

    String line;
    String varName;
    String varValue;
    int index;

    /* Read lines as variables and values. */
    for (int i = 0; i < len; i++) {
      line = lines.get(i).trim();

      /* Ignore comment. */
      if (line.startsWith(COMMENT_DELIMITER)) {
        continue;
      }
      index = line.indexOf(COMMENT_DELIMITER);
      if (index >= 0) {
        line = line.substring(0, index);
      }

      /* Parse variable. */
      index = line.indexOf(VARIABLE_DELIMITER);
      if (index < 0) {
        continue;
      }
      varName = line.substring(0, index).trim();
      varValue = line.substring(index + 1, line.length()).trim();

      this.variables.add(new ConfigVariable(varName, varValue));
    }

    return true;
  }

  public boolean refresh() {
    return open(this.memoryFile.getFile());
  }

  /**
   * Returns the index of the specified variable name.
   *
   * @param name specified variable name
   * @return
   *     the index of the specified variable name if found,
   *     otherwise -1
   */
  public int indexOfName(String name) {
    if (MainTools.isEmpty(name)) {
      LOGGER.warn(Debugging.emptyString());
      return -1;
    }

    int len = this.variables.size();
    ConfigVariable tmpVar;

    for (int i = 0; i < len ; i++) {
      tmpVar = this.variables.get(i);
      if (tmpVar.getName().equalsIgnoreCase(name)) {
        return i;
      }
    }

    return -1;
  }

  /**
   * Returns the index of the specified value.
   *
   * @param value specified value
   * @return
   *     the index of the specified value name if found,
   *     otherwise -1
   */
  public int indexOfValue(String value) {
    if (MainTools.isEmpty(value)) {
      LOGGER.warn(Debugging.emptyString());
      return -1;
    }

    int len = this.variables.size();
    ConfigVariable tmpVar;

    for (int i = 0; i < len ; i++) {
      tmpVar = this.variables.get(i);
      if (tmpVar.getValue().equalsIgnoreCase(value)) {
        return i;
      }
    }

    return -1;
  }

  /**
   * Returns the name corresponding to the specified variable value.
   *
   * @param value specified variable value
   * @return the name corresponding to the specified variable value
   */
  public String getName(String value) {
    int index = indexOfValue(value);
    if (index < 0) {
      return null;
    }
    return this.variables.get(index).getName();
  }

  /**
   * Returns the value corresponding to the specified variable name.
   *
   * @param name specified variable name
   * @return the value corresponding to the specified variable name
   */
  public String getValue(String name) {
    int index = indexOfName(name);
    if (index < 0) {
      return null;
    }
    return this.variables.get(index).getValue();
  }

  /**
   * Create a new variable in the config file.
   *
   * @param name variable name
   * @param value variable value
   * @return
   *     true if variable did not exist before and does now,
   *     otherwise false
   */
  private boolean createVariable(String name) {
    if (MainTools.isEmpty(name)) {
      LOGGER.warn(Debugging.emptyString());
      return false;
    }

    /* Test if variable already exists. */
    if (indexOfName(name) >= 0) {
      return true;
    }

    /* Add complete variable string to memory file. */
    this.memoryFile.getLines().add(
        name
        + " " + ConfigFile.VARIABLE_DELIMITER + " "
    );

    /* Update changes in file. */
    return this.memoryFile.writeToDisk() && refresh();
  }

  /**
   * Sets the specified variable's value and writes changes to disk.
   *
   * @param name specified variable name
   * @param value specified value of variable
   * @return
   *     true if variable exists, its value has been set, and written to disk,
   *     otherwise false
   */
  public boolean setVariable(String name, String value) {
    if (!createVariable(name)) {
      LOGGER.warn("failed to create variable: ");
      return false;
    }
    if (value == null) {
      value = "";
    }

    ArrayList<String> lines = this.memoryFile.getLines();
    String line;
    String comment;
    boolean writeToFile = false;
    int len = lines.size();
    int index;

    /* Find variable and set its value. */
    for (int i = 0; i < len; i++) {
      line = lines.get(i);

      comment = "";
      index = line.indexOf(COMMENT_DELIMITER);
      if (index >= 0) {
        comment = line.substring(index, line.length());
      }

      if (line.trim().toLowerCase().startsWith(name) && line.contains(VARIABLE_DELIMITER)) {
        line = name + " " + VARIABLE_DELIMITER + " " + value + " " + comment;
        this.memoryFile.getLines().set(i, line);
        writeToFile = true;
      }
    }

    /* Update changes in file. */
    if (writeToFile) {
      return (this.memoryFile.writeToDisk() && refresh());
    }

    /* If this line is reached, something went wrong. */
    LOGGER.warn("unable to set: " + name + " " + VARIABLE_DELIMITER + " " + value);
    return false;
  }

  /**
   * Enables the specifiedd variable in the config file by
   * uncommenting the line.
   *
   * @param name specified variable to enable
   * @return
   *     true if variable is enabled,
   *     otherwise false
   */
  public boolean enableVariable(String name) {
    if (indexOfName(name) >= 0) {
      /* Variable is already enabled. */
      return true;
    }

    ArrayList<String> lines = this.memoryFile.getLines();
    String line;
    String currentLine = null;
    int currentIndex = -1;
    int len = lines.size();
    int index;

    /* Find the most current line matching variable name. */
    for (int i = 0; i < len; i++) {
      line = lines.get(i);
      if (line.trim().toLowerCase().startsWith(COMMENT_DELIMITER)
          && line.contains(name + " ")
          && line.contains(VARIABLE_DELIMITER)) {
        currentLine = line;
        currentIndex = i;
      }
    }

    if (currentLine == null) {
      /* Unable to find variable. */
      return false;
    }

    index = currentLine.indexOf(COMMENT_DELIMITER);
    currentLine = currentLine.substring(index + 1, currentLine.length()).trim();
    this.memoryFile.getLines().set(currentIndex, currentLine);

    return (this.memoryFile.writeToDisk() && refresh());
  }

  /**
   * Disables the specified variable in the config file by
   * commenting the line.
   *
   * @param name specified variable to disable
   * @return
   *     true if variable is now disabled,
   *     otherwise false
   */
  public boolean disableVariable(String name) {
    if (indexOfName(name) < 0) {
      /* Variable is already disabled. */
      return true;
    }

    String line;
    ArrayList<String> lines = this.memoryFile.getLines();
    int len = lines.size();

    for (int i = 0; i < len; i++) {
      line = lines.get(i);
      if (line.trim().toLowerCase().startsWith(name)
          && line.contains(VARIABLE_DELIMITER)) {
        line = COMMENT_DELIMITER + " " + line;
        this.memoryFile.getLines().set(i, line);
        return (this.memoryFile.writeToDisk() && refresh());
      }
    }

    /* If this line is reached, variable was not found in the config. */
    return false;
  }

}
