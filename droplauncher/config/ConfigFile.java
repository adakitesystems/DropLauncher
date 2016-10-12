/* ConfigFile.java */

package droplauncher.config;

import droplauncher.tools.MainTools;
import droplauncher.tools.MemoryFile;
import droplauncher.tools.TokenArray;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class for handling configuration files.
 *
 * @author Adakite Systems
 * @author adakitesystems@gmail.com
 */
public class ConfigFile {

  private static final Logger LOGGER = Logger.getLogger(ConfigFile.class.getName());
  private static final boolean CLASS_DEBUG = (MainTools.DEBUG && false);

  public static final String FILE_EXTENSION = ".cfg";
  public static final String VARIABLE_DELIMITER = "=";
  public static final String COMMENT_DELIMITER = ";";

  private String _filename;
  private MemoryFile _memoryFile;
  private ArrayList<ConfigVariable> _variables;

  /**
   * Initialize class variables.
   */
  public ConfigFile() {
    _filename = null;
    _memoryFile = new MemoryFile();
    _variables = new ArrayList<>();
  }

  /**
   * Create a file if it does not already exist.
   *
   * @param filename path to file
   * @return
   *    true if file was created successfully and did not already exist,
   *    otherwise false
   */
  public boolean create(String filename) {
    if (MainTools.isEmpty(filename)) {
      if (CLASS_DEBUG) {
        LOGGER.log(Level.WARNING, MainTools.EMPTY_STRING);
      }
      return false;
    } else if (MainTools.doesFileExist(filename)) {
      if (CLASS_DEBUG) {
        LOGGER.log(Level.WARNING, "file already exists: " + filename);
      }
      return false;
    }

    File file = new File(filename);
    String parentDir = file.getParent();
    if (parentDir != null && !MainTools.doesDirectoryExist(parentDir)) {
      file.mkdirs();
    }
    try {
      boolean status = file.createNewFile();
      if (status && open(filename)) {
        _filename = filename;
      }
      return status;
    } catch (IOException ex) {
      if (CLASS_DEBUG) {
        LOGGER.log(Level.SEVERE, null, ex);
      }
      return false;
    }
  }

  /**
   * Opens the configuration file and read in its variables.
   *
   * @param filename specified configuration file to read
   * @return
   *     true if file has been opened successfully and contains
   *         valid variables,
   *     otherwise false
   */
  public boolean open(String filename) {
    if (!_memoryFile.readIntoMemory(filename)) {
      return false;
    }
    _variables.clear();

    TokenArray lines = _memoryFile.getLines();
    int len = lines.size();
    String line;
    String varName;
    String varValue;
    int index;

    /* Read was successful but file is empty. */
    if (len < 1) {
      return false;
    }

    /* Read lines as variables and values. */
    for (int i = 0; i < len; i++) {
      line = lines.get(i);
      line = line.trim();

      /* Ignore comments. */
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

      _variables.add(new ConfigVariable(varName, varValue));

      if (CLASS_DEBUG) {
        System.out.println(
            "Read variable: " + filename + ": "
            + varName + " " + VARIABLE_DELIMITER + " " + varValue
        );
      }
    }

    _filename = filename;

    return true;
  }

  /**
   * Returns the index of the specified variable name.
   *
   * @param name specified variable name
   */
  public int indexOfName(String name) {
    /* Validate parameters. */
    if (MainTools.isEmpty(name)) {
      if (CLASS_DEBUG) {
        LOGGER.log(Level.WARNING, MainTools.EMPTY_STRING);
      }
      return -1;
    }

    int len = _variables.size();
    ConfigVariable tmpVar;

    for (int i = 0; i < len ; i++) {
      tmpVar = _variables.get(i);
      if (tmpVar.getName().equals(name)) {
        return i;
      }
    }

    return -1;
  }

  /**
   * Returns the index of the specified value.
   *
   * @param value specified value
   */
  public int indexOfValue(String value) {
    /* Validate parameters. */
    if (MainTools.isEmpty(value)) {
      if (CLASS_DEBUG) {
        LOGGER.log(Level.WARNING, MainTools.EMPTY_STRING);
      }
      return -1;
    }

    int len = _variables.size();
    ConfigVariable tmpVar;

    for (int i = 0; i < len ; i++) {
      tmpVar = _variables.get(i);
      if (tmpVar.getValue().equals(value)) {
        return i;
      }
    }

    return -1;
  }

