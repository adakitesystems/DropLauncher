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

package droplauncher.util;

import droplauncher.bwheadless.BWHeadless;
import droplauncher.starcraft.Starcraft;

/**
 * Enum for the names of new tasks that should be killed when the
 * bot is stopped.
 */
public enum KillableTask {

  BWHEADLESS_EXE(BWHeadless.EXE_FILENAME),
  CONHOST_EXE("conhost.exe"),
  STARCRAFT_EXE(Starcraft.EXE_FILENAME),
  TASKLIST_EXE("tasklist.exe"),
  DLLHOST_EXE("dllhost.exe"),
  JAVA_EXE("java.exe"),
  JAVAW_EXE("javaw.exe")
  ;

  private final String str;

  private KillableTask(String str) {
    this.str = str;
  }

  @Override
  public String toString() {
    return this.str;
  }

}
