package droplauncher.util;

import adakite.util.AdakiteUtils;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.scene.control.TextArea;

/**
 * Class for ignoring output from an input stream.
 */
public class StreamGobbler extends Thread {

  private static final Logger LOGGER = Logger.getLogger(StreamGobbler.class.getName());
  private static final boolean CLASS_DEBUG = (Constants.DEBUG && true);

  private InputStream inputStream;
  private TextArea txtLogWindow;
  private String streamName;
  private String line;

  private StreamGobbler() {}

  public StreamGobbler(String streamName, InputStream inputStream, TextArea textArea) {
    this.streamName = streamName;
    this.inputStream = inputStream;
    this.txtLogWindow = textArea;
    this.line = "";
  }

  @Override
  public void run() {
    try {
      BufferedReader br = new BufferedReader(new InputStreamReader(this.inputStream));
      while ((this.line = br.readLine()) != null) {
        if (this.line.startsWith("fps: ")) {
          continue;
        }
        if (!AdakiteUtils.isNullOrEmpty(this.streamName)) {
          this.line = this.streamName + ": " + line;
        }
        if (this.txtLogWindow != null && this.txtLogWindow.isVisible()) {
          Platform.runLater(() -> {
            this.txtLogWindow.appendText(Util.newline() + this.line);
          });
        }
        System.out.println(this.line);
      }
    } catch (Exception ex) {
      if (CLASS_DEBUG) {
        LOGGER.log(Constants.DEFAULT_LOG_LEVEL, null, ex);
      }
    }
  }

}