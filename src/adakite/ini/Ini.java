package adakite.ini;

import adakite.debugging.Debugging;
import adakite.util.AdakiteUtils;
import adakite.util.MemoryFile;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;

/**
 * Class for manipulating a Windows INI file while attemping to preserve
 * the file's original structure.
 */
public class Ini {

  public static final String FILE_EXTENSION = ".ini";
  public static final String VARIABLE_DELIMITER = "=";
  public static final String COMMENT_DELIMITER = ";";

  private MemoryFile memoryFile;
  private HashMap<String, IniSection> sections;

  public Ini() {
    this.memoryFile = new MemoryFile();
    this.sections = new HashMap<>();

    this.sections.clear();
    this.sections.put(IniSectionName.NONE.toString(), new IniSection());
  }

  public HashMap<String, IniSection> getSections() {
    return this.sections;
  }

  /**
   * Clears relevant class members and, specifically, the HashMap object
   * which contains the Section objects. Always adds a section with no
   * name to hold the place of variables which do not belong to any section.
   */
  private void clear() {
    this.memoryFile.getLines().clear();
    this.sections.clear();
    this.sections.put(IniSectionName.NONE.toString(), new IniSection());
  }

  /**
   * Reads the specified INI file and parses its variables.
   *
   * @param path specified path to the INI file
   * @throws IOException
   */
  public void read(Path path) throws IOException {
    clear();

    /* Create a copy of the file into a MemoryFile object. */
    this.memoryFile.read(path);

    /* Parse sections and variables into the class HashMap object. */
    String currSection = IniSectionName.NONE.toString();
    List<String> lines = this.memoryFile.getLines();
    for (int i = 0; i < lines.size(); i++) {
      String line = lines.get(i);
      line = line.trim();
      if (line.isEmpty() || line.startsWith(COMMENT_DELIMITER)) {
        continue;
      }
      int commentIndex = line.indexOf(COMMENT_DELIMITER.charAt(0));
      if (commentIndex > 0) {
        line = line.substring(0, commentIndex).trim();
      }
      if (line.startsWith("[")) {
        if (!line.endsWith("]")) {
          /* Skip entire section if something appears off with
             the section header. */
          i++;
          while (!lines.get(i).startsWith("[") && i < lines.size()) {
            i++;
          }
          if (i < lines.size()) {
            /* Since we are not at EOF, rewind so the next loop iteration
               can read this line as a new section. */
            i--;
            /* This is an intentional unnecessary continue statement. */
            continue;
          }
        } else {
          /* New section */
          String name = line.substring(1, line.length() - 1).trim();
          this.sections.put(name, new IniSection(name));
          currSection = name;
        }
      } else {
        /* New variable */
        int varIndex = line.indexOf(VARIABLE_DELIMITER.charAt(0));
        if (varIndex < 0) {
          /* Skip line if the variable delimiter is not found. */
          continue;
        }
        String key = line.substring(0, varIndex).trim();
        String val = line.substring(varIndex + VARIABLE_DELIMITER.length(), line.length()).trim();
        this.sections.get(currSection).getKeys().put(key, val);
      }
    }
  }

  /**
   * Sets the specified variable and immediately writes the changes to disk.
   *
   * @param name specified section name
   * @param key specified key
   * @param val specified value
   */
  public void set(String name, String key, String val) {
    if (name == null) {
      name = "";
    }
    if (AdakiteUtils.isNullOrEmpty(key, true)) {
      throw new IllegalArgumentException(Debugging.cannotBeNullOrEmpty("key"));
    }
    if (val == null) {
      val = "";
    }

    enableVariable(name, key);

    boolean sectionExists = this.sections.containsKey(name);
    boolean keyExists = false;

    if (sectionExists) {
      /* If the section already exists, remove the key from the section.
         The HashMap will check whether or not the key exists before removing. */
      keyExists = ((this.sections.get(name).getKeys().remove(key)) != null);
    } else {
      /* Add the section. */
      this.sections.put(name, new IniSection(name));
    }

    /* Add the variable to the section. */
    this.sections.get(name).getKeys().put(key, val);

    /* Modify the memory file. */
    if (sectionExists) {
      int sectionIndex = getSectionIndex(name);
      if (keyExists) {
        /* Update the variable in-place. */
        int keyIndex = getKeyIndex(name, key);
        String line = this.memoryFile.getLines().get(keyIndex);
        String comment = getComment(line);
        String updated = key + VARIABLE_DELIMITER + val;
        if (comment != null) {
          comment = comment.trim();
          updated += " " + COMMENT_DELIMITER + comment;
        }
        this.memoryFile.getLines().set(keyIndex, updated);
      } else {
        /* Find the end of the section. */
        int i;
        if (name.equalsIgnoreCase(IniSectionName.NONE.toString())) {
          i = sectionIndex;
        } else {
          i = sectionIndex + 1;
        }
        while (i < this.memoryFile.getLines().size()
            && !(this.memoryFile.getLines().get(i).startsWith("[")
              && this.memoryFile.getLines().get(i).endsWith("]"))) {
          i++;
        }
        /* Add the variable. */
        this.memoryFile.getLines().add(i, key + VARIABLE_DELIMITER + val);
      }
    } else {
      /* Add the section at the end of the file and add the variable.
         By default, the INI protocol does not accept blank lines. So,
         don't separate the new section from the previous section. */
      String formattedName = "[" + name + "]";
      this.memoryFile.getLines().add(formattedName);
      this.memoryFile.getLines().add(key + VARIABLE_DELIMITER + val);
    }
  }

