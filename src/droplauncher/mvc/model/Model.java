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
import adakite.prefs.Prefs;
import droplauncher.bot.Bot;
import droplauncher.bwapi.BWAPI;
import droplauncher.bwheadless.BWHeadless;
import droplauncher.mvc.view.View;
import droplauncher.starcraft.Starcraft;
import droplauncher.util.DropLauncher;
import java.util.Locale;

public class Model {

  private Ini ini;
  private Bot bot;
  private BWHeadless bwheadless;

  public Model() {
    this.ini = new Ini();
    this.bot = new Bot();
    this.bwheadless = new BWHeadless();
  }

  public Bot getBot() {
    return this.bot;
  }

  public BWHeadless getBWHeadless() {
    return this.bwheadless;
  }

  private static Prefs getPrefs(String uniqueKey) {
    uniqueKey = uniqueKey.toLowerCase(Locale.US);
    for (BWAPI.Property val : BWAPI.Property.values()) {
      if (uniqueKey.equals(val.toString())) {
        return BWAPI.PREF_ROOT;
      }
    }
    for (BWHeadless.Property val : BWHeadless.Property.values()) {
      if (uniqueKey.equals(val.toString())) {
        return BWHeadless.PREF_ROOT;
      }
    }
    for (View.Property val : View.Property.values()) {
      if (uniqueKey.equals(val.toString())) {
        return DropLauncher.PREF_ROOT;
      }
    }
    for (Starcraft.Property val : Starcraft.Property.values()) {
      if (uniqueKey.equals(val.toString())) {
        return Starcraft.PREF_ROOT;
      }
    }
    throw new IllegalArgumentException("not found: uniqueKey=" + uniqueKey);
  }

  public static String getPref(String key) {
    return getPrefs(key).get(key);
  }

  public static void setPref(String key, String val) {
    getPrefs(key).set(key, val);
  }

  public static boolean isPrefEnabled(String key) {
    return getPrefs(key).isEnabled(key);
  }

  public static void setPrefEnabled(String key, boolean enabled) {
    getPrefs(key).set(key, Boolean.toString(enabled));
  }

}
