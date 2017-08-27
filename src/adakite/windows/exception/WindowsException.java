package adakite.windows.exception;

public class WindowsException extends Exception {

  public static enum SystemError {
    ERROR_MOD_NOT_FOUND(126, "The specified module could not be found. System Error 126 (0x7E)"),
    ERROR_ELEVATION_REQUIRED(740, "The requested operation requires elevation. System Error 740 (0x2E4)")
    ;

    private final int val;
    private final String description;

    private SystemError(int val, String description) {
      this.val = val;
      this.description = description;
    }

    public int intVal() {
      return this.val;
    }

    @Override
    public String toString() {
      return this.description;
    }
  }

  public WindowsException() {
    super();
  }

  public WindowsException(String message) {
    super(message);
  }

}
