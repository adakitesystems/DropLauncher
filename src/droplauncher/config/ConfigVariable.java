/* ConfigVariable.java */

package droplauncher.config;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Class for handling variables read from configuration files.
 *
 * @author Adakite Systems
 * @author adakitesystems@gmail.com
 */
public class ConfigVariable {

  private static final Logger LOGGER = LogManager.getRootLogger();

  private String name;
  private String value;

  public ConfigVariable() {
    this.name = null;
    this.value = null;
  }

  public ConfigVariable(String name, String value) {
    this.name = name;
    this.value = value;
  }

  public String getName() {
    return this.name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getValue() {
    return this.value;
  }

  public void setValue(String value) {
    this.value = value;
  }

}
