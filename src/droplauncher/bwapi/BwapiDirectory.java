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
import droplauncher.starcraft.Starcraft;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

public class BwapiDirectory {

  private Path path;

  public BwapiDirectory() {
    this.path = Paths.get("");
  }

  public Path getPath() {
    return this.path;
  }

  public void setPath(Path path) {
    if (path == null) {
      throw new IllegalArgumentException(Debugging.cannotBeNull("path"));
    }
    this.path = path;
  }

  public Path getAiPath() {
    return this.path.resolve(BWAPI.AI_PATH);
  }

  public Path getIniPath() {
    return this.path.resolve(BWAPI.INI_PATH);
  }

  public Path getIniBackupPath() {
    return this.path.resolve(BWAPI.INI_BACKUP_PATH);
  }

  public Path getReadPath() {
    return this.path.resolve(BWAPI.READ_PATH);
  }

  public Path getWritePath() {
    return this.path.resolve(BWAPI.WRITE_PATH);
  }

  public Path getDataPath() {
    return this.path.resolve(BWAPI.DATA_PATH);
  }

  public void backupIniFile() throws IOException {
    if (AdakiteUtils.fileExists(getIniPath())) {
      Files.copy(getIniPath(), getIniBackupPath(), StandardCopyOption.REPLACE_EXISTING);
    }
  }

  public void restoreIniFile() throws IOException {
    if (AdakiteUtils.fileExists(getIniBackupPath())) {
      Files.copy(getIniBackupPath(), getIniPath(), StandardCopyOption.REPLACE_EXISTING);
      AdakiteUtils.deleteFile(getIniBackupPath());
    }
  }

