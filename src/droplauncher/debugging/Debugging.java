/* Debugging.java */

package droplauncher.debugging;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Debugging {

  /* ************************************************************ */
  /* Debugging */
  /* ************************************************************ */
  public  static final boolean DEBUG = true;
  private static final boolean DEBUG_LOG_FILE_ENABLED = false;
  private static final String  DEBUG_LOG_FILENAME = "errorlog.xml";
  private static final String  CLASS_NAME = Debugging.class.getName();
  private static final boolean CLASS_DEBUG = (DEBUG && true);
  private static final Logger  LOGGER = Logger.getLogger(CLASS_NAME);
  /* Predefined logger messages */
  public static final String EMPTY_STRING = "null or empty string";
  public static final String NULL_OBJECT = "null object";
  public static final String INDEX_OOB = "index out of range";
  /* ************************************************************ */

  private Debugging() {
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

}
