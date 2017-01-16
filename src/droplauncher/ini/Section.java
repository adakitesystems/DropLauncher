package droplauncher.ini;

import java.util.HashMap;

public class Section {

  private String name;
  private HashMap<String, String> keys;

  public Section() {
    init();
  }

  public Section(String name) {
    init();
    this.name = name;
  }

  private void init() {
    this.name = SectionName.NONE.toString();
    this.keys = new HashMap<>();
  }

  public String getName() {
    return this.name;
  }

  public HashMap<String, String> getKeys() {
    return this.keys;
  }

}
