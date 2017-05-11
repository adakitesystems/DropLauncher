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

import adakite.util.AdakiteUtils;
import droplauncher.DropLauncher;
import droplauncher.mvc.controller.ControllerDAO;
import droplauncher.mvc.model.Model;
import java.util.ArrayList;
import javafx.application.Platform;
import javafx.scene.control.TextArea;

/**
 * Container class for log output. Uses a
 * {@link javafx.scene.control.TextArea} object as the destination.
 */
public class ConsoleOutput {

  private TextArea outputObject;
  private ArrayList<String> blacklist; /* lines to be ignored which contain this text */
  private ControllerDAO controller;

  public ConsoleOutput() {
    this.outputObject = new TextArea("");
    this.blacklist = new ArrayList<>();
    this.controller = null;
  }

  public void setController(ControllerDAO controller) {
    this.controller = controller;
  }

  /**
   * Returns the internal UI object to which data is printed.
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
  public ArrayList<String> getBlacklist() {
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

    if (message.startsWith(View.MessagePrefix.BWHEADLESS.get() + View.Message.GAME_HAS_ENDED.toString())
        && Model.hasPrefValue(DropLauncher.Property.AUTO_EJECT_BOT.toString())
        && Model.isPrefEnabled(DropLauncher.Property.AUTO_EJECT_BOT.toString())) {
      try {
        Thread.sleep(Model.AUTO_EJECT_DELAY);
        this.controller.btnStartClicked();
      } catch (Exception ex) {
        Platform.runLater(() -> {
          new ExceptionAlert().showAndWait("something went wrong with auto-ejecting the bot", ex);
        });
      }
    } else if (message.startsWith(View.MessagePrefix.DROPLAUNCHER.get() + View.Message.BOT_EJECTED.toString())
        && Model.hasPrefValue(DropLauncher.Property.AUTO_BOT_REJOIN.toString())
        && Model.isPrefEnabled(DropLauncher.Property.AUTO_BOT_REJOIN.toString())) {
      try {
        Thread.sleep(Model.AUTO_REJOIN_DELAY);
        this.controller.btnStartClicked();
      } catch (Exception ex) {
        Platform.runLater(() -> {
          new ExceptionAlert().showAndWait("something went wrong with auto-rejoin", ex);
        });
      }
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
