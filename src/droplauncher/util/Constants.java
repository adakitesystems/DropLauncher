package droplauncher.util;

import java.util.logging.Level;

/**
 * Constants class for global constants.
 */
public class Constants {

  public static final String PROGRAM_NAME = "DropLauncher";
  public static final String PROGRAM_VERSION = "0.02a";
  public static final String PROGRAM_AUTHOR = "Adakite";
  public static final String PROGRAM_TITLE = PROGRAM_NAME + " v" + PROGRAM_VERSION;
  public static final String PROGRAM_GITHUB = "https://github.com/AdakiteSystems/DropLauncher";
  public static final String PROGRAM_LICENSE = "AGPL 3.0";
  public static final String PROGRAM_LICENSE_LINK = "https://www.gnu.org/licenses/agpl-3.0.en.html";
  public static final String PROGRAM_DESC = "Designed to be a simple tool to aid players in setting up and playing 1v1 StarCraft: Brood War against BWAPI bots using Local Area Network (UDP) as the connection type.";
  public static final String PROGRAM_ABOUT
      = Constants.PROGRAM_NAME + Util.newline()
      + "Author: " + Constants.PROGRAM_AUTHOR + Util.newline()
      + "Version: " + Constants.PROGRAM_VERSION + Util.newline(2)
      + Constants.PROGRAM_DESC + Util.newline(2)
      + "License: " + Constants.PROGRAM_LICENSE + Util.newline()
      + Constants.PROGRAM_LICENSE_LINK + Util.newline(2)
      + "Source:" + Util.newline()
      + Constants.PROGRAM_GITHUB + Util.newline()
      + Util.newline()
      ;

  /* Logging */
  public static final boolean DEBUG = true;
  public static final Level DEFAULT_LOG_LEVEL = Level.SEVERE;

  public static final String DROPLAUNCHER_INI = "droplauncher.ini";
  public static final String DROPLAUNCHER_INI_SECTION = "droplauncher";

  public static final String TEMP_DIRECTORY = "tmp";

  private Constants() {}

}
