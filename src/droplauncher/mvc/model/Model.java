package droplauncher.mvc.model;

import adakite.ini.INI;
import adakite.util.AdakiteUtils;
import droplauncher.bwheadless.BWHeadless;
import droplauncher.bwheadless.BotModule;
import droplauncher.bwheadless.KillableTask;
import droplauncher.starcraft.Race;
import droplauncher.util.Constants;
import droplauncher.util.Debugging;
import droplauncher.util.SettingsKey;
import droplauncher.util.windows.Task;
import droplauncher.util.windows.TaskTracker;
import droplauncher.util.windows.Tasklist;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import net.lingala.zip4j.core.ZipFile;
import org.apache.commons.io.FileUtils;

public class Model {

  private static final Logger LOGGER = Logger.getLogger(Model.class.getName());
  private static final boolean DEBUG_CLASS = (Debugging.isEnabled() && true);
  private static final boolean DEBUG_KILL = false;

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
      parseSettings(this.ini);
      this.bwheadless.parseSettings(this.ini);
    } catch (IOException ex) {
      LOGGER.log(Debugging.getLoggerLevel(), null, ex);
    }
  }

  public INI getINI() {
    return this.ini;
  }

  public BWHeadless getBWHeadless() {
    return this.bwheadless;
  }

  /**
   * Reads a dropped or selected file which is meant for bwheadless and
   * sets the appropiate settings.
   *
   * @param path specified file to process
   */
  private void processFile(Path path) {
    String ext = AdakiteUtils.getFileExtension(path).toLowerCase();
    if (!AdakiteUtils.isNullOrEmpty(ext)) {
      if (ext.equals("zip")) {
        try {
          ZipFile zipFile = new ZipFile(path.toAbsolutePath().toString());
          if (zipFile.isEncrypted()) {
            return;
          }
          Path tmpDir = Paths.get(Constants.TEMP_DIRECTORY);
          FileUtils.deleteDirectory(tmpDir.toFile());
          AdakiteUtils.createDirectory(tmpDir);
          zipFile.extractAll(tmpDir.toAbsolutePath().toString());
          Path[] tmpList = AdakiteUtils.getDirectoryContents(tmpDir);
          for (Path tmpPath : tmpList) {
            if (!AdakiteUtils.directoryExists(tmpPath)) {
              Path dest = tmpPath.getFileName();
              FileUtils.copyFile(tmpPath.toFile(), dest.getFileName().toFile());
              processFile(tmpPath);
            }
          }
        } catch (Exception ex) {
          return;
        }
      } else if (ext.equals("dll") || ext.equals("exe")) {
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
        /* Treat as a config file. */
        this.bwheadless.getExtraBotFiles().add(path);
      }
    } else {
      /* Unrecognized file */
      /* Do nothing. */
    }
  }

  public void parseSettings(INI ini) {
    String val;
    if (!AdakiteUtils.isNullOrEmpty(val = ini.getValue(Constants.DROPLAUNCHER_INI_SECTION, SettingsKey.JAVA_EXE.toString()))
        && AdakiteUtils.fileExists(Paths.get(val))) {
      this.ini.set(Constants.DROPLAUNCHER_INI_SECTION, SettingsKey.JAVA_EXE.toString(), val);
    }
  }

  public void startBWHeadless() {
    this.taskTracker.update();
    this.bwheadless.start();
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
        if (DEBUG_KILL) {
          System.out.println("Killing: " + task.getPID() + ":" + task.getImageName());
        }
        tasklist.kill(task.getPID());
        continue;
      }
      /* Only kill tasks whose names match known associated tasks. */
      for (KillableTask kt : KillableTask.values()) {
        if (kt.toString().equalsIgnoreCase(task.getImageName())) {
          if (DEBUG_KILL) {
            System.out.println("Killing: " + task.getPID() + ":" + task.getImageName());
          }
          tasklist.kill(task.getPID());
          break;
        }
      }
    }
    this.bwheadless.stop();
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
          for (Path tmpPath : tmpList) {
            fileList.add(tmpPath);
          }
        } catch (IOException ex) {
          if (DEBUG_CLASS) {
            LOGGER.log(Debugging.getLoggerLevel(), "unable to get directory contents for: " + file.getAbsolutePath(), ex);
          }
        }
      } else if (file.isFile()) {
        fileList.add(file.toPath());
      } else {
        if (DEBUG_CLASS) {
          LOGGER.log(Debugging.getLoggerLevel(), "unknown file dropped: " + file.getAbsolutePath());
        }
      }
    }

    /* Process all files. */
    for (Path path : fileList) {
      processFile(path);
    }
  }

}
