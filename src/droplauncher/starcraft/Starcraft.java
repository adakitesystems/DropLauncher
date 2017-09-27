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
import adakite.util.AdakiteUtils;
import droplauncher.mvc.model.Model;
import droplauncher.starcraft.exception.MissingStarcraftExeException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;

/**
 * Utilities and constants class for StarCraft.
 */
public class Starcraft {

  public enum PropertyKey {

    /**
     * Path to StarCraft.exe
     */
    STARCRAFT_EXE("starcraft_exe"),

    /**
     * Whether runtime files should be deleted from the StarCraft directory.
     */
    CLEAN_SC_DIR("clean_sc_dir"),

    /**
     * Whether to extract bot dependencies to the StarCraft directory.
     * E.g. BWTA DLLs, JNIBWAPI dlls, etc.
     * This does not include BWAPI-related files such as "Broodwar.map".
     */
    EXTRACT_BOT_DEPENDENCIES("extract_bot_dependencies"),

    /**
     * Whether to check if the specified StarCraft.exe version is supported.
     */
    CHECK_FOR_SUPPORTED_VERSION("check_version")

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

  public static final String NAME = "StarCraft";
  public static final String EXPANSION_NAME = "Brood War";
  public static final String FULL_EXPANSION_NAME = NAME + ": " + EXPANSION_NAME;
  public static final String BINARY_FILENAME = "StarCraft.exe";

//  public static final Prefs PREF_ROOT = DropLauncher.PREF_ROOT.getChild("starcraft");

  public static final String REG_ENTRY_EXE_32BIT = "HKEY_LOCAL_MACHINE\\SOFTWARE\\Blizzard Entertainment\\Starcraft";
//  public static final String REG_ENTRY_EXE_64BIT = ""; //TODO

  /* Maximum profile name length in Brood War 1.16.1 */
  public static final int MAX_PROFILE_NAME_LENGTH = 24;

  public static final String BW_1161_STRING_SEARCH_KEY = "C:\\projects\\Legacy\\trunk\\Starcraft\\Starcraft1.16.1.build\\Build\\DebugInfo\\BroodWar.pdb";
  public static final String BW_1161_HEX_SEARCH_KEY = "433A5C70726F6A656374735C4C65676163795C7472756E6B5C5374617263726166745C537461726372616674312E31362E312E6275696C645C4275696C645C4465627567496E666F5C42726F6F645761722E706462"; /*  */
  public static final byte[] BW_1161_BINARY_SEARCH_KEY = {67, 58, 92, 112, 114, 111, 106, 101, 99, 116, 115, 92, 76, 101, 103, 97, 99, 121, 92, 116, 114, 117, 110, 107, 92, 83, 116, 97, 114, 99, 114, 97, 102, 116, 92, 83, 116, 97, 114, 99, 114, 97, 102, 116, 49, 46, 49, 54, 46, 49, 46, 98, 117, 105, 108, 100, 92, 66, 117, 105, 108, 100, 92, 68, 101, 98, 117, 103, 73, 110, 102, 111, 92, 66, 114, 111, 111, 100, 87, 97, 114, 46, 112, 100, 98};

  private Starcraft() {}

  /**
   * Returns a filtered string compatible with a StarCraft profile name.
   *
   * @param str specified string
   */
  public static String sanitizeProfileName(String str) {
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
  public static Path getDirectory() throws MissingStarcraftExeException {
    if (Model.getSettings().hasValue(Starcraft.PropertyKey.STARCRAFT_EXE.toString())) {
      return AdakiteUtils.getParentDirectory(Paths.get(Model.getSettings().getValue(Starcraft.PropertyKey.STARCRAFT_EXE.toString())));
    } else {
      throw new MissingStarcraftExeException();
    }
  }

  public static Path getExe() throws MissingStarcraftExeException {
    return getDirectory().resolve(Paths.get(Starcraft.BINARY_FILENAME));
  }

  /**
   * Tests whether a chunk of bytes found in the BW 1.16.1 executable is
   * present in the specified file. Note: This method is just quick and naive for
   * checking the executable version and may return a false positive.
   *
   * @param file specified path to file
   * @throws IOException
   */
  public static boolean isBroodWar1161(Path file) throws IOException {
    byte[] bytes = Files.readAllBytes(file);
    return (bytes.length < (2 * 1024 * 1024) /* StarCraft.exe 1.16.1 is 1.164 MiB. */
        && (bytes.length > Starcraft.BW_1161_BINARY_SEARCH_KEY.length)
        && AdakiteUtils.contains(bytes, Starcraft.BW_1161_BINARY_SEARCH_KEY));
  }

}
