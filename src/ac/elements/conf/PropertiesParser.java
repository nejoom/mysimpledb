/**
 *
 * Copyright 2008-2009 Elements. All Rights Reserved.
 *
 * License version: CPAL 1.0
 *
 * The Original Code is mysimpledb.com code. Please visit mysimpledb.com to see how
 * you can contribute and improve this software.
 *
 * The contents of this file are licensed under the Common Public Attribution
 * License Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 *
 *    http://mysimpledb.com/license.
 *
 * The License is based on the Mozilla Public License Version 1.1.
 *
 * Sections 14 and 15 have been added to cover use of software over a computer
 * network and provide for attribution determined by Elements.
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 * the specific language governing permissions and limitations under the
 * License.
 *
 * Elements is the Initial Developer and the Original Developer of the Original
 * Code.
 *
 * Based on commercial needs the contents of this file may be used under the
 * terms of the Elements End-User License Agreement (the Elements License), in
 * which case the provisions of the Elements License are applicable instead of
 * those above.
 *
 * You may wish to allow use of your version of this file under the terms of
 * the Elements License please visit http://mysimpledb.com/license for details.
 *
 */
package ac.elements.conf;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * The <code>PropertiesParser</code> class represents a persistent set of
 * properties. The <code>PropertiesParser</code> can be saved to a stream or
 * loaded from a stream. Each key and its corresponding value in the property
 * list is a string.
 * <p>
 * A property list can contain another property list as its "defaults"; this
 * second property list is searched if the property key is not found in the
 * original property list.
 * <p>
 * Because <code>Properties</code> inherits from <code>HashMap</code>, the
 * <code>put</code> and <code>putAll</code> methods can be applied to a
 * <code>PropertiesParser</code> object. Their use is strongly discouraged as
 * they allow the caller to insert entries whose keys or values are not
 * <code>Strings</code>. The <code>setProperty</code> method should be used
 * instead. If the <code>store</code> or <code>save</code> method is called on a
 * "compromised" <code>Properties</code> object that contains a non-
 * <code>String</code> key or value, the call will fail.
 * <p>
 * <a name="encoding"></a> When saving properties to a stream or loading them
 * from a stream, the ISO 8859-1 character encoding is used. For characters that
 * cannot be directly represented in this encoding, <a
 * href="http://java.sun.com/docs/books/jls/html/3.doc.html#100850">Unicode
 * escapes</a> are used; however, only a single 'u' character is allowed in an
 * escape sequence. The native2ascii tool can be used to convert property files
 * to and from other character encodings.
 * 
 * 
 * @author Eddie Moojen
 */
public class PropertiesParser {

    /** The Constant log. */
    private final static Log logger = LogFactory.getLog(PropertiesParser.class);

    // private static final String SUFFIX = ".properties";

    private Map<String, Value> properties = new LinkedHashMap<String, Value>();

    private Date lastModifiedFile = null;

    private String header = null;

    private String fileName = "Unkown file";

    private static final String newLine = System.getProperty("line.separator");

    private static final String protocol = "http://";

    private static final String lastModified = "last modified: ";

    private static final String timestamp = "timestamp: ";

    private static final String encoding = "encoding: ";

    private static final String keyValueSeparators = "=: \t\r\n\f";

    private static final String strictKeyValueSeparators = "=:";

    private static final String specialSaveChars = "=: \t\r\n\f#!";

    private static final String whiteSpaceChars = " \t\r\n\f";

    private static String serverEncoding = "UTF8";// 8859_1

    // A table of hex digits
    private static final char[] hexDigit =
            { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C',
                    'D', 'E', 'F' };

    // number of spaces to pad between key and "=" seperator
    private static int padding = 25;

    private static int SORT = 0;

    /**
     * Internal class to represent a value object with an internal comment.
     * 
     * @param value
     *            The internal value
     */
    private static class Value {
        private String comment;

        private Object value;

        Value(Object value, String _comment) {
            this.value = value;
            comment = _comment;
        }

        Object getValue() {
            return value;
        }

        String getComment() {
            return this.comment;
        }

        public String toString() {
            String _comment = comment;
            if (_comment == null)
                _comment = "";
            if (value == null)
                return "null";
            else
                return (_comment.equals("")) ? value.toString() : (_comment
                        + newLine + value.toString());
        }
    }

    /**
     * A PropertiesParser that contains default values for any keys not found in
     * this property list.
     * 
     * @serial
     */
    protected PropertiesParser defaults;

    /**
     * Creates an empty property list with no default values.
     */
    public PropertiesParser() {
        this(null);
    }

    /**
     * Creates an empty PropertiesParser with no default values.
     */
    public PropertiesParser(int form) {
        this(null);
        if (form == SORT)
            properties = new TreeMap<String, Value>();
    }

    /**
     * Creates an empty PropertiesParser with the specified defaults.
     * 
     * @param defaults
     *            the defaults.
     */
    public PropertiesParser(PropertiesParser defaults) {
        this.defaults = defaults;
    }

