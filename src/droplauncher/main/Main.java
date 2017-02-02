package droplauncher.main;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Main extends Application implements EventHandler<ActionEvent> {

  private Stage stage;
  private Scene scene;

  private Button button;
  private Button button2;

  public static void main(String[] args) {
//    MVC mvc = new MVC();

    launch(args);
  }

  @Override
  public void start(Stage primaryStage) {
    this.stage = primaryStage;

    this.button = new Button();
    this.button.setText("Button text here");
    this.button.setOnAction(this);

    this.button2 = new Button();
    this.button2.setText("Button text here again");
    this.button2.setOnAction(this);

//    VBox layout = new VBox(20);
//    layout.getChildren().addAll(this.button, this.button2);
//    scene = new Scene(layout, 500, 300);

    int val = 30;
    GridPane grid = new GridPane();
    grid.setPadding(new Insets(val, val, val, val));
//    grid.setVgap(0);
//    grid.setHgap(0);
    GridPane.setConstraints(this.button, 0, 0);
    GridPane.setConstraints(this.button2, 1, 0);
    GridPane.setConstraints(grid, 0, 3);

    grid.getChildren().addAll(this.button, this.button2);

    this.scene = new Scene(grid, 500, 300);

    this.stage.setTitle("This is my title");
    this.stage.setScene(scene);
    this.stage.show();
  }

  @Override
  public void handle(ActionEvent event) {
    if (event.getSource() == this.button) {
      System.out.println("button1 clicked");
    } else if (event.getSource() == this.button2) {
      System.out.println("button2 clicked");
    }
  }

}
