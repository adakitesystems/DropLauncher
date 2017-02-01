package droplauncher.util;

import java.util.logging.Level;

/**
 * Utility class for global constants.
 */
public class Constants {

  public static final String PROGRAM_NAME = "DropLauncher";
  public static final String PROGRAM_VERSION = "0.02a";
  public static final String PROGRAM_AUTHOR = "Adakite";
  public static final String PROGRAM_TITLE = PROGRAM_NAME + " v" + PROGRAM_VERSION;
  public static final String PROGRAM_GITHUB = "https://github.com/adakitesystems/droplauncher";
  public static final String PROGRAM_LICENSE = "AGPL 3.0";
  public static final String PROGRAM_LICENSE_LINK = "https://www.gnu.org/licenses/agpl-3.0.en.html";

  /* Logging */
  public static final boolean DEBUG = true;
  public static final Level DEFAULT_LOG_LEVEL = Level.SEVERE;

  private Constants() {}

}
