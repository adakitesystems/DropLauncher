package droplauncher.mvc;

import droplauncher.mvc.controller.Controller;
import droplauncher.mvc.model.Model;

public class MVC {

  private Controller controller;
  private Model model;

  public MVC() {
    this.controller = new Controller();
    this.model = new Model();

    this.controller.setModel(this.model);
  }

}
