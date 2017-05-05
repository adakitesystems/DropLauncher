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

import adakite.exception.InvalidStateException;
import adakite.prefs.Prefs;
import droplauncher.starcraft.Starcraft;
import droplauncher.util.DropLauncher;
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

    @Override
    public String toString() {
      return this.str;
    }

  }

  /**
   * Enum for BWAPI versions.
   */
  public enum Version {

    VER_374,
    VER_375,
    VER_401B,
    VER_410B,
    VER_411B,
    VER_412,
    VER_420
    ;

    /**
     * Returns the MD5 checksum of the matching BWAPI.dll.
     */
    public String getMD5Checksum() {
      switch (this) {
        case VER_374:  return "6e940dc6acc76b6e459b39a9cdd466ae";
        case VER_375:  return "5e590ea55c2d3c66a36bf75537f8655a";
        case VER_401B: return "84f413409387ae80a4b4acc51fed3923";
        case VER_410B: return "4814396fba36916fdb7cf3803b39ab51";
        case VER_411B: return "5d5128709ba714aa9c6095598bcf4624";
        case VER_412:  return "1364390d0aa085fba6ac11b7177797b0";
        case VER_420:  return "2f6fb401c0dcf65925ee7ad34dc6414a";
        default:
          throw new IllegalArgumentException();
      }
    }

    @Override
    public String toString() {
      String ret = "";
      String name = this.name();

      int index = name.indexOf('_');
      if (index >= 0) {
        name = name.substring(index + 1, name.length());
      }

      ret += name.charAt(0);
      for (int i = 1; i < name.length(); i++) {
        try {
          /* If previous character is a number, append a period to separate
             version sections. */
          Integer.parseInt("" + name.charAt(i - 1));
          ret += ".";
        } catch (Exception ex) {
          /* Previous character is not a number. */
          /* Do nothing. */
        }
        ret += name.charAt(i);
      }

      return ret.toLowerCase(Locale.US);
    }

  }

  /**
   * Enum for extractable files.
   *
   * sha256 checksums:
   * 60e69e90943073696458cd2e5ace9baa39c542cb4ec1ab1d2adfe5dba18035ee *Broodwar.map
   * d16c2909993b9de89b8a285c89cf17e8b16375c876d3ebfd443fde31c9d504a5 *bwapi.ini
   */
  public enum ExtractableFile {

    BROODWAR_MAP("Broodwar.map"), /* Exception Filter input file for BWAPI */
    BWAPI_INI("bwapi.ini")        /* BWAPI configuration file */
    ;

    private final String str;

    private ExtractableFile(String str) {
      this.str = str;
    }

    @Override
    public String toString() {
      return this.str;
    }

  }

  /**
   * Enum for extractable DLLs.
   *
   * sha256 checksums:
   * 28282d77de250e4c7dd7b5ac1563c62d43465a2dda19119f551b45e7dc0d2aa7 *gmp-vc90-mt-gd.dll
   * 9f45860228df80656d2c7407a0fa6d82c1b759d47c7dee843877700740eb4b58 *gmp-vc90-mt.dll
   * 9be85bd8468363703304d0bbd059c9709dba270d0ff5a1a94823cb5dbbfa5f20 *libgmp-10.dll
   * 5a72d472e892efd7d94ea287eda354637394805c2f445edec051b5c0a3d0f55b *libmpfr-4.dll
   * b1873ca36d8ff3f0df0bbf1895916cc13e4bb95588e0e711c689bd37ead8100f *mpfr-vc90-mt-gd.dll
   * 647760f4b63ce1a4c36de4c71176f59cfccdfbb9ad397979725228272c8c67ae *mpfr-vc90-mt.dll
   */
  public enum ExtractableDll {

    LIBGMP("libgmp-10.dll"),
    GMP("gmp-vc90-mt.dll"),
    GMP_GD("gmp-vc90-mt-gd.dll"),
    LIBMPFR("libmpfr-4.dll"),
    MPFR("mpfr-vc90-mt.dll"),
    MPFR_GD("mpfr-vc90-mt-gd.dll")
    ;

    private final String str;

    private ExtractableDll(String str) {
      this.str = str;
    }

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
  public static final Path DATA_DATA_PATH = DATA_PATH.resolve(Paths.get("data")); /* mostly for Broodwar.map */
  public static final String DLL_UNKNOWN = "Unknown";

  public static final Prefs PREF_ROOT = DropLauncher.PREF_ROOT.getChild("bwapi");

  private BWAPI() {}

  /**
   * Returns the BWAPI version associated with the specified MD5 checksum
   * of target BWAPI.dll file.
   *
   * @param dllChecksum specified MD5 checksum of target BWAPI.dll
   * @return
   *     the BWAPI version if known,
   *     otherwise an unknown-type indication string
   */
  public static String getBwapiVersion(String dllChecksum) {
    dllChecksum = dllChecksum.trim().toLowerCase(Locale.US);
    for (Version checksumValue : Version.values()) {
      if (dllChecksum.equals(checksumValue.getMD5Checksum())) {
        return checksumValue.toString();
      }
    }
    return DLL_UNKNOWN;
  }

  /**
   * Returns the path to the "StarCraft/bwapi-data/" directory.
   * The StarCraft directory is determined by {@link Starcraft#getPath()}.
   *
   * @see Starcraft#getPath()
   * @throws adakite.exception.InvalidStateException
   */
  public static Path getPath() throws InvalidStateException {
    Path starcraftDirectory = Starcraft.getPath();
    return (starcraftDirectory == null) ? null : starcraftDirectory.resolve(BWAPI.DATA_PATH);
  }

}
