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

import java.util.Optional;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;

public class YesNoDialog {

  public YesNoDialog() {
    /* Do nothing. */
  }

  public boolean userConfirms(String title, String content) {
      Alert alert = new Alert(AlertType.CONFIRMATION);
      alert.setTitle(title);
      alert.setContentText(content);
      alert.setHeaderText(null);
      View.addDefaultStylesheet(alert.getDialogPane().getStylesheets());
      ButtonType btnNo = new ButtonType("No");
      ButtonType btnYes = new ButtonType("Yes");
      alert.getButtonTypes().setAll(btnYes, btnNo);
      Optional<ButtonType> result = alert.showAndWait();
      return (result.get() == btnYes);
  }

}
