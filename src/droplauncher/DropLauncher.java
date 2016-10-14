/* DropLauncher.java */

package droplauncher;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author Adakite Systems
 * @author adakitesystems@gmail.com
 */
public class DropLauncher {

  public static final DropLauncher INSTANCE = new DropLauncher();

  private DropLauncher() {}

  private static final Logger LOGGER = LogManager.getRootLogger();

  public static final String PROGRAM_NAME = "DropLauncher";
  public static final String PROGRAM_VERSION = "0.01a";

}
