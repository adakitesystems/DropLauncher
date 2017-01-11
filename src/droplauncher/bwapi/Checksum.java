package droplauncher.bwapi;

public enum Checksum {

  BWAPI_DLL_374 ("6e940dc6acc76b6e459b39a9cdd466ae"),
  BWAPI_DLL_375 ("5e590ea55c2d3c66a36bf75537f8655a"),
  BWAPI_DLL_401B("84f413409387ae80a4b4acc51fed3923"),
  BWAPI_DLL_410B("4814396fba36916fdb7cf3803b39ab51"),
  BWAPI_DLL_411B("5d5128709ba714aa9c6095598bcf4624"),
  BWAPI_DLL_412 ("1364390d0aa085fba6ac11b7177797b0")
  ;

  private String str;

  private Checksum(String str) {
    this.str = str;
  }

  @Override
  public String toString() {
    return this.str;
  }

}