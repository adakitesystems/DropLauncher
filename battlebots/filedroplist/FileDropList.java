/* FileDropList.java */

package battlebots.filedroplist;

import battlebots.tools.FileArray;

import java.util.logging.Logger;

/**
 * Class for handling files dropped into file drop area.
 *
 * @author Adakite Systems
 * @author adakitesystems@gmail.com
 */
public class FileDropList {

  private static FileDropList INSTANCE = new FileDropList();

  private static final Logger LOGGER =
        Logger.getLogger(FileDropList.class.getName());

  private static FileArray _files;

  private FileDropList() {
    _files = new FileArray();
  }

  /**
   * Returns the FileArray object which holds the dropped files. Does not
   * return a copy.
   */
  public static FileArray getFiles() {
    return _files;
  }

}
