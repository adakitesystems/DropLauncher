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

import adakite.windows.Windows;
import droplauncher.DropLauncher;
import droplauncher.bwapi.BWAPI;
import droplauncher.mvc.model.Model;
import droplauncher.starcraft.Starcraft;
import java.io.File;
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

  private Stage stage;
  private Scene scene;

  private CheckBox chkShowLogWindow;
  private CheckBox chkBwapiWriteRead;
  private CheckBox chkCleanStarcraftDirectory;
  private CheckBox chkWarnBwapiDll;
  private Label lblChangeStarcraftExe;
  private Label lblChangeStarcraftExeText;
  private Button btnChangeStarcraftExe;
  private CheckBox chkExtractBotDependencies;
  private CheckBox chkEnableColorTheme;

  public SettingsWindow() {
    this.chkShowLogWindow = new CheckBox();
    this.chkBwapiWriteRead = new CheckBox();
    this.chkCleanStarcraftDirectory = new CheckBox();
    this.chkWarnBwapiDll = new CheckBox();
    this.lblChangeStarcraftExe = new Label();
    this.lblChangeStarcraftExeText = new Label();
    this.btnChangeStarcraftExe = new Button();
    this.chkExtractBotDependencies = new CheckBox();
    this.chkEnableColorTheme = new CheckBox();
  }

  public SettingsWindow showAndWait() {
    this.chkShowLogWindow.setText("Show log window (requires program restart)");
    this.chkShowLogWindow.setSelected(Model.getSettings().isEnabled(View.PropertyKey.SHOW_LOG_WINDOW.toString()));
    this.chkShowLogWindow.setOnAction(e -> {
      Model.getSettings().setEnabled(View.PropertyKey.SHOW_LOG_WINDOW.toString(), this.chkShowLogWindow.isSelected());
    });

    this.chkBwapiWriteRead.setText("Copy contents of \"bwapi-data/write/\" to \"bwapi-data/read/\" after eject");
    this.chkBwapiWriteRead.setSelected(Model.getSettings().isEnabled(BWAPI.PropertyKey.COPY_WRITE_READ.toString()));
    this.chkBwapiWriteRead.setOnAction(e -> {
      Model.getSettings().setEnabled(BWAPI.PropertyKey.COPY_WRITE_READ.toString(), this.chkBwapiWriteRead.isSelected());
    });

    this.chkCleanStarcraftDirectory.setText("Clean StarCraft directory before closing program");
    this.chkCleanStarcraftDirectory.setSelected(Model.getSettings().isEnabled(Starcraft.PropertyKey.CLEAN_SC_DIR.toString()));
    this.chkCleanStarcraftDirectory.setOnAction(e -> {
      Model.getSettings().setEnabled(Starcraft.PropertyKey.CLEAN_SC_DIR.toString(), this.chkCleanStarcraftDirectory.isSelected());
    });

    this.chkWarnBwapiDll.setText("Warn about unknown BWAPI versions");
    this.chkWarnBwapiDll.setSelected(Model.getSettings().isEnabled(BWAPI.PropertyKey.WARN_UNKNOWN_BWAPI_DLL.toString()));
    this.chkWarnBwapiDll.setOnAction(e -> {
      Model.getSettings().setEnabled(BWAPI.PropertyKey.WARN_UNKNOWN_BWAPI_DLL.toString(), this.chkWarnBwapiDll.isSelected());
    });

    this.lblChangeStarcraftExe.setText(Starcraft.EXE_FILENAME + ":");
    this.btnChangeStarcraftExe.setText("...");
    this.lblChangeStarcraftExeText.setText("");
    if (Model.getSettings().hasValue(Starcraft.PropertyKey.STARCRAFT_EXE.toString())) {
      String starcraftExe = Model.getSettings().getValue(Starcraft.PropertyKey.STARCRAFT_EXE.toString());
      this.lblChangeStarcraftExeText.setText(starcraftExe);
    }
    this.lblChangeStarcraftExeText.getStyleClass().add("highlighted-text");
    this.lblChangeStarcraftExeText.setMinWidth(Region.USE_PREF_SIZE);
    this.btnChangeStarcraftExe.setOnAction(e -> {
      FileChooser fc = new FileChooser();
      fc.getExtensionFilters().add(new ExtensionFilter(Starcraft.EXE_FILENAME, Starcraft.EXE_FILENAME));
      fc.setTitle("Select " + Starcraft.EXE_FILENAME + " ...");
      String userDirectory = Windows.getUserDesktopDirectory().toAbsolutePath().toString();
      if (userDirectory != null) {
        fc.setInitialDirectory(new File(userDirectory));
      }
      File file = fc.showOpenDialog(this.stage);
      if (file != null) {
        Model.getSettings().setValue(Starcraft.PropertyKey.STARCRAFT_EXE.toString(), file.getAbsolutePath());
        this.lblChangeStarcraftExeText.setText(file.getAbsolutePath());
      }
    });

    this.chkExtractBotDependencies.setText("Auto-extract bot dependencies");
    this.chkExtractBotDependencies.setSelected(Model.getSettings().isEnabled(Starcraft.PropertyKey.EXTRACT_BOT_DEPENDENCIES.toString()));
    this.chkExtractBotDependencies.setOnAction(e -> {
      Model.getSettings().setEnabled(Starcraft.PropertyKey.EXTRACT_BOT_DEPENDENCIES.toString(), this.chkExtractBotDependencies.isSelected());
    });

    this.chkEnableColorTheme.setText("Enable color theme (requires program restart)");
    this.chkEnableColorTheme.setSelected(Model.getSettings().isEnabled(DropLauncher.PropertyKey.USE_DROPLAUNCHER_THEME.toString()));
    this.chkEnableColorTheme.setOnAction(e -> {
      Model.getSettings().setEnabled(DropLauncher.PropertyKey.USE_DROPLAUNCHER_THEME.toString(), this.chkEnableColorTheme.isSelected());
    });

    CustomGridPane fileSelectPane = new CustomGridPane();
    fileSelectPane.add(this.lblChangeStarcraftExe);
    fileSelectPane.add(this.lblChangeStarcraftExeText);
    fileSelectPane.add(this.btnChangeStarcraftExe).nextRow();
    fileSelectPane.setGaps(View.DefaultSetting.GAP.intValue(), View.DefaultSetting.GAP.intValue());

    CustomGridPane mainGridPane = new CustomGridPane();
    mainGridPane.add(fileSelectPane.get()).nextRow();
    mainGridPane.add(new Separator()).nextRow();
    mainGridPane.add(this.chkExtractBotDependencies).nextRow();
    mainGridPane.add(this.chkCleanStarcraftDirectory).nextRow();
    mainGridPane.add(this.chkBwapiWriteRead).nextRow();
    mainGridPane.add(this.chkEnableColorTheme).nextRow();
    mainGridPane.add(this.chkShowLogWindow).nextRow();
    mainGridPane.add(this.chkWarnBwapiDll).nextRow();

    mainGridPane.setGaps(View.DefaultSetting.GAP.intValue(), View.DefaultSetting.GAP.intValue());
    mainGridPane.get().setPadding(new Insets(
        View.DefaultSetting.TOP_PADDING.intValue(),
        View.DefaultSetting.RIGHT_PADDING.intValue(),
        View.DefaultSetting.BOTTOM_PADDING.intValue(),
        View.DefaultSetting.LEFT_PADDING.intValue()
    ));

    this.scene = new Scene(mainGridPane.get());
    View.addDefaultStylesheet(this.scene.getStylesheets());

    this.stage = new Stage();
    this.stage.setTitle("Settings");
    this.stage.initModality(Modality.APPLICATION_MODAL);
    this.stage.setResizable(false);
    this.stage.setScene(this.scene);
    this.stage.showAndWait();

    return this;
  }

}
