package droplauncher.mvc.view;

import adakite.util.AdakiteUtils;
import javafx.application.Platform;
import javafx.scene.control.TextArea;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Container class for log output. Uses a
 * {@link javafx.scene.control.TextArea} object as the destination.
 */
public class ConsoleOutput {

  private static final Logger LOGGER = LogManager.getLogger();

  private TextArea obj;
  private boolean printToStdout;

  public ConsoleOutput() {
    this.obj = new TextArea("");
    this.obj.setEditable(false);
    this.printToStdout = true;
  }

  /**
   * Returns the object to which data is printed.
   */
  public TextArea get() {
    return this.obj;
  }

  /**
   * Sets whether printed data should also be printed to STDOUT.
   */
  public void setPrintToStdout(boolean enabled) {
    this.printToStdout = enabled;
  }

  /**
   * Prints the specified string to the output object.
   *
   * @param str specified string
   * @param writeToStdout whether to also print to STDOUT
   */
  public void print(String str, boolean writeToStdout) {
    Platform.runLater(() -> {
      this.obj.appendText(str);
    });
    if (writeToStdout) {
      System.out.print(str);
    }
  }

  /**
   * @see #print(java.lang.String, boolean)
   * @see #setPrintToStdout(boolean)
   */
  public void print(String str) {
    print(str, this.printToStdout);
  }

  /**
   * @see #print(java.lang.String, boolean)
   * @see #setPrintToStdout(boolean)
   */
  public void println(String line) {
    line += AdakiteUtils.newline();
    print(line);
  }

}
