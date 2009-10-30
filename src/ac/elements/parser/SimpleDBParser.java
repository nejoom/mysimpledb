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
package ac.elements.parser;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.UUID;

import org.apache.log4j.Logger;

import ac.elements.sdb.ASimpleDBApiExtended;
import ac.elements.sdb.collection.SimpleDBDataList;
import ac.elements.sdb.collection.SimpleDBMap;

public class SimpleDBParser {
    /** The Constant log. */
    private final static Logger log = Logger.getLogger(SimpleDBParser.class);

    public static Object[] parseList(String keySyntax, String optionalDelimiters) {
        if (log.isDebugEnabled())
            log.trace("Parsing list: " + keySyntax);

        CsvReader reader = CsvReader.parse(keySyntax);

        Charset charSet = Charset.forName("UTF-8");
        reader.setCharacterSet(charSet);
        reader.setTextQualifier(optionalDelimiters);
        reader.setEscapeMode(CsvReader.ESCAPE_MODE_DOUBLED);

        try {
            reader.readRecord();
        } catch (IOException e) {
            throw new RuntimeException("Illegal insertExpression at: "
                    + keySyntax);
        }

        Object[] keys = new Object[reader.getColumnCount()];
        for (int i = 0; i < keys.length; i++) {
            try {
                if (reader.isQualified(i)) {
                    keys[i] =
                            ExtendedFunctions.stripNonValidXML(reader.get(i))
                                    .trim();
                } else {
                    keys[i] =
                            SimpleDBConverter.getStringOrNumber(reader.get(i));
                }
            } catch (IOException e) {
                throw new RuntimeException("Illegal insertExpression at: "
                        + keySyntax);
            }
        }
        return keys;
    }

    /*
     * Returns the attributes selected in an sql statement. null if there is
     * none or an illegal select statement.
     */
    public static String getAttributes(String sql) {
        int fromIndex =
                indexOfIgnoreCaseRespectMarker(0, sql, " FROM ", "(\"'`",
                        ")\"'`");
        int selectIndex =
                indexOfIgnoreCaseRespectMarker(0, sql, "SELECT", "(\"'`",
                        ")\"'`");
        String subclause = "";
        if (fromIndex == -1 || selectIndex == -1) {
            return null;
        } else {
            subclause =
                    sql.substring(selectIndex + "SELECT".length(), fromIndex);
        }
        // System.out.println("**** got: " + subclause);
        return subclause;
    }

    /*
     * Returns the type of operation being performed: SELECT DELETE REPLACE
     * INSERT CREATE
     */
    public static String getOperation(String sql) {
        if (sql == null)
            return null;
        String operation = sql.trim().split(" ")[0].trim();
        if (operation.toLowerCase().equals("select")) {
            return "SELECT";
        } else if (operation.toLowerCase().equals("delete")) {
            return "DELETE";
        } else if (operation.toLowerCase().equals("replace")) {
            return "REPLACE";
        } else if (operation.toLowerCase().equals("insert")) {
            return "INSERT";
        } else if (operation.toLowerCase().equals("create")) {
            return "CREATE";
        } else {
            return "UNKNOWN";
        }
    }

