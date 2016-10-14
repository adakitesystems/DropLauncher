/* Argument.java */

package droplauncher.bwheadless;

/**
 * @author Adakite Systems
 * @author adakitesystems@gmail.com
 */
public final class Arguments {

  private String str;

  private Arguments(String str) {
    this.str = str;
  }

  @Override
  public String toString() {
    return this.str;
  }

  public static final Arguments STARCRAFT_EXE = new Arguments("-e"); /* requires second string */
  public static final Arguments JOIN_GAME = new Arguments("-j");
  public static final Arguments BOT_NAME = new Arguments("-n"); /* requires second argument */
  public static final Arguments BOT_RACE = new Arguments("-r"); /* requires second argument */
  public static final Arguments LOAD_DLL = new Arguments("-l"); /* requires second argument */
  public static final Arguments ENABLE_LAN = new Arguments("--lan");
  public static final Arguments ENABLE_LOCAL_PC = new Arguments("--localpc");
  public static final Arguments STARCRAFT_INSTALL_PATH =
      new Arguments("--installpath"); /* requires second argument */

}