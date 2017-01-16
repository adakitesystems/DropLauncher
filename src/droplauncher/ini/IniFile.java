package droplauncher.ini;

import adakite.debugging.Debugging;
import adakite.utils.AdakiteUtils;
import droplauncher.util.Constants;
import droplauncher.util.MemoryFile;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Logger;

public class IniFile {

  private static final Logger LOGGER = Logger.getLogger(IniFile.class.getName());
  private static final boolean CLASS_DEBUG = (Constants.DEBUG && true);

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

  public boolean open(File file) {
    clear();

    if (file == null) {
      if (CLASS_DEBUG) {
        LOGGER.log(Constants.DEFAULT_LOG_LEVEL, Debugging.nullObject());
      }
      return false;
    }

    /* Create a copy of the file into a MemoryFile object. */
    if (!this.memoryFile.open(file)) {
      if (CLASS_DEBUG) {
        LOGGER.log(Constants.DEFAULT_LOG_LEVEL, Debugging.openFail(file));
      }
      return false;
    }

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
        //DEBUG ---
        System.out.println("Read variable: [" + this.sections.get(currSection).getName() + "]: " + key + " = " + val);
        //---
      }
    }

    return true;
  }

  public void setVariable(String name, String key, String val) {
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
        while (!(this.memoryFile.getLines().get(i).startsWith("[") && this.memoryFile.getLines().get(i).endsWith("]"))
            && i < this.memoryFile.getLines().size()) {
          i++;
        }
        /* We have reached EOF or another section, rewind by one line. */
        i--;
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

  public int getSectionIndex(String name) {
    for (int i = 0; i < this.memoryFile.getLines().size(); i++) {
      String line = this.memoryFile.getLines().get(i);
      line = line.trim();
      if (line.startsWith("[" + name + "]")) {
        return i;
      }
    }
    return -1;
  }

  public int getKeyIndex(String name, String key) {
    int sectionIndex = getSectionIndex(name);
    if (sectionIndex < 0) {
      return -1;
    }
    for (int i = sectionIndex; i < this.memoryFile.getLines().size(); i++) {
      String line = this.memoryFile.getLines().get(i);
      line = line.trim();
      if (line.startsWith(key) && line.contains(VARIABLE_DELIMITER)) {
        return i;
      }
    }
    return -1;
  }

  public static String removeComment(String line) {
    if (AdakiteUtils.isNullOrEmpty(line)) {
      return "";
    }

    String ret = line.trim();

    int commentIndex = ret.indexOf(COMMENT_DELIMITER);
    if (commentIndex < 1) {
      /* Return an empty string if a comment is not found or starts at the
         beginning of the line.*/
      return "";
    }
    ret = ret.substring(0, commentIndex).trim();

    return ret;
  }

  public static String getComment(String line) {
    if (AdakiteUtils.isNullOrEmpty(line)) {
      return null;
    }

    String ret = line.trim();

    int commentIndex = ret.indexOf(COMMENT_DELIMITER);
    if (commentIndex < 0) {
      return null;
    }
    ret = ret.substring(commentIndex, ret.length()).trim();

    return ret;
  }

//  public boolean enableVariable(String key) {
//    if (this.settings.isVariableSet(key)) {
//      return true;
//    }
//    for (int i = 0; i < this.memoryFile.getLines().size(); i++) {
//      String line = this.memoryFile.getLines().get(i);
//      if (line.contains(COMMENT_DELIMITER)
//          && line.contains(key)
//          && line.contains(VARIABLE_DELIMITER)
//          && line.indexOf(COMMENT_DELIMITER) < line.indexOf(key)
//          && line.indexOf(key) < line.indexOf(VARIABLE_DELIMITER)) {
//        int commentIndex = line.indexOf(COMMENT_DELIMITER);
//        line = line.substring(commentIndex + COMMENT_DELIMITER.length(), line.length()).trim();
//        this.memoryFile.getLines().set(i, line);
//        this.memoryFile.dumpToFile();
//        refresh();
//        return true;
//      }
//    }
//    return false;
//  }
//
//  public void disableVariable(String key) {
//    if (!this.settings.isVariableSet(key)) {
//      /* Return if the variable is not set/found. */
//      return;
//    }
//    int lineIndex = getLineIndexByKey(key);
//    if (lineIndex < 0) {
//      if (CLASS_DEBUG) {
//        /* The lineIndex should always be greater than 0 if
//           "isVariableSet" is functioning properly. */
//        LOGGER.log(Constants.DEFAULT_LOG_LEVEL, "should not see this logic error");
//      }
//      return;
//    }
//    String line = this.memoryFile.getLines().get(lineIndex);
//    line = COMMENT_DELIMITER + line;
//    this.memoryFile.getLines().set(lineIndex, line);
//    this.memoryFile.dumpToFile();
//    this.settings.getVariables().remove(key);
//    refresh();
//  }

}