    @SuppressWarnings("unchecked")
    public static ArrayList<Object> getItemNames(String sql) {

        log.trace("Entering insert getKeys");
        String insertExpression = sql;
        insertExpression.replaceAll("replace", "insert");
        String parseString =
                insertExpression.substring("insert into ".length());
        parseString =
                parseString.substring(
                        parseString.substring(0, parseString.indexOf(" "))
                                .length()).trim();

        // get the key syntax without brackets
        String keySyntax =
                parseString.substring(
                        1,
                        SimpleDBParser.indexOfIgnoreCaseRespectQuotes(1,
                                parseString, ")", '`')).trim();

        parseString = parseString.substring(keySyntax.length() + 2).trim();

        Object[] items = null;
        ArrayList<Object> itemNames = new ArrayList<Object>();

        boolean useItemNameColumn = false;
        Object[] keys = getKeys(sql);

        if (keys != null && keys.length > 0 && keys[0].equals("itemName()")) {
            useItemNameColumn = true;
        }

        // if itemName is not a defined column, then use standard syntax
        if (!useItemNameColumn) {
            String itemSyntax =
                    parseString.substring(
                            parseString.toLowerCase().indexOf("values")
                                    + "values".length()).trim();

            log.error("Parsing itemSyntax: " + itemSyntax);
            StringBuffer itemBuffer = new StringBuffer(itemSyntax);
            int pointer = 0;
            // strip things to contain only item names
            while (pointer < itemBuffer.length()) {
                if (itemBuffer.charAt(pointer) == '(') {
                    itemBuffer.deleteCharAt(pointer);
                    while (itemBuffer.length() > 0
                            && itemBuffer.charAt(pointer) != ')') {
                        itemBuffer.deleteCharAt(pointer);
                    }
                    if (itemBuffer.charAt(pointer) == ')')
                        itemBuffer.deleteCharAt(pointer);
                }
                pointer++;
            }
            log.error("itemBuffer: " + itemBuffer.toString());
            items = SimpleDBParser.parseList(itemBuffer.toString(), "'\"");

            if (items.length == 0) {
                // generate random UUIDs
                itemNames.add(UUID.randomUUID().toString());
            } else {
                for (int i = 0; i < items.length; i++) {
                    Object itemName = items[i];
                    if (itemName == null || itemName.toString().equals("")) {
                        itemNames.add(UUID.randomUUID().toString());
                        continue;
                    }

                    itemNames.add(itemName);
                }
            }
            log.info("Parsed itemSyntax: " + itemNames);

        } else {

            // first key is itemName(), assume first column is itemName()
            // unique
            // key

            String itemSyntax =
                    parseString.substring(
                            parseString.toLowerCase().indexOf("values")
                                    + "values".length()).trim();

            if (log.isDebugEnabled())
                log.trace("Parsing dataSyntax: " + itemSyntax);

            StringBuffer dataBuffer = new StringBuffer(itemSyntax);
            int pointer = 0;
            // strip things to contain only data lists
            while (pointer < dataBuffer.length()) {

                if (dataBuffer.charAt(pointer) == '(') {
                    while (dataBuffer.length() > 0
                            && dataBuffer.charAt(pointer) != ')') {
                        pointer++;
                    }
                    pointer++;
                    if (dataBuffer.length() < pointer)
                        dataBuffer.insert(pointer, ',');
                    pointer++;
                } else {
                    dataBuffer.deleteCharAt(pointer);
                }
            }

            // replace ( and ) with | and then parse like its a | delimited
            // file
            // todo make beter
            String data =
                    dataBuffer.toString().replace('(', '|').replace(')', '|');
            Object[] valueSequence = SimpleDBParser.parseList(data, "|");

            SimpleDBDataList dataList = new SimpleDBDataList();
            if (valueSequence.length == 0) {
                throw new RuntimeException(
                        "Illegal insertExpression in values: "
                                + insertExpression);
            } else {
                for (int i = 0; i < valueSequence.length; i++) {
                    String valueString = (String) valueSequence[i];
                    Object[] values =
                            SimpleDBParser.parseList(valueString, "'\"");
                    if (!dataList.getItemNames().contains(values[0])) {

                        if (values[0].equals("null")) {
                            // generate random UUIDs
                            values[0] = UUID.randomUUID().toString();
                        }
                        itemNames.add(values[0]);
                    } else {
                        // Search for element in list
                        int index =
                                Collections.binarySearch(dataList
                                        .getItemNames(), values[0]);
                        SimpleDBMap sdbMap = dataList.get(index);
                        if (sdbMap.getItemName().equals(values[0])) {
                            for (int j = 1; j < values.length; j++) {
                                sdbMap.put(keys[j], values[j]);
                            }
                            if (log.isDebugEnabled()) {
                                log
                                        .trace("TODO: improve on method getItemName? "
                                                + sdbMap);

                                log.trace(dataList.getItemNames());
                                log.trace(dataList.get(index).toStringF());
                            }
                        } else {
                            log.error("TODO: improve on method "
                                    + "getItemName not found");
                            log.error(dataList.getItemNames());
                        }
                    }
                }
            }

        }
        return itemNames;
    }

