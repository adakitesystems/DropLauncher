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
import adakite.util.DirectoryMonitor;
import droplauncher.bwapi.BWAPI;
import droplauncher.bwapi.bot.Bot;
import droplauncher.mvc.model.Model;
import droplauncher.mvc.view.SettingsWindow;
import droplauncher.mvc.view.SimpleAlert;
import droplauncher.mvc.view.View;
import droplauncher.util.DropLauncher;
import droplauncher.bwapi.bot.exception.InvalidBwapiDllException;
import droplauncher.bwapi.bot.exception.MissingBotFileException;
import droplauncher.bwapi.bot.exception.MissingBotNameException;
import droplauncher.bwapi.bot.exception.MissingBotRaceException;
import droplauncher.bwapi.bot.exception.MissingBwapiDllException;
import droplauncher.bwheadless.exception.MissingBotException;
import droplauncher.exception.EncryptedArchiveException;
import droplauncher.exception.InvalidBotTypeException;
import droplauncher.mvc.view.ExceptionAlert;
import droplauncher.mvc.view.View.DialogTitle;
import droplauncher.starcraft.Starcraft;
import droplauncher.starcraft.Starcraft.Race;
import droplauncher.starcraft.exception.MissingStarcraftExeException;
import droplauncher.starcraft.exception.StarcraftProfileNameException;
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
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import net.lingala.zip4j.core.ZipFile;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

public class Controller {

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

