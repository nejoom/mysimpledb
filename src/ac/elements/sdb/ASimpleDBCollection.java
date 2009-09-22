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
package ac.elements.sdb;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;
import java.util.UUID;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ac.elements.conversion.TypeConverter;
import ac.elements.parser.SimpleDBConverter;
import ac.elements.parser.SimpleDBParser;

/**
 * The Class SimpleDBCollection.
 */
public abstract class ASimpleDBCollection extends SimpleDB {

    /** The Constant log. */
    private final static Log log = LogFactory.getLog(ASimpleDBCollection.class);

    /**
     * Instantiates a new simple db collection.
     * 
     * @param id
     *            the id
     * @param key
     *            the key
     */
    public ASimpleDBCollection(String id, String key) {
        super(id, key);
    }

    /**
     * Returns all of the attributes associated with the item as a map.
     * 
     * @param domain
     *            the domain
     * @param item
     *            the item
     * 
     * @return the attributes as a map
     */
    public SimpleDBMap getAttributesAsMap(final String domain, final String item) {

        String result = getAttributes(domain, item);

        SimpleDBMap map = new SimpleDBMap();
        map.setItemName(item);
        List<String> attributes = getElementsAsList(result, "Attribute");
        for (int x = 0; x < attributes.size(); x++) {
            String t = attributes.get(x).toString();

            String key =
                    t.substring(t.indexOf("<Name>") + 6, t.indexOf("</Name>"));
            String val =
                    t
                            .substring(t.indexOf("<Value>") + 7, t
                                    .indexOf("</Value>"));
            map.put(key, SimpleDBConverter.decodeValue(val));
        }

        return map;
    }

    /**
     * Gets the domains as a list.
     * 
     * @return the domains as a list of strings
     */
    public SimpleDBDataList getDomainsAsList() {

        long t0 = System.currentTimeMillis();

        SimpleDBDataList list = new SimpleDBDataList();

        String result = listDomains(null, null);
        List<String> domains = getElementsAsList(result, "DomainName");
        for (int x = 0; x < domains.size(); x++) {
            String t = domains.get(x).toString();

            SimpleDBMap sdbmap = new SimpleDBMap();
            sdbmap.setItemName(t);

            list.add(sdbmap);
        }
        List<String> responseToken = getElementsAsList(result, "NextToken");
        if (responseToken != null && responseToken.size() > 0) {
            String t = responseToken.get(0).toString();
            list.setNextToken(t);
        }
        responseToken = getElementsAsList(result, "RequestId");
        if (responseToken != null && responseToken.size() > 0) {
            String t = responseToken.get(0).toString();
            list.setRequestId(t);
        }
        responseToken = getElementsAsList(result, "BoxUsage");
        if (responseToken != null && responseToken.size() > 0) {
            String t = responseToken.get(0).toString();
            list.setBoxUsage(t);
        }
        long responseTime = System.currentTimeMillis() - t0;
        list.setResponseTime(responseTime);
        return list;
    }

    /**
     * Gets the domains as a list.
     * 
     * @param maxNumberOfDomains
     *            the maximum number of domain names you want returned. The
     *            range is 1 to 100. The default setting is 100.
     * @param nextToken
     *            the next token, string that tells Amazon SimpleDB where to
     *            start the list of domain names.
     * @param previousToken
     *            the previousToken, string that tells Amazon SimpleDB where to
     *            start the previous list of domain names.
     * 
     * @return the domains as a list of strings
     */
    public SimpleDBDataList getDomainsAsList(final String maxNumberOfDomains,
            final String nextToken) {

        long t0 = System.currentTimeMillis();
        SimpleDBDataList list = new SimpleDBDataList();
        String result = listDomains(maxNumberOfDomains, nextToken);
        List<String> domains = getElementsAsList(result, "DomainName");
        for (int x = 0; x < domains.size(); x++) {
            String t = domains.get(x).toString();

            SimpleDBMap sdbmap = new SimpleDBMap();
            sdbmap.setItemName(t);

            list.add(sdbmap);
        }
        List<String> responseToken = getElementsAsList(result, "NextToken");
        if (responseToken != null && responseToken.size() > 0) {
            String t = responseToken.get(0).toString();
            list.setNextToken(t);
        }
        responseToken = getElementsAsList(result, "RequestId");
        if (responseToken != null && responseToken.size() > 0) {
            String t = responseToken.get(0).toString();
            list.setRequestId(t);
        }
        responseToken = getElementsAsList(result, "BoxUsage");
        if (responseToken != null && responseToken.size() > 0) {
            String t = responseToken.get(0).toString();
            list.setBoxUsage(t);
        }
        long responseTime = System.currentTimeMillis() - t0;
        list.setResponseTime(responseTime);
        return list;
    }

    /**
     * Gets the elements in the xml response as a list.
     * 
     * @param xmlResponse
     *            the xml response as a string
     * @param element
     *            the element to get as a list
     * 
     * @return the element specified as a list
     */
    private List<String> getElementsAsList(String xmlResponse, String element) {
        ArrayList<String> list = new ArrayList<String>();

        String sTag = "<" + element + ">";
        String eTag = "</" + element + ">";

        int c1 = xmlResponse.indexOf(sTag);
        int c2 = xmlResponse.indexOf(eTag);
        while (c1 != -1 && c2 != -1) {
            list.add(xmlResponse.substring(c1 + sTag.length(), c2));
            c1 = xmlResponse.indexOf(sTag, c2);
            if (c1 != -1)
                c2 = xmlResponse.indexOf(eTag, c1);
        }
        return list;
    }