    public static Object[] getKeys(String sql) {

        log.trace("Entering insert getKeys");
        String insertExpression = sql;
        insertExpression.replaceAll("replace", "insert");

        // make sure spaces are ok and we have no enters
        insertExpression = ExtendedFunctions.trimSentence(insertExpression);

        log.trace("Got insert expression: " + insertExpression);
        // basic check if expression has good syntax
        if (insertExpression.trim().toLowerCase().indexOf("insert") != 0
                && insertExpression.trim().toLowerCase().indexOf("values") != 0) {
            throw new RuntimeException(
                    "Illegal insertExpression/ replaceExpression: "
                            + insertExpression);
        }

        String parseString =
                insertExpression.substring("insert into ".length());

        parseString =
                parseString.substring(
                        parseString.substring(0, parseString.indexOf(" "))
                                .length()).trim();

        // get the key syntax without brackets
        String keySyntax =
                parseString.substring(
                        1,
                        SimpleDBParser.indexOfIgnoreCaseRespectQuotes(1,
                                parseString, ")", '`')).trim();

        Object[] keys = SimpleDBParser.parseList(keySyntax, "`");

        return keys;
    }

    /*
     * Returns the type of operation being performed: SELECT DELETE REPLACE
     * INSERT CREATE
     */
    public static boolean isBatchOperation(String sql) {
        log.error("isBatchOperation(String sql)");
        if (sql == null)
            return false;
        if (!SimpleDBParser.getOperation(sql).equals("INSERT")
                && !SimpleDBParser.getOperation(sql).equals("REPLACE")) {
            return false;
        } else if (SimpleDBParser.getOperation(sql).equals("INSERT")
                || SimpleDBParser.getOperation(sql).equals("REPLACE")) {
            SimpleDBDataList dataList =
                    ASimpleDBApiExtended.extractSimpleDBList(sql);
            if (dataList.getItemNames().size() > 1)
                return true;
        }
        return false;
    }

    /*
     * Returns the domain of an sql statement. null if there is none.
     */
    public static String getDomain(String sql) {
        int fromIndex =
                indexOfIgnoreCaseRespectMarker(0, sql, " FROM ", "(\"'`",
                        ")\"'`");
        String subclause = "";
        if (fromIndex == -1) {
            int intoIndex =
                    indexOfIgnoreCaseRespectMarker(0, sql, " INTO ", "(\"'`",
                            ")\"'`");
            if (intoIndex == -1)
                return null;
            subclause =
                    sql.substring(intoIndex + " INTO ".length(), sql.length());
        } else {
            subclause =
                    sql.substring(fromIndex + " FROM ".length(), sql.length());
        }
        // System.out.println("**** got: " + subclause);
        String domain =
                subclause.substring(0, subclause.indexOf(' ') == -1 ? subclause
                        .length() : subclause.indexOf(' '));
        domain = ExtendedFunctions.trimCharacter(domain.trim(), '`').trim();
        return domain;
    }

    /*
     * Returns the sql statements where clause type encoded. If the sql passed
     * is null, null is returned.
     */
    public static String encodeWhereClause(String sql) {

        final String originalWhereClause = getWhereClause(sql);
        String whereClause = originalWhereClause;
        if (whereClause == null)
            return sql;

        // type modifying tokens
        // = , !=, >, >=, <, <=

        // replace operators with invalid xml code (which should never get here)
        // to avoid collisions with = sign
        whereClause = whereClause.replace("!=", "\u0001");
        whereClause = whereClause.replace(">=", "\u0002");
        whereClause = whereClause.replace("<=", "\u0003");
        // System.out.println("process =");
        String token = "=";
        whereClause = tokenEncode(whereClause, token);
        // System.out.println("process >");
        token = ">";
        whereClause = tokenEncode(whereClause, token);
        // System.out.println("process <");
        token = "<";
        whereClause = tokenEncode(whereClause, token);
        // System.out.println("process !=");
        token = "\u0001";
        whereClause = tokenEncode(whereClause, token);
        // System.out.println("process >=");
        token = "\u0002";
        // System.out.println("process <=");
        whereClause = tokenEncode(whereClause, token);
        token = "\u0003";
        whereClause = tokenEncode(whereClause, token);
        token = " between ";
        whereClause = tokenEncode(whereClause, token);
        whereClause = whereClause.replace("\u0001", "!=");
        whereClause = whereClause.replace("\u0002", ">=");
        whereClause = whereClause.replace("\u0003", "<=");

        if (originalWhereClause.equals(whereClause)) {
            return sql;
        }
        sql =
                sql.substring(0, getWhereClauseIndex(sql))
                        + whereClause
                        + sql.substring(getWhereClauseIndex(sql)
                                + originalWhereClause.length(), sql.length());

        // System.out.println("WHERE: " + sql);
        // System.out.println("whereClause: " + whereClause);
        // System.out.println("WHERE: " + sql);
        return sql;
    }

