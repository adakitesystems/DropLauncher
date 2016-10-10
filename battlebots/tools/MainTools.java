/* MainTools.java */

package battlebots.tools;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Useful variables and functions.
 *
 * @author Adakite Systems
 * @author adakitesystems@gmail.com
 */
public class MainTools {

  private static final MainTools INSTANCE = new MainTools();

  /* ************************************************************ */
  /* Debugging variables */
  /* ************************************************************ */
  private static final Logger  LOGGER = Logger.getLogger(MainTools.class.getName());
  public  static final boolean DEBUG = true;
  private static final boolean DEBUG_LOG_FILE_ENABLED = false;
  private static final String  DEBUG_LOG_FILENAME = "errorlog.xml";
  private static final boolean CLASS_DEBUG = (DEBUG && true);
  /* Predefined logger messages */
  public static final String EMPTY_STRING = "null or empty string";
  public static final String NULL_OBJECT = "null object";
  public static final String INDEX_OOB = "index out of range";
  /* ************************************************************ */

  private MainTools() {
    /* Create log file. */
    if (DEBUG_LOG_FILE_ENABLED) {
      try {
        Handler handler = new FileHandler(DEBUG_LOG_FILENAME);
        Logger.getLogger("").addHandler(handler);
      } catch (IOException | SecurityException ex) {
        if (CLASS_DEBUG) {
          LOGGER.log(
              Level.WARNING,
              "encountered while creating log file: " + DEBUG_LOG_FILENAME, ex
          );
        }
      }
    }
  }

  /**
   * Checks if the specified string is null or String.length() is less than 1.
   *
   * @param str string to check
   * @return true if string is null or empty, otherwise false
   */
  public static boolean isEmpty(String str) {
    boolean status = (str == null || str.length() < 1);
    /* A null string may be intended. An empty string may not be. */
    if (CLASS_DEBUG && str != null && str.isEmpty()) {
      LOGGER.log(Level.WARNING, "non-null empty string detected");
    }
    return status;
  }

  /**
   * Returns the String[] version of the specified ArrayList object.
   *
   * @param arrlist specified ArrayList object
   * @return
   *     String[] object if specified ArrayList is not null or empty,
   *     otherwise null
   */
  public static String[] toStringArray(ArrayList<String> arrlist) {
    int len = (arrlist == null) ? 0 : arrlist.size();

    /* Validate parameters. */
    if (len < 1) {
      if (CLASS_DEBUG) {
        /* Possibly just empty and not null. However, the result is the same. */
        LOGGER.log(Level.WARNING, NULL_OBJECT);
      }
      return null;
    }

    String[] arr = new String[len];
    for (int i = 0; i < len; i++) {
      arr[i] = arrlist.get(i);
    }

    return arr;
  }

  /**
   * Check if specified file exists and is not a directory.
   *
   * @param path path to file
   * @return
   *     true if specified path is a file,
   *     otherwise false
   */
  public static boolean doesFileExist(String path) {
    /* Validate parameters. */
    if (isEmpty(path)) {
      if (CLASS_DEBUG) {
        LOGGER.log(Level.WARNING, EMPTY_STRING);
      }
      return false;
    }

    try {
      File file = new File(path);
      if (file.exists() && file.isFile() && !file.isDirectory()) {
        return true;
      }
    } catch (Exception ex) {
      if (CLASS_DEBUG) {
        LOGGER.log(Level.SEVERE, "encountered while checking file existence", ex);
      }
      return false;
    }

    return false;
  }

  /**
   * Check if specified directory exists.
   *
   * @param path path to directory
   * @return
   *     true if specified path is a directory,
   *     otherwise false
   */
  public static boolean doesDirectoryExist(String path) {
    /* Validate parameters. */
    if (isEmpty(path)) {
      if (CLASS_DEBUG) {
        LOGGER.log(Level.WARNING, EMPTY_STRING);
      }
      return false;
    }

    try {
      File dir = new File(path);
      if (dir != null && dir.exists() && dir.isDirectory()) {
        return true;
      }
    } catch (Exception ex) {
      if (CLASS_DEBUG) {
        LOGGER.log(Level.SEVERE, "encountered while checking directory existence", ex);
      }
      return false;
    }

    return false;
  }

  /**
   * Return the specified filename excluding the last period character and
   * all characters after it.
   *
   * @param filename specified filename
   */
  public static String getFilenameNoExt(String filename) {
    /* Validate parameters. */
    if (isEmpty(filename)) {
      if (CLASS_DEBUG) {
        LOGGER.log(Level.WARNING, EMPTY_STRING);
      }
      return null;
    }

    int extIndex = filename.lastIndexOf(".");

    return (extIndex > 0) ? filename.substring(0, extIndex) : filename;
  }

  /**
   * Returns the parent directory of the specified path.
   *
   * @param path specified path
   * @return
   *     the parent directory of the specified path if parent exists,
   *     otherwise null
   */
  public static String getParentDirectory(String path) {
    /* Validate parameters. */
    if (isEmpty(path)) {
      if (CLASS_DEBUG) {
        LOGGER.log(Level.WARNING, EMPTY_STRING);
      }
      return null;
    }

    try {
      String parent = null;
      File file = new File(path);

      parent = file.getParent();

      return parent;
    } catch (Exception ex) {
      if (CLASS_DEBUG) {
        LOGGER.log(Level.WARNING, null, ex);
      }
      return null;
    }
  }

  /**
   * Add double quotations to the beginning and end of the
   * specified string.
   *
   * @param str specified string
   * @return
   *     the specified string encapsulated with quotation,
   *     otherwise null if the string is null or empty
   */
  public static String addQuotations(String str) {
    /* Validate parameters. */
    if (isEmpty(str)) {
      if (CLASS_DEBUG) {
        LOGGER.log(Level.WARNING, EMPTY_STRING);
      }
      return null;
    }

    if (!str.startsWith("\"")) {
      str = "\"" + str;
    }
    if (!str.endsWith("\"")) {
      str += "\"";
    }

    return str;
  }

  /**
   * Remove double quotations from the beginning and end of the
   * specified string.
   *
   * @param str specified string
   * @return
   *     the specified string without quotations,
   *     otherwise null if the string is null or empty
   */
  public static String removeQuotations(String str) {
    /* Validate parameters. */
    if (isEmpty(str)) {
      if (CLASS_DEBUG) {
        LOGGER.log(Level.WARNING, EMPTY_STRING);
      }
      return null;
    }

    if (str.startsWith("\"")) {
      str = str.substring(1, str.length());
    }
    if (str.endsWith("\"")) {
      str = str.substring(0, str.length() - 1);
    }

    return str;
  }

  public static String onlyLettersNumbers(String str) {
    /* Validate parameters. */
    if (isEmpty(str)) {
      if (CLASS_DEBUG) {
        LOGGER.log(Level.WARNING, EMPTY_STRING);
      }
      return null;
    }

    String newStr = "";
    char c;
    int len = str.length();

    for (int i = 0; i < len; i++) {
      c = str.charAt(i);
      if (c == ' ' || c == '(' || c == ')'
          || (c >= 'A' && c <= 'Z')
          || (c >= 'a' && c <= 'z')
          || (c >= '0' && c <= '9')) {
        newStr += c;
      }
    }

    if (newStr.length() < 1) {
      newStr = null;
    }

    return newStr;
  }

}
