package droplauncher.bwheadless;

/**
 * Enum for reporting the launch status of bwheadless.
 */
public enum ReadyStatus {

  READY("Ready"),
  BWHEADLESS_EXE("bwheadless.exe"),
  STARTCRAFT_EXE("StarCraft.exe"),
  BWAPI_DLL("BWAPI.dll"),
  BOT_NAME("Bot Name"),
  BOT_FILE("Bot DLL/EXE"),
  BOT_RACE("Bot Race"),
  NETWORK_PROVIDER("Network Provider"),
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
