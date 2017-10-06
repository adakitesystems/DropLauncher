package adakite.process;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class StreamGobbler implements Runnable {

    private InputStream inputStream;
    private CopyOnWriteArrayList<String> gobbledOutput;

    private StreamGobbler() {}

    public StreamGobbler(InputStream inputStream) {
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
          if (Thread.interrupted()) {
//            this.inputStream.close();
//            br.close();
            break;
          }
        }
      } catch (Exception ex) {
        //TODO: Handle error.
      }
    }

}
