package droplauncher.main;

import droplauncher.mvc.MVC;
import droplauncher.util.Constants;
import javafx.application.Application;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Main extends Application {

  private static final Logger LOGGER = LogManager.getLogger();

  public static void main(String[] args) {
    System.out.println(Constants.PROGRAM_ABOUT);

    LOGGER.info("start of main");
    launch(args);
    LOGGER.info("end of main");
  }

  @Override
  public void start(Stage stage) throws Exception {
    MVC mvc = new MVC();
    mvc.start(stage);
  }

}