    /**
     * Calls the <tt>HashMap</tt> method <code>put</code>. Provided for
     * parallelism with the <tt>getProperty</tt> method. Enforces use of strings
     * for property keys and values. The value returned is the result of the
     * <tt>HashMap</tt> call to <code>put</code>.
     * 
     * @param key
     *            the key to be placed into this property list.
     * @param value
     *            the value corresponding to <tt>key</tt>.
     * @return the previous value of the specified key in this property list, or
     *         <code>null</code> if it did not have one.
     * @see #getProperty
     * @since 1.2
     */
    public synchronized Object setProperty(String key, String value) {
        Value myValue = properties.get(key);
        if (myValue == null) {
            myValue = new Value(value, null);
        } else {
            myValue = new Value(value, myValue.getComment());
        }
        return properties.put(key, myValue);
    }

    /**
     * Returns the path and filename of the persistent properties file.
     * 
     * @return the path and filename of the persistent properties file.
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * Calls the <tt>HashMap</tt> method <code>put</code>. Provided for easy
     * method for adding comments to a properties file. Enforces use of strings
     * for property keys and comments. The value returned is the result of the
     * <tt>HashMap</tt> call to <code>put</code>.
     * 
     * @param key
     *            the key to be placed into this property list.
     * @param comment
     *            the comment in the properties file corresponding to
     *            <tt>key</tt>.
     * @return the previous value of the specified key in this property list, or
     *         <code>null</code> if it did not have one.
     */
    public synchronized Object setComment(String key, String comment) {
        Value myValue = properties.get(key);
        if (myValue == null) {
            myValue = new Value(null, comment);
        } else {
            myValue = new Value(myValue.getValue(), comment);
        }
        return properties.put(key, myValue);
    }

