package droplauncher.bwheadless;

import adakite.util.AdakiteUtils;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Container class for bot DLL or EXE files.
 */
public class BotModule {

  public enum Type {
    DLL,
    CLIENT,
    UNKNOWN
  }

  private Type type;
  private String path;

  public BotModule() {
    init();
    this.path = "";
  }

  public BotModule(String path) {
    init();
    setPath(path);
  }

  private void init() {
    this.type = Type.UNKNOWN;
    this.path = "";
  }

  public Type getType() {
    return this.type;
  }

  public Path getPath() {
    return Paths.get(this.path);
  }

  public void setPath(String path) {
    this.path = path;
    String ext = AdakiteUtils.getFileExtension(getPath()).toLowerCase();
    if (ext.equals("dll")) {
      this.type = Type.DLL;
    } else if (ext.equals("exe") || ext.equals("jar")) {
      this.type = Type.CLIENT;
    } else {
      this.type = Type.UNKNOWN;
    }
  }

  public void setPath(Path path) {
    setPath(path.toAbsolutePath().toString());
  }

  @Override
  public String toString() {
    return this.path;
  }

}
