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

package droplauncher.bot;

import adakite.debugging.Debugging;
import adakite.exception.InvalidArgumentException;
import adakite.exception.InvalidStateException;
import adakite.settings.Settings;
import adakite.util.AdakiteUtils;
import droplauncher.starcraft.Starcraft;
import droplauncher.starcraft.Starcraft.Race;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Locale;
import org.apache.commons.io.FilenameUtils;

/**
 * Container class for bot information.
 */
public class Bot {

  public enum Property {

    NAME("name"),
    RACE("race"),
    PATH("path"),
    BWAPI_DLL("bwapidll"),
    EXTRA_FILE("extrafile")
    ;

    private final String str;

    private Property(String str) {
      this.str = str;
    }

    @Override
    public String toString() {
      return this.str;
    }

  }

  public enum Type {
    DLL,
    CLIENT,
    UNKNOWN
  }

  public static final String DEFAULT_NAME = "BOT";

  private Settings settings;

  public Bot() {
    this.settings = new Settings();
  }

  /**
   * Returns the name of this bot.
   *
   * @throws InvalidStateException if name is not set
   */
  public String getName() throws InvalidStateException {
    String val = this.settings.getValue(Property.NAME.toString());
    if (AdakiteUtils.isNullOrEmpty(val, true)) {
      throw new InvalidStateException("name is not set");
    }
    return val;
  }

  /**
   * Sets the name of this bot to the specified string.
   *
   * @param name specified string
   * @throws InvalidArgumentException if the specified name is empty or
   *     does not adhere to the standard StarCraft profile name rules
   *     set by {@link droplauncher.starcraft.Starcraft#cleanProfileName(java.lang.String)}.
   */
  public void setName(String name) throws InvalidArgumentException {
    if (AdakiteUtils.isNullOrEmpty(name, true)) {
      throw new InvalidArgumentException(Debugging.emptyString());
    }
    name = name.trim();
    String cleaned = Starcraft.cleanProfileName(name);
    if (!cleaned.equals(name)) {
      throw new InvalidArgumentException("standard StarCraft profile name violation: " + name);
    }
    this.settings.set(Property.NAME.toString(), name);
  }

  /**
   * Returns the race of this bot.
   *
   * @throws InvalidStateException if race is not set
   */
  public String getRace() throws InvalidStateException {
    String val = this.settings.getValue(Property.RACE.toString());
    if (AdakiteUtils.isNullOrEmpty(val)) {
      throw new InvalidStateException("race is not set");
    }
    return val;
  }

  /**
   * Sets the race of this bot to the specified race.
   *
   * @param race specified race
   * @throws InvalidArgumentException if the specified race is not in
   *     {@link droplauncher.starcraft.Starcraft.Race}.
   */
  public void setRace(String race) throws InvalidArgumentException {
    Race raceEnum;
    try {
      raceEnum = Race.get(race);
    } catch (Exception ex) {
      String errorMessage = "invalid race";
      if (!AdakiteUtils.isNullOrEmpty(race)) {
        errorMessage += ": " + race;
      }
      throw new InvalidArgumentException(errorMessage);
    }
    this.settings.set(Property.RACE.toString(), raceEnum.toString());
  }

  /**
   * Returns the path to the bot file.
   *
   * @throws InvalidStateException if path is not set yet
   */
  public String getPath() throws InvalidStateException {
    String val = this.settings.getValue(Property.PATH.toString());
    if (AdakiteUtils.isNullOrEmpty(val, true)) {
      throw new InvalidStateException("path not set");
    }
    return val;
  }

  /**
   * Sets the path of this bot file to the specified input path.
   *
   * @param path specified input path
   * @throws InvalidArgumentException if the path is null, empty, or
   *     does not exist
   */
  public void setPath(String path) throws InvalidArgumentException {
    if (AdakiteUtils.isNullOrEmpty(path, true)) {
      throw new InvalidArgumentException(Debugging.emptyString());
    } else if (!AdakiteUtils.fileExists(Paths.get(path))) {
      throw new InvalidArgumentException(Debugging.fileDoesNotExist(Paths.get(path)));
    }
    this.settings.set(Property.PATH.toString(), path);
  }

