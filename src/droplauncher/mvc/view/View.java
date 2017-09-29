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
import adakite.util.AdakiteUtils;
import adakite.windows.Windows;
import droplauncher.mvc.controller.Controller;
import droplauncher.mvc.model.Model;
import droplauncher.starcraft.Starcraft.Race;
import droplauncher.DropLauncher;
import droplauncher.bwapi.BWAPI;
import droplauncher.mvc.controller.ControllerWrapper;
import droplauncher.starcraft.Starcraft;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TextField;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import org.apache.commons.io.FilenameUtils;

public class View implements EventHandler<DragEvent>  {

  private static final Logger LOGGER = Logger.getLogger(View.class.getName());

  public enum PropertyKey {

    /**
     * Whether to display the log window.
     */
    SHOW_LOG_WINDOW("show_log_window"),

    /**
     * Whether to use DropLauncher's theme or the system's default theme.
     */
    USE_DROPLAUNCHER_THEME("use_droplauncher_theme")

    ;

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
    CLEAR_EXTRA_BOT_FILES("Config Files")
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
      HELP_CONTENTS("DropLauncher Help"),
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

//  public static final Prefs PREF_ROOT = DropLauncher.PREF_ROOT.getChild("ui");

  private static final String RESOURCE_PATH = "/droplauncher/mvc/view/theme/";
  private static final String CSS_PATH = RESOURCE_PATH + "droplauncher.css";

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
    mnuFileSelectBotFiles.setOnAction(e -> { this.controller.mnuFileSelectBotFilesClicked(this.stage); });
    MenuItem mnuFileExit = new MenuItem(MenuText.EXIT.toString());
    mnuFileExit.setOnAction(e -> { this.controller.mnuFileExitClicked(this.stage); });
    this.fileMenu = new Menu(MenuText.FILE.toString());
    this.fileMenu.getItems().add(mnuFileSelectBotFiles);
    this.fileMenu.getItems().add(mnuFileExit);

    /* Edit */
    MenuItem mnuEditSettings = new MenuItem(MenuText.SETTINGS.toString());
    mnuEditSettings.setOnAction(e -> { this.controller.mnuEditSettingsClicked(); });
    this.editMenu = new Menu(MenuText.EDIT.toString());
    this.editMenu.getItems().add(mnuEditSettings);

    /* Help */
    MenuItem mnuHelpAbout = new MenuItem(MenuText.ABOUT.toString());
    mnuHelpAbout.setOnAction(e -> { this.controller.mnuHelpAboutClicked(); });
    MenuItem mnuHelpHelpContents = new MenuItem(MenuText.HELP_CONTENTS.toString());
    mnuHelpHelpContents.setOnAction(e -> { this.controller.mnuHelpContentsClicked(); });
    this.helpMenu = new Menu(MenuText.HELP.toString());
    this.helpMenu.getItems().add(mnuHelpHelpContents);
    this.helpMenu.getItems().add(mnuHelpAbout);

    /* Compile menus. */
    this.menuBar = new MenuBar();
    this.menuBar.getMenus().add(this.fileMenu);
    this.menuBar.getMenus().add(this.editMenu);
    this.menuBar.getMenus().add(this.helpMenu);
    this.menuBar.getStyleClass().add("launcher-menustyle");
  }