  /**
   * If the sepcified variable is present in the INI file but disabled via
   * a comment character, it will be enabled by removing the comment character.
   *
   * @param name specified section name
   * @param key specified key
   */
  public void enableVariable(String name, String key) {
    if (name == null) {
      name = IniSectionName.NONE.toString();
    }
    if (AdakiteUtils.isNullOrEmpty(key, true)) {
      throw new IllegalArgumentException(Debugging.cannotBeNullOrEmpty("key"));
    }

    if (this.sections.containsKey(name)) {
      int sectionIndex = getSectionIndex(name);
      /* Section exists. */
      if (!this.sections.get(name).getKeys().containsKey(key)) {
        /* Variable needs to be uncommented. */
        for (int i = sectionIndex; i < this.memoryFile.getLines().size(); i++) {
          String line = this.memoryFile.getLines().get(i);
          line = line.trim();
          if (line.startsWith(COMMENT_DELIMITER)
              && line.contains(key)
              && line.contains(VARIABLE_DELIMITER)
              && line.indexOf(COMMENT_DELIMITER.charAt(0)) < line.indexOf(key)
              && line.indexOf(key) < line.indexOf(VARIABLE_DELIMITER.charAt(0))) {
            int commentIndex = line.indexOf(COMMENT_DELIMITER.charAt(0));
            line = line.substring(commentIndex + COMMENT_DELIMITER.length(), line.length()).trim();
            this.memoryFile.getLines().set(i, line);
            break;
          }
        }
      }
    }
  }

  /**
   * If the sepcified variable is present in the INI file and enabled,
   * it will be disabled by adding a comment character to the beginning of
   * the line.
   *
   * @param name specified section name
   * @param key specified key
   */
  public void disableVariable(String name, String key) {
    if (name == null) {
      name = "";
    }
    if (AdakiteUtils.isNullOrEmpty(key, true)) {
      throw new IllegalArgumentException(Debugging.cannotBeNullOrEmpty("key"));
    }

    int keyIndex = getKeyIndex(name, key);
    if (keyIndex < 0) {
      /* Key not found. */
      return;
    }
    /* Key found, disable it. */
    String line = this.memoryFile.getLines().get(keyIndex);
    line = COMMENT_DELIMITER + line;
    this.memoryFile.getLines().set(keyIndex, line);
  }

  /**
   * Tests if the current variable is set to TRUE. Note: This is different
   * from {@link #enableVariable(java.lang.String, java.lang.String)} and
   * {@link #disableVariable(java.lang.String, java.lang.String)}.
   *
   * @param name specified section name
   * @param key specified key
   */
  public boolean isEnabled(String name, String key) {
    return (hasValue(name, key) && getValue(name, key).equalsIgnoreCase(Boolean.TRUE.toString()));
  }

  /**
   * Sets the current variable to the specified boolean value. Note: this is different
   * from {@link #enableVariable(java.lang.String, java.lang.String)} and
   * {@link #disableVariable(java.lang.String, java.lang.String)}.
   *
   * @param name specified section name
   * @param key specified key
   * @param enabled whether the variable should be set to TRUE
   */
  public void setEnabled(String name, String key, boolean enabled) {
    if (hasValue(name, key)) {
      String val = new Boolean(enabled).toString();
      set(name, key, val);
    }
  }

