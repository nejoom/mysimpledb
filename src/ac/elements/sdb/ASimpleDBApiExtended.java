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
import java.util.Date;
import java.util.List;
import java.util.TreeMap;
import java.util.UUID;

import org.apache.log4j.Logger;

import ac.elements.parser.ExtendedFunctions;
import ac.elements.parser.SimpleDBConverter;
import ac.elements.parser.SimpleDBParser;
import ac.elements.sdb.collection.SimpleDBDataList;
import ac.elements.sdb.collection.SimpleDBMap;

/**
 * The Class ASimpleDBApiExtended extends amazon's documented api with useful
 * utility operations.
 */
public abstract class ASimpleDBApiExtended extends ASimpleDBApi {

    public ASimpleDBApiExtended(final String id, final String key) {
        super(id, key);
    }

    /** The Constant log. */
    private final static Logger log =
        Logger.getLogger(ASimpleDBApiExtended.class);

    /**
     * Utility method to delete all the attributes and the item specified.
     * 
     * <p>
     * deleteItem is an idempotent operation; running it multiple times on the
     * same item does not result in an error response.
     * 
     * @param domain
     *            the domain name
     * @param item
     *            the item
     * 
     * @return the xml response as a string
     */
    public String deleteItem(final String domain, final String item) {

        log.trace("Entering deleteItem");
        SimpleDBMap sdbMap = new SimpleDBMap();
        sdbMap.setItemName(item);
        return deleteAttributes(domain, sdbMap);
    }

    /**
     * Utility method to perform the ListDomains operation which lists all
     * domains associated with the Access Key ID.
     * 
     * @return the xml response as a string
     */
    public String listDomains() {

        log.trace("Entering listDomains");
        return listDomains(null, null);
    }

