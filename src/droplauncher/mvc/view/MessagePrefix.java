package droplauncher.mvc.view;

/**
 * Enum for prepending strings to messages.
 */
public enum MessagePrefix {

  BWHEADLESS("bwh"),
  CLIENT("client"),
  DROPLAUNCHER("DropLauncher")
  ;

  private final String str;

  private MessagePrefix(String str) {
    this.str = str;
  }

  @Override
  public String toString() {
    return this.str;
  }

}
