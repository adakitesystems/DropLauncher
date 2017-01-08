package droplauncher.bwheadless;

public enum PredefinedVariable {

  STARCRAFT_EXE("starcraft_exe"),
  BWAPI_DLL("bwapi_dll"),
  BOT_NAME("bot_name"),
  BOT_DLL("bot_dll"),
  BOT_CLIENT("bot_client"),
  BOT_RACE("bot_race"),
  GAME_TYPE("network");

  private String str;

  private PredefinedVariable(String str) {
    this.str = str;
  }

  @Override
  public String toString() {
    return this.str;
  }

}