    /**
     * @param whereClause
     * @param token
     */
    private static String tokenEncode(String whereClause, String token) {
        int i = 0;

        // for debugging the whereClause with working index
        String length = "                                 ";
        for (int j = 0; j < 5; j++)
            length += length;

        int tokenLength = token.length();
        if (tokenLength < 1)
            return whereClause;

        // need to parameterize quotes, so that enclosed quotes in quotes
        // doesn't mess things up
        String quotes = "\"'";
        do {

            // working index i
            i =
                    indexOfIgnoreCaseRespectMarker(i, whereClause, token,
                            quotes, quotes);

            if (i == -1)
                continue;

            i += tokenLength;

            // move onwards towards first none white space
            while (whereClause.charAt(i) == ' ')
                i++;

            // quote enclosed value attributes are strings
            if (whereClause.charAt(i) != '\'' && whereClause.charAt(i) != '"') {

                int beginIndex = i;

                int nextSpace = whereClause.indexOf(' ', beginIndex);

                // a closeing bracket is also a marker for a variable
                // eg and (list < variable) or (...)
                int nextBracket = whereClause.indexOf(')', beginIndex);
                if (nextBracket != -1 && nextBracket < nextSpace) {
                    nextSpace = nextBracket;
                }
                // no spaces? then just use the length of the clause
                if (nextSpace == -1)
                    nextSpace = whereClause.length();

                String value = whereClause.substring(beginIndex, nextSpace);

                // this is what has been typed in the where clause e.g. 6.6d
                Object typedValue = SimpleDBConverter.getStringOrNumber(value);

                String encodedValue =
                        "'" + SimpleDBConverter.encodeValue(typedValue) + "'";

                whereClause =
                        whereClause.substring(0, beginIndex)
                                + encodedValue
                                + whereClause.substring(nextSpace, whereClause
                                        .length());
                if (nextSpace == whereClause.length())
                    i = -1;
                else
                    i += encodedValue.length() + 1;

                if (token.equalsIgnoreCase(" between ") && i != -1) {

                    // System.out.println(whereClause);
                    // System.out.println(length.substring(0, i -
                    // 1).concat("^"));

                    // need to track back one char (--i), don't ask why, see...
                    i =
                            indexOfIgnoreCase(--i, whereClause, " and ")
                                    + " and ".length();

                    while (whereClause.charAt(i) == ' ')
                        i++;

                    // System.out.println(whereClause);
                    // System.out.println(length.substring(0, i -
                    // 1).concat("^"));

                    if (whereClause.charAt(i) != '\''
                            && whereClause.charAt(i) != '"') {

                        int andBeginIndex = i;

                        int andNextSpace =
                                whereClause.indexOf(' ', andBeginIndex);

                        // a closeing bracket is also a marker for a variable
                        // eg and (list < variable) or (...)
                        int andNextBracket =
                                whereClause.indexOf(')', andBeginIndex);
                        if (andNextBracket != -1
                                && andNextBracket < andNextSpace) {
                            andNextSpace = andNextBracket;
                        }
                        // no spaces? then just use the length of the clause
                        if (andNextSpace == -1)
                            andNextSpace = whereClause.length();

                        String andValue =
                                whereClause.substring(andBeginIndex,
                                        andNextSpace);

                        // this is what has been typed in the where clause e.g.
                        // 6.6d
                        Object typedVal =
                                SimpleDBConverter.getStringOrNumber(andValue);

                        String andEncodedValue =
                                "'" + SimpleDBConverter.encodeValue(typedVal)
                                        + "'";

                        whereClause =
                                whereClause.substring(0, andBeginIndex)
                                        + andEncodedValue
                                        + whereClause.substring(andNextSpace,
                                                whereClause.length());
                        if (andNextSpace == whereClause.length())
                            i = -1;
                        else
                            i += andEncodedValue.length() + 1;

                    }
                }
                // System.out.println("clause: " + whereClause);
            } else {
                // i++ doesnt work, next search will be in quote, so need to
                // skip to end quote
                char quote = whereClause.charAt(i + tokenLength);
                if (whereClause.indexOf(quote, i + tokenLength + 1) > 0)
                    i = whereClause.indexOf(quote, i + tokenLength + 1);
                else
                    i++;
                // if (i < whereClause.length()) {
                // System.out.println("whereClause " + whereClause);
                // System.out.println("got a " + whereClause.charAt(i)
                // + " i: " + i);
                //
                // }
            }

            if (token.equalsIgnoreCase(" betweeen ")) {
                System.out.println("got and");
            }

        } while (i < whereClause.length() && i != -1);
        return whereClause;
    }

