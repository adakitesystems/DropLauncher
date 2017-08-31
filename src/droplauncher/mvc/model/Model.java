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

package droplauncher.mvc.model;

import adakite.ini.exception.IniParseException;
import adakite.util.AdakiteUtils;
import droplauncher.bwapi.BWAPI;
import droplauncher.bwheadless.BWHeadless;
import droplauncher.mvc.view.View;
import droplauncher.starcraft.Starcraft;
import droplauncher.DropLauncher;
import droplauncher.mvc.view.ExceptionAlert;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import javafx.application.Platform;

public class Model {

  public static final int AUTO_EJECT_DELAY = 3500; /* milliseconds */
  public static final int AUTO_REJOIN_DELAY = 3500; /* milliseconds */

  private static ProgramSettings SETTINGS = new ProgramSettings();

  private BWHeadless bwheadless;

  public Model() {
    this.bwheadless = new BWHeadless();
  }

  public BWHeadless getBWHeadless() {
    return this.bwheadless;
  }

  public static ProgramSettings getSettings() {
    return SETTINGS;
  }

  public void ensureDefaultSettings() {
    if (AdakiteUtils.fileExists(DropLauncher.SETTINGS_FILE)) {
      try {
        DropLauncher.getSettings().parse(DropLauncher.SETTINGS_FILE);
      } catch (Exception ex) {
        Platform.runLater(() -> {
          new ExceptionAlert().showAndWait("Failed to parse settings file: " + DropLauncher.SETTINGS_FILE.toString(), ex);
        });
      }
    } else {
      try {
        AdakiteUtils.createFile(DropLauncher.SETTINGS_FILE);
      } catch (Exception ex) {
        Platform.runLater(() -> {
          new ExceptionAlert().showAndWait("Failed to create settings file: " + DropLauncher.SETTINGS_FILE.toString(), ex);
        });
      }
    }

    if (!Model.getSettings().hasValue(DropLauncher.PropertyKey.VERSION.toString())) {
      Model.getSettings().setValue(DropLauncher.PropertyKey.VERSION.toString(), DropLauncher.PROGRAM_VERSION);
    } else {
      String version = Model.getSettings().getValue(DropLauncher.PropertyKey.VERSION.toString());
      if (!version.equalsIgnoreCase(DropLauncher.PROGRAM_VERSION)) {
        try {
          Files.copy(DropLauncher.SETTINGS_FILE, Paths.get(DropLauncher.SETTINGS_FILE.toString() + ".bak"), StandardCopyOption.REPLACE_EXISTING);
          Model.getSettings().setValue(DropLauncher.PropertyKey.VERSION.toString(), DropLauncher.PROGRAM_VERSION);
        } catch (Exception ex) {
          Platform.runLater(() -> {
            new ExceptionAlert().showAndWait("Failed to create backup of settings file: " + DropLauncher.SETTINGS_FILE.toString(), ex);
          });
        }
      }
    }
    if (!Model.getSettings().hasValue(Starcraft.PropertyKey.CLEAN_SC_DIR.toString())) {
      Model.getSettings().setEnabled(Starcraft.PropertyKey.CLEAN_SC_DIR.toString(), true);
    }
    if (!Model.getSettings().hasValue(BWAPI.PropertyKey.COPY_WRITE_READ.toString())) {
      Model.getSettings().setEnabled(BWAPI.PropertyKey.COPY_WRITE_READ.toString(), true);
    }
    if (!Model.getSettings().hasValue(BWAPI.PropertyKey.WARN_UNKNOWN_BWAPI_DLL.toString())) {
      Model.getSettings().setEnabled(BWAPI.PropertyKey.WARN_UNKNOWN_BWAPI_DLL.toString(), true);
    }
    if (!Model.getSettings().hasValue(View.PropertyKey.SHOW_LOG_WINDOW.toString())) {
      Model.getSettings().setEnabled(View.PropertyKey.SHOW_LOG_WINDOW.toString(), true);
    }
    /* Disabled for now. Force user to be aware and select which StarCraft directory will be used. */
//    if (!Model.hasPrefValue(Starcraft.Property.STARCRAFT_EXE.toString())) {
//      /* Attempt to determine StarCraft directory from registry. */
//      try {
//        String dir = WinRegistry.strValue(Starcraft.REG_ENTRY_EXE_32BIT, "Program");
//        if (!AdakiteUtils.isNullOrEmpty(dir)) {
//          Model.setPref(Starcraft.Property.STARCRAFT_EXE.toString(), dir);
//        }
//      } catch (Exception ex) {
//        /* Do nothing. */
//      }
//    }
    if (!Model.getSettings().hasValue(DropLauncher.PropertyKey.AUTO_EJECT_BOT.toString())) {
      Model.getSettings().setEnabled(DropLauncher.PropertyKey.AUTO_EJECT_BOT.toString(), true);
    }
    if (!Model.getSettings().hasValue(DropLauncher.PropertyKey.AUTO_BOT_REJOIN.toString())) {
      Model.getSettings().setEnabled(DropLauncher.PropertyKey.AUTO_BOT_REJOIN.toString(), false);
    }
    if (!Model.getSettings().hasValue(Starcraft.PropertyKey.EXTRACT_BOT_DEPENDENCIES.toString())) {
      Model.getSettings().setEnabled(Starcraft.PropertyKey.EXTRACT_BOT_DEPENDENCIES.toString(), true);
    }

    try {
      DropLauncher.getSettings().store(DropLauncher.SETTINGS_FILE);
    } catch (Exception ex) {
      Platform.runLater(() -> {
        new ExceptionAlert().showAndWait("Failed to save settings to local file: " + DropLauncher.SETTINGS_FILE.toString(), ex);
      });
    }
  }

}
