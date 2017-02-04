package droplauncher.mvc.view;

import droplauncher.util.Util;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class SettingsWindow {

  private static final int PADDING = 20;
  private static final int TOP_PADDING = PADDING;
  private static final int BOTTOM_PADDING = PADDING;
  private static final int LEFT_PADDING = PADDING;
  private static final int RIGHT_PADDING = PADDING;
  private static final int GAP = 10;

  private Stage stage;
  private Scene scene;

  private CheckBox chkKeepClientWindow;
  private Label lblChangeStarcraftExe;
  private Label lblChangeStarcraftExeText;
  private Button btnChangeStarcraftExe;
  private Label lblChangeJavaExe;
  private Label lblChangeJavaExeText;
  private Button btnChangeJavaExe;

  private String starcraftExe;
  private String javaExe;

  private SettingsWindow() {}

  public SettingsWindow(String starcraft, String java) {
    this.starcraftExe = starcraft;
    this.javaExe = java;

    this.chkKeepClientWindow = new CheckBox("Show log window for bot clients.");

    this.lblChangeStarcraftExe = new Label("StarCraft.exe:");
    this.btnChangeStarcraftExe = new Button("...");
    this.lblChangeStarcraftExeText = new Label(this.starcraftExe);

    this.lblChangeJavaExe = new Label("Java.exe:");
    this.btnChangeJavaExe = new Button("...");
    this.lblChangeJavaExeText = new Label(this.javaExe);
  }

  public String getStarcraftPath() {
    return this.starcraftExe;
  }

  public String getJavaPath() {
    return this.javaExe;
  }

  public SettingsWindow showAndWait() {
    this.btnChangeStarcraftExe.setOnAction(e -> {
      FileChooser fc = new FileChooser();
      fc.getExtensionFilters().add(new ExtensionFilter("StarCraft.exe", "StarCraft.exe"));
      fc.setTitle("Select StarCraft.exe ...");
      String userDirectory = Util.getUserHomeDirectory();
      if (userDirectory != null) {
        fc.setInitialDirectory(new File(userDirectory));
      }
      File file = fc.showOpenDialog(this.stage);
      if (file != null) {
        this.starcraftExe = file.getAbsolutePath();
        this.lblChangeStarcraftExeText.setText(this.starcraftExe);
      }
    });
    this.btnChangeJavaExe.setOnAction(e -> {
      FileChooser fc = new FileChooser();
      fc.getExtensionFilters().add(new ExtensionFilter("java.exe", "java.exe"));
      fc.setTitle("Select java.exe ...");
      String userDirectory = Util.getUserHomeDirectory();
      if (userDirectory != null) {
        fc.setInitialDirectory(new File(userDirectory));
      }
      File file = fc.showOpenDialog(this.stage);
      if (file != null) {
        this.javaExe = file.getAbsolutePath();
        this.lblChangeJavaExeText.setText(this.javaExe);
      }
    });

    CustomGridPane fileSelectPane = new CustomGridPane();
    fileSelectPane.add(this.lblChangeStarcraftExe);
    fileSelectPane.add(this.lblChangeStarcraftExeText);
    fileSelectPane.add(this.btnChangeStarcraftExe, true);
    fileSelectPane.add(this.lblChangeJavaExe);
    fileSelectPane.add(this.lblChangeJavaExeText);
    fileSelectPane.add(this.btnChangeJavaExe, true);
    fileSelectPane.setGaps(GAP, GAP);
    fileSelectPane.pack();

    CustomGridPane mainGridPane = new CustomGridPane();
    mainGridPane.add(fileSelectPane.get(), true);
    mainGridPane.add(new Separator(), true);
    mainGridPane.add(this.chkKeepClientWindow, true);
    mainGridPane.setGaps(GAP, GAP);
    mainGridPane.get().setPadding(new Insets(TOP_PADDING, LEFT_PADDING, BOTTOM_PADDING, RIGHT_PADDING));
    mainGridPane.pack();

    this.scene = new Scene(mainGridPane.get());

    this.stage = new Stage();
    this.stage.setTitle("Settings");
    this.stage.initModality(Modality.APPLICATION_MODAL);
    this.stage.setResizable(false);
    this.stage.setScene(this.scene);
    this.stage.showAndWait();

    return this;
  }

}
