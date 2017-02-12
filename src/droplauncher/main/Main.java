package droplauncher.main;

import droplauncher.mvc.MVC;
import droplauncher.util.Constants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Main {

  private static final Logger LOGGER = LogManager.getLogger();

  public static void main(String[] args) {
    System.out.println(Constants.PROGRAM_ABOUT);

    LOGGER.info("start of main");

    new MVC();
    
    LOGGER.info("end of main");
  }

}
