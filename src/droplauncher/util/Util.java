package droplauncher.util;

import java.io.File;

public class Util {

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

}
