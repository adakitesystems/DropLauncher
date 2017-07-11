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

    VER_374 ("6e940dc6acc76b6e459b39a9cdd466ae", "3.7.4"),
    VER_375 ("5e590ea55c2d3c66a36bf75537f8655a", "3.7.5"),
    VER_401B("84f413409387ae80a4b4acc51fed3923", "4.0.1b"),
    VER_410B("4814396fba36916fdb7cf3803b39ab51", "4.1.0b"),
    VER_411B("5d5128709ba714aa9c6095598bcf4624", "4.1.1b"),
    VER_412 ("1364390d0aa085fba6ac11b7177797b0", "4.1.2"),
    VER_420 ("2f6fb401c0dcf65925ee7ad34dc6414a", "4.2.0")
    ;

    private final String md5checksum;
    private final String description;

    private Version(String md5checksum, String name) {
      this.md5checksum = md5checksum;
      this.description = name;
    }

    /**
     * Returns the MD5 checksum of the matching BWAPI.dll.
     */
    public String getMD5Checksum() {
      return this.md5checksum;
    }

    /**
     * Returns the version number in a common format.
     * E.g. "4.1.2" instead of "VER_412".
     */
    @Override
    public String toString() {
      return this.description;
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
   * SHA-256 checksums:
   * 9b21735b6e7cd11531cad609abb27eb2e0cacd76f7466c24228dae0ddcf2a7ba *bwapi_bridge2_5.dll
   * ca985bdce5a7c16ad2a397df2c4ac53293c50648aa3474bbbfefa8d2fd8790ed *client-bridge-1_3-x86.dll
   * 75dbc782e66ec8ff127176034da5cd8e0f934fc21a79135d7688689674930bd6 *client-bridge-1_4b-x86.dll
   * e3a6ed46393507f794b051228ff995b8a4eb9d847715d33dde20076cddb23b01 *client-bridge-1_4c-x86.dll
   * 75dbc782e66ec8ff127176034da5cd8e0f934fc21a79135d7688689674930bd6 *client-bridge-1_4-x86.dll
   * 529637ef7bce676f9de869453eda0f57d09af2b9ebf6656bcf772c901c9aa392 *client-bridge-1_5-x86.dll
   * 9a037024258521a3d500caaa3ca59ef9fcf6052818a3e8d2521a26ac7511403d *client-bridge-amd64.dll
   * 732e941a23f6e5925f7837e68a60d535a8585bbd4293d9fe2db08282a8b74c0a *client-bridge-julien-rame.dll
   * 0c1b3c79608dd5a1c25057dc49d0763c5f89077cb4a16e8c0cc0181de363ce91 *client-bridge-x86.dll
   * 75dbc782e66ec8ff127176034da5cd8e0f934fc21a79135d7688689674930bd6 *client-bridge-x86-jni-sp-1-4.dll
   * 732e941a23f6e5925f7837e68a60d535a8585bbd4293d9fe2db08282a8b74c0a *client-bridge-x86-lebedser.dll
   * 9f45860228df80656d2c7407a0fa6d82c1b759d47c7dee843877700740eb4b58 *gmp-vc90-mt.dll
   * 28282d77de250e4c7dd7b5ac1563c62d43465a2dda19119f551b45e7dc0d2aa7 *gmp-vc90-mt-gd.dll
   * 9be85bd8468363703304d0bbd059c9709dba270d0ff5a1a94823cb5dbbfa5f20 *libgmp-10.dll
   * 5a72d472e892efd7d94ea287eda354637394805c2f445edec051b5c0a3d0f55b *libmpfr-4.dll
   * 647760f4b63ce1a4c36de4c71176f59cfccdfbb9ad397979725228272c8c67ae *mpfr-vc90-mt.dll
   * b1873ca36d8ff3f0df0bbf1895916cc13e4bb95588e0e711c689bd37ead8100f *mpfr-vc90-mt-gd.dll
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

    JNI_CLIENT_BRIDGE_1_3("client-bridge-1_3-x86.dll"),
    JNI_CLIENT_BRIDGE_1_4("client-bridge-1_4-x86.dll"),
    JNI_CLIENT_BRIDGE_1_4b("client-bridge-1_4b-x86.dll"),
    JNI_CLIENT_BRIDGE_1_4c("client-bridge-1_4c-x86.dll"),
    JNI_CLIENT_BRIDGE_1_5("client-bridge-1_5-x86.dll"),
    JNI_CLIENT_BRIDGE_AMD64("client-bridge-amd64.dll"),
    JNI_CLIENT_BRIDGE_JULIEN_RAME("client-bridge-julien-rame.dll"),
    JNI_CLIENT_BRIDGE_JNI_SP_1_4("client-bridge-x86-jni-sp-1-4.dll"),
    JNI_CLIENT_BRIDGE_LEBEDSER("client-bridge-x86-lebedser.dll"),
    JNI_CLIENT_BRIDGE("client-bridge-x86.dll"),

    /**********************************************************************/
    /* BWMirror */
    /**********************************************************************/

    BWMIRROR_BWAPI_BRIDGE_2_5("bwapi_bridge2_5.dll")

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
