package droplauncher.mvc.model;

import adakite.utils.FileOperation;
import droplauncher.mvc.view.View;
import droplauncher.bwheadless.BWHeadless;
import droplauncher.bwheadless.ReadyStatus;
import droplauncher.mvc.view.LaunchButtonText;
import droplauncher.starcraft.Race;
import droplauncher.util.Constants;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

public class Model {

  private static final Logger LOGGER = Logger.getLogger(Model.class.getName());
  private static final boolean CLASS_DEBUG = (Constants.DEBUG && true);

  private View view;

  private BWHeadless bwheadless;

  public Model() {
    this.bwheadless = new BWHeadless();
  }

  public void setView(View view) {
    this.view = view;
  }

  public BWHeadless getBWHeadless() {
    return this.bwheadless;
  }

  /* ************************************************************ */
  /* Events from View */
  /* ************************************************************ */

  public void btnLaunchActionPerformed(ActionEvent evt) {
    if (!this.bwheadless.isRunning()) {
      ReadyStatus status = this.bwheadless.getReadyStatus();
      if (status != ReadyStatus.READY) {
        this.view.showMessageBox("Not ready: " + status.toString(), JOptionPane.ERROR_MESSAGE);
        return;
      }
      if (this.bwheadless.start()) {
        this.view.getButtonLaunch().setText(LaunchButtonText.EJECT.toString());
      } else {
        //TODO: error
      }
    } else {
      this.bwheadless.stop();
      this.view.getButtonLaunch().setText(LaunchButtonText.LAUNCH.toString());
    }
  }

  public void btnStarcraftExeActionPerformed(ActionEvent evt) {
    //TODO: Only accept StarCraft.exe file selection. Might need to remove "All files" filter as well.
    //TODO: Guess StarCraft directory or read from registry.
    JFileChooser fc = new JFileChooser(new File("C:\\StarCraft"));
    fc.setFileFilter(new FileNameExtensionFilter("*.exe", "exe"));
    if (this.view.showFileChooser(fc) == JFileChooser.APPROVE_OPTION) {
      File file = fc.getSelectedFile();
      if (file != null) {
        this.bwheadless.getSettings().setStarcraftExe(file);
        this.view.getLabelStarcraftExeText().setText(this.bwheadless.getSettings().getStarcraftExe().getAbsolutePath());
      }
    }
  }

  public void filesDropped(File[] files) {
    for (File file : files) {
      if (file.isDirectory()) {
        File[] tmpList = new FileOperation(file).getDirectoryContents();
        for (File tmpFile : tmpList) {
          System.out.println("File dropped: " + tmpFile.getAbsolutePath());
        }
      } else if (file.isFile()) {
        System.out.println("File dropped: " + file.getAbsolutePath());
      }
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

  /* ************************************************************ */
  /* Events to View */
  /* ************************************************************ */

  //...

  /* ************************************************************ */

}
