package droplauncher.mvc;

import droplauncher.bwheadless.BwHeadless;
import java.io.File;

public class Model {

  private Controller controller;

  private BwHeadless bwheadless;

  public Model() {
    this.bwheadless = new BwHeadless();
  }

  public void setControler(Controller controller) {
    this.controller = controller;
  }

  public void filesDropped(File[] files) {
    for (File file : files) {
      System.out.println("It works! -> " + file.getAbsolutePath());
    }
  }

}
