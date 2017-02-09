package droplauncher.util;

import adakite.util.AdakiteUtils;
import droplauncher.mvc.view.ConsoleOutput;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Class for consuming output from an input stream.
 */
public class StreamGobbler extends Thread {

  private static final Logger LOGGER = LogManager.getLogger();

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
      LOGGER.error(ex);
    }
  }

}
