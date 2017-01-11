package droplauncher.mvc;

/**
 * Modified MVC design pattern without the Controller.
 * The Model has access to the View and the View has access to the Model.
 * For the most part, the View just displays internal variables of the Model.
 */
public class MVC {

  Model model;
  View view;

  public MVC() {
    init();
  }

  public MVC(String[] args) {
    init();
  }

  private void init() {
    this.model = new Model();
    this.view = new View();

    view.setModel(model);
    model.setView(view);
  }

}