  private void startBWHeadless() throws InvalidStateException,
                                        IOException,
                                        InvalidBotTypeException,
                                        IniParseException,
                                        MissingBotNameException,
                                        MissingBotRaceException,
                                        MissingBotFileException,
                                        MissingBwapiDllException,
                                        MissingStarcraftExeException,
                                        MissingBotException,
                                        InvalidArgumentException {
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
                                       ClosePipeException,
                                       MissingBotFileException,
                                       MissingStarcraftExeException {
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
  }

  /**
   * Attempts to close the specified stage. May fail if conditions are not met.
   *
   * @param stage specified stage to close
   * @throws InvalidStateException
   */
  public void closeProgramRequest(Stage stage) throws InvalidStateException {
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
        String errorMessage = "program is still in state " + this.state.toString()
            + AdakiteUtils.newline(2)
            + "Try ejecting the bot first or wait for the current operation to finish."
            ;
        throw new InvalidStateException(errorMessage);
    }

    if (Model.isPrefEnabled(Starcraft.Property.CLEAN_SC_DIR.toString())) {
      /* Clean up StarCraft directory. */
      try {
        if (this.directoryMonitor != null) {
          this.directoryMonitor.update();
          for (Path path : this.directoryMonitor.getNewFiles()) {
            if (AdakiteUtils.fileExists(path)) {
              AdakiteUtils.deleteFile(path);
            } else if (AdakiteUtils.directoryExists(path)) {
              FileUtils.deleteDirectory(path.toFile());
            }
          }
        }
      } catch (Exception ex) {
        new ExceptionAlert().showAndWait("failed to clean up StarCraft directory", ex);
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
  private void processFile(Path path) throws InvalidArgumentException,
                                             StarcraftProfileNameException,
                                             InvalidBwapiDllException {
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
          this.model.getBWHeadless().getBot().setBwapiDll(path.toAbsolutePath().toString());
        } else {
          /* Set bot file. */
          this.model.getBWHeadless().getBot().setPath(path.toAbsolutePath().toString());
          /* Set bot race. */
          this.model.getBWHeadless().getBot().setRace(Race.RANDOM.toString());
          /* Set clean bot name. */
          String name = FilenameUtils.getBaseName(path.toString());
          name = Starcraft.cleanProfileName(name);
          this.model.getBWHeadless().getBot().setName(name);
        }
        break;
      default:
        /* Treat as a config file. */
        this.model.getBWHeadless().getBot().addExtraFile(path.toAbsolutePath().toString());
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
      new ExceptionAlert().showAndWait(null, ex);
    }
  }

  public void filesDropped(List<File> files) throws IOException,
                                                    InvalidArgumentException,
                                                    StarcraftProfileNameException,
                                                    InvalidBwapiDllException {
    if (this.state != State.IDLE) {
      Platform.runLater(() -> {
        View.displayOperationProhibitedDialog("Loading bot files is not allowed while a bot is running.");
      });
      return;
    }

    /* Parse all objects dropped into a complete list of files dropped since
       dropping a directory does NOT include all subdirectories and
       files by default. */
    ArrayList<Path> fileList = new ArrayList<>();
    for (File file : files) {
      if (file.isDirectory()) {
        Path[] tmpList = AdakiteUtils.getDirectoryContents(file.toPath(), true);
        fileList.addAll(Arrays.asList(tmpList));
      } else if (file.isFile()) {
        fileList.add(file.toPath());
      } else {
        throw new InvalidArgumentException("unknown file dropped: " + file.getAbsolutePath());
      }
    }

    /* Keep track of previous number of extra bot files. */
    int prevNum = this.model.getBWHeadless().getBot().getExtraFiles().size();

    /* Process all files. */
    for (Path path : fileList) {
      processFile(path);
    }

    /* Find current number of extra bot files. */
    int currNum = this.model.getBWHeadless().getBot().getExtraFiles().size();

    if (currNum > prevNum) {
      /* If more extra bot files have been processed, display a dialog message. */
      StringBuilder sb = new StringBuilder(currNum);
      for (String extra : this.model.getBWHeadless().getBot().getExtraFiles()) {
        sb.append(FilenameUtils.getName(extra)).append(AdakiteUtils.newline());
      }
      Platform.runLater(() -> {
        String message = "" + currNum + " bot configuration file";
        if (currNum != 1) {
          message += "s";
        }
        message += " detected! These files will be copied to the \"" + BWAPI.DATA_PATH.toString() + "\" directory when the bot is launched: " + AdakiteUtils.newline(2) + sb.toString();
        new SimpleAlert().showAndWait(
            AlertType.INFORMATION,
            DialogTitle.PROGRAM_NAME,
            message
        );
      });
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
      String name = FilenameUtils.getName(this.model.getBWHeadless().getBot().getPath());
      return name;
    } catch (Exception ex) {
      return null;
    }
  }

  public String getBwapiDllVersion() {
    try {
      String dll = this.model.getBWHeadless().getBot().getBwapiDll();
      String md5sum = MD5Checksum.get(Paths.get(dll));
      String version = BWAPI.getBwapiVersion(md5sum);
      return version;
    } catch (Exception ex) {
      return null;
    }
  }

  public String getBotName() {
    try {
      String name = this.model.getBWHeadless().getBot().getName();
      return name;
    } catch (Exception ex) {
      return null;
    }
  }

  public Race getBotRace() {
    try {
      Race race = Race.get(this.model.getBWHeadless().getBot().getRace());
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
    try {
      closeProgramRequest(stage);
    } catch (Exception ex) {
      new ExceptionAlert().showAndWait(null, ex);
    }
  }

  public void mnuEditSettingsClicked() {
    if (this.state != State.IDLE) {
      Platform.runLater(() -> {
        View.displayOperationProhibitedDialog("Program settings are locked while bot is running.");
      });
      return;
    }
    new SettingsWindow().showAndWait();
  }

  public void mnuHelpAboutClicked() {
    new SimpleAlert().showAndWait(AlertType.INFORMATION, DialogTitle.PROGRAM_NAME, DropLauncher.PROGRAM_ABOUT);
  }

  public void btnStartClicked() throws InvalidStateException {
    /* Check if BWAPI.dll is known. */
    String bwapiDllVersion = getBwapiDllVersion();
    if (this.state == State.IDLE
        && Model.isPrefEnabled(BWAPI.Property.WARN_UNKNOWN_BWAPI_DLL.toString())
        && !AdakiteUtils.isNullOrEmpty(bwapiDllVersion)
        && bwapiDllVersion.equalsIgnoreCase(BWAPI.DLL_UNKNOWN)) {
      Alert alert = new Alert(AlertType.CONFIRMATION);
      alert.setTitle("Warning");
      alert.setContentText("The BWAPI.dll you provided is not on the list of known official BWAPI versions.\n\nDo you want to continue anyway?");
      alert.setHeaderText(null);
      View.addDefaultStylesheet(alert.getDialogPane().getStylesheets());
      ButtonType btnNo = new ButtonType("No");
      ButtonType btnYes = new ButtonType("Yes");
      alert.getButtonTypes().setAll(btnYes, btnNo);
      Optional<ButtonType> result = alert.showAndWait();
      if (result.get() != btnYes) {
        new SimpleAlert().showAndWait(
            AlertType.WARNING,
            DialogTitle.WARNING,
            "Launch aborted!"
        );
        return;
      }
    }

    State prevState = this.state;

    if (prevState == State.LOCKED) {
      throw new InvalidStateException("unable to start/stop, current_state=" + State.LOCKED.toString());
    }

    setState(State.LOCKED);

    switch (prevState) {
      case IDLE:
        /* Start bwheadless. */
        this.view.btnStartEnabled(false);
        new Thread(() -> {
          boolean success = false;
          try {
            this.view.getConsoleOutput().println(View.MessagePrefix.DROPLAUNCHER.toString() + ": connecting bot to StarCraft...");
            startBWHeadless();
            Platform.runLater(() -> {
              this.view.btnStartSetText(View.StartButtonText.STOP.toString());
              this.view.btnStartEnabled(true);
            });
            setState(State.RUNNING);
            success = true;
          } catch (InvalidStateException | IniParseException | IOException | InvalidArgumentException ex) {
            Platform.runLater(() -> {
              new ExceptionAlert().showAndWait(null, ex);
            });
          } catch (MissingBotFileException ex) {
            Platform.runLater(() -> {
              View.displayMissingFieldDialog("bot file (e.g.: *.dll, *.exe, *.jar)");
            });
          } catch (MissingBotNameException ex) {
            Platform.runLater(() -> {
              new ExceptionAlert().showAndWait("something went wrong with setting the bot's name", ex);
            });
          } catch (MissingBotRaceException ex) {
            Platform.runLater(() -> {
              new ExceptionAlert().showAndWait("something went wrong with setting the bot's race", ex);
            });
          } catch (MissingBwapiDllException ex) {
            Platform.runLater(() -> {
              View.displayMissingFieldDialog("BWAPI.dll is not set");
            });
          } catch (MissingBotException ex) {
            Platform.runLater(() -> {
              new ExceptionAlert().showAndWait("something went wrong with preparing the bot's data", ex);
            });
          } catch (InvalidBotTypeException ex) {
            Platform.runLater(() -> {
              new ExceptionAlert().showAndWait("bot type is not supported", ex);
            });
          } catch (MissingStarcraftExeException ex) {
            //TODO: Clear StarCraft.exe path. This exception could be because the provided path was not found.
            Platform.runLater(() -> {
              View.displayMissingFieldDialog("path to StarCraft.exe");
            });
          }
          if (!success) {
            this.view.getConsoleOutput().println(View.MessagePrefix.DROPLAUNCHER.toString() + ": failed to connect bot to StarCraft");
            setState(prevState);
            Platform.runLater(() -> {
              this.view.btnStartSetText(View.StartButtonText.START.toString());
              this.view.btnStartEnabled(true);
            });
          }
        }).start();
        return;
      case RUNNING:
        /* Stop bwheadless. */
        this.view.btnStartEnabled(false);
        new Thread(() -> {
          try {
            this.view.getConsoleOutput().println(View.MessagePrefix.DROPLAUNCHER.get() + "ejecting bot...");
            stopBWHeadless();
            Platform.runLater(() -> {
              this.view.btnStartSetText(View.StartButtonText.START.toString());
              this.view.btnStartEnabled(true);
            });
            setState(State.IDLE);
            this.view.getConsoleOutput().println(View.MessagePrefix.DROPLAUNCHER.get() + "bot has been ejected");
          } catch (Exception ex) {
            /* Stop failed. */
            Platform.runLater(() -> {
              new ExceptionAlert().showAndWait(null, ex);
            });
            this.view.getConsoleOutput().println(View.MessagePrefix.DROPLAUNCHER.get() + "failed to eject bot");
            setState(prevState);
            Platform.runLater(() -> {
              this.view.btnStartSetText(View.StartButtonText.STOP.toString());
              this.view.btnStartEnabled(true);
            });
          }
        }).start();
        return;
      default:
        break;
    }

    if (this.state == State.LOCKED) {
      throw new InvalidStateException("current_state=" + State.LOCKED.toString());
    }
  }

  public void botRaceChanged(String str) {
    if (this.state != State.IDLE) {
      Platform.runLater(() -> {
        View.displayWarningDialog(DialogTitle.WARNING, "Changing the bot's race while it is running has no effect.");
      });
      this.view.update();
      return;
    }
    try {
      this.model.getBWHeadless().getBot().setRace(str);
//      this.view.updateRaceChoiceBox(); //TODO: Why do we have to do this? Remove?
    } catch (Exception ex) {
      new ExceptionAlert().showAndWait(null, ex);
    }
  }

  //TODO: Provide some feedback when a user types an invalid bot name.
  public void botNameChanged(String str) {
    if (this.state != State.IDLE) {
      Platform.runLater(() -> {
        View.displayWarningDialog(DialogTitle.WARNING, "Changing the bot's name while it is running has no effect.");
      });
      this.view.update();
      return;
    }
    try {
      if (AdakiteUtils.isNullOrEmpty(str, true)) {
        this.model.getBWHeadless().getBot().setName(Bot.DEFAULT_NAME);
      } else {
        String cleaned = Starcraft.cleanProfileName(str);
        this.model.getBWHeadless().getBot().setName(cleaned);
      }
    } catch (Exception ex) {
      new ExceptionAlert().showAndWait(null, ex);
    }
  }

}
