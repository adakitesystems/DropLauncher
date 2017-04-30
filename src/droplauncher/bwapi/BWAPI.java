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

  public enum Property {

    COPY_WRITE_READ("bwapi_write_read"),
    WARN_UNKNOWN_BWAPI_DLL("warn_bwapi_dll")
    ;

    private final String str;

    private Property(String str) {
      this.str = str;
    }

    public String toString() {
      return this.str;
    }

  }

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

  /**
   * Enum for extractable files.
   *
   * sha256 checksums:
   * 60e69e90943073696458cd2e5ace9baa39c542cb4ec1ab1d2adfe5dba18035ee *Broodwar.map
   * d16c2909993b9de89b8a285c89cf17e8b16375c876d3ebfd443fde31c9d504a5 *bwapi.ini
   * 28282d77de250e4c7dd7b5ac1563c62d43465a2dda19119f551b45e7dc0d2aa7 *gmp-vc90-mt-gd.dll
   * 9f45860228df80656d2c7407a0fa6d82c1b759d47c7dee843877700740eb4b58 *gmp-vc90-mt.dll
   * b1873ca36d8ff3f0df0bbf1895916cc13e4bb95588e0e711c689bd37ead8100f *mpfr-vc90-mt-gd.dll
   * 647760f4b63ce1a4c36de4c71176f59cfccdfbb9ad397979725228272c8c67ae *mpfr-vc90-mt.dll
   */
  public enum ExtractableFile {

    BROODWAR_MAP("Broodwar.map"),  /* Exception Filter input file for BWAPI */
    BWAPI_INI("bwapi.ini"),        /* BWAPI configuration file */
    GMP("gmp-vc90-mt.dll"),        /* BWTA dependency */
    GMP_GD("gmp-vc90-mt-gd.dll"),  /* BWTA dependency */
    MPFR("mpfr-vc90-mt.dll"),      /* BWTA dependency */
    MPFR_GD("mpfr-vc90-mt-gd.dll") /* BWTA dependency */
    ;

    private final String str;

    private ExtractableFile(String str) {
      this.str = str;
    }

    public String toString() {
      return this.str;
    }

  }

  public static final Path DATA_PATH = Paths.get("bwapi-data");
  public static final Path DATA_AI_PATH = DATA_PATH.resolve(Paths.get("AI"));
  public static final Path DATA_INI_PATH = DATA_PATH.resolve(Paths.get("bwapi.ini"));
  public static final Path DATA_READ_PATH = DATA_PATH.resolve(Paths.get("read"));
  public static final Path DATA_WRITE_PATH = DATA_PATH.resolve(Paths.get("write"));
  public static final Path DATA_DATA_PATH = DATA_PATH.resolve(Paths.get("data")); /* mostly for Broodwar.map */
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
