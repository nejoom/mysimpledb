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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ac.elements.conf.Configuration;
import ac.elements.parser.SimpleDBParser;

/**
 * The Class SimpleDBCollection.
 */
public class SimpleDBCollection extends ASimpleDBCollection implements Runnable {

    /** The Constant log. */
    private final static Log log = LogFactory.getLog(SimpleDBCollection.class);

    /**
     * The main method.
     * 
     * @param args
     *            the arguments
     */
    public static void main(String[] args) {

        long t0 = System.currentTimeMillis();
        String insertExpression =
                " insert into testing (add3, `add2`) values (one, two) where key2='two'";

        /** The access key id. */
        String accessKeyId =
                Configuration.getInstance().getValue("aws", "AWSAccessKeyId");

        /*
         * To provide proof that you truly are the sender of the request, you
         * also include a digital signature calculated using your Secret Access
         * Key.
         */
        /** The secret access key. */
        String secretAccessKey =
                Configuration.getInstance().getValue("aws", "SecretAccessKey");
        SimpleDBCollection exampleDB =
                new SimpleDBCollection(accessKeyId, secretAccessKey);

        exampleDB.setInsertWhere(insertExpression);
        System.out.println(System.currentTimeMillis() - t0);

        //
        //
        // sql ="select * from amazonSample";
        //
        // sql = ExtendedFunctions.trimSentence(sql);
        // // extract domain
        // String[] tokens = sql.split(" ");
        // for (int i = 0; i < tokens.length; i++) {
        // if (i < tokens.length - 1 && tokens[i].equalsIgnoreCase("from")) {
        // System.out.println(tokens[i+1]);
        // // list.setDomainName(tokens[i + 1]);
        // break;
        // }
        // }
    }

    /**
     * Instantiates a new simple db collection.
     * 
     * @param id
     *            the id
     * @param key
     *            the key
     */
    public SimpleDBCollection(String id, String key) {
        super(id, key);
    }

    private String statement;

    private SimpleDBDataList dataList;

    /**
     * Instantiates a new simple db collection.
     * 
     * @param id
     *            the id
     * @param key
     *            the key
     */
    public SimpleDBCollection(String id, String key, String statement) {
        super(id, key);
        this.statement = statement;
        new Thread(this).start();
    }

    public SimpleDBCollection(String id, String key, SimpleDBDataList dataList) {
        super(id, key);
        this.dataList = dataList;
        new Thread(this).start();
    }

    private String parseStatement(String preparedStatement,
            ArrayList<Object> myList) {
        return preparedStatement;
    }

    public SimpleDBDataList setExcecute(String preparedStatement,
            ArrayList<Object> myList) {

        SimpleDBDataList sdb = null;
        preparedStatement = preparedStatement.trim();
        preparedStatement = ExtendedFunctions.trimSentence(preparedStatement);
        preparedStatement = parseStatement(preparedStatement, myList);
        log.error("in setExcecute");
        if (preparedStatement.toLowerCase().startsWith("select")) {
            sdb = getSelect(preparedStatement, null);
        } else if (preparedStatement.toLowerCase().startsWith("delete")) {
            if (preparedStatement.toLowerCase().startsWith("delete (")) {
                log.error("in delete (keys) where");
                sdb = setDeleteAttributeWhere(preparedStatement);
            } else {
                if (preparedStatement.toLowerCase().startsWith("delete from")) {
                    log.error("in delete from");
                    sdb = setDelete(preparedStatement);
                } else {
                    log.error("in delete att");
                    sdb = setDeleteAttribute(preparedStatement);
                }
            }
        } else if (preparedStatement.toLowerCase().startsWith("insert")) {
            if (preparedStatement.toLowerCase().indexOf(" where ") == -1)
                sdb = setInsert(preparedStatement);
            else
                sdb = setInsertWhere(preparedStatement);
        } else if (preparedStatement.toLowerCase().startsWith("replace")) {
            if (preparedStatement.toLowerCase().indexOf(" where ") == -1)
                sdb = setReplace(preparedStatement);
            else
                sdb = setReplaceWhere(preparedStatement);
        } else if (preparedStatement.toLowerCase().startsWith("create domain ")) {
            String domain =
                    preparedStatement.substring("create domain ".length(),
                            preparedStatement.length());
            createDomain(domain);
        }

        return sdb;
    }