    /**
     * Gets the itemNames of a domain as a SimpleDBDataList.
     * 
     * @param domain
     *            the domain
     * 
     * @return the itemNames as a SimpleDBDataList
     */
    public SimpleDBDataList getItemsAsList(String domain) {

        long t0 = System.currentTimeMillis();
        SimpleDBDataList list = new SimpleDBDataList();
        String result = listItems(domain);
        List<String> domains = getElementsAsList(result, "ItemName");
        for (int x = 0; x < domains.size(); x++) {
            String t = domains.get(x).toString();

            SimpleDBMap sdbmap = new SimpleDBMap();
            sdbmap.setItemName(t);

            list.add(sdbmap);
        }

        List<String> responseToken = getElementsAsList(result, "NextToken");
        if (responseToken != null && responseToken.size() > 0) {
            String t = responseToken.get(0).toString();
            list.setNextToken(t);
        }
        responseToken = getElementsAsList(result, "RequestId");
        if (responseToken != null && responseToken.size() > 0) {
            String t = responseToken.get(0).toString();
            list.setRequestId(t);
        }
        responseToken = getElementsAsList(result, "BoxUsage");
        if (responseToken != null && responseToken.size() > 0) {
            String t = responseToken.get(0).toString();
            list.setBoxUsage(t);
        }
        long responseTime = System.currentTimeMillis() - t0;
        list.setResponseTime(responseTime);
        return list;
    }

    /**
     * Returns information about the domain, including when the domain was
     * created, the number of items and attributes, and the size of attribute
     * names and values.
     * 
     * @param domain
     *            The name of the domain for which to display metadata.
     * 
     * @return the xml response as a HashMap
     */
    public TreeMap<String, String> getMetaDataAsMap(final String domain) {

        String result = domainMetadata(domain);

        TreeMap<String, String> map = new TreeMap<String, String>();
        try {
            map.put("ItemCount", getElementsAsList(result, "ItemCount").get(0));
            map.put("ItemNamesSizeBytes", getElementsAsList(result,
                    "ItemNamesSizeBytes").get(0));
            map.put("AttributeNameCount", getElementsAsList(result,
                    "AttributeNameCount").get(0));
            map.put("AttributeNamesSizeBytes", getElementsAsList(result,
                    "AttributeNamesSizeBytes").get(0));
            map.put("AttributeValueCount", getElementsAsList(result,
                    "AttributeValueCount").get(0));
            map.put("AttributeValuesSizeBytes", getElementsAsList(result,
                    "AttributeValuesSizeBytes").get(0));
            Long timestamp =
                    new Long(getElementsAsList(result, "Timestamp").get(0));
            map.put("Timestamp", (new Date(timestamp.longValue() * 1000))
                    .toString());
            map.put("BoxUsage", getElementsAsList(result, "BoxUsage").get(0));
        } catch (Exception e) {
            System.out.println("What to do for " + domain + "?");
            map.put("ItemCount", "Unknown");
            map.put("ItemNamesSizeBytes", "Unknown");
            map.put("AttributeNameCount", "Unknown");
            map.put("AttributeNamesSizeBytes", "Unknown");
            map.put("AttributeValueCount", "Unknown");
            map.put("AttributeValuesSizeBytes", "Unknown");
            map.put("Timestamp", "Unknown");
            map.put("BoxUsage", "Unknown");
            e.printStackTrace();
        }
        return map;
    }

    /**
     * Returns data from a select statement as a SimpleDBDataList object.
     * 
     * @param selectExpression
     *            the select expression
     * @param nextToken
     *            the next token
     * 
     * @return the xml response as a SimpleDBDataList
     */
    public SimpleDBDataList getSelect(String selectExpression,
            final String nextToken) {

        long t0 = System.currentTimeMillis();
        selectExpression = ExtendedFunctions.trimSentence(selectExpression);
        log.debug(selectExpression);
        String result = select(selectExpression, nextToken);
        log.debug(result);
        SimpleDBDataList list = new SimpleDBDataList();

        // extract domain
        String domain = SimpleDBParser.getDomain(selectExpression);
        list.setDomainName(domain);
        log.debug(domain);
        List<String> items = getElementsAsList(result, "Item");
        log.debug(items.size());
        for (int x = 0; x < items.size(); x++) {
            String t = items.get(x).toString();

            String item =
                    t.substring(t.indexOf("<Name>") + "<Name>".length(), t
                            .indexOf("</Name>"));

            SimpleDBMap sdbmap = new SimpleDBMap();
            sdbmap.setItemName(item);

            List<String> attributes = getElementsAsList(t, "Attribute");
            for (int x1 = 0; x1 < attributes.size(); x1++) {
                String t1 = attributes.get(x1).toString();

                String key =
                        t1.substring(t1.indexOf("<Name>") + 6, t1
                                .indexOf("</Name>"));
                String val =
                        t1.substring(t1.indexOf("<Value>") + 7, t1
                                .indexOf("</Value>"));
                sdbmap.put(key, SimpleDBConverter.decodeValue(val));
            }
            list.add(sdbmap);
        }
        List<String> responseToken = getElementsAsList(result, "NextToken");
        if (responseToken != null && responseToken.size() > 0) {
            String t = responseToken.get(0).toString();
            list.setNextToken(t);
        }
        responseToken = getElementsAsList(result, "RequestId");
        if (responseToken != null && responseToken.size() > 0) {
            String t = responseToken.get(0).toString();
            list.setRequestId(t);
        }
        responseToken = getElementsAsList(result, "BoxUsage");
        if (responseToken != null && responseToken.size() > 0) {
            String t = responseToken.get(0).toString();
            list.setBoxUsage(t);
        }

        long responseTime = System.currentTimeMillis() - t0;
        list.setResponseTime(responseTime);
        return list;

    }

