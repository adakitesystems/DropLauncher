package droplauncher.mvc.view;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class CloseViewListener extends WindowAdapter {

  private View view;

  private CloseViewListener() {}

  public CloseViewListener(View view) {
    this.view = view;
  }

  @Override
  public void windowClosing(WindowEvent e) {
    this.view.closeView();
  }

}