    /**
     * Reads a property list (key and element pairs) from the input stream. The
     * stream is assumed to be using the ISO 8859-1 character encoding; that is
     * each byte is one Latin1 character. Characters not in Latin1, and certain
     * special characters, can be represented in keys and elements using escape
     * sequences similar to those used for character and string literals (see <a
     * href=
     * "http://java.sun.com/docs/books/jls/second_edition/html/lexical.doc.html#100850"
     * >&sect;3.3</a> and <a href=
     * "http://java.sun.com/docs/books/jls/second_edition/html/lexical.doc.html#101089"
     * >&sect;3.10.6</a> of the <i>Java Language Specification</i>).
     * 
     * The differences from the character escape sequences used for characters
     * and strings are:
     * 
     * <ul>
     * <li>Octal escapes are not recognized.
     * 
     * <li>The character sequence <code>\b</code> does <i>not</i> represent a
     * backspace character.
     * 
     * <li>The method does not treat a backslash character, <code>\</code>,
     * before a non-valid escape character as an error; the backslash is
     * silently dropped. For example, in a Java string the sequence <code>"\z"</code> would
     * cause a compile time error. In contrast, this method silently drops the
     * backslash. Therefore, this method treats the two character sequence
     * <code>"\b"</code> as equivalent to the single character <code>'b'</code>.
     * 
     * <li>Escapes are not necessary for single and double quotes; however, by
     * the rule above, single and double quote characters preceded by a
     * backslash still yield single and double quote characters, respectively.
     * 
     * </ul>
     * 
     * An <code>IllegalArgumentException</code> is thrown if a malformed Unicode
     * escape appears in the input.
     * 
     * <p>
     * This method processes input in terms of lines. A natural line of input is
     * terminated either by a set of line terminator characters (<code>\n</code>
     * or <code>\r</code> or <code>\r\n</code>) or by the end of the file. A
     * natural line may be either a blank line, a comment line, or hold some
     * part of a key-element pair. The logical line holding all the data for a
     * key-element pair may be spread out across several adjacent natural lines
     * by escaping the line terminator sequence with a backslash character,
     * <code>\</code>. Note that a comment line cannot be extended in this
     * manner; every natural line that is a comment must have its own comment
     * indicator, as described below. If a logical line is continued over
     * several natural lines, the continuation lines receive further processing,
     * also described below. Lines are read from the input stream until end of
     * file is reached.
     * 
     * <p>
     * A natural line that contains only white space characters is considered
     * blank and is ignored. A comment line has an ASCII <code>'#'</code> or
     * <code>'!'</code> as its first non-white space character; comment lines
     * are also ignored and do not encode key-element information. In addition
     * to line terminators, this method considers the characters space (
     * <code>' '</code>, <code>'&#92;u0020'</code>), tab (<code>'\t'</code>, <code>'&#92;u0009'</code>), and form feed
     * (<code>'\f'</code>, <code>'&#92;u000C'</code>) to be white space.
     * 
     * <p>
     * If a logical line is spread across several natural lines, the backslash
     * escaping the line terminator sequence, the line terminator sequence, and
     * any white space at the start the following line have no affect on the key
     * or element values. The remainder of the discussion of key and element
     * parsing will assume all the characters constituting the key and element
     * appear on a single natural line after line continuation characters have
     * been removed. Note that it is <i>not</i> sufficient to only examine the
     * character preceding a line terminator sequence to to see if the line
     * terminator is escaped; there must be an odd number of contiguous
     * backslashes for the line terminator to be escaped. Since the input is
     * processed from left to right, a non-zero even number of 2<i>n</i>
     * contiguous backslashes before a line terminator (or elsewhere) encodes
     * <i>n</i> backslashes after escape processing.
     * 
     * <p>
     * The key contains all of the characters in the line starting with the
     * first non-white space character and up to, but not including, the first
     * unescaped <code>'='</code>, <code>':'</code>, or white space character
     * other than a line terminator. All of these key termination characters may
     * be included in the key by escaping them with a preceding backslash
     * character; for example,
     * <p>
     * 
     * <code>\:\=</code>
     * <p>
     * 
     * would be the two-character key <code>":="</code>. Line terminator
     * characters can be included using <code>\r</code> and <code>\n</code>
     * escape sequences. Any white space after the key is skipped; if the first
     * non-white space character after the key is <code>'='</code> or
     * <code>':'</code>, then it is ignored and any white space characters after
     * it are also skipped. All remaining characters on the line become part of
     * the associated element string; if there are no remaining characters, the
     * element is the empty string <code>&quot;&quot;</code>. Once the raw
     * character sequences constituting the key and element are identified,
     * escape processing is performed as described above.
     * 
     * <p>
     * As an example, each of the following three lines specifies the key
     * <code>"Truth"</code> and the associated element value
     * <code>"Beauty"</code>:
     * <p>
     * 
     * <pre>
     *    Truth = Beauty
     *      Truth:Beauty
     *    Truth         :Beauty
     * </pre>
     * 
     * As another example, the following three lines specify a single property:
     * <p>
     * 
     * <pre>
     *    fruits                           apple, banana, pear, \
     *                                     cantaloupe, watermelon, \
     *                                     kiwi, mango
     * </pre>
     * 
     * The key is <code>"fruits"</code> and the associated element is:
     * <p>
     * 
     * <pre>
     * &quot;apple, banana, pear, cantaloupe, watermelon, kiwi, mango&quot;
     * </pre>
     * 
     * Note that a space appears before each <code>\</code> so that a space will
     * appear after each comma in the final result; the <code>\</code>, line
     * terminator, and leading white space on the continuation line are merely
     * discarded and are <i>not</i> replaced by one or more other characters.
     * <p>
     * As a third example, the line:
     * <p>
     * 
     * <pre>
     * cheeses
     * </pre>
     * 
     * specifies that the key is <code>"cheeses"</code> and the associated
     * element is the empty string <code>""</code>.
     * <p>
     * 
     * @param inStream
     *            the input stream.
     * @exception IOException
     *                if an error occurred when reading from the input stream.
     * @throws IllegalArgumentException
     *             if the input stream contains a malformed Unicode escape
     *             sequence.
     */
    public synchronized void load(InputStream inStream) throws IOException {

        BufferedReader in =
                new BufferedReader(new InputStreamReader(inStream, "8859_1"));

        String comment = null;

        while (true) {

            // Get next line
            String line = in.readLine();

            if (line == null)
                return;

            if (line.length() > 0) {

                // Find start of key
                int len = line.length();
                int keyStart;

                // charater index of first non white space
                for (keyStart = 0; keyStart < len; keyStart++)
                    if (whiteSpaceChars.indexOf(line.charAt(keyStart)) == -1)
                        break;

                // Blank lines are ignored
                if (keyStart == len)
                    continue;

                // Continue lines that end in slashes if they are not comments
                char firstChar = line.charAt(keyStart);

                if ((firstChar != '#') && (firstChar != '!')) {
                    while (continueLine(line)) {
                        String nextLine = in.readLine();
                        if (nextLine == null)
                            nextLine = "";
                        String loppedLine = line.substring(0, len - 1);

                        // Advance beyond whitespace on new line
                        int startIndex;
                        for (startIndex = 0; startIndex < nextLine.length(); startIndex++)
                            if (whiteSpaceChars.indexOf(nextLine
                                    .charAt(startIndex)) == -1)
                                break;

                        nextLine =
                                nextLine.substring(startIndex, nextLine
                                        .length());

                        line = new String(loppedLine + nextLine);

                        len = line.length();
                    }// while continue line

                    // Find separation between key and value
                    int separatorIndex;
                    for (separatorIndex = keyStart; separatorIndex < len; separatorIndex++) {
                        char currentChar = line.charAt(separatorIndex);
                        if (currentChar == '\\')
                            separatorIndex++;
                        else if (keyValueSeparators.indexOf(currentChar) != -1)
                            break;
                    }

                    // Skip over whitespace after key if any
                    int valueIndex;
                    for (valueIndex = separatorIndex; valueIndex < len; valueIndex++)
                        if (whiteSpaceChars.indexOf(line.charAt(valueIndex)) == -1)
                            break;

                    // Skip over one non whitespace key value separators if any
                    if (valueIndex < len)
                        if (strictKeyValueSeparators.indexOf(line
                                .charAt(valueIndex)) != -1)
                            valueIndex++;

                    // Skip over white space after other separators if any
                    while (valueIndex < len) {
                        if (whiteSpaceChars.indexOf(line.charAt(valueIndex)) == -1)
                            break;
                        valueIndex++;
                    }
                    String key = line.substring(keyStart, separatorIndex);
                    String value =
                            (separatorIndex < len) ? line.substring(valueIndex,
                                    len) : "";

                    // Convert then store key and value
                    key = loadConvert(key);

                    value = loadConvert(value);

                    Value internalValue = new Value(value, comment);

                    properties.put(key, internalValue);

                    comment = null;
                }// end if comment
                else {
                    // avoid double commenting timestamp
                    if (line.indexOf(timestamp) == -1
                            && line.indexOf(encoding) == -1) {
                        if (comment != null)
                            comment += newLine;
                        if (comment == null)
                            comment = line.trim();
                        else
                            comment += line.trim();
                    } else if (line.indexOf(timestamp) != -1) {
                        // parse date. Format is standard date toString format
                        // eg: last modified: Sun Aug 07 11:58:42 CEST 2005
                        SimpleDateFormat formatter =
                                new SimpleDateFormat(
                                        "E MMM dd HH:mm:ss ZZZ yyyy");
                        try {
                            lastModifiedFile =
                                    (Date) formatter.parse(line,
                                            new ParsePosition(line
                                                    .indexOf(timestamp)
                                                    + timestamp.length()));
                        } catch (Exception e) {
                            logger.error(e);
                        }
                    } else if (line.indexOf(encoding) != -1) {
                        // parse date. Format is standard date toString format
                        // eg: last modified: Sun Aug 07 11:58:42 CEST 2005
                        serverEncoding =
                                line.substring(
                                        line.indexOf(encoding)
                                                + encoding.length()).trim();
                        System.out.println(serverEncoding);
                    }
                }
            }// line.lenght() > 0
        }
    }

