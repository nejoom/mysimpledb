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

import org.apache.log4j.Logger;

import ac.elements.conf.Configuration;
import ac.elements.parser.ExtendedFunctions;
import ac.elements.parser.SimpleDBParser;
import ac.elements.sdb.collection.SimpleDBDataList;

/**
 * The Class SimpleDBImplementationAsync.
 */
public class SimpleDBImplementationAsync extends ASimpleDBCustomAsync {

    /** The Constant log. */
    private final static Logger log =
            Logger.getLogger(SimpleDBImplementationAsync.class);

    /** The access key id. */
    private final static String staticAccessKeyId =
            Configuration.getInstance().getValue("aws", "AWSAccessKeyId");

    /*
     * To provide proof that you truly are the sender of the request, you also
     * include a digital signature calculated using your Secret Access Key.
     */
    /** The secret access key. */
    private final static String staticSecretAccessKey =
            Configuration.getInstance().getValue("aws", "SecretAccessKey");

    private final static SimpleDBImplementationAsync staticSdbc =
            new SimpleDBImplementationAsync(staticAccessKeyId,
                    staticSecretAccessKey);

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
        SimpleDBImplementation exampleDB =
                new SimpleDBImplementation(accessKeyId, secretAccessKey);

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
    public SimpleDBImplementationAsync(String id, String key) {
        super(id, key);
        log.trace("Constructed SimpleDBImplementationAsync");
    }

    private String parseStatement(String preparedStatement,
            ArrayList<Object> myList) {
        log.trace("Entering parseStatement(String preparedStatement, "
                + "ArrayList<Object> myList)");
        return preparedStatement;
    }

    public static SimpleDBDataList setStaticExecute(String preparedStatement) {

        log.trace("Entering setStaticExecute(String preparedStatement)");
        SimpleDBDataList sdbl = staticSdbc.setExcecute(preparedStatement, null);
        return sdbl;
    }

    public SimpleDBDataList setExcecute(String preparedStatement,
            ArrayList<Object> myList) {

        log.trace("Entering setExcecute(preparedStatement, myList)");
        return setExcecute(preparedStatement, myList, null);
    }

    public SimpleDBDataList setExcecute(String preparedStatement,
            ArrayList<Object> myList, String nextToken) {

        log.trace("Entering setExcecute(preparedStatement, myList)");
        long t0 = System.currentTimeMillis();
        SimpleDBDataList sdb = null;
        if (preparedStatement.indexOf("//") == 0)
            return null;
        if (preparedStatement.indexOf("#") == 0)
            return null;
        if (preparedStatement.indexOf("/*") == 0)
            return null;

        preparedStatement = preparedStatement.trim();
        // trim semicolon, which sql sometimes has
        preparedStatement =
                ExtendedFunctions.trimCharacter(preparedStatement, ';');
        preparedStatement = ExtendedFunctions.trimSentence(preparedStatement);
        preparedStatement = parseStatement(preparedStatement, myList);
        // do the type encoding
        preparedStatement = SimpleDBParser.encodeWhereClause(preparedStatement);
        if (preparedStatement.toLowerCase().startsWith("select")) {
            sdb = setSelect(preparedStatement, nextToken);
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
            if (SimpleDBParser.indexOfIgnoreCaseRespectMarker(0,
                    preparedStatement, " where ", "`'\"(", "`'\")") == -1)
                sdb = setInsert(preparedStatement);
            else
                sdb = setInsertWhere(preparedStatement);
        } else if (preparedStatement.toLowerCase().startsWith("replace")) {
            if (SimpleDBParser.indexOfIgnoreCaseRespectMarker(0,
                    preparedStatement, " where ", "`'\"(", "`'\")") == -1)
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

}
