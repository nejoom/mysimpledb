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

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ac.elements.io.Signature;
import ac.elements.parser.SimpleDBConverter;
import ac.elements.sdb.collection.SimpleDBDataList;
import ac.elements.sdb.collection.SimpleDBMap;

/**
 * The Class ASimpleDBApi is an abstract class implementing the documented
 * Amazon Simple DB Api ISimpleDBApi.
 */
public abstract class ASimpleDBApi implements ISimpleDBApi {

    /** The Constant log. */
    private final static Log log = LogFactory.getLog(ASimpleDBApi.class);

    /**
     * The Access Key ID is associated with your AWS account. You include it in
     * AWS service requests to identify yourself as the sender of the request.
     * The Access Key ID is not a secret, and anyone could use your Access Key
     * ID in requests to AWS.
     */
    private final String id;

    /**
     * To provide proof that you truly are the sender of the request, you also
     * include a digital signature calculated using your Secret Access Key.
     */
    private final String key;

    /**
     * Instantiates a new simple db to issue requests to Amazon's ASimpleDBApi.
     * 
     * <p>
     * Construction requires authentication to verify that the subscriber is
     * authorized to perform the requested action. Authentication ensures that
     * you don't get charged for operations you did not authorize and that
     * nobody else sees your private data..
     * 
     * @param id
     *            the Access Key ID is associated with your AWS account.
     * @param key
     *            the Secret Access Key
     */
    public ASimpleDBApi(final String id, final String key) {
        this.id = id;
        this.key = key;
    }

    /**
     * With the BatchPutAttributes operation, you can perform multiple
     * PutAttribute operations in a single call. This helps you yield savings in
     * round trips and latencies, and enables Amazon ASimpleDBApi to optimize
     * requests, which generally yields better throughput.
     * <p>
     * You can specify attributes and values for items using a combination of
     * the Item.Y.Attribute.X.Name and Item.Y.Attribute.X.Value parameters. To
     * specify attributes and values for the first item, you use
     * Item.0.Attribute.0.Name and Item.0.Attribute.0.Value for the first
     * attribute, Item.0.Attribute.1.Name and Item.0.Attribute.1.Value for the
     * second attribute, and so on.
     * <p>
     * To specify attributes and values for the second item, you use
     * Item.1.Attribute.0.Name and Item.1.Attribute.0.Value for the first
     * attribute, Item.1.Attribute.1.Name and Item.1.Attribute.1.Value for the
     * second attribute, and so on.
     * <p>
     * Amazon ASimpleDBApi uniquely identifies attributes in an item by their
     * name/value combinations. For example, a single item can have the
     * attributes { "first_name", "first_value" } and { "first_name",
     * second_value" }. However, it cannot have two attribute instances where
     * both the Item.Y.Attribute.X.Name and Item.Y.Attribute.X.Value are the
     * same.
     * <p>
     * Optionally, you can supply the Replace parameter for each individual
     * attribute. Setting this value to true causes the new attribute value to
     * replace the existing attribute value(s) if any exist. Otherwise, it
     * simply inserts the attribute values. For example, if an item has the
     * attributes { 'a', '1' }, { 'b', '2'}, and { 'b', '3' } and the requestor
     * calls BatchPutAttributes using the attributes { 'b', '4' } with the
     * Replace parameter set to true, the final attributes of the item are
     * changed to { 'a', '1' } and { 'b', '4' }. This occurs because the new 'b'
     * attribute replaces the old value.
     * 
     * <p>
     * You cannot specify an empty string as an item or attribute name. The
     * BatchPutAttributes operation succeeds or fails in its entirety. There are
     * no partial puts.
     * <p>
     * You can execute multiple BatchPutAttributes operations and other
     * operations in parallel. However, large numbers of concurrent
     * BatchPutAttributes calls can result in Service Unavailable (503)
     * responses.
     * <p>
     * This operation is vulnerable to exceeding the maximum URL size when
     * making a REST request using the HTTP GET method.
     * <p>
     * The following limitations are enforced for this operation:
     * <ul>
     * <li>256 attribute name-value pairs
     * <li>1 MB request size
     * <li>1 billion attributes per domain
     * <li>10 GB of total user data storage per domain
     * <li>25 item limit per BatchPutAttributes operation
     * </ul>
     * 
     * @param domain
     *            the domain
     * @param maps
     *            an SimpleDBDataList of the item key value pairs, the list
     *            corresponds to the items.
     * 
     * @return the xml response as a string
     */
    public String batchPutAttributes(final SimpleDBDataList maps) {

        log.trace("Entering batchPutAttributes");

        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put("Action", "BatchPutAttributes");

        parameters.put("DomainName", maps.getDomainName());

        int index = 0;

        for (final SimpleDBMap map : maps) {
            String itemKey = "Item.".concat("" + index);
            parameters.put(itemKey.concat(".ItemName"), SimpleDBConverter
                    .encodeValue(map.getItemName()));

            int count = 0;

            SimpleDBMap sdbMap = maps.get(index++);

            Iterator<String> keys = sdbMap.keySet().iterator();
            while (keys.hasNext()) {

                String key = keys.next();
                LinkedHashSet<Object> x = sdbMap.get(key);
                Iterator<Object> it = x.iterator();
                while (it.hasNext()) {

                    Object value = it.next();
                    parameters.put(itemKey.concat(".Attribute.").concat(
                            "" + count).concat(".Name"), key);

                    parameters.put(itemKey.concat(".Attribute.").concat(
                            "" + count).concat(".Value"), SimpleDBConverter
                            .encodeValue(value));

                    ++count;
                }
            }

        }
        // log.error(parameters);
        String response = Signature.getXMLResponse(parameters, id, key);
        // log.debug(response);
        return response;

    }

