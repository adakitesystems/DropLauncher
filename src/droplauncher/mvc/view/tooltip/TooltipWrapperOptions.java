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

package droplauncher.mvc.view.tooltip;

import javafx.scene.control.Tooltip;

/*
TODO: This class isn't used yet. Finish it or delete it. It was originally
intended to extend TooltipWrapper functionality.
*/

public class TooltipWrapperOptions {

  private boolean isSetWrapTextEnabled;
  private double width;
  private double height;

  public TooltipWrapperOptions() {
    this.isSetWrapTextEnabled = true;
  }

  public boolean isSetWrapTextEnabled() {
    return this.isSetWrapTextEnabled;
  }

  public TooltipWrapperOptions setWrapText(boolean enabled) {
    this.isSetWrapTextEnabled = enabled;
    return this;
  }

  public double getWidth() {
    return this.width;
  }

  public TooltipWrapperOptions setWidth(double width) {
    this.width = width;
    return this;
  }

  public double getHeight() {
    return this.height;
  }

  public TooltipWrapperOptions setHeight(double height) {
      this.height = height;
      return this;
  }

  public TooltipWrapperOptions apply(Tooltip tooltip) {
    tooltip.setWrapText(isSetWrapTextEnabled());
    tooltip.setWidth(getWidth());
    tooltip.setHeight(getHeight());
    return this;
  }

}
