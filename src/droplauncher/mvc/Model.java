package droplauncher.mvc;

import droplauncher.bwheadless.BWHeadless;
import droplauncher.util.Constants;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.logging.Logger;

public class Model {

  private static final Logger LOGGER = Logger.getLogger(Model.class.getName());
  private static final boolean CLASS_DEBUG = (Constants.DEBUG && true);

  private Controller controller;

  private BWHeadless bwheadless;

  public Model() {
    this.bwheadless = new BWHeadless();
  }

  public void setController(Controller controller) {
    this.controller = controller;
  }

  public void launchBWHeadless() {

  }

  /* ************************************************************ */
  /* Events from Controller */
  /* ************************************************************ */

  public void btnLaunchActionPerformed(ActionEvent evt) {
    launchBWHeadless();
  }

  public void filesDropped(File[] files) {
    for (File file : files) {
      System.out.println("It works! -> " + file.getAbsolutePath());
    }
  }

  /* ************************************************************ */

  /* ************************************************************ */
  /* Events to Controller */
  /* ************************************************************ */

  //...

  /* ************************************************************ */

}
