/* FileDropList.java */

package droplauncher.filedroplist;

import droplauncher.debugging.Debugging;
import droplauncher.tools.MainTools;

import java.io.File;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class for handling files dropped into file drop area.
 *
 * @author Adakite Systems
 * @author adakitesystems@gmail.com
 */
public class FileDropList {

  public static FileDropList INSTANCE = new FileDropList();

  /* ************************************************************ */
  /* Debugging */
  /* ************************************************************ */
  private static final String CLASS_NAME = FileDropList.class.getName();
  private static final boolean CLASS_DEBUG = true;
  private static final Logger LOGGER = Logger.getLogger(CLASS_NAME);
  /* ************************************************************ */

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
    int index = getIndex(file.getName());
    if (index > -1) {
      this.files.remove(index);
    }
    boolean status = this.files.add(file);
    if (status) {
      LOGGER.log(Level.INFO, "added file");
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
    int len = this.files.size();
    for (int i = 0; i < len; i++) {
      if (this.files.get(i).getName().equals(filename)) {
        return i;
      }
    }
    return -1;
  }

}
