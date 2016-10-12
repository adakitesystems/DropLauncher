/* MainTools.java */

package droplauncher.tools;

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
   * Tests whether the specified string is null or
   * {@link java.lang.String#length()} is less than 1.
   *
   * @param str the string to check
   * @return
   *     true if string is null or empty,
   *     otherwise false
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
   * Tests whether the specified file exists.
   *
   * @param path the path to the file
   * @return
   *     true if the file exists,
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

    File file = new File(path);

    return (file.exists() && file.isFile());
  }

  /**
   * Tests whether the specified directory exists.
   *
   * @param path path to the directory
   * @return
   *     true if directory exists,
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

    File dir = new File(path);

    return (dir.exists() && dir.isDirectory());
  }

  /**
   * Return the specified filename excluding the last period character and
   * all characters after it.
   *
   * @param filename specified filename
   *
   * @return
   *     the filename excluding the extension if the specified filename
   *         is not null or empty,
   *     otherwise null
   */
  public static String getFilenameNoExt(String filename) {
    /* Validate parameters. */
    if (isEmpty(filename)) {
      if (CLASS_DEBUG) {
        LOGGER.log(Level.WARNING, EMPTY_STRING);
      }
      return null;
    }

    int index = filename.lastIndexOf(".");

    return (index <= 0) ? null : filename.substring(0, index);
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

    File file = new File(path);

    return file.getParent();
  }

  /**
   * Ensures the specified string is encapsulated with double quotations.
   *
   * @param str specified string
   * @return
   *     the specified string encapsulated with quotations,
   *     otherwise null if the string is null or empty
   */
  public static String ensureQuotations(String str) {
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

    while (str.startsWith("\"")) {
      str = str.substring(1, str.length());
    }
    while (str.endsWith("\"")) {
      str = str.substring(0, str.length() - 1);
    }

    return str;
  }

  /**
   * Returns a string stripped of non-standard ASCII letter and
   * number characters. Parenthesis are allowed.
   *
   * @param str string to strip
   * @return
   *     the stripped string if specified string is not null or empty,
   *     otherwise null
   */
  public static String onlyLettersNumbers(String str) {
    /* Validate parameters. */
    if (isEmpty(str)) {
      if (CLASS_DEBUG) {
        LOGGER.log(Level.WARNING, EMPTY_STRING);
      }
      return null;
    }

    String newStr = "";
    char ch;
    int len = str.length();

    for (int i = 0; i < len; i++) {
      ch = str.charAt(i);
      if (ch == ' ' || ch == '(' || ch == ')'
          || (ch >= 'A' && ch <= 'Z')
          || (ch >= 'a' && ch <= 'z')
          || (ch >= '0' && ch <= '9')) {
        newStr += ch;
      }
    }

    if (newStr.length() < 1) {
      newStr = null;
    }

    return newStr;
  }

}
