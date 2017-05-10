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

package droplauncher.util;

import adakite.util.AdakiteUtils;
import droplauncher.mvc.view.ConsoleOutput;
import droplauncher.mvc.view.ExceptionAlert;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import javafx.application.Platform;

/**
 * Class for consuming output from an input stream.
 */
public class StreamGobbler extends Thread {

  private InputStream inputStream;
  private String line;
  private ConsoleOutput consoleOutput;
  private String streamName;

  public StreamGobbler() {
    this.inputStream = null;
    this.line = "";
    this.consoleOutput = null;
    this.streamName = null;
  }

  public StreamGobbler setInputStream(InputStream is) {
    this.inputStream = is;
    return this;
  }

  public StreamGobbler setConsoleOutput(ConsoleOutput co) {
    this.consoleOutput = co;
    return this;
  }

  public StreamGobbler setStreamName(String name) {
    this.streamName = name;
    return this;
  }

  /*
  TODO: Check if thread failed and provide some indication other than throwing an error to the log.
        Update: Now displaying an exception dialog.
  */
  @Override
  public void run() {
    try {
      BufferedReader br = new BufferedReader(new InputStreamReader(this.inputStream, StandardCharsets.UTF_8));
      while ((this.line = br.readLine()) != null) {
        if (this.consoleOutput != null) {
          if (!AdakiteUtils.isNullOrEmpty(this.streamName)) {
            this.line = this.streamName + ": " + this.line;
          }
          this.consoleOutput.println(this.line);
        }
      }
    } catch (Exception ex) {
      Platform.runLater(() -> {
        new ExceptionAlert().showAndWait(null, ex);
      });
    }
  }

}
