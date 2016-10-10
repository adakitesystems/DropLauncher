/* ConfigVariable.java */

package battlebots.tools.config;

import java.util.logging.Logger;

/**
 * Class for handling variables and values read or written to configuration
 * files associated with this program.
 *
 * @author Adakite Systems
 * @author adakitesystems@gmail.com
 */
public class ConfigVariable {

  private static final Logger LOGGER = Logger.getLogger(ConfigVariable.class.getName());

  private String _name;
  private String _value;

  public ConfigVariable() {
    reset();
  }

  public void reset() {
    _name = "";
    _value = "";
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

  @Override
  public String toString() {
    return (_name + " = " + _value);
  }

}
