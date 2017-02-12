package droplauncher.mvc.view;

/**
 * Enum for the launch button text states.
 */
public enum LaunchButtonText {

  LAUNCH("Launch"),
  EJECT("Eject")
  ;

  private final String str;

  private LaunchButtonText(String str) {
    this.str = str;
  }

  @Override
  public String toString() {
    return this.str;
  }

}
