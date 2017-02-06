package droplauncher.mvc.controller;

import adakite.util.AdakiteUtils;
import droplauncher.bwapi.BWAPI;
import droplauncher.bwheadless.BWHeadless;
import droplauncher.bwheadless.BotModule;
import droplauncher.mvc.model.Model;
import droplauncher.mvc.view.LaunchButtonText;
import droplauncher.mvc.view.SettingsWindow;
import droplauncher.mvc.view.SimpleAlert;
import droplauncher.mvc.view.View;
import droplauncher.starcraft.Race;
import droplauncher.util.Constants;
import droplauncher.util.SettingsKey;
import java.io.File;
import java.nio.file.Paths;
import java.util.List;
import javafx.application.Platform;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TextArea;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class Controller {

  private Model model;
  private View view;

  public Controller() {
    this.model = null;
    this.view = null;
  }

  public void setModel(Model model) {
    this.model = model;
  }

  public void setView(View view) {
    this.view = view;
    this.view.setINI(this.model.getINI());
  }

  private void startBWHeadless() {
    this.model.getBWHeadless().setLogWindow(this.view.getLogWindow());
    this.model.startBWHeadless();
  }

  private void stopBWHeadless() {
    this.model.stopBWHeadless();
  }

  public void closeProgramRequest(Stage stage) {
    if (this.model.getBWHeadless().isRunning()) {
      stopBWHeadless();
    }
    stage.close();
  }

  /* ************************************************************ */
  /* Accessible data */
  /* ************************************************************ */

  public TextArea getLogWindow() {
    return this.view.getLogWindow();
  }

  public BotModule getBotModule() {
    return this.model.getBWHeadless().getBotModule();
  }

  public String getBwapiDllVersion() {
    String dll = this.model.getBWHeadless().getINI().getValue(BWHeadless.BWHEADLESS_INI_SECTION, SettingsKey.BWAPI_DLL.toString());
    if (AdakiteUtils.isNullOrEmpty(dll)) {
      return "";
    } else {
      return BWAPI.getBwapiVersion(Paths.get(dll));
    }
  }

  public String getBotName() {
    return this.model.getBWHeadless().getINI().getValue(BWHeadless.BWHEADLESS_INI_SECTION, SettingsKey.BOT_NAME.toString());
  }

  public Race getBotRace() {
    return Race.get(this.model.getBWHeadless().getINI().getValue(BWHeadless.BWHEADLESS_INI_SECTION, SettingsKey.BOT_RACE.toString()));
  }

  public boolean isEnabledLogWindow() {
    return (this.model.getINI().hasValue(Constants.DROPLAUNCHER_INI_SECTION, SettingsKey.SHOW_LOG_WINDOW.toString())
        && this.model.getINI().getValue(Constants.DROPLAUNCHER_INI_SECTION, SettingsKey.SHOW_LOG_WINDOW.toString()).equals(Boolean.TRUE.toString()));
  }

  /* ************************************************************ */
  /* Events called by a view */
  /* ************************************************************ */

  public void mnuFileSelectBotFilesClicked(Stage stage) {
    FileChooser fc = new FileChooser();
    fc.setTitle("Select bot files ...");
    String userDirectory = AdakiteUtils.getUserHomeDirectory().toAbsolutePath().toString();
    if (userDirectory != null) {
      fc.setInitialDirectory(new File(userDirectory));
    }
    List<File> files = fc.showOpenMultipleDialog(stage);
    if (files != null && files.size() > 0) {
      filesDropped(files);
    }
  }

  public void mnuFileExitClicked(Stage stage) {
    closeProgramRequest(stage);
  }

  public void mnuEditSettingsClicked() {
    SettingsWindow window = new SettingsWindow(this.model.getINI());
    window.showAndWait();
  }

  public void mnuHelpAboutClicked() {
    new SimpleAlert().showAndWait(
        AlertType.INFORMATION,
        Constants.PROGRAM_NAME,
        Constants.PROGRAM_ABOUT
    );
  }

  public void filesDropped(List<File> files) {
    this.model.filesDropped(files);
    this.view.update();
  }

  public void botRaceChanged(String str) {
    if (str.equals(Race.TERRAN.toString())) {
      this.model.getBWHeadless().setBotRace(Race.TERRAN);
    } else if (str.equals(Race.ZERG.toString())) {
      this.model.getBWHeadless().setBotRace(Race.ZERG);
    } else if (str.equals(Race.PROTOSS.toString())) {
      this.model.getBWHeadless().setBotRace(Race.PROTOSS);
    } else if (str.equals(Race.RANDOM.toString())) {
      this.model.getBWHeadless().setBotRace(Race.RANDOM);
    }
  }

  public void botNameChanged(String str) {
    this.model.getBWHeadless().setBotName(str);
    this.view.update();
  }

  public void btnLaunchClicked() {
    if (!this.model.getBWHeadless().isRunning()) {
      if (!this.model.getBWHeadless().isReady()) {
        /* Display error message. */
        new SimpleAlert().showAndWait(
            AlertType.ERROR,
            "Not Ready",
            "The program is not ready due to the following error: " + AdakiteUtils.newline(2) +
            this.model.getBWHeadless().getReadyError().toString()
        );
      } else {
        /* Start bwheadless. */
        this.view.btnLaunchEnabled(false);
        new Thread(() -> {
          startBWHeadless();
          Platform.runLater(() -> {
            this.view.btnLaunchSetText(LaunchButtonText.EJECT.toString());
            this.view.btnLaunchEnabled(true);
          });
        }).start();
      }
    } else {
      /* Stop bwheadless. */
      this.view.btnLaunchEnabled(false);
      new Thread(() -> {
        stopBWHeadless();
        Platform.runLater(() -> {
          this.view.btnLaunchSetText(LaunchButtonText.LAUNCH.toString());
          this.view.btnLaunchEnabled(true);
        });
      }).start();
    }
  }

}
