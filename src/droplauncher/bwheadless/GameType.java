package droplauncher.bwheadless;

/**
 * Enum for game types passable an argument to the bwheadless process.
 * Currently, only LAN is supported since LocalPC requires admin privileges.
 */
public enum GameType {

  LAN("lan");

  private String name;

  private GameType(String name) {
    this.name = name;
  }

  @Override
  public String toString() {
    return this.name;
  }

}
