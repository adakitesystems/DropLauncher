package droplauncher.util;

import adakite.util.AdakiteUtils;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Class for consuming output from an input stream.
 */
public class StreamGobbler extends Thread {

  private static final Logger LOGGER = LogManager.getLogger();

  private InputStream inputStream;
  private String streamName;
  private String line;

  private StreamGobbler() {}

  public StreamGobbler(String streamName, InputStream inputStream) {
    this.streamName = streamName;
    this.inputStream = inputStream;
    this.line = "";
  }

  //TODO: How do we know if this failed? Redesign StreamGobbler implementation?
  @Override
  public void run() {
    try {
      BufferedReader br = new BufferedReader(new InputStreamReader(this.inputStream, StandardCharsets.UTF_8));
      while ((this.line = br.readLine()) != null) {
        if (this.line.startsWith("fps: ")) {
          continue;
        }
        if (!AdakiteUtils.isNullOrEmpty(this.streamName)) {
          this.line = this.streamName + ": " + line;
        }
      }
    } catch (Exception ex) {
      LOGGER.error(ex);
    }
  }

}
