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
package ac.elements.parser;

import java.util.TreeMap;
import java.util.LinkedHashSet;

/**
 * Class to represent a url in protocol, domain, user-password string port,
 * path, file query, anchor (fragment) format.
 */
public class UrlPacket {

    /** The protocol. */
    private String protocol;

    /** The user password. */
    private String userPassword;

    /** The domain. */
    private String domain;

    /** The port. */
    private String port;

    /** The path. */
    private String path;

    /** The file. */
    private String file;

    /** The query. */
    private String query;

    /** The fragment. */
    private String fragment;

    /** The original url. */
    private String originalUrl = "";

    /** The tracked url. */
    private String trackedUrl;

    /** The Constant QUERY. */
    public static final int QUERY = 1;

    /** The Constant FILE. */
    public static final int FILE = 2;

    /** The Constant PATH. */
    public static final int PATH = 3;

    /** The Constant ANCHOR. */
    public static final int ANCHOR = 4;

    /** The gv lock. */
    private final Object gvLock = new Object();

    /** The Constant PART_STRING. */
    public static final String[] PART_STRING =
            { "UNDEFINED", "QUERY", "FILE", "PATH", "ANCHOR" };

    /*
     * get the protocol for the url
     */
    /**
     * Gets the protocol.
     * 
     * @return the protocol
     */
    public String getProtocol() {
        return protocol;
    }

    /**
     * get the domain for the url.
     * 
     * @return the domain
     */
    public String getDomain() {
        return domain;
    }

    /**
     * get the port for the url.
     * 
     * @return the port
     */
    public String getPort() {
        return port;
    }

    /**
     * get the path for the url.
     * 
     * @return the path
     */
    public String getPath() {
        return path;
    }

    //
    // public String getCorrectedPath()
    // {
    // return path;
    // }

    /**
     * get the file for the url.
     * 
     * @return the file
     */
    public String getFile() {
        return file;
    }

    /**
     * get the query for the url.
     * 
     * @return the query
     */
    public String getQuery() {
        return query;
    }

    /**
     * get the anchor (fragment) for the url synonym getFragment().
     * 
     * @return the anchor
     */
    public String getAnchor() {
        return fragment;
    }

    /**
     * get the anchor (fragment) for the url synonym getAnchor().
     * 
     * @return the fragment
     */
    public String getFragment() {
        return fragment;
    }

    /**
     * get the root url for the url, ie the portion not including the query of
     * the url.
     * 
     * @return the root url
     */
    public String getRootUrl() {
        if (originalUrl == null)
            return null;

        String portString = port.equals("") ? "" : ":".concat(port);

        /* special case of localhost */
        String pathString = path.indexOf("/") != 0 ? "/".concat(path) : path;

        String _file = file;

        StringBuffer toString = new StringBuffer();

        toString.append(protocol).append("://").append(domain).append(
                portString).append(pathString).append(_file);

        trackedUrl = toString.toString();
        return trackedUrl;

    }

    /**
     * get the original url of the url internalized.
     * 
     * @return the original url
     */
    public String getOriginalUrl() {
        return originalUrl;
    }

    /**
     * get the string representation url of the url internalized.
     * 
     * @return the tracked url
     */
    public String getTrackedUrl() {
        return this.toString();
    }

    /**
     * get the user-password of the url internalized.
     * 
     * @return the user password
     */
    public String getUserPassword() {
        return userPassword;
    }

    /**
     * Gets the value set.
     * 
     * @param DELIMITER
     *            the dELIMITER
     * @param PART
     *            the pART
     * 
     * @return the value set
     */
    public LinkedHashSet<String> getValueSet(String DELIMITER, int PART) {

        String part;
        if (PART == QUERY)
            part = query;
        else if (PART == FILE)
            part = file;
        else if (PART == PATH)
            part = path;
        else if (PART == ANCHOR)
            part = fragment;
        else
            part = query;

        if (part.equals("") || part.length() == 1)
            return null;
        part = part.concat(DELIMITER);

        LinkedHashSet<String> lhs = new LinkedHashSet<String>();

        String value;

        String[] result = part.split(DELIMITER);

        for (int i = 0; i < result.length; i++) {

            value = result[i];
            if (value.length() > 0)
                lhs.add(value);

        }

        return lhs;
    }

