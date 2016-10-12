/* ProcessPipe.java */

/*
TODO:
- Test if generic Exception throws the specific exception when caught.
- Since the removal of "_isOpen", determine best way to check if
process is running... suggestion: ".isAlive()"?
*/

package droplauncher.tools;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Generic class for handling communication between the main program and
 * a process.
 *
 * @author Adakite Systems
 * @author adakitesystems@gmail.com
 */
public final class ProcessPipe {

  private static final Logger LOGGER = Logger.getLogger(ProcessPipe.class.getName());
  private static final boolean CLASS_DEBUG = (MainTools.DEBUG && true);

  public static final double DEFAULT_READ_TIMEOUT = (double)0.25; /* seconds */

  private String path;
  private String[] args;
  private Process process;
  private InputStream is;
  private BufferedReader br; /* read from process */
  private OutputStream os;
  private BufferedWriter bw; /* write to process */

  /**
   * Initialize class variables.
   */
  public ProcessPipe() {
    this.path = null;
    this.args = null;
    this.process = null;
    this.is = null;
    this.br = null;
    this.os = null;
    this.bw = null;
  }

  /**
   * Opens the pipe using the specified path and arguments. When specifying
   * arguments, each string should be standalone.
   * E.g. use args[0] = "--load" args[1] = "file.txt"
   * instead of args[0] = "--load file.txt". The args parameter can be null
   * in which case no arguments will be included during execution.
   *
   * @param path path to executable
   * @param args arguments excluding path to executable
   * @return
   *     true if pipe was opened succesfully,
   *     otherwise false
   */
  public boolean open(String path, String[] args) {
    if (MainTools.isEmpty(path)) {
      if (CLASS_DEBUG) {
        LOGGER.log(Level.SEVERE, MainTools.EMPTY_STRING);
      }
      return false;
    }

    try {
      String[] command;
      this.path = path;

      if (args == null) {
        command = new String[1];
      } else {
        this.args = Arrays.copyOf(args, args.length);
        command = new String[1 + this.args.length];
        System.arraycopy(this.args, 0, command, 1, this.args.length);
      }

      command[0] = this.path;

      this.process = new ProcessBuilder(command).start();

      this.is = this.process.getInputStream();
      this.br = new BufferedReader(new InputStreamReader(this.is));

      this.os = this.process.getOutputStream();
      this.bw = new BufferedWriter(new OutputStreamWriter(this.os));

      return true;
    } catch (NullPointerException | IOException ex) {
      if (CLASS_DEBUG) {
        LOGGER.log(Level.SEVERE, null, ex);
      }
      return false;
    } catch (Exception ex) {
      if (CLASS_DEBUG) {
        LOGGER.log(Level.SEVERE, null, ex);
      }
      return false;
    }
  }

  /**
   * Closes the pipe and destroys the process.
   *
   * @return
   *     true if all pipe-related objects were closed and no errors
   *         were encountered,
   *     otherwise false
   */
  public boolean close() {
    try {
      this.br.close();
      this.is.close();
      this.bw.close();
      this.os.close();
      this.process.destroy();
      return true;
    } catch (IOException ex) {
      if (CLASS_DEBUG) {
        LOGGER.log(Level.SEVERE, "encountered while closing pipe", ex);
      }
      return false;
    }
  }

  /**
   * Calls {@link #open(java.lang.String, java.lang.String[])}
   * with the stored path and args.
   *
   * @return value returned from {@link #open(java.lang.String, java.lang.String[])}.
   */
  public boolean reopen() {
    return open(this.path,this.args);
  }

  /**
   * Returns previously specified path of the executable file.
   */
  public String getPath() {
    /* Validate class variables. */
    if (MainTools.isEmpty(this.path)) {
      if (CLASS_DEBUG) {
        LOGGER.log(Level.WARNING, MainTools.EMPTY_STRING);
      }
      return null;
    }
    return this.path;
  }

