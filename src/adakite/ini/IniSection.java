package adakite.ini;

import java.util.HashMap;

/**
 * Container class for sections of an INI file.
 */
public class IniSection {

  private String name;
  private HashMap<String, String> keys;

  public IniSection() {
    init();
  }

  public IniSection(String name) {
    init();
    this.name = name;
  }

  private void init() {
    this.name = IniSectionName.NONE.toString();
    this.keys = new HashMap<>();
  }

  public String getName() {
    return this.name;
  }

  public HashMap<String, String> getKeys() {
    return this.keys;
  }

}