  /**
   * Configures BWAPI in the specified StarCraft directory.
   *
   * @param starcraftPath specified Starcraft path to use in configuration
   * @param bot specified bot to use in configuration
   * @throws IOException
   * @throws IniParseException
   * @throws MissingBotFileException
   * @throws InvalidBotTypeException
   * @throws InvalidArgumentException
   * @throws MissingBotNameException
   * @throws MissingBotRaceException
   */
  public void configure(Path starcraftPath, Bot bot) throws IOException,
                                                            IniParseException,
                                                            MissingBotFileException,
                                                            InvalidArgumentException,
                                                            InvalidBotTypeException,
                                                            MissingBotNameException,
                                                            MissingBotRaceException {
    /* Create common BWAPI paths. */
    AdakiteUtils.createDirectory(getPath());
    AdakiteUtils.createDirectory(getAiPath());
    AdakiteUtils.createDirectory(getReadPath());
    AdakiteUtils.createDirectory(getWritePath());
    AdakiteUtils.createDirectory(getDataPath());

    /* Create BWTA/BWTA2 paths. */
    AdakiteUtils.createDirectory(getPath().resolve("BWTA"));
    AdakiteUtils.createDirectory(getPath().resolve("BWTA2"));

    /* Check for bwapi.ini existence. */
    if (!AdakiteUtils.fileExists(getIniPath())) {
      /* If bwapi.ini is not found in the target BWAPI directory, extract it from this program. */
      URL url = DropLauncher.getResource(BWAPI.FILES_RESOURCE_PATH + BWAPI.ExtractableFile.BWAPI_INI.toString());
      FileUtils.copyURLToFile(url, getIniPath().toFile());
    }
    /* Read the bwapi.ini file. */
    Ini bwapiIni = new Ini();
    bwapiIni.parse(getIniPath());

    /* Check for the Broodwar.map file. */
    Path bwapiBroodwarMap = getDataPath().resolve(BWAPI.ExtractableFile.BROODWAR_MAP.toString());
    if (!AdakiteUtils.fileExists(bwapiBroodwarMap)) {
      /* If Broodwar.map is not found in the target BWAPI directory, extract it from this program. */
      URL url = DropLauncher.getResource(BWAPI.FILES_RESOURCE_PATH + BWAPI.ExtractableFile.BROODWAR_MAP.toString());
      FileUtils.copyURLToFile(url, bwapiBroodwarMap.toFile());
    }

    /* Check if dependencies should be extracted to the StarCraft root directory. */
    if (Model.getSettings().isEnabled(Starcraft.PropertyKey.EXTRACT_BOT_DEPENDENCIES.toString())) {
      for (BWAPI.ExtractableDll val : BWAPI.ExtractableDll.values()) {
        /* If dependency is not found in the StarCraft root directory, extract it from this program. */
        Path targetDependency = starcraftPath.resolve(val.toString());
        if (!AdakiteUtils.fileExists(targetDependency)) {
          URL url = DropLauncher.getResource(BWAPI.DLL_RESOURCE_PATH + val.toString());
          FileUtils.copyURLToFile(url, targetDependency.toFile());
        }
      }

      /* Copy BWTA cache files. */ {
        Path bwtaCacheDirectory = getPath().resolve(BWTA.V1_DIRECTORY);
        for (BWTA.CacheV1 val : BWTA.CacheV1.values()) {
          Path targetDependency = bwtaCacheDirectory.resolve(val.toString());
          if (!AdakiteUtils.fileExists(targetDependency)) {
            URL url = DropLauncher.getResource(BWTA.V1_RESOURCE_PATH + val.toString());
            FileUtils.copyURLToFile(url, targetDependency.toFile());
          }
        }
      }

      /* Copy BWTA2 cache files. */ {
        Path bwtaCacheDirectory = getPath().resolve(BWTA.V2_DIRECTORY);
        for (BWTA.CacheV2 val : BWTA.CacheV2.values()) {
          Path targetDependency = bwtaCacheDirectory.resolve(val.toString());
          if (!AdakiteUtils.fileExists(targetDependency)) {
            URL url = DropLauncher.getResource(BWTA.V2_RESOURCE_PATH + val.toString());
            FileUtils.copyURLToFile(url, targetDependency.toFile());
          }
        }
      }
    }

    switch (bot.getType()) {
      case DLL: {
        /* Copy DLL to "bwapi-data/AI/" directory. */
        Path src = bot.getPath();
        Path dest = getAiPath().resolve(FilenameUtils.getName(bot.getPath().toString()));
        AdakiteUtils.createDirectory(dest.getParent());
        Files.copy(src, dest, StandardCopyOption.REPLACE_EXISTING);
        bot.setPath(dest);
        Path iniAiPath = getAiPath().resolve(FilenameUtils.getName(bot.getPath().toString()));
        bwapiIni.set("ai", "ai", iniAiPath.toString());
        break;
      }
      case CLIENT: {
        /* Copy client to StarCraft root directory. */
        Path src = bot.getPath();
        Path dest = starcraftPath.resolve(FilenameUtils.getName(bot.getPath().toString()));
        Files.copy(src, dest, StandardCopyOption.REPLACE_EXISTING);
        bot.setPath(dest);
        bwapiIni.commentVariable("ai", "ai");
        break;
      }
      default: {
        throw new InvalidBotTypeException();
      }
    }

    /* Not tested yet whether it matters if ai_dbg is enabled. Disable anyway. */
    bwapiIni.commentVariable("ai", "ai_dbg");

    /* Set relevant variables. (bwheadless ignores these in headless mode) */
    bwapiIni.set("auto_menu", "auto_menu", "LAN");
    bwapiIni.set("auto_menu", "lan_mode", "Local Area Network (UDP)");
    bwapiIni.set("auto_menu", "character_name", bot.getName());
    bwapiIni.set("auto_menu", "pause_dbg", "OFF");
//    bwapiIni.set("auto_menu", "auto_restart", "OFF");
    bwapiIni.set("auto_menu", "race", bot.getRace());

    /* Update bwapi.ini file. */
    bwapiIni.store(getIniPath());

    /* Copy extra files to common bot I/O directories. */
    for (String path : bot.getExtraFiles()) {
      if (AdakiteUtils.fileExists(Paths.get(path))) {
        Files.copy(Paths.get(path), Paths.get(getAiPath().toString(), FilenameUtils.getName(path)), StandardCopyOption.REPLACE_EXISTING);
      }
    }
  }

}