    /**
     * The Delete operation is a custom utility method that first selects a set
     * of ItemNames that match the select query expression, and subsequently
     * deletes this set with the deleteItem method. Delete is similar to the
     * standard SQL DELETE statement.
     * 
     * <code>
     * delete from test where onekey='avalue'
     * </code>
     * 
     * @param deleteExpression
     *            the delete expression used to query the domain for itemNames.
     * 
     * @return a SimpleDBDataList of itemNames deleted
     */
    public SimpleDBDataList setDelete(final String deleteExpression) {

        long t0 = System.currentTimeMillis();
        log.trace("Entering delete");

        // modify expression to first perform select
        if (deleteExpression.trim().toLowerCase().indexOf("delete") != 0) {
            throw new RuntimeException("Illegal deleteExpression: "
                    + deleteExpression + ", index 0 !="
                    + deleteExpression.trim().toLowerCase().indexOf("delete"));
        }
        String selectExpression =
                "SELECT itemName() "
                        + deleteExpression.substring("delete".length()).trim();
        log.trace("selectExpression: " + selectExpression);
        String nextToken = null;
        SimpleDBDataList sdbList;
        ArrayList<Object> itemNames = new ArrayList<Object>();

        // does the delete statement have a limit clause?
        int limit =
                TypeConverter.getInt(SimpleDBParser
                        .getLimitClause(selectExpression), -1);
        do {
            sdbList = getSelect(selectExpression, nextToken);
            for (int i = 0; i < sdbList.size(); i++) {
                Object itemName = sdbList.get(i).getItemName();
                itemNames.add(itemName);
                deleteItem(sdbList.getDomainName(), SimpleDBConverter
                        .encodeValue(itemName));

                // should we limit the number of items deleted?
                if (limit != -1 && itemNames.size() >= limit) {
                    sdbList.setNextToken(null);
                    break;
                }
            }
            nextToken = sdbList.getNextToken();
            log.trace("nextToken: " + nextToken);
        } while (sdbList.getNextToken() != null);

        // return a select * from domain deleted attributes list
        SimpleDBDataList list = new SimpleDBDataList();
        Iterator<Object> iterator = itemNames.iterator();
        while (iterator.hasNext()) {

            Object itemName = iterator.next();
            SimpleDBMap map = new SimpleDBMap();
            map.setItemName(itemName);
            list.add(map);

        }
        long responseTime = System.currentTimeMillis() - t0;
        list.setResponseTime(responseTime);
        String domainName = SimpleDBParser.getDomain(selectExpression);
        list.setDomainName(domainName);
        return list;

    }

    /**
     * The Delete attribute operation is a custom utility method that first
     * selects a set of ItemNames that match the query expression, and
     * subsequently deletes the attributes with the deleteAttributes method.
     * Delete is a special case for deleting attributes, and resembles the
     * standard SQL DELETE statement syntax by augmenting the key attributes
     * that will be deleted.
     * 
     * <code>
     * delete (key1, `key2`) from test where onekey='avalue'
     * </code>
     * 
     * @param deleteExpression
     *            the delete expression used to query the domain for itemNames.
     * 
     * @return a SimpleDBDataList of itemNames deleted
     */
    public SimpleDBDataList setDeleteAttributeWhere(String deleteExpression) {

        long t0 = System.currentTimeMillis();
        log.trace("Entering deleteAttribute");
        deleteExpression = ExtendedFunctions.trimSentence(deleteExpression);

        // modify expression to first perform select
        if (deleteExpression.trim().toLowerCase().indexOf("delete (") != 0) {
            throw new RuntimeException("Illegal deleteExpression: "
                    + deleteExpression);
        }

        // get the key syntax without brackets
        String keySyntax =
                deleteExpression.substring(deleteExpression.indexOf("(") + 1,
                        deleteExpression.indexOf(")")).trim();
        log.error("keySyntax: " + keySyntax);

        Object[] keys = SimpleDBParser.parseList(keySyntax, "`");
        if (keys.length == 0) {
            throw new RuntimeException("Illegal deleteExpression");
        }

        String keysToSearch = "itemName()";
        boolean replaced = false;
        for (int j = 0; j < keys.length; j++) {
            if (((String) keys[j]).indexOf('=') == -1) {
                if (!replaced) {
                    keysToSearch = (String) keys[j];
                    replaced = true;
                } else {
                    keysToSearch += ", " + (String) keys[j];
                }
            }
        }
        String selectExpression =
                "SELECT "
                        + keysToSearch
                        + " "
                        + deleteExpression.substring(
                                deleteExpression.indexOf(")") + 1).trim();
        log.error("selectExpression: " + selectExpression);

        String nextToken = null;
        SimpleDBDataList sdbList;
        ArrayList<Object> itemNames = new ArrayList<Object>();

        // does the delete statement have a limit clause?
        int limit =
                TypeConverter.getInt(SimpleDBParser
                        .getLimitClause(selectExpression), -1);

        do {
            sdbList = getSelect(selectExpression, nextToken);
            SimpleDBMap sdbMap = new SimpleDBMap();

            for (int i = 0; i < sdbList.size(); i++) {
                Object itemName = sdbList.get(i).getItemName();
                itemNames.add(itemName);
                for (int j = 0; j < keys.length; j++) {
                    if (((String) keys[j]).indexOf('=') != -1) {
                        String keyValue[] = ((String) keys[j]).split("=");
                        sdbMap.put(keyValue[0], SimpleDBConverter
                                .getStringOrNumber(keyValue[1]));
                        log.error("got = " + sdbMap);
                    } else {

                        Object key = keys[j];
                        Object value = sdbList.get(i).get(key);
                        if (value == null) {
                            value = "";
                        } else {
                            // loop through value list
                            for (int k = 0; k < sdbList.get(i).get(key).size(); k++) {
                                Object valueInList =
                                        sdbList.get(i).get(key).toArray()[k];
                                sdbMap.put(key, valueInList);
                                log.error("got no = " + key + " , "
                                        + valueInList);
                            }
                        }

                    }
                }
                sdbMap.setItemName(itemName);
                deleteAttributes(sdbList.getDomainName(), sdbMap);

                // should we limit the number of items deleted?
                if (limit != -1 && itemNames.size() >= limit) {
                    sdbList.setNextToken(null);
                    break;
                }
            }
            nextToken = sdbList.getNextToken();
            log.trace("nextToken: " + nextToken);
        } while (sdbList.getNextToken() != null);

        // return a select * from domain deleted attributes list
        SimpleDBDataList list = new SimpleDBDataList();
        list.setDomainName(sdbList.getDomainName());
        Iterator<Object> iterator = itemNames.iterator();
        while (iterator.hasNext()) {

            Object itemName = iterator.next();
            SimpleDBMap map = new SimpleDBMap();
            map.setItemName(itemName);
            list.add(map);

        }
        long responseTime = System.currentTimeMillis() - t0;
        list.setResponseTime(responseTime);
        return list;

    }

