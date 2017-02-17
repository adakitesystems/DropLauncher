/*
 * Copyright (C) 2017 Adakite
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package droplauncher.bwheadless;

import adakite.util.AdakiteUtils;
import java.nio.file.Path;
import java.util.Locale;

/**
 * Container class for bot files.
 */
public class BotFile {

  public enum Type {
    DLL,
    CLIENT,
    UNKNOWN
  }

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
        /* Fall through. */
      case "jar":
        this.type = Type.CLIENT;
        break;
      default:
        this.type = Type.UNKNOWN;
        break;
    }
  }

}
