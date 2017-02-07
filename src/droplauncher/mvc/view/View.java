package droplauncher.mvc.view;

import adakite.ini.INI;
import adakite.util.AdakiteUtils;
import droplauncher.bwheadless.BotModule;
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
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class View implements EventHandler<DragEvent>  {

  public enum DefaultSetting {

    PADDING(20),
    TOP_PADDING(PADDING.getValue()),
    BOTTOM_PADDING(PADDING.getValue()),
    LEFT_PADDING(PADDING.getValue()),
    RIGHT_PADDING(PADDING.getValue()),
    GAP(10),
    LABEL_TEXT_SPACING(4)
    ;

    private int val;

    private DefaultSetting(int val) {
      this.val = val;
    }

    public int getValue() {
      return this.val;
    }

  }

  private static final String RESOURCE_PATH = "/droplauncher/mvc/view/theme/";
  private static final String IMAGE_PATH = RESOURCE_PATH + "images/";
  public static final String DEFAULT_CSS = RESOURCE_PATH + "droplauncher.css";

  private static final String EMPTY_LABEL = "-";

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
  private ConsoleOutput console;

  private ImageView imgFile;
  private ImageView imgFileBlank;
  private ImageView imgFileExe;
  private ImageView imgFileJar;
  private ImageView imgFileDll;
  private ImageView imgBwapi;
  private ImageView imgRobot;

  private INI ini;

  public View() {
    /* Do nothing. */
  }

  public void setController(Controller controller) {
    this.controller = controller;
  }

  public void setINI(INI ini) {
    this.ini = ini;
  }

  private void initMenus() {
    /* File */
    MenuItem mnuFileSelectBotFiles = new MenuItem(MenuText.SELECT_BOT_FILES.toString());
    mnuFileSelectBotFiles.setOnAction(e -> this.controller.mnuFileSelectBotFilesClicked(this.stage));
    MenuItem mnuFileExit = new MenuItem(MenuText.EXIT.toString());
    mnuFileExit.setOnAction(e -> {
      this.controller.mnuFileExitClicked(this.stage);
      e.consume();
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

  private void initImages() {
    this.imgFileBlank = new ImageView(IMAGE_PATH + "file-blank-2-80-56.png");
    this.imgFileDll = new ImageView(IMAGE_PATH + "file-dll-56.png");
    this.imgFileExe = new ImageView(IMAGE_PATH + "file-exe-56.png");
    this.imgFileJar = new ImageView(IMAGE_PATH + "file-java-56.png");
    this.imgFile = new ImageView(this.imgFileBlank.getImage());
    this.imgBwapi = new ImageView(IMAGE_PATH + "bwapi.png");
    this.imgRobot = new ImageView(IMAGE_PATH + "robot-56.png");
  }

  private void initComponents() {
    initMenus();
    initImages();

    this.lblBwapiVersion = new Label("BWAPI.dll Version:");
    this.lblBwapiVersion.setMinWidth(Region.USE_PREF_SIZE);
    this.lblBwapiVersionText = new Label(EMPTY_LABEL);
    this.lblBwapiVersionText.setMinWidth(Region.USE_PREF_SIZE);
    this.lblBotFile = new Label("Bot File:");
    this.lblBotFile.setMinWidth(Region.USE_PREF_SIZE);
    this.lblBotFileText = new Label(EMPTY_LABEL);
    this.lblBotName = new Label("Bot Name (max 24 characters):");
    this.lblBotName.setMinWidth(Region.USE_PREF_SIZE);
    this.txtBotName = new TextField("");
    this.txtBotName.setMinWidth(Region.USE_PREF_SIZE);
    this.cbRace = new ChoiceBox<>();
    this.cbRace.getItems().add(Race.TERRAN.toString());
    this.cbRace.getItems().add(Race.ZERG.toString());
    this.cbRace.getItems().add(Race.PROTOSS.toString());
    this.cbRace.getItems().add(Race.RANDOM.toString());
    this.btnLaunch = new Button(LaunchButtonText.LAUNCH.toString());
    this.btnLaunch.setMinWidth(300);
    this.btnLaunch.setMinHeight(50);
    this.btnLaunch.getStyleClass().add("launch-btn");
    this.console = new ConsoleOutput();
    this.console.get().getStyleClass().add("console-output");
    this.console.get().setMinWidth(500);
    this.console.get().setMinHeight(300);

    this.txtBotName.setOnKeyReleased(e -> this.controller.botNameChanged(this.txtBotName.getText()));
    this.cbRace.setOnAction(e -> {
      String previous = this.controller.getBotRace().toString();
      String current = this.cbRace.getValue();
      if (!current.equals(previous)) {
        this.controller.botRaceChanged(current);
      }
    });
    this.btnLaunch.setOnAction(e -> this.controller.btnLaunchClicked());

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
    infoGridPane.add(this.imgFile);
    infoGridPane.add(fileLabelGridPane.get(), true);
    infoGridPane.add(this.imgBwapi);
    infoGridPane.add(bwapiLabelGridPane.get(), true);
    infoGridPane.add(this.imgRobot);
    infoGridPane.add(botNameGridPane.get(), true);
    infoGridPane.setGaps(DefaultSetting.GAP.getValue(), DefaultSetting.GAP.getValue());
    infoGridPane.get().setMinWidth(Region.USE_PREF_SIZE);
    infoGridPane.get().setAlignment(Pos.CENTER_LEFT);

    VBox hbox = new VBox();
    this.btnLaunch.setAlignment(Pos.CENTER);
    hbox.getChildren().add(this.btnLaunch);
    if (this.controller.isEnabledLogWindow()) {
      hbox.getChildren().add(this.console.get());
    }
    hbox.setSpacing(DefaultSetting.GAP.getValue());
    hbox.setAlignment(Pos.CENTER);
    hbox.setMinWidth(Region.USE_PREF_SIZE);

    CustomGridPane mainGridPane = new CustomGridPane();
    mainGridPane.add(infoGridPane.get(), true);
    mainGridPane.add(hbox, true);
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
    this.scene.getStylesheets().add(View.class.getResource(DEFAULT_CSS).toString());

    this.stage.setOnCloseRequest(e -> {
      this.controller.closeProgramRequest(this.stage);
      e.consume();
    });
    this.stage.setResizable(false);
    this.stage.setTitle(Constants.PROGRAM_TITLE);
    this.stage.setScene(this.scene);
  }

  public ConsoleOutput getConsoleOutput() {
    return this.console;
  }

  public ChoiceBox getRaceChoiceBox() {
    return this.cbRace;
  }

  public void start(Stage primaryStage) {
    this.stage = primaryStage;

    initComponents();

    this.stage.show();

    update();
  }

  public void update() {
    setText(this.lblBwapiVersionText, this.controller.getBwapiDllVersion());

    switch (this.controller.getBotModule().getType()) {
      case CLIENT:
        String ext = AdakiteUtils.getFileExtension(this.controller.getBotModule().getPath().getFileName());
        if (ext.equalsIgnoreCase("jar")) {
          this.imgFile.setImage(this.imgFileJar.getImage());
        } else if (ext.equalsIgnoreCase("exe")) {
          this.imgFile.setImage(this.imgFileExe.getImage());
        } else {
          this.imgFile.setImage(this.imgFileBlank.getImage());
        }
        break;
      case DLL:
        this.imgFile.setImage(this.imgFileDll.getImage());
        break;
      default:
        this.imgFile.setImage(this.imgFileBlank.getImage());
        break;
    }
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

    if (this.controller.getBotModule().getType() != BotModule.Type.UNKNOWN) {
      this.cbRace.setVisible(true);
      this.imgFile.setOpacity(1.0);
      this.controller.updateRaceChoiceBox();
    } else {
      this.cbRace.setVisible(false);
      this.imgFile.setOpacity(0.4);
    }

    sizeToScene();
  }

  public void sizeToScene() {
    this.stage.sizeToScene();
  }

  public void setText(Node node, String str) {
    if (AdakiteUtils.isNullOrEmpty(str)) {
      str = EMPTY_LABEL;
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
