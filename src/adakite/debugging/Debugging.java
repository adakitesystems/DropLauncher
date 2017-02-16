package adakite.debugging;

import adakite.util.AdakiteUtils;
import java.nio.file.Path;
import java.util.logging.Level;

/**
 * Debugging utilities class mainly for debugging and logging messages or
 * anything related to debugging and logging.
 */
public class Debugging {

  /* Predefined logger messages */
  private static final String EMPTY_STRING = "null or empty string";
  private static final String ACK = "ack";
  private static final String NULL_OBJECT = "null object";
  private static final String INDEX_OOB = "index out of bounds";
  private static final String FILE_DOES_NOT_EXIST = "file inaccessible or does not exist";
  private static final String FILE_ALREADY_EXISTS = "file already exists";
  private static final String OPEN_FAIL = "open failed";
  private static final String CREATE_FAIL = "create failed";
  private static final String DELETE_FAIL = "delete failed";
  private static final String OPERATION_FAIL = "operation failed";
  private static final String APPEND_FAIL = "append failed";
  private static final String CANNOT_BE_NULL = "cannot be null";
  private static final String CANNOT_BE_NULL_OR_EMPTY = "cannot be null or empty";

  private static Level logLevel = Level.SEVERE;

  private static final int STATUS_LENGTH = 4;

  public enum Status {
    REQ, OK, FAIL
  }

  private Debugging() {}

  public static Level getLogLevel() {
    return logLevel;
  }

  public static void setLogLevel(Level level) {
    if (level == null) {
      return;
    }
    logLevel = level;
  }

  public static String emptyString() {
    return Debugging.EMPTY_STRING;
  }

  public static String emptyString(String message) {
    return Debugging.EMPTY_STRING + ": " + message;
  }

  public static String ack(String message) {
    String ret = Debugging.ACK;
    if (!AdakiteUtils.isNullOrEmpty(message)) {
      ret += ": " + message;
    }
    return ret;
  }

  public static String ack() {
    return ack(null);
  }

  public static String nullObject() {
    return Debugging.NULL_OBJECT;
  }

  public static String nullObject(String message) {
    return Debugging.NULL_OBJECT + ": " + message;
  }

  public static String indexOutOfBounds() {
    return Debugging.INDEX_OOB;
  }

  public static String fileDoesNotExist(Path path) {
    return (Debugging.FILE_DOES_NOT_EXIST + ": " + path.toAbsolutePath().toString());
  }

  public static String fileAlreadyExists(Path path) {
    return (Debugging.FILE_ALREADY_EXISTS + ": " + path.toAbsolutePath().toString());
  }

  public static String openFail(Path path) {
    return (Debugging.OPEN_FAIL + ": " + path.toAbsolutePath().toString());
  }

  public static String createFail(Path path) {
    return (Debugging.CREATE_FAIL + ": " + path.toAbsolutePath().toString());
  }

  public static String deleteFail(Path path) {
    return (Debugging.DELETE_FAIL + ": " + path.toAbsolutePath().toString());
  }

  public static String operationFail(String message) {
    String ret = Debugging.OPERATION_FAIL;
    if (!AdakiteUtils.isNullOrEmpty(message)) {
      ret += ": " + message;
    }
    return (ret);
  }

  public static String operationFail() {
    return operationFail(null);
  }

  public static String appendFail(Path path) {
    return (Debugging.APPEND_FAIL + ": " + path.toAbsolutePath().toString());
  }

  public static String cannotBeNull(String objName) {
    return (objName + " " + Debugging.CANNOT_BE_NULL);
  }

  public static String cannotBeNullOrEmpty(String objName) {
    return (objName + " " + Debugging.CANNOT_BE_NULL_OR_EMPTY);
  }

  public static String status(Status status, String message) {
    return AdakiteUtils.pad(status.toString(), STATUS_LENGTH) + " " + message;
  }

}
