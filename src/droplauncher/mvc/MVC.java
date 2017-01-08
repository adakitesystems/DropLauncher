package droplauncher.mvc;

public class MVC {

  Model model;
  View view;
  Controller controller;

  public MVC() {
    init();
  }

  public MVC(String[] args) {
    init();
  }

  private void init() {
    this.model = new Model();
    this.view = new View();
    this.controller = new Controller();

    controller.addModel(model);
    controller.addView(view);

    model.setController(controller);

    view.setController(controller);
  }

}
