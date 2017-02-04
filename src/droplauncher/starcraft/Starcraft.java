package droplauncher.starcraft;

import adakite.util.AdakiteUtils;

/**
 * Utilities and constants class for StarCraft.
 */
public class Starcraft {

  /* Maximum profile name length in Brood War 1.16.1 */
  public static final int MAX_PROFILE_NAME_LENGTH = 24;

  private Starcraft() {}

  /**
   * Returns a filtered string compatible with a StarCraft profile name.
   *
   * @param str specified string
   * @return
   *    a filtered string compatible with a StarCraft profile name
   */
  public static String cleanProfileName(String str) {
    if (AdakiteUtils.isNullOrEmpty(str, true)) {
      return "";
    }

    String ret = "";

    for (int i = 0; i < str.length(); i++) {
      char ch = str.charAt(i);
      if ((ch >= 'A' && ch <= 'Z')
          || (ch >= 'a' && ch <= 'z')
          || (ch >= '0' && ch <= '9')
          || ch == ' ') {
        ret += ch;
      }
    }

    if (ret.length() > MAX_PROFILE_NAME_LENGTH) {
      ret = ret.substring(0, MAX_PROFILE_NAME_LENGTH);
    }

    return ret;
  }

}
