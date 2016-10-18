package droplauncher.tools;

import droplauncher.DropLauncher;
import droplauncher.MainWindow;
import droplauncher.debugging.Debugging;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.logging.Level;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

/**
 * Useful variables and functions.
 *
 * @author Adakite Systems
 * @author adakitesystems@gmail.com
 */
public class MainTools {

  private static final Logger LOGGER = LogManager.getRootLogger();

  private MainTools() {}

  /**
   * Tests whether the specified string is null or
   * {@link java.lang.String#length()} is less than 1.
   *
   * @param str the string to check
   * @return
   *     true if string is null or empty,
   *     otherwise false
   */
  public static boolean isEmpty(String str) {
    boolean status = (str == null || str.length() < 1);
    /* A null string may be intended. An empty string may not be. */
    if (str != null && str.isEmpty()) {
      LOGGER.warn("non-null empty string detected");
    }
    return status;
  }

  /**
   * Returns the String[] version of the specified ArrayList object.
   *
   * @param arrlist specified ArrayList object
   * @return
   *     String[] object if specified ArrayList is not null or empty,
   *     otherwise null
   */
  public static String[] toStringArray(ArrayList<String> arrlist) {
    int len = (arrlist == null) ? 0 : arrlist.size();
    if (len < 1) {
      LOGGER.warn(Debugging.nullObject());
      return null;
    }
    String[] arr = new String[len];
    for (int i = 0; i < len; i++) {
      arr[i] = arrlist.get(i);
    }
    return arr;
  }

  /**
   * Tests whether the specified file exists.
   *
   * @param file specified file
   * @return
   *     true if the file exists,
   *     otherwise false
   */
  public static boolean doesFileExist(File file) {
    if (file == null) {
      LOGGER.warn(Debugging.nullObject());
      return false;
    }
    return (file.exists() && file.isFile());
  }

  public static boolean doesFileExist(String path) {
    return doesFileExist(new File(path));
  }

  /**
   * Tests whether the specified directory exists.
   *
   * @param path path to the directory
   * @return
   *     true if directory exists,
   *     otherwise false
   */
  public static boolean doesDirectoryExist(String path) {
    if (isEmpty(path)) {
      LOGGER.warn(Debugging.emptyString());
      return false;
    }
    File dir = new File(path);
    return (dir.exists() && dir.isDirectory());
  }

  /**
   * Returns the parent directory of the specified file.
   *
   * @param file specifiedd file
   * @return
   *     the parent directory of the specified path if parent exists,
   *     otherwise a non-null empty string
   */
  public static String getParentDirectory(File file) {
    if (file == null) {
      LOGGER.warn(Debugging.nullObject());
      return null;
    }
    String parent = file.getParent();
    return MainTools.isEmpty(parent) ? "" : parent;
  }

  public static String getParentDirectory(String path) {
    return getParentDirectory(new File(path));
  }

  /**
   * Returns the full path to the specified file.
   *
   * @param file specified file
   * @return the full path to the specified file
   */
  public static String getFullPath(File file) {
    if (file == null) {
      LOGGER.warn(Debugging.nullObject());
      return null;
    }
    if (!MainTools.doesFileExist(file)) {
      LOGGER.warn(Debugging.fileDoesNotExist(file));
      return null;
    }
    try {
      String path = file.getCanonicalPath();
      return path;
    } catch (IOException ex) {
      LOGGER.error(ex.getMessage(), ex);
    }
    return null;
  }

  /**
   * Ensures the specified string is encapsulated with double quotations.
   *
   * @param str specified string
   * @return
   *     the specified string encapsulated with quotations,
   *     otherwise null if the string is null or empty
   */
  public static String ensureQuotations(String str) {
    if (isEmpty(str)) {
      LOGGER.warn(Debugging.emptyString());
      return null;
    }

    if (!str.startsWith("\"")) {
      str = "\"" + str;
    }
    if (!str.endsWith("\"")) {
      str += "\"";
    }

    return str;
  }

