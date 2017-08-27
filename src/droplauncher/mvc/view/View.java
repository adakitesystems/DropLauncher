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
import droplauncher.DropLauncher;
import droplauncher.bwapi.BWAPI;
import droplauncher.mvc.controller.ControllerWrapper;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
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
import org.apache.commons.io.FilenameUtils;

public class View implements EventHandler<DragEvent>  {

  private static final Logger LOGGER = Logger.getLogger(View.class.getName());

  public enum PropertyKey {

    /**
     * Whether to display the log window.
     */
    SHOW_LOG_WINDOW("show_log_window");

    private final String str;

    private PropertyKey(String str) {
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

  public enum DialogTitle {

    PROGRAM_NAME(DropLauncher.PROGRAM_NAME),
    OPERATION_PROHIBITED("Operation prohibited"),
    MISSING_FIELD("Missing field"),
    WARNING("Warning"),
    ERROR_HAS_OCCURRED("An error has occurred")
    ;

    private final String str;

    private DialogTitle(String str) {
      this.str = str;
    }

    @Override
    public String toString() {
      return this.str;
    }

  }

  /**
   * Enum for generic button text.
   */
  public enum ButtonText {

    CLEAR("Clear"),
    CLEAR_EXTRA_BOT_FILES("Unload Config Files")
    ;

    private final String str;

    private ButtonText(String str) {
      this.str = str;
    }

    @Override
    public String toString() {
      return this.str;
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

    COPY("Copy"),
    KILL("Kill"),
    DELETE("Delete"),
    BWHEADLESS("bwheadless.exe"),
    BOT("bot"),
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

  public enum Message {

    GAME_HAS_ENDED("game has ended"),
    GAME_OVER("game over"),
    ERROR_126("error 126"),
    ERROR_740("error 740"),
    BOT_EJECTED("Bot has been ejected.")
    ;

    private final String str;

    private Message(String str) {
      this.str = str;
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
  private Button btnClearConsoleOutput;
  private CheckBox chkAutoEject;
  private CheckBox chkAutoRejoin;
  private Button btnClearExtraBotFiles;

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
    this.lblBwapiVersion = new Label(BWAPI.DEFAULT_DLL_FILENAME_RELEASE + " Version:");
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
    this.btnStart.setMinHeight(45); //30
    this.btnStart.getStyleClass().add("launch-btn");
    this.chkAutoEject = new CheckBox();
    this.chkAutoEject.setText("Auto-eject bot after game has ended");
    this.chkAutoEject.setSelected(Model.isPrefEnabled(DropLauncher.PropertyKey.AUTO_EJECT_BOT.toString()));
    this.chkAutoEject.setOnAction(e -> {
      Model.setPrefEnabled(DropLauncher.PropertyKey.AUTO_EJECT_BOT.toString(), this.chkAutoEject.isSelected());
    });
    this.chkAutoRejoin = new CheckBox();
    this.chkAutoRejoin.setText("Auto-connect bot to game lobby after eject");
    this.chkAutoRejoin.setSelected(Model.isPrefEnabled(DropLauncher.PropertyKey.AUTO_BOT_REJOIN.toString()));
    this.chkAutoRejoin.setOnAction(e -> {
      Model.setPrefEnabled(DropLauncher.PropertyKey.AUTO_BOT_REJOIN.toString(), this.chkAutoRejoin.isSelected());
    });
    this.consoleOutput = new ConsoleOutput();
    this.consoleOutput.getBlacklist().add("fps: "); /* bwheadless.exe spam */
    this.consoleOutput.get().getStyleClass().add("console-output");
    this.consoleOutput.get().setMinWidth(475); //500
    this.consoleOutput.get().setMinHeight(200); //300
    this.consoleOutput.get().setEditable(false);
    this.consoleOutput.setController(new ControllerWrapper(this.controller));
    this.btnClearConsoleOutput = new Button(ButtonText.CLEAR.toString());
    this.btnClearConsoleOutput.setOnAction(e -> {
      Platform.runLater(() -> {
        if (new YesNoDialog().userConfirms("Confirmation", "Are you sure you want to clear the console output? You will not be able to retrieve it.")) {
          this.consoleOutput.clear();
        }
      });
    });
//    this.btnClearConsoleOutput.getStyleClass().add("launch-btn");

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

    this.btnClearExtraBotFiles = new Button(ButtonText.CLEAR_EXTRA_BOT_FILES.toString());
    this.btnClearExtraBotFiles.setOnAction(e -> {
      Platform.runLater(() -> {
        int fileCount = this.controller.getExtraBotFiles().size();
        StringBuilder fileList = new StringBuilder();
        for (String extra : this.controller.getExtraBotFiles()) {
          fileList.append(FilenameUtils.getName(extra)).append(AdakiteUtils.newline());
        }
        if (fileCount > 0) {
          if (new YesNoDialog().userConfirms("Confirmation",
              "Total bot config files loaded: " + fileCount + AdakiteUtils.newline(2)
                  + "Are you sure you want to unload the following bot config files?" + AdakiteUtils.newline(2)
                  + fileList.toString())) {
            this.controller.clearExtraBotFiles();
          }
        } else {
          new SimpleAlert().showAndWait(AlertType.INFORMATION, DialogTitle.PROGRAM_NAME, "Total bot config files loaded: " + fileCount);
        }
      });
    });
//    this.btnClearExtraBotFiles.getStyleClass().add("launch-btn");

    CustomGridPane fileLabelGridPane = new CustomGridPane();
    fileLabelGridPane.add(this.lblBotFile);
    fileLabelGridPane.add(this.lblBotFileText);
    fileLabelGridPane.add(this.cbRace).nextRow();
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
    botNameGridPane.add(this.lblBotName).nextRow();
    botNameGridPane.add(this.txtBotName).nextRow();
    botNameGridPane.add(this.chkAutoEject).nextRow();
    botNameGridPane.add(this.chkAutoRejoin).nextRow();
    botNameGridPane.get().setAlignment(Pos.CENTER_LEFT);
    botNameGridPane.setGaps(0, DefaultSetting.LABEL_TEXT_SPACING.intValue());
    botNameGridPane.get().setMinWidth(Region.USE_PREF_SIZE);

    CustomGridPane infoGridPane = new CustomGridPane();
    infoGridPane.add(fileLabelGridPane.get()).nextRow();
    infoGridPane.add(bwapiLabelGridPane.get()).nextRow();
    infoGridPane.add(botNameGridPane.get()).nextRow();
    infoGridPane.setGaps(DefaultSetting.GAP.intValue(), DefaultSetting.GAP.intValue());
    infoGridPane.get().setMinWidth(Region.USE_PREF_SIZE);
    infoGridPane.get().setAlignment(Pos.CENTER_LEFT);

//    HBox boxClearExtra = new HBox();
//    boxClearExtra.getChildren().add(this.btnClearExtraBotFiles);
//    boxClearExtra.setAlignment(Pos.CENTER_RIGHT);
//    HBox boxClearConsole = new HBox();
//    boxClearConsole.getChildren().add(this.btnClearConsoleOutput);
//    boxClearConsole.setAlignment(Pos.CENTER_RIGHT);
    HBox boxClear = new HBox();
    boxClear.getChildren().add(this.btnClearExtraBotFiles);
    boxClear.getChildren().add(this.btnClearConsoleOutput);
    boxClear.setSpacing(30);
    boxClear.setAlignment(Pos.CENTER_RIGHT);
    VBox boxStartConsole = new VBox();
    boxStartConsole.getChildren().add(this.btnStart);
    if (Model.isPrefEnabled(View.PropertyKey.SHOW_LOG_WINDOW.toString())) {
      boxStartConsole.getChildren().add(this.consoleOutput.get());
      boxStartConsole.getChildren().add(boxClear);
    }
    boxStartConsole.setSpacing(DefaultSetting.GAP.intValue());
    boxStartConsole.setAlignment(Pos.CENTER);
    boxStartConsole.setMinWidth(Region.USE_PREF_SIZE);

    HBox boxInfo = new HBox();
    boxInfo.getChildren().add(infoGridPane.get());
    boxInfo.setSpacing(DefaultSetting.GAP.intValue());
    boxInfo.setAlignment(Pos.CENTER);
    boxInfo.setMinWidth(Region.USE_PREF_SIZE);

    CustomGridPane mainGridPane = new CustomGridPane();
    mainGridPane.add(boxInfo).nextRow();
    mainGridPane.add(boxStartConsole).nextRow();
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
    new SimpleAlert().showAndWait(AlertType.WARNING, DialogTitle.MISSING_FIELD, AdakiteUtils.formatAsSentence(field + " is not set", Locale.US));
  }

  public static void displayWarningDialog(DialogTitle title, String message) {
    new SimpleAlert().showAndWait(AlertType.WARNING, title, message);
  }

  public static void displayOperationProhibitedDialog(String message) {
    displayWarningDialog(DialogTitle.OPERATION_PROHIBITED, AdakiteUtils.formatAsSentence(message, Locale.US));
  }

}
