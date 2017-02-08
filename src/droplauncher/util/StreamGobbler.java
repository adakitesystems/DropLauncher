package droplauncher.util;

import adakite.util.AdakiteUtils;
import droplauncher.mvc.view.ConsoleOutput;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Logger;

/**
 * Class for consuming output from an input stream.
 */
public class StreamGobbler extends Thread {

  private static final Logger LOGGER = Logger.getLogger(StreamGobbler.class.getName());
  private static final boolean DEBUG_CLASS = (Debugging.isEnabled() && true);

  private InputStream inputStream;
  private ConsoleOutput consoleOutput;
  private String streamName;
  private String line;

  private StreamGobbler() {}

  public StreamGobbler(String streamName, InputStream inputStream, ConsoleOutput co) {
    this.streamName = streamName;
    this.inputStream = inputStream;
    this.consoleOutput = co;
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
        if (this.consoleOutput != null) {
          this.consoleOutput.println(line);
        }
      }
    } catch (Exception ex) {
      if (DEBUG_CLASS) {
        LOGGER.log(Debugging.getLoggerLevel(), null, ex);
      }
    }
  }

}
