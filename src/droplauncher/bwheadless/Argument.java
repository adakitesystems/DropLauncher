package droplauncher.bwheadless;

/**
 * Enum for arguments passed to the bwheadless.exe process.
 */
public enum Argument {

  STARCRAFT_EXE("-e"), /* requires second string */
  JOIN_GAME("-j"),
  BOT_NAME("-n"), /* requires second string */
  BOT_RACE("-r"), /* requires second string */
  LOAD_DLL("-l"), /* requires second string */
  ENABLE_LAN("--lan"),
  STARCRAFT_INSTALL_PATH("--installpath"); /* requires second string */

  private String name;

  private Argument(String name) {
    this.name = name;
  }

  @Override
  public String toString() {
    return name;
  }

}
