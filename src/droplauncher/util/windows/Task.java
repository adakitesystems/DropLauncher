package droplauncher.util.windows;

/**
 * Data container class for process information obtained from
 * Window's Tasklist.
 */
public class Task {

  private String imageName;
  private String pid;
  private String sessionName;
  private String sessionNumber;
  private String memUsage;
  private String status;
  private String username;
  private String cpuTime;
  private String windowTitle;

  public Task() {
    this.imageName = "";
    this.pid = "";
    this.sessionName = "";
    this.sessionNumber = "";
    this.memUsage = "";
    this.status = "";
    this.username = "";
    this.cpuTime = "";
    this.windowTitle = "";
  }

  public Task(Task task) {
    this.imageName = task.getImageName();
    this.pid = task.getPID();
    this.sessionName = task.getSessionName();
    this.sessionNumber = task.getSessionNumber();
    this.memUsage = task.getMemUsage();
    this.status = task.getStatus();
    this.username = task.getUsername();
    this.cpuTime = task.getCpuTime();
    this.windowTitle = task.getWindowTitle();
  }

  public String getImageName() {
    return this.imageName;
  }

  public void setImageName(String imageName) {
    this.imageName = imageName;
  }

  public String getPID() {
    return this.pid;
  }

  public void setPID(String pid) {
    this.pid = pid;
  }

  public String getSessionName() {
    return this.sessionName;
  }

  public void setSessionName(String sessionName) {
    this.sessionName = sessionName;
  }

  public String getSessionNumber() {
    return this.sessionNumber;
  }

  public void setSessionNumber(String sessionNumber) {
    this.sessionNumber = sessionNumber;
  }

  public String getMemUsage() {
    return this.memUsage;
  }

  public void setMemUsage(String memUsage) {
    this.memUsage = memUsage;
  }

  public String getStatus() {
    return this.status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public String getUsername() {
    return this.username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getCpuTime() {
    return this.cpuTime;
  }

   public void setCpuTime(String cpuTime) {
    this.cpuTime = cpuTime;
  }

  public String getWindowTitle() {
    return this.windowTitle;
  }

  public void setWindowTitle(String windowTitle) {
    this.windowTitle = windowTitle;
  }

}
