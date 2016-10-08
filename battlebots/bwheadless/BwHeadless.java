/* BwHeadless.java */

package battlebots.bwheadless;

import battlebots.tools.ProcessPipe;

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

  private ProcessPipe _pipe;

  private BwHeadless() {
    _pipe = new ProcessPipe();
  }

  /**
   * Starts and connects to the specified process.
   *
   * @param processPath path to the specified process
   * @return true if not previously open, is now successfully open,
   *     and no errors were encountered,
   *     otherwise false
   */
  public boolean connect(String processPath) {
    return _pipe.open(processPath);
  }

  /**
   * Closes pipe and destroys the process.
   *
   * @return true if all pipe-related objects from ProcessPipe were closed
   *     and no errors were encountered,
   *     otherwise false
   */
  public boolean disconnect() {
    return _pipe.close();
  }

}
