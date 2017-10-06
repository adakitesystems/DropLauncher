package adakite.settings;

import adakite.util.AdakiteUtils;
import adakite.util.AdakiteUtils.StringCompareOption;
import java.util.Enumeration;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Class for storing settings pairs in memory.
 */
public class Settings {

  private ConcurrentHashMap<String, String> settings;

  public Settings() {
    this.settings = new ConcurrentHashMap<>();
  }

  public Settings(Settings settings) {
    this.settings = new ConcurrentHashMap<>();
    Enumeration<String> enums = settings.getKeys();
    while (enums.hasMoreElements()) {
      String key = enums.nextElement();
      set(key, settings.getValue(key));
    }
  }

  public boolean containsKey(String key) {
    return this.settings.containsKey(key);
  }

  public Enumeration<String> getKeys() {
    return this.settings.keys();
  }

  /**
   * Sets the specified key with the specified value.
   *
   * @param key specified key
   * @param value specified value
   */
  public void set(String key, String value) {
    this.settings.put(key, value);
  }

  /**
   * Removes the key (and its corresponding value) from this map.
   * This method does nothing if the key is not in the map.
   *
   * @param key the key that needs to be removed
   * @return
   *     the previous value associated with key,
   *     otherwise null if there was no mapping for key
   */
  public String remove(String key) {
    return this.settings.remove(key);
  }

  /**
   * Returns the value associated with the specified key.
   *
   * @param key specified key
   */
  public String getValue(String key) {
    return this.settings.get(key);
  }

  /**
   * Tests whether the specified key has an associated value.
   *
   * @param key specified key
   */
  public boolean hasValue(String key) {
    return (!AdakiteUtils.isNullOrEmpty(getValue(key), StringCompareOption.TRIM));
  }

}
