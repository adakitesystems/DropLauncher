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

import adakite.exception.LogicException;
import adakite.prefs.Prefs;
import droplauncher.starcraft.Starcraft;
import droplauncher.starcraft.exception.MissingStarcraftExeException;
import droplauncher.DropLauncher;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;

/**
 * Utilities class for BWAPI-related implementations.
 */
public class BWAPI {

  public enum PropertyKey {

    /**
     * Whether to copy the contents of the "StarCraft/bwapi-data/write/"
     * directory to the "StarCraft/bwapi-data/read/" directory.
     */
    COPY_WRITE_READ("copy_write_read"),

    /**
     * Whether to warn if the specified BWAPI.dll is not recognized.
     */
    WARN_UNKNOWN_BWAPI_DLL("warn_bwapi_dll")
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
   * Enum for BWAPI.dll versions.
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
          throw new LogicException("checksum missing from list");
      }
    }

    /**
     * Returns the version number in a common format.
     * E.g. "4.1.2" instead of "VER_412".
     */
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
   * <p>Enum for extractable files.<br>
   * <br>
   * sha256 checksums:<br>
   * 60e69e90943073696458cd2e5ace9baa39c542cb4ec1ab1d2adfe5dba18035ee *Broodwar.map<br>
   * d16c2909993b9de89b8a285c89cf17e8b16375c876d3ebfd443fde31c9d504a5 *bwapi.ini</p>
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
   * 68122a17f50c8836d5e3086276d35b7d *client-bridge-1_3-x86.dll
   * 08475ed38618f04edcc648729687412a *client-bridge-1_4b-x86.dll
   * baf2f51b05d27eae41a966b963d98781 *client-bridge-1_4c-x86.dll
   * 08475ed38618f04edcc648729687412a *client-bridge-1_4-x86.dll
   * 9a73fc0821d22e9e276ab673664cf061 *client-bridge-1_5-x86.dll
   * 22df5206189ed3fe1b0ece79bfe75a06 *client-bridge-amd64.dll
   * 59ca6ba344b0bfef8a7715b95ed7032f *client-bridge-julien-rame.dll
   * d33de6fe1dcb11bd3516f2a2893c06f8 *client-bridge-x86.dll
   * 08475ed38618f04edcc648729687412a *client-bridge-x86-jni-sp-1-4.dll
   * 59ca6ba344b0bfef8a7715b95ed7032f *client-bridge-x86-lebedser.dll
   * 2ca6cb7747d32962e2465daa89ff6863 *gmp-vc90-mt.dll
   * 84e29a1be54c519c042bb80d13aea823 *gmp-vc90-mt-gd.dll
   * f2a7432cb1e3a574eb4af7678359b44b *libgmp-10.dll
   * 8afb228e4f6458b6628a202d9de9edf8 *libmpfr-4.dll
   * 11f9098c040179efeeb16b66eff1a6ca *mpfr-vc90-mt.dll
   * 71ac7ae0a579099b06f566a84636dd8b *mpfr-vc90-mt-gd.dll
   */
  public enum ExtractableDll {

    /**********************************************************************/
    /* BWTA */
    /**********************************************************************/

    LIBGMP("libgmp-10.dll"),
    GMP("gmp-vc90-mt.dll"),
    GMP_GD("gmp-vc90-mt-gd.dll"),
    LIBMPFR("libmpfr-4.dll"),
    MPFR("mpfr-vc90-mt.dll"),
    MPFR_GD("mpfr-vc90-mt-gd.dll"),

    /**********************************************************************/
    /* JNIBWAPI */
    /**********************************************************************/

    CLIENT_BRIDGE_1_3("client-bridge-1_3-x86.dll"),
    CLIENT_BRIDGE_1_4("client-bridge-1_4-x86.dll"),
    CLIENT_BRIDGE_1_4b("client-bridge-1_4b-x86.dll"),
    CLIENT_BRIDGE_1_4c("client-bridge-1_4c-x86.dll"),
    CLIENT_BRIDGE_1_5("client-bridge-1_5-x86.dll"),
    CLIENT_BRIDGE_AMD64("client-bridge-amd64.dll"),
    CLIENT_BRIDGE_JULIEN_RAME("client-bridge-julien-rame.dll"),
    CLIENT_BRIDGE_JNI_SP_1_4("client-bridge-x86-jni-sp-1-4.dll"),
    CLIENT_BRIDGE_LEBEDSER("client-bridge-x86-lebedser.dll"),
    CLIENT_BRIDGE("client-bridge-x86.dll")

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
  public static final Path DATA_DATA_PATH = DATA_PATH.resolve(Paths.get("data")); /* for Broodwar.map */
  public static final String DLL_UNKNOWN = "Unknown";

  public static final Prefs PREF_ROOT = DropLauncher.PREF_ROOT.getChild("bwapi");

  public static final String DEFAULT_DLL_FILENAME_RELEASE = "BWAPI.dll";
  public static final String DEFAULT_DLL_FILENAME_DEBUG = "BWAPId.dll";

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
   * @throws MissingStarcraftExeException
   */
  public static Path getPath() throws MissingStarcraftExeException {
    Path starcraftDirectory = Starcraft.getPath();
    return (starcraftDirectory == null) ? null : starcraftDirectory.resolve(BWAPI.DATA_PATH);
  }

}
