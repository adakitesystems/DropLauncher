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

package droplauncher.bwapi.bot;

import adakite.debugging.Debugging;
import adakite.exception.InvalidArgumentException;
import adakite.settings.Settings;
import adakite.util.AdakiteUtils;
import droplauncher.bwapi.BWAPI;
import droplauncher.bwapi.bot.exception.InvalidBwapiDllException;
import droplauncher.bwapi.bot.exception.MissingBotFileException;
import droplauncher.bwapi.bot.exception.MissingBotNameException;
import droplauncher.bwapi.bot.exception.MissingBotRaceException;
import droplauncher.bwapi.bot.exception.MissingBwapiDllException;
import droplauncher.starcraft.Starcraft;
import droplauncher.starcraft.exception.StarcraftProfileNameException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import org.apache.commons.io.FilenameUtils;

/**
 * Container class for bot information.
 */
public class Bot {

  private enum PropertyKey {

    NAME("name"),
    RACE("race"),
    PATH("path"),
    BWAPI_DLL("bwapi_dll"),
    ;

    private final String str;

    private PropertyKey(String str) {
      this.str = str;
    }

    @Override
    public String toString() {
      return this.str;
    }

  }

  public enum Type {

    /**
     * Indicates the bot is a module (i.e. compiled into a DLL).
     */
    DLL,

    /**
     * Indicates the bot is a client (i.e. compiled into an executable such as EXE or JAR).
     */
    CLIENT,

    /**
     * Indicates the bot type is not recognized.
     */
    UNKNOWN

  }

  public static final String DEFAULT_NAME = "BWAPI_BOT";

  private Settings settings;
  private List<String> extraFiles;

  public Bot() {
    this.settings = new Settings();
    this.settings.set(PropertyKey.NAME.toString(), Bot.DEFAULT_NAME);
    this.extraFiles = new ArrayList<>();
  }

  /**
   * Returns the name of this bot.
   *
   * @throws MissingBotNameException if name is not set
   */
  public String getName() throws MissingBotNameException {
    if (!this.settings.hasValue(PropertyKey.NAME.toString())) {
      throw new MissingBotNameException();
    }
    String val = this.settings.getValue(PropertyKey.NAME.toString());
    return val;
  }

  /**
   * Sets the name of this bot to the specified string.
   *
   * @param name specified string
   * @throws InvalidArgumentException if the specified name is null or empty
   * @throws StarcraftProfileNameException if the specified name does not
   *     adhere to the standard StarCraft profile name rules set by
   *     {@link droplauncher.starcraft.Starcraft#sanitizeProfileName(java.lang.String)}.
   */
  public void setName(String name) throws InvalidArgumentException,
                                          StarcraftProfileNameException {
    if (AdakiteUtils.isNullOrEmpty(name, true)) {
      throw new InvalidArgumentException(Debugging.cannotBeNullOrEmpty("name"));
    }
    String nameTrimmed = name.trim();
    String cleaned = Starcraft.sanitizeProfileName(nameTrimmed);
    if (!cleaned.equals(nameTrimmed)) {
      throw new StarcraftProfileNameException(name);
    }
    this.settings.set(PropertyKey.NAME.toString(), name);
  }

