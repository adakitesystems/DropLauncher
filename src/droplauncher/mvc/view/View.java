package droplauncher.mvc.view;

import adakite.utils.AdakiteUtils;
import droplauncher.mvc.controller.Controller;
import droplauncher.starcraft.Race;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class View implements EventHandler<ActionEvent> {

  private Controller controller;

  private Stage stage;
  private Scene scene;

  private Label lblBwapiVersion;
  private Label lblBwapiVersionText;
  private Label lblBotFile;
  private Label lblBotFileText;
  private TextField txtBotName;
  private ChoiceBox<String> cbRace;
  private Button btnLaunch;

  private static final int PADDING = 20;
  private static final int TOP_PADDING = PADDING;
  private static final int BOTTOM_PADDING = PADDING;
  private static final int LEFT_PADDING = PADDING;
  private static final int RIGHT_PADDING = PADDING;
  private static final int GAP = 10;

  public View() {

  }

  public void setController(Controller controller) {
    this.controller = controller;
  }

  private void initComponents() {

    this.lblBwapiVersion = new Label("BWAPI:");
    this.lblBwapiVersionText = new Label("lblBwapiVersionText");
    this.lblBotFile = new Label("Bot File:");
    this.lblBotFileText = new Label("lblBotFileText");
    this.txtBotName = new TextField("txtBotName");
    this.cbRace = new ChoiceBox<>();
    this.btnLaunch = new Button(LaunchButtonText.LAUNCH.toString());

//    this.btnLaunch.setMinWidth(this.btnLaunch.getWidth() + 500);
//    this.btnLaunch.set
//    System.out.println(this.btnLaunch.getWidth());

    this.cbRace.getItems().add(Race.TERRAN.toString());
    this.cbRace.getItems().add(Race.ZERG.toString());
    this.cbRace.getItems().add(Race.PROTOSS.toString());
    this.cbRace.getItems().add(Race.RANDOM.toString());
//    this.cbRace.setValue(Race.TERRAN.toString());
    setText(cbRace, Race.ZERG.toString());

    GridPane mainGridPane = new GridPane();
    mainGridPane.setPadding(new Insets(TOP_PADDING, LEFT_PADDING, BOTTOM_PADDING, RIGHT_PADDING));
    mainGridPane.setVgap(GAP);
    mainGridPane.setHgap(GAP);

    GridPane.setConstraints(this.lblBwapiVersion, 0, 0);
    GridPane.setConstraints(this.lblBwapiVersionText, 1, 0);
    GridPane.setConstraints(this.lblBotFile, 0, 1);
    GridPane.setConstraints(this.lblBotFileText, 1, 1);
    GridPane.setConstraints(this.txtBotName, 0, 2);
    GridPane.setConstraints(this.cbRace, 0, 3);
    GridPane.setConstraints(this.btnLaunch, 0, 4);


    mainGridPane.getChildren().addAll(
        this.lblBotFile,
        this.lblBotFileText,
        this.lblBwapiVersion,
        this.lblBwapiVersionText,
        this.txtBotName,
        this.cbRace,
        this.btnLaunch
    );

    this.scene = new Scene(mainGridPane);

    this.stage.setTitle("This is my title");
    this.stage.setScene(scene);
  }

  public void start(Stage primaryStage) {
    this.stage = primaryStage;

    initComponents();

    this.stage.show();

//
//    this.label = new Label("[label here]");
//    this.label.setText("[label text]");
//
//    this.button = new Button();
//    this.button.setText("Button text here");
//    this.button.setOnAction(this);
//
//    this.button2 = new Button();
//    this.button2.setText("Button text here again");
//    this.button2.setOnAction(this);
//
////    VBox layout = new VBox(20);
////    layout.getChildren().addAll(this.button, this.button2);
////    scene = new Scene(layout, 500, 300);
//
//    int val = 30;
//    GridPane grid = new GridPane();
//    grid.setPadding(new Insets(val, val, val, val));
////    grid.setVgap(0);
////    grid.setHgap(0);
//    GridPane.setConstraints(this.label, 2, 0);
//    GridPane.setConstraints(this.button, 0, 0);
//    GridPane.setConstraints(this.button2, 1, 0);
//    GridPane.setConstraints(grid, 0, 3);
//
//    grid.getChildren().addAll(
//        this.label,
//        this.button,
//        this.button2
//    );
//
//    this.scene = new Scene(grid, 500, 300);
//
//    this.stage.setTitle("This is my title");
//    this.stage.setScene(scene);
//    this.stage.show();

    this.stage = primaryStage;

    update();
  }

  @Override
  public void handle(ActionEvent event) {
//    if (event.getSource() == this.button) {
//      System.out.println("button clicked");
//    }
//    this.controller.handle(event);
  }

  public void update() {

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
    }
  }

}
