/**
 * 
 */
package ac.elements.conf.test;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.TreeSet;

import junit.framework.TestCase;
import ac.elements.parser.SimpleDBConverter;

/**
 * The Class SimpleDBCollectionTest. Test for converting string types to ecoded
 * types for amazons simpleDB. Sorting order is also tested see amazon's
 * documentation .
 */
@SuppressWarnings("unchecked")
public class SimpleDBConverterTest extends TestCase {

    ArrayList<Float> floats = new ArrayList<Float>();

    ArrayList<Double> doubles = new ArrayList<Double>();

    ArrayList<Integer> integers = new ArrayList<Integer>();

    ArrayList<Long> longs = new ArrayList<Long>();

    ArrayList<Short> shorts = new ArrayList<Short>();

    ArrayList<Date> dates = new ArrayList<Date>();

    ArrayList<String> strings = new ArrayList<String>();

    /*
     * (non-Javadoc)
     * 
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
        for (int i = 0; i < 1000; i++) {
            double aDouble = (Math.random() - 0.5);
            doubles.add(aDouble * Double.MAX_VALUE);
            floats.add((float) (aDouble * (double) Float.MAX_VALUE));
            integers.add((int) (aDouble * (double) Integer.MAX_VALUE));
            longs.add((long) (aDouble * (double) Long.MAX_VALUE));
            shorts.add((short) (aDouble * (double) Short.MAX_VALUE));
            long ms = System.currentTimeMillis();
            dates.add(new Date((long) (ms * aDouble)));
            strings.add("" + aDouble);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see junit.framework.TestCase#tearDown()
     */
    protected void tearDown() throws Exception {
        super.tearDown();
        doubles = null;
        floats = null;
        integers = null;
        longs = null;
        shorts = null;
        dates = null;
    }

    /**
     * Test int.
     */
    public void testInteger() {
        // String result = exampleDB.batchPutAttributes("test", maps);
        //
        // assertTrue("batchPutAttributes returns xml", result
        // .indexOf("BatchPutAttributesResponse") != -1);
        TreeSet order1 = new TreeSet();
        TreeSet order2 = new TreeSet();
        ArrayList list1 = new ArrayList();
        ArrayList list2 = new ArrayList();

        for (int i = 0; i < integers.size(); i++) {
            order1.add(SimpleDBConverter.encodeValue(integers.get(i)));
        }
        for (int i = 0; i < integers.size(); i++) {
            order2.add(integers.get(i));
        }
        for (Iterator items = order1.iterator(); items.hasNext();) {
            Object item = items.next();
            list1.add(item);
        }
        for (Iterator items = order2.iterator(); items.hasNext();) {
            Object item = items.next();
            list2.add(item);
        }
        for (int i = 0; i < list1.size(); i++) {
            int one =
                    (Integer) SimpleDBConverter.decodeValue((String) list1
                            .get(i));
            int two = (Integer) list2.get(i);
            assertTrue("integers are sorted ", one == two);
        }
    }

    /**
     * Test long.
     */
    public void testLong() {
        // String result = exampleDB.batchPutAttributes("test", maps);
        //
        // assertTrue("batchPutAttributes returns xml", result
        // .indexOf("BatchPutAttributesResponse") != -1);
        TreeSet order1 = new TreeSet();
        TreeSet order2 = new TreeSet();
        ArrayList list1 = new ArrayList();
        ArrayList list2 = new ArrayList();

        for (int i = 0; i < longs.size(); i++) {
            order1.add(SimpleDBConverter.encodeValue(longs.get(i)));
        }
        for (int i = 0; i < longs.size(); i++) {
            order2.add(longs.get(i));
        }
        for (Iterator items = order1.iterator(); items.hasNext();) {
            Object item = items.next();
            list1.add(item);
        }
        for (Iterator items = order2.iterator(); items.hasNext();) {
            Object item = items.next();
            list2.add(item);
        }
        for (int i = 0; i < list1.size(); i++) {
            long one =
                    (Long) SimpleDBConverter.decodeValue((String) list1.get(i));
            long two = (Long) list2.get(i);
            assertTrue("longs are sorted ", one == two);
        }
    }

    /**
     * Test short.
     */
    public void testShort() {
        // String result = exampleDB.batchPutAttributes("test", maps);
        //
        // assertTrue("batchPutAttributes returns xml", result
        // .indexOf("BatchPutAttributesResponse") != -1);
        TreeSet order1 = new TreeSet();
        TreeSet order2 = new TreeSet();
        ArrayList list1 = new ArrayList();
        ArrayList list2 = new ArrayList();

        for (int i = 0; i < shorts.size(); i++) {
            order1.add(SimpleDBConverter.encodeValue(shorts.get(i)));
        }
        for (int i = 0; i < shorts.size(); i++) {
            order2.add(shorts.get(i));
        }
        for (Iterator items = order1.iterator(); items.hasNext();) {
            Object item = items.next();
            list1.add(item);
        }
        for (Iterator items = order2.iterator(); items.hasNext();) {
            Object item = items.next();
            list2.add(item);
        }
        for (int i = 0; i < list1.size(); i++) {
            short one =
                    (Short) SimpleDBConverter
                            .decodeValue((String) list1.get(i));
            short two = (Short) list2.get(i);
            assertTrue("shorts are sorted ", one == two);
        }
    }

