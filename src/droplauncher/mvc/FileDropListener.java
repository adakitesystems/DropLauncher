package droplauncher.mvc;

import filedrop.FileDrop;

import java.io.File;

public class FileDropListener implements FileDrop.Listener {

  private View view;

  public FileDropListener(View view) {
    this.view = view;
  }

  @Override
  public void filesDropped(File[] files) {
    this.view.filesDropped(files);
  }

}
