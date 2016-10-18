package droplauncher.bwheadless;

/**
 * @author Adakite Systems
 * @author adakitesystems@gmail.com
 */
public final class PredefinedVariables {

  private String str;

  private PredefinedVariables(String str) {
    this.str = str;
  }

  @Override
  public String toString() {
    return this.str;
  }

  public static final PredefinedVariables STARCRAFT_EXE = new PredefinedVariables("starcraft_exe");
  public static final PredefinedVariables BWAPI_DLL = new PredefinedVariables("bwapi_dll");
  public static final PredefinedVariables BOT_NAME = new PredefinedVariables("bot_name");
  public static final PredefinedVariables BOT_DLL = new PredefinedVariables("bot_dll");
  public static final PredefinedVariables BOT_CLIENT = new PredefinedVariables("bot_client");
  public static final PredefinedVariables BOT_RACE = new PredefinedVariables("bot_race");
  public static final PredefinedVariables GAME_TYPE = new PredefinedVariables("network");

}
