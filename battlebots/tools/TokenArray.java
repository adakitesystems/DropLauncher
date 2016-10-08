/* TokenArray.java */

package battlebots.tools;

import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Custom string array with token functionality.
 *
 * @author Adakite Systems
 * @author adakitesystems@gmail.com
 */
public final class TokenArray {
  private static final Logger LOGGER = Logger.getLogger(TokenArray.class.getName());

  public static final int DEFAULT_ARRAY_SIZE = 1;
  private static final int DEFAULT_ARRAY_INCREMENT = 1;

  private String[] _tokens;
  private int _tokenCount;

  public TokenArray() {
    _tokens = new String[DEFAULT_ARRAY_SIZE];
    reset();
  }

  /**
   * Sets the number of elements to zero and does not resize the array.
   * All previous elements are still stored in the array until they are
   * overwritten by new elements. The old elements are inaccessible via
   * public functions due to the number of elements being set to zero and no
   * way of accessing the rest of the array through public functions.
   */
  public void reset() {
    _tokenCount = 0;
  }

  /**
   * Returns the number of accessible elements in the array via
   * public functions.
   */
  public int size() {
    return _tokenCount;
  }

  /**
   * Ensures the array can hold at least one more element. The array
   * is resized if required.
   *
   * @return
   *     true if the array's capacity is sufficient or was resized sucessfully,
   *     otherwise false
   */
  private boolean ensureCapacity() {
    if (_tokenCount >= _tokens.length) {
      try {
        String[] newArray = new String[_tokenCount + DEFAULT_ARRAY_INCREMENT];
        System.arraycopy(_tokens, 0, newArray, 0, _tokenCount);
        _tokens = newArray;
      } catch (Exception ex) {
        if (MainTools.DEBUG) {
          LOGGER.log(Level.SEVERE, "encountered while attempting to resize array", ex);
        }
        return false;
      }
    }
    return true;
  }

  /**
   * Returns the element at the specified index.
   *
   * @param index index of element
   * @return
   *     the array element at index if index is valid,
   *     otherwise null
   */
  public String get(int index) {
    /* Validate parameters. */
    if (index < 0 || index >= _tokenCount) {
      if (MainTools.DEBUG) {
        LOGGER.log(Level.WARNING, MainTools.INDEX_OOB);
      }
      return null;
    }
    return _tokens[index];
  }

  /**
   * Returns the index of the element matching the specified string.
   *
   * @param str specified string used as search key
   * @return
   *     index of element if found,
   *     otherwise -1
   */
  public int getIndexOf(String str) {
    if (MainTools.isEmpty(str)) {
      if (MainTools.DEBUG) {
        LOGGER.log(Level.WARNING, MainTools.EMPTY_STRING);
      }
      return -1;
    }

    for (int i = 0; i < _tokenCount; i++) {
      if (str.equals(_tokens[i])) {
        return i;
      }
    }

    /* If this line is reached, the specified string was not found. */
    return -1;
  }

  /**
   * Adds the specified object to the array.
   *
   * @param str string to add
   * @return
   *     true if element was added successfully,
   *     otherwise false
   */
  public boolean add(String str) {
    if (MainTools.isEmpty(str)) {
      if (MainTools.DEBUG) {
        LOGGER.log(Level.WARNING, MainTools.EMPTY_STRING);
      }
      return false;
    }

    if (!ensureCapacity()) {
      return false;
    }

    _tokens[_tokenCount++] = str;

    return true;
  }


  /**
   * Removes element at the specified index. If the index is invalid,
   * nothing is affected.
   *
   * @param index index of element to remove
   */
  public void remove(int index) {
    if (index < 0 || index >= _tokenCount) {
      if (MainTools.DEBUG) {
        LOGGER.log(Level.WARNING, MainTools.INDEX_OOB);
      }
      return;
    }

    if (_tokenCount < 2) {
      reset();
      return;
    }

    /* Shift elements to the left starting with the first element after
       the removed element. */
    int len = _tokenCount - 1;
    while (index < len) {
      _tokens[index] = _tokens[index + 1];
      index++;
    }
    _tokenCount--;
  }

  /**
   * Removes element that matches the specified string. If the specified
   * string is invalid, nothing is affected.
   *
   * @param str matching string of element to remove
   */
  public void remove(String str) {
    remove(getIndexOf(str));
  }

  /**
   * Breaks the string into tokens using delimiters.
   *
   * @param str string to tokenize
   * @return number of tokens parsed in string
   */
  public int tokenize(String str) {
    if (MainTools.isEmpty(str)) {
      if (MainTools.DEBUG) {
        LOGGER.log(Level.WARNING, MainTools.EMPTY_STRING + ": String str");
      }
      return 0;
    }

    String token;
    StringTokenizer st = new StringTokenizer(str);

    int maxTokens = getMaxTokens(str);
    if (maxTokens > _tokens.length) {
      _tokens = new String[maxTokens];
    }

    reset();

    while (st.hasMoreTokens()) {
      token = st.nextToken();
      add(token);
    }

    return _tokenCount;
  }

  /**
   * Returns the maximum calculated number of possible tokens in a string
   * using the space character as a delimiter.
   *
   * @param str specified string to examine
   */
  public static int getMaxTokens(String str) {
    if (MainTools.isEmpty(str)) {
      if (MainTools.DEBUG) {
        LOGGER.log(Level.WARNING, MainTools.EMPTY_STRING);
      }
      return 0;
    }

    int counter = 1;
    int len = str.length();

    for (int i = 0; i < len; i++) {
      if (str.charAt(i) == ' ') {
        counter++;
      }
    }

    return counter;
  }

  /**
   * Returns a string with all the tokens separated by the specified
   * delimiter. If the delimiter is null, tokens will not be separated
   * by any string or character.
   *
   * @param delim delimiter
   * @return
   *     the string of all tokens if token count is greater than 0,
   *     otherwise a non-null empty string
   */
  public String toString(String delim) {
    if (_tokenCount < 1) {
      if (MainTools.DEBUG) {
        LOGGER.log(Level.WARNING, "token count less than 1");
      }
      return "";
    }
    if (MainTools.isEmpty(delim)) {
      delim = "";
    }

    StringBuilder result = new StringBuilder(_tokens[0]);
    for (int i = 1; i < _tokenCount; i++) {
      result.append(delim).append(_tokens[i]);
    }

    return result.toString();
  }

  /**
   * Returns a string with all the tokens separated by a space character.
   *
   * @return
   *     string of all tokens
   */
  @Override
  public String toString() {
    return toString(" ");
  }
}
