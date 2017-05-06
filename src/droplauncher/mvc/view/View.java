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
import adakite.exception.InvalidStateException;
import adakite.prefs.Prefs;
import adakite.util.AdakiteUtils;
import droplauncher.mvc.controller.Controller;
import droplauncher.mvc.model.Model;
import droplauncher.starcraft.Starcraft.Race;
import droplauncher.util.DropLauncher;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
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

  private static final Logger LOGGER = Logger.getLogger(View.class.getName());

  public enum Property {

    SHOW_LOG_WINDOW("show_log_window");

    private final String str;

    private Property(String str) {
      this.str = str;
    }

    @Override
    public String toString() {
      return this.str;
    }

  }

  /**
   * Enum for UI component default settings.
   */
  public enum DefaultSetting {

    PADDING(20),
    TOP_PADDING(PADDING.intValue()),
    BOTTOM_PADDING(PADDING.intValue()),
    LEFT_PADDING(PADDING.intValue()),
    RIGHT_PADDING(PADDING.intValue()),
    GAP(10),
    LABEL_TEXT_SPACING(4)
    ;

    private final int val;

    private DefaultSetting(int val) {
      this.val = val;
    }

    public int intValue() {
      return this.val;
    }

  }

  /**
   * Enum for the launch button text states.
   */
  public enum StartButtonText {

    START("Start"),
    STOP("Eject")
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

  /**
   * Enum for prepending strings to messages.
   */
  public enum MessagePrefix {

    COPY("copy"),
    KILL("kill"),
    DELETE("delete"),
    BWHEADLESS("bwh"),
    CLIENT("client"),
    DROPLAUNCHER("DropLauncher")
    ;

    private final String str;

    private MessagePrefix(String str) {
      this.str = str;
    }

    /**
     * Returns the string version of this enum with an appended
     * colon character and space.
     *
     * @param str specified message to include
     */
    public String get(String str) {
      String ret = this.str + ": ";
      if (!AdakiteUtils.isNullOrEmpty(str)) {
        ret += str;
      }
      return ret;
    }

    public String get() {
      return get(null);
    }

    @Override
    public String toString() {
      return this.str;
    }

  }

  public enum MenuText {

    FILE("File"),
      SELECT_BOT_FILES("Select bot files..."),
      EXIT("Exit"),

    EDIT("Edit"),
      SETTINGS("Settings..."),

    HELP("Help"),
      ABOUT("About")
    ;

    private final String str;

    private MenuText(String str) {
      this.str = str;
    }

    @Override
    public String toString() {
      return this.str;
    }

  }

  public static final Prefs PREF_ROOT = DropLauncher.PREF_ROOT.getChild("ui");

  private static final String RESOURCE_PATH = "/droplauncher/mvc/view/theme/";
  private static final String DEFAULT_CSS = RESOURCE_PATH + "droplauncher.css";

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

    /* Compile menus. */
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
    this.btnStart.setOnAction(e -> {
      try {
        this.controller.btnStartClicked();
      } catch (InvalidStateException ex) {
        new ExceptionAlert().showAndWait(null, ex);
      }
    });

    CustomGridPane fileLabelGridPane = new CustomGridPane();
    fileLabelGridPane.add(this.lblBotFile);
    fileLabelGridPane.add(this.lblBotFileText);
    fileLabelGridPane.add(this.cbRace, true);
    fileLabelGridPane.get().setAlignment(Pos.CENTER_LEFT);
    fileLabelGridPane.setGaps(DefaultSetting.LABEL_TEXT_SPACING.intValue(), 0);
    fileLabelGridPane.get().setMinWidth(Region.USE_PREF_SIZE);
    CustomGridPane bwapiLabelGridPane = new CustomGridPane();
    bwapiLabelGridPane.add(this.lblBwapiVersion);
    bwapiLabelGridPane.add(this.lblBwapiVersionText);
    bwapiLabelGridPane.get().setAlignment(Pos.CENTER_LEFT);
    bwapiLabelGridPane.setGaps(DefaultSetting.LABEL_TEXT_SPACING.intValue(), 0);
    bwapiLabelGridPane.get().setMinWidth(Region.USE_PREF_SIZE);
    CustomGridPane botNameGridPane = new CustomGridPane();
    botNameGridPane.add(this.lblBotName, true);
    botNameGridPane.add(this.txtBotName, true);
    botNameGridPane.get().setAlignment(Pos.CENTER_LEFT);
    botNameGridPane.setGaps(0, DefaultSetting.LABEL_TEXT_SPACING.intValue());
    botNameGridPane.get().setMinWidth(Region.USE_PREF_SIZE);

    CustomGridPane infoGridPane = new CustomGridPane();
    infoGridPane.add(fileLabelGridPane.get(), true);
    infoGridPane.add(bwapiLabelGridPane.get(), true);
    infoGridPane.add(botNameGridPane.get(), true);
    infoGridPane.setGaps(DefaultSetting.GAP.intValue(), DefaultSetting.GAP.intValue());
    infoGridPane.get().setMinWidth(Region.USE_PREF_SIZE);
    infoGridPane.get().setAlignment(Pos.CENTER_LEFT);

    VBox vbox = new VBox();
    vbox.getChildren().add(this.btnStart);
    if (Model.isPrefEnabled(View.Property.SHOW_LOG_WINDOW.toString())) {
      vbox.getChildren().add(this.consoleOutput.get());
    }
    vbox.setSpacing(DefaultSetting.GAP.intValue());
    vbox.setAlignment(Pos.CENTER);
    vbox.setMinWidth(Region.USE_PREF_SIZE);

    HBox hbox = new HBox();
//    hbox.getChildren().add(this.btnStart);
    hbox.getChildren().add(infoGridPane.get());
    hbox.setSpacing(DefaultSetting.GAP.intValue());
    hbox.setAlignment(Pos.CENTER);
    hbox.setMinWidth(Region.USE_PREF_SIZE);

    CustomGridPane mainGridPane = new CustomGridPane();
//    mainGridPane.add(infoGridPane.get(), true);
    mainGridPane.add(hbox, true);
    mainGridPane.add(vbox, true);
    mainGridPane.get().setPadding(new Insets(
        DefaultSetting.TOP_PADDING.intValue(),
        DefaultSetting.RIGHT_PADDING.intValue(),
        DefaultSetting.BOTTOM_PADDING.intValue(),
        DefaultSetting.LEFT_PADDING.intValue()
    ));
    mainGridPane.setGaps(DefaultSetting.GAP.intValue(), DefaultSetting.GAP.intValue());
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
    View.addDefaultStylesheet(this.scene.getStylesheets());

    this.stage.setOnCloseRequest(e -> {
      e.consume();
      try {
        this.controller.closeProgramRequest(this.stage);
      } catch (Exception ex) {
        new ExceptionAlert().showAndWait(null, ex);
      }
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

//    /* Handling for when the user enters an invalid character for the bot name. */
//    String displayBotName = this.txtBotName.getText();
//    String internalBotName = this.controller.getBotName();
//    int caret = this.txtBotName.getCaretPosition();
//    if (!AdakiteUtils.isNonNullAndEqual(displayBotName, internalBotName)) {
//      if (caret >= internalBotName.length()) {
//        caret = internalBotName.length();
//      } else if (caret > 1) {
//        caret--;
//      }
//      setText(this.txtBotName, internalBotName);
//      this.txtBotName.positionCaret(caret);
//    }
    setText(this.txtBotName, this.controller.getBotName());

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
      try {
        this.controller.filesDropped(files);
      } catch (Exception ex) {
        new ExceptionAlert().showAndWait(null, ex);
      }
    }
  }

  public static void addDefaultStylesheet(ObservableList<String> sheets) {
    try {
      sheets.add(DEFAULT_CSS);
    } catch (Exception ex) {
      LOGGER.log(Debugging.getLogLevel(), null, ex);
    }
  }

  public static void displayMissingFieldDialog(String field) {
    new SimpleAlert().showAndWait(Alert.AlertType.WARNING, "Missing Field", AdakiteUtils.formatAsSentence(field + " is not set"));
  }

}
