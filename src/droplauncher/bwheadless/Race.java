/* Race.java */

package droplauncher.bwheadless;

/**
 * @author Adakite Systems
 * @author adakitesystems@gmail.com
 */
public final class Race {

  private String str;

  private Race(String str) {
    this.str = str;
  }

  @Override
  public String toString() {
    return this.str;
  }

  public static final Race TERRAN  = new Race("Terran");
  public static final Race ZERG    = new Race("Zerg");
  public static final Race PROTOSS = new Race("Protoss");
  public static final Race RANDOM  = new Race("Random");

}