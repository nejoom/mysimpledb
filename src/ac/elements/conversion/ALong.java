/*
 */

package ac.elements.conversion;

/**
 */
public class ALong {

    static int MAXRADIX = 1112032;

    /**
     * All possible chars for representing a number as a String
     */
    final static char[] digits =
            { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c',
                    'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o',
                    'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z' };

    public static String getISOIEC10646DigitPerLong(int offset) {
        char[] digit = null;

        // i: 0, Character.toChars(0x9),
        // i: 1, Character.toChars(0xA),
        // i: 2, Character.toChars(0xD),
        // i: 3, Character.toChars(0x20),
        // i: 55266, Character.toChars(0xD7FF),
        // i: 55267, Character.toChars(0xE000),
        // i: 63456, Character.toChars(0xFFFD),
        // i: 63457, Character.toChars(0x10000),
        // i: 1112032, Character.toChars(0x10FFFF),

        if (offset == 0) {
            digit = Character.toChars(0x9);
        } else if (offset == 1) {
            digit = Character.toChars(0xA);
        } else if (offset == 2) {
            digit = Character.toChars(0xD);
        } else if (offset >= 3 && offset <= 15) {
            digit = Character.toChars(0x20 + offset - 3);
        } else if (offset >= 16 && offset <= 55265) {
            // skip the minus sign
            digit = Character.toChars(0x20 + offset - 3 + 1);
        } else if (offset >= 55266 && offset <= 63455) {
            digit = Character.toChars(0xE000 + offset - 55266);
        } else if (offset >= 63456 && offset <= 1112031) {
            digit = Character.toChars(0x10000 + offset - 63456);
        }
        // from
        // http://java.sun.com/developer/technicalArticles/Intl/Supplementary/
        return new String(digit);
    }

    private static int mapCodePoint(String s, int i) {
        int character = s.codePointAt(i);
        if (character == 0x9)
            return 0;
        if (character == 0xA)
            return 1;
        if (character == 0xD)
            return 2;
        if (character >= 0x20 && character <= 0x20 + 12)
            return s.codePointAt(i) - 0x20 + 3;
        if (character >= 0x20 + 13 && character <= 0xD7FF)
            return s.codePointAt(i) - 0x20 + 2;
        if (character >= 0xE000 && character <= 0xFFFD)
            return s.codePointAt(i) - 0xE000 + 55266;
        if (character >= 0x10000 && character <= 0x10FFFF)
            return s.codePointAt(i) - 0x10000 + 63456;

        return character;
    }

    /**
     * Returns a string representation of the first argument in the radix
     * specified by the second argument.
     * <p>
     * If the radix is smaller than <code>Character.MIN_RADIX</code> or larger
     * than <code>Character.MAX_RADIX</code>, then the radix <code>10</code> is
     * used instead.
     * <p>
     * If the first argument is negative, the first element of the result is the
     * ASCII minus sign <code>'-'</code> (<code>'&#92;u002d'</code>). If the first argument is not
     * negative, no sign character appears in the result.
     * <p>
     * The remaining characters of the result represent the magnitude of the
     * first argument. If the magnitude is zero, it is represented by a single
     * zero character <code>'0'</code> (<code>'&#92;u0030'</code>); otherwise, the first character
     * of the representation of the magnitude will not be the zero character.
     * The following ASCII characters are used as digits: <blockquote>
     * 
     * <pre>
     *   0123456789abcdefghijklmnopqrstuvwxyz
     * </pre>
     * 
     * </blockquote> These are <code>'&#92;u0030'</code> through <code>'&#92;u0039'</code> and <code>'&#92;u0061'</code> through <code>'&#92;u007a'</code>.
     * If <code>radix</code> is <var>N</var>, then the first <var>N</var> of
     * these characters are used as radix-<var>N</var> digits in the order
     * shown. Thus, the digits for hexadecimal (radix 16) are
     * <code>0123456789abcdef</code>. If uppercase letters are desired, the
     * {@link java.lang.String#toUpperCase()} method may be called on the
     * result: <blockquote>
     * 
     * <pre>
     * Long.toString(n, 16).toUpperCase()
     * </pre>
     * 
     * </blockquote>
     * 
     * @param i
     *            a <code>long</code>to be converted to a string.
     * @param radix
     *            the radix to use in the string representation.
     * @return a string representation of the argument in the specified radix.
     * @see java.lang.Character#MAX_RADIX
     * @see java.lang.Character#MIN_RADIX
     */
    public static String toISOIEC10646String(long i) {

        int radix = MAXRADIX;
        String[] buf = new String[65];
        int charPos = 64;
        boolean negative = (i < 0);

        if (!negative) {
            i = -i;
        }

        while (i <= -radix) {
            buf[charPos--] = getISOIEC10646DigitPerLong((int) (-(i % radix)));// digits[(int)
            // (-(i %
            // radix))];
            i = i / radix;
        }
        buf[charPos] = getISOIEC10646DigitPerLong((int) (-i));// digits[(int)
        // (-i)];

        if (negative) {
            buf[--charPos] = "-";
        }

        StringBuffer returnString = new StringBuffer();
        for (int j = charPos; j < 65; j++) {
            returnString.append(buf[j]);
        }
        return returnString.toString();
    }

