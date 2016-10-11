/* ConfigFile.java */

package wildlauncher.config;

import wildlauncher.tools.MainTools;
import wildlauncher.tools.MemoryFile;
import wildlauncher.tools.TokenArray;

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
  private static final boolean CLASS_DEBUG = (MainTools.DEBUG && true);

  public static final String FILE_EXTENSION = ".cfg";
  public static final String VARIABLE_DELIMITER = "=";
  public static final String COMMENT_DELIMITER = ";";

  private MemoryFile _memoryFile;
  private ArrayList<ConfigVariable> _variables;

  public ConfigFile() {
    _memoryFile = new MemoryFile();
    _variables = new ArrayList<>();
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
        System.out.println("Read variable: " + varName + " " + VARIABLE_DELIMITER + " " + varValue);
      }
    }

    return true;
  }

  /**
   * Returns the index of the specified variable name in the variables
   * list.
   *
   * @param name specified variable name
   */
  public int indexOf(String name) {
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
   * Returns the value corresponding to the specified variable name.
   *
   * @param name specified variable name
   */
  public String getValue(String name) {
    int index = indexOf(name);
    if (index < 0) {
      return null;
    }
    return _variables.get(index).getValue();
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
    int varIndex = indexOf(name);
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

}
