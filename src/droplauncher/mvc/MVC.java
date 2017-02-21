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

package droplauncher.mvc;

import adakite.debugging.Debugging;
import droplauncher.mvc.controller.Controller;
import droplauncher.mvc.model.Model;
import droplauncher.mvc.view.View;
import java.util.logging.Logger;
import javafx.stage.Stage;

public class MVC {

  private static final Logger LOGGER = Logger.getLogger(MVC.class.getName());

  private Controller controller;
  private Model model;
  private View view;

  public MVC() {
    this.controller = new Controller();
    this.model = new Model();
    this.view = new View();

    this.controller.setModel(this.model);
    this.controller.setView(this.view);

    this.view.setController(this.controller);

    try {
      this.model.setup();
    } catch (Exception ex) {
      LOGGER.log(Debugging.getLogLevel(), null, ex);
    }
  }

  public void start(Stage stage) {
    this.view.start(stage);
  }

}
