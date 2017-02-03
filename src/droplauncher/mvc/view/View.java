package droplauncher.mvc.view;

import adakite.utils.AdakiteUtils;
import droplauncher.bwheadless.BWHeadless;
import droplauncher.mvc.controller.Controller;
import droplauncher.starcraft.Race;
import droplauncher.starcraft.Starcraft;
import droplauncher.util.Constants;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class View {

  private Controller controller;

  private Stage stage;
  private Scene scene;

  private Label lblBwapiVersion;
  private Label lblBwapiVersionText;
  private Label lblBotFile;
  private Label lblBotFileText;
  private Label lblBotName;
  private TextField txtBotName;
  private ChoiceBox<String> cbRace;
  private Button btnLaunch;
  private TextField txtDropArea;

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
    this.lblBwapiVersionText = new Label("-");
    this.lblBotFile = new Label("Bot File:");
    this.lblBotFileText = new Label("-");
    this.lblBotName = new Label("Bot name (max 24 characaters):");
    this.txtBotName = new TextField("");
    this.cbRace = new ChoiceBox<>();
    this.cbRace.getItems().add(Race.TERRAN.toString());
    this.cbRace.getItems().add(Race.ZERG.toString());
    this.cbRace.getItems().add(Race.PROTOSS.toString());
    this.cbRace.getItems().add(Race.RANDOM.toString());
    this.btnLaunch = new Button(LaunchButtonText.LAUNCH.toString());
    this.btnLaunch.setMinWidth(200);
    this.btnLaunch.setMinHeight(40);
    this.txtDropArea = new TextField();
    this.txtDropArea.setMinWidth(150);
    this.txtDropArea.setMinHeight(150);

    this.txtBotName.setOnKeyReleased(e -> {
      this.controller.botNameChanged(this.txtBotName.getText());
    });
    this.cbRace.setOnAction(e -> {
      this.controller.botRaceChanged(this.cbRace.getValue());
    });
    this.btnLaunch.setOnAction(e -> {
      this.controller.launchButtonPressed();
    });

    CustomGridPane fileGridPane = new CustomGridPane();
    fileGridPane.add(this.lblBotFile);
    fileGridPane.add(this.lblBotFileText, true);
    fileGridPane.add(this.lblBwapiVersion);
    fileGridPane.add(this.lblBwapiVersionText, true);
    fileGridPane.setGaps(GAP, GAP);
    fileGridPane.pack();

    CustomGridPane botNameGridPane = new CustomGridPane();
    botNameGridPane.add(this.lblBotName, true);
    botNameGridPane.add(this.txtBotName, true);
    botNameGridPane.setGaps(2, 2);
    botNameGridPane.pack();

    CustomGridPane botGridPane = new CustomGridPane();
    botGridPane.add(botNameGridPane.get(), true);
    botGridPane.add(this.cbRace, true);
    botGridPane.add(this.btnLaunch, true);
    botGridPane.setGaps(GAP, GAP);
    botGridPane.pack();

    CustomGridPane txtDropGridPane = new CustomGridPane();
    txtDropGridPane.add(this.txtDropArea, true);
    txtDropGridPane.setGaps(GAP, GAP);
    txtDropGridPane.pack();

    CustomGridPane botAreaGridPane = new CustomGridPane();
    botAreaGridPane.add(txtDropGridPane.get());
    botAreaGridPane.add(botGridPane.get());
    botAreaGridPane.setGaps(GAP, GAP);
    botAreaGridPane.pack();

    CustomGridPane mainGridPane = new CustomGridPane();
    mainGridPane.add(fileGridPane.get(), true);
    mainGridPane.add(botAreaGridPane.get(), true);
    mainGridPane.get().setPadding(new Insets(TOP_PADDING, LEFT_PADDING, BOTTOM_PADDING, RIGHT_PADDING));
    mainGridPane.setGaps(GAP, GAP);
    mainGridPane.pack();

    this.scene = new Scene(mainGridPane.get());

    this.stage.setResizable(false);
    this.stage.setTitle(Constants.PROGRAM_TITLE);
    this.stage.setScene(this.scene);
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

}
