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
import adakite.util.AdakiteUtils.StringCompareOption;
import droplauncher.mvc.view.ConsoleOutputWrapper;
import droplauncher.mvc.view.ExceptionAlert;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import javafx.application.Platform;

/**
 * Class for consuming output from an input stream.
 */
public class CustomStreamGobbler implements Runnable {

  private InputStream inputStream;
  private ConsoleOutputWrapper consoleOutput;
  private String streamName;

  private CustomStreamGobbler() {}

  public CustomStreamGobbler(InputStream inputStream) {
    this.inputStream = inputStream;
    this.consoleOutput = null;
    this.streamName = null;
  }

  public CustomStreamGobbler setConsoleOutput(ConsoleOutputWrapper consoleOutput) {
    this.consoleOutput = consoleOutput;
    return this;
  }

  public CustomStreamGobbler setStreamName(String name) {
    this.streamName = name;
    return this;
  }

  @Override
  public void run() {
    try {
      BufferedReader br = new BufferedReader(new InputStreamReader(this.inputStream, StandardCharsets.UTF_8));
      String line;
      while ((line = br.readLine()) != null) {
        if (this.consoleOutput != null && !AdakiteUtils.isNullOrEmpty(line, StringCompareOption.TRIM)) {
          if (!AdakiteUtils.isNullOrEmpty(this.streamName, StringCompareOption.TRIM)) {
            line = this.streamName + ": " + line;
          }
          /* Redirect output. */
          this.consoleOutput.println(line);
        }
        if (Thread.interrupted()) {
//          this.inputStream.close();
//          br.close();
          break;
        }
      }
    } catch (Exception ex) {
      Platform.runLater(() -> {
        new ExceptionAlert().showAndWait(null, ex);
      });
    }
  }

}
