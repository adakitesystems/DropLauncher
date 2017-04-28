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

package droplauncher.mvc.view;

import adakite.debugging.Debugging;
import adakite.ini.Ini;
import adakite.util.AdakiteUtils;
import droplauncher.bwapi.BWAPI;
import droplauncher.bwheadless.BWHeadless;
import droplauncher.util.DropLauncher;
import java.io.File;
import java.nio.file.Paths;
import java.util.logging.Logger;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.Region;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * Class for the specific main settings popup window.
 */
public class SettingsWindow {

  private static final Logger LOGGER = Logger.getLogger(SettingsWindow.class.getName());

  private Stage stage;
  private Scene scene;

  private CheckBox chkKeepClientWindow;
  private CheckBox chkBwapiWriteRead;
  private CheckBox chkCleanStarcraftDirectory;
  private Label lblChangeStarcraftExe;
  private Label lblChangeStarcraftExeText;
  private Button btnChangeStarcraftExe;

  private Ini ini;

  private SettingsWindow() {}

  public SettingsWindow(Ini ini) {
    this.chkKeepClientWindow = new CheckBox();
    this.chkBwapiWriteRead = new CheckBox();
    this.chkCleanStarcraftDirectory = new CheckBox();
    this.lblChangeStarcraftExe = new Label();
    this.lblChangeStarcraftExeText = new Label();
    this.btnChangeStarcraftExe = new Button();

    this.ini = ini;
  }

  public SettingsWindow showAndWait() {
    this.chkKeepClientWindow.setText("Show log window for executable bot clients (requires program restart)");
    if (this.ini.isEnabled(DropLauncher.DROPLAUNCHER_INI_SECTION_NAME, View.Property.SHOW_LOG_WINDOW.toString())) {
      this.chkKeepClientWindow.setSelected(true);
    } else {
      this.chkKeepClientWindow.setSelected(false);
    }
    this.chkKeepClientWindow.setOnAction(e -> {
      try {
        this.ini.setEnabled(DropLauncher.DROPLAUNCHER_INI_SECTION_NAME, View.Property.SHOW_LOG_WINDOW.toString(), this.chkKeepClientWindow.isSelected());
      } catch (Exception ex) {
        LOGGER.log(Debugging.getLogLevel(), null, ex);
      }
    });

    this.chkBwapiWriteRead.setText("Copy contents of \"bwapi-data/write/\" to \"bwapi-data/read/\" after eject");
    if (this.ini.isEnabled(BWAPI.DEFAULT_INI_SECTION_NAME, BWAPI.Property.COPY_WRITE_READ.toString())) {
      this.chkBwapiWriteRead.setSelected(true);
    } else {
      this.chkBwapiWriteRead.setSelected(false);
    }
    this.chkBwapiWriteRead.setOnAction(e -> {
      try {
        this.ini.setEnabled(BWAPI.DEFAULT_INI_SECTION_NAME, BWAPI.Property.COPY_WRITE_READ.toString(), this.chkBwapiWriteRead.isSelected());
      } catch (Exception ex) {
        LOGGER.log(Debugging.getLogLevel(), null, ex);
      }
    });

    this.chkCleanStarcraftDirectory.setText("Clean StarCraft directory before closing program");
    if (this.ini.isEnabled(DropLauncher.DROPLAUNCHER_INI_SECTION_NAME, BWHeadless.Property.CLEAN_SC_DIR.toString())) {
      this.chkCleanStarcraftDirectory.setSelected(true);
    } else {
      this.chkCleanStarcraftDirectory.setSelected(false);
    }
    this.chkCleanStarcraftDirectory.setOnAction(e -> {
      try {
        this.ini.setEnabled(DropLauncher.DROPLAUNCHER_INI_SECTION_NAME, BWHeadless.Property.CLEAN_SC_DIR.toString(), this.chkCleanStarcraftDirectory.isSelected());
      } catch (Exception ex) {
        LOGGER.log(Debugging.getLogLevel(), null, ex);
      }
    });

    this.lblChangeStarcraftExe.setText("StarCraft.exe:");
    this.btnChangeStarcraftExe.setText("...");
    this.lblChangeStarcraftExeText.setText("");
    String starcraftExe = this.ini.getValue(BWHeadless.DEFAULT_INI_SECTION_NAME, BWHeadless.Property.STARCRAFT_EXE.toString());
    if (!AdakiteUtils.isNullOrEmpty(starcraftExe)
        && AdakiteUtils.fileExists(Paths.get(starcraftExe))) {
      this.lblChangeStarcraftExeText.setText(starcraftExe);
    }
    this.lblChangeStarcraftExeText.setMinWidth(Region.USE_PREF_SIZE);
    this.btnChangeStarcraftExe.setOnAction(e -> {
      FileChooser fc = new FileChooser();
      fc.getExtensionFilters().add(new ExtensionFilter("StarCraft.exe", "StarCraft.exe"));
      fc.setTitle("Select StarCraft.exe ...");
      String userDirectory = AdakiteUtils.getUserHomeDirectory().toAbsolutePath().toString();
      if (userDirectory != null) {
        fc.setInitialDirectory(new File(userDirectory));
      }
      File file = fc.showOpenDialog(this.stage);
      if (file != null) {
        this.ini.set(BWHeadless.DEFAULT_INI_SECTION_NAME, BWHeadless.Property.STARCRAFT_EXE.toString(), file.getAbsolutePath());
        this.lblChangeStarcraftExeText.setText(this.ini.getValue(BWHeadless.DEFAULT_INI_SECTION_NAME, BWHeadless.Property.STARCRAFT_EXE.toString()));
      }
    });

    CustomGridPane fileSelectPane = new CustomGridPane();
    fileSelectPane.add(this.lblChangeStarcraftExe);
    fileSelectPane.add(this.lblChangeStarcraftExeText);
    fileSelectPane.add(this.btnChangeStarcraftExe, true);
    fileSelectPane.setGaps(View.DefaultSetting.GAP.getValue(), View.DefaultSetting.GAP.getValue());

    CustomGridPane mainGridPane = new CustomGridPane();
    mainGridPane.add(fileSelectPane.get(), true);
    mainGridPane.add(new Separator(), true);
    mainGridPane.add(this.chkCleanStarcraftDirectory, true);
    mainGridPane.add(this.chkBwapiWriteRead, true);
    mainGridPane.add(this.chkKeepClientWindow, true);
    mainGridPane.setGaps(View.DefaultSetting.GAP.getValue(), View.DefaultSetting.GAP.getValue());
    mainGridPane.get().setPadding(new Insets(
        View.DefaultSetting.TOP_PADDING.getValue(),
        View.DefaultSetting.RIGHT_PADDING.getValue(),
        View.DefaultSetting.BOTTOM_PADDING.getValue(),
        View.DefaultSetting.LEFT_PADDING.getValue()
    ));

    this.scene = new Scene(mainGridPane.get());
    try {
      this.scene.getStylesheets().add(View.DEFAULT_CSS);
    } catch (Exception ex) {
      LOGGER.log(Debugging.getLogLevel(), null, ex);
    }

    this.stage = new Stage();
    this.stage.setTitle("Settings");
    this.stage.initModality(Modality.APPLICATION_MODAL);
    this.stage.setResizable(false);
    this.stage.setScene(this.scene);
    this.stage.showAndWait();

    return this;
  }

}
