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

/**
 * Enum for prepending strings to messages.
 */
public enum MessagePrefix {

  COPY("copy"),
  KILL("kill"),
  DELETE("delete"),
  BWHEADLESS("bwh"),
  CLIENT("client"),
  DROPLAUNCHER("DropLauncher")
  ;

  private final String str;

  private MessagePrefix(String str) {
    this.str = str;
  }

  /**
   * Returns the string version of this enum with an appended
   * colon character and space.
   *
   * @param str specified message to include
   */
  public String get(String str) {
    String ret = this.str + ": ";
    if (!AdakiteUtils.isNullOrEmpty(str)) {
      ret += str;
    }
    return ret;
  }

  public String get() {
    return get(null);
  }

  @Override
  public String toString() {
    return this.str;
  }

}