    public SimpleDBDataList setExcecute(String preparedStatement,
            ArrayList<Object> myList, String nextToken) {

        long t0 = System.currentTimeMillis();
        SimpleDBDataList sdb = null;
        if (preparedStatement.indexOf("--") == 0)
            return null;
        if (preparedStatement.indexOf("#") == 0)
            return null;
        if (preparedStatement.indexOf("/*") == 0)
            return null;

        preparedStatement = preparedStatement.trim();
        // trim semicolon, which sql usually has
        preparedStatement =
                ExtendedFunctions.trimCharacter(preparedStatement, ';');
        preparedStatement = ExtendedFunctions.trimSentence(preparedStatement);
        preparedStatement = parseStatement(preparedStatement, myList);
        // do the type encoding
        preparedStatement = SimpleDBParser.encodeWhereClause(preparedStatement);
        if (preparedStatement.toLowerCase().startsWith("select")) {
            sdb = getSelect(preparedStatement, nextToken);
        } else if (preparedStatement.toLowerCase().startsWith("delete")) {
            if (preparedStatement.toLowerCase().startsWith("delete (")) {
                log.error("in delete (keys) where");
                sdb = setDeleteAttributeWhere(preparedStatement);
            } else {
                if (preparedStatement.toLowerCase().startsWith("delete from")) {
                    log.error("in delete from");
                    sdb = setDelete(preparedStatement);
                } else {
                    log.error("in delete att");
                    sdb = setDeleteAttribute(preparedStatement);
                }
            }
        } else if (preparedStatement.toLowerCase().startsWith("insert")) {
            if (preparedStatement.toLowerCase().indexOf(" where ") == -1)
                sdb = setInsert(preparedStatement);
            else
                sdb = setInsertWhere(preparedStatement);
        } else if (preparedStatement.toLowerCase().startsWith("replace")) {
            if (preparedStatement.toLowerCase().indexOf(" where ") == -1)
                sdb = setReplace(preparedStatement);
            else
                sdb = setReplaceWhere(preparedStatement);
        } else if (preparedStatement.toLowerCase().startsWith("create domain ")) {
            String domain =
                    preparedStatement.substring("create domain ".length(),
                            preparedStatement.length()).trim();
            createDomain(domain);
            sdb = new SimpleDBDataList();
            sdb.setDomainName(domain);
        }

        long responseTime = System.currentTimeMillis() - t0;

        sdb.setResponseTime(responseTime);
        return sdb;
    }

    private static int threads = 0;

    private static int threadsSleeping = 0;

    public void run() {
        String response = "Error";
        threads++;
        while (threads > 40 && threadsSleeping < 40) {
            try {
                threadsSleeping++;
                Thread.sleep(250);
                threadsSleeping--;
                log.error("Error Threads srunning: " + threads);
                log.error("Error Threads sleeping: " + threadsSleeping);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                log.error("Error/ Exception Threads running: " + threads);
            }
        }
        while (response.indexOf("Error") != -1) {
            // TODO Auto-generated method stub
            if (statement != null) {
                log.warn("Starting with: " + statement);
                setExcecute(statement, null, null);
                response = "done";
            } else if (dataList != null) {
                log.warn("Starting with: " + dataList.getDomainName());
                log.warn("Starting with: " + dataList.size());
                response = batchPutAttributes(dataList);
                if (response.indexOf("Error") != -1) {
                    log.error("Error/ Response Threads running: " + threads);
                }
            }
            log.error("Error Threads running: " + threads);
        }
        threads--;
    }
}
