package droplauncher.main;

import droplauncher.ini.IniFile;
import droplauncher.mvc.MVC;
import java.io.File;

public class Main {

  public static void main(String[] args) {
//    MVC mvc = new MVC(args);
    IniFile ini = new IniFile();
    ini.open(new File("bwapi.ini"));
    ini.setVariable("ai", "ai2", "bot2.dll");
  }

}
