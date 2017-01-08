package droplauncher.mvc;

import droplauncher.util.Constants;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.logging.Logger;

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

  public void rbRaceTerranActionPerformed(ActionEvent evt) {
    //TODO
  }

  public void rbRaceProtossActionPerformed(ActionEvent evt) {
    //TODO
  }

  public void rbRaceRandomActionPerformed(ActionEvent evt) {
    //TODO
  }

  public void rbRaceZergActionPerformed(ActionEvent evt) {
    //TODO
  }

  public void txtBotNameKeyReleased(KeyEvent evt) {
    //TODO
//    String input = txtBotName.getText();
//    bwheadless.setBotName(input);
//    String inputCorrected = bwheadless.getBotName();
//    if (!input.equals(inputCorrected)) {
//      txtBotName.setText(inputCorrected);
//      if (this.caretPosition > 0 && this.caretPosition < inputCorrected.length()) {
//        txtBotName.setCaretPosition(this.caretPosition);
//      } else {
//        txtBotName.setCaretPosition(inputCorrected.length());
//      }
//    }
  }

  public void txtStarcraftExeMousePressed(MouseEvent evt) {
    //TODO
//    JFileChooser fc = new JFileChooser();
//    if (fc.showOpenDialog(MainWindow.this) == JFileChooser.APPROVE_OPTION) {
//      File file = fc.getSelectedFile();
//      if (file != null) {
//        if (bwheadless.setStarcraftExe(file)) {
//          txtStarcraftExe.setText(file.getAbsolutePath());
//        } else {
//          txtStarcraftExe.setText("");
//        }
//      }
//    }
  }

  public void txtBotNameKeyPressed(KeyEvent evt) {
    //TODO
  }

  public void filesDropped(File[] files) {
    this.model.filesDropped(files);
  }

  /* ************************************************************ */

  /* ************************************************************ */
  /* Events to View */
  /* ************************************************************ */

  //...

  /* ************************************************************ */

}