    /*
     * Returns true if the given line is a line that must be appended to the
     * next line
     */
    private boolean continueLine(String line) {
        int slashCount = 0;
        int index = line.length() - 1;
        while ((index >= 0) && (line.charAt(index--) == '\\'))
            slashCount++;
        return (slashCount % 2 == 1);
    }

    /*
     * Converts encoded &#92;uxxxx to unicode chars and changes special saved
     * chars to their original forms
     */
    private String loadConvert(String theString) {
        char aChar;
        int len = theString.length();
        StringBuffer outBuffer = new StringBuffer(len);

        for (int x = 0; x < len;) {
            aChar = theString.charAt(x++);
            if (aChar == '\\') {
                aChar = theString.charAt(x++);
                if (aChar == 'u') {
                    // Read the xxxx
                    int value = 0;
                    for (int i = 0; i < 4; i++) {
                        aChar = theString.charAt(x++);
                        switch (aChar) {
                        case '0':
                        case '1':
                        case '2':
                        case '3':
                        case '4':
                        case '5':
                        case '6':
                        case '7':
                        case '8':
                        case '9':
                            value = (value << 4) + aChar - '0';
                            break;
                        case 'a':
                        case 'b':
                        case 'c':
                        case 'd':
                        case 'e':
                        case 'f':
                            value = (value << 4) + 10 + aChar - 'a';
                            break;
                        case 'A':
                        case 'B':
                        case 'C':
                        case 'D':
                        case 'E':
                        case 'F':
                            value = (value << 4) + 10 + aChar - 'A';
                            break;
                        default:
                            throw new IllegalArgumentException(
                                    "Malformed \\uxxxx encoding.");
                        }
                    }
                    outBuffer.append((char) value);
                } else {
                    if (aChar == 't')
                        aChar = '\t';
                    else if (aChar == 'r')
                        aChar = '\r';
                    else if (aChar == 'n')
                        aChar = '\n';
                    else if (aChar == 'f')
                        aChar = '\f';
                    outBuffer.append(aChar);
                }
            } else
                outBuffer.append(aChar);
        }
        return outBuffer.toString();
    }

    /*
     * Converts unicodes to encoded &#92;uxxxx and writes out any of the
     * characters in specialSaveChars with a preceding slash
     */
    private String saveConvert(String theString, boolean escapeSpace) {
        int len = theString.length();
        StringBuffer outBuffer = new StringBuffer(len * 2);

        for (int x = 0; x < len; x++) {
            char aChar = theString.charAt(x);
            switch (aChar) {
            case ' ':
                if (x == 0 || escapeSpace)
                    outBuffer.append('\\');

                outBuffer.append(' ');
                break;
            case '\\':
                outBuffer.append('\\');
                outBuffer.append('\\');
                break;
            case '\t':
                outBuffer.append('\\');
                outBuffer.append('t');
                break;
            case '\n':
                outBuffer.append('\\');
                outBuffer.append('n');
                break;
            case '\r':
                outBuffer.append('\\');
                outBuffer.append('r');
                break;
            case '\f':
                outBuffer.append('\\');
                outBuffer.append('f');
                break;
            default:
                if ((aChar < 0x0020) || (aChar > 0x007e)) {
                    outBuffer.append('\\');
                    outBuffer.append('u');
                    outBuffer.append(toHex((aChar >> 12) & 0xF));
                    outBuffer.append(toHex((aChar >> 8) & 0xF));
                    outBuffer.append(toHex((aChar >> 4) & 0xF));
                    outBuffer.append(toHex(aChar & 0xF));
                } else {
                    if (specialSaveChars.indexOf(aChar) != -1)
                        outBuffer.append('\\');
                    outBuffer.append(aChar);
                }
            }
        }
        return outBuffer.toString();
    }

