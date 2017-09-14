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

package droplauncher.mvc.view;

import adakite.exception.InvalidStateException;
import adakite.util.AdakiteUtils;
import adakite.windows.exception.WindowsException;
import droplauncher.DropLauncher;
import droplauncher.mvc.controller.ControllerWrapper;
import droplauncher.mvc.model.Model;
import java.util.ArrayList;
import java.util.List;
import javafx.application.Platform;
import javafx.scene.control.TextArea;

/**
 * Container class for log output. Uses a
 * {@link javafx.scene.control.TextArea} object as the destination.
 */
public class ConsoleOutput {

  private TextArea outputObject;
  private List<String> blacklist; /* lines to be ignored which contain this text */
  private ControllerWrapper controller;

  public ConsoleOutput() {
    this.outputObject = new TextArea("");
    this.blacklist = new ArrayList<>();
    this.controller = null;
  }

  public void setController(ControllerWrapper controller) {
    this.controller = controller;
  }

  /**
   * Returns the internal UI object to which the text is printed.
   */
  public TextArea get() {
    return this.outputObject;
  }

  /**
   * Clears the text.
   */
  public void clear() {
    this.outputObject.clear();
  }

  /**
   * Returns the list of strings which indicate that a line containing
   * these strings should be ignored.
   */
  public List<String> getBlacklist() {
    return this.blacklist;
  }

  /**
   * Prints the specified string to the output object.
   *
   * @param str specified string
   */
  public void print(String str) {
    for (String item : this.blacklist) {
      if (str.contains(item)) {
        return;
      }
    }

    /* Replace bwheadless.exe child process output prefix with bot module prefix. */
    if (str.startsWith(View.MessagePrefix.BWHEADLESS.get() + ":: ")) {
      int index = str.indexOf(":: ");
      str = str.substring(index + ":: ".length(), str.length());
      str = View.MessagePrefix.BOT.get() + str;
    }
    String message = str;

    if (AdakiteUtils.isNullOrEmpty(message)) {
      return;
    }

    Platform.runLater(() -> {
      this.outputObject.appendText(message);
    });

    /* Test for auto-eject option. */
    if ((message.startsWith(View.MessagePrefix.BWHEADLESS.get() + View.Message.GAME_HAS_ENDED.toString())
          || message.startsWith(View.MessagePrefix.BWHEADLESS.get() + View.Message.GAME_OVER.toString()))
        && Model.getSettings().hasValue(DropLauncher.PropertyKey.AUTO_EJECT_BOT.toString())
        && Model.getSettings().isEnabled(DropLauncher.PropertyKey.AUTO_EJECT_BOT.toString())) {
      try {
        Thread.sleep(Model.AUTO_EJECT_DELAY);
      } catch (InterruptedException ex) {
        /* Do nothing. */
      } catch (Exception ex) {
        Platform.runLater(() -> {
          new ExceptionAlert().showAndWait("something went wrong with the auto-eject timer", ex);
        });
      }
      try {
        this.controller.stopBWHeadless();
      } catch (Exception ex) {
        Platform.runLater(() -> {
          new ExceptionAlert().showAndWait("something went wrong with auto-ejecting the bot", ex);
        });
      }
    /* Test for auto-rejoin option. */
    } else if (message.startsWith(View.MessagePrefix.DROPLAUNCHER.get() + View.Message.BOT_EJECTED.toString())
        && Model.getSettings().hasValue(DropLauncher.PropertyKey.AUTO_BOT_REJOIN.toString())
        && Model.getSettings().isEnabled(DropLauncher.PropertyKey.AUTO_BOT_REJOIN.toString())) {
      try {
        Thread.sleep(Model.AUTO_REJOIN_DELAY);
      } catch (InterruptedException ex) {
        /* Do nothing. */
      } catch (Exception ex) {
        Platform.runLater(() -> {
          new ExceptionAlert().showAndWait("something went wrong with the auto-rejoin timer", ex);
        });
      }
      //TODO: Auto-rejoin fails if BWAPI.dll is unknown because displaying the
      //      YesNoDialog is not within the JavaFX thread when the call is invoked.
      //      Using "Platform.runLater" is a temporary fix for the issue.
      Platform.runLater(() -> {
        try {
          this.controller.startBWHeadless();
        } catch (Exception ex) {
          new ExceptionAlert().showAndWait("something went wrong with auto-rejoin", ex);
        }
      });
    /* Test for "failed to load module" error. */
    } else if (message.contains(View.Message.ERROR_126.toString())) {
      Platform.runLater(() -> {
        try {
          this.controller.stopBWHeadless();
        } catch (InvalidStateException ex) {
          /* Do nothing. */
          //TODO: Do something?
        }
        String exceptionMessage = WindowsException.SystemError.ERROR_MOD_NOT_FOUND.toString()
            + AdakiteUtils.newline(2)
            + "This error may be caused by missing Microsoft Visual C++ runtime files if you are trying to load BWAPI 4.2.0 or newer."
            + AdakiteUtils.newline(2)
            + "Please download and install the Microsoft Visual C++ 2015 Redistributable Package for your system.";
        new ExceptionAlert().showAndWait(exceptionMessage, new WindowsException(WindowsException.SystemError.ERROR_MOD_NOT_FOUND.toString()));
      });
    /* Test for "operation requires elevation" error. */
    } else if (message.contains(View.Message.ERROR_740.toString())) {
      Platform.runLater(() -> {
        try {
          this.controller.stopBWHeadless();
        } catch (InvalidStateException ex) {
          /* Do nothing. */
          //TODO: Do something?
        }
        String exceptionMessage = WindowsException.SystemError.ERROR_ELEVATION_REQUIRED.toString()
            + AdakiteUtils.newline(2)
            + "This error may be caused by trying to start StarCraft with administrative compatibility settings."
            + AdakiteUtils.newline(2)
            + "Please try removing all administrative compatibility and startup settings from StarCraft.exe.";
        new ExceptionAlert().showAndWait(exceptionMessage, new WindowsException(WindowsException.SystemError.ERROR_ELEVATION_REQUIRED.toString()));
      });
    }
  }

  /**
   * @see #print(java.lang.String, boolean)
   * @see #printToStdoutEnabled(boolean)
   */
  public void println(String line) {
    line += AdakiteUtils.newline();
    print(line);
  }

}
