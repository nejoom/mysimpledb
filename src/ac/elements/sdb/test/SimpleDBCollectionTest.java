/**
 * 
 */
package ac.elements.sdb.test;

import java.sql.Date;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import ac.elements.conf.Configuration;
import ac.elements.sdb.SimpleDBCollection;
import ac.elements.sdb.SimpleDBDataList;
import ac.elements.sdb.SimpleDBMap;

/**
 * The Class SimpleDBCollectionTest.
 */
public class SimpleDBCollectionTest extends TestCase {

    // private final Log log = LogFactory.getLog(SimpleDB.class);

    /** The example db. */
    private SimpleDBCollection exampleDB;

    /** The maps. */
    private SimpleDBDataList maps;

    /** The large map. */
    SimpleDBMap largeMap = new SimpleDBMap();

    /** The small map. */
    SimpleDBMap smallMap = new SimpleDBMap();

    /** The empty map. */
    SimpleDBMap singleMap = new SimpleDBMap();

    /*
     * The Access Key ID is associated with your AWS account. You include it in
     * AWS service requests to identify yourself as the sender of the request.
     * 
     * The Access Key ID is not a secret, and anyone could use your Access Key
     * ID in requests to AWS.
     */

    String accessKeyId =
            Configuration.getInstance().getValue("aws", "AWSAccessKeyId");

    /*
     * To provide proof that you truly are the sender of the request, you also
     * include a digital signature calculated using your Secret Access Key.
     */

    String secretAccessKey =
            Configuration.getInstance().getValue("aws", "SecretAccessKey");

    /**
     * Suite.
     * 
     * @return the test
     */
    public static Test suite() {
        return new TestSuite(SimpleDBCollectionTest.class);
    }

    /*
     * (non-Javadoc)
     * 
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
        exampleDB = new SimpleDBCollection(accessKeyId, secretAccessKey);

        maps = new SimpleDBDataList();

        largeMap
                .put(
                        "oneKey",
                        "oneValue!@#$%^&*()/\"<>+_-\u0441\u0442\u0430\u0442\u0438\u0441\u0442\u0438\u043A\u0430");
        largeMap.put("oneKey1", new Float(12.123123f));
        largeMap.put("oneKey2", new Double(123.12312d));
        largeMap.put("oneKey3", new Long(12312312l));
        largeMap.put("oneKey4", 123123);
        largeMap.put("oneKey5", 0.0001);
        largeMap.put("oneKey6", new Date(123));

        smallMap.put("oneKey2", "oneValue2");
        singleMap.put("oneKey2", "deleteMe");
        largeMap.setItemName("1");
        maps.add(new SimpleDBMap(largeMap));
        smallMap.setItemName("2");
        maps.add(new SimpleDBMap(smallMap));
        largeMap.setItemName("3");
        maps.add(new SimpleDBMap(largeMap));
        largeMap.setItemName("4");
        maps.add(new SimpleDBMap(largeMap));
        largeMap.setItemName("5");
        maps.add(new SimpleDBMap(largeMap));
        largeMap.setItemName("6");
        maps.add(new SimpleDBMap(largeMap));
        singleMap.setItemName("7");
        maps.add(new SimpleDBMap(singleMap));
        singleMap.setItemName(8d);
        maps.add(new SimpleDBMap(singleMap));
    }

    /*
     * (non-Javadoc)
     * 
     * @see junit.framework.TestCase#tearDown()
     */
    protected void tearDown() throws Exception {
        super.tearDown();
        exampleDB = null;
    }

    /**
     * Test batch put attributes.
     */
    public void testBatchPutAttributes() {
        maps.setDomainName("test");
        String result = exampleDB.batchPutAttributes(maps);

        assertTrue("batchPutAttributes returns xml", result
                .indexOf("BatchPutAttributesResponse") != -1);

    }

    /**
     * Test create domain.
     */
    public void testCreateDomain() {
        String result = exampleDB.createDomain("test");
        assertTrue("createDomain returns xml", result
                .indexOf("CreateDomainResponse") != -1);
    }

    /**
     * Test delete attributes.
     */
    public void testDeleteAttributes() {
        String result =
                exampleDB.deleteAttributes("test", smallMap);
        assertTrue("deleteAttributes returns xml", result
                .indexOf("DeleteAttributesResponse") != -1);
    }

    /**
     * Test delete domain.
     */
    public void testDeleteDomain() {
        String result = exampleDB.deleteDomain("test1");
        assertTrue("deleteDomain returns xml", result
                .indexOf("DeleteDomainResponse") != -1);
    }

    /**
     * Test delete item.
     */
    public void testDeleteItem() {
        String result = exampleDB.deleteItem("test", "second item");

        assertTrue("deleteItem returns xml", result
                .indexOf("DeleteAttributesResponse") != -1);
    }

    /**
     * Test domain metadata.
     */
    public void testDomainMetadata() {
        String result = exampleDB.domainMetadata("test");
        System.out.println(result);
        assertTrue("domainMetadata returns xml", result
                .indexOf("DomainMetadataResponse") != -1);
    }

    /**
     * Test get attributes.
     */
    public void testGetAttributes() {
        String result = exampleDB.getAttributes("test", "second item");

        assertTrue("getAttributes returns xml", result
                .indexOf("GetAttributesResponse") != -1);
    }

    /**
     * Test list domains.
     */
    public void testListDomains() {
        String result = exampleDB.listDomains();
        assertTrue("ListDomains returns xml", result
                .indexOf("ListDomainsResponse") != -1);
    }

    /**
     * Test put attributes.
     */
    public void testPutAttributes() {
        String result = exampleDB.putAttributes("test", "fifth", largeMap);

        assertTrue("PutAttributes returns xml", result
                .indexOf("PutAttributesResponse") != -1);
    }

    /**
     * Test query.
     */
    public void testQuery() {
        String result = exampleDB.query("test", "", null, null);
        assertTrue("Query returns xml", result.indexOf("QueryResponse") != -1);

        result = exampleDB.query("test", null, null, null);
        assertTrue("Query returns xml", result.indexOf("QueryResponse") != -1);
        result =
                exampleDB
                        .query("test", "['oneKey2' = 'oneValue2']", null, null);
        assertTrue("Query returns xml", result.indexOf("QueryResponse") != -1);
    }

    /**
     * Test query with attributes.
     */
    public void testQueryWithAttributes() {
        String result = exampleDB.queryWithAttributes("test", "", null, null);
        assertTrue("QueryWithAttributes returns xml", result
                .indexOf("QueryWithAttributesResponse") != -1);

        result = exampleDB.queryWithAttributes("test", null, null, null);
        assertTrue("QueryWithAttributes returns xml", result
                .indexOf("QueryWithAttributesResponse") != -1);
        result =
                exampleDB.queryWithAttributes("test",
                        "['oneKey2' = 'oneValue2']", null, null);
        assertTrue("QueryWithAttributes returns xml", result
                .indexOf("QueryWithAttributesResponse") != -1);

    }

    /**
     * Test select.
     */
    public void testSelect() {
        String result = exampleDB.select("select * from test", null);
        System.out.println("select: " + exampleDB.getSelect("select * from test", null));

        assertTrue("Select returns xml", result.indexOf("SelectResponse") != -1);
        assertTrue("Testing unicode xml", result.indexOf("\u0441") != -1);
    }

    /**
     * Test select.
     */
    public void testSetDelete() {
        SimpleDBDataList result =
                exampleDB
                        .setDelete("delete from test where oneKey2='deleteMe'");

        assertTrue("Delete returns deleted items", result.size() == 1);
    }
}
