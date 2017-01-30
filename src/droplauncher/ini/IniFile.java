/*
TODO: Check error scenarios, e.g. if the memory file was not able to be opened,
can the variables still be changed or updated, etc? What happens if the variables
can be changed but the file cannot be modified?
Since the file relies on a MemoryFile object and the MemoryFile object
checks the file before writing, it should be OK? Double check. Determine if there
is a better course of action.
TODO: Use Path objects instead of File objects.
*/

package droplauncher.ini;

import adakite.debugging.Debugging;
import adakite.utils.AdakiteUtils;
import adakite.utils.MemoryFile;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Class for manipulating a Windows INI file while attemping to preserve
 * the file's original variables and comments.
 */
public class IniFile {

  public static final String FILE_EXTENSION = ".ini";
  public static final String VARIABLE_DELIMITER = "=";
  public static final String COMMENT_DELIMITER = ";";

  private MemoryFile memoryFile;
  private HashMap<String, Section> sections;

  public IniFile() {
    this.memoryFile = new MemoryFile();
    this.sections = new HashMap<>();
    clear();
  }

  public HashMap<String, Section> getSections() {
    return this.sections;
  }

  public File getFile() {
    return this.memoryFile.getFile();
  }

  /**
   * Clears relevant class members and, specifically, the HashMap object
   * which contains the Section objects. Always adds a section with no
   * name to hold the place of variables which do not belong to any section.
   */
  private void clear() {
    this.memoryFile.clear();
    this.sections.clear();
    this.sections.put(SectionName.NONE.toString(), new Section());
  }

  public void open(File file) throws IOException {
    clear();

    /* Create a copy of the file into a MemoryFile object. */
    this.memoryFile.open(file);

    /* Parse sections and variables into the class HashMap object. */
    String currSection = SectionName.NONE.toString();
    ArrayList<String> lines = this.memoryFile.getLines();
    for (int i = 0; i < lines.size(); i++) {
      String line = lines.get(i);
      line = line.trim();
      if (line.isEmpty() || line.startsWith(COMMENT_DELIMITER)) {
        continue;
      }
      int commentIndex = line.indexOf(COMMENT_DELIMITER);
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
            continue;
          }
        } else {
          /* New section */
          String name = line.substring(1, line.length() - 1).trim();
          this.sections.put(name, new Section(name));
          currSection = name;
        }
      } else {
        /* New variable */
        int varIndex = line.indexOf(VARIABLE_DELIMITER);
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

  public void setVariable(String name, String key, String val) throws IOException {
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
      this.sections.put(name, new Section(name));
    }

    /* Add the variable to the section. */
    this.sections.get(name).getKeys().put(key, val);

    /* Modify the file. */
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
        int i = sectionIndex + 1;
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

    this.memoryFile.dumpToFile();
  }

  public void enableVariable(String name, String key) throws IOException {
    if (name == null) {
      name = "";
    }
    if (AdakiteUtils.isNullOrEmpty(key, true)) {
      throw new IllegalArgumentException(Debugging.cannotBeNullOrEmpty("key"));
    }

    if (this.sections.containsKey(name)) {
      int sectionIndex = getSectionIndex(name);
      /* Section exists. */
      if (this.sections.get(name).getKeys().containsKey(key)) {
        /* Variable is enabled already. */
        return;
      } else {
        /* Variable needs to be uncommented. */
        for (int i = sectionIndex; i < this.memoryFile.getLines().size(); i++) {
          String line = this.memoryFile.getLines().get(i);
          if (line.contains(COMMENT_DELIMITER)
              && line.contains(key)
              && line.contains(VARIABLE_DELIMITER)
              && line.indexOf(COMMENT_DELIMITER) < line.indexOf(key)
              && line.indexOf(key) < line.indexOf(VARIABLE_DELIMITER)) {
            int commentIndex = line.indexOf(COMMENT_DELIMITER);
            line = line.substring(commentIndex + COMMENT_DELIMITER.length(), line.length()).trim();
            this.memoryFile.getLines().set(i, line);
            this.memoryFile.dumpToFile();
            reload();
            break;
          }
        }
      }
    }
  }

  public void disableVariable(String name, String key) throws IOException {
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
    this.memoryFile.dumpToFile();
    reload();
  }

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
      return "";
    }
  }

  private int getSectionIndex(String name) {
    if (AdakiteUtils.isNullOrEmpty(name, true) && this.memoryFile.getLines().size() > 0) {
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

  public static String removeComment(String line) {
    if (AdakiteUtils.isNullOrEmpty(line, true)) {
      return "";
    }

    String ret = line.trim();

    int commentIndex = ret.indexOf(COMMENT_DELIMITER);
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

    int commentIndex = ret.indexOf(COMMENT_DELIMITER);
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

  private void reload() throws IOException {
    File file = new File(this.memoryFile.getFile().getAbsolutePath());
    open(file);
  }

}
