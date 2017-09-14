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

package droplauncher.mvc.controller;

import adakite.exception.InvalidStateException;

public class ControllerWrapper {

  private Controller controller;

  private ControllerWrapper() {}

  public ControllerWrapper(Controller controller) {
    this.controller = controller;
  }

  /**
   * Makes a request to start BWHeadless.
   *
   * @throws InvalidStateException
   */
  public void startBWHeadless() throws InvalidStateException {
    if (this.controller.getState() == Controller.State.IDLE) {
      this.controller.btnStartClicked();
    }
  }

  /**
   * Makes a request to stop BWHeadless.
   *
   * @throws InvalidStateException
   */
  public void stopBWHeadless() throws InvalidStateException {
    if (this.controller.getState() == Controller.State.RUNNING) {
      this.controller.btnStartClicked();
    }
  }

}
