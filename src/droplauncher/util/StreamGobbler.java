package droplauncher.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Logger;

public class StreamGobbler extends Thread {

  private static final Logger LOGGER = Logger.getLogger(StreamGobbler.class.getName());
  private static final boolean CLASS_DEBUG = (Constants.DEBUG && true);

  private InputStream is;
  private BufferedReader br;

  private StreamGobbler() {}

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
      LOGGER.log(Constants.DEFAULT_LOG_LEVEL, null, ex);
    }
  }

}