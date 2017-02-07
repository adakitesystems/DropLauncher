package droplauncher.mvc.view;

/**
 * Enum for the launch button text states.
 */
public enum LaunchButtonText {

  LAUNCH("LAUNCH"),
  EJECT("EJECT")
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
