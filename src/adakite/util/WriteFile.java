package adakite.util;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

public class WriteFile {

  private Path path;
  private FileOutputStream fos;
  private BufferedWriter bw;

  public WriteFile() {
    this.bw = null;
    this.fos = null;
    this.path = null;
  }

  /**
   * Opens the specified file for writing.
   *
   * @param path specified file to write
   * @throws FileNotFoundException
   * @throws UnsupportedEncodingException
   * @throws IOException
   */
  public void open(Path path) throws FileNotFoundException,
                                     UnsupportedEncodingException,
                                     IOException {
    close();
    this.path = path;
    this.fos = new FileOutputStream(this.path.toFile());
    this.bw = new BufferedWriter(new OutputStreamWriter(this.fos, StandardCharsets.UTF_8));
  }

  /**
   * Closes the file.
   *
   * @throws IOException
   */
  public void close() throws IOException {
    if (this.bw != null) {
      this.bw.flush();
      this.bw.close();
    }
    if (this.fos != null) {
      this.fos.close();
    }
    this.bw = null;
    this.fos = null;
    this.path = null;
  }

  /**
   * Writes a string to the file.
   *
   * @param str string to write
   * @param appendNewline whether a newline should be appended to the specified string
   * @throws IOException
   */
  public void write(String str, boolean appendNewline) throws IOException {
    if (appendNewline) {
      str += AdakiteUtils.newline();
    }
    this.bw.write(str);
  }

  /**
   * Writes a string to the file. A newline will NOT be appended by default.
   *
   * @param str string to write
   * @see #write(String, boolean)
   * @throws IOException
   */
  public void write(String str) throws IOException {
    write(str, false);
  }

  /**
   * Writes a string to the file. A newline will be appended.
   *
   * @param str string to write
   * @see #write(String, boolean)
   * @throws IOException
   */
  public void writeLine(String str) throws IOException {
    write(str, true);
  }

  /**
   * Returns the associated Path object.
   */
  public Path getPath() {
    return this.path;
  }

}
