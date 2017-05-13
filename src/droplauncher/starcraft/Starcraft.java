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

package droplauncher.starcraft;

import adakite.debugging.Debugging;
import adakite.prefs.Prefs;
import adakite.util.AdakiteUtils;
import droplauncher.mvc.model.Model;
import droplauncher.starcraft.exception.MissingStarcraftExeException;
import droplauncher.DropLauncher;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;

/**
 * Utilities and constants class for StarCraft.
 */
public class Starcraft {

  public enum PropertyKey {

    STARCRAFT_EXE("starcraft_exe"),
    CLEAN_SC_DIR("clean_sc_dir"),
    ;

    private final String str;

    private PropertyKey(String str) {
      this.str = str;
    }

    @Override
    public String toString() {
      return this.str;
    }

  }

  /**
   * Enum for race selections in StarCraft.
   */
  public enum Race {

    TERRAN("Terran"),
    PROTOSS("Protoss"),
    ZERG("Zerg"),
    RANDOM("Random")
    ;

    private final String str;

    private Race(String str) {
      this.str = str;
    }

    /**
     * Returns the corresponding Race object.
     *
     * @param str specified string
     * @return
     *     the corresponding Race object if valid,
     *     otherwise null
     */
    public static Race get(String str) {
      str = str.toLowerCase(Locale.US);
      for (Race val : Race.values()) {
        if (str.equals(val.toString().toLowerCase(Locale.US))) {
          return val;
        }
      }
      throw new IllegalArgumentException("Race not found: " + str);
    }

    @Override
    public String toString() {
      return this.str;
    }

    /**
     * Tests if the specified race is known.
     *
     * @param race specified race
     */
    public static boolean isValid(String race) {
      try {
        Race.get(race);
      } catch (Exception ex) {
        return false;
      }
      return true;
    }

  }

  public static final String DEFAULT_EXE_FILENAME = "StarCraft.exe";

  public static final Prefs PREF_ROOT = DropLauncher.PREF_ROOT.getChild("starcraft");

  public static final String REG_ENTRY_EXE_32BIT = "HKEY_LOCAL_MACHINE\\SOFTWARE\\Blizzard Entertainment\\Starcraft";
//  public static final String REG_ENTRY_EXE_64BIT = ""; //TODO

  /* Maximum profile name length in Brood War 1.16.1 */
  public static final int MAX_PROFILE_NAME_LENGTH = 24;

  private Starcraft() {}

  /**
   * Returns a filtered string compatible with a StarCraft profile name.
   *
   * @param str specified string
   */
  public static String cleanProfileName(String str) {
    if (AdakiteUtils.isNullOrEmpty(str, true)) {
      throw new IllegalArgumentException(Debugging.emptyString());
    }

    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < str.length(); i++) {
      char ch = str.charAt(i);
      if ((ch >= 'A' && ch <= 'Z')
          || (ch >= 'a' && ch <= 'z')
          || (ch >= '0' && ch <= '9')
          || ch == ' '
          || ch == '_') {
        sb.append(ch);
      }
    }

    String ret = sb.toString().trim();
    if (ret.length() > MAX_PROFILE_NAME_LENGTH) {
      ret = ret.substring(0, MAX_PROFILE_NAME_LENGTH);
    }
    return ret;
  }

  /**
   * Returns the path to the StarCraft directory.
   *
   * @throws MissingStarcraftExeException if the StarCraft path is not set
   */
  public static Path getPath() throws MissingStarcraftExeException {
    if (Model.hasPrefValue(Starcraft.PropertyKey.STARCRAFT_EXE.toString())) {
      return AdakiteUtils.getParentDirectory(Paths.get(Model.getPref(Starcraft.PropertyKey.STARCRAFT_EXE.toString())));
    } else {
      throw new MissingStarcraftExeException();
    }
  }

  public static Path getExePath() throws MissingStarcraftExeException {
    return getPath().resolve(Paths.get(DEFAULT_EXE_FILENAME));
  }

}
