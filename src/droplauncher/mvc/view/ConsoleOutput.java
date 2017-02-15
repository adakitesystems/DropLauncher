package droplauncher.mvc.view;

import adakite.util.AdakiteUtils;
import javafx.application.Platform;
import javafx.scene.control.TextArea;

/**
 * Container class for log output. Uses a
 * {@link javafx.scene.control.TextArea} object as the destination.
 */
public class ConsoleOutput {

  private TextArea outputObject;
  private boolean printToStdout;

  public ConsoleOutput() {
    this.outputObject = new TextArea("");
    this.printToStdout = true;
  }

  /**
   * Returns the object to which data is printed.
   */
  public TextArea get() {
    return this.outputObject;
  }

  /**
   * Sets whether printed data should also be printed to STDOUT.
   */
  public void printToStdoutEnabled(boolean enabled) {
    this.printToStdout = enabled;
  }

  /**
   * Prints the specified string to the output object.
   *
   * @param str specified string
   * @param printToStdout whether to also print to STDOUT
   */
  public void print(String str, boolean printToStdout) {
    Platform.runLater(() -> {
      this.outputObject.appendText(str);
    });
    if (printToStdout) {
      System.out.print(str);
    }
  }

  /**
   * @see #print(java.lang.String, boolean)
   * @see #printToStdoutEnabled(boolean)
   */
  public void print(String str) {
    print(str, this.printToStdout);
  }

  /**
   * @see #print(java.lang.String, boolean)
   * @see #printToStdoutEnabled(boolean)
   */
  public void println(String line) {
    line += AdakiteUtils.newline();
    print(line);
  }

}
