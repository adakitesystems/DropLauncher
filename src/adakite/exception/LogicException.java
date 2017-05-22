package adakite.exception;

/**
 * Unchecked exception for indicating a logic error in which the
 * developer is at fault.
 */
public class LogicException extends RuntimeException {

  public LogicException() {
    super();
  }

  public LogicException(String message) {
    super(message);
  }

}
