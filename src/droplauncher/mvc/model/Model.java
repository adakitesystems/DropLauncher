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

package droplauncher.mvc.model;

import adakite.ini.Ini;
import adakite.ini.IniParseException;
import adakite.util.AdakiteUtils;
import droplauncher.bwapi.BWAPI;
import droplauncher.bwheadless.BWHeadless;
import droplauncher.mvc.view.View;
import droplauncher.starcraft.Starcraft;
import droplauncher.util.DropLauncher;
import java.io.IOException;
import java.util.Locale;

public class Model {

  private Ini ini;
  private BWHeadless bwheadless;

  public Model() {
    this.ini = new Ini();
    this.bwheadless = new BWHeadless();

    this.bwheadless.setINI(this.ini);
  }

  public void setup() throws IOException, IniParseException {
    if (!AdakiteUtils.fileExists(DropLauncher.DEFAULT_INI_PATH)) {
      AdakiteUtils.createFile(DropLauncher.DEFAULT_INI_PATH);
    }
    this.ini.parse(DropLauncher.DEFAULT_INI_PATH);
    parseSettings(this.ini);
    this.bwheadless.parseSettings(this.ini);
  }

  public Ini getINI() {
    return this.ini;
  }

  public BWHeadless getBWHeadless() {
    return this.bwheadless;
  }

  private void parseSettings(Ini ini) {
    if (!ini.hasValue(Model.getIniSection(View.Property.SHOW_LOG_WINDOW.toString()), View.Property.SHOW_LOG_WINDOW.toString())) {
      ini.set(Model.getIniSection(View.Property.SHOW_LOG_WINDOW.toString()), View.Property.SHOW_LOG_WINDOW.toString(), Boolean.TRUE.toString());
    }
    if (!ini.hasValue(Model.getIniSection(BWAPI.Property.COPY_WRITE_READ.toString()), BWAPI.Property.COPY_WRITE_READ.toString())) {
      ini.set(Model.getIniSection(BWAPI.Property.COPY_WRITE_READ.toString()), BWAPI.Property.COPY_WRITE_READ.toString(), Boolean.TRUE.toString());
    }
    if (!ini.hasValue(Model.getIniSection(Starcraft.Property.CLEAN_SC_DIR.toString()), Starcraft.Property.CLEAN_SC_DIR.toString())) {
      ini.set(Model.getIniSection(Starcraft.Property.CLEAN_SC_DIR.toString()), Starcraft.Property.CLEAN_SC_DIR.toString(), Boolean.TRUE.toString());
    }
    if (!ini.hasValue(Model.getIniSection(BWAPI.Property.WARN_UNKNOWN_BWAPI_DLL.toString()), BWAPI.Property.WARN_UNKNOWN_BWAPI_DLL.toString())) {
      ini.set(Model.getIniSection(BWAPI.Property.WARN_UNKNOWN_BWAPI_DLL.toString()), BWAPI.Property.WARN_UNKNOWN_BWAPI_DLL.toString(), Boolean.TRUE.toString());
    }
  }

  /**
   * Returns the INI section name associated with the specified
   * unique key. Using this method removes the programmer's
   * requirement of knowing which unique settings key belongs to which
   * INI section. This also makes the moving/renaming/adding of keys easier.
   *
   * @param uniqueKey specified unique key
   */
  public static String getIniSection(String uniqueKey) {
    uniqueKey = uniqueKey.toLowerCase(Locale.US);
    for (BWAPI.Property val : BWAPI.Property.values()) {
      if (uniqueKey.equals(val.toString())) {
        return BWAPI.DEFAULT_INI_SECTION_NAME;
      }
    }
    for (BWHeadless.Property val : BWHeadless.Property.values()) {
      if (uniqueKey.equals(val.toString())) {
        return BWHeadless.DEFAULT_INI_SECTION_NAME;
      }
    }
    for (View.Property val : View.Property.values()) {
      if (uniqueKey.equals(val.toString())) {
        return DropLauncher.DEFAULT_INI_SECTION_NAME;
      }
    }
    for (Starcraft.Property val : Starcraft.Property.values()) {
      if (uniqueKey.equals(val.toString())) {
        return Starcraft.DEFAULT_INI_SECTION_NAME;
      }
    }
    throw new IllegalArgumentException("not found: uniqueKey=" + uniqueKey);
  }

}
