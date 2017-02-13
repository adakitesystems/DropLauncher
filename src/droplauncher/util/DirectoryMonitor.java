package droplauncher.util;

import adakite.util.AdakiteUtils;
import java.io.IOException;
import java.nio.file.Path;

public class DirectoryMonitor {

  private Path path;
  private Path[] prevFiles;
  private Path[] currFiles;
  private Path[] newFiles;

  private DirectoryMonitor() {}

  public DirectoryMonitor(Path directory) {
    this.path = directory;
    this.prevFiles = new Path[]{};
    this.currFiles = new Path[]{};
    this.newFiles = new Path[]{};
  }

  public Path getPath() {
    return this.path;
  }

  public Path[] getPreviousFiles() {
    return this.prevFiles;
  }

  public Path[] getCurrentFiles() {
    return this.currFiles;
  }

  public Path[] getNewFiles() {
    return this.newFiles;
  }

  public void reset() throws IOException {
    this.prevFiles = AdakiteUtils.getDirectoryContents(this.path);
    this.currFiles = AdakiteUtils.getDirectoryContents(this.path);
    this.newFiles = AdakiteUtils.getDirectoryContents(this.path);
  }



}
