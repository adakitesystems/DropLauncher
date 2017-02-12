package droplauncher.bwheadless;

import java.util.Locale;

/**
 * Enum for how the bot should connect to a game lobby.
 */
public enum ConnectMode {

  JOIN("join"),
  HOST("host")
  ;

  private final String str;

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
    str = str.toLowerCase(Locale.US);
    for (ConnectMode val : ConnectMode.values()) {
      if (str.equals(val.toString().toLowerCase(Locale.US))) {
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
