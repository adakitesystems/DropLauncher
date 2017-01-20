package droplauncher.mvc.model;

import adakite.debugging.Debugging;
import adakite.utils.AdakiteUtils;
import adakite.utils.FileOperation;
import droplauncher.mvc.view.View;
import droplauncher.bwheadless.BWHeadless;
import droplauncher.bwheadless.ReadyStatus;
import droplauncher.ini.IniFile;
import droplauncher.mvc.view.LaunchButtonText;
import droplauncher.starcraft.Race;
import droplauncher.starcraft.Starcraft;
import droplauncher.util.Constants;
import droplauncher.util.Util;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;

public class Model {

  private static final Logger LOGGER = Logger.getLogger(Model.class.getName());
  private static final boolean CLASS_DEBUG = (Constants.DEBUG && true);

  public static final File DROP_LAUNCHER_INI = new File("droplauncher.ini");

  private View view;

  private BWHeadless bwheadless;
  private IniFile ini;

  public Model() {
    this.bwheadless = new BWHeadless();
    this.ini = new IniFile();

    this.bwheadless.setIniFile(this.ini);
    if (this.ini.open(DROP_LAUNCHER_INI)) {
      this.bwheadless.readSettingsFile(this.ini);
    } else {
      if (CLASS_DEBUG) {
        LOGGER.log(Constants.DEFAULT_LOG_LEVEL, Debugging.openFail(DROP_LAUNCHER_INI));
      }
    }
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
    ReadyStatus status = this.bwheadless.getReadyStatus();
    if (status != ReadyStatus.READY) {
      this.view.showMessageBox(JOptionPane.ERROR_MESSAGE, "Not ready: " + status.toString());
      return;
    }
    if (this.view.getButtonLaunch().getText().equalsIgnoreCase(LaunchButtonText.LAUNCH.toString())) {
      this.view.getButtonLaunch().setText(LaunchButtonText.EJECT.toString());
    } else if (this.view.getButtonLaunch().getText().equalsIgnoreCase(LaunchButtonText.EJECT.toString())) {
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
        this.bwheadless.setStarcraftExe(file);
        this.view.getLabelStarcraftExeText().setText(this.bwheadless.getStarcraftExe().getAbsolutePath());
      }
    }
  }

  public void filesDropped(File[] files) {
    ArrayList<File> fileList = new ArrayList<>();
    for (File file : files) {
      if (file.isDirectory()) {
        File[] tmpList = new FileOperation(file).getDirectoryContents();
        for (File tmpFile : tmpList) {
          fileList.add(tmpFile);
        }
      } else if (file.isFile()) {
        fileList.add(file);
      } else {
        if (CLASS_DEBUG) {
          LOGGER.log(Constants.DEFAULT_LOG_LEVEL, "Unknown file dropped: " + file.getAbsolutePath());
        }
      }
    }

    for (File file : fileList) {
      String ext = Util.getFileExtension(file);
      if (ext != null) {
        if (ext.equalsIgnoreCase("dll")) {
          if (file.getName().equalsIgnoreCase("BWAPI.dll")) {
            this.bwheadless.setBwapiDll(file);
          } else {
            this.bwheadless.setBotDll(file);
          }
        } else if (ext.equalsIgnoreCase("exe")) {
          this.bwheadless.setBotClient(file);
        } else if (ext.equalsIgnoreCase("jar")) {
          this.bwheadless.setBotClient(file);
        } else {
          /* If file extension is not recognized, ignore file. */
        }
      } else {
        /* If no file extension is detected, ignore file. */
      }
    }

    this.view.update();
  }

  public void rbRaceProtossActionPerformed(ActionEvent evt) {
    this.bwheadless.setBotRace(Race.PROTOSS);
  }

  public void rbRaceRandomActionPerformed(ActionEvent evt) {
    this.bwheadless.setBotRace(Race.RANDOM);
  }

  public void rbRaceTerranActionPerformed(ActionEvent evt) {
    this.bwheadless.setBotRace(Race.TERRAN);
  }

  public void rbRaceZergActionPerformed(ActionEvent evt) {
    this.bwheadless.setBotRace(Race.ZERG);
  }

  //TODO: Delete
  public void txtBotNameKeyPressed(KeyEvent evt) {

  }

  public void txtBotNameKeyReleased(KeyEvent evt) {
    JTextField txt = this.view.getTextFieldBotName();
    int caret = txt.getCaretPosition();
    String botName = txt.getText();
    String botNameFixed = Starcraft.cleanProfileName(botName);
    if (AdakiteUtils.isNullOrEmpty(botNameFixed)) {
      botNameFixed = BWHeadless.DEFAULT_BOT_NAME;
      caret = botNameFixed.length();
    }
    if (botName.equalsIgnoreCase(botNameFixed)) {
//      System.out.println("Bot name: " + botName);
    } else {
      if (caret < 0) {
        caret = 0;
      } else if (caret >= botNameFixed.length()) {
        caret = botNameFixed.length();
      } else {
        caret--;
      }
      txt.setText(botNameFixed);
      txt.setCaretPosition(caret);
//      System.out.println("Fixed bot name: " + botNameFixed);
    }
    this.bwheadless.setBotName(botNameFixed);
  }

  /* ************************************************************ */
  /* Events to View */
  /* ************************************************************ */

  //...

  /* ************************************************************ */

}
