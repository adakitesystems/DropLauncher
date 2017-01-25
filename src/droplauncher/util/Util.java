package droplauncher.util;

import adakite.debugging.Debugging;
import java.io.File;
import java.util.ArrayList;
import java.util.logging.Logger;

public class Util {

  private static final Logger LOGGER = Logger.getLogger(Util.class.getName());
  private static final boolean CLASS_DEBUG = (Constants.DEBUG && true);

  private Util() {}

  public static String getFileExtension(File file) {
    if (file == null) {
      return null;
    }

    String ret = file.getAbsolutePath();
    int index = ret.lastIndexOf(".");
    if (index < 0) {
      return null;
    }
    ret = ret.substring(index + 1, ret.length());

    return ret;
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
    if (len < 1) {
      LOGGER.log(Debugging.getLogLevel(), Debugging.nullObject("null or empty list"));
      return null;
    }
    String[] arr = new String[len];
    for (int i = 0; i < len; i++) {
      arr[i] = arrlist.get(i);
    }
    return arr;
  }

}
