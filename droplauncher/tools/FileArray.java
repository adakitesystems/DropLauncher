/* FileArray.java */

package droplauncher.tools;

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

  private static final Logger LOGGER = Logger.getLogger(FileArray.class.getName());
  private static final boolean CLASS_DEBUG = (MainTools.DEBUG && true);

  private static final int DEFAULT_ARRAY_SIZE = 1;
  private static final int DEFAULT_ARRAY_INCREMENT = 1;

  private File[] files;
  private int fileCount;

  public FileArray() {
    this.files = new File[DEFAULT_ARRAY_SIZE];
    reset();
  }

  /**
   * Sets the number of elements to zero and does not resize the array.
   * All previous elements are still stored in the array until they are
   * overwritten by new elements. The old elements are inaccessible via
   * public functions due to the number of elements being set to zero and no
   * way of accessing the rest of the array through public functions.
   */
  public void reset() {
    this.fileCount = 0;
  }

  /**
   * Returns the number of accessible elements in the array via
   * public functions.
   */
  public int size() {
    return this.fileCount;
  }

  /**
   * Ensures the array can hold at least one more element. The array
   * is resized if required.
   *
   * @return
   *     true if the array's capacity is sufficient or was resized sucessfully,
   *     otherwise false
   */
  private boolean ensureCapacity() {
    if (this.fileCount >= this.files.length) {
      try {
        File[] newArray = new File[this.fileCount + DEFAULT_ARRAY_INCREMENT];
        System.arraycopy(this.files, 0, newArray, 0,this.fileCount);
        this.files = newArray;
      } catch (Exception ex) {
        if (CLASS_DEBUG) {
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
   * return false. If the File object is a directory, this function will
   * recursively be called on all subdirectories and files.
   *
   * @param file File object to add
   * @return
   *     true if object was added,
   *     otherwise false
   */
  public boolean add(File file) {
    /* Validate parameters. */
    if (file == null) {
      if (CLASS_DEBUG) {
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
      } catch (IOException | SecurityException ex) {
        if (CLASS_DEBUG) {
          LOGGER.log(Level.SEVERE, null, ex);
        }
      }
      if (!ensureCapacity()) {
        return false;
      }
      /* Add file. */
      this.files[this.fileCount++] = file;
      if (CLASS_DEBUG) {
        System.out.println("Added file: " + getCanonicalPath(file));
      }
    } else if (file.isDirectory()) {
      /* If specified file object is a directory. */
      try {
        File[] files = file.listFiles();
        if (files == null) {
          if (CLASS_DEBUG) {
            LOGGER.log(
                Level.WARNING,
                "empty directory detected or an error was encountered"
            );
          }
          return false;
        }
        /* Check if directory contains any files. */
        int len = files.length;
        if (len < 1) {
          if (CLASS_DEBUG) {
            LOGGER.log(Level.WARNING, "empty directory detected");
          }
          return false;
        }
        /* Add directory files to array. */
        for (int i = 0; i < len; i++) {
          if (!add(files[i])) {
            if (CLASS_DEBUG) {
              LOGGER.log(
                  Level.SEVERE,
                  "encountered while adding file from directory"
              );
            }
          }
        }
      } catch (SecurityException ex) {
        if (CLASS_DEBUG) {
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
   *     the File object at the specified index,
   *     otherwise null
   */
  public File get(int index) {
    /* Validate parameters. */
    if (index < 0 || index >= this.fileCount) {
      if (CLASS_DEBUG) {
        LOGGER.log(Level.WARNING, MainTools.INDEX_OOB);
      }
      return null;
    }
    return this.files[index];
  }

  /**
   * Returns the index of the file matching the specified path or filename.
   *
   * @param path specified path or filename to use as search key
   * @return
   *     index of the element in the array if found,
   *     otherwise -1
   */
  public int getIndexOf(String path) {
    /* Validate parameters. */
    if (MainTools.isEmpty(path)) {
      if (CLASS_DEBUG) {
        LOGGER.log(Level.WARNING, MainTools.EMPTY_STRING);
      }
      return -1;
    }

    File tmpFile;

    for (int i = 0; i < this.fileCount; i++) {
      tmpFile = this.files[i];
      try {
        if (tmpFile.getName().equals(path)
            || tmpFile.getCanonicalPath().equals(path)) {
          return i;
        }
      } catch (IOException | SecurityException ex) {
        if (CLASS_DEBUG) {
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
    /* Validate parameters. */
    if (file == null) {
      if (CLASS_DEBUG) {
        LOGGER.log(Level.WARNING, MainTools.NULL_OBJECT);
      }
      return -1;
    }
    /* Determine file size. */
    try {
      if (!file.isFile()) {
        if (CLASS_DEBUG) {
          LOGGER.log(Level.WARNING, "object is not a file");
        }
        return -1;
      }
      long len = file.length();
      return len;
    } catch (SecurityException ex) {
      if (CLASS_DEBUG) {
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
    /* Validate parameters. */
    if (file == null) {
      if (CLASS_DEBUG) {
        LOGGER.log(Level.WARNING, MainTools.NULL_OBJECT);
      }
      return null;
    }
    /* Determine canonical path. */
    try {
      String path = file.getCanonicalPath();
      return path;
    } catch (IOException | SecurityException ex) {
      if (CLASS_DEBUG) {
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
    /* Validate parameters. */
    if (file == null) {
      if (CLASS_DEBUG) {
        LOGGER.log(Level.WARNING, MainTools.NULL_OBJECT);
      }
      return null;
    }
    /* Determine short path. */
    try {
      String path = file.getName();
      return path;
    } catch (SecurityException ex) {
      if (CLASS_DEBUG) {
        LOGGER.log(Level.SEVERE, null, ex);
      }
      return null;
    }
  }

}
