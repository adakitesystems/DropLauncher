package droplauncher.util;

import droplauncher.mvc.view.ConsoleOutput;
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
  private String line;
  private ConsoleOutput consoleOutput;

  public StreamGobbler() {
    this.inputStream = null;
    this.line = "";
    this.consoleOutput = null;
  }

  public StreamGobbler setInputStream(InputStream is) {
    this.inputStream = is;
    return this;
  }

  public StreamGobbler setConsoleOutput(ConsoleOutput co) {
    this.consoleOutput = co;
    return this;
  }

  //TODO: Check if thread failed and provide some indication other than throwing an error to the log.
  @Override
  public void run() {
    try {
      BufferedReader br = new BufferedReader(new InputStreamReader(this.inputStream, StandardCharsets.UTF_8));
      while ((this.line = br.readLine()) != null) {
        if (this.line.startsWith("fps: ")) {
          continue;
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
