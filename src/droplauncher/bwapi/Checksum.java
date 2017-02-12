/*
TODO: Move these values to a dedicated text file and read into a HashMap.
*/

package droplauncher.bwapi;

/**
 * Enum for BWAPI.dll MD5 checksums.
 */
public enum Checksum {

  BWAPI_DLL_374("6e940dc6acc76b6e459b39a9cdd466ae"),
  BWAPI_DLL_375("5e590ea55c2d3c66a36bf75537f8655a"),
  BWAPI_DLL_401B("84f413409387ae80a4b4acc51fed3923"),
  BWAPI_DLL_410B("4814396fba36916fdb7cf3803b39ab51"),
  BWAPI_DLL_411B("5d5128709ba714aa9c6095598bcf4624"),
  BWAPI_DLL_412("1364390d0aa085fba6ac11b7177797b0"),
  UNKNOWN("00000000000000000000000000000000")
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
      case BWAPI_DLL_374: return "3.7.4";
      case BWAPI_DLL_375: return "3.7.5";
      case BWAPI_DLL_401B: return "4.0.1b";
      case BWAPI_DLL_410B: return "4.1.0b";
      case BWAPI_DLL_411B: return "4.1.1b";
      case BWAPI_DLL_412: return "4.1.2";
      default: return "Unknown";
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
