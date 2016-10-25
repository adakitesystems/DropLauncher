package droplauncher.bwapi;

import droplauncher.starcraft.Checksum;
import droplauncher.tools.MD5Checksum;

import java.io.File;

/**
 * @author Adakite Systems
 * @author adakitesystems@gmail.com
 */
public class Bwapi {

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
    String checksum = new MD5Checksum(file).toString();

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
