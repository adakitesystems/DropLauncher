package droplauncher.bwapi;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Utilities and constants class for BWAPI.
 */
public class BWAPI {

  public static final Path BWAPI_DATA_PATH = Paths.get("bwapi-data");
  public static final Path BWAPI_DATA_AI_PATH = Paths.get(BWAPI_DATA_PATH.toString(), "AI");
  public static final Path BWAPI_DATA_INI_PATH = Paths.get(BWAPI_DATA_PATH.toString(), "bwapi.ini");
  public static final Path BWAPI_DATA_READ_PATH = Paths.get(BWAPI_DATA_PATH.toString(), "read");
  public static final Path BWAPI_DATA_WRITE_PATH = Paths.get(BWAPI_DATA_PATH.toString(), "write");
  public static final String BWAPI_DLL_UNKNOWN = "Unknown";

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
    checksum = checksum.toLowerCase();
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
    } else {
      return Checksum.UNKNOWN.getName();
    }
  }

}
