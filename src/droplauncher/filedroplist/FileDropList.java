/* FileDropList.java */

package droplauncher.filedroplist;

import droplauncher.debugging.Debugging;
import droplauncher.tools.MainTools;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.util.ArrayList;

/**
 * Class for handling files dropped into file drop area.
 *
 * @author Adakite Systems
 * @author adakitesystems@gmail.com
 */
public class FileDropList {

  public static FileDropList INSTANCE = new FileDropList();

  private static final Logger LOGGER = LogManager.getRootLogger();

  private ArrayList<File> files;

  private FileDropList() {
    this.files = new ArrayList<>();
  }

  public void clear() {
    this.files.clear();
  }

  /**
   * Adds the specified file to the list.
   *
   * @param file specified file
   * @return
   *     true if file was added successfully,
   *     otherwise false
   */
  public boolean add(File file) {
    if (file == null) {
      LOGGER.warn(Debugging.NULL_OBJECT);
      return false;
    }

    if (file.isDirectory()) {
      File[] dirList = file.listFiles();
      for (File tmpFile : dirList) {
        add(tmpFile);
      }
      return true;
    }

    /* Remove old file. */
    boolean oldFilePresent = false;
    int index = getIndex(file.getName());
    if (index >= 0) {
      oldFilePresent = true;
      this.files.remove(index);
    }

    /* Add new file. */
    boolean status = this.files.add(file);
    String filename = file.getName();
    if (status) {
      if (oldFilePresent) {
        LOGGER.info("replaced file: " + filename);
      } else {
        LOGGER.info("added file: " + filename);
      }
    }

    return status;
  }

  /**
   * Returns the index of the specified file.
   *
   * @param filename specified file
   * @return
   *     the index of the specified file if found,
   *     otherwise -1
   */
  public int getIndex(String filename) {
    if (MainTools.isEmpty(filename)) {
      LOGGER.warn(Debugging.EMPTY_STRING);
      return -1;
    }

    int len = this.files.size();
    for (int i = 0; i < len; i++) {
      if (this.files.get(i).getName().equalsIgnoreCase(filename)) {
        return i;
      }
    }

    return -1;
  }

  /**
   * Returns a copy of all the dropped files.
   *
   * @return a copy of all the dropped files
   */
  public ArrayList<File> getFiles() {
    ArrayList<File> copyArray = new ArrayList<>();
    for (File tmpFile : this.files) {
      copyArray.add(tmpFile);
    }
    return copyArray;
  }

}
