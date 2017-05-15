package adakite.debugging;

import adakite.util.AdakiteUtils;
import java.nio.file.Path;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Debugging utilities class for anything related to debugging and logging.
 */
public class Debugging {

  private enum Message {

    EMPTY_STRING("null or empty string"),
    ACK("ack"),
    NULL_OBJECT("null object"),
    FILE_DOES_NOT_EXIST("file inaccessible or does not exist"),
    FILE_ALREADY_EXISTS("file already exists"),
    OPEN_FAIL("open failed"),
    CREATE_FAIL("create failed"),
    DELETE_FAIL("delete failed"),
    OPERATION_FAIL("operation failed"),
    APPEND_FAIL("append failed"),
    CANNOT_BE_NULL("cannot be null"),
    CANNOT_BE_NULL_OR_EMPTY("cannot be null or empty"),
    VALUE_NOT_SET_FOR_KEY("value not set for key")
    ;

    private final String str;

    private Message(String str) {
      this.str = str;
    }

    @Override
    public String toString() {
      return this.str;
    }

  }

  private static final Logger LOGGER = Logger.getLogger(Debugging.class.getName());

  private static Level logLevel = Level.SEVERE;

  private Debugging() {}

  public static Level getLogLevel() {
    return logLevel;
  }

  public static void setLogLevel(Level level) {
    if (level == null) {
      throw new IllegalArgumentException(Debugging.nullObject());
    }
    logLevel = level;
  }

  public static String emptyString() {
    return Debugging.Message.EMPTY_STRING.toString();
  }

  public static String emptyString(String message) {
    return Debugging.Message.EMPTY_STRING.toString() + ": " + message;
  }

  public static String ack(String message) {
    String ret = Debugging.Message.ACK.toString();
    if (!AdakiteUtils.isNullOrEmpty(message)) {
      ret += ": " + message;
    }
    return ret;
  }

  public static String ack() {
    return ack(null);
  }

  public static String nullObject() {
    return Debugging.Message.NULL_OBJECT.toString();
  }

  public static String nullObject(String message) {
    return Debugging.Message.NULL_OBJECT.toString() + ": " + message;
  }

  public static String fileDoesNotExist(Path path) {
    return (Debugging.Message.FILE_DOES_NOT_EXIST.toString() + ": " + path.toAbsolutePath().toString());
  }

  public static String fileAlreadyExists(Path path) {
    return (Debugging.Message.FILE_ALREADY_EXISTS.toString() + ": " + path.toAbsolutePath().toString());
  }

  public static String openFail(Path path) {
    return (Debugging.Message.OPEN_FAIL.toString() + ": " + path.toAbsolutePath().toString());
  }

  public static String createFail(Path path) {
    return (Debugging.Message.CREATE_FAIL.toString() + ": " + path.toAbsolutePath().toString());
  }

  public static String deleteFail(Path path) {
    return (Debugging.Message.DELETE_FAIL.toString() + ": " + path.toAbsolutePath().toString());
  }

  public static String operationFail(String message) {
    String ret = Debugging.Message.OPERATION_FAIL.toString();
    if (!AdakiteUtils.isNullOrEmpty(message)) {
      ret += ": " + message;
    }
    return ret;
  }

  public static String operationFail() {
    return operationFail(null);
  }

  public static String appendFail(Path path) {
    return (Debugging.Message.APPEND_FAIL.toString() + ": " + path.toAbsolutePath().toString());
  }

  public static String cannotBeNull(String objName) {
    return (objName + " " + Debugging.Message.CANNOT_BE_NULL.toString());
  }

  public static String cannotBeNullOrEmpty(String objName) {
    return (objName + " " + Debugging.Message.CANNOT_BE_NULL_OR_EMPTY.toString());
  }

  public static String valueNotSetForKey() {
    return Debugging.Message.VALUE_NOT_SET_FOR_KEY.toString();
  }

  public static String valueNotSetForKey(String key) {
    return (Debugging.Message.VALUE_NOT_SET_FOR_KEY.toString() + ": " + key);
  }

}
