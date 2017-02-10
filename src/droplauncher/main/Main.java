package droplauncher.main;

import droplauncher.mvc.MVC;
import javafx.application.Application;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Main extends Application {

  private static final Logger LOGGER = LogManager.getLogger();

  public static void main(String[] args) {
    LOGGER.info("start of main");
    /* Try-catch for sending all exceptions to log4j2. */
    try {
      launch(args);
    } catch (Exception ex) {
      LOGGER.error("droplauncher", ex);
    }
    LOGGER.info("end of main");
  }

  @Override
  public void start(Stage primaryStage) {
    MVC mvc = new MVC();
    mvc.start(primaryStage);
  }

}
