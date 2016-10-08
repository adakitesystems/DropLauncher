/* BwHeadless.java */

package battlebots.bwheadless;

import java.util.logging.Logger;

/**
 * Singleton class for handling communication with "bwheadless.exe".
 *
 * @author Adakite Systems
 * @author adakitesystems@gmail.com
 */
public class BwHeadless {

  public static final BwHeadless INSTANCE = new BwHeadless();

  private static final Logger LOGGER = Logger.getLogger(BwHeadless.class.getName());

  private BwHeadless() {

  }

}
