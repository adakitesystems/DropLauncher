/*
 * Copyright (C) 2017 Adakite
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package droplauncher.util.process;

import adakite.debugging.Debugging;
import adakite.util.AdakiteUtils;
import droplauncher.mvc.view.ConsoleOutputWrapper;
import droplauncher.util.StreamGobbler;
import droplauncher.util.process.exception.ClosePipeException;
import java.io.IOException;
import java.nio.file.Path;

/**
 * Container class for starting and stopping a new process.
 */
public class CustomProcess {

  private Process process;
  private Path cwd;
  private StreamGobbler gobblerStdout;
  private StreamGobbler gobblerStderr;
  private String processName;
  private ConsoleOutputWrapper consoleOutput;

  public CustomProcess() {
    this.process = null;
    this.cwd = null;
    this.gobblerStdout = null;
    this.gobblerStderr = null;
    this.processName = null;
    this.consoleOutput = null;
  }

  /**
   * Sets the current working directory for the executable.
   *
   * @param path specified current working directory
   */
  public CustomProcess setCWD(Path path) {
    this.cwd = path;
    return this;
  }

  /**
   * Sets the process name which will be prepended to any output from
   * the process.
   *
   * @param name specified process name
   */
  public CustomProcess setProcessName(String name) {
    this.processName = name;
    return this;
  }

  /**
   * Sets the UI control text object on which to display process output.
   *
   * @param consoleOutput specified UI control text object
   */
  public CustomProcess setConsoleOutput(ConsoleOutputWrapper consoleOutput) {
    this.consoleOutput = consoleOutput;
    return this;
  }

  /**
   * Creates and opens the pipe to the specified executable.
   *
   * @param args specified command and arguments to run
   * @throws IOException if an I/O error occurs
   */
  public void start(String[] args) throws IOException {
    if (args == null) {
      throw new IllegalArgumentException(Debugging.cannotBeNull("args"));
    }

    ProcessBuilder pb = new ProcessBuilder(args);
    if (this.cwd != null && AdakiteUtils.directoryExists(this.cwd)) {
      /* Set current working directory for the new process. */
      pb.directory(this.cwd.toFile());
    }

    this.process = pb.start();

    this.gobblerStdout = new StreamGobbler()
        .setInputStream(this.process.getInputStream())
        .setConsoleOutput(this.consoleOutput)
        .setStreamName(this.processName);
    this.gobblerStderr = new StreamGobbler()
        .setInputStream(this.process.getErrorStream())
        .setConsoleOutput(this.consoleOutput)
        .setStreamName(this.processName);
    this.gobblerStdout.start();
    this.gobblerStderr.start();
  }

  /**
   * Attempts to close the pipe.
   *
   * @throws ClosePipeException if {@link Process#isAlive()} returns true
   *     after attempting to destroy
   */
  public void stop() throws ClosePipeException {
    this.gobblerStdout.interrupt();
    this.gobblerStderr.interrupt();
    this.process.destroy();
    try {
      Thread.sleep(250);
    } catch (InterruptedException ex) {
      /* Do nothing. */
    }
    if (this.process.isAlive()) {
      throw new ClosePipeException("process is still alive after destroy attempt");
    }
  }

}
