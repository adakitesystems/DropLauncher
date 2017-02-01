package droplauncher.util.windows;

import java.nio.file.Path;
import java.nio.file.Paths;

public class Windows {

  public static Path TASKLIST_EXE = Paths.get("C:\\Windows\\System32\\tasklist.exe");
  public static String[] DEFAULT_TASKLIST_ARGS = {"/v"};

  public static Path TASKKILL_EXE = Paths.get("C:\\Windows\\System32\\taskkill.exe");
  public static String[] DEFAULT_TASKKILL_ARGS = {"/f", "/pid"}; /* /PID requires a second string */

  public static Path JAVA_EXE = Paths.get("C:\\ProgramData\\Oracle\\Java\\javapath\\java.exe");
  public static String[] DEFAULT_JAR_ARGS = {"-jar"};

  private Windows() {}

}
