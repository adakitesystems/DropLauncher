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

package droplauncher.bwapi;

import adakite.debugging.Debugging;
import adakite.exception.InvalidArgumentException;
import adakite.settings.Settings;
import adakite.util.AdakiteUtils;
import droplauncher.bot.exception.InvalidBwapiDllException;
import droplauncher.bot.exception.MissingBotFileException;
import droplauncher.bot.exception.MissingBotNameException;
import droplauncher.bot.exception.MissingBotRaceException;
import droplauncher.bot.exception.MissingBwapiDllException;
import droplauncher.starcraft.Starcraft;
import droplauncher.starcraft.exception.StarcraftProfileNameException;
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

  public static final String DEFAULT_NAME = "BWAPI_BOT";

  private Settings settings;

  public Bot() {
    this.settings = new Settings();
    this.settings.set(Property.NAME.toString(), DEFAULT_NAME);
  }

  //TODO: Right now this is not needed. Delete?
//  /**
//   * Returns a copy of the internal settings object.
//   */
//  public Settings getSettings() {
//    return new Settings(this.settings);
//  }

  /**
   * Returns the name of this bot.
   *
   * @throws MissingBotNameException if name is not set
   */
  public String getName() throws MissingBotNameException {
    String val = this.settings.getValue(Property.NAME.toString());
    if (AdakiteUtils.isNullOrEmpty(val, true)) {
      throw new MissingBotNameException();
    }
    return val;
  }

  /**
   * Sets the name of this bot to the specified string.
   *
   * @param name specified string
   * @throws InvalidArgumentException if the specified name is null or empty
   * @throws StarcraftProfileNameException if the specified name does not
   *     adhere to the standard StarCraft profile name rules set by
   *     {@link droplauncher.starcraft.Starcraft#cleanProfileName(java.lang.String)}.
   */
  public void setName(String name) throws InvalidArgumentException,
                                          StarcraftProfileNameException {
    if (AdakiteUtils.isNullOrEmpty(name, true)) {
      throw new InvalidArgumentException(Debugging.cannotBeNullOrEmpty("name"));
    }
    String nameTrimmed = name.trim();
    String cleaned = Starcraft.cleanProfileName(nameTrimmed);
    if (!cleaned.equals(nameTrimmed)) {
      throw new StarcraftProfileNameException(name);
    }
    this.settings.set(Property.NAME.toString(), name);
  }

  /**
   * Returns the race of this bot.
   *
   * @throws MissingBotRaceException if race is not set
   */
  public String getRace() throws MissingBotRaceException {
    String val = this.settings.getValue(Property.RACE.toString());
    if (AdakiteUtils.isNullOrEmpty(val, true)) {
      throw new MissingBotRaceException();
    }
    return val;
  }

  /**
   * Sets the race of this bot to the specified race.
   *
   * @param race specified race
   * @throws InvalidArgumentException if the specified race is invalid
   */
  public void setRace(String race) throws InvalidArgumentException {
    if (!Starcraft.Race.isValid(race)) {
      String errorMessage = "invalid race";
      if (!AdakiteUtils.isNullOrEmpty(race)) {
        errorMessage += ": " + race;
      }
      throw new InvalidArgumentException(errorMessage);
    } else {
      this.settings.set(Property.RACE.toString(), race);
    }
  }

  /**
   * Returns the path to the bot file.
   *
   * @throws MissingBotFileException if path is not set
   */
  public String getPath() throws MissingBotFileException {
    String val = this.settings.getValue(Property.PATH.toString());
    if (AdakiteUtils.isNullOrEmpty(val, true)) {
      throw new MissingBotFileException();
    }
    return val;
  }

  /**
   * Sets the path of this bot file to the specified input path.
   *
   * @param path specified input path
   * @throws InvalidArgumentException if the path is null or empty
   */
  public void setPath(String path) throws InvalidArgumentException {
    if (AdakiteUtils.isNullOrEmpty(path, true)) {
      throw new InvalidArgumentException(Debugging.cannotBeNullOrEmpty("path"));
    }
    this.settings.set(Property.PATH.toString(), path);
  }

  /**
   * Returns the path to the BWAPI.dll associated with this bot.
   *
   * @throws MissingBwapiDllException if BWAPI.dll is not set
   */
  public String getBwapiDll() throws MissingBwapiDllException {
    String val = this.settings.getValue(Property.BWAPI_DLL.toString());
    if (AdakiteUtils.isNullOrEmpty(val, true)) {
      throw new MissingBwapiDllException();
    }
    return val;
  }

  /**
   * Sets the path of the BWAPI.dll to the specified input path.
   *
   * @param path specified input path
   * @throws InvalidArgumentException if the path is null or empty
   * @throws InvalidBwapiDllException if the path does not
   *     equal BWAPI.dll as the filename
   */
  public void setBwapiDll(String path) throws InvalidArgumentException,
                                              InvalidBwapiDllException {
    if (AdakiteUtils.isNullOrEmpty(path, true)) {
      throw new InvalidArgumentException(Debugging.cannotBeNullOrEmpty("path"));
    } else if (!FilenameUtils.getName(path).toLowerCase(Locale.US).equals("bwapi.dll")) {
      throw new InvalidBwapiDllException("filename does not equal \"BWAPI.dll\": " + path);
    }
    this.settings.set(Property.BWAPI_DLL.toString(), path);
  }

  /**
   * Returns a copy of the list of extra bot files. An extra bot file is
   * described as any file that the bot uses after the bot has been invoked.
   * The list is an ArrayList of strings which are paths to each extra file.
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
   * @throws InvalidArgumentException if path is null or empty
   */
  public void addExtraFile(String path) throws InvalidArgumentException {
    if (AdakiteUtils.isNullOrEmpty(path, true)) {
      throw new InvalidArgumentException(Debugging.emptyString());
    }

    /* Check for existing extra bot files. */
    int index = 0;
    String val;
    while ((val = this.settings.getValue(Property.EXTRA_FILE.toString() + Integer.toString(index))) != null) {
      if (FilenameUtils.getName(path).equalsIgnoreCase(FilenameUtils.getName(val))) {
        /* Save index of existing extra bot file. */
        break;
      }
      index++;
    }

    /* Add/overwrite extra bot file. */
    this.settings.set(Property.EXTRA_FILE.toString() + Integer.toString(index), path);
  }

  /**
   * Returns the type of this bot.
   * Example: {@link Type#CLIENT}, {@link Type#DLL}, etc.
   *
   * @throws MissingBotFileException if an error occurs with {@link #getPath()}.
   */
  public Type getType() throws MissingBotFileException {
    String path = getPath();
    String ext = FilenameUtils.getExtension(path).toLowerCase(Locale.US);
    switch (ext) {
      case "dll":
        return Type.DLL;
      case "exe":
        /* Fall through. */
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

}
