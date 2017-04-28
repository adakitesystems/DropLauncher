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

  public static final Path BWAPI_DATA_PATH = Paths.get("bwapi-data");
  public static final Path BWAPI_DATA_AI_PATH = BWAPI_DATA_PATH.resolve(Paths.get("AI"));
  public static final Path BWAPI_DATA_INI_PATH = BWAPI_DATA_PATH.resolve(Paths.get("bwapi.ini"));
  public static final Path BWAPI_DATA_READ_PATH = BWAPI_DATA_PATH.resolve(Paths.get("read"));
  public static final Path BWAPI_DATA_WRITE_PATH = BWAPI_DATA_PATH.resolve(Paths.get("write"));
  public static final String BWAPI_DLL_UNKNOWN = "Unknown";

  public static final String DEFAULT_INI_SECTION_NAME = "bwapi";

  private BWAPI() {}

  /**
   * Returns the BWAPI version associated with the specified MD5 checksum.
   *
   * @param checksum specified MD5 checksum
   * @return
   *     the BWAPI version if known,
   *     otherwise an unknown-type indication string
   */
  public static String getBwapiVersion(String checksum) {
    checksum = checksum.toLowerCase(Locale.US);
    if (checksum.equals(Checksum.BWAPI_DLL_374.toString())) {
      return Checksum.BWAPI_DLL_374.getName();
    } else if (checksum.equals(Checksum.BWAPI_DLL_375.toString())) {
      return Checksum.BWAPI_DLL_375.getName();
    } else if (checksum.equals(Checksum.BWAPI_DLL_401B.toString())) {
      return Checksum.BWAPI_DLL_401B.getName();
    } else if (checksum.equals(Checksum.BWAPI_DLL_410B.toString())) {
      return Checksum.BWAPI_DLL_410B.getName();
    } else if (checksum.equals(Checksum.BWAPI_DLL_411B.toString())) {
      return Checksum.BWAPI_DLL_411B.getName();
    } else if (checksum.equals(Checksum.BWAPI_DLL_412.toString())) {
      return Checksum.BWAPI_DLL_412.getName();
    } else if (checksum.equals(Checksum.BWAPI_DLL_420.toString())) {
      return Checksum.BWAPI_DLL_420.getName();
    } else {
      return Checksum.UNKNOWN.getName();
    }
  }

}
