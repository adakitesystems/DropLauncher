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

import adakite.util.AdakiteUtils;
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
//  private Button btnResetSettings;

  public SettingsWindow() {
    this.chkShowLogWindow = new CheckBox();
    this.chkBwapiWriteRead = new CheckBox();
    this.chkCleanStarcraftDirectory = new CheckBox();
    this.chkWarnBwapiDll = new CheckBox();
    this.lblChangeStarcraftExe = new Label();
    this.lblChangeStarcraftExeText = new Label();
    this.btnChangeStarcraftExe = new Button();
//    this.btnResetSettings = new Button();
  }

  public SettingsWindow showAndWait() {
    this.chkShowLogWindow.setText("Show log window (requires program restart)");
    this.chkShowLogWindow.setSelected(Model.isPrefEnabled(View.Property.SHOW_LOG_WINDOW.toString()));
    this.chkShowLogWindow.setOnAction(e -> {
      Model.setPrefEnabled(View.Property.SHOW_LOG_WINDOW.toString(), this.chkShowLogWindow.isSelected());
    });

    this.chkBwapiWriteRead.setText("Copy contents of \"bwapi-data/write/\" to \"bwapi-data/read/\" after eject");
    this.chkBwapiWriteRead.setSelected(Model.isPrefEnabled(BWAPI.Property.COPY_WRITE_READ.toString()));
    this.chkBwapiWriteRead.setOnAction(e -> {
      Model.setPrefEnabled(BWAPI.Property.COPY_WRITE_READ.toString(), this.chkBwapiWriteRead.isSelected());
    });

    this.chkCleanStarcraftDirectory.setText("Clean StarCraft directory when closing program");
    this.chkCleanStarcraftDirectory.setSelected(Model.isPrefEnabled(Starcraft.Property.CLEAN_SC_DIR.toString()));
    this.chkCleanStarcraftDirectory.setOnAction(e -> {
      Model.setPrefEnabled(Starcraft.Property.CLEAN_SC_DIR.toString(), this.chkCleanStarcraftDirectory.isSelected());
    });

    this.chkWarnBwapiDll.setText("Warn about unknown BWAPI versions");
    this.chkWarnBwapiDll.setSelected(Model.isPrefEnabled(BWAPI.Property.WARN_UNKNOWN_BWAPI_DLL.toString()));
    this.chkWarnBwapiDll.setOnAction(e -> {
      Model.setPrefEnabled(BWAPI.Property.WARN_UNKNOWN_BWAPI_DLL.toString(), this.chkWarnBwapiDll.isSelected());
    });

    this.lblChangeStarcraftExe.setText("StarCraft.exe:");
    this.btnChangeStarcraftExe.setText("...");
    this.lblChangeStarcraftExeText.setText("");
    if (Model.hasPrefValue(Starcraft.Property.STARCRAFT_EXE.toString())) {
      String starcraftExe = Model.getPref(Starcraft.Property.STARCRAFT_EXE.toString());
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
        Model.setPref(Starcraft.Property.STARCRAFT_EXE.toString(), file.getAbsolutePath());
        this.lblChangeStarcraftExeText.setText(file.getAbsolutePath());
      }
    });

//    this.btnResetSettings.setText("Reset Settings");
//    this.btnResetSettings.setOnAction(e -> {
//      Alert alert = new Alert(AlertType.CONFIRMATION);
//      alert.setTitle("Warning");
//      alert.setContentText("Are you sure you want to reset the program settings?");
//      alert.setHeaderText(null);
//      View.addDefaultStylesheet(alert.getDialogPane().getStylesheets());
//      ButtonType btnNo = new ButtonType("No");
//      ButtonType btnYes = new ButtonType("Yes");
//      alert.getButtonTypes().setAll(btnYes, btnNo);
//      Optional<ButtonType> result = alert.showAndWait();
//      if (result.get() == btnYes) {
//        try {
//          Model.clearPrefs();
//        } catch (BackingStoreException ex) {
//          //TODO: Test if this needs to be wrapped by "runLater".
//          Platform.runLater(() -> {
//            new ExceptionAlert().showAndWait(null, ex);
//          });
//        }
//        new SimpleAlert().showAndWait(
//            AlertType.INFORMATION,
//            DialogTitle.PROGRAM_TITLE,
//            "Program settings have been reset! You will need to restart the program for changes to take effect."
//        );
//      }
//    });
//    HBox resetHBox = new HBox();
//    resetHBox.getChildren().add(this.btnResetSettings);
//    resetHBox.setAlignment(Pos.CENTER_RIGHT);

    CustomGridPane fileSelectPane = new CustomGridPane();
    fileSelectPane.add(this.lblChangeStarcraftExe);
    fileSelectPane.add(this.lblChangeStarcraftExeText);
    fileSelectPane.add(this.btnChangeStarcraftExe, true);
    fileSelectPane.setGaps(View.DefaultSetting.GAP.intValue(), View.DefaultSetting.GAP.intValue());

    CustomGridPane mainGridPane = new CustomGridPane();
    mainGridPane.add(fileSelectPane.get(), true);
    mainGridPane.add(new Separator(), true);
    mainGridPane.add(this.chkCleanStarcraftDirectory, true);
    mainGridPane.add(this.chkBwapiWriteRead, true);
    mainGridPane.add(this.chkWarnBwapiDll, true);
    mainGridPane.add(this.chkShowLogWindow, true);

    /* Disabled for now. There may be some unexpected behavior with respect to
       how the preferences nodes react after being deleted and the user
       tries to enable/disable settings without restarting the program first. */
//    mainGridPane.add(resetHBox, true);

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
