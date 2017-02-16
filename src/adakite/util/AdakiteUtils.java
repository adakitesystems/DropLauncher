package adakite.util;

import adakite.debugging.Debugging;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Optional;
import javax.swing.filechooser.FileSystemView;

public class AdakiteUtils {

  private static final String NEWLINE = System.lineSeparator();
  public static final String FILE_SEPARATOR = File.separator;

  private AdakiteUtils() {}

  /**
   * Tests whether the specified string is null or empty.
   *
   * @param str the specified string
   * @param trim whether to trim the specified string
   * @return
   *     true if string is null or the length is less than one,
   *     otherwise false
   */
  public static boolean isNullOrEmpty(String str, boolean trim) {
    if (str == null) {
      return true;
    }
    if (trim) {
      str = str.trim();
    }
    return (str.length() < 1);
  }

  /**
   * Tests whether the specified string is null or empty.
   *
   * @param str the specified string
   * @return
   *     true if string is null or the length is less than one,
   *     otherwise false
   */
  public static boolean isNullOrEmpty(String str) {
    return (str == null || str.length() < 1);
  }

  /**
   * Returns the integer value of the specified string.
   *
   * @param str the specified string to convert to an integer
   * @return
   *     the integer value of the specified string,
   *     otherwise Optional.empty() if an error occurs
   */
  public static Optional<Integer> toInteger(String str) {
    try {
      int i = Integer.parseInt(str);
      Optional<Integer> val = Optional.of(i);
      return val;
    } catch (Exception ex) {
      return Optional.empty();
    }
  }

  /**
   * Returns n number of system-dependent newline characters.
   *
   * @param n number of newline characters to return
   */
  public static String newline(int n) {
    if (n < 1) {
      return "";
    }
    StringBuilder sb = new StringBuilder(n);
    for (int i = 0; i < n; i++) {
      sb.append(NEWLINE);
    }
    return sb.toString();
  }

  /**
   * Returns one system-dependent newline character.
   *
   * @see #newline(int)
   */
  public static String newline() {
    return NEWLINE;
  }

  /**
   * Returns the user's home directory. On Windows, it will probably be
   * the Desktop directory.
   *
   * @return
   *     the user's home directory if found,
   *     otherwise null
   */
  public static Path getUserHomeDirectory() {
    FileSystemView fsv = FileSystemView.getFileSystemView();
    fsv.getRoots();
    File desktopPath = fsv.getHomeDirectory();
    return (desktopPath != null) ? desktopPath.toPath() : null;
  }

  /**
   * Tests if the specified file is readable.
   *
   * @param path specified path to file
   */
  public static boolean fileReadable(Path path) {
    return Files.isReadable(path);
  }

  /**
   * Tests if the specified file is writable.
   *
   * @param path specified path to file
   */
  public static boolean fileWritable(Path path) {
    return Files.isWritable(path);
  }

  /**
   * Tests if the specified file is not null and exists.
   *
   * @param path the specified path to file
   */
  public static boolean fileExists(Path path) {
    return (path != null && Files.isRegularFile(path));
  }

  /**
   * Tests if the specified directory is not null and exists.
   *
   * @param path the specified path to directory
   */
  public static boolean directoryExists(Path path) {
    return (path != null && Files.isDirectory(path));
  }

  /**
   * Returns the parent directory of the specified path.
   *
   * @param path the specified path
   * @return
   *     the parent directory of the specified path if parent exists,
   *     otherwise null
   */
  public static Path getParentDirectory(Path path) {
    return path.getParent();
  }

  /**
   * Returns a list of all files and subdirectories recursively. Only
   * supports regular files and directories.
   *
   * @param path the specified path to directory
   * @param omitDirectoryNames whether directory names should be included
   *     in the list
   * @return
   *     a list of all files and subdirectories,
   *     otherwise an empty list if directory does not exist or is empty
   * @throws IOException
   */
  public static Path[] getDirectoryContents(Path path, boolean omitDirectoryNames)
      throws IOException {
    if (!directoryExists(path)) {
      return new Path[]{};
    }

    ArrayList<Path> list = new ArrayList<>();
    Files.walk(path)
        .forEach(p -> {
          if (fileExists(p) || (directoryExists(p) && !omitDirectoryNames)) {
            list.add(p);
          }
        });

    Path[] ret = new Path[list.size()];
    for (int i = 0; i < list.size(); i++) {
      ret[i] = Paths.get(list.get(i).toString());
    }

    return ret;
  }

  /**
   * Returns a list of all files and subdirectories recursively. Default
   * omitDirectoryNames is false.
   *
   * @param path the specified path to directory
   * @return
   *     a list of all files and subdirectories,
   *     otherwise an empty list if directory does not exist or is empty
   * @see #getDirectoryContents(Path, boolean)
   * @throws IOException
   */
  public static Path[] getDirectoryContents(Path path) throws IOException {
    return getDirectoryContents(path, false);
  }

  /**
   * Creates the specified directory.
   *
   * @param path the specified path of the directory
   * @throws IOException
   */
  public static void createDirectory(Path path) throws IOException {
    if (directoryExists(path)) {
      return;
    }
    Files.createDirectories(path);
  }

  /**
   * Creates the parent directory of the specified path. This method
   * does not throw an exception if the parent directory is null.
   *
   * @param path the specified path
   * @throws IOException if an I/O error occurs
   */
  public static void createParentDirectory(Path path) throws IOException {
    Path parent = getParentDirectory(path);
    if (parent == null) {
      return;
    }
    createDirectory(parent);
  }

  /**
   * Creates the specified file.
   *
   * @param path the specified path to the file
   * @throws IOException if an I/O error occurs
   */
  public static void createFile(Path path) throws IOException {
    if (fileExists(path)) {
      return;
    }
    createParentDirectory(path);
    Files.createFile(path);
  }

  /**
   * Deletes the specified file.
   *
   * @param path the specified path to the file
   * @throws IOException if an I/O error occurs
   */
  public static void deleteFile(Path path) throws IOException {
    if (!fileExists(path)) {
      return;
    }
    Files.delete(path);
  }

  /**
   * Appends the specified string to the specified file.
   *
   * @param path specified path to the file
   * @param str specified string
   * @throws IOException if an I/O error occurs
   */
  public static void appendToFile(Path path, String str) throws IOException {
    if (!fileExists(path)) {
      createFile(path);
    }
    try (
        FileOutputStream fos = new FileOutputStream(path.toString(), true);
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos, StandardCharsets.UTF_8))
    ) {
      bw.write(str + newline());
    }
  }

  /**
   * Returns the file extension at the end of the specified file's path.
   *
   * @param path specified file
   * @return
   *     the file extension at the end of the specified file's path,
   *     otherwise null if path string does not contain a file extension
   * @throws IllegalArgumentException if the specified path is not a file
   */
  public static String getFileExtension(Path path) {
    if (!AdakiteUtils.fileExists(path)) {
      throw new IllegalArgumentException(Debugging.fileDoesNotExist(path));
    }
    String pathStr = path.getFileName().toString();
    int index = pathStr.lastIndexOf('.');
    return (index < 0) ? null : pathStr.substring(index + 1, pathStr.length());
  }

  /**
   * Pads the specified string with spaces if its length is less than the
   * specified length.
   *
   * @param str specified string
   * @param len specified length
   */
  public static String pad(String str, int len) {
    StringBuilder sb = new StringBuilder(str);
    int n = str.length() - len;
    for (int i = 0; i < n; i++) {
      sb.append(" ");
    }
    return sb.toString();
  }

}
