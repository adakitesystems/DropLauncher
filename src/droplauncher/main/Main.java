package droplauncher.main;

import droplauncher.mvc.MVC;
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

  public static void main(String[] args) {
    launch(args);
  }

  @Override
  public void start(Stage primaryStage) {
    MVC mvc = new MVC();
    mvc.start(primaryStage);
  }

}
