package droplauncher.util.windows;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Constants class for Windows-related programs and variables.
 */
public class Windows {

  public static Path WINDOWS_DIR = Paths.get("C:\\Windows");

  public static Path CMD_EXE = Paths.get("C:\\Windows\\System32\\cmd.exe");

  public static Path TASKLIST_EXE = Paths.get("C:\\Windows\\System32\\tasklist.exe");
  public static String[] DEFAULT_TASKLIST_ARGS = {"/v"};

  public static Path TASKKILL_EXE = Paths.get("C:\\Windows\\System32\\taskkill.exe");
  public static String[] DEFAULT_TASKKILL_ARGS = {"/f", "/pid"}; /* /PID requires a second string */

  public static Path JAVA_EXE = Paths.get("C:\\ProgramData\\Oracle\\Java\\javapath\\java.exe");
  public static String[] DEFAULT_JAR_ARGS = {"-jar"};

  public static Path PROGRAM_FILES_DIR = Paths.get("C:\\Program Files");
  public static Path PROGRAM_FILES_DIR_32 = Paths.get("C:\\Program Files (x86)");

  private Windows() {}

}
