package ac.elements.parser;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ac.elements.sdb.ExtendedFunctions;

public class SimpleDBParser {
    /** The Constant log. */
    private final static Log log = LogFactory.getLog(SimpleDBParser.class);

    public ArrayList getTokens(String sql) {

        return null;
    }

    public static Object[] parseList(String keySyntax, String optionalDelimiters) {
        log.error("Parsing list: " + keySyntax);

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
                System.out.println("** got: " + keys[i]);
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
        System.out.println("**** got: " + subclause);
        return subclause;
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
