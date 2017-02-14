package droplauncher.mvc.controller;

import adakite.debugging.Debugging;
import adakite.md5sum.MD5Checksum;
import adakite.util.AdakiteUtils;
import droplauncher.bwapi.BWAPI;
import droplauncher.bwheadless.BWHeadless;
import droplauncher.bwheadless.BotFile;
import droplauncher.exception.InvalidBotTypeException;
import droplauncher.mvc.model.Model;
import droplauncher.mvc.view.LaunchButtonText;
import droplauncher.mvc.view.SettingsWindow;
import droplauncher.mvc.view.SimpleAlert;
import droplauncher.mvc.view.View;
import droplauncher.starcraft.Race;
import droplauncher.util.Constants;
import droplauncher.util.DirectoryMonitor;
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
import javafx.application.Platform;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import net.lingala.zip4j.core.ZipFile;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Controller {

  private static final Logger LOGGER = LogManager.getLogger();

  private Model model;
  private View view;
  private State state;
  private final Object stateLock;
  private DirectoryMonitor directoryMonitor;

  public Controller() {
    this.model = null;
    this.state = State.IDLE;
    this.stateLock = new Object();
    this.directoryMonitor = null;
  }

  public void setModel(Model model) {
    this.model = model;
  }

  public void setView(View view) {
    this.view = view;
  }

  public State getState() {
    return this.state;
  }

  /**
   * Sets the program state after acquiring access to an intrinsic lock object.
   *
   * @param state
   */
  private void setState(State state) {
    LOGGER.info(Debugging.ack());
    synchronized(this.stateLock) {
      LOGGER.info("lock acquired: " + this.state.toString());
      this.state = state;
    }
    LOGGER.info("lock released: " + this.state.toString());
  }

  private void startBWHeadless() throws IOException, InvalidBotTypeException {
    /* Init DirectoryMonitor if required. */
    Path starcraftDirectory = this.model.getBWHeadless().getStarcraftDirectory();
    if (this.directoryMonitor == null) {
      this.directoryMonitor = new DirectoryMonitor(starcraftDirectory);
      this.directoryMonitor.reset();
    }

    setState(State.RUNNING);

    this.model.getBWHeadless().start();
  }

  private void stopBWHeadless() throws IOException {
    this.model.getBWHeadless().stop();

    /* Copy contents of "bwapi-data/write/" to "bwapi-data/read/". */
    Path starcraftDirectory = this.model.getBWHeadless().getStarcraftDirectory();
    Path bwapiWritePath = starcraftDirectory.resolve(BWAPI.BWAPI_DATA_WRITE_PATH);
    Path bwapiReadPath = starcraftDirectory.resolve(BWAPI.BWAPI_DATA_READ_PATH);
    LOGGER.info("Copy: \"" + bwapiWritePath.toString() + "\" -> \"" + bwapiReadPath.toString() + "\"");
    FileUtils.copyDirectory(bwapiWritePath.toFile(), bwapiReadPath.toFile());

    setState(State.IDLE);
  }

  public void closeProgramRequest(Stage stage) {
    /* Check the program's current state. */
    switch (this.state) {
      case IDLE:
        /* Do nothing. */
        break;
      case RUNNING:
        /* Fall through. */
      case LOCKED:
        /* Fall through. */
      default:
        LOGGER.warn("state should be " + State.IDLE.toString() + ", but state is " + this.state.toString());
        new SimpleAlert().showAndWait(AlertType.ERROR, "Program state error: " + this.state.toString(), "The program's state is: " + this.state.toString() + AdakiteUtils.newline(2) + "Try ejecting the bot first or wait for the current operation to finish.");
        return;
    }

    /* Clean up StarCraft directory. */
    try {
      if (this.directoryMonitor != null) {
        LOGGER.info("clean up StarCraft directory");
        Path starcraftDirectory = this.model.getBWHeadless().getStarcraftDirectory();
        Path bwapiWritePath = starcraftDirectory.resolve(BWAPI.BWAPI_DATA_WRITE_PATH);
        Path bwapiReadPath = starcraftDirectory.resolve(BWAPI.BWAPI_DATA_READ_PATH);
        this.directoryMonitor.update();
        for (Path path : this.directoryMonitor.getNewFiles()) {
          if (!path.toAbsolutePath().startsWith(bwapiWritePath)
              && !path.toAbsolutePath().startsWith(bwapiReadPath)) {
            if (AdakiteUtils.fileExists(path)) {
              LOGGER.info("Delete file: " + path.toString());
              AdakiteUtils.deleteFile(path);
            } else if (AdakiteUtils.directoryExists(path)) {
              LOGGER.info("Delete directory: " + path.toString());
              FileUtils.deleteDirectory(path.toFile());
            }
          }
        }
      }
    } catch (Exception ex) {
      LOGGER.error("clean up StarCraft directory", ex);
    }

    /* Save INI settings to file. */
    try {
      LOGGER.info("save INI settings to file: " + Constants.DROPLAUNCHER_INI_PATH.toString());
      this.model.getINI().saveTo(Constants.DROPLAUNCHER_INI_PATH);
    } catch (Exception ex) {
      LOGGER.error("save INI configuration", ex);
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
          this.model.getBWHeadless().setBotName(FilenameUtils.getBaseName(path.getFileName().toString()));
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

    this.view.update();
  }

  /* ************************************************************ */
  /* Accessible data */
  /* ************************************************************ */

  public String getBotFilename() {
    if (this.model.getBWHeadless().getBotType() != BotFile.Type.UNKNOWN) {
      return this.model.getBWHeadless().getBotPath().getFileName().toString();
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

  public void mnuFileSelectBotFilesClicked(Stage stage) {
    FileChooser fc = new FileChooser();
    fc.setTitle("Select bot files ...");
    Path userDirectory = AdakiteUtils.getUserHomeDirectory();
    if (userDirectory != null) {
      fc.setInitialDirectory(userDirectory.toFile());
    }
    List<File> files = fc.showOpenMultipleDialog(stage);
    if (files != null && files.size() > 0) {
      filesDropped(files);
    }
    this.view.update();
  }

  public void mnuFileExitClicked(Stage stage) {
    closeProgramRequest(stage);
  }

  public void mnuEditSettingsClicked() {
    new SettingsWindow(this.model.getINI()).showAndWait();
  }

  public void mnuHelpAboutClicked() {
    new SimpleAlert().showAndWait(AlertType.INFORMATION, Constants.PROGRAM_TITLE, Constants.PROGRAM_ABOUT);
  }

  public void btnLaunchClicked() {
    State prevState = this.state;

    if (prevState == State.LOCKED) {
      return;
    }

    setState(State.LOCKED);

    switch (prevState) {
      case IDLE:
        if (!this.model.getBWHeadless().isReady()) {
          /* Display error message. */
          new SimpleAlert().showAndWait(
              AlertType.ERROR,
              "Not Ready",
              "The program is not ready due to the following error: " + AdakiteUtils.newline(2) +
              this.model.getBWHeadless().getReadyError().toString()
          );
          setState(State.IDLE);
          return;
        } else {
          /* Start bwheadless. */
          this.view.btnLaunchEnabled(false);
          new Thread(() -> {
            try {
              startBWHeadless();
            } catch (Exception ex) {
              LOGGER.error(ex);
            }
            Platform.runLater(() -> {
              this.view.btnLaunchSetText(LaunchButtonText.EJECT.toString());
              this.view.btnLaunchEnabled(true);
            });
          }).start();
        }
        return;
      case RUNNING:
        /* Stop bwheadless. */
        this.view.btnLaunchEnabled(false);
        new Thread(() -> {
          try {
            stopBWHeadless();
          } catch (Exception ex) {
            LOGGER.error(ex);
          }
          Platform.runLater(() -> {
            this.view.btnLaunchSetText(LaunchButtonText.LAUNCH.toString());
            this.view.btnLaunchEnabled(true);
          });
        }).start();
        return;
      default:
        break;
    }

    if (this.state == State.LOCKED) {
      LOGGER.warn("still locked");
    }
  }

  public void updateRaceChoiceBox() {
    this.view.setText(this.view.getRaceChoiceBox(), getBotRace().toString());
    this.view.sizeToScene();
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
    updateRaceChoiceBox();
  }

  public void botNameChanged(String str) {
    this.model.getBWHeadless().setBotName(str);
    this.view.update();
  }

}
