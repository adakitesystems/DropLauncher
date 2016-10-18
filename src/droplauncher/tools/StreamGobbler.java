package droplauncher.tools;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class StreamGobbler extends Thread {

  private static final Logger LOGGER = LogManager.getRootLogger();

  private InputStream is;
  private BufferedReader br;

  private StreamGobbler() {

  }

  public StreamGobbler(InputStream is) {
    this.is = is;
  }

  @Override
  public void run() {
    try {
      br = new BufferedReader(new InputStreamReader(is));
      String line;
      while ((line = br.readLine()) != null) {
        System.out.println(line);
      }
    } catch (Exception ex) {
      LOGGER.error(ex.getMessage(), ex);
    }
  }

}