    /**
     * Calls the <code>store(OutputStream out, String header)</code> method and
     * suppresses IOExceptions that were thrown.
     * 
     * @deprecated This method does not throw an IOException if an I/O error
     *             occurs while saving the property list. As of the Java 2
     *             platform v1.2, the preferred way to save a properties list is
     *             via the <code>store(OutputStream out,
     * String header)</code> method.
     * 
     * @param out
     *            an output stream.
     * @param header
     *            a description of the property list.
     * @exception ClassCastException
     *                if this <code>Properties</code> object contains any keys
     *                or values that are not <code>Strings</code>.
     */
    public synchronized void save(OutputStream out, String header) {
        try {
            store(out, header);
        } catch (IOException e) {
        }
    }

    /**
     * Writes this property list (key and element pairs) in this
     * <code>Properties</code> table to the output stream in a format suitable
     * for loading into a <code>Properties</code> table using the
     * {@link #load(InputStream) load} method. The stream is written using the
     * ISO 8859-1 character encoding.
     * <p>
     * Properties from the defaults table of this <code>Properties</code> table
     * (if any) are <i>not</i> written out by this method.
     * <p>
     * If the header argument is not null, then an ASCII <code>#</code>
     * character, the header string, and a line separator are first written to
     * the output stream. Thus, the <code>header</code> can serve as an
     * identifying comment.
     * <p>
     * Next, a comment line is always written, consisting of an ASCII
     * <code>#</code> character, the current date and time (as if produced by
     * the <code>toString</code> method of <code>Date</code> for the current
     * time), and a line separator as generated by the Writer.
     * <p>
     * Then every entry in this <code>Properties</code> table is written out,
     * one per line. For each entry the key string is written, then an ASCII
     * <code>=</code>, then the associated element string. Each character of the
     * key and element strings is examined to see whether it should be rendered
     * as an escape sequence. The ASCII characters <code>\</code>, tab, form
     * feed, newline, and carriage return are written as <code>\\</code>,
     * <code>\t</code>, <code>\f</code> <code>\n</code>, and <code>\r</code>,
     * respectively. Characters less than <code>&#92;u0020</code> and characters
     * greater than <code>&#92;u007E</code> are written as <code>&#92;u</code>
     * <i>xxxx</i> for the appropriate hexadecimal value <i>xxxx</i>. For the
     * key, all space characters are written with a preceding <code>\</code>
     * character. For the element, leading space characters, but not embedded or
     * trailing space characters, are written with a preceding <code>\</code>
     * character. The key and element characters <code>#</code>, <code>!</code>,
     * <code>=</code>, and <code>:</code> are written with a preceding backslash
     * to ensure that they are properly loaded.
     * <p>
     * After the entries have been written, the output stream is flushed. The
     * output stream remains open after this method returns.
     * 
     * @param out
     *            an output stream.
     * @param header
     *            a description of the property list.
     * @exception IOException
     *                if writing this property list to the specified output
     *                stream throws an <tt>IOException</tt>.
     * @exception ClassCastException
     *                if this <code>Properties</code> object contains any keys
     *                or values that are not <code>Strings</code>.
     * @exception NullPointerException
     *                if <code>out</code> is null.
     * @since 1.2
     */
    public synchronized void store(OutputStream out, String _header)
            throws IOException {
        BufferedWriter awriter;
        awriter = new BufferedWriter(new OutputStreamWriter(out, "8859_1"));
        if (_header != null)
            writeln(awriter, "# " + _header);
        else if (header != null)
            writeln(awriter, "# " + header);
        writeln(awriter, "# " + timestamp + new Date().toString());
        writeln(awriter, "# " + encoding + serverEncoding);

        for (Iterator<String> e = properties.keySet().iterator(); e.hasNext();) {
            String key = e.next();

            Object oval = properties.get(key);
            String comment = null;
            if (oval != null) {
                oval = ((Value) oval).getValue();
                comment = properties.get(key).getComment();
            }

            String val = (String) oval;

            key = saveConvert(key, true);

            /*
             * No need to escape embedded and trailing spaces for value, hence
             * pass false to flag.
             */
            val = saveConvert(val, false);

            // use 20 spaces to pad
            int spaces =
                    (padding - key.length()) < 0 ? 0 : (padding - key.length());
            String tabString = "";
            for (int i = 0; i < spaces; i++)
                tabString += " ";

            if (comment != null) {
                writeln(awriter, "");
                writeln(awriter, comment);
            }
            writeln(awriter, key + tabString + "= " + val);

        }
        awriter.flush();
    }

    private static void writeln(BufferedWriter bw, String s) throws IOException {
        bw.write(s);
        bw.newLine();
    }

    /**
     * Searches for the property with the specified key in this property list.
     * If the key is not found in this property list, the default property list,
     * and its defaults, recursively, are then checked. The method returns
     * <code>null</code> if the property is not found.
     * 
     * @param key
     *            the property key.
     * @return the value in this property list with the specified key value.
     * @see #setProperty
     * @see #defaults
     */
    public String getProperty(String key) {
        Object oval = properties.get(key);

        if (oval != null) {
            oval = ((Value) oval).getValue();
        }

        String sval = (oval instanceof String) ? (String) oval : null;
        return ((sval == null) && (defaults != null)) ? defaults
                .getProperty(key) : sval;
    }