    //
    // public static String toISOIEC10646String(int i) {
    //
    // int radix = 1112033;
    // String[] buf = new String[65];
    // int charPos = 64;
    // boolean negative = (i < 0);
    //
    // if (!negative) {
    // i = -i;
    // }
    //
    // while (i <= -radix) {
    // buf[charPos--] = getISOIEC10646DigitPerLong((int) (-(i % radix)));//
    // digits[(int)
    // // (-(i %
    // // radix))];
    // i = i / radix;
    // }
    // buf[charPos] = getISOIEC10646DigitPerLong((int) (-i));// digits[(int)
    // // (-i)];
    //
    // if (negative) {
    // buf[--charPos] = "-";
    // }
    //
    // StringBuffer returnString = new StringBuffer();
    // for (int j = charPos; j < 65; j++) {
    // returnString.append(buf[j]);
    // }
    // return returnString.toString();
    // }

    /**
     * Parses the string argument as a signed integer in the radix specified by
     * the second argument. The characters in the string must all be digits of
     * the specified radix (as determined by whether
     * {@link java.lang.Character#digit(char, int)} returns a nonnegative
     * value), except that the first character may be an ASCII minus sign
     * <code>'-'</code> (<code>'&#92;u002D'</code>) to indicate a negative value. The resulting
     * integer value is returned.
     * <p>
     * An exception of type <code>NumberFormatException</code> is thrown if any
     * of the following situations occurs:
     * <ul>
     * <li>The first argument is <code>null</code> or is a string of length
     * zero.
     * <li>The radix is either smaller than
     * {@link java.lang.Character#MIN_RADIX} or larger than
     * {@link java.lang.Character#MAX_RADIX}.
     * <li>Any character of the string is not a digit of the specified radix,
     * except that the first character may be a minus sign <code>'-'</code> (<code>'&#92;u002D'</code>
     * ) provided that the string is longer than length 1.
     * <li>The value represented by the string is not a value of type
     * <code>int</code>.
     * </ul>
     * <p>
     * Examples: <blockquote>
     * 
     * <pre>
     * parseInt(&quot;0&quot;, 10) returns 0
     * parseInt(&quot;473&quot;, 10) returns 473
     * parseInt(&quot;-0&quot;, 10) returns 0
     * parseInt(&quot;-FF&quot;, 16) returns -255
     * parseInt(&quot;1100110&quot;, 2) returns 102
     * parseInt(&quot;2147483647&quot;, 10) returns 2147483647
     * parseInt(&quot;-2147483648&quot;, 10) returns -2147483648
     * parseInt(&quot;2147483648&quot;, 10) throws a NumberFormatException
     * parseInt(&quot;99&quot;, 8) throws a NumberFormatException
     * parseInt(&quot;Kona&quot;, 10) throws a NumberFormatException
     * parseInt(&quot;Kona&quot;, 27) returns 411787
     * </pre>
     * 
     * </blockquote>
     * 
     * @param s
     *            the <code>String</code> containing the integer representation
     *            to be parsed
     * @param radix
     *            the radix to be used while parsing <code>s</code>.
     * @return the integer represented by the string argument in the specified
     *         radix.
     * @exception NumberFormatException
     *                if the <code>String</code> does not contain a parsable
     *                <code>int</code>.
     */
    public static int parseInt(String s, int radix)
            throws NumberFormatException {
        if (s == null) {
            throw new NumberFormatException("null");
        }
        if (radix < Character.MIN_RADIX) {
            radix = MAXRADIX;
        }

        if (radix > Character.MAX_RADIX) {
            radix = MAXRADIX;
        }

        int result = 0;
        boolean negative = false;
        int i = 0, max = s.codePointCount(0, s.length());
        int limit;
        int multmin;
        int digit;

        if (max > 0) {
            if (s.charAt(0) == '-') {
                negative = true;
                limit = Integer.MIN_VALUE;
                i++;
            } else {
                limit = -Integer.MAX_VALUE;
            }
            multmin = limit / radix;
            // System.out.println(multmin);
            if (i < max) {
                if (radix != MAXRADIX)
                    digit = Character.digit(s.charAt(i++), radix);
                else
                    digit = mapCodePoint(s, i++);
                if (digit < 0) {
                    throw NumberFormatException.forInputString(s);
                } else {
                    result = -digit;
                }
            }
            // System.out.println("i: " + i);
            // System.out.println("max: " + max);
            while (i < max) {
                // Accumulating negatively avoids surprises near MAX_VALUE
                if (radix != MAXRADIX)
                    digit = Character.digit(s.charAt(i++), radix);
                else
                    digit = mapCodePoint(s, i++);
                if (digit < 0) {
                    throw NumberFormatException.forInputString(s);
                }
                if (result < multmin) {
                    throw NumberFormatException.forInputString(s);
                }
                result *= radix;
                if (result < limit + digit) {
                    throw NumberFormatException.forInputString(s);
                }
                result -= digit;
            }
        } else {
            throw NumberFormatException.forInputString(s);
        }
        if (negative) {
            if (i > 1) {
                return result;
            } else { /* Only got "-" */
                throw NumberFormatException.forInputString(s);
            }
        } else {
            return -result;
        }
    }

    /**
     * The main method.
     * 
     * @param args
     *            the arguments
     */
    public static void main(String[] args) {
        for (int i = 0; i < 2000000; i++) {
            String number = toISOIEC10646String(i);
            System.out.println("i: " + i);
            System.out.println("number: " + number);
             System.out.println("hex: "
             + Long.toHexString(number.codePointAt(0)));
            System.out.println("back: " + ALong.parseInt(number, 1000));
        }
    }
}
