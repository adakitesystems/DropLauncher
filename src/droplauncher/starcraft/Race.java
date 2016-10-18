/* Race.java */

package droplauncher.starcraft;

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