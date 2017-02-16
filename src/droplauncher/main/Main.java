package droplauncher.main;

import droplauncher.mvc.MVC;
import droplauncher.util.Constants;
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

  public static void main(String[] args) {
    System.out.println(Constants.PROGRAM_ABOUT);

    launch(args);
  }

  @Override
  public void start(Stage stage) throws Exception {
    MVC mvc = new MVC();
    mvc.start(stage);
  }

}
