package ac.elements.parser;

import java.math.BigInteger; //import ac.elements.conversion.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang.math.NumberUtils;

import ac.elements.sdb.ExtendedFunctions;

public class SimpleDBConverter {

    private final static int RADIX = 36;

    public static void main(String[] args) {
        String date = SimpleDBConverter.encodeDate(new Date(3453l));
        System.out.println(date);
        System.out.println(date.indexOf('-'));
        System.out.println(date.indexOf('-', 5));
        System.out.println(date.indexOf('T'));
        System.out.println(date.indexOf(':'));
        System.out.println(date.indexOf(':', 14));
        System.out.println(date.indexOf('.'));
        System.out.println(date.indexOf('+'));
        boolean boodate = false;
        if (date.indexOf('-') == 4 && date.indexOf('-', 5) == 7
                && date.indexOf('T') == 10 && date.indexOf(':') == 13
                && date.indexOf(':', 14) == 16 && date.indexOf('.') == 19
                && date.indexOf('+') == 23) {
            boodate = true;
        }
        System.out.println(boodate);

        System.out.println(getStringOrNumber("00.4") instanceof Float);
        System.out.println(getStringOrNumber("00.4d") instanceof Double);
        System.out.println(getStringOrNumber("4d") instanceof Double);
        System.out.println(getStringOrNumber("4f") instanceof Float);
        System.out.println(getStringOrNumber("4L") instanceof Long);
        System.out.println(getStringOrNumber("4l") instanceof Long);
        System.out.println(getStringOrNumber("4") instanceof Integer);
        System.out.println(getStringOrNumber("213 maximind") instanceof String);

    }

    /**
     * Decodes zero-padded positive long value from the string representation
     * 
     * com.xerox.amazonws.sdb.DataUtils
     * 
     * @param value
     *            zero-padded string representation of the long
     * @return original long value
     */
    private static long decodeLong(String value) {
        BigInteger bi = new BigInteger(value, RADIX);
        bi = bi.add(BigInteger.valueOf(Long.MIN_VALUE));
        return bi.longValue();
    }

    /**
     * Encodes real long value into a string by offsetting and zero-padding
     * number up to the specified number of digits. Use this encoding method if
     * the data range set includes both positive and negative values.
     * 
     * com.xerox.amazonws.sdb.DataUtils
     * 
     * @param number
     *            long to be encoded
     * @return string representation of the long
     */
    private static String encodeLong(long number) {
        int maxNumDigits =
                BigInteger.valueOf(Long.MAX_VALUE).subtract(
                        BigInteger.valueOf(Long.MIN_VALUE)).toString(RADIX)
                        .length();
        long offsetValue = Long.MIN_VALUE;

        BigInteger offsetNumber =
                BigInteger.valueOf(number).subtract(
                        BigInteger.valueOf(offsetValue));

        String longString = offsetNumber.toString(RADIX);
        int numZeroes = maxNumDigits - longString.length();
        StringBuffer strBuffer =
                new StringBuffer(numZeroes + longString.length());
        for (int i = 0; i < numZeroes; i++) {
            strBuffer.insert(i, '0');
        }
        strBuffer.append(longString);
        return strBuffer.toString();
    }

    /**
     * Decodes zero-padded positive integer value from the string representation
     * 
     * com.xerox.amazonws.sdb.DataUtils
     * 
     * @param value
     *            zero-padded string representation of the integer
     * @return original integer value
     */
    private static int decodeInt(String value) {
        return (int) (Long.parseLong(value, RADIX) - Integer.MIN_VALUE);
    }

    /**
     * Encodes real integer value into a string by offsetting and zero-padding
     * number up to the specified number of digits. Use this encoding method if
     * the data range set includes both positive and negative values.
     * 
     * com.xerox.amazonws.sdb.DataUtils
     * 
     * @param number
     *            int to be encoded
     * @return string representation of the int
     */
    private static String encodeInt(int number) {
        int maxNumDigits =
                BigInteger.valueOf(Integer.MAX_VALUE).subtract(
                        BigInteger.valueOf(Integer.MIN_VALUE)).toString(RADIX)
                        .length();
        long offsetValue = Integer.MIN_VALUE;

        BigInteger offsetNumber =
                BigInteger.valueOf(number).subtract(
                        BigInteger.valueOf(offsetValue));
        String longString = offsetNumber.toString(RADIX);
        int numZeroes = maxNumDigits - longString.length();
        StringBuffer strBuffer =
                new StringBuffer(numZeroes + longString.length());
        for (int i = 0; i < numZeroes; i++) {
            strBuffer.insert(i, '0');
        }
        strBuffer.append(longString);
        return strBuffer.toString();
    }

