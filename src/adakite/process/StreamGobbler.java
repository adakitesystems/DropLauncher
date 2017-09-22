package adakite.process;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class StreamGobbler implements Runnable {

  private static class StreamGobblerThread extends Thread {

    private InputStream inputStream;
    private CopyOnWriteArrayList<String> gobbledOutput;

    private StreamGobblerThread() {}

    public StreamGobblerThread(InputStream inputStream) {
      this.inputStream = inputStream;
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
  private StreamGobblerThread gobblerThread;
  private InputStream inputStream;

  private StreamGobbler() {}

  public StreamGobbler(InputStream inputStream) {
    this.inputStream = inputStream;
  }

  public List<String> getCurrentOutput() {
    return this.gobblerThread.getOutput();
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

  @Override
  public void run() {
    this.gobblerThread = new StreamGobblerThread(this.inputStream);
    this.thread = new Thread(this.gobblerThread);
    this.thread.start();
  }

}
