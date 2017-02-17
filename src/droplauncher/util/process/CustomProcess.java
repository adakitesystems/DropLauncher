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
import droplauncher.mvc.view.ConsoleOutput;
import droplauncher.util.StreamGobbler;
import java.io.IOException;
import java.nio.file.Path;
import java.util.logging.Logger;

/**
 * Container class for starting and stopping a new process.
 */
public class CustomProcess {

  private static final Logger LOGGER = Logger.getLogger(CustomProcess.class.getName());

  private Process process;
  private Path cwd;
  private StreamGobbler gobblerStdout;
  private StreamGobbler gobblerStderr;
  private String processName;

  public CustomProcess() {
    this.process = null;
    this.cwd = null;
    this.gobblerStdout = null;
    this.gobblerStderr = null;
    this.processName = null;
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

  public CustomProcess setProcessName(String name) {
    this.processName = name;
    return this;
  }

  /**
   * Creates and opens the pipe to the specified executable.
   *
   * @param args specified command and arguments to run
   * @param co specified ConsoleOutput to display process output stream
   * @throws IOException if an I/O error occurs
   */
  public void start(String[] args, ConsoleOutput co) throws IOException {
    if (args == null) {
      throw new IllegalArgumentException(Debugging.nullObject("args"));
    }

    ProcessBuilder pb = new ProcessBuilder(args);
    if (this.cwd != null && AdakiteUtils.directoryExists(this.cwd)) {
      /* Set current working directory for the new process. */
      pb.directory(this.cwd.toFile());
    }

    this.process = pb.start();

    this.gobblerStdout = new StreamGobbler()
        .setInputStream(this.process.getInputStream())
        .setConsoleOutput(co)
        .setStreamName(this.processName);
    this.gobblerStderr = new StreamGobbler()
        .setInputStream(this.process.getErrorStream())
        .setConsoleOutput(co)
        .setStreamName(this.processName);
    this.gobblerStdout.start();
    this.gobblerStderr.start();
  }

  /**
   * Attempts to close the pipe.
   */
  public void stop() {
    this.gobblerStdout.interrupt();
    this.gobblerStdout.interrupt();
    this.process.destroy();
    if (this.process.isAlive()) {
      LOGGER.log(Debugging.getLogLevel(), "process is still alive after destroy attempt");
    }
  }

}
