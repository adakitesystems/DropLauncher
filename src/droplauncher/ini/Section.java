package droplauncher.ini;

import droplauncher.util.Settings;

public class Section {

  private String name;
  private Settings settings;

  public Section() {
    init();
  }

  public Section(String name) {
    init();
    this.name = name;
  }

  private void init() {
    this.name = null;
    this.settings = new Settings();
  }

  public Settings getSettings() {
    return this.settings;
  }

  public void setSettings(Settings settings) {
    this.settings = settings;
  }

}
