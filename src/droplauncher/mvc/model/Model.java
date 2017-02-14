package droplauncher.mvc.model;

import adakite.ini.INI;
import adakite.util.AdakiteUtils;
import droplauncher.bwheadless.BWHeadless;
import droplauncher.util.Constants;
import java.io.IOException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Model {

  private static final Logger LOGGER = LogManager.getLogger();

  private INI ini;
  private BWHeadless bwheadless;

  public Model() {
    this.ini = new INI();
    this.bwheadless = new BWHeadless();

    this.bwheadless.setINI(this.ini);
    try {
      if (!AdakiteUtils.fileExists(Constants.DROPLAUNCHER_INI_PATH)) {
        AdakiteUtils.createFile(Constants.DROPLAUNCHER_INI_PATH);
      }
      this.ini.read(Constants.DROPLAUNCHER_INI_PATH);
      this.bwheadless.parseSettings(this.ini);
    } catch (IOException ex) {
      LOGGER.error(ex);
    }
  }

  public INI getINI() {
    return this.ini;
  }

  public BWHeadless getBWHeadless() {
    return this.bwheadless;
  }

}
