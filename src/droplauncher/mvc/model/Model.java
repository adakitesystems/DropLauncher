package droplauncher.mvc.model;

import adakite.ini.INI;
import adakite.util.AdakiteUtils;
import droplauncher.bwheadless.BWHeadless;
import droplauncher.bwheadless.BotModule;
import droplauncher.bwheadless.KillableTask;
import droplauncher.bwheadless.ReadyStatus;
import droplauncher.starcraft.Race;
import droplauncher.util.Constants;
import droplauncher.util.SettingsKey;
import droplauncher.util.windows.Task;
import droplauncher.util.windows.TaskTracker;
import droplauncher.util.windows.Tasklist;
import droplauncher.util.windows.Windows;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class Model {

  private static final Logger LOGGER = Logger.getLogger(Model.class.getName());
  private static final boolean CLASS_DEBUG = (Constants.DEBUG && true);
  private static final boolean KILL_DEBUG = false;

  private INI ini;
  private BWHeadless bwheadless;
  private TaskTracker taskTracker;

  public Model() {
    this.ini = new INI();
    this.bwheadless = new BWHeadless();
    this.taskTracker = new TaskTracker();

    this.bwheadless.setINI(this.ini);
    try {
      this.ini.open(Paths.get(Constants.DROPLAUNCHER_INI));
      readSettingsFile(this.ini);
      this.bwheadless.readSettingsFile(this.ini);
    } catch (IOException ex) {
      LOGGER.log(Constants.DEFAULT_LOG_LEVEL, null, ex);
    }
  }

  public INI getINI() {
    return this.ini;
  }

  public BWHeadless getBWHeadless() {
    return this.bwheadless;
  }

  /**
   * Reads a dropped or selected file which is meant for the
   * bwheadless.exe process and sets the appropiate settings.
   *
   * @param path specified file to process
   */
  private void processFile(Path path) {
    String ext = AdakiteUtils.getFileExtension(path).toLowerCase();
    if (!AdakiteUtils.isNullOrEmpty(ext)) {
      if (ext.equals("dll") || ext.equals("exe") || ext.equals("jar")) {
        if (path.getFileName().toString().equalsIgnoreCase("BWAPI.dll")) {
          /* BWAPI.dll */
          this.bwheadless.setBwapiDll(path.toAbsolutePath().toString());
        } else {
          /* Bot module */
          this.bwheadless.setBotModule(path.toAbsolutePath().toString());
          this.bwheadless.setBotName(AdakiteUtils.removeFileExtension(path.getFileName().toString()));
          this.bwheadless.setBotRace(Race.RANDOM);
        }
      } else {
        /* Possibly a config file */
        this.bwheadless.getExtraBotFiles().add(path);
      }
    } else {
      /* Unrecognized file */
      /* Do nothing. */
    }
  }

  public void readSettingsFile(INI ini) {
    String val;
    if (!AdakiteUtils.isNullOrEmpty(val = ini.getValue(Constants.DROPLAUNCHER_INI_SECTION, SettingsKey.JAVA_EXE.toString()))
        && AdakiteUtils.fileExists(Paths.get(val))) {
      this.ini.set(Constants.DROPLAUNCHER_INI_SECTION, SettingsKey.JAVA_EXE.toString(), val);
    } else {
      this.ini.set(Constants.DROPLAUNCHER_INI_SECTION, SettingsKey.JAVA_EXE.toString(), Windows.DEFAULT_JAVA_EXE.toAbsolutePath().toString());
    }
  }

  public void startBWHeadless() {
    /* Start bwheadless. */
    ReadyStatus status = this.bwheadless.getReadyStatus();
    if (status != ReadyStatus.READY) {
      if (CLASS_DEBUG) {
        System.out.println("Model: startBWHeadless: not ready: " + status.toString());
      }
    } else {
      this.taskTracker.update();
      this.bwheadless.start();
    }
  }

  public void stopBWHeadless() {
    /* Kill new tasks that were started with bwheadless. */
    this.taskTracker.updateNewTasks();
    ArrayList<Task> tasks = this.taskTracker.getNewTasks();
    Tasklist tasklist = new Tasklist();
    boolean isClient = this.bwheadless.getBotModule().getType() == BotModule.Type.CLIENT;
    String botModuleName = this.bwheadless.getBotModule().getPath().getFileName().toString();
    for (Task task : tasks) {
      /* Kill bot module. */
      if (isClient && botModuleName.contains(task.getImageName())) {
        if (KILL_DEBUG) {
          System.out.println("Killing: " + task.getPID() + ":" + task.getImageName());
        }
        tasklist.kill(task.getPID());
        continue;
      }
      /* Only kill tasks whose names match known associated tasks. */
      for (KillableTask kt : KillableTask.values()) {
        if (kt.toString().equalsIgnoreCase(task.getImageName())) {
          if (KILL_DEBUG) {
            System.out.println("Killing: " + task.getPID() + ":" + task.getImageName());
          }
          tasklist.kill(task.getPID());
          break;
        }
      }
    }
    this.bwheadless.stop();
  }

  /* ************************************************************ */
  /* Events from View */
  /* ************************************************************ */

  public void filesDropped(List<File> files) {
    /* Parse all objects dropped into a complete list of files dropped since
       dropping a directory does NOT include all subdirectories and
       files by default. */
    ArrayList<Path> fileList = new ArrayList<>();
    for (File file : files) {
      if (file.isDirectory()) {
        try {
          Path[] tmpList = AdakiteUtils.getDirectoryContents(file.toPath(), true);
          for (Path tmpPath : tmpList) {
            fileList.add(tmpPath);
          }
        } catch (IOException ex) {
          if (CLASS_DEBUG) {
            LOGGER.log(Constants.DEFAULT_LOG_LEVEL, null, ex);
          }
        }
      } else if (file.isFile()) {
        fileList.add(file.toPath());
      } else {
        if (CLASS_DEBUG) {
          LOGGER.log(Constants.DEFAULT_LOG_LEVEL, "Unknown file dropped: " + file.getAbsolutePath());
        }
      }
    }

    /* Process all files. */
    for (Path path : fileList) {
      processFile(path);
    }
  }

//  public void mnuFileExitActionPerformed(ActionEvent evt) {
//    closeProgram();
//  }

//  public void mnuHelpAboutActionPerformed(ActionEvent evt) {
//    StringBuilder sb = new StringBuilder();
//    sb.append(Constants.PROGRAM_NAME).append(System.lineSeparator())
//        .append(System.lineSeparator())
//        .append("Version: " + Constants.PROGRAM_VERSION).append(System.lineSeparator())
//        .append("Author: " + Constants.PROGRAM_AUTHOR).append(System.lineSeparator())
//        .append("Source: " + Constants.PROGRAM_GITHUB).append(System.lineSeparator())
//        .append(System.lineSeparator())
//        .append("License: " + Constants.PROGRAM_LICENSE).append(System.lineSeparator())
//        .append(Constants.PROGRAM_LICENSE_LINK).append(System.lineSeparator());
////    this.view.showMessageBox(JOptionPane.INFORMATION_MESSAGE, sb.toString());
//  }

//  public void rbRaceProtossActionPerformed(ActionEvent evt) {
//    this.bwheadless.setBotRace(Race.PROTOSS);
//  }

//  public void rbRaceRandomActionPerformed(ActionEvent evt) {
//    this.bwheadless.setBotRace(Race.RANDOM);
//  }

//  public void rbRaceTerranActionPerformed(ActionEvent evt) {
//    this.bwheadless.setBotRace(Race.TERRAN);
//  }

//  public void rbRaceZergActionPerformed(ActionEvent evt) {
//    this.bwheadless.setBotRace(Race.ZERG);
//  }

//  public void txtBotNameKeyReleased(KeyEvent evt) {
//    JTextField txt = this.view.getTextFieldBotName();
//    int caret = txt.getCaretPosition();
//    String botName = txt.getText();
//    String botNameFixed = Starcraft.cleanProfileName(botName);
//    if (AdakiteUtils.isNullOrEmpty(botNameFixed)) {
//      botNameFixed = BWHeadless.DEFAULT_BOT_NAME;
//      caret = botNameFixed.length();
//    }
//    if (!botName.equalsIgnoreCase(botNameFixed)) {
//      if (caret < 0) {
//        caret = 0;
//      } else if (caret >= botNameFixed.length()) {
//        caret = botNameFixed.length();
//      } else {
//        caret--;
//      }
//      txt.setText(botNameFixed);
//      txt.setCaretPosition(caret);
//    }
//    this.bwheadless.setBotName(botNameFixed);
//  }

}
