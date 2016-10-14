package droplauncher.bwheadless;

public final class PredefinedVariable {

  private String str;

  private PredefinedVariable(String str) {
    this.str = str;
  }

  @Override
  public String toString() {
    return this.str;
  }

  public static final PredefinedVariable STARCRAFT_EXE = new PredefinedVariable("starcraft_exe");
  public static final PredefinedVariable BWAPI_DLL = new PredefinedVariable("bwapi_dll");
  public static final PredefinedVariable BOT_NAME = new PredefinedVariable("bot_name");
  public static final PredefinedVariable BOT_DLL = new PredefinedVariable("bot_dll");
  public static final PredefinedVariable BOT_CLIENT = new PredefinedVariable("bot_client");
  public static final PredefinedVariable BOT_RACE = new PredefinedVariable("bot_race");
  public static final PredefinedVariable GAME_TYPE = new PredefinedVariable("game_type");

}
