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

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ac.elements.concurrency.StatementAsync;
import ac.elements.parser.SimpleDBParser;
import ac.elements.sdb.SimpleDBImplementationAsync;

public class ImportFile {

    // private static String NEWLINE = System.getProperty("line.separator");

    /** The Constant log. */
    private final static Log log = LogFactory.getLog(ImportFile.class);

    private static String SEPARATOR = System.getProperty("file.separator");

    public static void importFile(String path, String file, String id,
            String key) {

        try {
            if (path.lastIndexOf(SEPARATOR) != path.length() - 1) {
                path += SEPARATOR;
            }
            // Open the file that is the first
            // command line parameter
            FileInputStream fstream = new FileInputStream(path + file);

            // Get the object of DataInputStream
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br =
                    new BufferedReader(new InputStreamReader(in, "UTF8"));

            String strLine, cutLine = " ";
            String sql = " ";

            // Read File Line By Line, until a semicolon is found
            while ((strLine = br.readLine()) != null) {
                int end = -1;
                cutLine = strLine;
                while (SimpleDBParser.indexOfIgnoreCaseRespectMarker(0,
                        cutLine, ";", "(\"'`", ")\"'`") != -1) {
                    end =
                            SimpleDBParser.indexOfIgnoreCaseRespectMarker(0,
                                    cutLine, ";", "(\"'`", ")\"'`");
                    if (end == -1)
                        continue;
                    sql += cutLine.substring(0, end);
                    cutLine = cutLine.substring(end + 1);

                    // Excecute the invoke statement in thread framework
                    if (log.isDebugEnabled())
                        log.debug("Running " + sql);
                    SimpleDBImplementationAsync.setStaticExecute(sql.trim());

                    // sdbc.setExcecute(sql.trim(), null);
                    // System.out.println("importing: " + sql.trim());

                    sql = " ";
                }
                if (end == -1) {
                    sql += strLine;
                } else {
                    sql = cutLine + " ";
                    cutLine = " ";
                }
            }
            // Close the input stream
            in.close();
        } catch (Exception e) {// Catch exception if any
            System.err.println("Error: " + e.getMessage());
        }
    }
}
