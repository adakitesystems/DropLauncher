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

package droplauncher.mvc.view.help;

import droplauncher.DropLauncher;
import droplauncher.mvc.view.View;
import droplauncher.mvc.view.WebViewWrapper;
import droplauncher.mvc.view.help.exception.CategoryParseException;
import droplauncher.starcraft.Starcraft;
import java.nio.file.Path;
import java.nio.file.Paths;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.stage.Stage;

public class Help {

  public enum Category {

    QUICK_GUIDE("Quick Guide", HTML_DIRECTORY.resolve("quick-guide.html")),
    STARCRAFT_SETUP("Connecting " + DropLauncher.PROGRAM_NAME + " to " + Starcraft.NAME, HTML_DIRECTORY.resolve("connect-dl-to-sc.html")),
    DOWNLOADING_BOTS("Downloading Bots", HTML_DIRECTORY.resolve("download-bots.html")),
    PLAY_VS_BOTS("Playing Against a Bot", HTML_DIRECTORY.resolve("play-vs-bot.html")),
    SYS_REQS("Runtime Requirements", HTML_DIRECTORY.resolve("runtime-req.html")),
    TECHNICAL_DETAILS("Technical Details", HTML_DIRECTORY.resolve("tech-details.html"))
    ;

    private final String str;
    private final Path file;

    private Category(String str, Path file) {
      this.str = str;
      this.file = file;
    }

    public Path getFile() {
      return this.file;
    }

    public static Category parseCategory(String str) {
      for (Category category : Category.values()) {
        if (str.equals(category.toString())) {
          return category;
        }
      }
      throw new CategoryParseException("Category not found: " + str);
    }

    @Override
    public String toString() {
      return this.str;
    }

  }

  private static final Path HTML_DIRECTORY = Paths.get("docs").resolve("help-contents");

  private Stage stage;
  private Scene scene;

  private ListView<String> list;
  private WebViewWrapper browser;

  public Help() {
    this.list = new ListView<>();
    this.browser = new WebViewWrapper();
  }

  public Help show() {
    HBox hbox = new HBox();

    ObservableList<String> items = FXCollections.observableArrayList();
    for (Category category : Category.values()) {
      items.add(category.toString());
    }
    this.list.setItems(items);
    this.list.setMinWidth(Region.USE_PREF_SIZE);
    this.list.setOnMousePressed(e -> {
      String item = this.list.getSelectionModel().getSelectedItem();
      this.browser.load("file:///" + Category.parseCategory(item).getFile().toAbsolutePath().toString());
    });

    if (Category.values().length > 0) {
      this.browser.load("file:///" + Category.values()[0].getFile().toAbsolutePath().toString());
    }

    this.browser.prefWidthProperty().bind(hbox.widthProperty());
    this.browser.prefHeightProperty().bind(hbox.heightProperty());

    hbox.getChildren().add(this.list);
    hbox.getChildren().add(this.browser);

    this.scene = new Scene(hbox, 800, 600);

    this.stage = new Stage();
    this.stage.setTitle(View.MenuText.HELP_CONTENTS.toString());
    this.stage.setResizable(true);
    this.stage.setScene(this.scene);
    this.stage.show();

    return this;
  }

}