    /**
     * Test float.
     */
    public void testFloat() {
        // String result = exampleDB.batchPutAttributes("test", maps);
        //
        // assertTrue("batchPutAttributes returns xml", result
        // .indexOf("BatchPutAttributesResponse") != -1);
        TreeSet order1 = new TreeSet();
        TreeSet order2 = new TreeSet();
        ArrayList list1 = new ArrayList();
        ArrayList list2 = new ArrayList();

        for (int i = 0; i < floats.size(); i++) {
            order1.add(SimpleDBConverter.encodeValue(floats.get(i)));
        }
        for (int i = 0; i < floats.size(); i++) {
            order2.add(floats.get(i));
        }
        for (Iterator items = order1.iterator(); items.hasNext();) {
            Object item = items.next();
            list1.add(item);
        }
        for (Iterator items = order2.iterator(); items.hasNext();) {
            Object item = items.next();
            list2.add(item);
        }
        for (int i = 0; i < list1.size(); i++) {
            float one =
                    (Float) SimpleDBConverter
                            .decodeValue((String) list1.get(i));
            float two = (Float) list2.get(i);
            assertTrue("float are sorted ", one == two);
        }
    }

    /**
     * Test double.
     */
    public void testDouble() {
        // String result = exampleDB.batchPutAttributes("test", maps);
        //
        // assertTrue("batchPutAttributes returns xml", result
        // .indexOf("BatchPutAttributesResponse") != -1);
        TreeSet order1 = new TreeSet();
        TreeSet order2 = new TreeSet();
        ArrayList list1 = new ArrayList();
        ArrayList list2 = new ArrayList();

        for (int i = 0; i < doubles.size(); i++) {
            order1.add(SimpleDBConverter.encodeValue(doubles.get(i)));
        }
        for (int i = 0; i < doubles.size(); i++) {
            order2.add(doubles.get(i));
        }
        for (Iterator items = order1.iterator(); items.hasNext();) {
            Object item = items.next();
            list1.add(item);
        }
        for (Iterator items = order2.iterator(); items.hasNext();) {
            Object item = items.next();
            list2.add(item);
        }
        for (int i = 0; i < list1.size(); i++) {
            double one =
                    (Double) SimpleDBConverter.decodeValue((String) list1
                            .get(i));
            double two = (Double) list2.get(i);
            assertTrue("doubles are sorted ", one == two);
        }
    }

    /**
     * Test date.
     */
    public void testDate() {
        // String result = exampleDB.batchPutAttributes("test", maps);
        //
        // assertTrue("batchPutAttributes returns xml", result
        // .indexOf("BatchPutAttributesResponse") != -1);
        TreeSet order1 = new TreeSet();
        TreeSet order2 = new TreeSet();
        ArrayList list1 = new ArrayList();
        ArrayList list2 = new ArrayList();

        for (int i = 0; i < dates.size(); i++) {
            order1.add(SimpleDBConverter.encodeValue(dates.get(i)));
        }
        for (int i = 0; i < dates.size(); i++) {
            order2.add(dates.get(i));
        }
        for (Iterator items = order1.iterator(); items.hasNext();) {
            Object item = items.next();
            list1.add(item);
        }
        for (Iterator items = order2.iterator(); items.hasNext();) {
            Object item = items.next();
            list2.add(item);
        }
        for (int i = 0; i < list1.size(); i++) {
            Date one =
                    (Date) SimpleDBConverter.decodeValue((String) list1.get(i));
            Date two = (Date) list2.get(i);
            assertTrue("dates are sorted ", one.equals(two));
        }
    }

    /**
     * Test string.
     */
    public void testString() {
        // String result = exampleDB.batchPutAttributes("test", maps);
        //
        // assertTrue("batchPutAttributes returns xml", result
        // .indexOf("BatchPutAttributesResponse") != -1);
        TreeSet order1 = new TreeSet();
        TreeSet order2 = new TreeSet();
        ArrayList list1 = new ArrayList();
        ArrayList list2 = new ArrayList();

        for (int i = 0; i < strings.size(); i++) {
            order1.add(SimpleDBConverter.encodeValue(strings.get(i)));
        }
        for (int i = 0; i < strings.size(); i++) {
            order2.add(strings.get(i));
        }
        for (Iterator items = order1.iterator(); items.hasNext();) {
            Object item = items.next();
            list1.add(item);
        }
        for (Iterator items = order2.iterator(); items.hasNext();) {
            Object item = items.next();
            list2.add(item);
        }
        for (int i = 0; i < list1.size(); i++) {
            String one =
                    (String) SimpleDBConverter.decodeValue((String) list1
                            .get(i));
            String two = (String) list2.get(i);
            assertTrue("strings are sorted ", one.equals(two));
        }
    }
}