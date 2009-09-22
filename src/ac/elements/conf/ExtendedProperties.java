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
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

/**
 * The Class ExtendedProperties extends java.util.Properties.
 * 
 * @author <a href="mailto:eddie@tinyelements.com">Eddie Moojen</a>
 */
public class ExtendedProperties extends Properties {

    /** The Constant keyValueSeparators. */
    private static final String keyValueSeparators = "=: \t\r\n\f";

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -7750401853864225511L;

    /** The Constant strictKeyValueSeparators. */
    private static final String strictKeyValueSeparators = "=:";

    // private static final String specialSaveChars = "=: \t\r\n\f#!";

    /** The Constant whiteSpaceChars. */
    private static final String whiteSpaceChars = " \t\r\n\f";

    /**
     * Returns true if the given line is a line that must be appended to the
     * next line
     * 
     * @param line
     *            the line
     * 
     * @return true, if successful
     */
    private boolean continueLine(String line) {
        int slashCount = 0;
        int index = line.length() - 1;
        while ((index >= 0) && (line.charAt(index--) == '\\'))
            slashCount++;
        return (slashCount % 2 == 1);
    }

    /**
     * Returns true if the given line is not of form key[ ]=[ ]value
     * 
     * @param line
     *            the line
     * 
     * @return true, if successful
     */
    private boolean fixContinueLine(String line) {

        if (line == null)
            return false;
        if (line.trim().length() == 0)
            return true;

        // Continue lines that end in slashes if they are not comments
        char firstChar = line.trim().charAt(0);

        if ((firstChar == '#') || (firstChar == '!') || (firstChar == '*')) {
            return false;
        }

        if (line.indexOf("=") == -1)
            return true;

        // if key contains spaces then assume continuation
        String key = line.substring(0, line.indexOf("=")).trim();

        if (key.indexOf(" ") != -1)
            return true;

        return continueLine(line);
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.Properties#load(java.io.InputStream)
     */
    public synchronized void load(InputStream inStream) throws IOException {

        BufferedReader in =
                new BufferedReader(new InputStreamReader(inStream, "8859_1"));

        String previousLine = null;

        while (true) {

            // Get next line
            String line = "";

            if (previousLine != null)
                line = previousLine;
            else
                line = in.readLine();

            if (line == null)
                return;

            if (line.trim().length() > 0) {
                // Continue lines that end in slashes if they are not comments
                char firstChar = line.trim().charAt(0);

                if ((firstChar != '#') && (firstChar != '!')) {

                    String nextLineTest = in.readLine();
                    previousLine = nextLineTest;
                    while (fixContinueLine(nextLineTest)) {
                        String nextLine = nextLineTest;

                        if (nextLine == null)
                            nextLine = "";

                        line = line.concat("\n").concat(nextLine);

                        previousLine = in.readLine();

                        nextLineTest = previousLine;

                    }

                    // Find start of key
                    int len = line.length();
                    int keyStart;
                    for (keyStart = 0; keyStart < len; keyStart++) {
                        if (whiteSpaceChars.indexOf(line.charAt(keyStart)) == -1)
                            break;
                    }

                    // Blank lines are ignored
                    if (keyStart == len)
                        continue;

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
                    put(key, value);

                } else {
                    // treat first # and ! as comments
                    previousLine = in.readLine();

                }
            } else {// ie when (line.trim().length() = 0)

                previousLine = in.readLine();

            }
        }// while
    }// load

    /**
     * Converts encoded &#92;uxxxx to unicode chars and changes special saved
     * chars to their original forms.
     * 
     * @param theString
     *            the the string
     * 
     * @return the string
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
                    }// if(aChar == 'u')
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
                }// (aChar == '\\')
            } else
                outBuffer.append(aChar);
        }// for
        return outBuffer.toString();
    }
}
