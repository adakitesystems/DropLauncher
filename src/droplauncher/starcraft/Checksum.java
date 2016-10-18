package droplauncher.starcraft;

/**
 * @author Adakite Systems
 * @author adakitesystems@gmail.com
 */
public final class Checksum {

  private String str;

  private Checksum(String str) {
    this.str = str;
  }

  @Override
  public String toString() {
    return this.str;
  }

  public static final Checksum BWAPI_DLL_374  = new Checksum("6e940dc6acc76b6e459b39a9cdd466ae");
  public static final Checksum BWAPI_DLL_375  = new Checksum("5e590ea55c2d3c66a36bf75537f8655a");
  public static final Checksum BWAPI_DLL_401B = new Checksum("84f413409387ae80a4b4acc51fed3923");
  public static final Checksum BWAPI_DLL_410B = new Checksum("4814396fba36916fdb7cf3803b39ab51");
  public static final Checksum BWAPI_DLL_411B = new Checksum("5d5128709ba714aa9c6095598bcf4624");
  public static final Checksum BWAPI_DLL_412  = new Checksum("1364390d0aa085fba6ac11b7177797b0");

}