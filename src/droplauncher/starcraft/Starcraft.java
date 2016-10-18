/* Starcraft.java */

package droplauncher.starcraft;

/**
 * @author Adakite Systems
 * @author adakitesystems@gmail.com
 */
public class Starcraft {

  public enum Race {
    TERRAN("Terran"),
    PROTOSS("Protoss"),
    ZERG("Zerg"),
    RANDOM("Random");

    private final String name;

    private Race(String name) {
      this.name = name;
    }

    @Override
    public String toString() {
      return name;
    }
  }

  /* Maximum profile name length in Broodwar 1.16.1 */
  public static final int MAX_PROFILE_NAME_LENGTH = 24;

  private Starcraft() {}

}
