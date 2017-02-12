package droplauncher.mvc.controller;

import adakite.md5sum.MD5Checksum;
import adakite.util.AdakiteUtils;
import droplauncher.bwapi.BWAPI;
import droplauncher.bwheadless.BWHeadless;
import droplauncher.bwheadless.BotFile;
import droplauncher.mvc.model.Model;
import droplauncher.mvc.model.State;
import droplauncher.starcraft.Race;
import droplauncher.util.Constants;
import droplauncher.util.SettingsKey;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import javafx.stage.Stage;
import net.lingala.zip4j.core.ZipFile;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Controller {

  private static final Logger LOGGER = LogManager.getLogger();

  private Model model;
  private State state;

  public Controller() {
    this.model = null;
    this.state = State.IDLE;
  }

  public void setModel(Model model) {
    this.model = model;
  }

  public State getState() {
    return this.state;
  }

  private void startBWHeadless() {
    if (this.state != State.IDLE) {
      throw new IllegalStateException("state != " + State.IDLE.toString());
    }
    if (!this.model.getBWHeadless().isReady()) {
      throw new IllegalStateException("BWH not ready: " + this.model.getBWHeadless().getReadyError().toString());
    }

    try {
      this.model.startBWHeadless();
    } catch (Exception ex) {
      LOGGER.error(ex);
      return;
    }

    this.state = State.RUNNING;
  }

  private void stopBWHeadless() {
    if (this.state != State.RUNNING) {
      throw new IllegalStateException("state != " + State.RUNNING.toString());
    }

    try {
      this.model.stopBWHeadless();
    } catch (Exception ex) {
      LOGGER.error(ex);
      return;
    }

    this.state = State.IDLE;
  }

  public void closeProgramRequest(Stage stage) {
    if (this.state == State.RUNNING) {
      stopBWHeadless();
    }
    stage.close();
  }

  /**
   * Reads a dropped or selected file which is meant for bwheadless and
   * sets the appropiate settings.
   *
   * @param path specified file to process
   */
  private void processFile(Path path) {
    String ext = AdakiteUtils.getFileExtension(path).toLowerCase(Locale.US);
    if (AdakiteUtils.isNullOrEmpty(ext)) {
      return;
    }

    switch (ext) {
      case "zip":
        processArchive(path);
        break;
      case "dll":
        /* Fall through. */
      case "exe":
        if (path.getFileName().toString().equalsIgnoreCase("BWAPI.dll")) {
          /* BWAPI.dll */
          this.model.getBWHeadless().setBwapiDll(path.toAbsolutePath().toString());
        } else {
          /* Bot file */
          this.model.getBWHeadless().setBotFile(path.toAbsolutePath().toString());
          this.model.getBWHeadless().setBotName(AdakiteUtils.getFilenameNoExt(path));
          this.model.getBWHeadless().setBotRace(Race.RANDOM);
        }
        break;
      default:
        /* Treat as a config file. */
        this.model.getBWHeadless().getExtraBotFiles().add(path);
        break;
    }
  }

  private void processArchive(Path path) {
    try {
      ZipFile zipFile = new ZipFile(path.toAbsolutePath().toString());
      if (zipFile.isEncrypted()) {
        LOGGER.warn("unsupported encrypted archive: " + zipFile.getFile().getAbsolutePath());
        return;
      }
      /* Create temporary directory. */
      Path tmpDir = Paths.get(Constants.TEMP_DIRECTORY).toAbsolutePath();
      FileUtils.deleteDirectory(tmpDir.toFile());
      AdakiteUtils.createDirectory(tmpDir);
      /* Extract files to temporary directory. */
      zipFile.extractAll(tmpDir.toString());
      /* Process contents of temporary directory. */
      Path[] tmpList = AdakiteUtils.getDirectoryContents(tmpDir);
      for (Path tmpPath : tmpList) {
        if (!AdakiteUtils.directoryExists(tmpPath)) {
          processFile(tmpPath);
        }
      }
    } catch (Exception ex) {
      LOGGER.error("unable to process ZIP file: " + path.toAbsolutePath().toString(), ex);
    }
  }

  public void filesDropped(List<File> files) {
    /* Parse all objects dropped into a complete list of files dropped since
       dropping a directory does NOT include all subdirectories and
       files by default. */
    ArrayList<Path> fileList = new ArrayList<>();
    files.forEach((file) -> {
      if (file.isDirectory()) {
        try {
          Path[] tmpList = AdakiteUtils.getDirectoryContents(file.toPath(), true);
          fileList.addAll(Arrays.asList(tmpList));
        } catch (IOException ex) {
          LOGGER.error("unable to get directory contents for: " + file.getAbsolutePath(), ex);
        }
      } else if (file.isFile()) {
        fileList.add(file.toPath());
      } else {
        LOGGER.warn("unknown file dropped: " + file.getAbsolutePath());
      }
    });

    /* Process all files. */
    fileList.forEach((path) -> {
      processFile(path);
    });
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
      try {
        return BWAPI.getBwapiVersion(MD5Checksum.get(Paths.get(dll)));
      } catch (IOException | NoSuchAlgorithmException ex) {
        LOGGER.error(ex);
        return BWAPI.BWAPI_DLL_UNKNOWN;
      }
    }
  }

  public String getBotName() {
    return this.model.getBWHeadless().getINI().getValue(BWHeadless.DEFAULT_INI_SECTION_NAME, SettingsKey.BOT_NAME.toString());
  }

  public Race getBotRace() {
    return Race.get(this.model.getBWHeadless().getINI().getValue(BWHeadless.DEFAULT_INI_SECTION_NAME, SettingsKey.BOT_RACE.toString()));
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
