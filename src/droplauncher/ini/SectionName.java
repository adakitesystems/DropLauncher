package droplauncher.ini;

/**
 * Enum for predefined INI section names.
 */
public enum SectionName {

  NONE("")
  ;

  private String str;

  private SectionName(String str) {
    this.str = str;
  }

  @Override
  public String toString() {
    return this.str;
  }

}