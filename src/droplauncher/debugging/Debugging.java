/* Debugging.java */

package droplauncher.debugging;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;

public class Debugging {

  private static final Logger LOGGER = LogManager.getRootLogger();

  /* Predefined logger messages */
  public static final String EMPTY_STRING = "null or empty string";
  public static final String NULL_OBJECT = "null object";
  public static final String INDEX_OOB = "index out of bounds";
  public static final String FILE_DOES_NOT_EXIST = "file inaccessible or does not exist";
  public static final String FILE_ALREADY_EXISTS = "file already exists";
  public static final String OPEN_FAIL = "open failed";

  public static String emptyString() {
    return Debugging.EMPTY_STRING;
  }

  public static String nullObject() {
    return Debugging.NULL_OBJECT;
  }

  public static String indexOutOfBounds() {
    return Debugging.INDEX_OOB;
  }

  public static String fileDoesNotExist(File file) {
    return (Debugging.FILE_DOES_NOT_EXIST + ": " + file.getAbsolutePath());
  }

  public static String fileAlreadyExists(File file) {
    return (Debugging.FILE_ALREADY_EXISTS + ": " + file.getAbsolutePath());
  }

  public static String openFail(File file) {
    return (Debugging.OPEN_FAIL + ": " + file.getAbsolutePath());
  }

}
