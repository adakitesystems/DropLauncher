package adakite.process;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class StreamGobbler {

  private static class StreamGobblerRunnable implements Runnable {

    private InputStream inputStream;
    private CopyOnWriteArrayList<String> gobbledOutput;

    private StreamGobblerRunnable() {}

    public StreamGobblerRunnable(InputStream inputStream) {
      this.inputStream = inputStream;
      this.gobbledOutput = new CopyOnWriteArrayList<>();
    }

    public List<String> getOutput() {
      List<String> ret = new ArrayList<>(this.gobbledOutput);
      return ret;
    }

    @Override
    public void run() {
      this.gobbledOutput.clear();

      try {
        BufferedReader br = new BufferedReader(new InputStreamReader(this.inputStream, StandardCharsets.UTF_8));
        String line;
        while ((line = br.readLine()) != null) {
          this.gobbledOutput.add(line);
        }
      } catch (Exception ex) {
        //TODO: Handle error.
      }
    }

  }

  private Thread thread;
  private StreamGobblerRunnable runnable;

  private StreamGobbler() {}

  public StreamGobbler(InputStream inputStream) {
    this.runnable = new StreamGobblerRunnable(inputStream);
  }

  public List<String> getCurrentOutput() {
    return this.runnable.getOutput();
  }

  public void interrupt() {
    this.thread.interrupt();
  }

  public boolean isInterrupted() {
    return this.thread.isInterrupted();
  }

  public boolean isAlive() {
    return this.thread.isAlive();
  }

  public void run() {
    this.thread = new Thread(this.runnable);
    this.thread.start();
  }

}
