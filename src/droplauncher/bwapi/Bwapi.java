/* Bwapi.java  */

package droplauncher.bwapi;

import droplauncher.tools.MD5Checksum;

import java.io.File;

/**
 * @author Adakite Systems
 * @author adakitesystems@gmail.com
 */
public class Bwapi {

  /*
    public static final Checksums BWAPI_DLL_374  = new Checksums("6e940dc6acc76b6e459b39a9cdd466ae");
  public static final Checksums BWAPI_DLL_375  = new Checksums("5e590ea55c2d3c66a36bf75537f8655a");
  public static final Checksums BWAPI_DLL_401B = new Checksums("84f413409387ae80a4b4acc51fed3923");
  public static final Checksums BWAPI_DLL_410B = new Checksums("4814396fba36916fdb7cf3803b39ab51");
  public static final Checksums BWAPI_DLL_411B = new Checksums("5d5128709ba714aa9c6095598bcf4624");
  public static final Checksums BWAPI_DLL_412  = new Checksums("1364390d0aa085fba6ac11b7177797b0");
  */
  public enum Checksum {
    BWAPI_DLL_374 ("6e940dc6acc76b6e459b39a9cdd466ae"),
    BWAPI_DLL_375 ("5e590ea55c2d3c66a36bf75537f8655a"),
    BWAPI_DLL_401B("84f413409387ae80a4b4acc51fed3923"),
    BWAPI_DLL_410B("4814396fba36916fdb7cf3803b39ab51"),
    BWAPI_DLL_411B("5d5128709ba714aa9c6095598bcf4624"),
    BWAPI_DLL_412 ("1364390d0aa085fba6ac11b7177797b0");

    private final String name;

    private Checksum(String name) {
      this.name = name;
    }

    @Override
    public String toString() {
      return name;
    }
  }

  public static final String BWAPI_DATA_DIR = "bwapi-data";
  public static final String BWAPI_DATA_INI =
      BWAPI_DATA_DIR + File.separator + "bwapi.ini";

  private Bwapi() {}

  /**
   * Returns the BWAPI version for the specified file. This parameter
   * should be a "BWAPI.dll" file.
   *
   * @param file specified file
   * @return
   *     the BWAPI version if known,
   *     otherwise false
   */
  public static String getBwapiVersion(File file) {
    String version = null;
    String checksum = MD5Checksum.getMD5Checksum(file);

    if (checksum.equals(Checksum.BWAPI_DLL_374.toString())) {
      version = "3.7.4";
    } else if (checksum.equals(Checksum.BWAPI_DLL_375.toString())) {
      version = "3.7.5";
    } else if (checksum.equals(Checksum.BWAPI_DLL_401B.toString())) {
      version = "4.0.1b";
    } else if (checksum.equals(Checksum.BWAPI_DLL_410B.toString())) {
      version = "4.1.0b";
    } else if (checksum.equals(Checksum.BWAPI_DLL_411B.toString())) {
      version = "4.1.1b";
    } else if (checksum.equals(Checksum.BWAPI_DLL_412.toString())) {
      version = "4.1.2";
    }

    return version;
  }

}
