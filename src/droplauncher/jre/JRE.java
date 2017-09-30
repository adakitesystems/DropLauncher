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

package droplauncher.jre;

import droplauncher.DropLauncher;
import java.nio.file.Path;

public class JRE {

  /**
   * Default path to the JRE directory used by this program.
   */
  public static final Path ROOT_DIRECTORY = DropLauncher.BINARY_DIRECTORY.resolve("jre");

  /**
   * Default path to the JRE binary file used by this program.
   */
  public static final Path BINARY_FILE = ROOT_DIRECTORY.resolve("bin").resolve("java.exe");

  private JRE() {}

}
