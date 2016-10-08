/* MainTools.java */

package battlebots.tools;

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
  public  static final boolean DEBUG = true;
  private static final boolean DEBUG_LOG_FILE_ENABLED = false;
  private static final String  DEBUG_LOG_FILENAME = "errorlog.xml";
  private static final Logger  LOGGER = Logger.getLogger(MainTools.class.getName());
  /* Predefined logger messages */
  public static final String EMPTY_STRING = "null or empty string";
  public static final String NULL_OBJECT = "null object";
  public static final String INDEX_OOB = "index out of range";
  /* ************************************************************ */

  private MainTools() {
    /* Create log file. */
    if (MainTools.DEBUG_LOG_FILE_ENABLED) {
      try {
        Handler handler = new FileHandler(DEBUG_LOG_FILENAME);
        Logger.getLogger("").addHandler(handler);
      } catch (IOException | SecurityException ex) {
        if (MainTools.DEBUG) {
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
    if (MainTools.DEBUG && str != null && str.isEmpty()) {
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
      if (MainTools.DEBUG) {
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

}
