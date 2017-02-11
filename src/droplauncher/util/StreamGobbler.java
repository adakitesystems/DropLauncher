package droplauncher.util;

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

  private StreamGobbler() {}

  public StreamGobbler(InputStream inputStream) {
    this.inputStream = inputStream;
  }

  //TODO: Check if thread failed.
  @Override
  public void run() {
    try {
      BufferedReader br = new BufferedReader(new InputStreamReader(this.inputStream, StandardCharsets.UTF_8));
      String line;
      while ((line = br.readLine()) != null) {
//        if (this.line.startsWith("fps: ")) {
//          continue;
//        }
        System.out.println(line);
      }
    } catch (Exception ex) {
      LOGGER.error(ex);
    }
  }

}