  /**
   * Returns a string stripped of non-standard ASCII letter and
   * number characters. Parenthesis are allowed.
   *
   * @param str string to strip
   * @return
   *     the stripped string if specified string is not null or empty,
   *     otherwise null
   */
  public static String onlyLettersNumbers(String str) {
    if (isEmpty(str)) {
      LOGGER.warn(Debugging.emptyString());
      return null;
    }

    String newStr = "";
    char ch;
    int len = str.length();

    for (int i = 0; i < len; i++) {
      ch = str.charAt(i);
      if (ch == ' ' || ch == '(' || ch == ')'
          || (ch >= 'A' && ch <= 'Z')
          || (ch >= 'a' && ch <= 'z')
          || (ch >= '0' && ch <= '9')) {
        newStr += ch;
      }
    }

    if (isEmpty(newStr)) {
      newStr = null;
    }

    return newStr;
  }

  /**
   * Displays a window message.
   *
   * @param message specified message to display
   * @param title title of message window
   */
  public static void showInfoMessage(String message, String title) {
    if (message == null) {
      message = "";
    }
    if (isEmpty(title)) {
      title = DropLauncher.PROGRAM_NAME;
    }
    JOptionPane.showMessageDialog(
        MainWindow.mainWindow,
        message,
        title,
        JOptionPane.INFORMATION_MESSAGE
    );
  }

  public static void showWindowMessage(String message) {
    showInfoMessage(message, null);
  }

  /**
   * Display a file chooser dialog.
   *
   * @return
   *     the selected file if user selects OK with the file selected,
   *     otherwise null
   */
  public static File showFileChooserDialog() {
    JFileChooser fc = new JFileChooser();
    if (fc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
      File file = fc.getSelectedFile();
      return file;
    } else {
      return null;
    }
  }

  /**
   * Return a concatenated string of all the elements in the
   * String array list.
   *
   * @param arrlist specified String array list.
   * @return
   *     the concatenated string
   */
  public static String toString(ArrayList<String> arrlist) {
    int len = (arrlist == null) ? 0 : arrlist.size();
    if (len < 1) {
      LOGGER.warn("non-null empty array");
      return null;
    }

    String str = arrlist.get(0);
    for (int i = 1; i < len; i++) {
      str += " " + arrlist.get(i);
    }

    return str;
  }

  /**
   * Create specified file.
   *
   * @param file specified file
   * @return
   *     the created File if created or already exists,
   *     otherwise null
   */
  public static File create(File file) {
    if (file == null) {
      LOGGER.warn(Debugging.nullObject());
      return null;
    } else if (MainTools.doesFileExist(file)) {
      LOGGER.warn(Debugging.fileAlreadyExists(file));
      return new File(MainTools.getFullPath(file));
    }

    String parentDir = file.getParent();
    if (!MainTools.isEmpty(parentDir)
        && !MainTools.doesDirectoryExist(parentDir)) {
      file.mkdirs();
    }
    try {
      return file.createNewFile() ? new File(MainTools.getFullPath(file)) : null;
    } catch (IOException ex) {
      LOGGER.error(ex.getMessage(), ex);
      return null;
    }
  }

  /**
   * Copy specified source file to destination.
   *
   * @param src specified source
   * @param dest specified destination
   * @return
   *     true if file was copied successfully,
   *     otherwise false
   */
  public static boolean copyFile(String src, String dest) {
    Path srcPath = new File(src).toPath();
    Path destPath = new File(dest).toPath();

    LOGGER.info("Copy file: " + src + " to " + dest);

    try {
      Files.copy(srcPath, destPath, StandardCopyOption.REPLACE_EXISTING);
      return true;
    } catch (Exception ex) {
      LOGGER.error(ex.getMessage(), ex);
    }

    return false;
  }

  /**
   * Removes all characters after the last '.' character.
   *
   * @param filename specified string from which to remove the file extension
   * @return
   *     the specified string excluding the file extension
   *     otherwise null if the specified string is null or empty
   */
  public static String removeFileExtension(String filename) {
    if (MainTools.isEmpty(filename)) {
      LOGGER.warn(Debugging.emptyString());
      return null;
    }
    int index = filename.lastIndexOf(".");
    if (index < 0) {
      return null;
    }
    return filename.substring(0, index);
  }

}
