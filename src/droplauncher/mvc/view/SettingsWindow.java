package droplauncher.mvc.view;

import adakite.ini.Ini;
import adakite.util.AdakiteUtils;
import droplauncher.bwapi.BWAPI;
import droplauncher.bwheadless.BWHeadless;
import droplauncher.util.Constants;
import droplauncher.util.SettingsKey;
import java.io.File;
import java.nio.file.Paths;
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

public class SettingsWindow {

  private Stage stage;
  private Scene scene;

  private CheckBox chkKeepClientWindow;
  private CheckBox chkBwapiWriteRead;
  private Label lblChangeStarcraftExe;
  private Label lblChangeStarcraftExeText;
  private Button btnChangeStarcraftExe;

  private Ini ini;

  private SettingsWindow() {}

  public SettingsWindow(Ini ini) {
    this.chkKeepClientWindow = new CheckBox();
    this.chkBwapiWriteRead = new CheckBox();
    this.lblChangeStarcraftExe = new Label();
    this.lblChangeStarcraftExeText = new Label();
    this.btnChangeStarcraftExe = new Button();

    this.ini = ini;
  }

  public SettingsWindow showAndWait() {
    this.chkKeepClientWindow.setText("Show log window for executable bot clients (requires program restart)");
    if (this.ini.isEnabled(Constants.DROPLAUNCHER_INI_SECTION_NAME, SettingsKey.SHOW_LOG_WINDOW.toString())) {
      this.chkKeepClientWindow.setSelected(true);
    } else {
      this.chkKeepClientWindow.setSelected(false);
    }
    this.chkKeepClientWindow.setOnAction(e -> {
      this.ini.setEnabled(Constants.DROPLAUNCHER_INI_SECTION_NAME, SettingsKey.SHOW_LOG_WINDOW.toString(), this.chkKeepClientWindow.isSelected());
    });

    this.chkBwapiWriteRead.setText("Copy contents of \"bwapi-data/write/\" to \"bwapi-data/read/\" after eject");
    if (this.ini.isEnabled(BWAPI.DEFAULT_INI_SECTION_NAME, SettingsKey.COPY_WRITE_READ.toString())) {
      this.chkBwapiWriteRead.setSelected(true);
    } else {
      this.chkBwapiWriteRead.setSelected(false);
    }
    this.chkBwapiWriteRead.setOnAction(e -> {
      this.ini.setEnabled(BWAPI.DEFAULT_INI_SECTION_NAME, SettingsKey.COPY_WRITE_READ.toString(), this.chkBwapiWriteRead.isSelected());
    });

    this.lblChangeStarcraftExe.setText("StarCraft.exe:");
    this.btnChangeStarcraftExe.setText("...");
    this.lblChangeStarcraftExeText.setText("");
    String starcraftExe = this.ini.getValue(BWHeadless.DEFAULT_INI_SECTION_NAME, SettingsKey.STARCRAFT_EXE.toString());
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
        this.ini.set(BWHeadless.DEFAULT_INI_SECTION_NAME, SettingsKey.STARCRAFT_EXE.toString(), file.getAbsolutePath());
        this.lblChangeStarcraftExeText.setText(this.ini.getValue(BWHeadless.DEFAULT_INI_SECTION_NAME, SettingsKey.STARCRAFT_EXE.toString()));
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
    mainGridPane.add(this.chkKeepClientWindow, true);
    mainGridPane.add(this.chkBwapiWriteRead, true);
    mainGridPane.setGaps(View.DefaultSetting.GAP.getValue(), View.DefaultSetting.GAP.getValue());
    mainGridPane.get().setPadding(new Insets(
        View.DefaultSetting.TOP_PADDING.getValue(),
        View.DefaultSetting.RIGHT_PADDING.getValue(),
        View.DefaultSetting.BOTTOM_PADDING.getValue(),
        View.DefaultSetting.LEFT_PADDING.getValue()
    ));

    this.scene = new Scene(mainGridPane.get());
    this.scene.getStylesheets().add(View.DEFAULT_CSS);

    this.stage = new Stage();
    this.stage.setTitle("Settings");
    this.stage.initModality(Modality.APPLICATION_MODAL);
    this.stage.setResizable(false);
    this.stage.setScene(this.scene);
    this.stage.showAndWait();

    return this;
  }

}