    /**
     * The Delete attribute operation is a mapping to the deleteAttribute action
     * and deletes the attributes expression with the deleteAttributes method.
     * Delete is a special case of the standard SQL DELETE statement.
     * 
     * <code>
     * delete itemName(key1=1.0f, `key2`='myvalue') from domain
     * </code>
     * 
     * @param deleteExpression
     *            the delete expression used to query the domain for itemNames.
     * 
     * @return the number of itemNames deleted
     */
    public SimpleDBDataList setDeleteAttribute(String deleteExpression) {

        long t0 = System.currentTimeMillis();
        log.trace("Entering setDeleteAttribute");

        // make sure spaces are ok and we have no enters
        deleteExpression = ExtendedFunctions.trimSentence(deleteExpression);

        log.trace("Got delete expression: " + deleteExpression);
        // basic check if expression has good syntax
        if (deleteExpression.trim().toLowerCase().indexOf("delete") != 0
                && deleteExpression.trim().toLowerCase().indexOf("(") != 0
                && deleteExpression.trim().toLowerCase().indexOf(")") != 0) {
            throw new RuntimeException("Illegal deleteExpression: "
                    + deleteExpression);
        }

        String parseString = deleteExpression.substring("delete ".length());
        String domain = SimpleDBParser.getDomain(deleteExpression);

        log.error("Found domain: " + domain);

        String itemSyntax =
                parseString.substring(0,
                        parseString.toLowerCase().indexOf(" from ")).trim();

        log.error("Parsing itemSyntax: " + itemSyntax);
        ArrayList<Object> itemNames = new ArrayList<Object>();
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
        Object[] items = SimpleDBParser.parseList(itemBuffer.toString(), "`");

        if (items.length == 0) {
            throw new RuntimeException("wrong syntax, no itemName given.");
        } else {
            for (int i = 0; i < items.length; i++) {
                Object itemName = items[i];
                if (itemName == null || itemName.toString().equals("")) {
                    throw new RuntimeException("Item is null or empty");
                }

                itemNames.add(itemName);
            }
        }
        log.info("Parsed itemSyntax: " + itemNames);

        log.trace("Parsing dataSyntax: " + itemSyntax);

        StringBuffer dataBuffer = new StringBuffer(itemSyntax);
        pointer = 0;
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

        // replace ( and ) with | and then parse like its a | delimited file
        // todo make beter
        String data = dataBuffer.toString().replace('(', '|').replace(')', '|');
        Object[] valueSequence = SimpleDBParser.parseList(data, "|");

        SimpleDBMap sdbMap = new SimpleDBMap();
        if (valueSequence.length == 0 || valueSequence.length > 1) {
            throw new RuntimeException("Illegal deleteExpression in values: "
                    + deleteExpression);
        } else {
            log.error("vs: " + valueSequence[0]);
            String valueString = (String) valueSequence[0];
            Object[] values = SimpleDBParser.parseList(valueString, "");
            for (int j = 0; j < values.length; j++) {
                String[] keyValue = ((String) values[j]).split("=");
                if (keyValue.length != 2) {
                    throw new RuntimeException(
                            "Illegal deleteExpression key value pairs must "
                                    + "have syntax key=value: "
                                    + deleteExpression);
                }
                sdbMap.put(keyValue[0], SimpleDBConverter
                        .getStringOrNumber(keyValue[1]));
            }
            sdbMap.setItemName(itemNames.get(0));
        }
        deleteAttributes(domain, sdbMap);

        // return a select count(*) from domain formatted list
        SimpleDBDataList list = new SimpleDBDataList();
        list.setDomainName(domain);

        Iterator<Object> iterator = itemNames.iterator();
        while (iterator.hasNext()) {

            Object itemName = iterator.next();
            SimpleDBMap map = new SimpleDBMap();
            map.setItemName(itemName);
            list.add(map);

        }
        long responseTime = System.currentTimeMillis() - t0;
        list.setResponseTime(responseTime);
        return list;
    }