    /**
     * With the batchPutReplaceAttributes method, you can perform multiple
     * PutAttribute operations in a single call, similar to the
     * batchPutAttributes except that all key value pairs replace the current
     * key value pair.
     * <p>
     */
    public String batchPutReplaceAttributes(final SimpleDBDataList maps) {

        log.trace("Entering batchPutReplaceAttributes");

        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put("Action", "BatchPutAttributes");

        parameters.put("DomainName", maps.getDomainName());

        int index = 0;

        for (final SimpleDBMap map : maps) {
            String itemKey = "Item.".concat("" + index);
            parameters.put(itemKey.concat(".ItemName"), SimpleDBConverter
                    .encodeValue(map.getItemName()));

            int count = 0;

            SimpleDBMap sdbMap = maps.get(index++);

            Iterator<String> keys = sdbMap.keySet().iterator();
            while (keys.hasNext()) {

                String key = keys.next();
                LinkedHashSet<Object> x = sdbMap.get(key);
                Iterator<Object> it = x.iterator();
                while (it.hasNext()) {

                    Object value = it.next();
                    parameters.put(itemKey.concat(".Attribute.").concat(
                            "" + count).concat(".Name"), key);

                    parameters.put(itemKey.concat(".Attribute.").concat(
                            "" + count).concat(".Value"), SimpleDBConverter
                            .encodeValue(value));

                    parameters.put(itemKey.concat(".Attribute.").concat(
                            "" + count).concat(".Replace"), "true");

                }
                ++count;
            }

        }

        String response = Signature.getXMLResponse(parameters, id, key);
        System.out.println(response);
        return response;

    }

    /**
     * The CreateDomain operation creates a new domain. The domain name must be
     * unique among the domains associated with the Access Key ID provided in
     * the request. The CreateDomain operation might take 10 or more seconds to
     * complete.
     * 
     * <p>
     * CreateDomain is an idempotent operation; running it multiple times using
     * the same domain name will not result in an error response.
     * 
     * <p>
     * You can create up to 100 domains per account. If you require additional
     * domains, go to {@link "http://aws.amazon.com/contact-us/aws-sales/"}.
     * 
     * @param domain
     *            the domain name to create
     * 
     * @return the xml response as a string
     */
    public String createDomain(final String domain) {

        log.trace("Entering createDomain");

        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put("Action", "CreateDomain");
        parameters.put("DomainName", domain);
        return Signature.getXMLResponse(parameters, id, key);

    }

