package adakite.exception;

/**
 * Checked version of {@link java.lang.IllegalStateException}.
 */
public class InvalidStateException extends Exception {

  public InvalidStateException() {
    super();
  }

  public InvalidStateException(String message) {
    super(message);
  }

}
