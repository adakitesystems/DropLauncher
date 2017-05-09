package adakite.util.windows;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Utilities class for Windows-related implementations.
 */
public class Windows {

  /**
   * Enum class for Windows programs and predefined arguments.
   */
  public enum Program {

    CMD("C:\\Windows\\System32\\cmd.exe"),
    TASKLIST("C:\\Windows\\System32\\tasklist.exe"),
    TASKKILL("C:\\Windows\\System32\\taskkill.exe"),
    REG("C:\\Windows\\System32\\reg.exe")
    ;

    private final String str;

    private Program(String str) {
      this.str = str;
    }

    public Path getPath() {
      return Paths.get(this.str);
    }

    public String[] getPredefinedArgs() {
      switch (this) {
        case CMD: return new String[]{"/c", "start"};
        case TASKLIST: return new String[]{"/v"};
        case TASKKILL: return new String[]{"/f", "/pid"};
        default: throw new UnsupportedOperationException("predefined args not found");
      }
    }

    @Override
    public String toString() {
      return this.str;
    }

  }

  private Windows() {}

}
