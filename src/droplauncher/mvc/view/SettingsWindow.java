package droplauncher.mvc.view;

import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class SettingsWindow {

  public SettingsWindow() {

  }

  public void showAndWait() {
    Stage stage = new Stage();

    stage.initModality(Modality.APPLICATION_MODAL);
    stage.setMinWidth(500);
    stage.setMinHeight(300);

    VBox vbox = new VBox(10);
    Scene scene = new Scene(vbox);
    stage.setScene(scene);
    stage.showAndWait();
  }

}