    /*
     * Returns the start index of where clause. -1 if there is none.
     */
    private static int getWhereClauseIndex(String sql) {

        return indexOfIgnoreCaseRespectMarker(0, sql, " WHERE ", "(\"'`",
                ")\"'`");

    }

    /*
     * Returns the where clause of an sql statement, including the WHERE token.
     * null if there is no where clause or the sql passed is null.
     */
    public static String getWhereClause(String sql) {
        if (sql == null)
            return null;
        int whereIndex =
                indexOfIgnoreCaseRespectMarker(0, sql, " WHERE ", "(\"'`",
                        ")\"'`");
        String subclause = sql;
        if (whereIndex != -1) {
            subclause = sql.substring(whereIndex, sql.length());
        } else {
            return null;
        }
        int orderIndex =
                indexOfIgnoreCaseRespectMarker(0, subclause, " ORDER ",
                        "(\"'`", ")\"'`");

        if (orderIndex != -1)
            subclause = subclause.substring(0, orderIndex);

        int limitIndex =
                indexOfIgnoreCaseRespectMarker(0, subclause, " LIMIT ",
                        "(\"'`", ")\"'`");

        if (limitIndex != -1)
            subclause = subclause.substring(0, limitIndex);

        return subclause;
    }

    /*
     * Returns the order clause of an sql statement, including the ORDER token.
     * null if there is no where clause or the sql passed is null.
     */
    public static String getOrderClause(String sql) {
        if (sql == null)
            return null;
        int orderIndex =
                indexOfIgnoreCaseRespectMarker(0, sql, " ORDER BY ", "(\"'`",
                        ")\"'`");
        String subclause = sql;
        if (orderIndex != -1) {
            subclause = sql.substring(orderIndex, sql.length());
        } else {
            return null;
        }

        int limitIndex =
                indexOfIgnoreCaseRespectMarker(0, subclause, " LIMIT ",
                        "(\"'`", ")\"'`");

        if (limitIndex != -1)
            subclause = subclause.substring(0, limitIndex);

        return subclause;
    }

    /*
     * Returns the limit of an sql statement excluding the limit token. null if
     * there is no where clause or the sql passed is null.
     */
    public static String getLimitClause(String sql) {
        if (sql == null)
            return null;
        int limitIndex =
                indexOfIgnoreCaseRespectMarker(0, sql, " LIMIT ", "(\"'`",
                        ")\"'`");
        String subclause = sql;
        if (limitIndex != -1) {
            subclause =
                    sql
                            .substring(limitIndex + " LIMIT ".length(), sql
                                    .length());
        } else {
            return null;
        }

        return subclause;
    }

    // from com.mysql.jdbc.StringUtils

    public static int indexOfIgnoreCaseRespectMarker(int startAt, String src,
            String target, String marker, String markerCloses) {
        char contextMarker = Character.MIN_VALUE;
        boolean escaped = false;
        int markerTypeFound = 0;
        int srcLength = src.length();
        int ind = 0;

        for (int i = startAt; i < srcLength; i++) {
            char c = src.charAt(i);

            if (c == markerCloses.charAt(markerTypeFound) && !escaped) {
                contextMarker = Character.MIN_VALUE;
            } else if ((ind = marker.indexOf(c)) != -1 && !escaped
                    && contextMarker == Character.MIN_VALUE) {
                markerTypeFound = ind;
                contextMarker = c;
            } else if ((Character.toUpperCase(c) == Character
                    .toUpperCase(target.charAt(0)) || Character.toLowerCase(c) == Character
                    .toLowerCase(target.charAt(0)))
                    && !escaped && contextMarker == Character.MIN_VALUE) {
                if (startsWithIgnoreCase(src, i, target))
                    return i;
            }
        }

        return -1;

    }