    /**
     * Searches for the comment with the specified key in this property list. If
     * the key is not found in this property list, the default comment is
     * returned are then checked. The method returns <code>null</code> if the
     * comment is not found.
     * 
     * @param key
     *            the property key.
     * @param boolean withLink suppresses urls in comment and "url:" token.
     * @return the value in this property list with the specified key value.
     * @see #setProperty
     * @see #defaults
     */
    public String getComment(String key, boolean withLinks) {
        Object oval = properties.get(key);

        if (oval != null) {
            oval = ((Value) oval).getComment();
        }

        String sval = (oval instanceof String) ? (String) oval : null;

        // supress links
        if (!withLinks && sval != null) {
            String lines[] = sval.split(newLine);

            String newComment = "";
            for (int i = 0; i < lines.length; i++) {
                // ignore empty things
                if (lines[i].indexOf(protocol) != -1)
                    continue;
                if (lines[i].indexOf("url:") != -1)
                    continue;
                newComment = newComment.concat(lines[i]).concat(newLine);
            }
            sval = newComment.trim();
        }

        return ((sval == null) && (defaults != null)) ? defaults
                .getComment(key) : sval;
    }

    /**
     * Searches for the property with the specified key in this property list.
     * If the key is not found in this property list, the default property list,
     * and its defaults, recursively, are then checked. The method returns
     * <code>null</code> if the property is not found. Links in comments are
     * automatically supressed.
     * 
     * @param key
     *            the property key.
     * @return the value in this property list with the specified key value.
     * @see #setProperty
     * @see #defaults
     */
    public String getComment(String key) {
        return getComment(key, false);
    }

    public void sort() {
        TreeMap<String, Value> myMap = new TreeMap<String, Value>(properties);
        properties = (Map<String, Value>) myMap;
    }

    /**
     * Searches for the property with the specified key in this property list.
     * If the key is not found in this property list, the default property list,
     * and its defaults, recursively, are then checked. The method returns the
     * default value argument if the property is not found.
     * 
     * @param key
     *            the hashtable key.
     * @param defaultValue
     *            a default value.
     * 
     * @return the value in this property list with the specified key value.
     * @see #setProperty
     * @see #defaults
     */
    public String getProperty(String key, String defaultValue) {
        String val = getProperty(key);
        return (val == null) ? defaultValue : val;
    }

    /**
     * The method returns the underlying map of this properties parser.
     * 
     * @return the underlying map.
     */
    public Map<String, Value> getPropertiesMap() {
        return properties;
    }

    /**
     * Returns an enumeration of all the keys in this property list, including
     * distinct keys in the default property list if a key of the same name has
     * not already been found from the main properties list.
     * 
     * @return an enumeration of all the keys in this property list, including
     *         the keys in the default property list.
     * @see java.util.Enumeration
     * @see java.util.Properties#defaults
     */
    public Iterator<String> propertyNames() {
        Map<String, Value> h = (Map<String, Value>) properties;
        enumerate(h);
        return h.keySet().iterator();
    }

    /**
     * Prints this property list out to the specified output stream. This method
     * is useful for debugging.
     * 
     * @param out
     *            an output stream.
     */
    public void list(PrintStream out) {
        out.println("-- listing properties --");
        HashMap<String, Value> h = new HashMap<String, Value>();
        enumerate(h);
        for (Iterator<String> e = h.keySet().iterator(); e.hasNext();) {
            String key = e.next();

            Object oval = h.get(key);

            if (oval != null) {
                oval = ((Value) oval).getValue();
            }

            String val = (String) oval;
            if (val.length() > 40) {
                val = val.substring(0, 37) + "...";
            }
            out.println(key + "=" + val);
        }
    }

    /**
     * Prints this property list out to the specified output stream. This method
     * is useful for debugging.
     * 
     * @param out
     *            an output stream.
     * @since JDK1.1
     */
    /*
     * Rather than use an anonymous inner class to share common code, this
     * method is duplicated in order to ensure that a non-1.1 compiler can
     * compile this file.
     */
    public void list(PrintWriter out) {
        out.println("-- listing properties --");
        HashMap<String, Value> h = new HashMap<String, Value>();
        enumerate(h);
        for (Iterator<String> e = h.keySet().iterator(); e.hasNext();) {
            String key = e.next();

            Object oval = h.get(key);

            if (oval != null) {
                oval = ((Value) oval).getValue();
            }

            String val = (String) oval;
            if (val.length() > 40) {
                val = val.substring(0, 37) + "...";
            }
            out.println(key + "=" + val);
        }
    }

    /**
     * Enumerates all key/value pairs in the specified hastable.
     * 
     * @param h
     *            the hashtable
     */
    private synchronized void enumerate(Map<String, Value> h) {

        if (defaults != null) {
            defaults.enumerate(h);
        }
        for (Iterator<String> e = h.keySet().iterator(); e.hasNext();) {
            String key = e.next();

            h.put(key, properties.get(key));
        }

    }

    /**
     * Utility method to read a property list (key and element/ comment pair)
     * from the input filename. The file is assumed to be using the ISO 8859-1
     * character encoding; that is each byte is one Latin1 character.
     */
    public static PropertiesParser loadProperties(String _fileName) {

        InputStream propsFile;
        PropertiesParser tempProp = new PropertiesParser();
        tempProp.fileName = _fileName;
        try {
            propsFile = new FileInputStream(_fileName);
            tempProp.load(propsFile);
            propsFile.close();
        } catch (IOException ioe) {
            System.out.println("I/O Exception.");
            ioe.printStackTrace();
        }

        return tempProp;

    }

