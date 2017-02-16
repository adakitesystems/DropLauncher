package droplauncher.starcraft;

import java.util.Locale;

/**
 * Enum for race selections in StarCraft.
 */
public enum Race {

  TERRAN("Terran"),
  PROTOSS("Protoss"),
  ZERG("Zerg"),
  RANDOM("Random"),
  ;

  private final String str;

  private Race(String str) {
    this.str = str;
  }

  /**
   * Returns the corresponding Race object.
   *
   * @param str specified string
   * @return
   *     the corresponding Race object if valid,
   *     otherwise null
   */
  public static Race get(String str) {
    str = str.toLowerCase(Locale.US);
    for (Race val : Race.values()) {
      if (str.equals(val.toString().toLowerCase(Locale.US))) {
        return val;
      }
    }
    return null;
  }

  @Override
  public String toString() {
    return this.str;
  }

}
