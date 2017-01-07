package droplauncher.mvc;

import droplauncher.bwheadless.BwHeadless;
import java.awt.event.ActionEvent;
import java.io.File;

public class Model {

  private Controller controller;

  private BwHeadless bwheadless;

  public Model() {
    this.bwheadless = new BwHeadless();
  }

  public void setController(Controller controller) {
    this.controller = controller;
  }

  public void launchBwHeadless() {
    if (this.bwheadless.getPipe().isOpen()) {
      this.bwheadless.close();
    } else {
      this.bwheadless.launch();
    }
  }

  /* ************************************************************ */
  /* Events from Controller */
  /* ************************************************************ */

  public void btnLaunchActionPerformed(ActionEvent evt) {
    launchBwHeadless();
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
