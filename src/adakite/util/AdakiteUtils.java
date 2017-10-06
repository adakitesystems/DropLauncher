package adakite.util;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Utitilies class for useful or wrapped variables and methods.
 */
public class AdakiteUtils {

  public enum StringCompareOption {

    TRIM
    ;

  }

  public enum DirectoryTraverseOption {

    OMIT_DIRECTORY_NAMES
    ;

  }

  /**
   * Wrapped Java's built-in platform-dependent newline character.
   */
  private static final String NEWLINE = System.lineSeparator();

  private AdakiteUtils() {}

  /**
   * Tests whether the specified string is null or empty.
   *
   * @param str the specified string
   * @param options
   * @return
   *     true if string is null or the length is less than one,
   *     otherwise false
   */
  public static boolean isNullOrEmpty(String str, StringCompareOption... options) {
    if (str == null) {
      return true;
    }
    if (options != null && options.length >= 1) {
      for (StringCompareOption option : StringCompareOption.values()) {
        if (option == StringCompareOption.TRIM) {
          str = str.trim();
        }
      }
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
   * Tests whether the specfied two strings are both non-null and equal
   * to each other.
   *
   * @param str1 specified first string
   * @param str2 specified second string
   */
  public static boolean isNonNullAndEqual(String str1, String str2) {
    return (str1 != null && str2 != null && str1.equals(str2));
  }

  /**
   * Returns n number of system-dependent newline characters.
   *
   * @param n number of newline characters to return
   */
  public static String newline(int n) {
    if (n < 1) {
      throw new IllegalArgumentException("invalid number of newlines: " + n);
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
   * Returns a string whose value is this string, with any leading
   * whitespace removed.
   *
   * @param str specified string
   */
  public static String trimLeft(String str) {
    int index;
    for (index = 0; index < str.length(); index++) {
      char ch = str.charAt(index);
      if (ch != ' ' && ch != '\t') {
        break;
      }
    }
    return str.substring(index, str.length());
  }

  /**
   * Returns a string whose value is this string, with any trailing
   * whitespace removed.
   *
   * @param str specified string
   */
  public static String trimRight(String str) {
    int index;
    for (index = str.length() - 1; index >= 0; index--) {
      char ch = str.charAt(index);
      if (ch != ' ' && ch != '\t') {
        break;
      }
    }
    return str.substring(0, index + 1);
  }

  /**
   * Returns a string whose value is this string, with any leading, trailing
   * and excess infix whitespace removed. "Excess infix" whitespace is described
   * as more than one whitespace between words. In order words, each word will
   * have exactly one whitespace between them.
   *
   * @param str specified string
   */
  public static String trimAll(String str) {
    if (str.isEmpty()) {
      return "";
    }

    String[] tokens = str
        .replace("\t", " ")
        .trim()
        .split(" ");
    StringBuilder ret = new StringBuilder(tokens.length);
    ret.append(tokens[0]);
    for (int i = 1; i < tokens.length; i++) {
      ret.append(" ").append(tokens[i]);
    }

    return ret.toString();
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
   * @param options
   * @return
   *     a list of all files and subdirectories,
   *     otherwise an empty list if directory does not exist or is empty
   * @throws FileNotFoundException
   * @throws IOException
   */
  public static Path[] getDirectoryContents(Path path, DirectoryTraverseOption... options) throws FileNotFoundException,
                                                                                                  IOException {
    if (!directoryExists(path)) {
      throw new FileNotFoundException();
    }

    List<DirectoryTraverseOption> directoryTraverseOptions = new ArrayList<>();
    if (options != null && options.length == 1) {
      for (DirectoryTraverseOption option : options) {
        directoryTraverseOptions.add(option);
      }
    }

    List<Path> list = new ArrayList<>();
    Files.walk(path)
        .forEach(p -> {
          if (fileExists(p) || (directoryExists(p) && !directoryTraverseOptions.contains(DirectoryTraverseOption.OMIT_DIRECTORY_NAMES))) {
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
      bw.write(str + AdakiteUtils.newline());
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

  /**
   * Formats the specified string as a sentence by ensuring the first
   * character is uppercase and the last character is a period.
   *
   * @param str specified string
   * @param locale specified locale to use when converting to uppercase.
   */
  public static String formatAsSentence(String str, Locale locale) {
    String firstChar = ("" + str.charAt(0)).toUpperCase(locale);
    String ret = firstChar + str.substring(1, str.length());

    String lastChar = "" + str.charAt(str.length() - 1);
    if (!lastChar.equals(".")) {
      ret += ".";
    }

    return ret;
  }

  public static boolean contains(byte[] arr, byte[] key) {
    int ilen = arr.length - key.length + 1;
    int jlen = key.length;
    for (int i = 0; i < ilen; ++i) {
      int j = 0;
      for (j = 0; j < jlen; ++j) {
        if (arr[i + j] != key[j]) {
          break;
        }
      }
      if (j >= jlen) {
        return true;
      }
    }
    return false;
  }

}
