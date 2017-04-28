package adakite.ini;

import adakite.settings.Settings;
import adakite.util.AdakiteUtils;
import adakite.util.MemoryFile;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Enumeration;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

public class Ini {

  private static final Logger LOGGER = Logger.getLogger(Ini.class.getName());

  private static class Section {

    private String name;
    private Settings settings;

    private Section() {}

    public Section(String name) {
      this.name = name;
      this.settings = new Settings();
    }

    public String getName() {
      return this.name;
    }

    public Settings getSettings() {
      return this.settings;
    }

  }

  public static final String FILE_EXTENSION = ".ini";
  public static final char VARIABLE_DELIMITER = '=';
  public static final char COMMENT_DELIMITER = ';';
  public static final String NULL_SECTION_NAME = "";

  private MemoryFile memoryFile;
  private ConcurrentHashMap<String, Section> sections;

  public Ini() {
    this.memoryFile = new MemoryFile();
    this.sections = new ConcurrentHashMap<>();
    clear();
  }

  private void clear() {
    this.memoryFile.clear();
    this.sections.clear();
    this.sections.put(NULL_SECTION_NAME, new Section(NULL_SECTION_NAME));
  }

  /**
   * Parses the specified INI file.
   *
   * @param path path to the specified file to parse
   * @throws IOException if an I/O error occurs
   * @throws IniParseException
   */
  public void parse(Path path) throws IOException, IniParseException {
    clear();

    this.memoryFile.read(path);

    String currSection = NULL_SECTION_NAME;
    int i = 0;
    while (i < this.memoryFile.getLines().size()) {
      String line = this.memoryFile.getLines().get(i);
      if (AdakiteUtils.isNullOrEmpty(line)
          || AdakiteUtils.isNullOrEmpty(removeComment(line), true)) {
        /* Line does not contain any data. Skip it. */
        i++;
      } else if (isSectionHeader(line)) {
        /* Add the section and go to the next loop iteration. */
        String sectionName = parseSectionName(line);
        this.sections.put(sectionName, new Section(sectionName));
        currSection = sectionName;
        i++;
      } else {
        /* Add the variable. */
        String key = parseKey(line);
        String value = parseValue(line);
        if (AdakiteUtils.isNullOrEmpty(key)) {
          throw new IniParseException(path.toString() + ":" + (i + 1) + ":" + line);
        }
        if (AdakiteUtils.isNullOrEmpty(value)) {
          value = "";
        }
        this.sections.get(currSection).getSettings().set(key, value);
        i++;
      }
    }
  }

  public String getValue(String name, String key) {
    if (AdakiteUtils.isNullOrEmpty(name)) {
      name = NULL_SECTION_NAME;
    }
    if (AdakiteUtils.isNullOrEmpty(key)) {
      throw new IllegalArgumentException("key cannot be null or empty");
    }
    if (!this.sections.containsKey(name)) {
      return null;
    }
    return this.sections.get(name).getSettings().getValue(key);
  }

  public boolean hasValue(String name, String key) {
    return (getValue(name, key) != null);
  }

  public boolean isEnabled(String name, String key) {
    return (hasValue(name, key) && getValue(name, key).equalsIgnoreCase(Boolean.TRUE.toString()));
  }

  public void setEnabled(String name, String key, boolean enabled) throws IniParseException {
    set(name, key, Boolean.toString(enabled));
  }

  public void set(String name, String key, String value) {
    uncommentVariable(name, key);

    int sectionIndex = getSectionIndex(name);
    if (sectionIndex < 0) {
      /* Add section and variable to settings. */
      this.sections.put(name, new Section(name));
      this.sections.get(name).getSettings().set(key, value);
      /* Add section and variable to memory file. */
      String line = key + VARIABLE_DELIMITER + value;
      this.memoryFile.getLines().add("[" + name + "]");
      this.memoryFile.getLines().add(line);
    } else {
      int keyIndex = getKeyIndex(name, key);
      if (keyIndex < 0) {
        /* Add key to settings. */
        this.sections.get(name).getSettings().set(key, value);
        /* Add key to the end of the section in the memory file. */
        int i = sectionIndex + 1;
        while (i < this.memoryFile.getLines().size() && !isSectionHeader(this.memoryFile.getLines().get(i))) {
          i++;
        }
        if (i >= this.memoryFile.getLines().size()) {
          /* End of file reached. Add here. */
          String line = key + VARIABLE_DELIMITER + value;
          this.memoryFile.getLines().add(line);
        } else {
          /* Found end of section. Add here. */
          i--; /* Rewind from current header to end of previous section. */
          String line = key + VARIABLE_DELIMITER + value;
          this.memoryFile.getLines().add(i, line);
        }
      } else {
        /* Change key to new value in settings. */
        this.sections.get(name).getSettings().set(key, value);
        /* Change key to new value in memory file. */
        String line = this.memoryFile.getLines().get(keyIndex);
        String comment = getComment(line);
        line = removeComment(line);
        String parsedKey = parseKey(line);
        String modifiedLine = parsedKey + VARIABLE_DELIMITER + value;
        if (!AdakiteUtils.isNullOrEmpty(comment)) {
          modifiedLine += " " + COMMENT_DELIMITER + " " + comment;
        }
        this.memoryFile.getLines().set(keyIndex, modifiedLine);
      }
    }
  }

