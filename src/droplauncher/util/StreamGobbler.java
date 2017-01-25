package droplauncher.util;

import adakite.debugging.Debugging;
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
      this.br = new BufferedReader(new InputStreamReader(this.is));
      String line;
      while ((line = this.br.readLine()) != null) {
        System.out.println(line);
      }
    } catch (Exception ex) {
      LOGGER.log(Debugging.getLogLevel(), null, ex);
    }
  }

}