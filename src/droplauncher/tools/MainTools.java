/* MainTools.java */

package droplauncher.tools;

import droplauncher.DropLauncher;
import droplauncher.MainWindow;
import droplauncher.debugging.Debugging;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.xml.bind.DatatypeConverter;

/**
 * Useful variables and functions.
 *
 * @author Adakite Systems
 * @author adakitesystems@gmail.com
 */
public class MainTools {

  private static final MainTools INSTANCE = new MainTools();

  /* ************************************************************ */
  /* Debugging */
  /* ************************************************************ */
  public  static final boolean DEBUG = true;
  private static final boolean DEBUG_LOG_FILE_ENABLED = false;
  private static final String  DEBUG_LOG_FILENAME = "errorlog.xml";
  private static final String  CLASS_NAME = Debugging.class.getName();
  private static final boolean CLASS_DEBUG = (DEBUG && true);
  private static final Logger  LOGGER = Logger.getLogger(CLASS_NAME);
  /* Predefined logger messages */
  public static final String EMPTY_STRING = "null or empty string";
  public static final String NULL_OBJECT = "null object";
  public static final String INDEX_OOB = "index out of range";
  /* ************************************************************ */

  private static MessageDigest md;
  public static final String EMPTY_MD5_CHECKSUM = "00000000000000000000000000000000";

  private MainTools() {
    try {
      md = MessageDigest.getInstance("MD5");
    } catch (Exception ex) {
      LOGGER.log(Level.SEVERE, null, ex);
    }
  }

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
    if (CLASS_DEBUG && str != null && str.isEmpty()) {
      LOGGER.log(Level.WARNING, "non-null empty string detected");
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

    /* Validate parameters. */
    if (len < 1) {
      if (CLASS_DEBUG) {
        /* Possibly just empty and not null. However, the result is the same. */
        LOGGER.log(Level.WARNING, NULL_OBJECT);
      }
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
   * @param path the path to the file
   * @return
   *     true if the file exists,
   *     otherwise false
   */
  public static boolean doesFileExist(String path) {
    /* Validate parameters. */
    if (isEmpty(path)) {
      if (CLASS_DEBUG) {
        LOGGER.log(Level.WARNING, EMPTY_STRING);
      }
      return false;
    }

    File file = new File(path);

    return (file.exists() && file.isFile());
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
    /* Validate parameters. */
    if (isEmpty(path)) {
      if (CLASS_DEBUG) {
        LOGGER.log(Level.WARNING, EMPTY_STRING);
      }
      return false;
    }

    File dir = new File(path);

    return (dir.exists() && dir.isDirectory());
  }

  /**
   * Return the specified filename excluding the last period character and
   * all characters after it.
   *
   * @param filename specified filename
   *
   * @return
   *     the filename excluding the extension if the specified filename
   *         is not null or empty,
   *     otherwise null
   */
  public static String getFilenameNoExt(String filename) {
    /* Validate parameters. */
    if (isEmpty(filename)) {
      if (CLASS_DEBUG) {
        LOGGER.log(Level.WARNING, EMPTY_STRING);
      }
      return null;
    }

    int index = filename.lastIndexOf(".");

    return (index <= 0) ? null : filename.substring(0, index);
  }

  /**
   * Returns the parent directory of the specified path.
   *
   * @param path specified path
   * @return
   *     the parent directory of the specified path if parent exists,
   *     otherwise null
   */
  public static String getParentDirectory(String path) {
    /* Validate parameters. */
    if (isEmpty(path)) {
      if (CLASS_DEBUG) {
        LOGGER.log(Level.WARNING, EMPTY_STRING);
      }
      return null;
    }

    File file = new File(path);

    return file.getParent();
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
    /* Validate parameters. */
    if (isEmpty(str)) {
      if (CLASS_DEBUG) {
        LOGGER.log(Level.WARNING, EMPTY_STRING);
      }
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
   * Remove double quotations from the beginning and end of the
   * specified string.
   *
   * @param str specified string
   * @return
   *     the specified string without quotations,
   *     otherwise null if the string is null or empty
   */
  public static String removeQuotations(String str) {
    /* Validate parameters. */
    if (isEmpty(str)) {
      if (CLASS_DEBUG) {
        LOGGER.log(Level.WARNING, EMPTY_STRING);
      }
      return null;
    }

    while (str.startsWith("\"")) {
      str = str.substring(1, str.length());
    }
    while (str.endsWith("\"")) {
      str = str.substring(0, str.length() - 1);
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
    /* Validate parameters. */
    if (isEmpty(str)) {
      if (CLASS_DEBUG) {
        LOGGER.log(Level.WARNING, EMPTY_STRING);
      }
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

    if (newStr.length() < 1) {
      newStr = null;
    }

    return newStr;
  }

  /**
   * Return the MD5 checksum of the specified file.
   *
   * @param path specified path to file
   * @return
   *     the MD5 checksum of the specified file,
   *     otherwise {@link #EMPTY_MD5_CHECKSUM} if an error occurred
   */
  public static String getMD5Checksum(String path) {
    if (!doesFileExist(path)) {
      return EMPTY_MD5_CHECKSUM;
    }
    try {
      md.update(Files.readAllBytes(Paths.get(path)));
      byte[] digest = md.digest();
      String checksum = DatatypeConverter.printHexBinary(digest).toLowerCase();
      return checksum;
    } catch (Exception ex) {
      if (CLASS_DEBUG) {
        LOGGER.log(Level.SEVERE, null, ex);
      }
    }
    return EMPTY_MD5_CHECKSUM;
  }

  /**
   * Display window message.
   *
   * @param message specified message to display
   * @param title title of message window
   */
  public static void showWindowMessage(String message, String title) {
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
    showWindowMessage(message, null);
  }

  /**
   * Display a file chooser dialog.
   *
   * @return
   *     the selected file if user selects OK with the file selected,
   *     otherwise null
   */
  public static File showfileChooserDialog() {
    JFileChooser fc = new JFileChooser();
    if (fc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
      File file = fc.getSelectedFile();
      return file;
    } else {
      return null;
    }
  }

}