    /**
     * Returns all of the attributes associated with the item as a map.
     * 
     * @param domain
     *            the domain
     * @param item
     *            the item
     * 
     * @return the attributes as a SimpleDBMap
     */
    public SimpleDBMap getAttributesAsSimpleDBMap(final String domain,
            final String item) {

        SimpleDBMap map = new SimpleDBMap();
        map.setItemName(item);

        String result = getAttributes(domain, map);

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
    public SimpleDBDataList getDomainsAsSimpleDBDataList() {

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
     *            the next token, string that tells Amazon ASimpleDBApi where to
     *            start the list of domain names.
     * @param previousToken
     *            the previousToken, string that tells Amazon ASimpleDBApi where
     *            to start the previous list of domain names.
     * 
     * @return the domains as a list of strings
     */
    public SimpleDBDataList getDomainsAsSimpleDBDataList(
            final String maxNumberOfDomains, final String nextToken) {

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
     * Returns information about the domain, including when the domain was
     * created, the number of items and attributes, and the size of attribute
     * names and values.
     * 
     * @param domain
     *            The name of the domain for which to display metadata.
     * 
     * @return the xml response as a HashMap
     */
    public TreeMap<String, String> getMetaDataAsTreeMap(final String domain) {

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
    public SimpleDBDataList setSelect(String selectExpression,
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

    public static SimpleDBDataList extractSimpleDBList(String sql) {

        // make sure spaces are ok and we have no enters
        sql = ExtendedFunctions.trimSentence(sql);

        if (log.isDebugEnabled())
            log.debug("Got sql expression: " + sql);

        if (sql.toLowerCase().indexOf("replace ") == 0)
            sql = sql.replace("replace ", "insert ");

        // basic check if expression has good syntax
        if (sql.trim().toLowerCase().indexOf("insert ") != 0
                && sql.trim().toLowerCase().indexOf(" values ") != 0) {
            throw new RuntimeException("Illegal sql expression: " + sql);
        }

        String domain = SimpleDBParser.getDomain(sql);
        if (log.isDebugEnabled())
            log.debug("Found domain: " + domain);

        String parseString = sql.substring("insert into ".length());

        parseString = parseString.substring(domain.length()).trim();

        // get the key syntax without brackets
        String keySyntax =
                parseString.substring(
                        1,
                        SimpleDBParser.indexOfIgnoreCaseRespectQuotes(1,
                                parseString, ")", '`')).trim();
        if (log.isDebugEnabled())
            log.debug("Found keySyntax: " + keySyntax);

        Object[] keys = SimpleDBParser.parseList(keySyntax, "`");

        boolean useItemNameColumn = false;
        if (keys != null && keys.length > 0 && keys[0].equals("itemName()")) {
            useItemNameColumn = true;
        }
        if (log.isDebugEnabled())
            log.debug("Found useItemNameColumn: " + useItemNameColumn);

        parseString = parseString.substring(keySyntax.length() + 2).trim();
        if (log.isDebugEnabled())
            log.debug("Found parseString: " + parseString);

        Object[] items;
        ArrayList<Object> itemNames = new ArrayList<Object>();
        SimpleDBDataList dataList = new SimpleDBDataList();

        // if itemName is not a defined column, then use standard syntax
        if (!useItemNameColumn) {

            if (log.isDebugEnabled()) {
                log.debug("Debug !useItemNameColum");
            }
            String itemSyntax =
                    parseString.substring(
                            parseString.toLowerCase().indexOf("values")
                                    + "values".length()).trim();

            if (log.isDebugEnabled()) {
                log.debug("Parsing itemSyntax: " + itemSyntax);
            }

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
            if (log.isDebugEnabled()) {
                log.debug("Parsing itemBuffer: " + itemBuffer);
            }
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
                throw new RuntimeException("Illegal sql in values: " + sql);
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
                        if (log.isDebugEnabled()) {
                            log.trace("TODO: in method getItemName? " + sdbMap);

                            log.trace(dataList.getItemNames());
                            log.trace(dataList.get(dataList.size() - 1)
                                    .toStringF());
                        }

                    } else {

                        // ÊSearchÊforÊelementÊinÊlist
                        int index =
                                dataList.getItemNames().indexOf(
                                        itemNames.get(i));

                        // ÊSearchÊforÊelementÊinÊlist
                        // int index =
                        // Collections.binarySearch(dataList
                        // .getItemNames(), itemNames.get(i));

                        if (index != -1) {

                            SimpleDBMap sdbMap = dataList.get(index);
                            for (int j = 0; j < values.length; j++) {
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
        } else {

            if (log.isDebugEnabled()) {
                log.debug("Debug useItemNameColum");
            }
            // first key is itemName(), assume first column is itemName() unique
            // key

            String itemSyntax =
                    parseString.substring(
                            parseString.toLowerCase().indexOf("values")
                                    + "values".length()).trim();

            if (log.isDebugEnabled()) {
                log.debug("Parsing itemSyntax: " + itemSyntax);
            }

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

            if (log.isDebugEnabled()) {
                log.debug("Parsing dataBuffer: " + dataBuffer);
            }

            // replace ( and ) with | and then parse like its a | delimited file
            // todo make beter
            String data =
                    dataBuffer.toString().replace('(', '|').replace(')', '|');
            Object[] valueSequence = SimpleDBParser.parseList(data, "|");

            if (log.isDebugEnabled()) {
                log.debug("1. Parsing valueSequence "
                        + "(array of the list of values): "
                        + valueSequence.length);
            }
            if (valueSequence.length == 0) {
                throw new RuntimeException("Illegal sql expression in values: "
                        + sql);
            } else {
                for (int i = 0; i < valueSequence.length; i++) {
                    String valueString = (String) valueSequence[i];
                    Object[] values =
                            SimpleDBParser.parseList(valueString, "'\"");

                    if (log.isDebugEnabled()) {
                        log.debug("1. Parsing i, valueString: " + i + ", "
                                + valueString);
                        log.debug("1. Got x values from valueString: "
                                + values.length);
                        log.debug("1. values[0] is itemName(): " + values[0]);
                        log.debug("1. dataList has itemName(): "
                                + dataList.getItemNames());
                    }

                    if (!dataList.getItemNames().contains(values[0])) {

                        if (log.isDebugEnabled())
                            log.debug("1. values[0] not yet in dataList");
                        if (values[0].equals("null")) {
                            // generate random UUIDs
                            values[0] = UUID.randomUUID().toString();
                            if (log.isDebugEnabled()) {
                                log.debug("1. uuid for: " + values[0]);
                            }
                        }
                        itemNames.add(values[0]);
                        SimpleDBMap sdbMap = new SimpleDBMap();
                        sdbMap.setItemName(values[0]);
                        for (int j = 1; j < values.length; j++) {
                            sdbMap.put(keys[j], values[j]);
                        }
                        dataList.add(sdbMap);
                        if (log.isDebugEnabled()) {
                            log.debug("1. TODO: in method getItemName? "
                                    + sdbMap);
                            log.debug(dataList.getItemNames());
                            log.debug(dataList.get(dataList.size() - 1)
                                    .toStringF());
                        }
                    } else {

                        // ÊSearchÊforÊelementÊinÊlist
                        int index = dataList.getItemNames().indexOf(values[0]);

                        if (log.isDebugEnabled()) {
                            log.debug("1. values[0] already in dataList: "
                                    + values[0]);
                            log.debug("1. dataList has itemName(): "
                                    + dataList.getItemNames());

                            log.debug("1. Found index: " + index);
                        }

                        if (index != -1) {

                            SimpleDBMap sdbMap = dataList.get(index);
                            for (int j = 1; j < values.length; j++) {
                                sdbMap.put(keys[j], values[j]);
                            }
                            if (log.isDebugEnabled()) {
                                log
                                        .debug("1 TODO: improve on method getItemName? "
                                                + sdbMap);
                                log.debug(dataList.getItemNames());
                                log.debug(dataList.get(index).toStringF());
                            }

                        } else {
                            log.error("TODO: improve on method "
                                    + "getItemName not found");
                            log.error("1. values[0] already in dataList: "
                                    + values[0]);
                            log.error(dataList.getItemNames());
                        }
                    }
                }
            }

        }

        if (log.isDebugEnabled())
            log.trace(dataList);
        return dataList;
    }

}
