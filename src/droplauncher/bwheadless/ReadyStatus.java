package droplauncher.bwheadless;

public enum ReadyStatus {

  READY("Ready"),
  STARTCRAFT_EXE("StarCraft.exe"),
  BWAPI_DLL("BWAPI.dll"),
  BOT_NAME("Bot Name"),
  BOT_FILE("Bot DLL/EXE"),
  BOT_RACE("Bot Race"),
  GAME_TYPE("Game Type"),
  JOIN_MODE("Join Mode")
  ;

  public String name;

  private ReadyStatus(String name) {
    this.name = name;
  }

  @Override
  public String toString() {
    return this.name;
  }

}
