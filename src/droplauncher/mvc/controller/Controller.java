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
import adakite.ini.exception.IniParseException;
import adakite.md5sum.MD5Checksum;
import adakite.util.AdakiteUtils;
import adakite.util.DirectoryMonitor;
import adakite.windows.Windows;
import adakite.windows.task.exception.TasklistParseException;
import droplauncher.bwapi.BWAPI;
import droplauncher.bwapi.bot.Bot;
import droplauncher.mvc.model.Model;
import droplauncher.mvc.view.SettingsWindow;
import droplauncher.mvc.view.SimpleAlert;
import droplauncher.mvc.view.View;
import droplauncher.DropLauncher;
import droplauncher.bwapi.bot.exception.InvalidBwapiDllException;
import droplauncher.bwapi.bot.exception.MissingBotFileException;
import droplauncher.bwapi.bot.exception.MissingBotNameException;
import droplauncher.bwapi.bot.exception.MissingBotRaceException;
import droplauncher.bwapi.bot.exception.MissingBwapiDllException;
import droplauncher.bwheadless.exception.MissingBotException;
import droplauncher.exception.EncryptedArchiveException;
import droplauncher.bwapi.bot.exception.InvalidBotTypeException;
import droplauncher.bwheadless.exception.MissingBWHeadlessExeException;
import droplauncher.mvc.view.ConsoleOutputWrapper;
import droplauncher.mvc.view.ExceptionAlert;
import droplauncher.mvc.view.View.DialogTitle;
import droplauncher.mvc.view.YesNoDialog;
import droplauncher.mvc.view.help.Help;
import droplauncher.starcraft.Starcraft;
import droplauncher.starcraft.Starcraft.Race;
import droplauncher.starcraft.exception.MissingStarcraftExeException;
import droplauncher.starcraft.exception.StarcraftProfileNameException;
import droplauncher.starcraft.exception.UnsupportedStarcraftVersionException;
import droplauncher.util.process.exception.ClosePipeException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
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

public class Controller {

  public enum State {
    LOCKED,
    IDLE,
    RUNNING
  }

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
                                        InvalidArgumentException,
                                        TasklistParseException,
                                        MissingBWHeadlessExeException,
                                        UnsupportedStarcraftVersionException {
    /* Initialize DirectoryMonitor if required. */
    Path starcraftPath = Starcraft.getDirectory();
    if (this.directoryMonitor == null) {
      this.directoryMonitor = new DirectoryMonitor(starcraftPath);
      this.directoryMonitor.getIgnoreList().add("maps"); /* ignore any file/directory containing "*maps*" */
      this.directoryMonitor.getIgnoreList().add("bwta"); /* ignore any file/directory containing "*bwta*" */
      this.directoryMonitor.getIgnoreList().add("bwta2"); /* ignore any file/directory containing "*bwta2*" */
      this.directoryMonitor.getIgnoreList().add("bwapi-data"); /* ignore any file/directory containing "*bwapi-data*" */
      this.directoryMonitor.reset();
    }

    this.model.getBWHeadless()
        .setStarcraftExe(Starcraft.getExe())
        .enableConsoleOutput(new ConsoleOutputWrapper(this.view.getConsoleOutput()));
    this.model.getBWHeadless().start();
  }