  private void initComponents() {
    this.lblBwapiVersion = new Label(BWAPI.DLL_FILENAME_RELEASE + " Version:");
    this.lblBwapiVersion.setMinWidth(Region.USE_PREF_SIZE);
    this.lblBwapiVersionText = new Label(EMPTY_LABEL_TEXT);
    this.lblBwapiVersionText.getStyleClass().add("highlighted-text");
    this.lblBwapiVersionText.setMinWidth(Region.USE_PREF_SIZE);
    this.lblBotFile = new Label("Bot File:");
    this.lblBotFile.setMinWidth(Region.USE_PREF_SIZE);
    this.lblBotFileText = new Label(EMPTY_LABEL_TEXT);
    this.lblBotFileText.getStyleClass().add("highlighted-text");
    this.lblBotName = new Label("Bot Name (max 24 characters):");
    this.lblBotName.setMinWidth(Region.USE_PREF_SIZE);
    this.txtBotName = new TextField("");
    this.txtBotName.setMinWidth(Region.USE_PREF_SIZE);
    this.txtBotName.setOnKeyReleased(e -> { this.controller.botNameChanged(this.txtBotName.getText()); });
    this.cbRace = new ChoiceBox<>();
    this.cbRace.getItems().add(Race.TERRAN.toString());
    this.cbRace.getItems().add(Race.ZERG.toString());
    this.cbRace.getItems().add(Race.PROTOSS.toString());
    this.cbRace.getItems().add(Race.RANDOM.toString());
//    this.cbRace.getStyleClass().add("launcher-context-menu");
    this.cbRace.setOnAction(e -> {
      String previous = this.controller.getBotRace().toString();
      String current = this.cbRace.getValue();
      if (!current.equals(previous)) {
        this.controller.botRaceChanged(current);
      }
    });
    this.btnStart = new Button(StartButtonText.START.toString());
    this.btnStart.setMinWidth(250);
    this.btnStart.setMinHeight(45); //Changed from 30
    this.btnStart.getStyleClass().add("launch-btn");
    this.btnStart.setOnAction(e -> {
      try {
        this.controller.btnStartClicked();
      } catch (InvalidStateException ex) {
        new ExceptionAlert().showAndWait(null, ex);
      }
    });
    this.chkAutoEject = new CheckBox();
    this.chkAutoEject.setText("Auto-eject bot after game has ended");
    this.chkAutoEject.setSelected(Model.getSettings().isEnabled(DropLauncher.PropertyKey.AUTO_EJECT_BOT.toString()));
    this.chkAutoEject.setOnAction(e -> { Model.getSettings().setEnabled(DropLauncher.PropertyKey.AUTO_EJECT_BOT.toString(), this.chkAutoEject.isSelected()); });
    this.chkAutoRejoin = new CheckBox();
    this.chkAutoRejoin.setText("Auto-connect bot to game lobby after eject");
    this.chkAutoRejoin.setSelected(Model.getSettings().isEnabled(DropLauncher.PropertyKey.AUTO_BOT_REJOIN.toString()));
    this.chkAutoRejoin.setOnAction(e -> { Model.getSettings().setEnabled(DropLauncher.PropertyKey.AUTO_BOT_REJOIN.toString(), this.chkAutoRejoin.isSelected()); });
    this.consoleOutput = new ConsoleOutput();
    this.consoleOutput.getBlacklist().add("fps: "); /* bwheadless.exe spam */
    this.consoleOutput.get().getStyleClass().add("console-output");
    this.consoleOutput.get().setMinWidth(475); //500
    this.consoleOutput.get().setMinHeight(200); //300
    this.consoleOutput.get().setEditable(false);
    this.consoleOutput.setController(new ControllerWrapper(this.controller));
    ContextMenu cmConsoleOutput = new ContextMenu();
    MenuItem miClear = new MenuItem("Clear");
    miClear.setOnAction(e -> {
      Platform.runLater(() -> {
        if (new YesNoDialog().userConfirms("Confirmation", "Are you sure you want to clear the console output? You will not be able to retrieve it.")) {
          this.consoleOutput.clear();
        }
      });
    });
    cmConsoleOutput.getItems().add(miClear);
    cmConsoleOutput.getItems().add(new SeparatorMenuItem());
    MenuItem miSave = new MenuItem("Save to file...");
    miSave.setOnAction(e -> {
      FileChooser fc = new FileChooser();
      fc.getExtensionFilters().add(new ExtensionFilter("*.log", "log"));
      String botName = this.controller.getBotName();
      String datetime = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss").format(LocalDateTime.now());
      String initialFilename = datetime + "_" + (AdakiteUtils.isNullOrEmpty(botName, true) ? "DropLauncher" : botName) + ".log";
      fc.setInitialFileName(initialFilename);
      String userDirectory = Windows.getUserDesktopDirectory().toAbsolutePath().toString();
      if (userDirectory != null) {
        fc.setInitialDirectory(new File(userDirectory));
      }
      File userFile = fc.showSaveDialog(this.stage);
      if (userFile != null) {
        Path saveFile = userFile.toPath().toAbsolutePath();
        String filename = FilenameUtils.getBaseName(saveFile.toString());
        Path saveParent = AdakiteUtils.getParentDirectory(saveFile);
        if (saveParent == null) {
          saveParent = Paths.get("");
        }
        String saveExt = fc.getSelectedExtensionFilter().getExtensions().get(0);
        if (AdakiteUtils.isNullOrEmpty(saveExt, true) || saveExt.equals("*")) {
          saveExt = "log";
        }
        String userExt = AdakiteUtils.getFileExtension(saveFile);
        if (!AdakiteUtils.isNullOrEmpty(userExt, true)) {
          /* If user used a file extention, use that one over the context menu selected file extension. */
          saveExt = userExt;
        }

        try {
          saveFile = saveParent.resolve(filename + (AdakiteUtils.isNullOrEmpty(saveExt, true) ? "" : ("." + saveExt)));
        } catch (Exception ex) {
          new ExceptionAlert().showAndWait("Failed to save file due to an invalid filename or directory", ex);
          return;
        }

        try {
          this.controller.saveToFile(saveFile, this.consoleOutput.get().getText());
          new SimpleAlert().showAndWait(AlertType.INFORMATION, DialogTitle.PROGRAM_NAME, "File saved to: " + AdakiteUtils.newline(2) + saveFile.toAbsolutePath().toString());
        } catch (Exception ex) {
          new ExceptionAlert().showAndWait(null, ex);
          return;
        }
      }
    });
    cmConsoleOutput.getItems().add(miSave);
    this.consoleOutput.get().setContextMenu(cmConsoleOutput);
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
                  + "Do you want to unload the following bot config files?" + AdakiteUtils.newline(2)
                  + fileList.toString())) {
            this.controller.clearExtraBotFiles();
          }
        } else {
          new SimpleAlert().showAndWait(AlertType.INFORMATION, DialogTitle.PROGRAM_NAME, "Total bot config files loaded: " + fileCount);
        }
        update();
      });
    });

    CustomGridPane fileLabelGridPane = new CustomGridPane();
    fileLabelGridPane.add(this.lblBotFile);
    fileLabelGridPane.add(this.lblBotFileText);
    fileLabelGridPane.add(this.cbRace);
    fileLabelGridPane.add(this.btnClearExtraBotFiles).nextRow();
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

    HBox boxClear = new HBox();
    boxClear.setSpacing(30);
    boxClear.setAlignment(Pos.CENTER_RIGHT);
    VBox boxStartConsole = new VBox();
    boxStartConsole.getChildren().add(this.btnStart);
    if (Model.getSettings().isEnabled(View.PropertyKey.SHOW_LOG_WINDOW.toString())) {
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

    this.btnStart.requestFocus();
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

    if (!Model.getSettings().hasValue(Starcraft.PropertyKey.STARCRAFT_EXE.toString())) {
      new SimpleAlert().showAndWait(AlertType.INFORMATION, DialogTitle.PROGRAM_NAME,
          "Welcome to DropLauncher!"
          + AdakiteUtils.newline(2)
          + Starcraft.BINARY_FILENAME + " is not set. Please go to the \""
          + View.MenuText.EDIT.toString() + " > " + View.MenuText.SETTINGS.toString()
          + "\" menu option and set it before playing against a bot."
      );
    }
  }

  /**
   * Updates the components to display the internal values.
   */
  public void update() {
    setText(this.lblBwapiVersionText, this.controller.getBwapiDllVersion());

    setText(this.lblBotFileText, this.controller.getBotFilename());
    if (!AdakiteUtils.isNullOrEmpty(this.controller.getBotFilename())) {
      this.cbRace.setVisible(true);
      if (this.controller.getExtraBotFiles().size() > 0) {
        this.btnClearExtraBotFiles.setText(ButtonText.CLEAR_EXTRA_BOT_FILES.toString() + " (" + this.controller.getExtraBotFiles().size() + ")");
        this.btnClearExtraBotFiles.setVisible(true);
        this.btnClearExtraBotFiles.setDisable(false);
      } else {
        this.btnClearExtraBotFiles.setText(ButtonText.CLEAR_EXTRA_BOT_FILES.toString());
        this.btnClearExtraBotFiles.setVisible(true);
        this.btnClearExtraBotFiles.setDisable(true);
      }
      this.cbRace.getSelectionModel().select(this.controller.getBotRace().toString());
    } else {
      this.cbRace.setVisible(false);
      this.btnClearExtraBotFiles.setVisible(false);
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
      str = View.EMPTY_LABEL_TEXT;
    }

    if (node instanceof Button) {
      Button button = (Button) node;
      button.setText(str);
    } else if (node instanceof Label) {
      Label label = (Label) node;
      label.setText(str);
    } else if (node instanceof TextField) {
      TextField textField = (TextField) node;
      textField.setText(str);
    }
  }

  public void btnStartSetText(String str) {
    setText(this.btnStart, str);
  }

  public void btnStartSetEnabled(boolean enabled) {
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
    if (Model.getSettings().isEnabled(View.PropertyKey.USE_DROPLAUNCHER_THEME.toString())) {
      try {
        sheets.add(View.CSS_PATH);
      } catch (Exception ex) {
        LOGGER.log(Debugging.getLogLevel(), null, ex);
      }
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
