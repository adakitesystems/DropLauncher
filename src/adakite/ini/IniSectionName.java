package adakite.ini;

/**
 * Enum for predefined INI section names.
 */
public enum IniSectionName {

  NONE("")
  ;

  private final String str;

  private IniSectionName(String str) {
    this.str = str;
  }

  @Override
  public String toString() {
    return this.str;
  }

}