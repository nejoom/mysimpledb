package ac.elements.conversion;

import java.math.BigInteger;
import java.util.Date;
import java.util.UUID;

public class IdGen {

    private static final int RADIX = 16; //hex
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
    
    public static void main(String[] args) {
        

        String uuid = UUID.randomUUID().toString();

        System.out.println(new Date(Long.MAX_VALUE));
        System.out.println(System.currentTimeMillis());
        System.out.println(Long.MAX_VALUE/4096);
        String longHex = encodeLong(System.currentTimeMillis());
        String longHex2 = encodeLong(Long.MAX_VALUE);
        String longHex3 = encodeLong(-10000000);
        System.out.println(System.currentTimeMillis());
        System.out.println(longHex);
        System.out.println(longHex2);
        System.out.println(longHex3);
        System.out.println(uuid);
    }
}
