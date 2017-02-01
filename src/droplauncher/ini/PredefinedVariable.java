package droplauncher.ini;

public enum PredefinedVariable {

  JAVA_EXE("java_exe")
  ;

  private String str;

  private PredefinedVariable(String str) {
    this.str = str;
  }

  @Override
  public String toString() {
    return this.str;
  }

}
