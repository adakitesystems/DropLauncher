package droplauncher.util;

import droplauncher.mvc.MainWindow;
import droplauncher.mvc.Model;
import filedrop.FileDrop;

import java.io.File;

public class FileDropListener implements FileDrop.Listener {

  private Model model;

  public FileDropListener(MainWindow view) {
    this.model = view.getModel();
  }

  @Override
  public void filesDropped(File[] files) {
    this.model.filesDropped(files);
  }

}
