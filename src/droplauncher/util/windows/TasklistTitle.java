package droplauncher.util.windows;

public enum TasklistTitle {

  IMAGE_NAME("Image Name"),
  PID("PID"),
  SESSION_NAME("Session Name"),
  SESSION_NUMBER("Session Number"),
  MEM_USAGE("Mem Usage"),
  STATUS("Status"),
  USERNAME("User Name"),
  CPU_TIME("CPU Time"),
  WINDOW_TITLE("Window Title")
  ;

  private String str;

  private TasklistTitle(String str) {
    this.str = str;
  }

  @Override
  public String toString() {
    return this.str;
  }

}
