package droplauncher.mvc.view;

public enum MenuText {

  FILE("File"),
  SELECT_BOT_FILES("Select bot files..."),
  EXIT("Exit"),

  EDIT("Edit"),
  SETTINGS("Settings..."),

  HELP("Help"),
  ABOUT("About")
  ;

  private final String str;

  private MenuText(String str) {
    this.str = str;
  }

  @Override
  public String toString() {
    return this.str;
  }

}
