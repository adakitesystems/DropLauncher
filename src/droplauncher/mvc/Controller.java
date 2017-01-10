package droplauncher.mvc;

import droplauncher.util.Constants;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.logging.Logger;
import javax.swing.JFileChooser;

public class Controller {

  private static final Logger LOGGER = Logger.getLogger(Controller.class.getName());
  private static final boolean CLASS_DEBUG = (Constants.DEBUG && true);

  private Model model;
  private View view;

  public Controller() {

  }

  public void addModel(Model model) {
    this.model = model;
  }

  public void addView(View view) {
    this.view = view;
  }

  /* ************************************************************ */
  /* Events from Model */
  /* ************************************************************ */

  //...

  /* ************************************************************ */

  /* ************************************************************ */
  /* Events to Model */
  /* ************************************************************ */

  //...

  /* ************************************************************ */

  /* ************************************************************ */
  /* Events from View */
  /* ************************************************************ */

  public void btnLaunchActionPerformed(ActionEvent evt) {
    this.model.btnLaunchActionPerformed(evt);
  }

  public void filesDropped(File[] files) {
    this.model.filesDropped(files);
  }

  public void rbRaceProtossActionPerformed(ActionEvent evt) {
    this.model.rbRaceProtossActionPerformed(evt);
  }

  public void rbRaceRandomActionPerformed(ActionEvent evt) {
    this.model.rbRaceRandomActionPerformed(evt);
  }

  public void rbRaceTerranActionPerformed(ActionEvent evt) {
    this.model.rbRaceTerranActionPerformed(evt);
  }

  public void rbRaceZergActionPerformed(ActionEvent evt) {
    this.model.rbRaceZergActionPerformed(evt);
  }

  public void txtBotNameKeyPressed(KeyEvent evt) {
    this.model.txtBotNameKeyPressed(evt);
  }

  public void txtBotNameKeyReleased(KeyEvent evt) {
    this.model.txtBotNameKeyReleased(evt);
  }

  public void txtStarcraftExeMousePressed(MouseEvent evt) {
    this.model.txtStarcraftExeMousePressed(evt);
  }

  /* ************************************************************ */

  /* ************************************************************ */
  /* Events to View */
  /* ************************************************************ */

  public int showFileChooser(JFileChooser fc) {
    return this.view.showFileChooser(fc);
  }

  /* ************************************************************ */

}
