package droplauncher.mvc.view;

import adakite.util.AdakiteUtils;
import java.util.ArrayList;
import javafx.application.Platform;
import javafx.scene.control.TextArea;

/**
 * Container class for log output. Uses a
 * {@link javafx.scene.control.TextArea} object as the destination.
 */
public class ConsoleOutput {

  private TextArea outputObject;
  private boolean printToStdout;
  private ArrayList<String> blacklist; /* lines to be ignored which contain this text */

  public ConsoleOutput() {
    this.outputObject = new TextArea("");
    this.printToStdout = true;
    this.blacklist = new ArrayList<>();
  }

  /**
   * Returns the object to which data is printed.
   */
  public TextArea get() {
    return this.outputObject;
  }

  /**
   * Returns the list of strings which indicate that a line containing
   * these strings should be ignored.
   */
  public ArrayList<String> getBlacklist() {
    return this.blacklist;
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
    for (String item : this.blacklist) {
      if (str.contains(item)) {
        return;
      }
    }
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
