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
import java.io.PrintWriter;
import java.io.StringWriter;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

public class ExceptionAlert {

  public ExceptionAlert() {
    /* Do nothing. */
  }

  /*
    Modified version of:
      http://code.makery.ch/blog/javafx-dialogs-official/
      Created by Marco Jakob - Licensed under Creative Commons Attribution 4.0
  */
  public void showAndWait(String message, Exception ex) {
    Alert alert = new Alert(AlertType.ERROR);
    alert.setTitle("An error has occurred");
    alert.setHeaderText(null);

    if (AdakiteUtils.isNullOrEmpty(message, true)
        && !AdakiteUtils.isNullOrEmpty(ex.getMessage())) {
      message = ex.getMessage();
    }
    if (!AdakiteUtils.isNullOrEmpty(message, true)) {
      alert.setContentText("Error: " + message);
    } else {
      alert.setContentText(null);
    }

    if (ex != null) {
      alert.setTitle("Exception Dialog");
      alert.setHeaderText("An error has occurred");
      StringWriter sw = new StringWriter();
      PrintWriter pw = new PrintWriter(sw);
      ex.printStackTrace(pw);
      String exceptionText = sw.toString();

      Label label = new Label("The exception stacktrace was:");

      TextArea textArea = new TextArea(exceptionText);
      textArea.setEditable(false);
      textArea.setWrapText(true);

      textArea.setMaxWidth(Double.MAX_VALUE);
      textArea.setMaxHeight(Double.MAX_VALUE);
      GridPane.setVgrow(textArea, Priority.ALWAYS);
      GridPane.setHgrow(textArea, Priority.ALWAYS);

      GridPane expContent = new GridPane();
      expContent.setMaxWidth(Double.MAX_VALUE);
      expContent.add(label, 0, 0);
      expContent.add(textArea, 0, 1);
      expContent.setMinWidth((double) 600);

      alert.getDialogPane().setExpandableContent(expContent);
    }

    alert.showAndWait();
  }

}
