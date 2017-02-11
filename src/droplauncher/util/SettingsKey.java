package droplauncher.util;

public enum SettingsKey {

  /* Program keys */
  SHOW_LOG_WINDOW("show_log_window"),

  /* BWHeadless keys */
  STARCRAFT_EXE("starcraft_exe"),
  BWAPI_DLL("bwapi_dll"),
  BOT_NAME("bot_name"),
  BOT_FILE("bot_file"),
  BOT_RACE("bot_race"),
  NETWORK_PROVIDER("network"),
  CONNECT_MODE("connect_mode"),
  GAME_NAME("game_name"),
  MAP("map")
  ;

  private String str;

  private SettingsKey(String str) {
    this.str = str;
  }

  @Override
  public String toString() {
    return this.str;
  }

}
