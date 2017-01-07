package droplauncher.mvc;

import droplauncher.bwheadless.BwHeadless;
import java.awt.event.ActionEvent;

public class Model {

  private MainWindow view;

  private BwHeadless bwheadless;

  public Model() {

  }

  public void setView(MainWindow view) {
    this.view = view;
  }

  /* ************************************************************ */
  /* Model functions called by the View */
  /* ************************************************************ */

  public void btnTestPressed(ActionEvent evt) {

  }

  /* ************************************************************ */

}
