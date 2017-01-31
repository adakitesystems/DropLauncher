package droplauncher.bwapi;

import adakite.md5sum.MD5Checksum;
import java.io.File;

public class BWAPI {

  public static final String BWAPI_DATA_DIR = "bwapi-data";
  public static final String BWAPI_DATA_AI_DIR = BWAPI_DATA_DIR + File.separator + "AI";
  public static final String BWAPI_DATA_INI = BWAPI_DATA_DIR + File.separator + "bwapi.ini";
  public static final String BWAPI_DATA_DIR_READ = BWAPI_DATA_DIR + File.separator + "read";
  public static final String BWAPI_DATA_DIR_WRITE = BWAPI_DATA_DIR + File.separator + "write";
  public static final String BWAPI_DLL_UNKNOWN = "Unknown";

  private BWAPI() {}

  /**
   * Returns the BWAPI version for the specified file. This parameter
   * should be a "BWAPI.dll" file.
   *
   * @param file specified file
   * @return
   *     the BWAPI version if known,
   *     otherwise error string
   */
  public static String getBwapiVersion(File file) {
    if (file == null) {
      return BWAPI_DLL_UNKNOWN;
    }

    String checksum = MD5Checksum.get(file);
    String version;

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
    } else {
      version = BWAPI_DLL_UNKNOWN;
    }

    return version;
  }

}
