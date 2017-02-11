package droplauncher.util;

import adakite.debugging.Debugging;
import adakite.util.AdakiteUtils;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Arrays;

/**
 * Generic class for handling communication between the main program and
 * a process.
 */
public class ProcessPipe extends Thread {

  public static final double DEFAULT_READ_TIMEOUT = (double)0.25; /* seconds */

  private Path path;
  private String[] args;
  private Process process;
  private InputStream is;
  private BufferedReader br; /* read from process */
  private OutputStream os;
  private BufferedWriter bw; /* write to process */
  private StreamGobbler gobblerStdout;
  private StreamGobbler gobblerStderr;
  private Path cwd;

  public ProcessPipe() {
    this.path = null;
    this.args = null;
    this.process = null;
    this.is = null;
    this.br = null;
    this.os = null;
    this.bw = null;
    this.gobblerStdout = null;
    this.gobblerStderr = null;
    this.cwd = null;
  }

  public Path getCWD() {
    return this.cwd;
  }

  public void setCWD(Path path) {
    this.cwd = path;
  }

  /**
   * Open a pipe to the specified program.
   *
   * @param path specified program
   * @param args arguments to include during invocation
   * @throws UnsupportedEncodingException
   * @throws IOException
   * @throws IllegalArgumentException if Path argument is null
   */
  public void open(Path path, String[] args)
      throws UnsupportedEncodingException,
             IOException {
    if (path == null) {
      throw new IllegalArgumentException(Debugging.nullObject("path"));
    }

    String[] command;
    if (args == null) {
      command = new String[1];
    } else {
      this.args = Arrays.copyOf(args, args.length);
      command = new String[this.args.length + 1];
      System.arraycopy(this.args, 0, command, 1, this.args.length);
    }

    this.path = path;
    command[0] = this.path.toString();
    if (AdakiteUtils.directoryExists(this.cwd)) {
      /* Set current working directory for the new process. */
      ProcessBuilder pb = new ProcessBuilder(command);
      pb.directory(this.cwd.toFile());
      this.process = pb.start();
    } else {
      this.process = new ProcessBuilder(command).start();
    }

    this.is = this.process.getInputStream();
    this.br = new BufferedReader(new InputStreamReader(this.is, StandardCharsets.UTF_8));
    this.gobblerStdout = new StreamGobbler(this.process.getInputStream());
    this.gobblerStderr = new StreamGobbler(this.process.getErrorStream());
    this.gobblerStdout.start();
    this.gobblerStderr.start();

    this.os = this.process.getOutputStream();
    this.bw = new BufferedWriter(new OutputStreamWriter(this.os, StandardCharsets.UTF_8));
  }

  /**
   * Closes the pipe and destroys the process.
   */
  public void close() throws IOException {
    this.gobblerStderr.interrupt();
    this.gobblerStdout.interrupt();
    this.br.close();
    this.is.close();
    this.bw.close();
    this.os.close();
  }

  @Override
  public void run() {

  }

}
