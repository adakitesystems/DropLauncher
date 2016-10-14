/* DropLauncher.java */

package droplauncher;

import java.util.logging.Logger;

/**
 * @author Adakite Systems
 * @author adakitesystems@gmail.com
 */
public class DropLauncher {

  public static final DropLauncher INSTANCE = new DropLauncher();

  private DropLauncher() {}

  private static final Logger LOGGER = Logger.getLogger(DropLauncher.class.getName());

  public static final String PROGRAM_NAME = "DropLauncher";
  public static final String PROGRAM_VERSION = "0.01a";

}