    /**
     * Get the value key pairs in a query string (url formatted).<br>
     * <br>
     * eg. for http://google/search?keyEg=valueEg<br>
     * getKeyValuePairs("&", UrlParser.QUERY) returns keyEg=valueEg
     * 
     * @param DELIMITER
     *            the dELIMITER
     * @param IDENTIFIER
     *            the iDENTIFIER
     * @param PART
     *            the pART
     * 
     * @return the key value map
     */
    public TreeMap<String, String> getKeyValueMap(String DELIMITER,
            String IDENTIFIER, int PART) {

        String part;
        if (PART == QUERY)
            part = query;
        else if (PART == FILE)
            part = file;
        else if (PART == PATH)
            part = path;
        else
            part = query;

        if (part.equals("") || part.length() == 1)
            return null;

        TreeMap<String, String> tm = new TreeMap<String, String>();

        String keyValuePair;

        String[] result = part.split(DELIMITER);
        for (int i = 0; i < result.length; i++) {

            keyValuePair = result[i];

            int index = keyValuePair.indexOf(IDENTIFIER);
            if (index != -1) {
                String value =
                        keyValuePair
                                .substring(index + 1, keyValuePair.length());
                if (!value.equals(""))
                    tm.put(keyValuePair.substring(0, index), value);

            }

        }

        return tm;
    }

    /**
     * Get the value in a query string (url formatted).<br>
     * <br>
     * eg. for http://google/search?keyEg=valueEg<br>
     * getValue("keyEg","=","&") return valueEg
     * 
     * @param key
     *            The key to extract the value for
     * @param IDENTIFIER
     *            The IDENTIFIER (normally =)
     * @param DELIMITER
     *            The DELIMITER (normally &)
     * @param PART
     *            The PART of the url that should be structured
     * @param DECODE
     *            The decode algorithm used, eg unicode "utf-8" decoding pass
     *            null if no decoding should be used.
     * 
     * @return the value
     */
    public String getValue(String key, String IDENTIFIER, String DELIMITER,
            int PART, String DECODE) {
        synchronized (gvLock) {
            if (key == null)
                return null;

            DECODE = DECODE.trim();

            String part;
            if (PART == QUERY)
                part = query;
            else if (PART == FILE)
                part = file;
            else if (PART == PATH)
                part = path;
            else if (PART == ANCHOR)
                part = fragment;
            else
                part = query;

            if (part == null)
                return null;

            part = DELIMITER.concat(part);

            if (part.equals("") || part.indexOf(IDENTIFIER) == -1) {
                return null;
            } else {

                int keyPos =
                        part.indexOf(DELIMITER.concat(key).concat(IDENTIFIER));

                if (keyPos == -1)
                    return null;

                keyPos = keyPos + DELIMITER.concat(key).length() + 1;

                String value =
                        part.substring(keyPos, part.length()).concat(DELIMITER);

                value = value.substring(0, value.indexOf(DELIMITER));

            }
        }// lock
        return DECODE;
    }

    /**
     * Get the value in a query string (url formatted).<br>
     * <br>
     * eg. for http://google/search?keyEg=valueEg#fragment<br>
     * getValue("keyEg","=","&") return valueEg
     * 
     * @param key
     *            The SERPERATOR (normally &)
     * @param IDENTIFIER
     *            the iDENTIFIER
     * @param DELIMITER
     *            the dELIMITER
     * @param PART
     *            the pART
     * 
     * @return the value
     */
    public String getValue(String key, String IDENTIFIER, String DELIMITER,
            int PART) {
        return getValue(key, IDENTIFIER, DELIMITER, PART, "UTF-8");
    }

    /**
     * Get the value in a query string (url formatted).<br>
     * <br>
     * eg. for http://google/search?keyEg=valueEg<br>
     * getValue("keyEg","=","&") return valueEg
     * 
     * @param key
     *            The SERPERATOR (normally &)
     * @param IDENTIFIER
     *            the iDENTIFIER
     * @param DELIMITER
     *            the dELIMITER
     * 
     * @return the value
     */
    public String getValue(String key, String IDENTIFIER, String DELIMITER) {
        return getValue(key, IDENTIFIER, DELIMITER, QUERY, "UTF-8");
    }

