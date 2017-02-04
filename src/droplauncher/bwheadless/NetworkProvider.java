package droplauncher.bwheadless;

/**
 * Enum for the passable network argument to the bwheadless process.
 * Currently, only LAN is supported since LocalPC requires admin privileges.
 */
public enum NetworkProvider {

  LAN("lan"),
  ;

  private String str;

  private NetworkProvider(String str) {
    this.str = str;
  }

  /**
   * Returns the corresponding NetworkProvider object.
   *
   * @param str specified string
   * @return
   *     the corresponding NetworkProvider object,
   *     otherwise null if no match was found
   */
  public static NetworkProvider get(String str) {
    str = str.toLowerCase();
    for (NetworkProvider val : NetworkProvider.values()) {
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
