/* FileDropListListener.java */

package droplauncher.filedroplist;

import droplauncher.bwheadless.BwHeadless;

import filedrop.FileDrop;

import java.io.File;

public class FileDropListener implements FileDrop.Listener {

  private BwHeadless bwh;

  public FileDropListener(BwHeadless bwh) {
    this.bwh = bwh;
  }

  @Override
  public void filesDropped(File[] files) {
    for (File file : files) {
      bwh.readDroppedFiles();
      FileDropList.INSTANCE.add(file);
    }
  }


}
