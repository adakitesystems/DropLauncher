package droplauncher.mvc.view;

import adakite.util.AdakiteUtils;
import javafx.application.Platform;
import javafx.scene.control.TextArea;

/**
 * Container class for log output.
 */
public class Console {

  private TextArea obj;

  private Console() {}

  public Console(TextArea textArea) {
    this.obj = textArea;
  }

  public TextArea get() {
    return this.obj;
  }

  public void writeln(String line) {
    Platform.runLater(() -> {
      this.obj.appendText(AdakiteUtils.newline() + line);
    });
  }

}
