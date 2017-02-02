package droplauncher.main;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
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

    VBox layout = new VBox(20);
    layout.getChildren().addAll(this.button, this.button2);
    scene = new Scene(layout, 500, 300);

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