    public static int indexOfIgnoreCaseRespectQuotes(int startAt, String src,
            String target, char quoteChar) {
        char contextMarker = Character.MIN_VALUE;
        boolean escaped = false;

        int srcLength = src.length();

        for (int i = startAt; i < srcLength; i++) {
            char c = src.charAt(i);

            if (c == contextMarker && !escaped) {
                contextMarker = Character.MIN_VALUE;
            } else if (c == quoteChar && !escaped
                    && contextMarker == Character.MIN_VALUE) {
                contextMarker = c;
                // This test looks complex, but remember that in certain
                // locales, upper case
                // of two different codepoints coverts to same codepoint, and
                // vice-versa.
            } else if ((Character.toUpperCase(c) == Character
                    .toUpperCase(target.charAt(0)) || Character.toLowerCase(c) == Character
                    .toLowerCase(target.charAt(0)))
                    && !escaped && contextMarker == Character.MIN_VALUE) {
                if (startsWithIgnoreCase(src, i, target))
                    return i;
            }
        }

        return -1;

    }

    private final static boolean isNotEqualIgnoreCharCase(String searchIn,
            char firstCharOfPatternUc, char firstCharOfPatternLc, int i) {
        return Character.toLowerCase(searchIn.charAt(i)) != firstCharOfPatternLc
                && Character.toUpperCase(searchIn.charAt(i)) != firstCharOfPatternUc;
    }

    /**
     * DOCUMENT ME!
     * 
     * @param searchIn
     *            DOCUMENT ME!
     * @param searchFor
     *            DOCUMENT ME!
     * 
     * @return DOCUMENT ME!
     */
    public final static int indexOfIgnoreCase(String searchIn, String searchFor) {
        return indexOfIgnoreCase(0, searchIn, searchFor);
    }

    public final static int indexOfIgnoreCase(int startingPosition,
            String searchIn, String searchFor) {
        if ((searchIn == null) || (searchFor == null)
                || startingPosition > searchIn.length()) {
            return -1;
        }

        int patternLength = searchFor.length();
        int stringLength = searchIn.length();
        int stopSearchingAt = stringLength - patternLength;

        if (patternLength == 0) {
            return -1;
        }

        // Brute force string pattern matching
        // Some locales don't follow upper-case rule, so need to check both
        char firstCharOfPatternUc = Character.toUpperCase(searchFor.charAt(0));
        char firstCharOfPatternLc = Character.toLowerCase(searchFor.charAt(0));

        // note, this also catches the case where patternLength > stringLength
        for (int i = startingPosition; i <= stopSearchingAt; i++) {
            if (isNotEqualIgnoreCharCase(searchIn, firstCharOfPatternUc,
                    firstCharOfPatternLc, i)) {
                // find the first occurrence of the first character of searchFor
                // in searchIn
                while (++i <= stopSearchingAt
                        && (isNotEqualIgnoreCharCase(searchIn,
                                firstCharOfPatternUc, firstCharOfPatternLc, i)))
                    ;
            }

            if (i <= stopSearchingAt /* searchFor might be one character long! */) {
                // walk searchIn and searchFor in lock-step starting just past
                // the first match,bail out if not
                // a match, or we've hit the end of searchFor...
                int j = i + 1;
                int end = j + patternLength - 1;
                for (int k = 1; j < end
                        && (Character.toLowerCase(searchIn.charAt(j)) == Character
                                .toLowerCase(searchFor.charAt(k)) || Character
                                .toUpperCase(searchIn.charAt(j)) == Character
                                .toUpperCase(searchFor.charAt(k))); j++, k++)
                    ;

                if (j == end) {
                    return i;
                }
            }
        }

        return -1;
    }

    /**
     * Determines whether or not the string 'searchIn' contains the string
     * 'searchFor', dis-regarding case starting at 'startAt' Shorthand for a
     * String.regionMatch(...)
     * 
     * @param searchIn
     *            the string to search in
     * @param startAt
     *            the position to start at
     * @param searchFor
     *            the string to search for
     * 
     * @return whether searchIn starts with searchFor, ignoring case
     */
    public static boolean startsWithIgnoreCase(String searchIn, int startAt,
            String searchFor) {
        return searchIn.regionMatches(true, startAt, searchFor, 0, searchFor
                .length());
    }

