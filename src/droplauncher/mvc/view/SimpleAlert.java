package droplauncher.mvc.view;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class SimpleAlert {

  public SimpleAlert() {
    /* Do nothing. */
  }

  public void showAndWait(AlertType alertType, String title, String msg) {
    Alert alert = new Alert(alertType);
    alert.setTitle(title);
    alert.setHeaderText(null);
    alert.setContentText(msg);
//    alert.getDialogPane().getStylesheets().add(View.DEFAULT_CSS);
    alert.showAndWait();
  }

  public void showAndWait(AlertType alertType, String msg) {
    showAndWait(alertType, null, msg);
  }

}