  private void stopBWHeadless() throws IOException,
                                       InvalidStateException,
                                       ClosePipeException,
                                       MissingBotFileException,
                                       MissingStarcraftExeException,
                                       TasklistParseException {
    this.model.getBWHeadless().stop();

    if (Model.getSettings().isEnabled(BWAPI.PropertyKey.COPY_WRITE_READ.toString())) {
      /* Copy contents of "bwapi-data/write/" to "bwapi-data/read/". */
      Path bwapiWriteDirectory = this.model.getBWHeadless().getBwapiDirectory().getWriteDirectory();
      Path bwapiReadDirectory = this.model.getBWHeadless().getBwapiDirectory().getReadDirectory();
      String copyMessage = View.MessagePrefix.COPY.get() + bwapiWriteDirectory.toString() + " -> " + bwapiReadDirectory.toString();
      this.view.getConsoleOutput().println(View.MessagePrefix.DROPLAUNCHER.get() + copyMessage);
      FileUtils.copyDirectory(bwapiWriteDirectory.toFile(), bwapiReadDirectory.toFile());
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
    State state = getState();
    switch (state) {
      case IDLE:
        /* Do nothing. */
        break;
      case RUNNING:
        /* Fall through. */
      case LOCKED:
        /* Fall through. */
      default:
        String errorMessage = "program is still in state " + state.toString()
            + AdakiteUtils.newline(2)
            + "Try ejecting the bot first or wait for the current operation to finish."
            ;
        throw new InvalidStateException(errorMessage);
    }

    if (Model.getSettings().isEnabled(Starcraft.PropertyKey.CLEAN_SC_DIR.toString())) {
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
   * sets the appropriate settings.
   *
   * @param file specified file to process
   */
  private void processFile(Path file) throws InvalidArgumentException,
                                             StarcraftProfileNameException,
                                             InvalidBwapiDllException {
    if (getState() != State.IDLE) {
      return;
    }

    if (file == null) {
      throw new InvalidArgumentException(Debugging.cannotBeNull("file"));
    }

    String ext = AdakiteUtils.getFileExtension(file);
    if (AdakiteUtils.isNullOrEmpty(ext)) {
      ext = "";
    } else {
      ext = ext.toLowerCase(Locale.US);
    }
    switch (ext) {
      case "zip":
        processZipFile(file);
        break;
      case "dll":
        /* Fall through. */
      case "exe":
        /* Fall through. */
      case "jar":
        if (file.getFileName().toString().equalsIgnoreCase(Starcraft.BINARY_FILENAME)) {
          /* Set StarCraft.exe path. */
          Model.getSettings().setValue(Starcraft.PropertyKey.STARCRAFT_EXE.toString(), file.toAbsolutePath().toString());
          Platform.runLater(() -> {
            new SimpleAlert().showAndWait(AlertType.INFORMATION,
                DialogTitle.PROGRAM_NAME,
                Starcraft.BINARY_FILENAME + " path set to: " + file.toAbsolutePath().toString()
            );
          });
        } else if (file.getFileName().toString().equalsIgnoreCase(BWAPI.DLL_FILENAME_RELEASE)) {
          /* Set BWAPI.dll path. */
          this.model.getBWHeadless().getBot().setBwapiDll(file.toAbsolutePath());
        } else {
          /* Set bot file. */
          this.model.getBWHeadless().getBot().setFile(file.toAbsolutePath());
          /* Set bot race. */
          this.model.getBWHeadless().getBot().setRace(Race.RANDOM.toString());
          /* Set clean bot name. */
          String name = FilenameUtils.getBaseName(file.toString());
          name = Starcraft.sanitizeProfileName(name);
          this.model.getBWHeadless().getBot().setName(name);
        }
        break;
      default:
        /* Treat as a config file. */
        this.model.getBWHeadless().getBot().addExtraFile(file);
        break;
    }
  }

  /**
   * Processes a ZIP file. Extracts the ZIP file and processes its contents
   * via {@link #processFile(java.nio.file.Path)}.
   *
   * @param file specified path to the ZIP file
   * @see #processFile(java.nio.file.Path)
   */
  private void processZipFile(Path file) {
    try {
      if (file == null) {
        throw new IllegalArgumentException(Debugging.cannotBeNull("file"));
      } else {
        String ext = AdakiteUtils.getFileExtension(file);
        if (AdakiteUtils.isNullOrEmpty(ext) || !ext.equalsIgnoreCase("zip")) {
          throw new IllegalArgumentException("path does not appear to be a ZIP file: " + file.toString());
        }
      }

      ZipFile zipFile = new ZipFile(file.toAbsolutePath().toString());
      if (zipFile.isEncrypted()) {
        throw new EncryptedArchiveException("encrypted archive not supported: " + zipFile.getFile().getAbsolutePath());
      }
      /* Create temporary directory. */
      Path tmpDir = DropLauncher.TEMP_DIRECTORY;
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

  public void filesDropped(List<File> paths) throws IOException,
                                                    InvalidArgumentException,
                                                    StarcraftProfileNameException,
                                                    InvalidBwapiDllException {
    if (getState() != State.IDLE) {
      Platform.runLater(() -> {
        View.displayOperationProhibitedDialog("Loading bot files is not allowed while a bot is running.");
      });
      return;
    }

    /* Parse all objects dropped into a complete list of files dropped since
       dropping a directory does NOT include all subdirectories and
       files by default. */
    List<Path> fileList = new ArrayList<>();
    for (File path : paths) {
      if (path.isDirectory()) {
        Path[] tmpList = AdakiteUtils.getDirectoryContents(path.toPath(), true);
        fileList.addAll(Arrays.asList(tmpList));
      } else if (path.isFile()) {
        fileList.add(path.toPath());
      } else {
        throw new InvalidArgumentException("unknown file dropped: " + path.getAbsolutePath());
      }
    }

    /* Keep track of previous number of extra bot files. */
    int prevNum = this.model.getBWHeadless().getBot().getExtraFiles().size();

    /* Process all files. */
    for (Path file : fileList) {
      processFile(file);
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
        String message = "The following file"
            + ((currNum != 1) ? "s" : "") + " will be treated as "
            + ((currNum != 1) ? "configuration files" : "a configuration file")
            + " and will be copied to the \"" + BWAPI.ROOT_DIRECTORY.resolve(BWAPI.AI_DIRECTORY).toString() + "\" directory when the bot is launched: "
            + AdakiteUtils.newline(2) + sb.toString();
        new SimpleAlert().showAndWait(
            AlertType.INFORMATION,
            DialogTitle.PROGRAM_NAME,
            message
        );
      });
    }

    this.view.update();
  }

  public void clearExtraBotFiles() {
    this.model.getBWHeadless().getBot().clearExtraFiles();
  }

  /* ************************************************************ */
  /* Accessible data */
  /* ************************************************************ */

  /**
   * Gets the manually set state indicator of the program.
   */
  public State getState() {
    State currentState;
    synchronized(this.stateLock) {
      currentState = this.state;
    }
    return currentState;
  }

  public String getBotFilename() {
    try {
      String name = FilenameUtils.getName(this.model.getBWHeadless().getBot().getFile().toString());
      return name;
    } catch (Exception ex) {
      return null;
    }
  }

  public String getBwapiDllVersion() {
    try {
      String dll = this.model.getBWHeadless().getBot().getBwapiDll().toString();
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

  public List<String> getExtraBotFiles() {
    return this.model.getBWHeadless().getBot().getExtraFiles();
  }

  /* ************************************************************ */
  /* Events called by a view */
  /* ************************************************************ */

  public void mnuFileSelectBotFilesClicked(Stage stage) {
    FileChooser fc = new FileChooser();
    fc.setTitle("Select bot files ...");
    Path initialDirectory = Windows.getUserDesktopDirectory();
    if (initialDirectory != null) {
      fc.setInitialDirectory(initialDirectory.toFile());
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
    if (getState() != State.IDLE) {
      Platform.runLater(() -> {
        View.displayOperationProhibitedDialog("Program settings are locked while bot is running.");
      });
      return;
    }
    new SettingsWindow().showAndWait();
  }

  public void mnuHelpContentsClicked() {
    Platform.runLater(() -> {
      new Help().show();
    });
  }

  public void mnuHelpAboutClicked() {
    new SimpleAlert().showAndWait(AlertType.INFORMATION, DialogTitle.PROGRAM_NAME, DropLauncher.PROGRAM_ABOUT);
  }

  public void btnStartClicked() throws InvalidStateException {
    /* Check if BWAPI.dll is known. */
    String bwapiDllVersion = getBwapiDllVersion();
    if (getState() == State.IDLE
        && Model.getSettings().isEnabled(BWAPI.PropertyKey.WARN_UNKNOWN_BWAPI_DLL.toString())
        && !AdakiteUtils.isNullOrEmpty(bwapiDllVersion)
        && bwapiDllVersion.equalsIgnoreCase(BWAPI.DLL_UNKNOWN)) {
      boolean response = new YesNoDialog().userConfirms(
          "Warning",
          "The " + BWAPI.DLL_FILENAME_RELEASE + " you provided is not on the list of known official BWAPI versions.\n\nDo you want to continue anyway?"
      );
      if (response == false) {
        /* User does not wish to continue. Abort. */
        new SimpleAlert().showAndWait(
            AlertType.WARNING,
            DialogTitle.WARNING,
            "Launch aborted!"
        );
        return;
      }
    }

    State prevState = getState();

    if (prevState == State.LOCKED) {
      throw new InvalidStateException("unable to start/stop, current_state=" + prevState.toString());
    }

    setState(State.LOCKED);

    switch (prevState) {
      case IDLE:
        /* Start bwheadless. */
        this.view.btnStartEnabled(false);
        new Thread(() -> {
          boolean success = false;
          try {
            this.view.getConsoleOutput().println(View.MessagePrefix.DROPLAUNCHER.get() + "Connecting bot to StarCraft...");
            startBWHeadless();
            Platform.runLater(() -> {
              this.view.btnStartSetText(View.StartButtonText.STOP.toString());
              this.view.btnStartEnabled(true);
            });
            setState(State.RUNNING);
            success = true;
          } catch (InvalidStateException
              | IniParseException
              | IOException
              | InvalidArgumentException
              | TasklistParseException ex) {
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
              View.displayMissingFieldDialog(BWAPI.DLL_FILENAME_RELEASE + " is not set");
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
              View.displayMissingFieldDialog("path to " + Starcraft.BINARY_FILENAME);
            });
          } catch (MissingBWHeadlessExeException ex) {
            Platform.runLater(() -> {
              new ExceptionAlert().showAndWait("something went wrong with preparing bwheadless", ex);
            });
          } catch (UnsupportedStarcraftVersionException ex) {
            Platform.runLater(() -> {
              new ExceptionAlert().showAndWait("The selected " + Starcraft.BINARY_FILENAME + " is not supported. Currently, only Brood War 1.16.1 is supported. You can disable this error in the settings if you believe this to be a false positive.", ex);
            });
          }
          if (!success) {
            this.view.getConsoleOutput().println(View.MessagePrefix.DROPLAUNCHER.get() + "Failed to connect bot to StarCraft.");
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
            this.view.getConsoleOutput().println(View.MessagePrefix.DROPLAUNCHER.get() + "Ejecting bot...");
            stopBWHeadless();
            Platform.runLater(() -> {
              this.view.btnStartSetText(View.StartButtonText.START.toString());
              this.view.btnStartEnabled(true);
            });
            setState(State.IDLE);
            this.view.getConsoleOutput().println(View.MessagePrefix.DROPLAUNCHER.get() + View.Message.BOT_EJECTED.toString());
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

    //TODO: It does not seem effectively possible to reach this line. Delete? Refactor above code?
    if (getState() == State.LOCKED) {
      throw new InvalidStateException("current_state=" + State.LOCKED.toString());
    }
  }

  public void botRaceChanged(String str) {
    if (getState() != State.IDLE) {
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
    if (getState() != State.IDLE) {
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
        String cleaned = Starcraft.sanitizeProfileName(str);
        this.model.getBWHeadless().getBot().setName(cleaned);
      }
    } catch (Exception ex) {
      new ExceptionAlert().showAndWait(null, ex);
    }
  }

}