    /**
     * The main method.
     * 
     * @param args
     *            the arguments
     */
    public static void main(String[] args) {

        String statement =
                "The question as to whether the jab is"
                        + " superior to the cross has been debated for some time in"
                        + " boxing circles. However, it is my opinion that this"
                        + " false dichotomy misses the point. I call your attention"
                        + " to the fact that the best boxers often use a combination of"
                        + " the two. I call your attention to the fact that Mohammed"
                        + " Ali,the Greatest of the sport of boxing, used both. He had"
                        + " a tremendous jab, yet used his cross effectively, often,"
                        + " and well";

        String newStmt =
                statement.replaceAll("The question as to whether", "Whether");

        newStmt = newStmt.replaceAll(" of the sport of boxing", "");
        newStmt = newStmt.replaceAll("amount of success", "success");
        newStmt =
                newStmt.replaceAll("However, it is my opinion that this",
                        "This");

        newStmt = newStmt.replaceAll("a combination of the two", "both");
        newStmt =
                newStmt.replaceAll(
                        "This is in spite of the fact that" + " the", "The");
        newStmt =
                newStmt
                        .replaceAll("I call your attention to the fact that",
                                "");

        System.out.println("BEFORE:\n" + statement + "\n");
        System.out.println("AFTER:\n" + newStmt);

        String sql =
                "SELECT * FROM ijoasijdasd AS blabal where key=' \" ON DUPLICATE KEY UPDATE \"'";

        System.out.println(getDomain(sql));

        String list =
                "'He said, \"That''s the ticket!\"', \"He said, \"\"That's the ticket!\"\"\", 'what,''',s up doc', \"whats,\"\",s up doc\"";
        Object[] result = (Object[]) parseList(list, "'\"");
        System.out.println(list);
        System.out.println(result.length);
        System.out.println(result[0]);
        System.out.println(result[1]);
        System.out.println(result[2]);
        System.out.println(result[3]);
        System.out.println(result[4]);

        System.out.println("------");
        // sql =
        // "SELECT * FROM ijoasijdasd AS blabal where key=' \" ON DUPLICATE KEY UPDATE \"'";
        // getWhereClause(sql);
        // sql = "SELECT * FROM temp";
        // getWhereClause(sql);
        // sql = "SELECT * FROM temp order by key limit 10";
        // getWhereClause(sql);
        // sql =
        // "SELECT \" where \", ' order ', ` limit ` FROM temp WHERE key=today LIMIT 10";
        // getWhereClause(sql);
        // sql =
        // "SELECT \" where \", ' order ', ` limit ` FROM temp WHERE key=today or key1=4.3f or key3=4 or key=5d or key=9l or key='9l' or key =\"9l\" ORDER by key LIMIT 10";
        // getWhereClause(sql);
        // sql =
        // "SELECT \" where \", ' order ', ` limit ` FROM temp WHERE key=today or key1!=4.3f or key3>=4 or key<=5d or key<9l or key>'9l' or key =\"9l\" ORDER by key LIMIT 10";
        // getWhereClause(sql);
        // encodeWhereClause(sql);
        sql =
                "SELECT \" where \", ' order ', ` limit ` FROM temp WHERE key='a list >= ''erer = erok ' or key= today or key1!=-4.3f or key3>=  4 or key<=  5d or key <  9l or key >  '9l' or key =  \"9l\" ORDER by key LIMIT 10";

        System.out.println(encodeWhereClause(sql));

        // todo Following gives error
        sql =
                "SELECT \" where \", ' order ', ` limit ` FROM temp WHERE key='a list >= ''erer !=\"  erok ' or key= today or key1!=-4.3f or key3>=  4 or key<=  5d or key <  9l or key >  '9l' or key =  \"9l\" ORDER by key LIMIT 10";
        System.out.println("*** " + getAttributes(sql));
        // System.out.println(encodeWhereClause(sql));
        sql =
                "Select * from Location where countryCode='NL' and latitude => 52 and latitude =< 53";
        System.out.println(encodeWhereClause(sql));
        sql =
                "Select * from Location where (countryCode='NL' and latitude => 52) and latitude =< 53 and latitude BETWEEN 54 and   55";
        System.out.println(encodeWhereClause(sql));

        sql =
                "Select * from Location where (countryCode='NL' and latitude => 52) and latitude =< 53 and latitude BETWEEN 54 and   '55'";
        System.out.println(encodeWhereClause(sql));

        sql =
                "Select * from Location where (countryCode='NL' and latitude => 52) and key2=' LIMIT ' (latitude =< 53 or latitude BETWEEN 54 and   55) and key = '22' LIMIT 10";
        System.out.println(encodeWhereClause(sql));
        System.out.println(getLimitClause(sql));
        System.out.println(getWhereClause(sql));
        System.out.println(0xD7FF);
    }
}