    /**
     * The Insert operation is a custom utility method that inserts a set of key
     * value pairs, by parsing the insert statement and uses the
     * batchPutAttributes method to insert the key value pairs. Unlike standard
     * SQL, the item name used is also given. If there is already an item name,
     * the key - value pair is added to the item name.
     * <p>
     * The quoting rules are the same as for the select statement.
     * <p>
     * If no itemName is given then a universally unique identifiers (UUID) is
     * generated for the item name, a guarantee for no collisions of the items
     * inserted. For the rest the insert is similar to the standard SQL INSERT
     * statement. Example strings follow (no new line characters should be
     * used):
     * 
     * <code>
     * insert into domain (`keyone`, keytwo) 
     *   values aItemName('value1a', 'value2a');
     * insert into domain (keyone, `keytwo`) 
     *   values ('value1a', 'value2a'), ('value1b', 'value2b');
     * </code>
     * 
     * @param insertExpression
     *            the insert expression used to generate key value pairs for the
     *            domain and the itemName.
     * 
     * @return a SimpleDBDataList of itemNames inserted
     */
    @SuppressWarnings("unchecked")
    public SimpleDBDataList setInsert(String insertExpression) {
        long t0 = System.currentTimeMillis();
        log.trace("Entering insert");

        // make sure spaces are ok and we have no enters
        insertExpression = ExtendedFunctions.trimSentence(insertExpression);

        log.trace("Got insert expression: " + insertExpression);
        // basic check if expression has good syntax
        if (insertExpression.trim().toLowerCase().indexOf("insert") != 0
                && insertExpression.trim().toLowerCase().indexOf("values") != 0) {
            throw new RuntimeException("Illegal insertExpression: "
                    + insertExpression);
        }

        String parseString =
                insertExpression.substring("insert into ".length());
        String domain = SimpleDBParser.getDomain(insertExpression);

        log.trace("Found domain: " + domain);

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

        boolean useItemNameColumn = false;
        if (keys != null && keys.length > 0 && keys[0].equals("itemName()")) {
            useItemNameColumn = true;
        }

        parseString = parseString.substring(keySyntax.length() + 2).trim();

        Object[] items;
        ArrayList<Object> itemNames = new ArrayList<Object>();
        SimpleDBDataList dataList = new SimpleDBDataList();

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

            log.trace("Parsing dataSyntax: " + itemSyntax);
            dataList.setDomainName(domain);
            StringBuffer dataBuffer = new StringBuffer(itemSyntax);
            pointer = 0;
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

            // replace ( and ) with | and then parse like its a | delimited file
            // todo make beter
            String data =
                    dataBuffer.toString().replace('(', '|').replace(')', '|');
            Object[] valueSequence = SimpleDBParser.parseList(data, "|");

            if (valueSequence.length == 0) {
                throw new RuntimeException(
                        "Illegal insertExpression in values: "
                                + insertExpression);
            } else {
                for (int i = 0; i < valueSequence.length; i++) {
                    String valueString = (String) valueSequence[i];
                    Object[] values =
                            SimpleDBParser.parseList(valueString, "'\"");
                    if (!dataList.getItemNames().contains(itemNames.get(i))) {
                        SimpleDBMap sdbMap = new SimpleDBMap();
                        for (int j = 0; j < values.length; j++) {
                            Object value = values[j];
                            sdbMap.put(keys[j], value);
                        }
                        sdbMap.setItemName(itemNames.get(i));
                        dataList.add(sdbMap);
                        log.error("TODO: in method getItemName? " + sdbMap);
                        log.error(dataList.getItemNames());
                        log
                                .error(dataList.get(dataList.size() - 1)
                                        .toStringF());
                    } else {
                        // ÊSearchÊforÊelementÊinÊlist
                        int index =
                                Collections.binarySearch(dataList
                                        .getItemNames(), itemNames.get(i));
                        SimpleDBMap sdbMap = dataList.get(index);
                        if (sdbMap.getItemName().equals(itemNames.get(i))) {
                            for (int j = 0; j < values.length; j++) {
                                sdbMap.put(keys[j], values[j]);
                            }
                            log.error("TODO: improve on method getItemName? "
                                    + sdbMap);

                            log.error(dataList.getItemNames());
                            log.error(dataList.get(index).toStringF());
                        } else {
                            log
                                    .error("TODO: improve on method getItemName not found");
                            log.error(dataList.getItemNames());
                        }
                    }
                }
            }
        } else {

            // first key is itemName(), assume first column is itemName() unique
            // key

            String itemSyntax =
                    parseString.substring(
                            parseString.toLowerCase().indexOf("values")
                                    + "values".length()).trim();

            log.trace("Parsing dataSyntax: " + itemSyntax);
            dataList.setDomainName(domain);
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

            // replace ( and ) with | and then parse like its a | delimited file
            // todo make beter
            String data =
                    dataBuffer.toString().replace('(', '|').replace(')', '|');
            Object[] valueSequence = SimpleDBParser.parseList(data, "|");

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
                        SimpleDBMap sdbMap = new SimpleDBMap();
                        sdbMap.setItemName(values[0]);
                        for (int j = 1; j < values.length; j++) {
                            sdbMap.put(keys[j], values[j]);
                        }
                        dataList.add(sdbMap);
                        log.error("TODO: in method getItemName? " + sdbMap);
                        log.error(dataList.getItemNames());
                        log
                                .error(dataList.get(dataList.size() - 1)
                                        .toStringF());
                    } else {
                        // ÊSearchÊforÊelementÊinÊlist
                        int index =
                                Collections.binarySearch(dataList
                                        .getItemNames(), values[0]);
                        SimpleDBMap sdbMap = dataList.get(index);
                        if (sdbMap.getItemName().equals(values[0])) {
                            for (int j = 1; j < values.length; j++) {
                                sdbMap.put(keys[j], values[j]);
                            }
                            log.error("TODO: improve on method getItemName? "
                                    + sdbMap);

                            log.error(dataList.getItemNames());
                            log.error(dataList.get(index).toStringF());
                        } else {
                            log
                                    .error("TODO: improve on method getItemName not found");
                            log.error(dataList.getItemNames());
                        }
                    }
                }
            }

        }

        log.trace(dataList);
        batchPutAttributes(dataList);

        // return a select count(*) from domain formatted list
        SimpleDBDataList list = new SimpleDBDataList();
        list.setDomainName(domain);

        Iterator<Object> iterator = itemNames.iterator();
        while (iterator.hasNext()) {

            Object itemName = iterator.next();
            SimpleDBMap map = new SimpleDBMap();
            map.setItemName(itemName);
            list.add(map);

        }
        long responseTime = System.currentTimeMillis() - t0;
        list.setResponseTime(responseTime);
        return list;
    }

    /**
     * The Insert operation is a custom utility method that inserts a set of key
     * value pairs, by parsing the insert statement and uses the
     * batchPutAttributes method to insert the key value pairs. Unlike standard
     * SQL, the item name used is also given. If there is already an item name,
     * the key - value pair is added to the item name.
     * <p>
     * The quoting rules are the same as for the standard select statement
     * syntax.
     * <p>
     * If no itemName is given then a universally unique identifiers (UUID) is
     * generated for the item name, a guarantee for no collisions of the items
     * inserted. For the rest the insert is similar to the standard SQL INSERT
     * statement. Example strings follow (no new line characters should be
     * used):
     * 
     * <code>
     * insert into domain (`keyone`, keytwo) 
     *   values ('value1a', 'value2a') WHERE key1='value';
     * </code>
     * 
     * @param insertExpression
     *            the insert expression used to generate key value pairs for the
     *            domain and the itemName.
     * 
     * @return a SimpleDBDataList of itemNames inserted
     */
    public SimpleDBDataList setInsertWhere(String insertExpression) {

        long t0 = System.currentTimeMillis();

        log.trace("Entering setInsertWhere");
        insertExpression = ExtendedFunctions.trimSentence(insertExpression);

        // modify expression to first perform select
        if (insertExpression.trim().toLowerCase().indexOf("insert") != 0) {
            throw new RuntimeException("Illegal insertExpression: "
                    + insertExpression);
        }

        String parseString =
                insertExpression.substring("insert into ".length());
        String domain = parseString.substring(0, parseString.indexOf(" "));

        log.trace("Found domain: " + domain);

        String selectExpression =
                "SELECT itemName() FROM "
                        + domain
                        + " "
                        + insertExpression
                                .substring(
                                        insertExpression.toLowerCase().indexOf(
                                                "where")).trim();
        log.trace("selectExpression: " + selectExpression);

        String nextToken = null;
        SimpleDBDataList sdbList;
        ArrayList<Object> itemNames = new ArrayList<Object>();
        int size = 0;
        do {
            sdbList = getSelect(selectExpression, nextToken);
            size += sdbList.size();
            if (size == 0)
                return null;
            for (int i = 0; i < sdbList.size(); i++) {
                Object itemName = sdbList.get(i).getItemName();
                itemNames.add(SimpleDBConverter.encodeValue(itemName));
                log.error("item: " + itemNames.get(i));
            }
            nextToken = sdbList.getNextToken();
            log.trace("nextToken: " + nextToken);
        } while (sdbList.getNextToken() != null);

        // get the key syntax without brackets
        String keySyntax =
                insertExpression.substring(
                        insertExpression.indexOf("(") + 1,
                        SimpleDBParser.indexOfIgnoreCaseRespectQuotes(1,
                                insertExpression, ")", '`')).trim();

        Object[] keys = SimpleDBParser.parseList(keySyntax, "`");
        if (keys.length == 0) {
            throw new RuntimeException("Illegal insertExpression");
        }

        String valueSyntax =
                insertExpression.substring(
                        insertExpression.toLowerCase().indexOf("values")
                                + "values".length(),
                        insertExpression.toLowerCase().indexOf("where")).trim();

        log.trace("Parsing valueSyntax: " + valueSyntax);
        SimpleDBDataList dataList = new SimpleDBDataList();
        dataList.setDomainName(domain);
        StringBuffer dataBuffer = new StringBuffer(valueSyntax);
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
        // replace ( and ) with | and then parse like its a | delimited file
        // todo make beter
        String data = dataBuffer.toString().replace('(', '|').replace(')', '|');
        Object[] valueSequence = SimpleDBParser.parseList(data, "|");

        String valueString = (String) valueSequence[0];
        Object[] values = SimpleDBParser.parseList(valueString, "'\"");

        if (valueSequence.length == 0) {
            throw new RuntimeException("Illegal insertExpression in values: "
                    + insertExpression);
        } else {
            for (int i = 0; i < itemNames.size(); i++) {

                SimpleDBMap sdbMap = new SimpleDBMap();
                for (int j = 0; j < values.length; j++) {
                    Object value = values[j];
                    sdbMap.put(keys[j], value);

                }
                sdbMap.setItemName(itemNames.get(i));
                dataList.add(sdbMap);
            }
        }

        batchPutAttributes(dataList);

        // return a select count(*) from domain formatted list
        SimpleDBDataList list = new SimpleDBDataList();
        list.setDomainName(domain);

        Iterator<Object> iterator = itemNames.iterator();
        while (iterator.hasNext()) {

            Object itemName = iterator.next();
            SimpleDBMap map = new SimpleDBMap();
            map.setItemName(itemName);
            list.add(map);

        }
        long responseTime = System.currentTimeMillis() - t0;
        list.setResponseTime(responseTime);
        return list;
    }

    /**
     * The Replace operation is a custom utility method that inserts a set of
     * key value pairs, if the key already exists it replaces it with the given
     * key value pair.
     * <p>
     * The code does this by parsing the replace statement and uses the
     * batchPutReplaceAttributes method to insert/ replace the key value pairs.
     * Unlike standard SQL, the item name used can also be given. If there is
     * already an item name, the key - value pair is replaced in the item name.
     * <p>
     * The quoting rules are the same as for the standard select statement
     * syntax.
     * <p>
     * If no itemName is given then a universally unique identifiers (UUID) is
     * generated for the item name, a guarantee for no collisions of the items
     * inserted. For the rest the replace is similar to the standard SQL REPLACE
     * statement. Example strings follow (no new line characters should be
     * used):
     * 
     * <code>
     * replace into domain (`keyone`, keytwo) 
     *   values aItemName('value1a', 'value2a');
     * replace into domain (keyone, `keytwo`) 
     *   values ('value1a', 'value2a'), ('value1b', 'value2b');
     * </code>
     * 
     * @param replaceExpression
     *            the replace expression used to generate key value pairs for
     *            the domain and the itemName.
     * 
     * @return a SimpleDBDataList of itemNames modified/ inserted
     */
    public SimpleDBDataList setReplace(String replaceExpression) {
        long t0 = System.currentTimeMillis();
        log.trace("Entering replace");

        // make sure spaces are ok and we have no enters
        replaceExpression = ExtendedFunctions.trimSentence(replaceExpression);

        log.trace("Got replace expression: " + replaceExpression);
        // basic check if expression has good syntax
        if (replaceExpression.trim().toLowerCase().indexOf("replace") != 0
                && replaceExpression.trim().toLowerCase().indexOf("values") != 0) {
            throw new RuntimeException("Illegal insertExpression: "
                    + replaceExpression);
        }

        String parseString =
                replaceExpression.substring("replace into ".length());
        String domain = parseString.substring(0, parseString.indexOf(" "));

        log.trace("Found domain: " + domain);

        parseString = parseString.substring(domain.length()).trim();

        // get the key syntax without brackets
        String keySyntax =
                parseString.substring(
                        1,
                        SimpleDBParser.indexOfIgnoreCaseRespectQuotes(1,
                                parseString, ")", '`')).trim();

        Object[] keys = SimpleDBParser.parseList(keySyntax, "`");

        boolean useItemNameColumn = false;
        if (keys != null && keys.length > 0 && keys[0].equals("itemName()")) {
            useItemNameColumn = true;
        }
        parseString = parseString.substring(keySyntax.length() + 2).trim();
        Object[] items;
        ArrayList<Object> itemNames = new ArrayList<Object>();
        SimpleDBDataList dataList = new SimpleDBDataList();

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

            log.trace("Parsing dataSyntax: " + itemSyntax);
            dataList.setDomainName(domain);
            StringBuffer dataBuffer = new StringBuffer(itemSyntax);
            pointer = 0;
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

            // replace ( and ) with | and then parse like its a | delimited file
            // todo make beter
            String data =
                    dataBuffer.toString().replace('(', '|').replace(')', '|');
            Object[] valueSequence = SimpleDBParser.parseList(data, "|");

            if (valueSequence.length == 0) {
                throw new RuntimeException(
                        "Illegal replaceExpression in values: "
                                + replaceExpression);
            } else {
                for (int i = 0; i < valueSequence.length; i++) {
                    String valueString = (String) valueSequence[i];
                    Object[] values =
                            SimpleDBParser.parseList(valueString, "'\"");
                    if (!dataList.getItemNames().contains(itemNames.get(i))) {
                        SimpleDBMap sdbMap = new SimpleDBMap();
                        for (int j = 0; j < values.length; j++) {
                            Object value = values[j];
                            sdbMap.put(keys[j], value);
                        }
                        sdbMap.setItemName(itemNames.get(i));
                        dataList.add(sdbMap);
                        log.error("TODO: in method getItemName? " + sdbMap);
                        log.error(dataList.getItemNames());
                        log
                                .error(dataList.get(dataList.size() - 1)
                                        .toStringF());
                    } else {
                        // ÊSearchÊforÊelementÊinÊlist
                        int index =
                                Collections.binarySearch(dataList
                                        .getItemNames(), itemNames.get(i));
                        SimpleDBMap sdbMap = dataList.get(index);
                        if (sdbMap.getItemName().equals(itemNames.get(i))) {
                            for (int j = 0; j < values.length; j++) {
                                sdbMap.put(keys[j], values[j]);
                            }
                            log.error("TODO: improve on method getItemName? "
                                    + sdbMap);

                            log.error(dataList.getItemNames());
                            log.error(dataList.get(index).toStringF());
                        } else {
                            log
                                    .error("TODO: improve on method getItemName not found");
                            log.error(dataList.getItemNames());
                        }
                    }
                }
            }
        } else {

            // first key is itemName(), assume first column is itemName() unique
            // key

            String itemSyntax =
                    parseString.substring(
                            parseString.toLowerCase().indexOf("values")
                                    + "values".length()).trim();

            log.trace("Parsing dataSyntax: " + itemSyntax);
            dataList.setDomainName(domain);
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

            // replace ( and ) with | and then parse like its a | delimited file
            // todo make beter
            String data =
                    dataBuffer.toString().replace('(', '|').replace(')', '|');
            Object[] valueSequence = SimpleDBParser.parseList(data, "|");

            if (valueSequence.length == 0) {
                throw new RuntimeException(
                        "Illegal replaceExpression in values: "
                                + replaceExpression);
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
                        SimpleDBMap sdbMap = new SimpleDBMap();
                        sdbMap.setItemName(values[0]);
                        for (int j = 1; j < values.length; j++) {
                            sdbMap.put(keys[j], values[j]);
                        }
                        dataList.add(sdbMap);
                        log.error("TODO: in method getItemName? " + sdbMap);
                        log.error(dataList.getItemNames());
                        log
                                .error(dataList.get(dataList.size() - 1)
                                        .toStringF());
                    } else {
                        // ÊSearchÊforÊelementÊinÊlist
                        int index =
                                Collections.binarySearch(dataList
                                        .getItemNames(), values[0]);
                        SimpleDBMap sdbMap = dataList.get(index);
                        if (sdbMap.getItemName().equals(values[0])) {
                            for (int j = 1; j < values.length; j++) {
                                sdbMap.put(keys[j], values[j]);
                            }
                            log.error("TODO: improve on method getItemName? "
                                    + sdbMap);

                            log.error(dataList.getItemNames());
                            log.error(dataList.get(index).toStringF());
                        } else {
                            log
                                    .error("TODO: improve on method getItemName not found");
                            log.error(dataList.getItemNames());
                        }
                    }
                }
            }

        }

        log.trace(dataList);
        batchPutReplaceAttributes(dataList);

        // return a select count(*) from domain formatted list
        SimpleDBDataList list = new SimpleDBDataList();
        list.setDomainName(domain);

        Iterator<Object> iterator = itemNames.iterator();
        while (iterator.hasNext()) {

            Object itemName = iterator.next();
            SimpleDBMap map = new SimpleDBMap();
            map.setItemName(itemName);
            list.add(map);

        }
        long responseTime = System.currentTimeMillis() - t0;
        list.setResponseTime(responseTime);
        return list;
    }

    /**
     * The Replace operation is a custom utility method that replaces a set of
     * key value pairs, by parsing the replace statement and uses the
     * batchPutReplaceAttributes method to replace the key value pairs. Unlike
     * standard SQL.
     * <p>
     * The quoting rules are the same as for the standard select statement
     * syntax.
     * <p>
     * The replace is similar to the standard MYSQL REPLACE statement except
     * that it expects a where clause to select the itemNames to work on.
     * Example strings follow:
     * 
     * <code>
     * replace into domain (`keyone`, keytwo) 
     *   values ('value1a', 'value2a') WHERE key1='value';
     * </code>
     * 
     * @param replaceExpression
     *            the replace expression used to replace key value pairs.
     * 
     * @return a SimpleDBDataList of itemNames modified/ inserted
     */
    public SimpleDBDataList setReplaceWhere(String replaceExpression) {
        long t0 = System.currentTimeMillis();

        log.error("Entering setReplaceWhere");
        replaceExpression = ExtendedFunctions.trimSentence(replaceExpression);

        // modify expression to first perform select
        if (replaceExpression.trim().toLowerCase().indexOf("replace") != 0) {
            throw new RuntimeException("Illegal replaceExpression: "
                    + replaceExpression);
        }

        String parseString =
                replaceExpression.substring("replace into ".length());
        String domain = parseString.substring(0, parseString.indexOf(" "));

        log.trace("Found domain: " + domain);

        String selectExpression =
                "SELECT itemName() FROM "
                        + domain
                        + " "
                        + replaceExpression.substring(
                                replaceExpression.toLowerCase()
                                        .indexOf("where")).trim();
        log.trace("selectExpression: " + selectExpression);

        String nextToken = null;
        SimpleDBDataList sdbList;
        ArrayList<Object> itemNames = new ArrayList<Object>();
        int size = 0;
        do {
            sdbList = getSelect(selectExpression, nextToken);
            size += sdbList.size();
            if (size == 0) {
                SimpleDBDataList list = new SimpleDBDataList();
                list.setDomainName(domain);
            }
            for (int i = 0; i < sdbList.size(); i++) {
                Object itemName = sdbList.get(i).getItemName();
                itemNames.add(SimpleDBConverter.encodeValue(itemName));
                log.error("item: " + itemNames.get(i));
            }
            nextToken = sdbList.getNextToken();
            log.trace("nextToken: " + nextToken);
        } while (sdbList.getNextToken() != null);

        // get the key syntax without brackets
        String keySyntax =
                replaceExpression.substring(
                        replaceExpression.indexOf("(") + 1,
                        SimpleDBParser.indexOfIgnoreCaseRespectQuotes(1,
                                replaceExpression, ")", '`')).trim();

        Object[] keys = SimpleDBParser.parseList(keySyntax, "`");
        if (keys.length == 0) {
            throw new RuntimeException("Illegal replaceExpression");
        }

        String valueSyntax =
                replaceExpression.substring(
                        replaceExpression.toLowerCase().indexOf("values")
                                + "values".length(),
                        replaceExpression.toLowerCase().indexOf("where"))
                        .trim();

        log.trace("Parsing valueSyntax: " + valueSyntax);
        SimpleDBDataList dataList = new SimpleDBDataList();
        dataList.setDomainName(domain);
        StringBuffer dataBuffer = new StringBuffer(valueSyntax);
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
        // replace ( and ) with | and then parse like its a | delimited file
        // todo make beter
        String data = dataBuffer.toString().replace('(', '|').replace(')', '|');
        Object[] valueSequence = SimpleDBParser.parseList(data, "|");

        String valueString = (String) valueSequence[0];
        Object[] values = SimpleDBParser.parseList(valueString, "'\"");

        if (valueSequence.length == 0) {
            throw new RuntimeException("Illegal replaceExpression in values: "
                    + replaceExpression);
        } else {
            for (int i = 0; i < itemNames.size(); i++) {

                SimpleDBMap sdbMap = new SimpleDBMap();
                for (int j = 0; j < values.length; j++) {
                    Object value = values[j];
                    sdbMap.put(keys[j], value);

                }
                sdbMap.setItemName(itemNames.get(i));
                dataList.add(sdbMap);
            }
        }
        batchPutReplaceAttributes(dataList);

        // return a select count(*) from domain formatted list
        SimpleDBDataList list = new SimpleDBDataList();
        list.setDomainName(domain);

        Iterator<Object> iterator = itemNames.iterator();
        while (iterator.hasNext()) {

            Object itemName = iterator.next();
            SimpleDBMap map = new SimpleDBMap();
            map.setItemName(itemName);
            list.add(map);

        }
        long responseTime = System.currentTimeMillis() - t0;
        list.setResponseTime(responseTime);
        return list;
    }

}
