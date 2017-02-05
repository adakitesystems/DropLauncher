package droplauncher.util;

import adakite.util.AdakiteUtils;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Logger;
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

  private StreamGobbler() {}

  public StreamGobbler(String streamName, InputStream inputStream, TextArea textArea) {
    this.streamName = streamName;
    this.inputStream = inputStream;
    this.txtLogWindow = textArea;
  }

  @Override
  public void run() {
    try {
      BufferedReader br = new BufferedReader(new InputStreamReader(this.inputStream));
      String line;
      while ((line = br.readLine()) != null) {
        if (line.startsWith("fps: ")) {
          continue;
        }
        if (!AdakiteUtils.isNullOrEmpty(this.streamName)) {
          line = this.streamName + ": " + line;
        }
        if (this.txtLogWindow != null && this.txtLogWindow.isVisible()) {
          this.txtLogWindow.appendText(Util.newline() + line);
        } else {
          System.out.println(line);
        }
      }
    } catch (Exception ex) {
      if (CLASS_DEBUG) {
        LOGGER.log(Constants.DEFAULT_LOG_LEVEL, null, ex);
      }
    }
  }

}