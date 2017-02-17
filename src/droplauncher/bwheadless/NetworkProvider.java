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

import java.util.Locale;

/**
 * Enum for the passable network argument to the bwheadless process.
 * Currently, only LAN is supported since LocalPC requires admin privileges
 * and a modified SNP file.
 */
public enum NetworkProvider {

  LAN("lan"),
  ;

  private final String str;

  private NetworkProvider(String str) {
    this.str = str;
  }

  /**
   * Returns the corresponding NetworkProvider object.
   *
   * @param str specified string
   * @return
   *     the corresponding NetworkProvider object,
   *     otherwise null if no match was found
   */
  public static NetworkProvider get(String str) {
    str = str.toLowerCase(Locale.US);
    for (NetworkProvider val : NetworkProvider.values()) {
      if (str.equals(val.toString().toLowerCase(Locale.US))) {
        return val;
      }
    }
    return null;
  }

  @Override
  public String toString() {
    return this.str;
  }

}
