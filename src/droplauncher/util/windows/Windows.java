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

  public static Path WINDOWS_DIR = Paths.get("C:\\Windows");

  public static Path CMD_EXE = Paths.get("C:\\Windows\\System32\\cmd.exe");
  public static String[] DEFAULT_CMD_POPUP_ARGS = {"/c", "start"};

  public static Path TASKLIST_EXE = Paths.get("C:\\Windows\\System32\\tasklist.exe");
  public static String[] DEFAULT_TASKLIST_ARGS = {"/v"};

  public static Path TASKKILL_EXE = Paths.get("C:\\Windows\\System32\\taskkill.exe");
  public static String[] DEFAULT_TASKKILL_ARGS = {"/f", "/pid"}; /* /PID requires a second string */

  private Windows() {}

}
