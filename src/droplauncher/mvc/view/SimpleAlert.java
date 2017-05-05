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

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

/**
 * Simple wrapper class for the JavaFX Alert object.
 */
public class SimpleAlert {

  public SimpleAlert() {
    /* Do nothing. */
  }

  public void showAndWait(AlertType alertType, String title, String msg) {
    if (alertType == AlertType.ERROR) {
      throw new UnsupportedOperationException("Use ExceptionAlert instead of SimpleAlert");
    }

    Alert alert = new Alert(alertType);
    alert.setTitle(title);
    alert.setHeaderText(null);
    alert.setContentText(msg);
    View.addDefaultStylesheet(alert.getDialogPane().getStylesheets());
    alert.showAndWait();
  }

  public void showAndWait(AlertType alertType, String msg) {
    showAndWait(alertType, null, msg);
  }

}
