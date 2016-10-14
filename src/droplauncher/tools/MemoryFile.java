/* MemoryFile.java */

package droplauncher.tools;

import droplauncher.debugging.Debugging;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

/**
 * Class for manipulating files. First, a file must be completely read in
 * with {@link #readIntoMemory(java.lang.String)}. After, any line may be
 * edited. After modifications have been made, the file can be written to
 * disk with {@link #writeToDisk()} or {@link #writeToDisk(java.lang.String)}.
 *
 * @author Adakite Systems
 * @author adakitesystems@gmail.com
 */
public class MemoryFile  {

  private static final Logger LOGGER = LogManager.getRootLogger();

  private String filename;
  private ArrayList<String> lines;

  public MemoryFile() {
    this.filename = null;
    this.lines = new ArrayList<>();
  }

  private void reset() {
    this.filename = null;
    this.lines.clear();
  }

  /**
   * Returns the path to this file.
   *
   * @return the path to this file
   */
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
    if (MainTools.isEmpty(filename)) {
      LOGGER.warn(Debugging.EMPTY_STRING);
      return false;
    }
    if (!MainTools.doesFileExist(filename)) {
      LOGGER.warn("file inaccessible: " + filename);
      return false;
    }

    reset();
    this.filename = filename;

    try {
      FileInputStream fis = new FileInputStream(filename);
      BufferedReader br = new BufferedReader(new InputStreamReader(fis));
      String line;

      while ((line = br.readLine()) != null) {
        this.lines.add(line);
      }

      br.close();
      fis.close();

      return true;
    } catch (Exception ex) {
      LOGGER.error(ex.getMessage(), ex);
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
    if (MainTools.isEmpty(filename)) {
      LOGGER.warn(Debugging.EMPTY_STRING);
      return false;
    }
    if (!MainTools.doesFileExist(filename)) {
      LOGGER.warn("file inaccessible: " + filename);
      return false;
    }
    if (this.lines.size() < 1) {
      LOGGER.warn("nothing to write");
      return false;
    }

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
    } catch (Exception ex) {
      LOGGER.error(ex.getMessage(), ex);
      return false;
    }
  }

  public boolean writeToDisk() {
    return writeToDisk(this.filename);
  }

  /**
   * Returns the object which holds the lines of the file.
   *
   * @return the object which holds the lines of the file
   */
  public ArrayList<String> getLines() {
    return this.lines;
  }

  /**
   * Prints memory file to console. Mainly used for debugging.
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