    /**
     * Decodes zero-padded positive short value from the string representation
     * 
     * com.xerox.amazonws.sdb.DataUtils
     * 
     * @param value
     *            zero-padded string representation of the short
     * @return original short value
     */
    private static short decodeShort(String value) {
        return (short) (Long.parseLong(value, RADIX) - Short.MIN_VALUE);
    }

    /**
     * Encodes real long value into a string by offsetting and zero-padding
     * number up to the specified number of digits. Use this encoding method if
     * the data range set includes both positive and negative values.
     * 
     * com.xerox.amazonws.sdb.DataUtils
     * 
     * @param number
     *            short to be encoded
     * @return string representation of the short
     */
    private static String encodeShort(short number) {
        int maxNumDigits =
                BigInteger.valueOf(Short.MAX_VALUE).subtract(
                        BigInteger.valueOf(Short.MIN_VALUE)).toString(RADIX)
                        .length();
        long offsetValue = Short.MIN_VALUE;

        BigInteger offsetNumber =
                BigInteger.valueOf(number).subtract(
                        BigInteger.valueOf(offsetValue));
        String longString = offsetNumber.toString(RADIX);
        int numZeroes = maxNumDigits - longString.length();
        StringBuffer strBuffer =
                new StringBuffer(numZeroes + longString.length());
        for (int i = 0; i < numZeroes; i++) {
            strBuffer.insert(i, '0');
        }
        strBuffer.append(longString);
        return strBuffer.toString();
    }

    /**
     * Decodes zero-padded positive double value from the string representation
     * 
     * com.sleepycat.bind.tuple.TupleInput
     * 
     * @param value
     *            zero-padded string representation of the double
     * @return original double value
     */
    private static Double decodeSortedDouble(String value) {

        long val = decodeLong(value);
        val ^= (val < 0) ? 0x8000000000000000L : 0xffffffffffffffffL;
        val = (long) (val ^ 0xffffffffffffffffL);
        return Double.longBitsToDouble(val);
    }

    /**
     * Writes a signed double (eight byte) value to the buffer, with support for
     * correct default sorting of all values. Writes values that can be read
     * using {@link TupleInput#readSortedDouble}.
     * 
     * <p>
     * <code>Float.doubleToLongBits</code> and the following bit manipulations
     * are used to convert the signed double value to a representation that is
     * sorted correctly by default.
     * </p>
     * 
     * <pre>
     * long longVal = Double.doubleToLongBits(val);
     * longVal &circ;= (longVal &lt; 0) ? 0xffffffffffffffffL : 0x8000000000000000L;
     * </pre>
     * 
     * Based on com.sleepycat.bind.tuple (berkley db/ oracle)
     * 
     * @param val
     *            is the value to write to the buffer.
     * 
     * @return this tuple output object.
     */
    private static final String encodeSortedDouble(double val) {
        long longVal = Double.doubleToLongBits(val);
        longVal ^= (longVal < 0) ? 0xffffffffffffffffL : 0x8000000000000000L;
        longVal = (long) (longVal ^ 0x8000000000000000L);

        return encodeLong(longVal);
    }

    /**
     * Decodes zero-padded positive float value from the string representation
     * 
     * com.sleepycat.bind.tuple.TupleInput
     * 
     * @param value
     *            zero-padded string representation of the float
     * @return original float value
     */
    private static Float decodeSortedFloat(String value) {

        int val = decodeInt(value);
        val ^= (val < 0) ? 0x80000000 : 0xffffffff;
        val = (int) (val ^ 0xffffffff);
        return Float.intBitsToFloat(val);
    }

    /**
     * Writes a signed float (four byte) value to the buffer, with support for
     * correct default sorting of all values. Writes values that can be read
     * using {@link TupleInput#readSortedFloat}.
     * 
     * <p>
     * <code>Float.floatToIntBits</code> and the following bit manipulations are
     * used to convert the signed float value to a representation that is sorted
     * correctly by default.
     * </p>
     * 
     * <pre>
     * int intVal = Float.floatToIntBits(val);
     * intVal &circ;= (intVal &lt; 0) ? 0xffffffff : 0x80000000;
     * </pre>
     * 
     * Based on com.sleepycat.bind.tuple (berkley db/ oracle)
     * 
     * @param val
     *            is the value to write to the buffer.
     * 
     * @return this tuple output object.
     */
    private static final String encodeSortedFloat(float val) {

        int intVal = Float.floatToIntBits(val);
        intVal ^= (intVal < 0) ? 0xffffffff : 0x80000000;
        intVal = (int) (intVal ^ 0x80000000);

        return encodeInt(intVal);

    }