  /**
   * Returns the value corresponding to the specified variable name.
   *
   * @param name specified variable name
   */
  public String getValue(String name) {
    int index = indexOfName(name);
    if (index < 0) {
      return null;
    }
    return _variables.get(index).getValue();
  }

  public boolean createVariable(String name, String value) {
    if (indexOfName(name) >= 0)  {
      return false;
    }
    _memoryFile.getLines().add(
        name
        + " " + ConfigFile.VARIABLE_DELIMITER + " "
        + value
    );
    System.out.println(_memoryFile.getLines().get(_memoryFile.getLines().size() - 1));
    return _memoryFile.writeToDisk();
  }

  /**
   * Sets the specified variable's value and writes changes to disk.
   *
   * @param name specified variable name
   * @param value value of variable
   * @return
   *     true if variable exists, its value has been set, and written to disk,
   *     otherwise false
   */
  public boolean setVariable(String name, String value) {
    int varIndex = indexOfName(name);
    if (varIndex < 0) {
      return false;
    }

    int index;
    TokenArray lines = _memoryFile.getLines();
    String line;
    String comment;
    boolean writeToFile = false;
    boolean writeSuccess;

    int len = lines.size();
    for (int i = 0; i < len; i++) {
      line = lines.get(i);

      index = line.indexOf(COMMENT_DELIMITER);
      comment = "";
      if (index >= 0) {
        comment = line.substring(index, line.length());
      }

      if (line.trim().startsWith(name) && line.contains(VARIABLE_DELIMITER)) {
        line = name + " " + VARIABLE_DELIMITER + " " + value + " " + comment;
        _memoryFile.getLines().set(i, line);
        writeToFile = true;
      }
    }

    if (writeToFile) {
      writeSuccess = _memoryFile.writeToDisk();
      if (!writeSuccess) {
        return false;
      }
      _variables.set(varIndex, new ConfigVariable(name, value));
      return true;
    }

    return false;
  }

  /**
   * Enable a variable in the config file by uncommenting the line.
   *
   * @param name specified variable to enable
   * @return
   *     true if variable is now enable if not previously,
   *     otherwise false
   */
  public boolean enableVariable(String name) {
    int index = indexOfName(name);
    if (index >= 0) {
      /* Variable is already enabled. */
      return true;
    }

    TokenArray lines = _memoryFile.getLines();
    String line;
    String currentLine = null;
    int currentIndex = -1;
    int len = lines.size();

    /* Find the most current line. */
    for (int i = 0; i < len; i++) {
      line = lines.get(i);
      if (line.trim().startsWith(COMMENT_DELIMITER)
          && line.contains(name + " ")
          && line.contains(VARIABLE_DELIMITER)) {
        currentLine = line;
        currentIndex = i;
      }
    }

    if (currentLine == null) {
      return false;
    }

    index = currentLine.indexOf(COMMENT_DELIMITER);
    currentLine = currentLine.substring(index + 1, currentLine.length()).trim();
    _memoryFile.getLines().set(currentIndex, currentLine);

    return (_memoryFile.writeToDisk() && open(_filename));
  }

  /**
   * Disable a variable in the config file by commenting the line.
   *
   * @param name specified variable to disable
   * @return
   *     true if variable is now disabled if not previously,
   *     otherwise false
   */
  public boolean disableVariable(String name) {
    if (indexOfName(name) < 0) {
      /* Variable is already disabled. */
      return true;
    }

    String line;
    TokenArray lines = _memoryFile.getLines();
    int len = lines.size();

    for (int i = 0; i < len; i++) {
      line = lines.get(i);
      if (line.trim().startsWith(name)
          && line.contains(VARIABLE_DELIMITER)) {
        line = COMMENT_DELIMITER + " " + line;
        _memoryFile.getLines().set(i, line);
        return (_memoryFile.writeToDisk() && open(_filename));
      }
    }

    /* If this line is reached, variable was not found in the config. */
    return false;
  }

}
