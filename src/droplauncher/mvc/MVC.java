package droplauncher.mvc;

public class MVC {

  private MVC() {
    /* Do nothing. */
  }

  public MVC(String[] args) {
    Model model = new Model();
    View view = new View();
    Controller controller = new Controller();

    controller.addModel(model);
    controller.addView(view);

    model.setController(controller);

    view.setController(controller);
  }

}
