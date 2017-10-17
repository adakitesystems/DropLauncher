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

package droplauncher.bwapi;

import adakite.debugging.Debugging;
import adakite.exception.InvalidArgumentException;
import adakite.ini.Ini;
import adakite.ini.exception.IniParseException;
import adakite.util.AdakiteUtils;
import droplauncher.DropLauncher;
import droplauncher.bwapi.bot.Bot;
import droplauncher.bwapi.bot.exception.InvalidBotTypeException;
import droplauncher.bwapi.bot.exception.MissingBotFileException;
import droplauncher.bwapi.bot.exception.MissingBotNameException;
import droplauncher.bwapi.bot.exception.MissingBotRaceException;
import droplauncher.bwta.BWTA;
import droplauncher.mvc.model.Model;
import droplauncher.mvc.view.ConsoleOutputWrapper;
import droplauncher.mvc.view.View;
import droplauncher.starcraft.Starcraft;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

public class BwapiDirectory {

  private Path directory;
  private ConsoleOutputWrapper consoleOutput;

  public BwapiDirectory() {
    this.directory = Paths.get("");
    this.consoleOutput = null;
  }

  public void setConsoleOutput(ConsoleOutputWrapper consoleOutput) {
    this.consoleOutput = consoleOutput;
  }

  public Path getDirectory() {
    return this.directory;
  }

  public void setDirectory(Path directory) {
    if (directory == null) {
      throw new IllegalArgumentException(Debugging.Message.CANNOT_BE_NULL.toString("directory"));
    }
    this.directory = directory;
  }

  public Path getAiDirectory() {
    return this.directory.resolve(BWAPI.AI_DIRECTORY);
  }

  public Path getIniFile() {
    return this.directory.resolve(BWAPI.INI_FILE);
  }

  public Path getIniBackupFile() {
    return this.directory.resolve(BWAPI.INI_BACKUP_FILE);
  }

  public Path getReadDirectory() {
    return this.directory.resolve(BWAPI.READ_DIRECTORY);
  }

  public Path getWriteDirectory() {
    return this.directory.resolve(BWAPI.WRITE_DIRECTORY);
  }

  public Path getDataDirectory() {
    return this.directory.resolve(BWAPI.DATA_DIRECTORY);
  }

  public void backupIniFile() throws IOException {
    if (AdakiteUtils.fileExists(getIniFile())) {
      Files.copy(getIniFile(), getIniBackupFile(), StandardCopyOption.REPLACE_EXISTING);
    }
  }

  public void restoreIniFile() throws IOException {
    if (AdakiteUtils.fileExists(getIniBackupFile())) {
      Files.copy(getIniBackupFile(), getIniFile(), StandardCopyOption.REPLACE_EXISTING);
      AdakiteUtils.deleteFile(getIniBackupFile());
    }
  }

