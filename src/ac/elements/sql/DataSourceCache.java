/**
 *
 * Copyright 2008-2009 Elements. All Rights Reserved.
 *
 * License version: CPAL 1.0
 *
 * The Original Code is glowaxes.org code. Please visit glowaxes.org to see how
 * you can contribute and improve this software.
 *
 * The contents of this file are licensed under the Common Public Attribution
 * License Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 *
 *    http://glowaxes.org/license.
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
 * The contents of this file may be used under the terms of the Elements 
 * End-User License Agreement (the Elements License), in which case the 
 * provisions of the Elements License are applicable instead of those above.
 *
 * You may wish to allow use of your version of this file under the terms of
 * the Elements License please visit http://glowaxes.org/license for details.
 *
 */

package ac.elements.sql;

// Import log4j
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.naming.InitialContext;
import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * DataSourceCache.
 * 
 * @author <a href="mailto:eddie@tinyelements.com">Eddie Moojen</a>
 */
public class DataSourceCache {

    /** The ctx. */
    private static InitialContext ctx = null;

    /** The Constant log. */
    private final static Log logger = LogFactory.getLog(DataSourceCache.class);

    /** The Constant SINGLETON. */
    public static final DataSourceCache SINGLETON = new DataSourceCache();

    static {
        try {
            ctx = new InitialContext();
        } catch (javax.naming.NamingException ne) {
            logger.fatal("Cant create new InitialContext()");
            throw new RuntimeException(ne);
        }

    }

    /** The ds cache. */
    private final Map<String, DataSource> dsCache =
            Collections.synchronizedMap(new HashMap<String, DataSource>());

    /**
     * Private DataSourceCache cannot be instantiated. Singleton access methods
     * are used.
     */
    private DataSourceCache() {

    }

    /**
     * Gets the data source.
     * 
     * @param _dataSource
     *            the dataSource
     * 
     * @return the data source
     */
    public DataSource getDataSource(String _dataSource) {

        DataSource dataSource = dsCache.get(_dataSource);

        if (dataSource == null)
            try {

                DataSource ds =
                        (DataSource) ctx.lookup("java:comp/env/"
                                .concat(_dataSource));

                dsCache.put(_dataSource, ds);

                return ds;

            } catch (javax.naming.NamingException ne) {

                logger.fatal("Cant find datasource for name: " + _dataSource
                        + ", " + ne);
                throw new RuntimeException(ne);

            } catch (java.lang.NullPointerException npe) {

                logger.fatal("Cant find datasource for name: " + _dataSource
                        + ", " + npe);
                throw new RuntimeException(npe);

            }
        else {

            return dataSource;

        }
    }

}