package droplauncher.util;

import adakite.util.AdakiteUtils;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Class for consuming output from an input stream.
 */
public class StreamGobbler extends Thread {

  private static final Logger LOGGER = LogManager.getLogger();

  private InputStream inputStream;
  private OutputStream outputStream;

  private StreamGobbler() {}

  public StreamGobbler(InputStream inputStream) {
    this.inputStream = inputStream;
    this.outputStream = null;
  }

  public StreamGobbler(InputStream inputStream, OutputStream outputStream) {
    this.inputStream = inputStream;
    this.outputStream = outputStream;
  }

  //TODO: Check if thread failed and provide some indication other than throwing an error to the log.
  @Override
  public void run() {
    try {
      BufferedReader br = new BufferedReader(new InputStreamReader(this.inputStream, StandardCharsets.UTF_8));
      BufferedWriter bw = null;
      if (this.outputStream != null) {
        bw = new BufferedWriter(new OutputStreamWriter(this.outputStream, StandardCharsets.UTF_8));
      }
      String line;
      while ((line = br.readLine()) != null) {
//        if (this.line.startsWith("fps: ")) {
//          continue;
//        }
        System.out.println(line);
        if (bw != null) {
          bw.write(line + AdakiteUtils.newline());
          bw.flush();
        }
      }
    } catch (Exception ex) {
      LOGGER.error(ex);
    }
  }

}
