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

package droplauncher.bwheadless;

/**
 * Enum for passable arguments to bwheadless.
 */
public enum Argument {

  STARCRAFT_EXE("-e"), /* requires second string */
  HOST("-h"),
  GAME_NAME("-g"), /* requires second string */
  JOIN_GAME("-j"),
  MAP("-m"), /* requires second string */
  BOT_NAME("-n"), /* requires second string */
  BOT_RACE("-r"), /* requires second string */
  LOAD_DLL("-l"), /* requires second string */
  ENABLE_LAN("--lan"),
  STARCRAFT_INSTALL_PATH("--installpath") /* requires second string */
  ;

  private final String str;

  private Argument(String str) {
    this.str = str;
  }

  @Override
  public String toString() {
    return str;
  }

}
