package droplauncher.main;

import droplauncher.mvc.MVC;
import droplauncher.util.SimpleProcess;
import droplauncher.util.Util;
import droplauncher.util.windows.Tasklist;
import java.io.File;
import java.util.ArrayList;

public class Main {

  public static void main(String[] args) {
//    MVC mvc = new MVC(args);

//    SimpleProcess process = new SimpleProcess();
//    ArrayList<String> pargs = new ArrayList<>();
//    pargs.add("/v");
//    process.run(new File("C:\\Windows\\System32\\tasklist.exe"), Util.toStringArray(pargs));
////    process.run(new File("C:\\Windows\\System32\\tasklist.exe"), null);
//    for (String line : process.getLog()) {
//      System.out.println(line);
//    }
    Tasklist tl = new Tasklist();
    tl.update();
  }

}
