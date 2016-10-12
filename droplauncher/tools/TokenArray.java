/* TokenArray.java */

package droplauncher.tools;

import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Custom string array with token functionality.
 *
 * @author Adakite Systems
 * @author adakitesystems@gmail.com
 */
public class TokenArray {

  private static final Logger LOGGER = Logger.getLogger(TokenArray.class.getName());
  private static final boolean CLASS_DEBUG = (MainTools.DEBUG && true);

  public static final int DEFAULT_ARRAY_SIZE = 1;
  private static final int DEFAULT_ARRAY_INCREMENT = 1;

  private String[] tokens;
  private int tokenCount;

  public TokenArray() {
    this.tokens = new String[DEFAULT_ARRAY_SIZE];
    reset();
  }

  /**
   * Constructor allowing to set the initial array size. If a less than one
   * value is specified, the default array size will be used.
   *
   * @param initialSize initial size to allocate array
   */
  public TokenArray(int initialSize) {
    if (initialSize < 1) {
      initialSize = DEFAULT_ARRAY_SIZE;
      return;
    }
    this.tokens = new String[initialSize];
    reset();
  }

  /**
   * Ignores all tokens including and after the specified index.
   *
   * @param index specified index
   */
  public void chopAt(int index) {
    if (index < 0 || index >= this.tokenCount) {
      if (CLASS_DEBUG) {
        LOGGER.log(Level.WARNING, MainTools.INDEX_OOB);
      }
      return;
    }
    this.tokenCount = index;
  }

  /**
   * Sets the number of elements to zero and does not resize the array.
   * All previous elements are still stored in the array until they are
   * overwritten by new elements. The old elements are inaccessible via
   * public functions due to the number of elements being set to zero and no
   * way of accessing the rest of the array through public functions.
   */
  public void reset() {
    this.tokenCount = 0;
  }

  /**
   * Returns the number of accessible elements in the array via
   * public functions.
   *
   * @return the number of tokens
   */
  public int size() {
    return this.tokenCount;
  }

