package droplauncher.mvc.controller;

import adakite.md5sum.MD5Checksum;
import adakite.util.AdakiteUtils;
import droplauncher.bwapi.BWAPI;
import droplauncher.bwheadless.BWHeadless;
import droplauncher.bwheadless.BotFile;
import droplauncher.exception.InvalidBotTypeException;
import droplauncher.mvc.model.Model;
import droplauncher.starcraft.Race;
import droplauncher.util.Constants;
import droplauncher.util.SettingsKey;
import java.io.IOException;
import java.nio.file.Paths;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Controller {

  private static final Logger LOGGER = LogManager.getLogger();

  private Model model;

  public Controller() {
    this.model = null;
  }

  public void setModel(Model model) {
    this.model = model;
  }

  private void startBWHeadless() throws Throwable {
    try {
      this.model.startBWHeadless();
    } catch (IOException | InvalidBotTypeException ex) {
      LOGGER.error(ex);
    }
  }

  private void stopBWHeadless() {
    LOGGER.info("ack");
    try {
      this.model.stopBWHeadless();
      LOGGER.info("ack2");
    } catch (IOException ex) {
      LOGGER.error(ex);
    }
    LOGGER.info("ack3");
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

  public String getBotFilename() {
    if (this.model.getBWHeadless().getBotFile().getType() != BotFile.Type.UNKNOWN) {
      return this.model.getBWHeadless().getBotFile().getPath().getFileName().toString();
    } else {
      return null;
    }
  }

  public String getBwapiDllVersion() {
    String dll = this.model.getBWHeadless().getINI().getValue(BWHeadless.DEFAULT_INI_SECTION_NAME, SettingsKey.BWAPI_DLL.toString());
    if (AdakiteUtils.isNullOrEmpty(dll)) {
      return null;
    } else {
      return BWAPI.getBwapiVersion(MD5Checksum.get(Paths.get(dll)));
    }
  }

  public String getBotName() {
    return this.model.getBWHeadless().getINI().getValue(BWHeadless.DEFAULT_INI_SECTION_NAME, SettingsKey.BOT_NAME.toString());
  }

  public Race getBotRace() {
    return Race.get(this.model.getBWHeadless().getINI().getValue(BWHeadless.DEFAULT_INI_SECTION_NAME, SettingsKey.BOT_RACE.toString()));
  }

  public boolean isEnabledLogWindow() {
    return (this.model.getINI().hasValue(Constants.DROPLAUNCHER_INI_SECTION_NAME, SettingsKey.SHOW_LOG_WINDOW.toString())
        && this.model.getINI().getValue(Constants.DROPLAUNCHER_INI_SECTION_NAME, SettingsKey.SHOW_LOG_WINDOW.toString()).equals(Boolean.TRUE.toString()));
  }

  /* ************************************************************ */
  /* Events called by a view */
  /* ************************************************************ */

//  public void btnLaunchClicked() {
//    if (!this.model.getBWHeadless().isRunning()) {
//      if (!this.model.getBWHeadless().isReady()) {
//        /* Display error message. */
//        new SimpleAlert().showAndWait(
//            AlertType.ERROR,
//            "Not Ready",
//            "The program is not ready due to the following error: " + AdakiteUtils.newline(2) +
//            this.model.getBWHeadless().getReadyError().toString()
//        );
//      } else {
//        /* Start bwheadless. */
//        this.view.btnLaunchEnabled(false);
//        new Thread(() -> {
//          startBWHeadless();
//          Platform.runLater(() -> {
//            this.view.btnLaunchSetText(LaunchButtonText.EJECT.toString());
//            this.view.btnLaunchEnabled(true);
//          });
//        }).start();
//      }
//    } else {
//      /* Stop bwheadless. */
//      this.view.btnLaunchEnabled(false);
//      new Thread(() -> {
//        stopBWHeadless();
//        Platform.runLater(() -> {
//          this.view.btnLaunchSetText(LaunchButtonText.LAUNCH.toString());
//          this.view.btnLaunchEnabled(true);
//        });
//      }).start();
//    }
//  }

}
