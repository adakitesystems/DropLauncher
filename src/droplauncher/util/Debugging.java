package droplauncher.util;

import java.util.logging.Level;
import java.util.logging.Logger;

public class Debugging {

  private static final Logger LOGGER = Logger.getLogger(Debugging.class.getName());

  private static boolean isEnabled = true;
  private static Level defaultLevel = Level.SEVERE;
  private static Level defaultNonErrorLevel = Level.INFO;
  private static Level level = defaultLevel;

  private Debugging() {}

  public static boolean isEnabled() {
    return isEnabled;
  }

  public static void setEnabled(boolean enabled) {
    isEnabled = enabled;
  }

  public static Level getLoggerLevel() {
    return level;
  }

  public void setLoggerLevel(Level l) {
    LOGGER.log(Level.SEVERE, "logger level changing to: " + l.toString());
    level = l;
  }

}
