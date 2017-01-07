package droplauncher.mvc;

public class MVC {

  private MVC() {
    /* Do nothing. */
  }

  public MVC(String[] args) {
    Model model = new Model();
    View view = new View();
    Controller controller = new Controller();

    controller.setModel(model);
    controller.setView(view);

    model.setControler(controller);

    view.setController(controller);
  }

}
