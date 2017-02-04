package droplauncher.mvc.view;

import adakite.ini.INI;
import adakite.util.AdakiteUtils;
import droplauncher.mvc.controller.Controller;
import droplauncher.starcraft.Race;
import droplauncher.util.Constants;
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
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class View implements EventHandler<DragEvent>  {

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
  private Button btnLaunch;
  private TextArea txtLogWindow;

  private static final int PADDING = 20;
  private static final int TOP_PADDING = PADDING;
  private static final int BOTTOM_PADDING = PADDING;
  private static final int LEFT_PADDING = PADDING;
  private static final int RIGHT_PADDING = PADDING;
  private static final int GAP = 10;
  private static final int LABEL_TEXT_SPACING = 4;

  private static final String EMPTY_LABEL = "-";

  private INI ini;

  public View() {

  }

  public void setController(Controller controller) {
    this.controller = controller;
  }

  public void setINI(INI ini) {
    this.ini = ini;
  }

  private void initMenus() {
    MenuItem mnuFileSelectBotFiles = new MenuItem(MenuText.SELECT_BOT_FILES.toString());
    mnuFileSelectBotFiles.setOnAction(e -> this.controller.mnuFileSelectBotFilesClicked());
    MenuItem mnuFileExit = new MenuItem(MenuText.EXIT.toString());
    mnuFileExit.setOnAction(e -> this.controller.mnuFileExitClicked());
    this.fileMenu = new Menu(MenuText.FILE.toString());
    this.fileMenu.getItems().add(mnuFileSelectBotFiles);
    this.fileMenu.getItems().add(mnuFileExit);

    MenuItem mnuEditSettings = new MenuItem(MenuText.SETTINGS.toString());
    mnuEditSettings.setOnAction(e -> this.controller.mnuEditSettingsClicked());
    this.editMenu = new Menu(MenuText.EDIT.toString());
    this.editMenu.getItems().add(mnuEditSettings);

    MenuItem mnuHelpAbout = new MenuItem(MenuText.ABOUT.toString());
    mnuHelpAbout.setOnAction(e -> this.controller.mnuHelpAboutClicked());
    this.helpMenu = new Menu(MenuText.HELP.toString());
    this.helpMenu.getItems().add(new MenuItem(MenuText.ABOUT.toString()));

    this.menuBar = new MenuBar();
    this.menuBar.getMenus().add(this.fileMenu);
    this.menuBar.getMenus().add(this.editMenu);
    this.menuBar.getMenus().add(this.helpMenu);
  }

  private void initComponents() {
    initMenus();

    this.lblBwapiVersion = new Label("BWAPI.dll Version:");
    this.lblBwapiVersionText = new Label(EMPTY_LABEL);
    this.lblBotFile = new Label("Bot File:");
    this.lblBotFileText = new Label(EMPTY_LABEL);
    this.lblBotName = new Label("Bot Name (max 24 characters):");
    this.txtBotName = new TextField("");
    this.cbRace = new ChoiceBox<>();
    this.cbRace.getItems().add(Race.TERRAN.toString());
    this.cbRace.getItems().add(Race.ZERG.toString());
    this.cbRace.getItems().add(Race.PROTOSS.toString());
    this.cbRace.getItems().add(Race.RANDOM.toString());
    this.btnLaunch = new Button(LaunchButtonText.LAUNCH.toString());
    this.btnLaunch.setMinWidth(200);
    this.btnLaunch.setMinHeight(40);
    this.txtLogWindow = new TextArea("");
    this.txtLogWindow.setEditable(false);

    this.txtBotName.setOnKeyReleased(e -> this.controller.botNameChanged(this.txtBotName.getText()));
    this.cbRace.setOnAction(e -> {
      String previous = this.controller.getBotRace().toString();
      String current = this.cbRace.getValue();
      if (!current.equals(previous)) {
        this.controller.botRaceChanged(current);
      }
    });
    this.btnLaunch.setOnAction(e -> this.controller.btnLaunchClicked());

    CustomGridPane fileGridPane = new CustomGridPane();
    HBox hboxBotFile = new HBox();
    hboxBotFile.getChildren().add(this.lblBotFile);
    hboxBotFile.getChildren().add(this.lblBotFileText);
    hboxBotFile.setSpacing(LABEL_TEXT_SPACING);
    fileGridPane.add(hboxBotFile, true);
    HBox hboxBwapiVersion = new HBox();
    hboxBwapiVersion.getChildren().add(this.lblBwapiVersion);
    hboxBwapiVersion.getChildren().add(this.lblBwapiVersionText);
    hboxBwapiVersion.setSpacing(LABEL_TEXT_SPACING);
    fileGridPane.add(hboxBwapiVersion, true);
    fileGridPane.setGaps(GAP, GAP);
    fileGridPane.pack();

    CustomGridPane botNameGridPane = new CustomGridPane();
    botNameGridPane.add(this.lblBotName, true);
    botNameGridPane.add(this.txtBotName);
    botNameGridPane.add(this.cbRace, true);
    botNameGridPane.setGaps(GAP, 2);
    botNameGridPane.pack();

    CustomGridPane botInfoGridPane = new CustomGridPane();
    botInfoGridPane.add(fileGridPane.get(), true);
    botInfoGridPane.add(botNameGridPane.get(), true);
    botInfoGridPane.setGaps(GAP, GAP);
    botInfoGridPane.get().setAlignment(Pos.CENTER);
    botInfoGridPane.pack();

    CustomGridPane btnLaunchGridPane = new CustomGridPane();
    btnLaunchGridPane.add(this.btnLaunch, true);
    btnLaunchGridPane.get().setAlignment(Pos.CENTER);
    btnLaunchGridPane.setGaps(GAP, GAP);
    btnLaunchGridPane.pack();

    CustomGridPane txtLogWindowGridPane = new CustomGridPane();
    if (this.controller.isEnabledLogWindow()) {
      txtLogWindowGridPane.add(this.txtLogWindow);
    }
    txtLogWindowGridPane.setGaps(GAP, GAP);
    txtLogWindowGridPane.get().setAlignment(Pos.CENTER);
    txtLogWindowGridPane.pack();

    CustomGridPane mainGridPane = new CustomGridPane();
    mainGridPane.add(botInfoGridPane.get(), true);
    mainGridPane.add(btnLaunchGridPane.get(), true);
    mainGridPane.add(txtLogWindowGridPane.get(), true);
    mainGridPane.get().setPadding(new Insets(TOP_PADDING, LEFT_PADDING, BOTTOM_PADDING, RIGHT_PADDING));
    mainGridPane.setGaps(GAP, GAP);
    mainGridPane.get().setAlignment(Pos.CENTER);
    mainGridPane.pack();

    BorderPane borderPane = new BorderPane();
    borderPane.setTop(this.menuBar);
    borderPane.setCenter(mainGridPane.get());

    this.scene = new Scene(borderPane);
    this.scene.setOnDragOver(this);
    this.scene.setOnDragDropped(this);

    this.stage.setResizable(false);
    this.stage.setTitle(Constants.PROGRAM_TITLE);
    this.stage.setScene(this.scene);
  }

  public TextArea getLogWindow() {
    return this.txtLogWindow;
  }

  public void start(Stage primaryStage) {
    this.stage = primaryStage;

    initComponents();

    this.stage.show();

    update();
  }

  public void update() {
    setText(this.lblBwapiVersionText, this.controller.getBwapiDllVersion());

    setText(this.lblBotFileText, this.controller.getBotModule().getPath().getFileName().toString());

    String displayBotName = this.txtBotName.getText();
    String internalBotName = this.controller.getBotName();
    int caret = txtBotName.getCaretPosition();
    if (!displayBotName.equals(internalBotName)) {
      if (caret >= internalBotName.length()) {
        caret = internalBotName.length();
      } else if (caret > 1) {
        caret--;
      }
      setText(this.txtBotName, internalBotName);
      txtBotName.positionCaret(caret);
    }

    setText(this.cbRace, this.controller.getBotRace().toString());
  }

  private void setText(Node node, String str) {
    if (AdakiteUtils.isNullOrEmpty(str)) {
      str = "";
    }

    if (node instanceof Button) {
      Button button = (Button) node;
      button.setText(str);
    } else if (node instanceof Label) {
      Label label = (Label) node;
      label.setText(str);
    } else if (node instanceof ChoiceBox) {
      ChoiceBox choiceBox = (ChoiceBox) node;
      choiceBox.setValue(str);
    } else if (node instanceof TextField) {
      TextField textField = (TextField) node;
      textField.setText(str);
    }
  }

  public void btnLaunchSetText(String str) {
    setText(this.btnLaunch, str);
  }

  public void btnLaunchEnabled(boolean enabled) {
    this.btnLaunch.setDisable(!enabled);
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
