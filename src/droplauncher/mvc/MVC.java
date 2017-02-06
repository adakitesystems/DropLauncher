package droplauncher.mvc;

import droplauncher.mvc.controller.Controller;
import droplauncher.mvc.model.Model;
import droplauncher.mvc.view.View;
import javafx.stage.Stage;

public class MVC {

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
  }

  public void start(Stage primaryStage) {
    this.view.start(primaryStage);
  }

}