  /*
  TODO: this may need to be changed.
  */
  /**
   * Keeps checking the pipe's buffer status for a specified
   * amount of seconds. Uses a while() loop and values returned from
   * System.currentTimeMillis().
   *
   * @param seconds maximum wait time given in seconds
   * @return
   *     true if pipe's buffer is ready for reading and no errors
   *         were encountered,
   *     otherwise false
   */
  public boolean isInputReady(double seconds) {
    /* Validate parameters. */
    if (Double.compare(seconds, (double)0) < 0) {
      if (CLASS_DEBUG) {
        LOGGER.log(Level.WARNING, "negative value for seconds parameter");
      }
      return isInputReady((double)0);
    }

    long currentTime = System.currentTimeMillis();
    long millisec = (long)(seconds * (double)1000);
    long endTime = currentTime + millisec;

    /* Keep checking pipe's buffer. */
    while (currentTime <= endTime) {
      try {
        if (this.br.ready()) {
          return true;
        }
      } catch (IOException ex) {
        if (CLASS_DEBUG) {
          LOGGER.log(Level.SEVERE, null, ex);
        }
      }
      currentTime = System.currentTimeMillis();
    }

    return false;
  }

  /**
   * Reads a line from the input stream of the pipe.
   *
   * @return
   *     the line read from the pipe excluding terminating newline characters,
   *     otherwise null if nothing was read or errors were encountered
   */
  public String readLine() {
    /* Read from pipe. */
    try {
      if (this.br.ready()) {
        String line = this.br.readLine();
        if (CLASS_DEBUG && line != null && line.isEmpty()) {
          /* A non-null empty string will not break the program.
             This logging event was added in curiosity of such
             occurences and their frequency. */
          LOGGER.log(Level.WARNING, "non-null empty string read from pipe");
        }
        return line;
      } else {
        return null;
      }
    } catch (IOException ex) {
      if (CLASS_DEBUG) {
        LOGGER.log(Level.SEVERE, null, ex);
      }
      return null;
    }
  }

  /**
   * Writes the specified string to the output stream of the pipe.
   *
   * @param str output string
   * @return
   *     true if write was successful,
   *     otherwise false
   */
  public boolean write(String str) {
    /* Validate parameters. */
    if (MainTools.isEmpty(str)) {
      if (CLASS_DEBUG) {
        LOGGER.log(Level.WARNING, MainTools.EMPTY_STRING);
      }
      return false;
    }

    /* Write to pipe. */
    try {
      this.bw.write(str);
      this.bw.flush();
    } catch (IOException ex) {
      if (CLASS_DEBUG) {
        LOGGER.log(Level.SEVERE, "encountered while writing to pipe", ex);
      }
      return false;
    }

    return true;
  }

  /**
   * Writes the specified string to the output stream of the pipe and
   * terminates with the '\n' character.
   *
   * @param str specified string to write
   * @return
   *     true if write was successful,
   *     otherwise false
   */
  public boolean writeLine(String str) {
    /* Usually, communication with processes involve terminating a line
       with the '\n' character. */
    return write(str + "\n");
  }

  /**
   * Writes the specified string to the output stream of the pipe with
   * a custom terminating string.
   *
   * @param str specified string to write
   * @param terminator custom string terminator
   * @return
   *     true if write was successful,
   *     otherwise false
   */
  public boolean writeLine(String str, String terminator) {
    return write(str + terminator);
  }

  /**
   * Flushes the pipe's output stream by reading then ignoring
   * data from the stream until {@link #isInputReady(double)} is false.
   *
   * @param print whether or not to print pipe's output to console
   *     after reading
   */
  public void flushRead(boolean print) {
    /* Flush input. */
    try {
      String line;
      while (isInputReady(DEFAULT_READ_TIMEOUT)) {
        line = this.br.readLine();
        if (print && !MainTools.isEmpty(line)) {
          System.out.println(line);
        }
      }
    } catch (IOException ex) {
      if (CLASS_DEBUG) {
        LOGGER.log(Level.SEVERE, "encountered while reading from pipe", ex);
      }
    }
  }

  /**
   * Flushes the pipe's output stream by reading then ignoring
   * data from the stream until ProcessPipe.isInputReady() is false.
   */
  public void flushRead() {
    flushRead(false);
  }
}
