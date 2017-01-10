package droplauncher.mvc;

import adakite.utils.FileOperation;
import droplauncher.bwheadless.BWHeadless;
import droplauncher.starcraft.Race;
import droplauncher.util.Constants;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

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

  public void startBWHeadless() {

  }

  public void stopBWHeadless() {

  }

  /* ************************************************************ */
  /* Events from Controller */
  /* ************************************************************ */

  public void btnLaunchActionPerformed(ActionEvent evt) {
    startBWHeadless();
  }

  public void filesDropped(File[] files) {
    for (File file : files) {
      System.out.println("It works! -> " + file.getAbsolutePath());
    }
  }

  public void rbRaceProtossActionPerformed(ActionEvent evt) {
    this.bwheadless.getSettings().setBotRace(Race.PROTOSS);
  }

  public void rbRaceRandomActionPerformed(ActionEvent evt) {
    this.bwheadless.getSettings().setBotRace(Race.RANDOM);
  }

  public void rbRaceTerranActionPerformed(ActionEvent evt) {
    this.bwheadless.getSettings().setBotRace(Race.TERRAN);
  }

  public void rbRaceZergActionPerformed(ActionEvent evt) {
    this.bwheadless.getSettings().setBotRace(Race.ZERG);
  }

  public void txtBotNameKeyPressed(KeyEvent evt) {
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
    //TODO: Only accept StarCraft.exe file selection. Might need to remove "All files" filter as well.
    JFileChooser fc = new JFileChooser(new File("C:\\StarCraft"));
    fc.setFileFilter(new FileNameExtensionFilter("*.exe", "exe"));
    if (this.controller.showFileChooser(fc) == JFileChooser.APPROVE_OPTION) {
      File file = fc.getSelectedFile();
      if (file != null) {
        System.out.println("File chosen: " + file.getAbsolutePath());
      }
    }
  }

  /* ************************************************************ */

  /* ************************************************************ */
  /* Events to Controller */
  /* ************************************************************ */

  //...

  /* ************************************************************ */

}
