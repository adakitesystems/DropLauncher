package adakite.util;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Class for monitoring a directory structure.
 */
public class DirectoryMonitor {

  private Path path;
  private List<Path> prevFiles;
  private List<Path> currFiles;
  private List<Path> newFiles;

  private DirectoryMonitor() {}

  public DirectoryMonitor(Path directory) {
    this.path = directory;
    this.prevFiles = new ArrayList<>();
    this.currFiles = new ArrayList<>();
    this.newFiles = new ArrayList<>();
  }

  /**
   * Returns the path to the currently monitored directory.
   */
  public Path getPath() {
    return this.path;
  }

  /**
   * Returns the contents of the directory observed at the last {@link #reset()}.
   */
  public List<Path> getPreviousFiles() {
    return this.prevFiles;
  }

  /**
   * Returns the contents of the directory observed at the last
   * {@link #reset()} or {@link #update()}.
   */
  public List<Path> getCurrentFiles() {
    return this.currFiles;
  }

  /**
   * Returns the contents of the directory observed at the last
   * {@link #reset()} or {@link #update()}.
   */
  public List<Path> getNewFiles() {
    return this.newFiles;
  }

  /**
   * Resets the new files list and refreshes both previous and current files
   * lists.
   *
   * @throws IOException if an I/O error occurs
   */
  public void reset() throws IOException {
    this.newFiles.clear();
    this.prevFiles = Arrays.asList(AdakiteUtils.getDirectoryContents(this.path));
    this.currFiles = new ArrayList<>(this.prevFiles);
  }

  /**
   * Refreshes the current files list and fills the new files list by comparing
   * the previous files list to the current files list.
   *
   * @see #getNewFiles()
   * @throws IOException if an I/O error occurs
   */
  public void update() throws IOException {
    this.newFiles.clear();
    this.currFiles = Arrays.asList(AdakiteUtils.getDirectoryContents(this.path));
    for (Path currFile : this.currFiles) {
      if (!this.prevFiles.contains(currFile)) {
        this.newFiles.add(currFile);
      }
    }
  }

}