    /**
     * Get the value in a query string (url formatted).<br>
     * <br>
     * eg. for http://google/search?keyEg=valueEg<br>
     * getValue("keyEg") return valueEg
     * 
     * @param key
     *            The key to extract the value for
     * 
     * @return the value
     */
    public String getValue(String key) {
        return getValue(key, "=", "&", QUERY, "UTF-8");
    }

    /**
     * Returns <tt>true</tt> if this url has been mutated to comply to url
     * format definitions.
     * 
     * @return <tt>true</tt> if this url has been mutated
     */
    public boolean isMutated() {
        String url = this.toString();

        int last = url.lastIndexOf('/');
        if (last == -1)
            last = url.length();

        /* first check for fast simple case */
        if (url.equals(originalUrl)) {
            return false;
        }
        /*
         * assume http://www.domain.com == http://www.domain.com/
         */
        else if (originalUrl.equals(url.substring(0, last))) {
            return false;
        }

        /*
         * assume http://www.domain.com/file.html? ==
         * http://www.domain.com/file.html
         */
        last = originalUrl.lastIndexOf('?');
        if (last != -1)
            if (url.equals(originalUrl.substring(0, last))) {
                return false;
            }

        /*
         * assume http://www.domain.com/file.html# ==
         * http://www.domain.com/file.html
         */
        last = originalUrl.lastIndexOf('?');
        if (last != -1)
            if (url.equals(originalUrl.substring(0, last))) {
                return false;
            }

        // check if equals or with url encoding
        if (originalUrl.indexOf("%") != -1) {

            /* first check for fast simple case */
            if (url.equals(originalUrl)) {
                return false;
            } else if (last != -1 && originalUrl.equals(url.substring(0, last))) {
                return false;
            }
        }

        return true;
    }

    /**
     * Set the protocol for the url.
     * 
     * @param protocol
     *            the protocol
     */
    protected void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    /**
     * Set the userPassword for the url.
     * 
     * @param userPassword
     *            the user password
     */
    protected void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    /**
     * Set the domain for the url.
     * 
     * @param domain
     *            the domain
     */
    protected void setDomain(String domain) {
        this.domain = domain;
    }

    /**
     * Set the port for the url.
     * 
     * @param port
     *            the port
     */
    protected void setPort(String port) {
        this.port = port;
    }

    /**
     * Set the path for the url.
     * 
     * @param path
     *            the path
     */
    protected void setPath(String path) {
        // present windows path as c:\my\path\to\file.txt
        if (path.indexOf(':') != -1 && path.indexOf(':') < "/C:".length()) {
            path = path.substring(1, path.length());
        }
        this.path = path;
    }

    /**
     * Set the file in the url.
     * 
     * @param file
     *            the file
     */
    protected void setFile(String file) {
        this.file = file;
    }

    /**
     * Set the query in the url.
     * 
     * @param query
     *            the query
     */
    protected void setQuery(String query) {
        this.query = query;
    }

    /**
     * Set the originalUrl for the internalized url.
     * 
     * @param originalUrl
     *            the original url
     */
    protected void setOriginalUrl(String originalUrl) {
        this.originalUrl = originalUrl;
    }

    /**
     * Set the fragment (anchor) for the url.
     * 
     * @param fragment
     *            the fragment
     */
    protected void setFragment(String fragment) {
        this.fragment = fragment;
    }

    /**
     * To string.
     * 
     * @return A www valid string representation of the url
     */
    public String toString() {
        if (originalUrl == null)
            return null;

        if (trackedUrl != null)
            return trackedUrl;

        String queryString = query.equals("") ? "" : "?".concat(query);

        String fragmentString = fragment.equals("") ? "" : "#".concat(fragment);

        String userPassString =
                userPassword.equals("") ? "" : userPassword.concat("@");

        String portString = port.equals("") ? "" : ":".concat(port);

        /* special case of localhost */
        String pathString = path.indexOf("/") != 0 ? "/".concat(path) : path;

        String _file = file;

        StringBuffer toString = new StringBuffer();

        toString.append(protocol).append("://").append(userPassString).append(
                domain).append(portString).append(pathString).append(_file)
                .append(queryString).append(fragmentString);

        trackedUrl = toString.toString();
        return trackedUrl;
    }

}
