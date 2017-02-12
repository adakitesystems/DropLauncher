package droplauncher.bwheadless;

import adakite.util.AdakiteUtils;
import java.nio.file.Path;
import java.util.Locale;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Container class for bot files.
 */
public class BotFile {

  public enum Type {
    DLL,
    CLIENT,
    UNKNOWN
  }

  private static final Logger LOGGER = LogManager.getLogger();

  private Type type;
  private Path path;

  public BotFile() {
    this.type = Type.UNKNOWN;
    this.path = null;
  }

  /**
   * Returns the type. E.g. either a DLL file, or a client file (EXE/JAR/etc).
   */
  public Type getType() {
    return this.type;
  }

  /**
   * Returns the path which represents this object.
   */
  public Path getPath() {
    if (this.path == null) {
      throw new IllegalStateException("path not set");
    }
    return this.path;
  }

  /**
   * Sets the path to the bot file.
   *
   * @param path specified path
   */
  public void setPath(Path path) {
    this.path = path;
    String ext = AdakiteUtils.getFileExtension(this.path).toLowerCase(Locale.US);
    switch (ext) {
      case "dll":
        this.type = Type.DLL;
        break;
      case "exe":
        this.type = Type.CLIENT;
        break;
      default:
        this.type = Type.UNKNOWN;
        break;
    }
  }

}
