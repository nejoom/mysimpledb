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
package ac.elements.io;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.LinkedHashSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ac.elements.conversion.TypeConverter;
import ac.elements.parser.SimpleDBConverter;
import ac.elements.parser.SimpleDBParser;
import ac.elements.sdb.ExtendedFunctions;
import ac.elements.sdb.SimpleDBCollection;
import ac.elements.sdb.SimpleDBDataList;
import ac.elements.sdb.SimpleDBMap;

public class SaveAsFile {
    /** The Constant log. */
    private final static Log log = LogFactory.getLog(SaveAsFile.class);

    private static String NEWLINE = System.getProperty("line.separator");

    private static String SEPARATOR = System.getProperty("file.separator");

    /**
     */
    public static void exportSelect(final String selectExpression, String path,
            String file, String accessKeyId, String secretAccessKey) {
        if (path.lastIndexOf(SEPARATOR) != path.length() - 1) {
            path += SEPARATOR;
        }
        SimpleDBCollection sdbc =
                new SimpleDBCollection(accessKeyId, secretAccessKey);

        // modify expression to first perform select
        if (selectExpression.trim().toLowerCase().indexOf("select") != 0) {
            throw new RuntimeException("Illegal deleteExpression: "
                    + selectExpression + ", index 0 !="
                    + selectExpression.trim().toLowerCase().indexOf("select"));
        }

        String nextToken = null;
        SimpleDBDataList sdbList;

        // does the select statement have a limit clause?
        int limit =
                TypeConverter.getInt(SimpleDBParser
                        .getLimitClause(selectExpression), -1);
        int rows = 0;
        do {
            sdbList = sdbc.getSelect(selectExpression, nextToken);
            export(sdbList, path, file, true);
            rows += sdbList.size();

            // should we limit the number of items deleted?
            if (limit != -1 && rows >= limit) {
                sdbList.setNextToken(null);
                break;
            }
            nextToken = sdbList.getNextToken();
            log.trace("nextToken: " + nextToken);
        } while (sdbList.getNextToken() != null);

    }

    public static void export(SimpleDBDataList sdb, String path, String file,
            boolean append) {
        long t0 = System.currentTimeMillis();
        Writer pw = null;
        LinkedHashSet<String> keys = new LinkedHashSet<String>();
        if (path.lastIndexOf(SEPARATOR) != path.length() - 1) {
            path += SEPARATOR;
        }
        String pathFile = path + file;
        try {

            FileOutputStream fos = new FileOutputStream(pathFile, append);

            BufferedWriter osw =
                    new BufferedWriter(new OutputStreamWriter(fos, "UTF8"));
            pw = new PrintWriter(osw);
            // pw = osw;
            for (int i = 0; i < sdb.size(); i++) {
                SimpleDBMap map = sdb.get(i);
                StringBuffer line =
                        new StringBuffer("INSERT INTO " + sdb.getDomainName()
                                + " (`itemName()`");

                for (String key : map.keySet()) {
                    line.append(", ");
                    line.append("`");
                    line.append(key);
                    line.append("`");
                    if (!keys.contains(key)) {
                        keys.add(key);
                    }
                }

                line.append(") VALUES ");

                int listCounter = 0;
                int counter = 0;

                while (counter <= listCounter) {
                    if (counter != 0) {
                        line.append(", ('");
                    } else {
                        line.append("('");
                    }
                    line.append(ExtendedFunctions.escapeSql((String) map
                            .getItemName()));
                    line.append("'");

                    for (String key : map.keySet()) {
                        line.append(", ");
                        // increase the loop if attributes are list
                        if (map.get(key).toArray().length - 1 > listCounter) {
                            listCounter = map.get(key).toArray().length - 1;
                        }
                        if (map.get(key).toArray().length > counter) {
                            line.append("'");

                            // convert integers/ numbers to the encoded value
                            String val =
                                    SimpleDBConverter.encodeValue(map.get(key)
                                            .toArray()[counter]);
                            val = ExtendedFunctions.escapeSql(val);
                            line.append(val);
                            line.append("'");
                        } else {
                            line.append("NULL");
                        }
                    }
                    line.append(")");
                    counter++;
                }
                line.append(";");

                ((PrintWriter) pw).println(line);
                // pw.write(line.toString());
                // File filed = new File(pathFile);
                // writeUtf8ToFile(filed, true, line.toString());
                System.out.println(line.toString());
            }

        } catch (IOException e) {
            System.err.println(e);
        } finally {
            if (pw != null) {
                log.error("Saving fileName: " + pathFile);
                try {
                    pw.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }

        pathFile = path + "create." + file;
        File f = new File(pathFile);

        // only create the file if its not already there
        if (!f.isFile())
            try {

                FileOutputStream fos = new FileOutputStream(pathFile, false);

                BufferedWriter osw =
                        new BufferedWriter(new OutputStreamWriter(fos, "UTF8"));
                pw = new PrintWriter(osw);
                StringBuffer line =
                        new StringBuffer("CREATE TABLE " + sdb.getDomainName()
                                + NEWLINE + "(`itemName()` TEXT DEFAULT NULL");
                for (String key : keys) {
                    line.append(", ");
                    line.append(NEWLINE);
                    line.append("`");
                    line.append(key);
                    line.append("`");
                    line.append(" TEXT DEFAULT NULL");
                }
                line.append(");");
                ((PrintWriter) pw).println(line);
                // pw.write(line.toString());

            } catch (IOException e) {
                log.error(e);
            } finally {
                if (pw != null) {
                    log.error("Saving fileName: " + pathFile);
                    try {
                        pw.close();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }

        long ms = (System.currentTimeMillis() - t0);
        log.debug("Time to save: " + ms + "[ms]");
    }

}
