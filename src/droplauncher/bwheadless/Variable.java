package droplauncher.bwheadless;

public final class Variable {

  private String str;

  private Variable(String str) {
    this.str = str;
  }

  @Override
  public String toString() {
    return this.str;
  }

  public static final Variable STARCRAFT_EXE = new Variable("starcraft_exe");
  public static final Variable BWAPI_DLL = new Variable("bwapi_dll");
  public static final Variable BOT_NAME = new Variable("bot_name");
  public static final Variable BOT_DLL = new Variable("bot_dll");
  public static final Variable BOT_CLIENT = new Variable("bot_client");
  public static final Variable BOT_RACE = new Variable("bot_race");
  public static final Variable GAME_TYPE = new Variable("game_type");

}
