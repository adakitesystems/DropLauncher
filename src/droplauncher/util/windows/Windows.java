package droplauncher.util.windows;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Constants class for Windows-related programs and variables.
 */
public class Windows {

  public enum FileType {
    EXE, DLL
  }

  public enum Program {

    CMD("C:\\Windows\\System32\\cmd.exe"),
    TASKLIST("C:\\Windows\\System32\\tasklist.exe"),
    TASKKILL("C:\\Windows\\System32\\taskkill.exe");
    ;

    private String str;

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
        default: return new String[]{};
      }
    }

    @Override
    public String toString() {
      return this.str;
    }

  }

  private Windows() {}

}