    /**
     * <p>
     * Deletes one or more attributes associated with the item. If all
     * attributes of an item are deleted, the item is deleted.
     * 
     * <p>
     * If you specify DeleteAttributes with a null map, all the attributes for
     * the item are deleted. {@see #deleteItem(String, String)}
     * 
     * <p>
     * DeleteAttributes is an idempotent operation; running it multiple times on
     * the same item or attribute does not result in an error response.
     * 
     * @param domain
     *            the domain
     * @param item
     *            the item
     * @param sdbMap
     *            the sdb map
     * 
     * @return the xml response as a string
     */
    public String deleteAttributes(final String domain, final SimpleDBMap sdbMap) {

        log.trace("Entering deleteAttributes");

        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put("Action", "DeleteAttributes");
        parameters.put("DomainName", domain);
        parameters.put("ItemName", (String) sdbMap.getItemName());
        log.error(sdbMap);
        int count = 0;
        if (sdbMap != null && sdbMap.keySet() != null) {
            Iterator<String> keys = sdbMap.keySet().iterator();
            while (keys.hasNext()) {

                String key = keys.next();
                LinkedHashSet<Object> x = sdbMap.get(key);
                Iterator<Object> it = x.iterator();

                while (it.hasNext()) {

                    Object value = it.next();
                    parameters.put("Attribute.".concat("" + count).concat(
                            ".Name"), key);
                    if (value != null) {
                        parameters
                                .put("Attribute.".concat("" + count).concat(
                                        ".Value"), SimpleDBConverter
                                        .encodeValue(value));
                    }
                    ++count;
                }
            }
        }
        log.error(parameters);
        return Signature.getXMLResponse(parameters, id, key);
    }

    /**
     * The DeleteDomain operation deletes a domain. Any items (and their
     * attributes) in the domain are deleted as well. The DeleteDomain operation
     * might take 10 or more seconds to complete.
     * 
     * <p>
     * Running DeleteDomain on a domain that does not exist or running the
     * function multiple times using the same domain name will not result in an
     * error response.
     * 
     * @param domain
     *            the domain name to delete
     * 
     * @return the xml response as a string
     */
    public String deleteDomain(final String domain) {

        log.trace("Entering deleteDomain");

        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put("Action", "DeleteDomain");
        parameters.put("DomainName", domain);
        return Signature.getXMLResponse(parameters, id, key);

    }

    /**
     * Returns information about the domain, including when the domain was
     * created, the number of items and attributes, and the size of attribute
     * names and values.
     * 
     * @param domain
     *            The name of the domain for which to display metadata.
     * 
     * @return the xml response as a string
     */
    public String domainMetadata(final String domain) {

        log.trace("Entering domainMetadata");

        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put("Action", "DomainMetadata");
        parameters.put("DomainName", domain);
        return Signature.getXMLResponse(parameters, id, key);

    }

    /**
     * Returns all of the attributes associated with the item. Optionally, the
     * attributes returned can be limited to one or more specified attribute
     * name parameters.
     * 
     * <p>
     * If the item does not exist on the replica that was accessed for this
     * operation, an empty set is returned. The system does not return an error
     * as it cannot guarantee the item does not exist on other replicas.
     * 
     * <p>
     * If you specify GetAttributes without any attribute names, all the
     * attributes for the item are returned.
     * 
     * @param domain
     *            the domain
     * @param sdbMap
     *            the sdb map containing the item name
     * 
     * @return the xml response as a string
     */
    public String getAttributes(final String domain, final SimpleDBMap sdbMap) {

        log.trace("Entering getAttributes");
        String item = (String) sdbMap.getItemName();
        Set<String> attributes = sdbMap.keySet();

        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put("Action", "GetAttributes");
        parameters.put("DomainName", domain);
        if (item != null) {
            parameters.put("ItemName", item);
        }

        int count = 0;
        if (attributes != null) {
            for (final String x : attributes) {

                parameters.put("AttributeName.".concat("" + count), x);
                ++count;
            }
        }

        return Signature.getXMLResponse(parameters, id, key);
    }

    /**
     * The ListDomains operation lists all domains associated with the Access
     * Key ID. It returns domain names up to the limit set by
     * MaxNumberOfDomains.
     * 
     * A NextToken is returned if there are more than MaxNumberOfDomains
     * domains. Calling ListDomains successive times with the NextToken returns
     * up to MaxNumberOfDomains more domain names each time.
     * 
     * @param maxNumberOfDomains
     *            the max number of domains, null if none
     * @param nextToken
     *            the next token, null if none
     * 
     * @return the xml response as a string
     */
    public String listDomains(final String maxNumberOfDomains,
            final String nextToken) {

        log.trace("Entering listDomains");

        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put("Action", "ListDomains");

        if (nextToken != null) {
            parameters.put("NextToken", nextToken);
        }

        if (maxNumberOfDomains != null) {
            parameters.put("MaxNumberOfDomains", maxNumberOfDomains);
        }

        return Signature.getXMLResponse(parameters, id, key);
    }

