package droplauncher.bwheadless;

/**
 * Enum for reporting the launch status of bwheadless.
 */
public enum ReadyError {

  NONE("OK"),
  BWHEADLESS_EXE("unable to read/locate bwheadless.exe"),
  STARTCRAFT_EXE("unable to read/locate StarCraft.exe"),
  BWAPI_DLL("unable to read/locate BWAPI.dll"),
  BOT_NAME("invalid bot name"),
  BOT_FILE("unable to read/locate bot file (*.dll, *.exe)"),
  BOT_RACE("invalid bot race"),
  NETWORK_PROVIDER("invalid network provider"),
  CONNECT_MODE("invalid connect mode"),
  BWAPI_INSTALL("corrupt or missing BWAPI installation"),
  JRE_INSTALL("corrupt or missing JRE installation")
  ;

  private final String name;

  private ReadyError(String name) {
    this.name = name;
  }

  @Override
  public String toString() {
    return this.name;
  }

}
