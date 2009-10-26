/*
 * Copyright 2000,2005 wingS development team.
 *
 * This file is part of wingS (http://wingsframework.org).
 *
 * wingS is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 *
 * Please see COPYING for the complete licence.
 */
package ac.elements.parser;

import java.math.BigInteger;
import java.util.StringTokenizer;

/**
 * Some string manipulation utilities.
 * 
 * @author <a href="mailto:haaf@mercatis.de">Armin Haaf</a>
 */
@SuppressWarnings("unused")
public class EncodeRadix {

    /**
     * All possible digits for representing a number as a String
     */
    private final static char[] DIGITS =
            { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c',
                    'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o',
                    'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', 'A',
                    'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M',
                    'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y',
                    'Z', '-', '_', '0', '1', '2', '3', '4', '5', '6', '7', '8',
                    '9', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k',
                    'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w',
                    'x', 'y', 'z', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I',
                    'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U',
                    'V', 'W', 'X', 'Y', 'Z', '-', '_', '0', '1', '2', '3', '4',
                    '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f', 'g',
                    'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's',
                    't', 'u', 'v', 'w', 'x', 'y', 'z', 'A', 'B', 'C', 'D', 'E',
                    'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q',
                    'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', '-', '_', '0',
                    '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c',
                    'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o',
                    'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', 'A',
                    'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M',
                    'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y',
                    'Z', '-', '_'

            };

    public static final int RADIX = DIGITS.length;

    /**
     * Codes number up to radix 64.
     * 
     * @param minDigits
     *            returns a string with a least minDigits digits
     */
    public static String toString(Object value, int radix, int minDigits) {
        char[] buf = new char[65];

        BigInteger i = new BigInteger(value.toString());

        radix = Math.min(Math.abs(radix), RADIX);
        minDigits = Math.min(buf.length - 1, Math.abs(minDigits));

        int charPos = buf.length - 1;

        boolean negative = (i.signum() == -1);
        if (negative) {
            i = i.negate();
        }

        BigInteger bigIntegerRadix = BigInteger.valueOf(radix);
        while (i.compareTo(bigIntegerRadix) == 1) {
            buf[charPos--] = DIGITS[i.mod(bigIntegerRadix).intValue()];
            i = i.divide(bigIntegerRadix);
        }
        buf[charPos] = DIGITS[i.intValue()];

        // if minimum length of the result string is set, pad it with the
        // zero-representation (that is: '0')
        while (charPos > buf.length - minDigits)
            buf[--charPos] = DIGITS[0];

        if (negative) {
            buf[--charPos] = '-';
        }

        return new String(buf, charPos, buf.length - charPos);
    }

    private static String encodeLong(long number) {
        int maxNumDigits =
                BigInteger.valueOf(Long.MAX_VALUE).subtract(
                        BigInteger.valueOf(Long.MIN_VALUE)).toString(36)
                        .length();
        long offsetValue = Long.MIN_VALUE;

        BigInteger offsetNumber = BigInteger.valueOf(number);

        String longString = offsetNumber.toString(36);
        int numZeroes = maxNumDigits - longString.length();
        StringBuffer strBuffer =
                new StringBuffer(numZeroes + longString.length());
        for (int i = 0; i < numZeroes; i++) {
            strBuffer.insert(i, '0');
        }
        strBuffer.append(longString);
        return strBuffer.toString();
    }

    public static void main(String args[]) {
        BigInteger digits =
                BigInteger.valueOf(Long.MAX_VALUE).subtract(
                        BigInteger.valueOf(Long.MIN_VALUE));
        // System.out.println(EncodeRadix.toString(digits, 64, 0).length());
        System.out.println(EncodeRadix.encodeLong(9124l));
        System.out.println(EncodeRadix.toString(9124l, 256, EncodeRadix
                .toString(digits, 128, 0).length()));
    }
}
