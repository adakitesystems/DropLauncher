package adakite.util;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Class for storing an entire plain text file in memory as an ArrayList of
 * String objects for each line.
 */
public class MemoryFile  {

  private Path path;
  private List<String> lines;

  public MemoryFile() {
    this.path = null;
    this.lines = new ArrayList<>();
  }

  /**
   * Clears the memory file.
   */
  public void clear() {
    this.path = null;
    this.lines.clear();
  }

  /**
   * Returns a copy of the associated File object.
   */
  public Path getPath() {
    return this.path;
  }

  /**
   * Returns the currently stored lines.
   */
  public List<String> getLines() {
    return this.lines;
  }

  /**
   * Clears the current memory file and reads the specified
   * file into the memory file.
   *
   * @param path specified file to read/create
   * @throws IOException
   */
  public void read(Path path) throws IOException {
    clear();
    this.path = path;
    this.lines = Files.readAllLines(this.path, StandardCharsets.UTF_8);
  }

  /**
   * Dumps the currently stored lines to the specified file.
   *
   * @param path the specified file to dump lines
   * @throws FileNotFoundException
   */
  public void dumpToFile(Path path) throws FileNotFoundException, IOException {
    try (
        FileOutputStream fos = new FileOutputStream(path.toString());
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos, StandardCharsets.UTF_8))
    ) {
      for (String line : this.lines) {
        bw.write(line + AdakiteUtils.newline());
      }
    }
  }

  /**
   * Dumps the currently stored lines to the stored File object.
   *
   * @see #dumpToFile(java.nio.file.Path)
   * @throws UnsupportedEncodingException
   * @throws IOException
   */
  public void dumpToFile() throws IOException {
    this.dumpToFile(this.path);
  }

  /**
   * Dumps the currently stored lines to the console.
   */
  public void dumpToConsole() {
    if (this.lines.size() < 1) {
      return;
    }
    StringBuilder sb = new StringBuilder();
    this.lines.forEach((line) -> {
      sb.append(line).append(System.lineSeparator());
    });
    System.out.print(sb.toString());
  }

}