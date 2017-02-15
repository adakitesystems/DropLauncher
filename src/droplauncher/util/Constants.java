/*
Software versioning:

major.minor[.build[status]]

Statuses:
a for alpha
b for beta
rc for release candidate
omit for (final) release
*/

package droplauncher.util;

import adakite.util.AdakiteUtils;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Constants class for global constants.
 */
public class Constants {

  public static final String PROGRAM_NAME = "DropLauncher";
  public static final String PROGRAM_VERSION = "0.2.2a";
  public static final String PROGRAM_AUTHOR = "Adakite";
  public static final String PROGRAM_TITLE = PROGRAM_NAME + " v" + PROGRAM_VERSION;
  public static final String PROGRAM_GITHUB = "https://github.com/AdakiteSystems/DropLauncher";
  public static final String PROGRAM_LICENSE = "AGPL 3.0";
  public static final String PROGRAM_LICENSE_LINK = "https://www.gnu.org/licenses/agpl-3.0.en.html";
  public static final String PROGRAM_DESC = "Designed to be a simple tool to aid players in setting up and playing 1v1 StarCraft: Brood War against BWAPI bots using Local Area Network (UDP) as the connection type.";
  public static final String PROGRAM_ABOUT
      = Constants.PROGRAM_NAME + AdakiteUtils.newline()
      + "Author: " + Constants.PROGRAM_AUTHOR + AdakiteUtils.newline()
      + "Version: " + Constants.PROGRAM_VERSION + AdakiteUtils.newline(2)
      + Constants.PROGRAM_DESC + AdakiteUtils.newline(2)
      + "License: " + Constants.PROGRAM_LICENSE + AdakiteUtils.newline()
      + Constants.PROGRAM_LICENSE_LINK + AdakiteUtils.newline(2)
      + "Source:" + AdakiteUtils.newline()
      + Constants.PROGRAM_GITHUB + AdakiteUtils.newline()
      ;

  public static final Path DROPLAUNCHER_INI_PATH = Paths.get("droplauncher.ini");
  public static final String DROPLAUNCHER_INI_SECTION_NAME = "droplauncher";

  public static final String TEMP_DIRECTORY = "tmp";

  private Constants() {}

}
