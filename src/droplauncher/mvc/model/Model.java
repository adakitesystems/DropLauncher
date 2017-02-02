package droplauncher.mvc.model;

import adakite.utils.AdakiteUtils;
import droplauncher.mvc.view.View;
import droplauncher.bwheadless.BWHeadless;
import droplauncher.bwheadless.BotFile;
import droplauncher.bwheadless.KillableTask;
import droplauncher.bwheadless.ReadyStatus;
import droplauncher.ini.IniFile;
import droplauncher.ini.PredefinedVariable;
import droplauncher.starcraft.Race;
import droplauncher.util.Constants;
import droplauncher.util.windows.Task;
import droplauncher.util.windows.TaskTracker;
import droplauncher.util.windows.Tasklist;
import droplauncher.util.windows.Windows;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.logging.Logger;
import javafx.beans.property.SimpleStringProperty;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

public class Model {

  private static final Logger LOGGER = Logger.getLogger(Model.class.getName());
  private static final boolean CLASS_DEBUG = (Constants.DEBUG && true);

  public static final String DROP_LAUNCHER_INI = "droplauncher.ini";

  private View view;

  private BWHeadless bwheadless;
  private IniFile iniFile;
  private TaskTracker taskTracker;

  private Path javaPath;

  private SimpleStringProperty botRank;

  public Model() {
    this.bwheadless = new BWHeadless();
    this.iniFile = new IniFile();
    this.taskTracker = new TaskTracker();
    this.javaPath = null;
    this.botRank = new SimpleStringProperty();

    this.bwheadless.setIniFile(this.iniFile);
    try {
      this.iniFile.open(Paths.get(DROP_LAUNCHER_INI));
      this.bwheadless.readSettingsFile(this.iniFile);
      readSettingsFile(this.iniFile);
    } catch (Exception ex) {
      LOGGER.log(Constants.DEFAULT_LOG_LEVEL, null, ex);
    }
  }

  public String getBotRank() {
    return this.botRank.get();
  }

  public void setBotRank(String str) {
    this.botRank.set(str);
  }

  public SimpleStringProperty botRankProperty() {
    return this.botRank;
  }

//  private void closeProgram() {
//    if (this.bwheadless.isRunning()) {
//      stopBWHeadless();
//    }
//    System.exit(0);
//  }

//  public void closeView() {
//    closeProgram();
//  }

  public void setView(View view) {
    this.view = view;
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
        }
      } else {
        /* Possibly a config file */
        this.bwheadless.getMiscFiles().add(path);
      }
    } else {
      /* Unrecognized file */
      /* Do nothing. */
    }
  }

  public void readSettingsFile(IniFile ini) throws IOException {
    String val;
    if (!AdakiteUtils.isNullOrEmpty(val = ini.getValue("droplauncher", PredefinedVariable.JAVA_EXE.toString()))
        && AdakiteUtils.fileExists(Paths.get(val))) {
      this.javaPath = Paths.get(val);
      this.bwheadless.setJavaPath(this.javaPath);
    } else {
      this.iniFile.setVariable("droplauncher", PredefinedVariable.JAVA_EXE.toString(), Windows.JAVA_EXE.toAbsolutePath().toString());
      this.bwheadless.setJavaPath(this.javaPath);
    }
  }

  public void startBWHeadless() {
    /* Start bwheadless. */
    ReadyStatus status = this.bwheadless.getReadyStatus();
    if (status != ReadyStatus.READY) {
//      this.view.showMessageBox(JOptionPane.ERROR_MESSAGE, "Not ready: " + status.toString());
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
    boolean isClient = this.bwheadless.getBotModule().getType() == BotFile.Type.CLIENT;
    String botModuleName = this.bwheadless.getBotModule().getPath().getFileName().toString();
    for (Task task : tasks) {
      /* Kill bot module. */
      if (isClient && task.getImageName().equalsIgnoreCase(botModuleName)) {
        tasklist.kill(task.getPID());
        continue;
      }
      /* Only kill tasks whose names match known associated tasks. */
      for (KillableTask kt : KillableTask.values()) {
        tasklist.kill(task.getPID());
        break;
      }
    }
    this.bwheadless.stop();
  }

  /* ************************************************************ */
  /* Events from View */
  /* ************************************************************ */

//  public void boxDropFilesMouseClicked(MouseEvent evt) {
//    JFileChooser fc = new JFileChooser();
//    fc.setDialogTitle("Select bot files ...");
//    fc.setAcceptAllFileFilterUsed(false);
//    fc.setMultiSelectionEnabled(true);
//    fc.setFileFilter(new FileNameExtensionFilter("All supported files (*.dll, *.exe)", "dll", "exe"));
////    if (this.view.showFileChooser(fc) == JFileChooser.APPROVE_OPTION) {
////      File[] fileList = fc.getSelectedFiles();
////      if (fileList != null && fileList.length > 0) {
////        for (File file : fileList) {
////          processFile(file.toPath());
////        }
////      }
////      this.view.update();
////    }
//  }

//  public void btnLaunchActionPerformed(ActionEvent evt) {
//    if (this.bwheadless.isRunning()) {
//      stopBWHeadless();
//    } else {
//      startBWHeadless();
//    }
////    this.view.update();
//  }

//  public void btnStarcraftExeActionPerformed(ActionEvent evt) {
//    //TODO: Only accept StarCraft.exe file selection.
//    //TODO: Guess StarCraft directory or read from registry.
//    JFileChooser fc = new JFileChooser(new File("C:\\StarCraft"));
//    fc.setDialogTitle("Select StarCraft.exe ...");
//    fc.setAcceptAllFileFilterUsed(false);
//    fc.setFileFilter(new FileNameExtensionFilter("*.exe", "exe"));
////    if (this.view.showFileChooser(fc) == JFileChooser.APPROVE_OPTION) {
////      File file = fc.getSelectedFile();
////      if (file != null) {
////        this.bwheadless.setStarcraftExe(file.getAbsolutePath());
////        this.view.getLabelStarcraftExeText().setText(this.bwheadless.getStarcraftExe());
////      }
////    }
//  }

//  public void filesDropped(File[] files) {
//    /* Parse all objects dropped into a complete list of files dropped since
//       dropping a directory does NOT include all subdirectories and
//       files by default. */
//    ArrayList<Path> fileList = new ArrayList<>();
//    for (File file : files) {
//      if (file.isDirectory()) {
//        try {
//          Path[] tmpList = AdakiteUtils.getDirectoryContents(file.toPath(), true);
//          for (Path tmpPath : tmpList) {
//            fileList.add(tmpPath);
//          }
//        } catch (IOException ex) {
//          if (CLASS_DEBUG) {
//            LOGGER.log(Constants.DEFAULT_LOG_LEVEL, null, ex);
//          }
//        }
//      } else if (file.isFile()) {
//        fileList.add(file.toPath());
//      } else {
//        if (CLASS_DEBUG) {
//          LOGGER.log(Constants.DEFAULT_LOG_LEVEL, "Unknown file dropped: " + file.getAbsolutePath());
//        }
//      }
//    }
//
//    /* Process all files. */
//    for (Path path : fileList) {
//      processFile(path);
//    }
//
////    this.view.update();
//  }

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
