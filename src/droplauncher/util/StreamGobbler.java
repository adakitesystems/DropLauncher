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

  private InputStream is;
  private BufferedReader br;

  private TextArea txtLogWindow;

  private StreamGobbler() {}

  public StreamGobbler(InputStream is, TextArea ta) {
    this.is = is;
    this.txtLogWindow = ta;
  }

  public StreamGobbler(InputStream is) {
    this.is = is;
    this.txtLogWindow = null;
  }

  @Override
  public void run() {
    try {
      this.br = new BufferedReader(new InputStreamReader(this.is));
      String line;
      while ((line = this.br.readLine()) != null) {
        if (line.startsWith("fps: ")) {
          continue;
        }
        if (this.txtLogWindow != null && this.txtLogWindow.isVisible()) {
          this.txtLogWindow.appendText(AdakiteUtils.NEWLINE + line);
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