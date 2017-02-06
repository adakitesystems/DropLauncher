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
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
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

  public static final String DEFAULT_CSS = "droplauncher/mvc/view/themes/droplauncher.css";
//  public static final String NEUROPOL_FONT = "droplauncher/mvc/view/themes/neuropol.ttf";

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

  private Image imgFile;
  private Image imgFileBlank;
  private Image imgFileExe;
  private Image imgFileJar;
  private Image imgFileDll;
  private Image imgBwapi;

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
    this.imgFileBlank = new Image("droplauncher/mvc/view/themes/images/file-blank-56.png");
    this.imgFileDll = new Image("droplauncher/mvc/view/themes/images/file-dll-56.png");
    this.imgFileExe = new Image("droplauncher/mvc/view/themes/images/file-exe-56.png");
    this.imgFileJar = new Image("droplauncher/mvc/view/themes/images/file-java-56.png");
    this.imgFile = this.imgFileJar;
    this.imgBwapi = new Image("droplauncher/mvc/view/themes/images/bwapi.png");
  }

  private void initComponents() {
    initMenus();
    initImages();

    this.lblBwapiVersion = new Label("BWAPI.dll Version:");
    this.lblBwapiVersionText = new Label(EMPTY_LABEL);
    this.lblBwapiVersionText.setMinWidth(Region.USE_PREF_SIZE);
    this.lblBotFile = new Label("Bot File:");
    this.lblBotFileText = new Label(EMPTY_LABEL);
    this.lblBotFileText.setMinWidth(Region.USE_PREF_SIZE);
    this.lblBotName = new Label("Bot Name (max 24 characters):");
    this.txtBotName = new TextField("");
    this.txtBotName.setMinWidth(Region.USE_PREF_SIZE);
    this.cbRace = new ChoiceBox<>();
    this.cbRace.setMinWidth(Region.USE_PREF_SIZE);
    this.cbRace.getItems().add(Race.TERRAN.toString());
    this.cbRace.getItems().add(Race.ZERG.toString());
    this.cbRace.getItems().add(Race.PROTOSS.toString());
    this.cbRace.getItems().add(Race.RANDOM.toString());
    this.cbRace.getStyleClass().add("protoss-font");
    this.btnLaunch = new Button(LaunchButtonText.LAUNCH.toString());
    this.btnLaunch.setMinWidth(200);
    this.btnLaunch.setMinHeight(40);
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

    HBox hboxBotFile = new HBox();
    hboxBotFile.getChildren().add(new ImageView(this.imgFile));
    hboxBotFile.getChildren().add(this.lblBotFile);
    hboxBotFile.getChildren().add(this.lblBotFileText);
    hboxBotFile.setSpacing(DefaultSetting.LABEL_TEXT_SPACING.getValue());
    hboxBotFile.setAlignment(Pos.CENTER);
    HBox hboxBwapiVersion = new HBox();
    hboxBwapiVersion.getChildren().add(new ImageView(this.imgBwapi));
    hboxBwapiVersion.getChildren().add(this.lblBwapiVersion);
    hboxBwapiVersion.getChildren().add(this.lblBwapiVersionText);
    hboxBwapiVersion.setSpacing(DefaultSetting.LABEL_TEXT_SPACING.getValue());
    hboxBwapiVersion.setAlignment(Pos.CENTER);
    CustomGridPane fileGridPane = new CustomGridPane();
    fileGridPane.add(hboxBotFile, true);
    fileGridPane.add(hboxBwapiVersion, true);
    fileGridPane.setGaps(DefaultSetting.GAP.getValue(), DefaultSetting.GAP.getValue());

    CustomGridPane botNameGridPane = new CustomGridPane();
    botNameGridPane.add(this.lblBotName, true);
    botNameGridPane.add(this.txtBotName);
    botNameGridPane.add(this.cbRace, true);
    botNameGridPane.setGaps(DefaultSetting.GAP.getValue(), 2);

    CustomGridPane botInfoGridPane = new CustomGridPane();
    botInfoGridPane.add(fileGridPane.get(), true);
    botInfoGridPane.add(botNameGridPane.get(), true);
    botInfoGridPane.setGaps(DefaultSetting.GAP.getValue(), DefaultSetting.GAP.getValue());
    botInfoGridPane.get().setAlignment(Pos.CENTER);

    CustomGridPane btnLaunchGridPane = new CustomGridPane();
    btnLaunchGridPane.add(this.btnLaunch, true);
    btnLaunchGridPane.get().setAlignment(Pos.CENTER);
    btnLaunchGridPane.setGaps(DefaultSetting.GAP.getValue(), DefaultSetting.GAP.getValue());

    CustomGridPane txtLogWindowGridPane = new CustomGridPane();
    if (this.controller.isEnabledLogWindow()) {
      txtLogWindowGridPane.add(this.console.get());
    }
    txtLogWindowGridPane.setGaps(DefaultSetting.GAP.getValue(), DefaultSetting.GAP.getValue());
    txtLogWindowGridPane.get().setAlignment(Pos.CENTER);

    CustomGridPane mainGridPane = new CustomGridPane();
    mainGridPane.add(botInfoGridPane.get(), true);
    mainGridPane.add(btnLaunchGridPane.get(), true);
    mainGridPane.add(txtLogWindowGridPane.get(), true);
    mainGridPane.get().setPadding(new Insets(
        DefaultSetting.TOP_PADDING.getValue(),
        DefaultSetting.LEFT_PADDING.getValue(),
        DefaultSetting.BOTTOM_PADDING.getValue(),
        DefaultSetting.RIGHT_PADDING.getValue()
    ));
    mainGridPane.setGaps(DefaultSetting.GAP.getValue(), DefaultSetting.GAP.getValue());
    mainGridPane.get().setAlignment(Pos.CENTER);

    BorderPane borderPane = new BorderPane();
    borderPane.setTop(this.menuBar);
    borderPane.setCenter(mainGridPane.get());
    borderPane.setMinWidth(800);
    borderPane.getStyleClass().add("dl-bp");

    this.scene = new Scene(borderPane);
    this.scene.setOnDragOver(this);
    this.scene.setOnDragDropped(this);
//    this.scene.getStylesheets().add(getClass().getResource("themes/droplauncher.css").toString());
//    this.scene.getStylesheets().add("droplauncher/mvc/view/themes/droplauncher.css");
    this.scene.getStylesheets().add(DEFAULT_CSS);

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
