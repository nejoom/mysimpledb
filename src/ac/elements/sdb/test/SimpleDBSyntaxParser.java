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
package ac.elements.sdb.test;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.log4j.Logger;

import ac.elements.sdb.SimpleDBImplementation;
import ac.elements.sdb.collection.SimpleDBDataList;

/**
 * The Class SimpleDBSyntaxParser.
 */
public class SimpleDBSyntaxParser extends TestCase {

    /** The Constant log. */
    @SuppressWarnings("unused")
    private final static Logger log =
        Logger.getLogger(SimpleDBSyntaxParser.class);

    /** The example db. */
    @SuppressWarnings("unused")
    private SimpleDBImplementation exampleDB;
//
//    /** The maps. */
//    private SimpleDBDataList maps;

    /**
     * Suite.
     * 
     * @return the test
     */
    public static Test suite() {
        return new TestSuite(SimpleDBSyntaxParser.class);
    }

    /*
     * (non-Javadoc)
     * 
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
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

    /*
    CREATE DOMAIN aTestDomain;
    INSERT INTO aTestDomain Values (1, 1.2, 8, 'a string');
    INSERT INTO aTestDomain (key1, key2, key3) Values (1, 2, 3);
    INSERT INTO aTestDomain (`key1`, `key2`, `key3`) VALUES (1,2,3);
    INSERT INTO aTestDomain (`key1`, `key2`, `key4`) VALUES (1l, 2l, 3l);
    INSERT INTO aTestDomain (`key1`, `key2`, `key4`) VALUES ('1l', '2l', '3l');
    INSERT INTO aTestDomain (`key1`, `key2`, `key4`) VALUES ("1l", "2l", "3l");
    INSERT INTO aTestDomain (`key1`, `key2`, `key4`) VALUES (1f, 2f, 3f);
    INSERT INTO aTestDomain (`key1`, `key2`, `key4`) VALUES (1d, 2d, 3d);
    INSERT INTO aTestDomain (`key1`, `key2`, `key4`) VALUES ItemName(1d, 2d, 3d);
    INSERT INTO aTestDomain (`key1`, `key2`, `key4`) VALUES (1d, 2d, 3d), (1f, 2f, 3f); 
    INSERT INTO `aTestDomain` (`key1`, `key2`, `key4`) VALUES (1d, 2d, 3d), (1f, 2f, null); 
    INSERT INTO 'aTestDomain' (`key1`, `key2`, `key4`) VALUES `newItem`(1d, 2d, 3d), (1f, 2f, null); 
    INSERT INTO "aTestDomain" (`key1`, `key2`, `key4`) VALUES (1d, 2d, 3d), 6f(1f, 2f, null); 
    INSERT INTO aTestDomain (`key1`, `key2`, `key4`) VALUES `1l`(1d, 2d, 3d), "2l"(1f, 2f, null); 
    INSERT INTO aTestDomain (`key1`, `key2`, `key4`) VALUES `1l`(1d, 2d, 3d), "2l"(1f, 2f, null); 
    INSERT INTO aTestDomain (`key1`, `key2`, `key4`) VALUES `1l`(1d, 2d, 3d), "2l"(1f, 2f, null); 
    INSERT INTO aTestDomain (`key1`, `key2`, `key4`) VALUES `1l`(1d, 2d, 3d), "2l"(1f, 2f, null); 
    INSERT INTO aTestDomain (`key1`, `key2`, `key4`) VALUES `1l`(1d, 2d, 3d), "2l"(1f, 2f, null); 
    INSERT INTO aTestDomain (`key1`, `key2`, `key4`) VALUES `1l`(1d, 2d, 3d), "2l"(1f, 2f, null); 
    SELECT * FROM aTestDomain WHERE key1 = ?;
    SELECT * FROM aTestDomain WHERE key2 = ?;
    */
    /**
     */
    public SimpleDBDataList setDelete(final String deleteExpression) {
        return null;
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
    public SimpleDBDataList setDeleteAttribute(String deleteExpression) {

        return null;

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
    public SimpleDBDataList setInsert(String insertExpression) {

        return null;
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
        return null;
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
        return null;
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
        return null;
    }

}