  /**
   * Returns the race of this bot.
   *
   * @throws MissingBotRaceException if race is not set
   */
  public String getRace() throws MissingBotRaceException {
    if (!this.settings.hasValue(PropertyKey.RACE.toString())) {
      throw new MissingBotRaceException();
    }
    String val = this.settings.getValue(PropertyKey.RACE.toString());
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
    }
    this.settings.set(PropertyKey.RACE.toString(), race);
  }

  /**
   * Returns the path to the bot file.
   *
   * @throws MissingBotFileException if path is not set
   */
  public Path getPath() throws MissingBotFileException {
    if (!this.settings.hasValue(PropertyKey.PATH.toString())) {
      throw new MissingBotFileException();
    }
    String val = this.settings.getValue(PropertyKey.PATH.toString());
    return Paths.get(val);
  }

  /**
   * Sets the path of this bot file to the specified input path.
   *
   * @param path specified input path
   * @throws InvalidArgumentException if the path is null or empty
   */
  public void setPath(Path path) throws InvalidArgumentException {
    if (path == null) {
      throw new InvalidArgumentException(Debugging.cannotBeNull("path"));
    }
    this.settings.set(PropertyKey.PATH.toString(), path.toString());
  }

  /**
   * Returns the path to the BWAPI.dll associated with this bot.
   *
   * @throws MissingBwapiDllException if BWAPI.dll is not set
   */
  public Path getBwapiDll() throws MissingBwapiDllException {
    if (!this.settings.hasValue(PropertyKey.BWAPI_DLL.toString())) {
      throw new MissingBwapiDllException();
    }
    String val = this.settings.getValue(PropertyKey.BWAPI_DLL.toString());
    return Paths.get(val);
  }

  /**
   * Sets the path of the BWAPI.dll to the specified input path.
   *
   * @param path specified input path
   * @throws InvalidArgumentException if the path is null or empty
   * @throws InvalidBwapiDllException if the path does not
   *     equal BWAPI.dll as the filename
   */
  public void setBwapiDll(Path path) throws InvalidBwapiDllException,
                                            InvalidArgumentException {
    if (path == null) {
      throw new InvalidArgumentException(Debugging.cannotBeNullOrEmpty("path"));
    } else if (!FilenameUtils.getName(path.toString()).equalsIgnoreCase(BWAPI.DLL_FILENAME_RELEASE)) {
      throw new InvalidBwapiDllException("filename does not equal " + BWAPI.DLL_FILENAME_RELEASE + ": " + path);
    }
    this.settings.set(PropertyKey.BWAPI_DLL.toString(), path.toString());
  }

  /**
   * Returns a copy of the list of extra bot files. An extra bot file is
   * described as any file that the bot uses after the bot has been invoked.
   * The list is an ArrayList of strings which are paths to each extra file.
   * To add an extra bot file, use {@link #addExtraFile(java.lang.String)}.
   *
   * @see #addExtraFile(java.lang.String)
   */
  public List<String> getExtraFiles() {
    List<String> ret = new ArrayList<>();
    for (String file : this.extraFiles) {
      ret.add(file);
    }
    return ret;
  }

  /**
   * Adds the specified path as a path to an extra bot file. If the specified
   * path's filename matches a filename of an existing extra bot file path, the
   * old path will be overwritten.
   *
   * @param path specified path to file
   * @throws InvalidArgumentException if path is null or empty
   */
  public void addExtraFile(String path) throws InvalidArgumentException {
    if (AdakiteUtils.isNullOrEmpty(path, true)) {
      throw new InvalidArgumentException(Debugging.cannotBeNullOrEmpty("path"));
    }

    /* Check for existing extra bot files. */
    String pathFilename = FilenameUtils.getName(path);
    Iterator<String> itr = this.extraFiles.iterator();
    while (itr.hasNext()) {
      String extra = itr.next();
      String extraFilename = FilenameUtils.getName(extra);
      if (pathFilename.equalsIgnoreCase(extraFilename)) {
        /* Remove the existing extra bot file. */
        itr.remove();
      }
    }

    /* Add extra bot file. */
    this.extraFiles.add(path);
  }

  /**
   * Removes all of the elements from this list.
   * The list will be empty after this call returns.
   */
  public void clearExtraFiles() {
    this.extraFiles.clear();
  }

  /**
   * Returns the type of this bot.
   * Example: {@link Type#CLIENT}, {@link Type#DLL}, etc.
   *
   * @throws MissingBotFileException if an error occurs with {@link #getPath()}.
   */
  public Type getType() throws MissingBotFileException {
    String ext = AdakiteUtils.getFileExtension(getPath()).toLowerCase(Locale.US);
    if (ext == null) {
      ext = "";
    }
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

}
