/*
 * Copyright (C) 2017 Adakite
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package droplauncher.mvc.controller;

import adakite.debugging.Debugging;
import adakite.exception.InvalidArgumentException;
import adakite.exception.InvalidStateException;
import adakite.ini.IniParseException;
import adakite.md5sum.MD5Checksum;
import adakite.util.AdakiteUtils;
import droplauncher.bwapi.BWAPI;
import droplauncher.mvc.model.Model;
import droplauncher.mvc.view.SettingsWindow;
import droplauncher.mvc.view.SimpleAlert;
import droplauncher.mvc.view.View;
import droplauncher.util.DropLauncher;
import adakite.util.DirectoryMonitor;
import droplauncher.exception.EncryptedArchiveException;
import droplauncher.exception.InvalidBotTypeException;
import droplauncher.mvc.view.ExceptionAlert;
import droplauncher.starcraft.Starcraft;
import droplauncher.starcraft.Starcraft.Race;
import droplauncher.util.process.exception.ClosePipeException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

public class Controller {

  private static final Logger LOGGER = Logger.getLogger(Controller.class.getName());

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

  /**
   * Manually sets the program state indicator after acquiring
   * access to an intrinsic lock object.
   *
   * @param state specified state
   */
  private void setState(State state) {
    synchronized(this.stateLock) {
      this.state = state;
    }
  }

  private void startBWHeadless() throws IOException,
                                        InvalidBotTypeException,
                                        IniParseException,
                                        InvalidStateException {
    setState(State.RUNNING);

    this.view.getConsoleOutput().println(View.MessagePrefix.DROPLAUNCHER.toString() + ": connecting bot to StarCraft");

    /* Init DirectoryMonitor if required. */
    Path starcraftDirectory = Starcraft.getPath();
    if (this.directoryMonitor == null) {
      this.directoryMonitor = new DirectoryMonitor(starcraftDirectory);
      this.directoryMonitor.getIgnoreList().add("maps"); /* ignore any "*maps*" file/directory */
      this.directoryMonitor.getIgnoreList().add("bwta"); /* ignore any "*bwta*" file/directory */
      this.directoryMonitor.getIgnoreList().add("bwta2"); /* ignore any "*bwta2*" file/directory */
      this.directoryMonitor.getIgnoreList().add("bwapi-data"); /* ignore any "*bwapi-data*" file/directory */
      this.directoryMonitor.reset();
    }

    this.model.getBWHeadless().start(this.view.getConsoleOutput());
  }

  private void stopBWHeadless() throws IOException,
                                       InvalidStateException,
                                       ClosePipeException {
    setState(State.IDLE);

    this.model.getBWHeadless().stop();

    if (Model.isPrefEnabled(BWAPI.Property.COPY_WRITE_READ.toString())) {
      /* Copy contents of "bwapi-data/write/" to "bwapi-data/read/". */
      Path starcraftDirectory = Starcraft.getPath();
      Path bwapiWritePath = starcraftDirectory.resolve(BWAPI.DATA_WRITE_PATH);
      Path bwapiReadPath = starcraftDirectory.resolve(BWAPI.DATA_READ_PATH);
      String copyMessage = View.MessagePrefix.COPY.get() + bwapiWritePath.toString() + " -> " + bwapiReadPath.toString();
      this.view.getConsoleOutput().println(View.MessagePrefix.DROPLAUNCHER.get() + copyMessage);
      FileUtils.copyDirectory(bwapiWritePath.toFile(), bwapiReadPath.toFile());
    }

    this.view.getConsoleOutput().println(View.MessagePrefix.DROPLAUNCHER.get() + "ejected bot");
  }

  /**
   * Attempts to close the specified stage. May fail if conditions are not met.
   *
   * @param stage specified stage to close
   */
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
        String errorMessage = "The program's state is: " + this.state.toString() + " and should be " + State.IDLE.toString();
        LOGGER.log(Debugging.getLogLevel(), errorMessage);
        new ExceptionAlert().showAndWait(
            errorMessage
            + AdakiteUtils.newline(2) + "Try ejecting the bot first or wait for the current operation to finish.", null
        );
        return;
    }

    if (Model.isPrefEnabled(Starcraft.Property.CLEAN_SC_DIR.toString())) {
      /* Clean up StarCraft directory. */
      try {
        if (this.directoryMonitor != null) {
          Path starcraftDirectory = Starcraft.getPath();
          Path bwapiWritePath = starcraftDirectory.resolve(BWAPI.DATA_WRITE_PATH);
          Path bwapiReadPath = starcraftDirectory.resolve(BWAPI.DATA_READ_PATH);
          this.directoryMonitor.update();
          for (Path path : this.directoryMonitor.getNewFiles()) {
            if (!path.toAbsolutePath().startsWith(bwapiWritePath)
                && !path.toAbsolutePath().startsWith(bwapiReadPath)) {
              /* Delete file/directory if not in the read/write directory. */
              if (AdakiteUtils.fileExists(path)) {
                AdakiteUtils.deleteFile(path);
              } else if (AdakiteUtils.directoryExists(path)) {
                FileUtils.deleteDirectory(path.toFile());
              }
            }
          }
        }
      } catch (Exception ex) {
        LOGGER.log(Debugging.getLogLevel(), "clean up StarCraft directory", ex);
        new ExceptionAlert().showAndWait("clean up StarCraft directory", ex);
      }
    }

    stage.close();
  }

  /**
   * Reads a dropped or selected file which is meant for bwheadless and
   * sets the appropiate settings.
   *
   * @param path specified file to process
   */
  private void processFile(Path path) throws InvalidArgumentException {
    if (this.state != State.IDLE) {
      return;
    }

    String ext = AdakiteUtils.getFileExtension(path).toLowerCase(Locale.US);
    if (AdakiteUtils.isNullOrEmpty(ext)) {
      return;
    }
    switch (ext) {
      case "zip":
        processZipFile(path);
        break;
      case "dll":
        /* Fall through. */
      case "exe":
        /* Fall through. */
      case "jar":
        if (path.getFileName().toString().equalsIgnoreCase("BWAPI.dll")) {
          /* BWAPI.dll */
          this.model.getBot().setBwapiDll(path.toAbsolutePath().toString());
        } else {
          /* Bot file */
          this.model.getBot().setPath(path.toAbsolutePath().toString());
          this.model.getBot().setName(FilenameUtils.getBaseName(path.toString()));
          this.model.getBot().setRace(Race.RANDOM.toString());
        }
        break;
      default:
        /* Treat as a config file. */
        this.model.getBot().addExtraFile(path.toAbsolutePath().toString());
        break;
    }
  }

  /**
   * Processes a ZIP file. Extracts the ZIP file and processes its contents
   * via {@link #processFile(java.nio.file.Path)}.
   *
   * @param path specified path to the ZIP file
   * @see #processFile(java.nio.file.Path)
   */
  private void processZipFile(Path path) {
    try {
      if (path == null) {
        throw new IllegalArgumentException(Debugging.cannotBeNull("path"));
      } else if (!AdakiteUtils.getFileExtension(path).equalsIgnoreCase("zip")) {
        throw new IllegalArgumentException("path does not appear to be a ZIP file: " + path.toString());
      }

      ZipFile zipFile = new ZipFile(path.toAbsolutePath().toString());
      if (zipFile.isEncrypted()) {
        throw new EncryptedArchiveException("encrypted archive not supported: " + zipFile.getFile().getAbsolutePath());
      }
      /* Create temporary directory. */
      Path tmpDir = Paths.get(DropLauncher.TEMP_DIRECTORY).toAbsolutePath();
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
      LOGGER.log(Debugging.getLogLevel(), ex.getMessage(), ex);
      new ExceptionAlert().showAndWait(null, ex);
    }
  }

  public void filesDropped(List<File> files) {
    /* Parse all objects dropped into a complete list of files dropped since
       dropping a directory does NOT include all subdirectories and
       files by default. */
    ArrayList<Path> fileList = new ArrayList<>();
    for (File file : files) {
      if (file.isDirectory()) {
        try {
          Path[] tmpList = AdakiteUtils.getDirectoryContents(file.toPath(), true);
          fileList.addAll(Arrays.asList(tmpList));
        } catch (IOException ex) {
          LOGGER.log(Debugging.getLogLevel(), "unable to get directory contents for: " + file.getAbsolutePath(), ex);
        }
      } else if (file.isFile()) {
        fileList.add(file.toPath());
      } else {
        LOGGER.log(Debugging.getLogLevel(), "unknown file dropped: " + file.getAbsolutePath());
      }
    }

    /* Process all files. */
    for (Path path : fileList) {
      try {
        processFile(path);
      } catch (Exception ex) {
        LOGGER.log(Debugging.getLogLevel(), null, ex);
      }
    }

    this.view.update();
  }

  /* ************************************************************ */
  /* Accessible data */
  /* ************************************************************ */

  /**
   * Gets the manually set state indicator of the program.
   */
  public State getState() {
    return this.state;
  }

  public String getBotFilename() {
    try {
      String name = FilenameUtils.getName(this.model.getBot().getPath());
      return name;
    } catch (Exception ex) {
      return null;
    }
  }

  public String getBwapiDllVersion() {
    try {
      String dll = this.model.getBot().getBwapiDll();
      String md5sum = MD5Checksum.get(Paths.get(dll));
      String version = BWAPI.getBwapiVersion(md5sum);
      return version;
    } catch (Exception ex) {
      LOGGER.log(Debugging.getLogLevel(), null, ex);
      return "";
    }
  }

  public String getBotName() {
    try {
      String name = this.model.getBot().getName();
      return name;
    } catch (Exception ex) {
      return null;
    }
  }

  public Race getBotRace() {
    try {
      Race race = Race.get(this.model.getBot().getRace());
      return race;
    } catch (Exception ex) {
      return null;
    }
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
      try {
        filesDropped(files);
      } catch (Exception ex) {
        /* Do nothing. */
      }
    }
    this.view.update();
  }

  public void mnuFileExitClicked(Stage stage) {
    closeProgramRequest(stage);
  }

  public void mnuEditSettingsClicked() {
    new SettingsWindow().showAndWait();
  }

  public void mnuHelpAboutClicked() {
    new SimpleAlert().showAndWait(AlertType.INFORMATION, DropLauncher.PROGRAM_TITLE, DropLauncher.PROGRAM_ABOUT);
  }

  public void btnStartClicked() {
    /* Check if BWAPI.dll is known. */
    String bwapiDllVersion = getBwapiDllVersion();
    if (this.state == State.IDLE
        && Model.isPrefEnabled(BWAPI.Property.WARN_UNKNOWN_BWAPI_DLL.toString())
        && !AdakiteUtils.isNullOrEmpty(bwapiDllVersion)
        && bwapiDllVersion.equalsIgnoreCase(BWAPI.DLL_UNKNOWN)) {
      Alert alert = new Alert(AlertType.CONFIRMATION);
      alert.setTitle("Warning");
      alert.setContentText("The BWAPI.dll you provided does not match the list of known official BWAPI versions.\n\nDo you want to continue anyway?");
      alert.setHeaderText(null);
      View.addDefaultStylesheet(alert.getDialogPane().getStylesheets());
      ButtonType btnNo = new ButtonType("No");
      ButtonType btnYes = new ButtonType("Yes");
      alert.getButtonTypes().setAll(btnYes, btnNo);
      Optional<ButtonType> result = alert.showAndWait();
      if (result.get() != btnYes) {
        new SimpleAlert().showAndWait(
            AlertType.WARNING,
            "Warning",
            "Launch aborted!"
        );
        return;
      }
    }

    State prevState = this.state;

    if (prevState == State.LOCKED) {
      return;
    }

//    boolean isReady = false;
//    try {
//      isReady = this.model.getBWHeadless().isReady();
//    } catch (Exception ex) {
//      /* Do nothing. */
//    }
//
//    if (!isReady) {
//      try {
//        /* Display error message. */
//        new SimpleAlert().showAndWait(
//            AlertType.ERROR,
//            "Not Ready",
//            "The program is not ready due to the following error: " + AdakiteUtils.newline(2) +
//            this.model.getBWHeadless().checkReady().toString()
//        );
//        setState(State.IDLE);
//      } catch (Exception ex) {
//        LOGGER.log(Debugging.getLogLevel(), null, ex);
//      }
//    }

    setState(State.LOCKED);

    switch (prevState) {
      case IDLE:
        /* Start bwheadless. */
        this.view.btnStartEnabled(false);
        new Thread(() -> {
          try {
            startBWHeadless();
          } catch (Exception ex) {
            this.view.getConsoleOutput().println(
                View.MessagePrefix.DROPLAUNCHER.get()
                + "unable to connect bot due to the following error:" + AdakiteUtils.newline(2)
                + ex.toString() + AdakiteUtils.newline()
            );
            LOGGER.log(Debugging.getLogLevel(), null, ex);
          }
          Platform.runLater(() -> {
            this.view.btnStartSetText(View.StartButtonText.STOP.toString());
            this.view.btnStartEnabled(true);
          });
        }).start();
        return;
      case RUNNING:
        /* Stop bwheadless. */
        this.view.btnStartEnabled(false);
        new Thread(() -> {
          try {
            stopBWHeadless();
          } catch (Exception ex) {
            LOGGER.log(Debugging.getLogLevel(), null, ex);
          }
          Platform.runLater(() -> {
            this.view.btnStartSetText(View.StartButtonText.START.toString());
            this.view.btnStartEnabled(true);
          });
        }).start();
        return;
      default:
        break;
    }

    if (this.state == State.LOCKED) {
      LOGGER.log(Debugging.getLogLevel(), "still locked");
    }
  }

  public void botRaceChanged(String str) {
    try {
      this.model.getBot().setRace(str);
      this.view.updateRaceChoiceBox(); //TODO: Why do we have to do this? Remove?
    } catch (Exception ex) {
      LOGGER.log(Debugging.getLogLevel(), null, ex);
    }
  }

  public void botNameChanged(String str) {
    try {
      this.model.getBot().setName(str);
      this.view.update();
    } catch (Exception ex) {
      LOGGER.log(Debugging.getLogLevel(), null, ex);
    }
  }

}
