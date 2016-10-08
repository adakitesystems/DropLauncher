/* TokenArray.java */

// CHECKSTYLE:OFF

package battlebots.tools;

import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Custom string array with token functionality.
 */
public final class TokenArray
{
    private static final Logger LOGGER = Logger.getLogger(TokenArray.class.getName());

    public static final int DEFAULT_ARRAY_SIZE = 1;

    private String[] _tokens;
    private int _nTokens;

    public TokenArray()
    {
        _tokens = new String[DEFAULT_ARRAY_SIZE];
        reset();
    }

    /**
     * Ensures the array can support one more element. The array is
     * resized if required.
     *
     * @return true if array's capacity is sufficient, otherwise false in the
     * case if the array was not resized successfully
     */
    private boolean ensureCapacity()
    {
        int len = _tokens.length;
        if (_nTokens >= len)
        {
            try
            {
                String[] newArray = new String[_nTokens + 1];
                System.arraycopy(_tokens, 0, newArray, 0, len);
                _tokens = newArray;
            }
            catch (Exception ex)
            {
                if (MainTools.DEBUG)
                {
                    LOGGER.log(Level.SEVERE, "encountered while attempting to resize array", ex);
                }
                return false;
            }
        }
        return true;
    }

    /**
     * Sets the number of elements to zero and does not resize the array.
     * All previous elements are still stored in the array until they are
     * overwritten by new elements. The old elements are inaccessible via
     * public functions due to the number of elements being set to zero and no
     * way of accessing the rest of the array through public functions.
     */
    public void reset()
    {
        _nTokens = 0;
    }

    /**
     * Returns the number of accessible tokens in the array via
     * public functions.
     *
     * @return number of accessible tokens
     */
    public int size()
    {
        return _nTokens;
    }

    /**
     * Returns the element at the specified index.
     *
     * @param index index of element
     * @return array element at index if index is valid, otherwise null
     */
    public String get(int index)
    {
        return (index < 0 || index >= _nTokens) ? null : _tokens[index];
    }

    /**
     * Searches the array for specified element and returns its index.
     *
     * @param str search key
     * @return index of element if found, otherwise -1
     */
    public int getIndexOf(String str)
    {
        if (MainTools.isEmpty(str))
        {
            if (MainTools.DEBUG)
            {
                LOGGER.log(Level.WARNING, "isEmpty(str):true");
            }
            return -1;
        }

        for (int i = 0; i < _nTokens; i++)
        {
            if (str.equals(_tokens[i]))
            {
                return i;
            }
        }

        return -1;
    }

    /**
     * Adds the specified object to the array.
     *
     * @param str string to add
     * @return true if element was added successfully, otherwise false
     */
    public boolean add(String str)
    {
        if (MainTools.isEmpty(str))
        {
            if (MainTools.DEBUG)
            {
                LOGGER.log(Level.WARNING, "isEmpty(str): true");
            }
            return false;
        }

        if (!ensureCapacity())
        {
            return false;
        }

        _tokens[_nTokens++] = str;

        return true;
    }


    /**
     * Removes element at the specified index. If the index is invalid,
     * nothing is affected.
     *
     * @param index index of element to remove
     */
    public void remove(int index)
    {
        if (index < 0 || index >= _nTokens)
        {
            if (MainTools.DEBUG)
            {
                LOGGER.log(Level.WARNING, "(index < 0 || index >= _nTokens): true");
            }
            return;
        }

        if (_nTokens < 2)
        {
            reset();
            return;
        }

        /* Shift elements to the left starting with the first element after
           the removed element. */
        int len = _nTokens - 1;
        while (index < len)
        {
            _tokens[index] = _tokens[index + 1];
            index++;
        }
        _nTokens--;
    }

    /**
     * Removes element that matches the specified string. If the specified
     * string is invalid, nothing is affected.
     *
     * @param str matching string of element to remove
     */
    public void remove(String str)
    {
        remove(getIndexOf(str));
    }

    /**
     * Breaks the string into tokens using delimiters.
     *
     * @param str string to tokenize
     * @param delim delimiters
     * @return number of tokens parsed in string
     */
    public int tokenize(String str, String delim)
    {
        if (MainTools.isEmpty(str))
        {
            if (MainTools.DEBUG)
            {
                LOGGER.log(Level.WARNING, "isEmpty(str): true");
            }
            return 0;
        }
        if (MainTools.isEmpty(delim))
        {
            if (MainTools.DEBUG)
            {
                LOGGER.log(Level.WARNING, "isEmpty(delim): true");
            }
            delim = " ";
        }

        String token;
        StringTokenizer st = new StringTokenizer(str, delim);

        if (delim.equals(" "))
        {
            int len = _tokens.length;
            int maxTokens = getMaxTokens(str);
            if (maxTokens > len)
            {
                _tokens = new String[maxTokens];
            }
        }

        reset();

        while (st.hasMoreTokens())
        {
            token = st.nextToken();
            add(token);
        }

        return _nTokens;
    }

    /**
     * Breaks the string into tokens using a space character
     * as its delimiter.
     *
     * @param str string to tokenize
     * @return number of tokens parsed in string
     */
    public int tokenize(String str)
    {
        return tokenize(str, " ");
    }

    /**
     * Returns the maximum calculated number of possible tokens in a string
     * using the space character as a delimiter.
     *
     * @param str
     * @return
     */
    public static int getMaxTokens(String str)
    {
        if (MainTools.isEmpty(str))
        {
            if (MainTools.DEBUG)
            {
                LOGGER.log(Level.WARNING, "isEmpty(str): true");
            }
            return 0;
        }

        int n = 1;
        int len = str.length();

        for (int i = 0; i < len; i++)
        {
            if (str.charAt(i) == ' ')
            {
                n++;
            }
        }

        return n;
    }

    /**
     * Returns a string with all the tokens separated by the specified
     * delimiter.
     *
     * @param delim delimiter
     * @return string of all tokens
     */
    public String toString(String delim)
    {
        if (_nTokens < 1)
        {
            if (MainTools.DEBUG)
            {
                LOGGER.log(Level.WARNING, "(_nTokens < 1): true");
            }
            return "";
        }
        if (MainTools.isEmpty(delim))
        {
            delim = "";
        }

        StringBuilder result = new StringBuilder(_tokens[0]);
        for (int i = 1; i < _nTokens; i++)
        {
            result.append(delim).append(_tokens[i]);
        }

        return result.toString();
    }

    /**
     * Returns a string with all the tokens separated by a space character.
     *
     * @return string of all tokens
     */
    @Override
    public String toString()
    {
        return toString(" ");
    }
}