    public static String encodeValue(Object object) {
        String value = "unknown";
        if (object instanceof Long) {
            value = encodeLong((Long) object) + LONG_TOKEN;
        } else if (object instanceof Character) {
            value = object.toString();
        } else if (object instanceof java.util.Date) {
            value = encodeDate((java.util.Date) object);
        } else if (object instanceof Float) {
            value = encodeSortedFloat((Float) object) + FLOAT_TOKEN;
        } else if (object instanceof Double) {
            value = encodeSortedDouble((Double) object) + DOUBLE_TOKEN;
        } else if (object instanceof Integer) {
            value = encodeInt((Integer) object) + INTEGER_TOKEN;
        } else if (object instanceof Short) {
            value = encodeShort((Short) object) + SHORT_TOKEN;
        } else if (object instanceof String) {
            value = (String) object;
        }

        return value;
    }

    // U+0110 c4 90 LATIN CAPITAL LETTER D WITH STROKE
    private static final char DOUBLE_TOKEN = '\u0110';

    // U+0141 c5 81 LATIN CAPITAL LETTER L WITH STROKE
    private static final char LONG_TOKEN = '\u0141';

    // U+015A c5 9a LATIN CAPITAL LETTER S WITH ACUTE
    private static final char SHORT_TOKEN = '\u015A';

    // U+0197 c6 97 LATIN CAPITAL LETTER I WITH STROKE
    private static final char INTEGER_TOKEN = '\u0197';

    // U+0191 c6 91 LATIN CAPITAL LETTER F WITH HOOK
    private static final char FLOAT_TOKEN = '\u0191';

    public static Object decodeValue(String object) {
        Object value = null;
        if (object.indexOf(LONG_TOKEN) == 13) {
            value = decodeLong(object.substring(0, object.length() - 1));
        } else if (object.indexOf(DOUBLE_TOKEN) == 13) {
            value =
                    decodeSortedDouble(object.substring(0, object.length() - 1));
        } else if (object.indexOf(FLOAT_TOKEN) == 7) {
            value = decodeSortedFloat(object.substring(0, object.length() - 1));
        } else if (object.indexOf(INTEGER_TOKEN) == 7) {
            value = decodeInt(object.substring(0, object.length() - 1));
        } else if (object.indexOf(SHORT_TOKEN) == 4) {
            value = decodeShort(object.substring(0, object.length() - 1));
        } // parse date for known tokens
        else if (object.indexOf('-') == 4 && object.indexOf('-', 5) == 7
                && object.indexOf('T') == 10 && object.indexOf(':') == 13
                && object.indexOf(':', 14) == 16 && object.indexOf('.') == 19
                && object.indexOf('+') == 23) {

            try {
                value = decodeDate(object);
            } catch (ParseException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else {
            value = (String) object;
        }

        return value;
    }

    /**
     * static value hard-coding date format used for conversation of Date into
     * Stobject
     */
    private static String dateFormat = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";

    /**
     * Encodes date value into string format that can be compared
     * lexicographically
     * 
     * com.xerox.amazonws.sdb.DataUtils
     * 
     * @param date
     *            date value to be encoded
     * @return string representation of the date value
     */
    private static String encodeDate(Date date) {
        SimpleDateFormat dateFormatter = new SimpleDateFormat(dateFormat);
        /* Java doesn't handle ISO8601 nicely: need to add ':' manually */
        String result = dateFormatter.format(date);
        return result.substring(0, result.length() - 2) + ":"
                + result.substring(result.length() - 2);
    }

    public static Object getStringOrNumber(String number) {
        boolean hasOnlyDigits = NumberUtils.isDigits(number);
        if (hasOnlyDigits) {

            return NumberUtils.createInteger(number);

        } else if (NumberUtils.isNumber(number)) {
            if (SimpleDBParser.indexOfIgnoreCase(number, "l") == number
                    .length() - 1) {
                return NumberUtils.createLong(number.substring(0, number
                        .length() - 1));
            } else if (SimpleDBParser.indexOfIgnoreCase(number, "d") == number
                    .length() - 1) {
                return NumberUtils.createDouble(number);
            } else {
                return NumberUtils.createFloat(number);
            }

        }
        return ExtendedFunctions.trimCharacter(ExtendedFunctions.trimCharacter(
                number, '\''), '"');
    }

    /**
     * Decodes date value from the string representation created using
     * encodeDate(..) function.
     * 
     * com.xerox.amazonws.sdb.DataUtils
     * 
     * @param value
     *            string representation of the date value
     * @return original date value
     */
    private static Date decodeDate(String value) throws ParseException {
        String javaValue =
                value.substring(0, value.length() - 3)
                        + value.substring(value.length() - 2);
        SimpleDateFormat dateFormatter = new SimpleDateFormat(dateFormat);
        return dateFormatter.parse(javaValue);
    }
}
