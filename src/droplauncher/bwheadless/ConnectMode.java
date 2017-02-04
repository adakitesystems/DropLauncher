package droplauncher.bwheadless;

/**
 * Enum for how the bot should connect to a game lobby.
 */
public enum ConnectMode {

  JOIN("join"),
  HOST("host")
  ;

  private String str;

  private ConnectMode(String str) {
    this.str = str;
  }

  /**
   * Returns the corresponding ConnectMode object.
   *
   * @param str specified string
   * @return
   *     the corresponding ConnectMode object,
   *     otherwise null if no match was found
   */
  public static ConnectMode get(String str) {
    str = str.toLowerCase();
    for (ConnectMode val : ConnectMode.values()) {
      if (str.equals(val.toString().toLowerCase())) {
        return val;
      }
    }
    return null;
  }

  @Override
  public String toString() {
    return this.str;
  }

}
