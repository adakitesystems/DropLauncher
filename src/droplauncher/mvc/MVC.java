package droplauncher.mvc;

import adakite.debugging.Debugging;
import droplauncher.mvc.controller.Controller;
import droplauncher.mvc.model.Model;
import droplauncher.mvc.view.View;
import java.io.IOException;
import java.util.logging.Logger;
import javafx.stage.Stage;

public class MVC {

  private static final Logger LOGGER = Logger.getLogger(MVC.class.getName());

  private Controller controller;
  private Model model;
  private View view;

  public MVC() {
    this.controller = new Controller();
    this.model = new Model();
    this.view = new View();

    this.controller.setModel(this.model);
    this.controller.setView(this.view);

    this.view.setController(this.controller);

    try {
      this.model.setup();
    } catch (IOException ex) {
      LOGGER.log(Debugging.getLogLevel(), null, ex);
    }
  }

  public void start(Stage stage) {
    this.view.start(stage);
  }

}
