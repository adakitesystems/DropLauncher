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

import droplauncher.DropLauncher;
import droplauncher.bwapi.BWAPI;
import droplauncher.mvc.view.ExceptionAlert;
import droplauncher.mvc.view.View;
import droplauncher.starcraft.Starcraft;
import java.util.Locale;
import javafx.application.Platform;

public class ProgramSettings {

  public ProgramSettings() {
    /* Do nothing. */
  }

  private static String getSection(String uniqueKey) {
    uniqueKey = uniqueKey.toLowerCase(Locale.US);

    for (DropLauncher.PropertyKey val : DropLauncher.PropertyKey.values()) {
      if (uniqueKey.equals(val.toString())) {
        return "droplauncher";
      }
    }

    for (BWAPI.PropertyKey val : BWAPI.PropertyKey.values()) {
      if (uniqueKey.equals(val.toString())) {
        return "bwapi";
      }
    }

    for (View.PropertyKey val : View.PropertyKey.values()) {
      if (uniqueKey.equals(val.toString())) {
        return "ui";
      }
    }

    for (Starcraft.PropertyKey val : Starcraft.PropertyKey.values()) {
      if (uniqueKey.equals(val.toString())) {
        return "starcraft";
      }
    }

    throw new IllegalArgumentException("not found: uniqueKey=" + uniqueKey);
  }

  /**
   * Returns the associated value with the specified key.
   *
   * @param key specified key
   * @throws IllegalStateException if the specified key does not exist
   */
  public static String getValue(String key) {
    return DropLauncher.getSettings().getValue(getSection(key), key);
  }

  /**
   * Tests whether the specified key has an associated value.
   *
   * @param key specified key
   */
  public static boolean hasValue(String key) {
    return DropLauncher.getSettings().hasValue(getSection(key), key);
  }

  /**
   * Sets the specified key to the specified value regardless if
   * the specified key existed previously.
   *
   * @param key specified key
   * @param val specified value
   */
  public static void setValue(String key, String val) {
    DropLauncher.getSettings().set(getSection(key), key, val);
    try {
      DropLauncher.getSettings().store(DropLauncher.SETTINGS_FILE);
    } catch (Exception ex) {
      Platform.runLater(() -> {
        new ExceptionAlert().showAndWait("Failed to update settings file: " + DropLauncher.SETTINGS_FILE.toString(), ex);
      });
    }
  }

  /**
   * Tests whether the specified key has a TRUE or FALSE value. Returns
   * FALSE even if the specified key does not exist.
   *
   * @param key specified key
   */
  public static boolean isEnabled(String key) {
    return (DropLauncher.getSettings().hasValue(getSection(key), key)
        && DropLauncher.getSettings().getValue(getSection(key), key).equalsIgnoreCase(Boolean.TRUE.toString()));
  }

  /**
   * Sets the specified key to the specified boolean value regardless if
   * the specified key existed previously.
   *
   * @param key specified key
   * @param enabled specified boolean value
   */
  public static void setEnabled(String key, boolean enabled) {
    setValue(key, Boolean.toString(enabled));
  }

}
