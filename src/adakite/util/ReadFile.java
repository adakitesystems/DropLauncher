package adakite.util;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

public class ReadFile {

  private Path path;
  private FileInputStream fis;
  private BufferedReader br;

  public ReadFile() {
    this.br = null;
    this.fis = null;
    this.path = null;
  }

  /**
   * Closes the file.
   *
   * @throws IOException
   */
  public void close() throws IOException {
    if (this.br != null) {
      this.br.close();
    }
    if (this.fis != null) {
      this.fis.close();
    }
    this.br = null;
    this.fis = null;
    this.path = null;
  }

  /**
   * Opens the specified file for reading.
   *
   * @param path specified file to read
   * @throws IOException
   */
  public void open(Path path) throws IOException {
    close();
    this.path = path;
    this.fis = new FileInputStream(this.path.toFile());
    this.br = new BufferedReader(new InputStreamReader(this.fis, StandardCharsets.UTF_8));
  }

  /**
   * Returns the next line in the file.
   *
   * @throws IOException
   */
  public String getNextLine() throws IOException {
    if (this.br == null) {
      return null;
    }
    String line = this.br.readLine();
    return line;
  }

  /**
   * Skips the specified amount of lines in the file.
   *
   * @param n specified amount of lines to skip
   * @throws IOException
   */
  public void skipLines(int n) throws IOException {
    while (n-- > 0 && getNextLine() != null) {
      /* Do nothing. */
    }
  }

  /**
   * Returns the associated Path object.
   */
  public Path getPath() {
    return this.path;
  }

}
