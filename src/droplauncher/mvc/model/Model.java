package droplauncher.mvc.model;

import adakite.ini.INI;
import adakite.util.AdakiteUtils;
import droplauncher.bwheadless.BWHeadless;
import droplauncher.bwheadless.BotFile;
import droplauncher.bwheadless.KillableTask;
import droplauncher.starcraft.Race;
import droplauncher.util.Constants;
import droplauncher.util.windows.Task;
import droplauncher.util.windows.TaskTracker;
import droplauncher.util.windows.Tasklist;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Model {

  private static final Logger LOGGER = LogManager.getLogger();

  private INI ini;
  private BWHeadless bwheadless;
  private TaskTracker taskTracker;

  public Model() {
    this.ini = new INI();
    this.bwheadless = new BWHeadless();
    this.taskTracker = new TaskTracker();

    this.bwheadless.setINI(this.ini);
    try {
      this.ini.open(Constants.DROPLAUNCHER_INI_PATH);
      this.bwheadless.parseSettings(this.ini);
    } catch (IOException ex) {
      LOGGER.error(ex);
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
    String ext = AdakiteUtils.getFileExtension(path).toLowerCase(Locale.US);
    if (AdakiteUtils.isNullOrEmpty(ext)) {
      return;
    }

    if (ext.equals("zip")) {
      try {
        ZipFile zipFile = new ZipFile(path.toAbsolutePath().toString());
        if (zipFile.isEncrypted()) {
//            throw new EncryptedArchiveException();
          LOGGER.warn("unsupported encrypted archive: " + zipFile.getFile().getAbsolutePath());
          return;
        }
        Path tmpDir = Paths.get(Constants.TEMP_DIRECTORY).toAbsolutePath();
        FileUtils.deleteDirectory(tmpDir.toFile());
        AdakiteUtils.createDirectory(tmpDir);
        zipFile.extractAll(tmpDir.toString());
        Path[] tmpList = AdakiteUtils.getDirectoryContents(tmpDir);
        for (Path tmpPath : tmpList) {
          if (!AdakiteUtils.directoryExists(tmpPath)) {
            Path dest = tmpPath.getFileName();
            FileUtils.copyFile(tmpPath.toFile(), dest.getFileName().toFile());
            processFile(tmpPath);
          }
        }
      } catch (IOException | ZipException ex) {
        LOGGER.warn("unable to process ZIP file: " + path.toAbsolutePath().toString(), ex);
        return;
      }
    } else if (ext.equals("dll") || ext.equals("exe")) {
      if (path.getFileName().toString().equalsIgnoreCase("BWAPI.dll")) {
        /* BWAPI.dll */
        this.bwheadless.setBwapiDll(path.toAbsolutePath().toString());
      } else {
        /* Bot file */
        this.bwheadless.setBotFile(path.toAbsolutePath().toString());
        this.bwheadless.setBotName(AdakiteUtils.getFilenameNoExt(path));
        this.bwheadless.setBotRace(Race.RANDOM);
      }
    } else {
      /* Treat as a config file. */
      this.bwheadless.getExtraBotFiles().add(path);
    }
  }

  public void startBWHeadless() throws IOException {
    this.taskTracker.update();
    this.bwheadless.start();
  }

  public void stopBWHeadless() throws IOException {
    /* Kill new tasks that were started with bwheadless. */
    this.taskTracker.updateNewTasks();
    ArrayList<Task> tasks = this.taskTracker.getNewTasks();
    Tasklist tasklist = new Tasklist();
    boolean isClient = this.bwheadless.getBotFile().getType() == BotFile.Type.CLIENT;
    String botName = this.bwheadless.getBotFile().getPath().getFileName().toString();
    for (Task task : tasks) {
      /* Kill bot client. */
      if (isClient && botName.contains(task.getImageName())) {
        LOGGER.info("Killing: " + task.getPID() + ":" + task.getImageName());
        tasklist.kill(task.getPID());
        continue;
      }
      /* Only kill tasks whose names match known associated tasks. */
      for (KillableTask kt : KillableTask.values()) {
        if (kt.toString().equalsIgnoreCase(task.getImageName())) {
          LOGGER.info("Killing: " + task.getPID() + ":" + task.getImageName());
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
          LOGGER.error("unable to get directory contents for: " + file.getAbsolutePath(), ex);
        }
      } else if (file.isFile()) {
        fileList.add(file.toPath());
      } else {
        LOGGER.warn("unknown file dropped: " + file.getAbsolutePath());
      }
    }

    /* Process all files. */
    for (Path path : fileList) {
      processFile(path);
    }
  }

}
