package droplauncher.bwheadless;

public enum KillableTask {

  BWHEADLESS_EXE("bwheadless.exe"),
  CONHOST_EXE("conhost.exe"),
  STARCRAFT_EXE("StarCraft.exe"),
  TASKLIST_EXE("tasklist.exe"),
  DLLHOST_EXE("dllhost.exe")
  ;

  private String str;

  private KillableTask(String str) {
    this.str = str;
  }

  @Override
  public String toString() {
    return this.str;
  }

}
