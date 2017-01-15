package droplauncher.util;

import adakite.utils.AdakiteUtils;
import java.util.HashMap;

/**
 * Class for storing dynamic settings and variables.
 */
public class Settings {

  private HashMap<String, String> variables;

  public Settings() {
    init();
  }

  private void init() {
    this.variables = new HashMap<>();
  }

  public void clear() {
    this.variables.clear();
  }

  public HashMap<String, String> getVariables() {
    return this.variables;
  }

  public boolean isVariableSet(String key) {
    return !AdakiteUtils.isNullOrEmpty(this.variables.get(key));
  }

  /**
   * Updates/creates the corresponding variable to the specified key.
   * If the variable already exists, it will be updated with the specified
   * value, otherwise the specified key and value will be added as a
   * new variable.
   *
   * @param key specified key
   * @param val specified value
   * @return
   *     true if the variable was updated successfully,
   *     otherwise false
   */
  public boolean setVariable(String key, String val) {
    if (this.variables.containsKey(key)) {
      this.variables.remove(key);
    }
    this.variables.put(key, val);
    return true;
  }

  public String getValue(String key) {
    if (!this.variables.containsKey(key)) {
      return null;
    } else {
      return this.variables.get(key);
    }
  }

}
