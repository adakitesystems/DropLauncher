package droplauncher.util;

import adakite.debugging.Debugging;
import java.util.ArrayList;
import java.util.logging.Logger;

/**
 * Utilities and constants class for DropLauncher.
 */
public class Util {

  private static final Logger LOGGER = Logger.getLogger(Util.class.getName());
  private static final boolean CLASS_DEBUG = (Constants.DEBUG && true);

  private Util() {}

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
    if (len < 1) {
      if (CLASS_DEBUG) {
        LOGGER.log(Constants.DEFAULT_LOG_LEVEL, Debugging.nullObject("null or empty list"));
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