  /**
   * Configures BWAPI in the specified StarCraft directory.
   *
   * @param starcraftDirectory specified Starcraft path to use in configuration
   * @param bot specified bot to use in configuration
   * @throws IOException
   * @throws IniParseException
   * @throws MissingBotFileException
   * @throws InvalidBotTypeException
   * @throws InvalidArgumentException
   * @throws MissingBotNameException
   * @throws MissingBotRaceException
   */
  public void configure(Path starcraftDirectory, Bot bot) throws IOException,
                                                                 IniParseException,
                                                                 MissingBotFileException,
                                                                 InvalidArgumentException,
                                                                 InvalidBotTypeException,
                                                                 MissingBotNameException,
                                                                 MissingBotRaceException {
    uilog("Configuring BWAPI in " + starcraftDirectory.toAbsolutePath().toString());
    int logDepth = 2;

    /* Create common BWAPI paths. */
    uilog("Configuring common BWAPI directories", logDepth);
    AdakiteUtils.createDirectory(getDirectory());
    AdakiteUtils.createDirectory(getAiDirectory());
    AdakiteUtils.createDirectory(getReadDirectory());
    AdakiteUtils.createDirectory(getWriteDirectory());
    AdakiteUtils.createDirectory(getDataDirectory());

    /* Create BWTA/BWTA2 paths. */
    uilog("Configuring common BWTA directories", logDepth);
    AdakiteUtils.createDirectory(getDirectory().resolve("BWTA"));
    AdakiteUtils.createDirectory(getDirectory().resolve("BWTA2"));

    /* Check for bwapi.ini existence. */
    uilog("Configuring " + BWAPI.ExtractableFile.BWAPI_INI.toString(), logDepth);
    if (!AdakiteUtils.fileExists(getIniFile())) {
      uilog("Extracting " + BWAPI.ExtractableFile.BWAPI_INI.toString(), logDepth);
      /* If bwapi.ini is not found in the target BWAPI directory, extract it from this program. */
      URL url = DropLauncher.getResource(BWAPI.FILES_RESOURCE_DIRECTORY + BWAPI.ExtractableFile.BWAPI_INI.toString());
      FileUtils.copyURLToFile(url, getIniFile().toFile());
    }
    /* Read the bwapi.ini file. */
    Ini bwapiIni = new Ini();
    uilog("Parsing " + BWAPI.ExtractableFile.BWAPI_INI.toString(), logDepth);
    bwapiIni.parse(getIniFile());

    /* Check for the Broodwar.map file. */
    uilog("Configuring " + BWAPI.ExtractableFile.BROODWAR_MAP.toString(), logDepth);
    Path bwapiBroodwarMap = getDataDirectory().resolve(BWAPI.ExtractableFile.BROODWAR_MAP.toString());
    if (!AdakiteUtils.fileExists(bwapiBroodwarMap)) {
      uilog("Extracting " + BWAPI.ExtractableFile.BROODWAR_MAP.toString(), logDepth);
      /* If Broodwar.map is not found in the target BWAPI directory, extract it from this program. */
      URL url = DropLauncher.getResource(BWAPI.FILES_RESOURCE_DIRECTORY + BWAPI.ExtractableFile.BROODWAR_MAP.toString());
      FileUtils.copyURLToFile(url, bwapiBroodwarMap.toFile());
    }

    /* Check if bot dependencies should be extracted to the StarCraft root directory. */
    if (Model.getSettings().isEnabled(Starcraft.PropertyKey.EXTRACT_BOT_DEPENDENCIES.toString())) {
      uilog("Extracting bot dependencies", logDepth);
      for (BWAPI.ExtractableDll val : BWAPI.ExtractableDll.values()) {
        /* If dependency is not found in the StarCraft root directory, extract it from this program. */
        Path targetDependency = starcraftDirectory.resolve(val.toString());
        if (!AdakiteUtils.fileExists(targetDependency)) {
          URL url = DropLauncher.getResource(BWAPI.DLL_RESOURCE_DIRECTORY + val.toString());
          FileUtils.copyURLToFile(url, targetDependency.toFile());
        }
      }

      /* Extract BWTA cache files. */
      try {
        uilog("Configuring BWTA cache files", logDepth);

        ZipFile bwtaZip = new ZipFile(BWTA.CACHE_ARCHIVE_FILE.toFile());

          /* Check for BWTA version 1 cache files. */
          {
            uilog("Configuring BWTA version 1 cache files", logDepth + 1);
            Path bwtaCacheDirectory = getDirectory().resolve(BWTA.V1_DIRECTORY);
            for (BWTA.CacheV1 val : BWTA.CacheV1.values()) {
              if (!AdakiteUtils.fileExists(bwtaCacheDirectory.resolve(val.toString()))) {
                bwtaZip.extractFile(BWTA.V1_DIRECTORY.resolve(val.toString()).toString(), getDirectory().toString());
              }
            }
          }

          /* Check for BWTA version 2 cache files. */
          {
            uilog("Configuring BWTA version 2 cache files", logDepth + 1);
            Path bwtaCacheDirectory = getDirectory().resolve(BWTA.V2_DIRECTORY);
            for (BWTA.CacheV2 val : BWTA.CacheV2.values()) {
              if (!AdakiteUtils.fileExists(bwtaCacheDirectory.resolve(val.toString()))) {
                bwtaZip.extractFile(BWTA.V2_DIRECTORY.resolve(val.toString()).toString(), getDirectory().toString());
              }
            }
          }
      } catch (ZipException ex) {
        /* Do nothing. */
      }
    }

    uilog("Determining bot type", logDepth);
    switch (bot.getType()) {
      case DLL: {
        uilog("Configuring DLL bot", logDepth + 1);
        /* Copy DLL to "bwapi-data/AI/" directory. */
        Path src = bot.getFile();
        Path dest = getAiDirectory().resolve(FilenameUtils.getName(bot.getFile().toString()));
        AdakiteUtils.createDirectory(dest.getParent());
        Files.copy(src, dest, StandardCopyOption.REPLACE_EXISTING);
        bot.setFile(dest);
//        Path iniAiPath = getAiPath().resolve(FilenameUtils.getName(bot.getPath().toString()));
        Path iniAiPath = BWAPI.ROOT_DIRECTORY.resolve(BWAPI.AI_DIRECTORY).resolve(FilenameUtils.getName(bot.getFile().toString()));
        bwapiIni.setValue("ai", "ai", iniAiPath.toString());
        break;
      }
      case CLIENT: {
        uilog("Configuring client bot", logDepth + 1);
        /* Copy client to StarCraft root directory. */
        Path src = bot.getFile();
        Path dest = starcraftDirectory.resolve(FilenameUtils.getName(bot.getFile().toString()));
        Files.copy(src, dest, StandardCopyOption.REPLACE_EXISTING);
        bot.setFile(dest);
        bwapiIni.commentVariable("ai", "ai");
        break;
      }
      default: {
        throw new InvalidBotTypeException();
      }
    }

    uilog("Configuring " + BWAPI.ExtractableFile.BWAPI_INI.toString(), logDepth);
    /* Not tested yet whether it matters if ai_dbg is enabled. Disable anyway. */
    bwapiIni.commentVariable("ai", "ai_dbg");

    /* Set relevant variables. (bwheadless ignores these in headless mode) */
    bwapiIni.setValue("auto_menu", "auto_menu", "LAN");
    bwapiIni.setValue("auto_menu", "lan_mode", "Local Area Network (UDP)");
    bwapiIni.setValue("auto_menu", "character_name", bot.getName());
    bwapiIni.setValue("auto_menu", "pause_dbg", "OFF");
//    bwapiIni.set("auto_menu", "auto_restart", "OFF");
    bwapiIni.setValue("auto_menu", "race", bot.getRace());

    uilog("Creating a backup of " + BWAPI.ExtractableFile.BWAPI_INI.toString(), logDepth);
    /* Update bwapi.ini file. */
    bwapiIni.store(getIniFile());

    if (bot.getExtraFiles().size() > 0) {
      uilog("Copying extra bot configuration files to " + getAiDirectory().toString(), logDepth);
    }
    /* Copy extra files to common bot I/O directories. */
    for (String file : bot.getExtraFiles()) {
      if (AdakiteUtils.fileExists(Paths.get(file))) {
        Files.copy(Paths.get(file), Paths.get(getAiDirectory().toString(), FilenameUtils.getName(file)), StandardCopyOption.REPLACE_EXISTING);
      }
    }
  }

  private void uilog(String message, int depth) {
    if (this.consoleOutput == null) {
      return;
    }
    String ret = "";
    for (int i = 1; i < depth; ++i) {
      ret += "    ";
    }
    ret += message + "...";
    ret = View.MessagePrefix.DROPLAUNCHER.get(ret);
    this.consoleOutput.println(ret);
  }

  private void uilog(String message) {
    uilog(message, 1);
  }

}
