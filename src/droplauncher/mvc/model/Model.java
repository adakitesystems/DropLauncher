package droplauncher.mvc.model;

import adakite.ini.Ini;
import adakite.util.AdakiteUtils;
import droplauncher.bwapi.BWAPI;
import droplauncher.bwheadless.BWHeadless;
import droplauncher.util.Constants;
import droplauncher.util.SettingsKey;
import java.io.IOException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Model {

  private static final Logger LOGGER = LogManager.getLogger();

  private Ini ini;
  private BWHeadless bwheadless;

  public Model() {
    this.ini = new Ini();
    this.bwheadless = new BWHeadless();

    this.bwheadless.setINI(this.ini);
  }

  public void setup() throws IOException {
    if (!AdakiteUtils.fileExists(Constants.DROPLAUNCHER_INI_PATH)) {
      AdakiteUtils.createFile(Constants.DROPLAUNCHER_INI_PATH);
    }
    this.ini.read(Constants.DROPLAUNCHER_INI_PATH);
    parseSettings(this.ini);
    this.bwheadless.parseSettings(this.ini);
  }

  public Ini getINI() {
    return this.ini;
  }

  public BWHeadless getBWHeadless() {
    return this.bwheadless;
  }

  private void parseSettings(Ini ini) {
    if (!ini.hasValue(Constants.DROPLAUNCHER_INI_SECTION_NAME, SettingsKey.SHOW_LOG_WINDOW.toString())) {
      ini.set(Constants.DROPLAUNCHER_INI_SECTION_NAME, SettingsKey.SHOW_LOG_WINDOW.toString(), Boolean.FALSE.toString());
    }
    if (!ini.hasValue(BWAPI.DEFAULT_INI_SECTION_NAME, SettingsKey.COPY_WRITE_READ.toString())) {
      ini.set(BWAPI.DEFAULT_INI_SECTION_NAME, SettingsKey.COPY_WRITE_READ.toString(), Boolean.TRUE.toString());
    }
    if (!ini.hasValue(Constants.DROPLAUNCHER_INI_SECTION_NAME, SettingsKey.CLEAN_SC_DIR.toString())) {
      ini.set(Constants.DROPLAUNCHER_INI_SECTION_NAME, SettingsKey.CLEAN_SC_DIR.toString(), Boolean.TRUE.toString());
    }
  }

}