    /**
     * Utility method to save a property file (key and element/ comment pair)
     * from the input filename. The file is assumed to be using the ISO 8859-1
     * character encoding; that is each byte is one Latin1 character.
     */
    public static void saveProperties(PropertiesParser p, String fileName,
            boolean sort) {

        OutputStream propsFile;

        if (sort) {
            p.sort();
        }

        try {
            propsFile = new FileOutputStream(fileName);
            p.store(propsFile, null);
            propsFile.close();
        } catch (IOException ioe) {
            System.out.println("I/O Exception.");
            ioe.printStackTrace();
        }

    }

    public static void saveProperties(PropertiesParser p, String fileName) {
        saveProperties(p, fileName, false);
    }

    /**
     * Updates the internal map representation of the private properties map.
     * Accepts the key value, map array from a jsp request.getParameterMap()
     * Comments of keys have the prefix "cmt." and auto timestamped keys with
     * the prefix "hidden." are skipped (so that variables can be hidden) the
     * key hiddenAnchor is skipped
     */
    public synchronized void setProperties(Map propertiesMap) {

        for (Iterator e = propertiesMap.keySet().iterator(); e.hasNext();) {
            String key = (String) e.next();

            // if this is a comment key then continue
            if (key.indexOf("cmt.") != -1)
                continue;
            if (key.indexOf("hidden.") != -1)
                continue;
            if (key.indexOf("hiddenAnchor") != -1)
                continue;

            String value = "";
            String originalValue = "";
            try {

                value =
                        new String(((String[]) propertiesMap.get(key))[0]
                                .getBytes(serverEncoding), "UTF8");
                if (value == null)
                    value = "";
                originalValue = value;

            } catch (java.io.UnsupportedEncodingException uee) {
                logger.fatal("UnsupportedEncodingException");
            }

            String mappedValue = this.getProperty(key);
            if (mappedValue == null) {
                mappedValue = value;
            }

            mappedValue = mappedValue.trim();
            value = value.trim();

            // skip empty values
            if (value.equals(""))
                continue;

            // based on unicode encoding
            boolean change =
                    !saveConvert(value, false).equals(
                            saveConvert(mappedValue, false));

            // count adding spaces as an easy way to modify timestamp
            if (saveConvert(originalValue, false).equals(
                    saveConvert(value, false).concat(" "))) {
                change = true;
            }

            setProperty(key, value);

            String comment = null;

            String typedComment = null;

            String oldComment = (String) this.getComment(key);

            boolean hadModifiedTag = false;

            if (propertiesMap.get("cmt." + key) != null) {
                typedComment =
                        new String(
                                ((String[]) propertiesMap.get("cmt." + key))[0]);
            }

            // hadModifiedTag is set if last modified was in comment,
            // AND comment was more than one line
            if (oldComment != null) {
                hadModifiedTag = oldComment.indexOf(lastModified) != -1;
            }

            // empty comment doesnt count
            if (typedComment != null && typedComment.trim().equals(""))
                typedComment = null;

            // force comment 'last modified' on changing
            // key-value pair with empty comment
            // (unless it had a modified tag and its
            // been removed)... logic is getting tricky...
            boolean forceComment = false;
            if (change && typedComment == null && !hadModifiedTag) {
                forceComment = true;
            }

            // if (change || forceComment) {
            // System.out.println("change: " + change);
            // System.out.println("forceComment: " + forceComment);
            // System.out.println("hadModifiedTag: " + hadModifiedTag);
            // System.out
            // .println("value: '" + saveConvert(value, false) + "'");
            // System.out.println("mappedValue: '"
            // + saveConvert(mappedValue, false) + "'");
            // System.out.println("typedComment: '" + typedComment + "'");
            // System.out.println("oldComment: '" + oldComment + "'");
            // }

            // if someone typed a comment OR the comment is being forced,
            // OR theres been a change
            if (typedComment != null || forceComment || change) {
                // System.out.println("change: " + change);
                // System.out.println("forceComment: " + forceComment);
                // System.out.println("value: '" +
                // saveConvert(value, false) +"'");
                // System.out.println("mappedValue: '" +
                // saveConvert(mappedValue, false) +"'");
                // System.out.println("cmt."+key+": '" +
                // propertiesMap.get("cmt."+key) +"'");
                // System.out.println("emptyComment: '" +
                // emptyComment +"'");

                if (forceComment) {
                    comment = "# " + lastModified + new Date();
                } else {
                    if (typedComment != null)
                        comment = typedComment;
                    else
                        comment = "# " + lastModified + new Date();
                }

                String lines[] = comment.split(newLine);

                String newComment = "";
                boolean hasModifiedTag = false;
                for (int i = 0; i < lines.length; i++) {
                    // trim things, ignore null
                    if (lines[i] != null) {
                        lines[i] = lines[i].trim();
                    } else {
                        continue;
                    }

                    // ignore empty things
                    if (lines[i].length() == 0)
                        continue;

                    // strip last modified tag if more than one
                    if (hasModifiedTag && lines[i].indexOf(lastModified) != -1) {
                        continue;
                    }

                    // mark has modified tag
                    if (lines[i].indexOf(lastModified) != -1)
                        hasModifiedTag = true;

                    // if change, replace last modified stamp
                    if (change && lines[i].indexOf(lastModified) != -1) {
                        lines[i] = "# " + lastModified + new Date();
                    }

                    // force comment tag
                    if (lines[i].charAt(0) != '#' && lines[i].charAt(0) != '!') {
                        if (lines[i] == null || lines[i].trim().equals("null"))
                            continue;
                        lines[i] = "# " + lines[i];
                    }

                    newComment = newComment.concat(lines[i]).concat(newLine);

                }

                // if there was a last modified tag, but
                // now there is no last modified tag,
                // then assume it was taken away on purpose
                // and skip this (ie only do it if it
                // didnt have one to begin with).
                if (!hasModifiedTag && !hadModifiedTag && change) {
                    newComment =
                            "# " + lastModified + new Date() + newLine
                                    + newComment;
                }
                setComment(key, newComment.trim());

            }

        }
    }

