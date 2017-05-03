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
import droplauncher.mvc.controller.Controller;
import droplauncher.mvc.model.Model;
import droplauncher.starcraft.Starcraft.Race;
import droplauncher.util.DropLauncher;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class View implements EventHandler<DragEvent>  {

  public enum Property {

    SHOW_LOG_WINDOW("show_log_window");

    private final String str;

    private Property(String str) {
      this.str = str;
    }

    public String toString() {
      return this.str;
    }

  }

  /**
   * Enum for UI component default settings.
   */
  public enum DefaultSetting {

    PADDING(20),
    TOP_PADDING(PADDING.getValue()),
    BOTTOM_PADDING(PADDING.getValue()),
    LEFT_PADDING(PADDING.getValue()),
    RIGHT_PADDING(PADDING.getValue()),
    GAP(10),
    LABEL_TEXT_SPACING(4)
    ;

    private final int val;

    private DefaultSetting(int val) {
      this.val = val;
    }

    public int getValue() {
      return this.val;
    }

  }

  /**
   * Enum for the launch button text states.
   */
  public enum StartButtonText {

    START("Start"),
    STOP("Stop")
    ;

    private final String str;

    private StartButtonText(String str) {
      this.str = str;
    }

    @Override
    public String toString() {
      return this.str;
    }

  }


  private static final String RESOURCE_PATH = "/droplauncher/mvc/view/theme/";
  public static final String DEFAULT_CSS = RESOURCE_PATH + "droplauncher.css";

  private static final String EMPTY_LABEL_TEXT = "-";

  private Controller controller;

  private Stage stage;
  private Scene scene;

  private MenuBar menuBar;
  private Menu fileMenu;
  private Menu editMenu;
  private Menu helpMenu;

  private Label lblBwapiVersion;
  private Label lblBwapiVersionText;
  private Label lblBotFile;
  private Label lblBotFileText;
  private Label lblBotName;
  private TextField txtBotName;
  private ChoiceBox<String> cbRace;
  private Button btnStart;
  private ConsoleOutput consoleOutput;

  public View() {
    /* Do nothing. */
  }

  public void setController(Controller controller) {
    this.controller = controller;
  }

  private void initMenus() {
    /* File */
    MenuItem mnuFileSelectBotFiles = new MenuItem(MenuText.SELECT_BOT_FILES.toString());
    mnuFileSelectBotFiles.setOnAction(e -> this.controller.mnuFileSelectBotFilesClicked(this.stage));
    MenuItem mnuFileExit = new MenuItem(MenuText.EXIT.toString());
    mnuFileExit.setOnAction(e -> {
      this.controller.mnuFileExitClicked(this.stage);
    });
    this.fileMenu = new Menu(MenuText.FILE.toString());
    this.fileMenu.getItems().add(mnuFileSelectBotFiles);
    this.fileMenu.getItems().add(mnuFileExit);

    /* Edit */
    MenuItem mnuEditSettings = new MenuItem(MenuText.SETTINGS.toString());
    mnuEditSettings.setOnAction(e -> this.controller.mnuEditSettingsClicked());
    this.editMenu = new Menu(MenuText.EDIT.toString());
    this.editMenu.getItems().add(mnuEditSettings);

    /* Help */
    MenuItem mnuHelpAbout = new MenuItem(MenuText.ABOUT.toString());
    mnuHelpAbout.setOnAction(e -> this.controller.mnuHelpAboutClicked());
    this.helpMenu = new Menu(MenuText.HELP.toString());
    this.helpMenu.getItems().add(mnuHelpAbout);

    this.menuBar = new MenuBar();
    this.menuBar.getMenus().add(this.fileMenu);
    this.menuBar.getMenus().add(this.editMenu);
    this.menuBar.getMenus().add(this.helpMenu);
    this.menuBar.getStyleClass().add("dl-menustyle");
  }

  private void initComponents() {
    this.lblBwapiVersion = new Label("BWAPI.dll Version:");
    this.lblBwapiVersion.setMinWidth(Region.USE_PREF_SIZE);
    this.lblBwapiVersionText = new Label(EMPTY_LABEL_TEXT);
    this.lblBwapiVersionText.setMinWidth(Region.USE_PREF_SIZE);
    this.lblBotFile = new Label("Bot File:");
    this.lblBotFile.setMinWidth(Region.USE_PREF_SIZE);
    this.lblBotFileText = new Label(EMPTY_LABEL_TEXT);
    this.lblBotName = new Label("Bot Name (max 24 characters):");
    this.lblBotName.setMinWidth(Region.USE_PREF_SIZE);
    this.txtBotName = new TextField("");
    this.txtBotName.setMinWidth(Region.USE_PREF_SIZE);
    this.cbRace = new ChoiceBox<>();
    this.cbRace.getItems().add(Race.TERRAN.toString());
    this.cbRace.getItems().add(Race.ZERG.toString());
    this.cbRace.getItems().add(Race.PROTOSS.toString());
    this.cbRace.getItems().add(Race.RANDOM.toString());
    this.btnStart = new Button(StartButtonText.START.toString());
    this.btnStart.setMinWidth(250);
    this.btnStart.setMinHeight(30);
//    this.btnStart.setMinWidth(120);
//    this.btnStart.setMinHeight(80);
    this.btnStart.getStyleClass().add("launch-btn");
    this.consoleOutput = new ConsoleOutput();
    this.consoleOutput.getBlacklist().add("fps: "); /* bwheadless.exe spam */
    this.consoleOutput.get().getStyleClass().add("console-output");
    this.consoleOutput.get().setMinWidth(500);
    this.consoleOutput.get().setMinHeight(300);
    this.consoleOutput.get().setEditable(false);

    this.txtBotName.setOnKeyReleased(e -> this.controller.botNameChanged(this.txtBotName.getText()));
    this.cbRace.setOnAction(e -> {
      String previous = this.controller.getBotRace().toString();
      String current = this.cbRace.getValue();
      if (!current.equals(previous)) {
        this.controller.botRaceChanged(current);
      }
    });
    this.btnStart.setOnAction(e -> this.controller.btnStartClicked());

    CustomGridPane fileLabelGridPane = new CustomGridPane();
    fileLabelGridPane.add(this.lblBotFile);
    fileLabelGridPane.add(this.lblBotFileText);
    fileLabelGridPane.add(this.cbRace, true);
    fileLabelGridPane.get().setAlignment(Pos.CENTER_LEFT);
    fileLabelGridPane.setGaps(DefaultSetting.LABEL_TEXT_SPACING.getValue(), 0);
    fileLabelGridPane.get().setMinWidth(Region.USE_PREF_SIZE);
    CustomGridPane bwapiLabelGridPane = new CustomGridPane();
    bwapiLabelGridPane.add(this.lblBwapiVersion);
    bwapiLabelGridPane.add(this.lblBwapiVersionText);
    bwapiLabelGridPane.get().setAlignment(Pos.CENTER_LEFT);
    bwapiLabelGridPane.setGaps(DefaultSetting.LABEL_TEXT_SPACING.getValue(), 0);
    bwapiLabelGridPane.get().setMinWidth(Region.USE_PREF_SIZE);
    CustomGridPane botNameGridPane = new CustomGridPane();
    botNameGridPane.add(this.lblBotName, true);
    botNameGridPane.add(this.txtBotName, true);
    botNameGridPane.get().setAlignment(Pos.CENTER_LEFT);
    botNameGridPane.setGaps(0, DefaultSetting.LABEL_TEXT_SPACING.getValue());
    botNameGridPane.get().setMinWidth(Region.USE_PREF_SIZE);

    CustomGridPane infoGridPane = new CustomGridPane();
    infoGridPane.add(fileLabelGridPane.get(), true);
    infoGridPane.add(bwapiLabelGridPane.get(), true);
    infoGridPane.add(botNameGridPane.get(), true);
    infoGridPane.setGaps(DefaultSetting.GAP.getValue(), DefaultSetting.GAP.getValue());
    infoGridPane.get().setMinWidth(Region.USE_PREF_SIZE);
    infoGridPane.get().setAlignment(Pos.CENTER_LEFT);

    VBox vbox = new VBox();
    vbox.getChildren().add(this.btnStart);
    if (Model.isPrefEnabled(View.Property.SHOW_LOG_WINDOW.toString())) {
      vbox.getChildren().add(this.consoleOutput.get());
    }
    vbox.setSpacing(DefaultSetting.GAP.getValue());
    vbox.setAlignment(Pos.CENTER);
    vbox.setMinWidth(Region.USE_PREF_SIZE);

    HBox hbox = new HBox();
//    hbox.getChildren().add(this.btnStart);
    hbox.getChildren().add(infoGridPane.get());
    hbox.setSpacing(DefaultSetting.GAP.getValue());
    hbox.setAlignment(Pos.CENTER);
    hbox.setMinWidth(Region.USE_PREF_SIZE);

    CustomGridPane mainGridPane = new CustomGridPane();
//    mainGridPane.add(infoGridPane.get(), true);
    mainGridPane.add(hbox, true);
    mainGridPane.add(vbox, true);
    mainGridPane.get().setPadding(new Insets(
        DefaultSetting.TOP_PADDING.getValue(),
        DefaultSetting.RIGHT_PADDING.getValue(),
        DefaultSetting.BOTTOM_PADDING.getValue(),
        DefaultSetting.LEFT_PADDING.getValue()
    ));
    mainGridPane.setGaps(DefaultSetting.GAP.getValue(), DefaultSetting.GAP.getValue());
    mainGridPane.get().setAlignment(Pos.CENTER);
    mainGridPane.get().setMinWidth(Region.USE_PREF_SIZE);

    BorderPane borderPane = new BorderPane();
    borderPane.setTop(this.menuBar);
    borderPane.setCenter(mainGridPane.get());

//    System.setProperty("prism.lcdtext", "false");
//    System.setProperty("prism.text", "t2k");

    this.scene = new Scene(borderPane);
    this.scene.setOnDragOver(this);
    this.scene.setOnDragDropped(this);
    try {
      this.scene.getStylesheets().add(View.class.getResource(DEFAULT_CSS).toString());
    } catch (Exception ex) {
      /* Do nothing. */
    }

    this.stage.setOnCloseRequest(e -> {
      this.controller.closeProgramRequest(this.stage);
      e.consume();
    });
    this.stage.setResizable(false);
    this.stage.setTitle(DropLauncher.PROGRAM_TITLE);
    this.stage.setScene(this.scene);
  }

  public ConsoleOutput getConsoleOutput() {
    return this.consoleOutput;
  }

  public ChoiceBox getRaceChoiceBox() {
    return this.cbRace;
  }

  public void start(Stage primaryStage) {
    this.stage = primaryStage;

    initMenus();
    initComponents();

    this.stage.show();

    update();
  }

  /**
   * Updates the components to display the internal values.
   */
  public void update() {
    setText(this.lblBwapiVersionText, this.controller.getBwapiDllVersion());

    setText(this.lblBotFileText, this.controller.getBotFilename());
    if (!AdakiteUtils.isNullOrEmpty(this.controller.getBotFilename())) {
      this.cbRace.setVisible(true);
      updateRaceChoiceBox();
    } else {
      this.cbRace.setVisible(false);
    }

    /* Handling for when the user enters an invalid character for the bot name. */
    String displayBotName = this.txtBotName.getText();
    String internalBotName = this.controller.getBotName();
    int caret = txtBotName.getCaretPosition();
    if (!AdakiteUtils.isNonNullAndEqual(displayBotName, internalBotName)) {
      if (caret >= internalBotName.length()) {
        caret = internalBotName.length();
      } else if (caret > 1) {
        caret--;
      }
      setText(this.txtBotName, internalBotName);
      txtBotName.positionCaret(caret);
    }

    sizeToScene();
  }

  /**
   * Wrapper for setting the width and height of this Window to match the
   * size of the content of this Window's Scene.
   *
   * @see javafx.stage.Window#sizeToScene()
   */
  public void sizeToScene() {
    this.stage.sizeToScene();
  }

  public void setText(Node node, String str) {
    if (AdakiteUtils.isNullOrEmpty(str)) {
      str = EMPTY_LABEL_TEXT;
    }

    if (node instanceof Button) {
      Button button = (Button) node;
      button.setText(str);
    } else if (node instanceof Label) {
      Label label = (Label) node;
      label.setText(str);
    } else if (node instanceof ChoiceBox) {
      ChoiceBox choiceBox = (ChoiceBox) node;
      //TODO: Use ChoiceBox.getSelectionModel().select(int)
      choiceBox.setValue(str);
    } else if (node instanceof TextField) {
      TextField textField = (TextField) node;
      textField.setText(str);
    }
  }

  public void updateRaceChoiceBox() {
    setText(getRaceChoiceBox(), this.controller.getBotRace().toString());
    sizeToScene();
  }

  public void btnStartSetText(String str) {
    setText(this.btnStart, str);
  }

  public void btnStartEnabled(boolean enabled) {
    this.btnStart.setDisable(!enabled);
  }

  @Override
  public void handle(DragEvent event) {
    if (event.getEventType() == DragEvent.DRAG_OVER) {
      if (event.getDragboard().hasFiles()) {
        event.acceptTransferModes(TransferMode.COPY);
      }
      event.consume();
    } else if (event.getEventType() == DragEvent.DRAG_DROPPED) {
      boolean isTransferDone = false;
      List<File> files = new ArrayList<>();
      Dragboard dragBoard = event.getDragboard();
      if (dragBoard.hasFiles()) {
        isTransferDone = true;
        files = dragBoard.getFiles();
      }
      event.setDropCompleted(isTransferDone);
      event.consume();
      this.controller.filesDropped(files);
    }
  }

}
