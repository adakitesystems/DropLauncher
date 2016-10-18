/* Race.java */

package droplauncher.starcraft;

/**
 * @author Adakite Systems
 * @author adakitesystems@gmail.com
 */
public final class Races {

  private String str;

  private Races(String str) {
    this.str = str;
  }

  @Override
  public String toString() {
    return this.str;
  }

  public static final Races TERRAN  = new Races("Terran");
  public static final Races ZERG    = new Races("Zerg");
  public static final Races PROTOSS = new Races("Protoss");
  public static final Races RANDOM  = new Races("Random");

}