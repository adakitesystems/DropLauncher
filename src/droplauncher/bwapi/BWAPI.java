package droplauncher.bwapi;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;

/**
 * Utilities and constants class for BWAPI.
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
    } else {
      return Checksum.UNKNOWN.getName();
    }
  }

}
