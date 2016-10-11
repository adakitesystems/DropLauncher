/* ConfigVariable.java */

package droplauncher.config;

import droplauncher.tools.MainTools;

import java.util.logging.Logger;

/**
 * Class for handling variables read from configuration files.
 *
 * @author Adakite Systems
 * @author adakitesystems@gmail.com
 */
public class ConfigVariable {

  private static final Logger LOGGER = Logger.getLogger(ConfigVariable.class.getName());
  private static final boolean CLASS_DEBUG = (MainTools.DEBUG && true);

  private String _name;
  private String _value;

  public ConfigVariable() {
    _name = null;
    _value = null;
  }

  public ConfigVariable(String name, String value) {
    _name = name;
    _value = value;
  }

  public String getName() {
    return _name;
  }

  public void setName(String name) {
    _name = name;
  }

  public String getValue() {
    return _value;
  }

  public void setValue(String value) {
    _value = value;
  }

}
