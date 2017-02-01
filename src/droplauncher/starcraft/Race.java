package droplauncher.starcraft;

/**
 * Enum for races in StarCraft.
 */
public enum Race {

  TERRAN("Terran"),
  PROTOSS("Protoss"),
  ZERG("Zerg"),
  RANDOM("Random"),
  NONE("")
  ;

  private String name;

  private Race(String name) {
    this.name = name;
  }

  @Override
  public String toString() {
    return this.name;
  }

}