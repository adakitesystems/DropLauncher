package droplauncher.bwheadless;

/**
 * Enum for the names of tasks that should be killed when the bot is
 * stopped.
 */
public enum KillableTask {

  BWHEADLESS_EXE("bwheadless.exe"),
  CONHOST_EXE("conhost.exe"),
  STARCRAFT_EXE("StarCraft.exe"),
  TASKLIST_EXE("tasklist.exe"),
  DLLHOST_EXE("dllhost.exe"),
  ;

  private final String str;

  private KillableTask(String str) {
    this.str = str;
  }

  @Override
  public String toString() {
    return this.str;
  }

}
