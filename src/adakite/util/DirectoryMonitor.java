package adakite.util;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Class for monitoring a directory structure. This class can be used to
 * observe the changes in a directory from time A to time B using the
 * {@link #reset()} and {@link #update()} methods and retrieving the changes
 * via {@link #getNewFiles()}.
 */
public class DirectoryMonitor {

  private Path path;
  private List<Path> prevFiles;
  private List<Path> currFiles;
  private List<Path> newFiles;
  private List<String> ignoreList;

  private DirectoryMonitor() {}

  public DirectoryMonitor(Path directory) {
    this.path = directory;
    this.prevFiles = new ArrayList<>();
    this.currFiles = new ArrayList<>();
    this.newFiles = new ArrayList<>();
    this.ignoreList = new ArrayList<>();
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
   * Returns the ignore list which contains the names of files or directories
   * that should be ignored.
   */
  public List<String> getIgnoreList() {
    return this.ignoreList;
  }

  /**
   * Resets the new files list and refreshes both previous and current files
   * lists.
   *
   * @throws IOException if an I/O error occurs
   */
  public void reset() throws IOException {
    this.newFiles.clear();
    this.prevFiles.clear();

    boolean found;
    Path[] contents = AdakiteUtils.getDirectoryContents(this.path);
    for (Path path : contents) {
      String pathLower = path.toAbsolutePath().toString().toLowerCase(Locale.US);
      found = false;
      for (String str : this.ignoreList) {
        if (pathLower.contains(str.toLowerCase(Locale.US))) {
          found = true;
          break;
        }
      }
      if (!found) {
        this.prevFiles.add(path);
      }
    }

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
    this.currFiles.clear();

    boolean found;
    Path[] contents = AdakiteUtils.getDirectoryContents(this.path);
    for (Path path : contents) {
      String pathLower = path.toAbsolutePath().toString().toLowerCase(Locale.US);
      found = false;
      for (String str : this.ignoreList) {
        if (pathLower.contains(str.toLowerCase(Locale.US))) {
          found = true;
          break;
        }
      }
      if (!found) {
        this.currFiles.add(path);
      }
    }

    for (Path path : this.currFiles) {
      if (!this.prevFiles.contains(path)) {
        this.newFiles.add(path);
      }
    }
  }

}
