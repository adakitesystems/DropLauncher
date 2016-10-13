/* FileDropList.java */

package droplauncher.filedroplist;

import droplauncher.tools.FileArray;

import java.util.logging.Logger;

/**
 * Class for handling files dropped into file drop area.
 *
 * @author Adakite Systems
 * @author adakitesystems@gmail.com
 */
public class FileDropList {

  public static FileDropList INSTANCE = new FileDropList();

  private static final Logger LOGGER =
        Logger.getLogger(FileDropList.class.getName());

  private static FileArray files;

  private FileDropList() {
    this.files = new FileArray();
  }

  /**
   * Returns the FileArray object which holds the dropped files. Does not
   * return a copy.
   *
   * @return
   *     the FileArray object
   */
  public static FileArray getFiles() {
    return FileDropList.files;
  }

}
