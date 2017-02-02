package droplauncher.mvc.view;

import filedrop.FileDrop;
import java.io.File;

/**
 * Listener class for FileDrop components.
 */
public class FileDropListener implements FileDrop.Listener {

  private View view;

  private FileDropListener() {}

  public FileDropListener(View view) {
    this.view = view;
  }

  @Override
  public void filesDropped(File[] files) {
//    this.view.filesDropped(files);
  }

}