  /**
   * Ensures this array can hold at least one more element. The array
   * is resized if required.
   *
   * @return
   *     true if the array's capacity is sufficient or was resized sucessfully,
   *     otherwise false
   */
  private boolean ensureCapacity() {
    if (this.tokenCount >= this.tokens.length) {
      try {
        String[] newArray = new String[this.tokenCount + DEFAULT_ARRAY_INCREMENT];
        System.arraycopy(this.tokens, 0, newArray, 0,this.tokenCount);
        this.tokens = newArray;
      } catch (Exception ex) {
        if (CLASS_DEBUG) {
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
    if (index < 0 || index >= this.tokenCount) {
      if (CLASS_DEBUG) {
        LOGGER.log(Level.WARNING, MainTools.INDEX_OOB);
      }
      return null;
    }
    return this.tokens[index];
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
    /* Validate parameters. */
    if (MainTools.isEmpty(str)) {
      if (CLASS_DEBUG) {
        LOGGER.log(Level.WARNING, MainTools.EMPTY_STRING);
      }
      return -1;
    }

    for (int i = 0; i < this.tokenCount; i++) {
      if (str.equals(this.tokens[i])) {
        return i;
      }
    }

    /* If this line is reached, the specified string was not found. */
    return -1;
  }

  /**
   * Adds the specified string to the array.
   *
   * @param str string to add
   * @param addEmpty whether a non-null empty string can be added
   * @return
   *     true if element was added successfully,
   *     otherwise false
   */
  public boolean add(String str, boolean addEmpty) {
    /* Validate parameters. */
    if (str == null) {
      if (CLASS_DEBUG) {
        LOGGER.log(Level.WARNING, MainTools.NULL_OBJECT);
      }
      return false;
    }

    /* Return false for unmet conditions. */
    if ((str.isEmpty() && !addEmpty)
        || !ensureCapacity()) {
      return false;
    }

    this.tokens[this.tokenCount++] = str;

    return true;
  }

  /**
   * Adds the specified string to the array. An empty string is valid.
   * A null string will not be added.
   *
   * @param str string to add
   * @return
   *     true if element was added successfully,
   *     otherwise false
   */
  public boolean add(String str) {
    return add(str, true);
  }

  /**
   * Adds each string from the specified String array.
   *
   * @param arr specified array
   * @param addEmpty whether a non-null empty string can be added
   * @return
   *     true if the elements were added successfully,
   *     otherwise false
   */
  public boolean add(String[] arr, boolean addEmpty) {
    /* Validate parameters. */
    if (arr == null) {
      if (CLASS_DEBUG) {
        LOGGER.log(Level.WARNING, MainTools.NULL_OBJECT);
      }
      return false;
    }

    for (String str : arr) {
      add(str, addEmpty);
    }
    return true;
  }

  /**
   * Adds each string from the specified String array. An empty string is valid.
   * A null string will not be added.
   *
   * @param arr specified array
   * @return
   *     true if the elements were added successfully,
   *     otherwise false
   */
  public boolean add(String[] arr) {
    return add(arr, true);
  }

  /**
   * Removes element at the specified index. If the index is invalid,
   * nothing is affected.
   *
   * @param index index of element to remove
   */
  public void remove(int index) {
    /* Validate parameters. */
    if (index < 0 || index >= this.tokenCount) {
      if (CLASS_DEBUG) {
        LOGGER.log(Level.WARNING, MainTools.INDEX_OOB);
      }
      return;
    }

    if (this.tokenCount < 2) {
      reset();
      return;
    }

    /* Shift elements to the left starting with the first element after
       the removed element. */
    int len = this.tokenCount - 1;
    while (index < len) {
      this.tokens[index] = this.tokens[index + 1];
      index++;
    }
    this.tokenCount--;
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
   * Overwrites an entire set of tokens at the specified index. If the
   * specified string is null or empty, the token set will be set to
   * a non-null empty string.
   *
   * @param index specified index
   * @param str replacement string
   * @return
   *     true if token set at index was overwritten,
   *     otherwise false
   */
  public boolean set(int index, String str) {
    /* Validate parameters. */
    if (index < 0 || index >= this.tokenCount) {
      if (CLASS_DEBUG) {
        LOGGER.log(Level.WARNING, MainTools.INDEX_OOB);
      }
      return false;
    }

    if (MainTools.isEmpty(str)) {
      this.tokens[index] = "";
      return true;
    }

    this.tokens[index] = str;
    return true;
  }

  /**
   * Breaks the string into tokens using delimiters.
   *
   * @param str string to tokenize
   * @return number of tokens parsed in string
   */
  public int tokenize(String str) {
    /* Validate parameters. */
    if (MainTools.isEmpty(str)) {
      if (CLASS_DEBUG) {
        LOGGER.log(Level.WARNING, MainTools.EMPTY_STRING + ": String str");
      }
      return 0;
    }

    String token;
    StringTokenizer st = new StringTokenizer(str);

    int maxTokens = getMaxTokens(str);
    if (maxTokens > this.tokens.length) {
      this.tokens = new String[maxTokens];
    }

    reset();

    while (st.hasMoreTokens()) {
      token = st.nextToken();
      add(token);
    }

    return this.tokenCount;
  }

  /**
   * Returns the maximum calculated number of possible tokens in a string
   * using the space character as a delimiter.
   *
   * @param str specified string to examine
   * @return the number of max tokens
   */
  public static int getMaxTokens(String str) {
    /* Validate parameters. */
    if (MainTools.isEmpty(str)) {
      if (CLASS_DEBUG) {
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
    /* Validate parameters. */
    if (this.tokenCount < 1) {
      if (CLASS_DEBUG) {
        LOGGER.log(Level.WARNING, "token count less than 1");
      }
      return "";
    }
    if (MainTools.isEmpty(delim)) {
      delim = "";
    }

    StringBuilder result = new StringBuilder(this.tokens[0]);
    for (int i = 1; i < this.tokenCount; i++) {
      result.append(delim).append(this.tokens[i]);
    }

    return result.toString();
  }

  /**
   * Returns a string with all the tokens separated by the space character.
   *
   * @return string of all tokens
   */
  @Override
  public String toString() {
    return toString(" ");
  }

  /**
   * Returns a correctly allocated string array of the internal class array.
   *
   * @return the correctly allocated string array of this token array
   */
  public String[] toStringArray() {
    if (this.tokenCount < 1)  {
      return null;
    }
    String[] newArray = new String[this.tokenCount];
    System.arraycopy(this.tokens, 0, newArray, 0,this.tokenCount);
    return newArray;
  }
}
