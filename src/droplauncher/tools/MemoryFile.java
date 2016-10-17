/* MemoryFile.java */

package droplauncher.tools;

import droplauncher.debugging.Debugging;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
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

  private File file;
  private ArrayList<String> lines;

  public MemoryFile() {
    this.file = null;
    this.lines = new ArrayList<>();
  }

  /**
   * Constructor allowing a specified file as a parameter to load as
   * the working file.
   *
   * @param file specified file
   */
  public MemoryFile(File file) {
    this.file = null;
    this.lines = new ArrayList<>();
    readIntoMemory(file);
  }

  public void reset() {
    this.file = null;
    this.lines.clear();
  }

  /**
   * Returns this file object.
   *
   * @return this file object
   */
  public File getFile() {
    return this.file;
  }

  /**
   * Create specified file.
   *
   * @param file specified file
   * @return
   *     true if file was created successfully or already exists,
   *     otherwise false
   */
  public boolean create(File file) {
    reset();
    this.file = MainTools.create(file);
    return (this.file != null);
  }

  /**
   * Reads file into memory.
   *
   * @param file specified file to read
   * @return
   *     true if file has been read into memory,
   *     otherwise false
   */
  public boolean readIntoMemory(File file) {
    reset();

    if (file == null) {
      LOGGER.warn(Debugging.nullObject());
      return false;
    }
    if (!MainTools.doesFileExist(file)) {
      LOGGER.warn(Debugging.fileDoesNotExist(file));
      return false;
    }

    this.file = file;

    try {
      FileInputStream fis = new FileInputStream(file);
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
   * @return the value from {@link #readIntoMemory(java.io.File)}
   */
  public boolean refresh() {
    return readIntoMemory(this.file);
  }

  /**
   * Writes entire file from memory to disk.
   *
   * @param file specified file to write
   * @return
   *     true if entire file has been written to disk,
   *     otherwise false
   */
  public boolean writeToDisk(File file) {
    if (file == null) {
      LOGGER.warn(Debugging.nullObject());
      return false;
    }
    if (!MainTools.doesFileExist(file)
        && ((file = MainTools.create(file)) == null)) {
      LOGGER.warn(Debugging.fileDoesNotExist(file));
      return false;
    }
    if (this.lines.size() < 1) {
      LOGGER.warn("nothing to write");
      return true;
    }

    try {
      FileOutputStream fos = new FileOutputStream(file);
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
    return writeToDisk(this.file);
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
    System.out.println(this.file.getAbsolutePath() + ":");
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < len; i++) {
      sb.append(this.lines.get(i)).append(System.lineSeparator());
    }
    System.out.println(sb.toString());
  }

}
