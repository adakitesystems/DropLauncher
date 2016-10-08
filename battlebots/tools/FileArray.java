/* FileArray.java */

package battlebots.tools;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Custom array for File objects.
 *
 * @author Adakite Systems
 * @author adakitesystems@gmail.com
 */
public class FileArray {

  private static final Logger LOGGER =
      Logger.getLogger(FileArray.class.getName());
  private static final boolean CLASS_DEBUG = true;

  private static final int DEFAULT_ARRAY_SIZE = 1;
  private static final int DEFAULT_ARRAY_INCREMENT = 1;

  private File[] _files;
  private int _fileCount;

  public FileArray() {
    _files = new File[DEFAULT_ARRAY_SIZE];
    reset();
  }

  /**
   * Returns the number of elements in the array.
   */
  public int size() {
    return _fileCount;
  }

  /**
   * Resets the array element count without setting any elements to null.
   */
  public void reset() {
    _fileCount = 0;
  }

  /**
   * Ensures the array can hold at least one more element. The array
   * is resized if required.
   *
   * @return
   *     true if array's capacity is sufficient or was resized sucessfully,
   *     otherwise false
   */
  private boolean ensureCapacity() {
    if (_fileCount >= _files.length) {
      try {
        File[] newArray = new File[_fileCount + DEFAULT_ARRAY_INCREMENT];
        System.arraycopy(_files, 0, newArray, 0, _fileCount);
        _files = newArray;
      } catch (Exception ex) {
        if (MainTools.DEBUG) {
          LOGGER.log(Level.SEVERE, "encountered while resizing array", ex);
        }
        return false;
      }
    }
    return true;
  }

  /**
   * Adds the specified file to the array. The file will not be added if
   * its canonical path matches another file already in the array and will
   * return false.
   *
   * @param file file object to add
   * @return
   *     true if element was added,
   *     otherwise false
   */
  public boolean add(File file) {
    if (file == null) {
      if (MainTools.DEBUG) {
        LOGGER.log(Level.WARNING, MainTools.NULL_OBJECT);
      }
      return false;
    }

    if (file.isFile() && !file.isDirectory()) {
      /* If specified file object is an actual file. */
      try {
        /* If file already exists in array. */
        if (getIndexOf(file.getCanonicalPath()) >= 0) {
          return false;
        }
      } catch (IOException ex) {
        if (MainTools.DEBUG) {
          LOGGER.log(Level.SEVERE, null, ex);
        }
      }
      if (!ensureCapacity()) {
        return false;
      }
      _files[_fileCount++] = file;
      if (CLASS_DEBUG) {
        System.out.println("FileArray: added file: " + getCanonicalPath(file));
      }
    } else if (file.isDirectory()) {
      /* If specified file object is a directory. */
      try {
        File[] files = file.listFiles();
        if (files == null) {
          if (MainTools.DEBUG) {
            LOGGER.log(
                Level.WARNING,
                "empty directory detected or an error was encountered"
            );
          }
          return false;
        }
        int len = files.length;
        if (len < 1) {
          if (MainTools.DEBUG) {
            LOGGER.log(Level.WARNING, "empty directory detected");
          }
          return false;
        }
        /* Add directory contents to array. */
        for (int i = 0; i < len; i++) {
          if (!add(files[i])) {
            if (MainTools.DEBUG) {
              LOGGER.log(
                  Level.SEVERE,
                  "encountered while adding file from directory"
              );
            }
          }
        }
      } catch (SecurityException ex) {
        if (MainTools.DEBUG) {
          LOGGER.log(
              Level.SEVERE,
              "encountered while reading list of files in directory",
              ex
          );
        }
        return false;
      }
    }

    /* If this line is reached, operation was successful. */
    return true;
  }

  /**
   * Returns the element at the specified index.
   *
   * @param index index of element to return
   * @return
   *     File object at specified index,
   *     otherwise null
   */
  public File get(int index) {
    if (index < 0 || index >= _fileCount) {
      if (MainTools.DEBUG) {
        LOGGER.log(Level.WARNING, MainTools.INDEX_OOB);
      }
      return null;
    }
    return _files[index];
  }

  /**
   * Returns the index of the file matching the specified filename.
   *
   * @param filename specified filename to use as search key
   * @return
   *     index of the element in the array if found,
   *     otherwise -1
   */
  public int getIndexOf(String filename) {
    if (MainTools.isEmpty(filename)) {
      if (MainTools.DEBUG) {
        LOGGER.log(Level.WARNING, MainTools.EMPTY_STRING);
      }
      return -1;
    }

    File tmpFile;

    for (int i = 0; i < _fileCount; i++) {
      tmpFile = _files[i];
      try {
        if (tmpFile.getName().equals(filename)
            || tmpFile.getCanonicalPath().equals(filename)) {
          return i;
        }
      } catch (IOException | SecurityException ex) {
        if (MainTools.DEBUG) {
          LOGGER.log(Level.SEVERE, null, ex);
        }
      }
    }

    return -1;
  }

  /**
   * Returns the file size in bytes.
   *
   * @param file specified file
   * @return
   *     number of bytes in file if no errors were encountered,
   *     otherwise -1
   */
  public static long getFileSize(File file) {
    if (file == null) {
      if (MainTools.DEBUG) {
        LOGGER.log(Level.WARNING, MainTools.NULL_OBJECT);
      }
      return -1;
    }
    try {
      if (!file.isFile()) {
        if (MainTools.DEBUG) {
          LOGGER.log(Level.WARNING, "object is not a file");
        }
        return -1;
      }
      long len = file.length();
      return len;
    } catch (SecurityException ex) {
      if (MainTools.DEBUG) {
        LOGGER.log(Level.SEVERE, "encountered while reading file size", ex);
      }
      return -1;
    }
  }

  /**
   * Returns the canocial path to the specified file.
   *
   * @param file specified file
   * @return
   *     the canonical path to the file if no errors were encountered,
   *     otherwise null
   */
  public static String getCanonicalPath(File file) {
    if (file == null) {
      if (MainTools.DEBUG) {
        LOGGER.log(Level.WARNING, MainTools.NULL_OBJECT);
      }
      return null;
    }
    try {
      String path = file.getCanonicalPath();
      return path;
    } catch (IOException | SecurityException ex) {
      if (MainTools.DEBUG) {
        LOGGER.log(Level.SEVERE, null, ex);
      }
      return null;
    }
  }

  /**
   * Returns the last name in the full path to the specified file.
   *
   * @param file specified file
   * @return
   *     the last name in the full path to the file if no
   *         errors were encountered,
   *     otherwise null
   */
  public static String getShortPath(File file) {
    if (file == null) {
      if (MainTools.DEBUG) {
        LOGGER.log(Level.WARNING, MainTools.NULL_OBJECT);
      }
      return null;
    }
    try {
      String path = file.getName();
      return path;
    } catch (SecurityException ex) {
      if (MainTools.DEBUG) {
        LOGGER.log(Level.SEVERE, null, ex);
      }
      return null;
    }
  }

}
