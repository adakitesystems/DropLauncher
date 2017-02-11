package droplauncher.bwheadless;

/**
 * Enum for passable arguments to bwheadless.
 */
public enum Argument {

  STARCRAFT_EXE("-e"), /* requires second string */
  HOST("-h"),
  GAME_NAME("-g"), /* requires second string */
  JOIN_GAME("-j"),
  MAP("-m"), /* requires second string */
  BOT_NAME("-n"), /* requires second string */
  BOT_RACE("-r"), /* requires second string */
  LOAD_DLL("-l"), /* requires second string */
  ENABLE_LAN("--lan"),
  STARCRAFT_INSTALL_PATH("--installpath") /* requires second string */
  ;

  private String str;

  private Argument(String str) {
    this.str = str;
  }

  @Override
  public String toString() {
    return str;
  }

}
