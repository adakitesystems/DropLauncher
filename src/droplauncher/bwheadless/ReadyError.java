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
 * Enum for reporting the launch status of bwheadless.
 */
public enum ReadyError {

  NONE("OK"),
  BWHEADLESS_EXE("unable to read/locate bwheadless.exe"),
  STARTCRAFT_EXE("unable to read/locate StarCraft.exe"),
  BWAPI_DLL("unable to read/locate BWAPI.dll"),
  BOT_NAME("invalid bot name"),
  BOT_FILE("unable to read/locate bot file (*.dll, *.exe)"),
  BOT_RACE("invalid bot race"),
  NETWORK_PROVIDER("invalid network provider"),
  CONNECT_MODE("invalid connect mode"),
  BWAPI_INSTALL("corrupt or missing BWAPI installation"),
  JRE_INSTALL("corrupt or missing JRE installation")
  ;

  private final String name;

  private ReadyError(String name) {
    this.name = name;
  }

  @Override
  public String toString() {
    return this.name;
  }

}
