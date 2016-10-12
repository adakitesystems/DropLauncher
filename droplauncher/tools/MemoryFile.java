/* MemoryFile.java */

package droplauncher.tools;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class for manipulating files. First, a file must be completely read in
 * with {@link #readIntoMemory(java.lang.String)}. After, any line may be
 * edited. After modifications have been made, the file can be written to
 * disk with {@link #writeToDisk(java.lang.String)}.
 *
 * @author Adakite Systems
 * @author adakitesystems@gmail.com
 */
public class MemoryFile  {

  private static final Logger LOGGER = Logger.getLogger(MemoryFile.class.getName());
  private static final boolean CLASS_DEBUG = (MainTools.DEBUG && true);

  private String filename;
  private TokenArray lines;

  public MemoryFile() {
    this.lines = new TokenArray();
  }

  private void reset() {
    this.filename = null;
    this.lines.reset();
  }

  public String getPath() {
    return this.filename;
  }

  /**
   * Reads entire file into memory.
   *
   * @param filename specified file to read
   * @return
   *     true if entire file has been read into memory,
   *     otherwise false
   */
  public boolean readIntoMemory(String filename) {
    /* Validate parameters. */
    if (MainTools.isEmpty(filename)) {
      if (CLASS_DEBUG) {
        LOGGER.log(Level.SEVERE, MainTools.EMPTY_STRING);
      }
      return false;
    }

    reset();

    /* Read file into memory. */
    try {
      FileInputStream fis = new FileInputStream(filename);
      BufferedReader br = new BufferedReader(new InputStreamReader(fis));
      String line;

      this.filename = filename;

      while ((line = br.readLine()) != null) {
        this.lines.add(line);
      }

      br.close();
      fis.close();

      return true;
    } catch (FileNotFoundException ex) {
      if (CLASS_DEBUG) {
        LOGGER.log(Level.SEVERE, null, ex);
      }
      return false;
    } catch (IOException ex) {
      if (CLASS_DEBUG) {
        LOGGER.log(Level.SEVERE, null, ex);
      }
      return false;
    }
  }

  /**
   * Reads the entire file into memory again.
   *
   * @return
   *     the value from {@link #readIntoMemory(java.lang.String)}
   */
  public boolean refresh() {
    return readIntoMemory(this.filename);
  }

  /**
   * Writes entire file from memory to disk.
   *
   * @param filename specified file to write
   * @return
   *     true if entire file has been written to disk,
   *     otherwise false
   */
  public boolean writeToDisk(String filename) {
    /* Validate parameters. */
    if (MainTools.isEmpty(filename)) {
      if (CLASS_DEBUG) {
        LOGGER.log(Level.SEVERE, MainTools.EMPTY_STRING);
      }
      return false;
    }
    if (!MainTools.doesFileExist(filename)) {
      if (CLASS_DEBUG) {
        LOGGER.log(Level.SEVERE, "file inaccessible or does not exist: " + filename);
      }
      return false;
    }

    if (this.lines.size() < 1) {
      if (CLASS_DEBUG) {
        LOGGER.log(Level.WARNING, "nothing to write");
      }
      return false;
    }

    /* Write file to disk. */
    try {
      FileOutputStream fos = new FileOutputStream(filename);
      BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));

      int len = this.lines.size();
      for (int i = 0; i < len; i++) {
        bw.write(this.lines.get(i) + System.lineSeparator());
      }

      bw.flush();

      bw.close();
      fos.close();

      return true;
    } catch (FileNotFoundException ex) {
      if (CLASS_DEBUG) {
        LOGGER.log(Level.SEVERE, null, ex);
      }
      return false;
    } catch (IOException ex) {
      if (CLASS_DEBUG) {
        LOGGER.log(Level.SEVERE, null, ex);
      }
      return false;
    }
  }

  public boolean writeToDisk() {
    return writeToDisk(getPath());
  }

  public TokenArray getLines() {
    return this.lines;
  }

  /**
   * Returns the first index of the string starting with the specified
   * prefix.
   *
   * @param prefix specified prefix to find
   * @return
   *     the index of the string,
   *     otherwise -1
   */
  public int getIndexStartsWith(String prefix) {
    /* Validate parameters. */
    if (MainTools.isEmpty(prefix)) {
      if (CLASS_DEBUG) {
        LOGGER.log(Level.WARNING, MainTools.EMPTY_STRING);
      }
      return -1;
    }

    int len = this.lines.size();
    for (int i = 0; i < len; i++) {
      if (this.lines.get(i).startsWith(prefix)) {
        return i;
      }
    }

    /* If this line is reached, line was not found to start with
       the specified index. */
    return -1;
  }

  /**
   * Prints file to console.
   */
  public void printToConsole() {
    int len = this.lines.size();
    if (len < 1) {
      return;
    }
    System.out.println(this.filename + ":");
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < len; i++) {
      sb.append(this.lines.get(i)).append(System.lineSeparator());
    }
    System.out.println(sb.toString());
  }

}
