package droplauncher.bwheadless;

public final class Argument {

  private String str;

  private Argument(String str) {
    this.str = str;
  }

  @Override
  public String toString() {
    return this.str;
  }

  public static final Argument STARCRAFT_EXE = new Argument("-e"); /* requires second string */
  public static final Argument JOIN_GAME = new Argument("-j");
  public static final Argument BOT_NAME = new Argument("-n"); /* requires second argument */
  public static final Argument BOT_RACE = new Argument("-r"); /* requires second argument */
  public static final Argument LOAD_DLL = new Argument("-l"); /* requires second argument */
  public static final Argument ENABLE_LAN = new Argument("--lan");
  public static final Argument ENABLE_LOCAL_PC = new Argument("--localpc");
  public static final Argument STARCRAFT_INSTALL_PATH = new Argument("--installpath"); /* requires second argument */

}