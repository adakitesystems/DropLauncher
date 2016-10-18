package droplauncher.bwheadless;

/**
 * @author Adakite Systems
 * @author adakitesystems@gmail.com
 */
public final class GameTypes {

  private String str;

  private GameTypes(String str) {
    this.str = str;
  }

  @Override
  public String toString() {
    return this.str;
  }

  public static final GameTypes LAN = new GameTypes("lan");
//  public static final GameTypes LOCAL_PC = new GameTypes("localpc");

}