  /**
   * Disables the variable by placing a character delimiter at the beginning
   * of the line.
   *
   * @param name specified section name
   * @param key specified key
   */
  public void commentVariable(String name, String key) {
    int keyIndex = getKeyIndex(name, key);
    if (keyIndex < 0) {
//      throw new IniParseException("variable not found: section=" + name + ", key=" + key);
      return;
    }
    String line = COMMENT_DELIMITER + this.memoryFile.getLines().get(keyIndex).trim();
    this.memoryFile.getLines().set(keyIndex, line);
    this.sections.get(name).settings.remove(key);
  }

  public void uncommentVariable(String name, String key) {
    int sectionIndex = getSectionIndex(name);
    if (sectionIndex < 0) {
//      throw new IniParseException("section not found: " + name);
      return;
    }
    if (getKeyIndex(name, key) >= 0) {
//      throw new IniParseException("variable is not commented: section=" + name + ", key=" + key);
      return;
    }
    for (int i = sectionIndex + 1; i < this.memoryFile.getLines().size(); i++) {
      String line = this.memoryFile.getLines().get(i).trim();
      if (AdakiteUtils.isNullOrEmpty(line)) {
        continue;
      }
      if (isSectionHeader(line)) {
//        throw new IniParseException("reached end of section before uncommenting variable: section=" + name + ", key=" + key);
        return;
      }
      int commentIndex = line.indexOf(COMMENT_DELIMITER);
      if (commentIndex < 0) {
        continue;
      }
      line = line.substring(commentIndex + 1, line.length()).trim();
      String parsedKey = parseKey(line);
      String parsedValue = parseValue(line);
      if (AdakiteUtils.isNullOrEmpty(parsedKey)) {
        continue;
      }
      if (parsedKey.equalsIgnoreCase(key)) {
        this.memoryFile.getLines().set(i, line);
        this.sections.get(name).settings.set(parsedKey, parsedValue);
        return;
      }
    }
//    throw new IniParseException("reached end of file before uncommenting variable: section=" + name + ", key=" + key);
  }

  public void store(Path path) throws IOException {
    this.memoryFile.dumpToFile(path);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    for (String name : this.sections.keySet()) {
      sb.append("[" + name + "]" + AdakiteUtils.newline());
      Section section = this.sections.get(name);
      Enumeration<String> keys = section.getSettings().getKeys();
      while (keys.hasMoreElements()) {
        String key = keys.nextElement();
        sb.append("  " + key + VARIABLE_DELIMITER + section.getSettings().getValue(key) + AdakiteUtils.newline());
      }
    }
    return sb.toString();
  }

  private boolean isSectionHeader(String str) {
    if (AdakiteUtils.isNullOrEmpty(str)) {
      return false;
    }
    return (str.length() >= 3 && str.startsWith("[") && str.endsWith("]"));
  }

  private String parseSectionName(String str) {
    return (str.length() < 3) ? null : str.substring(1, str.length() - 1).trim();
  }

  /**
   * Returns the key in the specified string.
   *
   * @param str specified string
   * @return
   *     the key in the specified string if it exists,
   *     otherwise null
   */
  private String parseKey(String str) {
    int varIndex = str.indexOf(VARIABLE_DELIMITER);
    return (varIndex < 1) ? null : str.substring(0, varIndex).trim();
  }

  /**
   * Returns the value in the specified string.
   *
   * @param str specified string
   * @return
   *     the non-empty value in the specified string if it exists,
   *     otherwise null if a key does not exist,
   *     otherwise an empty string if the value is empty
   */
  private String parseValue(String str) {
    int varIndex = str.indexOf(VARIABLE_DELIMITER);
    if (varIndex < 1) {
      return null;
    } else if (varIndex + 1 >= str.length()) {
      return "";
    } else {
      return str.substring(varIndex + 1, str.length()).trim();
    }
  }

  private String getComment(String str) {
    int commentIndex = str.indexOf(COMMENT_DELIMITER);
    return (commentIndex < 0) ? null : str.substring(commentIndex + 1, str.length()).trim();
  }

  /**
   * Returns the specified string excluding a comment if present.
   *
   * @param str specified string to scan
   * @return
   *     the specified string excluding a comment if present,
   *     otherwise an empty string
   */
  private String removeComment(String str) {
    int commentIndex = str.indexOf(COMMENT_DELIMITER);
    return (commentIndex < 0) ? str : str.substring(0, commentIndex).trim();
  }

  private int getSectionIndex(String name) {
    for (int i = 0; i < this.memoryFile.getLines().size(); i++) {
      String line = this.memoryFile.getLines().get(i);
      if (AdakiteUtils.isNullOrEmpty(line)
          || AdakiteUtils.isNullOrEmpty(removeComment(line), true)) {
        continue;
      }
      if (isSectionHeader(line) && parseSectionName(line).equalsIgnoreCase(name)) {
        return i;
      }
    }
    return -1;
  }

  private int getKeyIndex(String name, String key) {
    int sectionIndex = getSectionIndex(name);
    if (sectionIndex < 0) {
      return -1;
    }
    for (int i = sectionIndex + 1; i < this.memoryFile.getLines().size(); i++) {
      String line = this.memoryFile.getLines().get(i);
      if (AdakiteUtils.isNullOrEmpty(line)
          || AdakiteUtils.isNullOrEmpty(removeComment(line), true)) {
        continue;
      }
      if (isSectionHeader(line)) {
        /* End of section reached. */
        return -1;
      }
      String parsedKey = parseKey(line);
      if (parsedKey.equalsIgnoreCase(key)) {
        return i;
      }
    }
    return -1;
  }

}
