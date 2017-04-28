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

/**
 * Enum for predefined settings keys for use with settings objects
 * such as {@link adakite.ini.Ini}.
 */
public enum SettingsKey {

  /* Program keys */
  SHOW_LOG_WINDOW("show_log_window"),
  CLEAN_SC_DIR("clean_sc_dir"),

  /* BWAPI keys */
  COPY_WRITE_READ("bwapi_write_read"),
  
  ;

  private final String str;

  private SettingsKey(String str) {
    this.str = str;
  }

  @Override
  public String toString() {
    return this.str;
  }

}
