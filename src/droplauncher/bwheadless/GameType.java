package droplauncher.bwheadless;

public final class GameType {

  private String str;

  private GameType(String str) {
    this.str = str;
  }

  @Override
  public String toString() {
    return this.str;
  }

  public static final GameType LAN = new GameType("lan");
  public static final GameType LOCAL_PC = new GameType("localpc");

}