    /**
     * The PutAttributes operation creates or replaces attributes in an item.
     * You specify new attributes using a Map<String, String>.
     * 
     * <p>
     * Attributes are uniquely identified in an item by their name/value
     * combination. For example, a single item can have the attributes {
     * "first_name", "first_value" } and { "first_name", second_value" }.
     * However, it cannot have two attribute instances where both the
     * Attribute.X.Name and Attribute.X.Value are the same.
     * 
     * <p>
     * Optionally, the requestor can supply the Replace parameter for each
     * individual attribute. Setting this value to true causes the new attribute
     * value to replace the existing attribute value(s).
     * 
     * <p>
     * For example, if an item has the attributes { 'a', '1' }, { 'b', '2'} and
     * { 'b', '3' } and the requestor calls PutAttributes using the attributes {
     * 'b', '4' } with the Replace parameter set to true, the final attributes
     * of the item are changed to { 'a', '1' } and { 'b', '4' }, which replaces
     * the previous values of the 'b' attribute with the new value.
     * 
     * <p>
     * Using PutAttributes to replace attribute values that do not exist will
     * not result in an error response.
     * 
     * <p>
     * You cannot specify an empty string as an attribute name.
     * 
     * <p>
     * Because Amazon ASimpleDBApi makes multiple copies of your data and uses
     * an eventual consistency update model, an immediate GetAttributes or Query
     * request (read) immediately after a DeleteAttributes request (write) might
     * not return the updated data.
     * 
     * <p>
     * The following limitations are enforced for this operation:
     * <ul>
     * <li>256 total attribute name-value pairs per item
     * <li>One billion attributes per domain
     * <li>10 GB of total user data storage per domain
     * </ul>
     * 
     * @param domain
     *            the domain
     * @param item
     *            the item
     * @param sdbMap
     *            the sdb map
     * 
     * @return the xml response as a string
     */
    public String putAttributes(final String domain, final SimpleDBMap sdbMap) {

        log.trace("Entering putAttributes");
        String item = SimpleDBConverter.encodeValue(sdbMap.getItemName());
        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put("Action", "PutAttributes");

        parameters.put("DomainName", domain);

        if (item == null || item.trim().equals("")) {
            // generate random UUIDs
            item = UUID.randomUUID().toString();
        }
        parameters.put("ItemName", item);

        int count = 0;

        Iterator<String> keys = sdbMap.keySet().iterator();
        while (keys.hasNext()) {

            String key = keys.next();
            LinkedHashSet<Object> x = sdbMap.get(key);
            Iterator<Object> it = x.iterator();
            while (it.hasNext()) {

                Object value = it.next();
                parameters.put("Attribute.".concat("" + count).concat(".Name"),
                        key);

                parameters.put(
                        "Attribute.".concat("" + count).concat(".Value"),
                        SimpleDBConverter.encodeValue(value));

            }
            count++;
        }

        return Signature.getXMLResponse(parameters, id, key);

    }

    /**
     * The Select operation returns a set of Attributes for ItemNames that match
     * the query expression. Select is similar to the standard SQL SELECT
     * statement.
     * 
     * <p>
     * The total size of the response cannot exceed 1 MB in total size. Amazon
     * ASimpleDBApi automatically adjusts the number of items returned per page
     * to enforce this limit. For example, even if you ask to retrieve 250
     * items, but each individual item is 100 kB in size, the system returns 10
     * items and an appropriate next token so you can get the next page of
     * results. For information on how to construct select expressions, see
     * Using Select
     * 
     * <p>
     * Operations that run longer than 5 seconds return a time-out error
     * response or a partial or empty result set. Partial and empty result sets
     * contains a next token which allow you to continue the operation from
     * where it left off.
     * 
     * <p>
     * Responses larger than one megabyte return a partial result set. Your
     * application should not excessively retry queries that return
     * RequestTimeout errors. If you receive too many RequestTimeout errors,
     * reduce the complexity of your query expression. When designing your
     * application, keep in mind that Amazon ASimpleDBApi does not guarantee how
     * attributes are ordered in the returned response.
     * 
     * <p>
     * For information about limits that affect Select, see Amazon ASimpleDBApi
     * Limits
     * 
     * @param selectExpression
     *            the select expression used to query the domain.
     * @param nextToken
     *            the next token, string that tells Amazon ASimpleDBApi where to
     *            start the next list of ItemNames.
     * 
     * @return the xml response as a string
     */
    public String select(final String selectExpression, final String nextToken) {

        log.trace("Entering select");
        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put("Action", "Select");

        if (nextToken != null) {
            parameters.put("NextToken", nextToken);
        }

        if (selectExpression != null) {
            parameters.put("SelectExpression", selectExpression);
        }

        String response = Signature.getXMLResponse(parameters, id, key);

        // log.error(response);
        return response;

    }
}