  /**
   * Tests whether a value exists for the specified name and key
   *
   * @param name specified section name
   * @param key specified key
   * @return
   *     true if a value exists,
   *     otherwise false
   */
  public boolean hasValue(String name, String key) {
    return !AdakiteUtils.isNullOrEmpty(getValue(name, key));
  }

  /**
   * Returns the value assoicated with the specified INI section and key.
   *
   * @param name specified section name
   * @param key specified key
   * @return
   *     the value assoicated with the specified INI section and key
   */
  public String getValue(String name, String key) {
    if (name == null) {
      name = "";
    }
    if (AdakiteUtils.isNullOrEmpty(key, true)) {
      throw new IllegalArgumentException(Debugging.cannotBeNullOrEmpty("key"));
    }

    if (this.sections.containsKey(name)
        && this.sections.get(name).getKeys().containsKey(key)) {
      return this.sections.get(name).getKeys().get(key);
    } else {
      return null;
    }
  }

  /**
   * Returns the section line index.
   *
   * @param name specified section name
   * @return
   *     the section line index
   */
  private int getSectionIndex(String name) {
    if (AdakiteUtils.isNullOrEmpty(name, true) && this.memoryFile.getLines().size() >= 0) {
      return 0;
    }

    for (int i = 0; i < this.memoryFile.getLines().size(); i++) {
      String line = this.memoryFile.getLines().get(i);
      line = line.trim();
      if (line.startsWith("[" + name + "]")) {
        return i;
      }
    }

    return -1;
  }

  /**
   * Returns the key line index.
   *
   * @param name specified section name
   * @param key specified key
   * @return
   *     the key line index
   */
  private int getKeyIndex(String name, String key) {
    if (name == null) {
      name = "";
    }
    if (AdakiteUtils.isNullOrEmpty(key, true)) {
      throw new IllegalArgumentException(Debugging.cannotBeNullOrEmpty("key"));
    }

    int sectionIndex = getSectionIndex(name);
    if (sectionIndex < 0) {
      return -1;
    }
    for (int i = sectionIndex; i < this.memoryFile.getLines().size(); i++) {
      String line = this.memoryFile.getLines().get(i);
      line = line.trim();
      line = removeComment(line);
      if ((line.startsWith(key + " ") || line.startsWith(key + VARIABLE_DELIMITER))
          && line.contains(VARIABLE_DELIMITER)) {
        return i;
      }
    }

    return -1;
  }

  /**
   * Saves the current INI configuration to the specified file.
   *
   * @param path specified path to the file
   * @throws IOException if an I/O error occurs
   */
  public void saveTo(Path path) throws IOException {
    this.memoryFile.dumpToFile(path);
  }

  /**
   * Returns the specified string excluding a comment if present.
   *
   * @param line specified string to scan
   * @return
   *     the specified string excluding a comment if present
   */
  public static String removeComment(String line) {
    if (AdakiteUtils.isNullOrEmpty(line, true)) {
      return "";
    }

    String ret = line.trim();

    int commentIndex = ret.indexOf(COMMENT_DELIMITER.charAt(0));
    if (commentIndex == 0) {
      return "";
    } else if (commentIndex > 0) {
      ret = ret.substring(0, commentIndex).trim();
    }

    return ret;
  }

  /**
   * Scans the specified string for a comment and returns that string.
   *
   * @param str specified string to scan
   * @return
   *     the comment if present,
   *     otherwise null
   */
  public static String getComment(String str) {
    if (AdakiteUtils.isNullOrEmpty(str, true)) {
      throw new IllegalArgumentException(Debugging.cannotBeNullOrEmpty("line"));
    }

    String ret = str.trim();

    int commentIndex = ret.indexOf(COMMENT_DELIMITER.charAt(0));
    if (commentIndex < 0) {
      return null;
    }
    ret = ret.substring(commentIndex, ret.length()).trim();
    if (ret.length() < 2) {
      /* Comment only contains the comment delimiter. */
      return null;
    }

    return ret;
  }

  public void debug() {
    StringBuilder sb = new StringBuilder(getSections().size() + getSections().keySet().size());
    for (String name : getSections().keySet()) {
      IniSection section = getSections().get(name);
      sb.append("[").append(section.getName()).append("]").append(AdakiteUtils.newline());
      for (String key : section.getKeys().keySet()) {
        sb.append("key" + VARIABLE_DELIMITER).append(section.getKeys().get(key)).append(AdakiteUtils.newline());
      }
    }
    System.out.println(sb.toString());
  }

}
