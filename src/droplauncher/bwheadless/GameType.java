/* GameType.java */

package droplauncher.bwheadless;

/**
 * @author Adakite Systems
 * @author adakitesystems@gmail.com
 */
public enum GameType {

  LAN("lan");

  private String str;

  private GameType(String str) {
    this.str = str;
  }

  @Override
  public String toString() {
    return this.str;
  }


}
