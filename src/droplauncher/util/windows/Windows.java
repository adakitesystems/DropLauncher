package droplauncher.util.windows;

import adakite.utils.AdakiteUtils;
import java.nio.file.Path;
import java.nio.file.Paths;

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

  public static boolean checkSystem(SystemProperty property) {
    switch (property) {
      case BIT_ARCH_32:
        return !AdakiteUtils.directoryExists(PROGRAM_FILES_DIR_32);
      case BIT_ARCH_64:
        return AdakiteUtils.directoryExists(PROGRAM_FILES_DIR_32);
      case JAVA_PATH_FOUND:
        return AdakiteUtils.fileExists(JAVA_EXE);
      case WINDOWS:
        return AdakiteUtils.directoryExists(WINDOWS_DIR);
      default:
        return false;
    }
  }

}
