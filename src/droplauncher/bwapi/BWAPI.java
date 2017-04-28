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

package droplauncher.bwapi;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;

/**
 * Utilities class for BWAPI-related implementations.
 */
public class BWAPI {

  /**
   * Enum for BWAPI.dll MD5 checksums.
   */
  public enum Checksum {

    DLL_374("6e940dc6acc76b6e459b39a9cdd466ae"),
    DLL_375("5e590ea55c2d3c66a36bf75537f8655a"),
    DLL_401B("84f413409387ae80a4b4acc51fed3923"),
    DLL_410B("4814396fba36916fdb7cf3803b39ab51"),
    DLL_411B("5d5128709ba714aa9c6095598bcf4624"),
    DLL_412("1364390d0aa085fba6ac11b7177797b0"),
    DLL_420("2f6fb401c0dcf65925ee7ad34dc6414a"),
    ;

    private final String str;

    private Checksum(String str) {
      this.str = str;
    }

    /**
     * Returns the version in string format.
     */
    public String getName() {
      switch (this) {
        case DLL_374: return "3.7.4";
        case DLL_375: return "3.7.5";
        case DLL_401B: return "4.0.1b";
        case DLL_410B: return "4.1.0b";
        case DLL_411B: return "4.1.1b";
        case DLL_412: return "4.1.2";
        case DLL_420: return "4.2.0";
        default:
          throw new IllegalArgumentException();
      }
    }

    /**
     * Returns the MD5 checksum.
     */
    @Override
    public String toString() {
      return this.str;
    }

  }

  public static final Path DATA_PATH = Paths.get("bwapi-data");
  public static final Path DATA_AI_PATH = DATA_PATH.resolve(Paths.get("AI"));
  public static final Path DATA_INI_PATH = DATA_PATH.resolve(Paths.get("bwapi.ini"));
  public static final Path DATA_READ_PATH = DATA_PATH.resolve(Paths.get("read"));
  public static final Path DATA_WRITE_PATH = DATA_PATH.resolve(Paths.get("write"));
  public static final String DLL_UNKNOWN = "Unknown";

  public static final String DEFAULT_INI_SECTION_NAME = "bwapi";

  private BWAPI() {}

  /**
   * Returns the BWAPI version associated with the specified MD5 checksum.
   *
   * @param dllChecksum specified MD5 checksum
   * @return
   *     the BWAPI version if known,
   *     otherwise an unknown-type indication string
   */
  public static String getBwapiVersion(String dllChecksum) {
    dllChecksum = dllChecksum.toLowerCase(Locale.US);
    for (Checksum checksumValue : Checksum.values()) {
      if (dllChecksum.equals(checksumValue.toString())) {
        return checksumValue.getName();
      }
    }
    return DLL_UNKNOWN;
  }

}
