package droplauncher.exception;

/**
 * Exception class for invalid or unrecognized bot types.
 */
public class InvalidBotTypeException extends Exception {

  public InvalidBotTypeException() {
    super();
  }

  public InvalidBotTypeException(String message) {
    super(message);
  }

}