    /**
     * Get the last modified date of the properties file from the first comment
     * line. Returns Epoch 1970 if key isnt found or tag isnt found.
     */
    public Date getLastModifiedFile() {

        return lastModifiedFile;
    }

    /**
     * Get the last modified date from the comment text. Returns Epoch 1970 if
     * key isnt found or tag isnt found.
     */
    public Date getLastModified(String key) {

        Date lastModifiedDate = new Date(0);
        String comment = getComment(key);
        if (comment == null)
            return lastModifiedDate;
        String lines[] = comment.split(newLine);

        // loop through lines
        for (int i = 0; i < lines.length; i++) {

            // if change, replace last modified stamp
            if (lines[i].indexOf(lastModified) != -1) {
                // parse date. Format is standard date toString format
                // eg: last modified: Sun Aug 07 11:58:42 CEST 2005
                SimpleDateFormat formatter =
                        new SimpleDateFormat("E MMM dd HH:mm:ss ZZZ yyyy");
                try {
                    lastModifiedDate =
                            (Date) formatter.parse(lines[i], new ParsePosition(
                                    lines[i].indexOf(lastModified)
                                            + lastModified.length()));
                } catch (Exception e) {
                    logger.error(e);
                }
                break;
            }

        }

        return lastModifiedDate;
    }

    /**
     * Get the first link starting with "url:" (supports relative links) or with
     * "http://" if "url:" token is not found in the comment.
     */
    public String getLink(String key) {

        String link = null;
        String comment = getComment(key, true);
        if (comment == null)
            return link;
        String lines[] = comment.split(newLine);

        // loop through lines
        for (int i = 0; i < lines.length; i++) {

            // if change, replace last modified stamp
            if (lines[i].indexOf(protocol) != -1) {
                try {
                    // from protocol to first space
                    link =
                            lines[i].substring(lines[i].indexOf(protocol),
                                    lines[i].length());
                    if (link.indexOf(" ") != -1) {
                        link = link.substring(0, link.indexOf(" "));
                    }
                } catch (Exception e) {
                    logger.error(e);
                }
                break;
            }

        }

        // loop through lines
        for (int i = 0; i < lines.length; i++) {

            // if change, replace last modified stamp
            if (lines[i].indexOf("url:") != -1) {
                try {
                    // from protocol to first space
                    link =
                            lines[i].substring(lines[i].indexOf("url:")
                                    + "url:".length(), lines[i].length());
                    link = link.trim();
                    if (link.indexOf(" ") != -1) {
                        link = link.substring(0, link.indexOf(" "));
                    }
                } catch (Exception e) {
                    logger.error(e);
                }
                break;
            }

        }

        return link;
    }

    /**
     * Convert a nibble to a hex character
     * 
     * @param nibble
     *            the nibble to convert.
     */
    private static char toHex(int nibble) {
        return hexDigit[(nibble & 0xF)];
    }

    public static void main(String args[]) {
        // long t0 = System.currentTimeMillis();
        // PropertiesParser pp =
        // PropertiesParser.
        // loadProperties("E:\\development\\tinyelements.net\\src\\language.properties");
        //
        // for (Iterator keys = pp.propertyNames(); keys.hasNext();) {
        // System.out.println(keys.next());
        // }
        //
        // String value = pp.getProperty("64Kcolors","test");
        // System.out.println("value: " + value);
        // String comment = pp.getComment("64Kcolors");
        //
        // PropertiesParser.
        // saveProperties(pp,
        // "E:\\development\\tinyelements.net\\src\\language.properties.test2",
        // true);
        String link = null;
        String protocol = "http://";
        String comment = "hello # pokfpok http://www.opentracker.net?fo epok";
        // if (comment == null) return link;
        String lines[] = comment.split(newLine);

        // loop through lines
        for (int i = 0; i < lines.length; i++) {

            // if change, replace last modified stamp
            if (lines[i].indexOf(protocol) != -1) {
                // System.out.println(lines[i]);
                try {
                    // from protocol to first space
                    link =
                            lines[i].substring(lines[i].indexOf(protocol),
                                    lines[i].length());
                    if (link.indexOf(" ") != -1) {
                        link = link.substring(0, link.indexOf(" "));
                    }
                    // System.out.println("1. " + lines[i]);
                    // System.out.println("2. " + link);

                } catch (Exception e) {
                    logger.error(e);
                }
                break;
            }

        }

        // System.out.println(link);

    }
}
