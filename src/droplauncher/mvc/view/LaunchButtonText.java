package droplauncher.mvc.view;

public enum LaunchButtonText {

  LAUNCH("Launch"),
  EJECT("Eject")
  ;

  private String str;

  private LaunchButtonText(String str) {
    this.str = str;
  }

  @Override
  public String toString() {
    return this.str;
  }
  
}