  /**
   * Returns the path to the BWAPI.dll associated with this bot.
   *
   * @throws InvalidStateException if BWAPI.dll is not set
   */
  public String getBwapiDll() throws InvalidStateException {
    String val = this.settings.getValue(Property.BWAPI_DLL.toString());
    if (AdakiteUtils.isNullOrEmpty(val, true)) {
      throw new InvalidStateException("BWAPI.dll path is not set");
    }
    return val;
  }

  /**
   * Sets the path of the BWAPI.dll to the specified input path.
   *
   * @param path specified input path
   * @throws InvalidArgumentException if the path is null, empty, does
   *     not exist, does not contain BWAPI in the filename, or does not
   *     end with the ".dll" file extension.
   */
  public void setBwapiDll(String path) throws InvalidArgumentException {
    if (AdakiteUtils.isNullOrEmpty(path)) {
      throw new InvalidArgumentException(Debugging.emptyString());
    } else if (!AdakiteUtils.fileExists(Paths.get(path))) {
      throw new InvalidArgumentException(Debugging.fileDoesNotExist(Paths.get(path)));
    } else if (!FilenameUtils.getBaseName(path).toLowerCase(Locale.US).equals("bwapi")) {
      throw new InvalidArgumentException("filename does not equal \"BWAPI\": " + path);
    } else if (!FilenameUtils.getExtension(path).toLowerCase(Locale.US).equals("dll")) {
      throw new InvalidArgumentException("file extension is not DLL: " + path);
    }
    this.settings.set(Property.BWAPI_DLL.toString(), path);
  }

  /**
   * Returns a copy of the list of extra bot files. An extra bot file is
   * described as any file that the bot uses after the bot has been invoked.
   * The list is an ArrayList of strings which are paths to each bot file.
   * To add an extra bot file, use {@link #addExtraFile(java.lang.String)}.
   *
   * @see #addExtraFile(java.lang.String)
   */
  public ArrayList<String> getExtraFiles() {
    ArrayList<String> files = new ArrayList<>();
    String file;
    int index = 0;
    while (!AdakiteUtils.isNullOrEmpty(file = getExtraFile(index++))) {
      files.add(file);
    }
    return files;
  }

  /**
   * Adds the specified path as a path to an extra bot file.
   *
   * @param path specified path to file
   * @throws InvalidArgumentException if path is null, empty, or
   *     does not exist
   */
  public void addExtraFile(String path) throws InvalidArgumentException {
    if (AdakiteUtils.isNullOrEmpty(path)) {
      throw new InvalidArgumentException(Debugging.emptyString());
    } else if (!AdakiteUtils.fileExists(Paths.get(path))) {
      throw new InvalidArgumentException(Debugging.fileDoesNotExist(Paths.get(path)));
    }
    this.settings.set(Property.EXTRA_FILE.toString() + Integer.toString(getNextExtraFileIndex()), path);
  }

  /**
   * Returns the type of this bot.
   * Example: {@link Type#CLIENT}, {@link Type#DLL}, etc.
   *
   * @throws InvalidStateException if an error occurs with {@link #getPath()}.
   */
  public Type getType() throws InvalidStateException {
    String path = getPath();
    String ext = FilenameUtils.getExtension(path).toLowerCase(Locale.US);
    switch (ext) {
      case "dll":
        return Type.DLL;
      case "exe":
      case "jar":
        return Type.CLIENT;
      default:
        return Type.UNKNOWN;
    }
  }

  /**
   * Returns the path to the extra bot file at the specified index.
   *
   * @param index specified index
   * @return
   *     the path to the extra bot file at the specified index if found,
   *     otherwise null
   */
  private String getExtraFile(int index) {
    return this.settings.getValue(Property.EXTRA_FILE.toString() + Integer.toString(index));
  }

  private int getNextExtraFileIndex() {
    int index = 0;
    while (this.settings.getValue(Property.EXTRA_FILE.toString() + Integer.toString(index)) != null) {
      index++;
    }
    return index;
  }

  //TODO
  public static void loadProfile(Bot bot, String directory) {
    throw new UnsupportedOperationException("operation not supported yet");
  }

}