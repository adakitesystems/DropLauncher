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
   *
   * @return
   *     the path which represents this object
   * @throws IllegalStateException If path is null. Another possible cause
   *     is if {@link #setPath(java.nio.file.Path) has NOT been called yet.
   */
  public Path getPath() {
    if (this.path == null) {
      throw LOGGER.throwing(new IllegalStateException("path not set"));
    }
    return this.path;
  }

  public void setPath(Path path) {
    this.path = path;
    String ext = AdakiteUtils.getFileExtension(this.path).toLowerCase(Locale.US);
    if (ext.equals("dll")) {
      this.type = Type.DLL;
    } else if (ext.equals("exe")) {
      this.type = Type.CLIENT;
    } else {
      this.type = Type.UNKNOWN;
    }
  }

}
