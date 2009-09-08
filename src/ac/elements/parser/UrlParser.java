package ac.elements.parser;

/*
 *  Elements Java Object tracking toolkit Library
 *  Copyright (C) 1999-2004 Elements
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  The full license is located at the root of this distribution
 *  in the LICENSE file.
 *
 *  Please report bugs to development@tinyelements.net
 *  Adapted from Jonathan Lurie
 *  http://java.sun.com/developer/technicalArticles/ALT/cachingservices/
 *
 *  10000000 in 6068 msecs HashMap stored
 *  10000000 in 5358 ms UrlPacket
 *  10000000 in 4256 ms Standard packet
 *  10000000 in 3916 ms Standard packet without checks
 *  10000000 in 4557 ms Standard http: with checks
 *  10000000 in 5100 ms Standard http: with checks
 *  10000000 in 7872 ms with logging.and error handling
 *  10000000 in 8482 ms with logging, error handeling, 
 *                      and common format checked
 */

import java.util.WeakHashMap;

import ac.elements.sdb.ExtendedFunctions;

/**
 * Class to produce a UrlPackets. Internal methods parse url and take care of
 * www anomolies in url representation.<br>
 * Url produced is in form of:<br>
 * p:[http://]u:[]d:[opentracker.net]pt:[]p:[/path/]f:[file.html]q:[]a:[]
 * 
 * The following is an example cases that cause errors:
 * http://www.test.com/page.asp//adding/key/types
 * 
 * Uses an underlying AutoRefreshMap to cache results of algorithm.
 */
public class UrlParser {

    // private static final int
    // PROTOCOL_SEPERATOR = "://".length();

    /** The Constant DEFAULT_FILE. */
    public static final String DEFAULT_FILE = "";

    /** The queryset. */
    private static boolean queryset = false;

    /** The _original url. */
    private static String _originalUrl;

    /*
     * public AutoRefreshMap(String name, int initialCapacity, int _minSize, int
     * _maxSize)
     */

    /** The arm. */
    private static WeakHashMap<String, UrlPacket> arm = new WeakHashMap<String, UrlPacket>();

    /**
     * Instantiates a new url parser.
     */
    private UrlParser() {
    }

    /*
     * private on account that rawUrl is formed for non-http(s) urls - ////
     * slashes are minimized - files get well formatted - mailto: gets protocol -
     * mid, attachment, outbind, outlook, ftp get handled lazily
     * 
     */
    /**
     * Gets the specialized protocol.
     * 
     * @param rawUrl the raw url
     * 
     * @return the specialized protocol
     */
    private static String getSpecializedProtocol(String rawUrl) {

        /*
         * catch http:/www.domain.nl
         */
        if (rawUrl.substring(0, 6).equals("http:/")) {
            rawUrl = "http://".concat(rawUrl.substring(6, rawUrl.length()));
            return rawUrl;
        }

        try {

            // remove excessive slashes
            if (rawUrl.indexOf("////") != -1)
                rawUrl = ExtendedFunctions.replace(rawUrl, "////", "///");

            // mailbox: must have refurnished protocal
            if (rawUrl.indexOf("mailbox:") == 0) {
                rawUrl = ExtendedFunctions.replace(rawUrl, "|", ":");

                if (rawUrl.indexOf("///") == -1) {

                    rawUrl =
                            rawUrl.substring("mailbox:".length(), rawUrl
                                    .length());

                    /* cut "mailbox://" "mailbox:/C|/" etc TO C:/file.html */
                    /* cut any trailing slashes */
                    boolean slashFound = true;
                    int parse = 0;
                    while (slashFound) {
                        slashFound = rawUrl.charAt(++parse) == '/';
                    }

                    /* distinguish network name from harddrive name */
                    if (rawUrl.indexOf(':') <= "C:".length() + 1
                            && rawUrl.indexOf(':') != -1)
                        rawUrl =
                                "mailbox:///".concat(rawUrl.substring(parse,
                                        rawUrl.length()));
                    else
                        rawUrl =
                                "mailbox://".concat(rawUrl.substring(parse,
                                        rawUrl.length()));
                }
            } else

            // file must have refurnished protocal
            if (rawUrl.indexOf("file:") == 0) {

                rawUrl = ExtendedFunctions.replace(rawUrl, "|", ":");

                if (rawUrl.indexOf("///") == -1) {

                    rawUrl =
                            rawUrl.substring("file:".length(), rawUrl.length());

                    /* cut "file://" "file:/C|/" etc TO C:/file.html */
                    /* cut any trailing slashes */
                    boolean slashFound = true;
                    int parse = 0;
                    while (slashFound) {
                        slashFound = rawUrl.charAt(++parse) == '/';
                    }

                    /* distinguish network name from harddrive name */
                    if (rawUrl.indexOf(':') <= "C:".length() + 1
                            && rawUrl.indexOf(':') != -1)
                        rawUrl =
                                "file:///".concat(rawUrl.substring(parse,
                                        rawUrl.length()));
                    else
                        rawUrl =
                                "file://".concat(rawUrl.substring(parse, rawUrl
                                        .length()));
                }
            }

            // url must have some sort of protocal
            if (rawUrl.indexOf(":///") == -1) {
                String lcRawUrl = rawUrl.toLowerCase();
                if (lcRawUrl.indexOf("file:") != -1) {
                } else if (lcRawUrl.indexOf("mailto:") != -1) {
                    rawUrl =
                            ExtendedFunctions.replace(rawUrl, "mailto:",
                                    "mailto://");
                }
                /* a javascript url */
                else if (lcRawUrl.indexOf("javascript:") != -1) {
                    rawUrl =
                            ExtendedFunctions.replace(rawUrl, "javascript:",
                                    "javascript:///");
                }
                /* windows ? */
                else if (lcRawUrl.indexOf("attachment:/") != -1) {
                    rawUrl =
                            ExtendedFunctions.replace(rawUrl, "attachment:/",
                                    "mail:///");
                }
                /* message-ID are defined in RFC 2111 */
                else if (lcRawUrl.indexOf("mid:/") != -1) {
                    rawUrl = "mid:///";
                }
                /* outlook express */
                else if (lcRawUrl.indexOf("outbind://") != -1) {
                    rawUrl = "outbind:///";
                } else if (lcRawUrl.indexOf("mk:@msitstore") != -1) {
                    rawUrl =
                            "ms-its://".concat(rawUrl.substring(rawUrl
                                    .indexOf("::/") + 3, rawUrl.length()));
                } else if (lcRawUrl.indexOf("ms-its") != -1) {
                    rawUrl =
                            "ms-its:///".concat(rawUrl.substring(rawUrl
                                    .indexOf("::/") + 3, rawUrl.length()));
                } else if (rawUrl.indexOf("outlook://") != -1) {

                    rawUrl =
                            "outlook:///".concat(rawUrl.substring(rawUrl
                                    .indexOf("://") + 3, rawUrl.length()));

                } else if (lcRawUrl.indexOf("mailbox:/") != -1) {

                } else if (lcRawUrl.indexOf("ftp://") != -1) {

                } else if (lcRawUrl.indexOf("imap://") != -1) {

                } else if (lcRawUrl.indexOf("news://") != -1) {

                }

                /*
                 * mhtml:file : is outlook express
                 * http://www.us-cert.gov/cas/techalerts/TA04-099A.html
                 * mhtml:file://c:documents%20and%20settings\\local%20settings\\temporary%20internet%20files.mht!http://www.effenaar.nl/fnr_content_engine/Effenaar_act.php";
                 * ssp: sspng:
                 */
                else {
                    throw new RuntimeException(
                            ".getSpecializedProtocol: Illegal protocol: "
                                    + rawUrl);
                }
            }

            return rawUrl;

        } catch (Exception e) {
            throw new RuntimeException("Illegal protocol: ".concat(rawUrl)
                    .concat(", ").concat(e.toString()));
        }

    }

    /*
     * private on account that rawUrl is mutated - url is trimmed - spaces
     * become %20 - \ slashes become / - trailing # removed - trailing . removed -
     * trailing / - specialized routine called for non http and https urls TODO:
     * remove yyy/./xxx/file.htm -> yyy/xxx/file.htm (=current)
     */
    /**
     * Gets the well formed url.
     * 
     * @param rawUrl the raw url
     * 
     * @return the well formed url
     */
    private static String getWellFormedUrl(String rawUrl) {

        rawUrl = rawUrl.trim();
        if (rawUrl.equals(""))
            return "";

        // fix for javascript not sending space as "%20"
        // if (rawUrl.indexOf(" ") != -1)
        // rawUrl = TxtBean.replaceText(rawUrl," ", "%20");

        // fix for "C:\Documents and settings\file.html" etc
        if (rawUrl.indexOf("\\") != -1)
            rawUrl = ExtendedFunctions.replace(rawUrl, "\\", "/");

        // remove trailing hash
        if (rawUrl.endsWith("#"))
            rawUrl = rawUrl.substring(0, rawUrl.length() - 1);

        // remove trailing question mark
        if (rawUrl.endsWith("?"))
            rawUrl = rawUrl.substring(0, rawUrl.length() - 1);

        // //remove trailing points
        // if (rawUrl.endsWith(".") &&
        // rawUrl.indexOf("#") == -1 &&
        // rawUrl.indexOf("?") == -1)
        // rawUrl = rawUrl.substring(0,rawUrl.length()-1);

        // remove trailing slash
        if (rawUrl.endsWith("/") && rawUrl.indexOf("#") == -1
                && rawUrl.indexOf("?") == -1)
            rawUrl = rawUrl.substring(0, rawUrl.length() - 1);

        String rawUrLc = rawUrl.toLowerCase();
        if (!rawUrLc.startsWith("http://") && !rawUrLc.startsWith("https://")) {
            rawUrl = getSpecializedProtocol(rawUrl);
        }

        return rawUrl;
    }

    /**
     * Gets the query.
     * 
     * @param rawUrl the raw url
     * 
     * @return the query
     */
    private static String getQuery(String rawUrl) {

        if (rawUrl.indexOf('?') == -1)
            return "";
        else {
            queryset = true;
            rawUrl = rawUrl.substring(rawUrl.indexOf('?') + 1, rawUrl.length());

            return rawUrl;

        }
    }

    /**
     * Gets the protocol.
     * 
     * @param rawUrl the raw url
     * 
     * @return the protocol
     */
    private static String getProtocol(String rawUrl) {

        String protocol =
                rawUrl.substring(0, rawUrl.indexOf("://")).toLowerCase();

        if (protocol.length() > 5) {
            /* cases which should not throw an exception */
            if (protocol.indexOf("mailto") == -1
                    && protocol.indexOf("javascript") == -1
                    && protocol.indexOf("attachment") == -1
                    && protocol.indexOf("outbind") == -1
                    && protocol.indexOf("ms-its") == -1
                    && protocol.indexOf("outlook") == -1
                    && protocol.indexOf("mailbox") == -1)
                throw new RuntimeException("Illegal protocol: " + rawUrl);
        }

        return protocol;

    }

    /*
     * Private on account that rawUrl is already chopped to Ensured form
     * [protocol://]x [user:password@] [domain:port][/path/][file.name]
     * x[?query][#anchor]
     */
    /**
     * Gets the user password.
     * 
     * @param rawUrl the raw url
     * 
     * @return the user password
     */
    private static String getUserPassword(String rawUrl) {
        /* if rawUrl of type http://domain/ */
        if (rawUrl.indexOf("@") == -1) {
            return "";
        }

        String chopped =
                rawUrl.substring(rawUrl.indexOf("://") + 1, rawUrl.length());

        // case http://test/
        if (chopped.indexOf("/") != -1) {
            chopped = chopped.substring(0, chopped.indexOf("/"));
        }

        if (chopped.indexOf("@") == -1)
            return "";

        // case http://user:pass@domain
        chopped = chopped.substring(0, chopped.indexOf("@"));

        return chopped;
    }

    /*
     * Private on account that rawUrl is already chopped to Ensured form
     * [protocol://][user:password@]x [domain:port][/path/][file.name]
     * x[?query][#anchor]
     */
    /**
     * Gets the port.
     * 
     * @param rawUrl the raw url
     * 
     * @return the port
     */
    private static String getPort(String rawUrl) {

        String chopped = rawUrl;

        // case www.cafedvd.com/cgi-bin/MM=123:123:123.html?test
        if (chopped.indexOf('/') != -1)
            chopped = chopped.substring(0, chopped.indexOf("/"));

        if (chopped.indexOf(':') == -1)
            return "";

        // case c:\\filename.txt
        if (chopped.length() <= 1) {
            return "";
        }

        // case test:8000?query=test
        if (chopped.indexOf("?") != -1) {
            chopped = chopped.substring(0, chopped.indexOf("?"));
        }

        // case test:8000#anchor
        if (chopped.indexOf("#") != -1) {
            chopped = chopped.substring(0, chopped.indexOf("#"));
        }

        // case domain:8000
        chopped = chopped.substring(chopped.indexOf(":") + 1, chopped.length());

        return chopped;
    }

    /*
     * Private on account that rawUrl is already chopped to Ensured form
     * [protocol://][user:password@]x [domain:port][/path/][file.name]
     * x[?query][#anchor]
     */
    /**
     * Gets the domain.
     * 
     * @param rawUrl the raw url
     * 
     * @return the domain
     */
    private static String getDomain(String rawUrl) {

        String chopped = rawUrl;

        // case test:port/path
        if (chopped.indexOf("/") != -1) {
            chopped = chopped.substring(0, chopped.indexOf("/"));
        }

        // case user:pass@domain
        if (chopped.indexOf("@") != -1) {
            chopped =
                    chopped.substring(chopped.indexOf("@") + 1, chopped
                            .length());
        }

        // case domain:1234/path
        if (chopped.indexOf(":") != -1) {
            chopped = chopped.substring(0, chopped.indexOf(":"));
        }

        /* Remove trailing . */
        if (chopped.endsWith("."))
            chopped = chopped.substring(0, chopped.length() - 1);

        if (chopped.equals(""))
            chopped = "localhost";

        return chopped.toLowerCase();
    }

    /*
     * Private on account that rawUrl is already chopped to Ensured form
     * [protocol://][user:password@]x [domain:port][/path/][file.name]
     * x[?query][#anchor]
     */
    /**
     * Gets the path.
     * 
     * @param rawUrl the raw url
     * 
     * @return the path
     */
    private static String getPath(String rawUrl) {

        /* case domain:port/ or domain:port */
        if (rawUrl.indexOf("/") == -1
                || (rawUrl.indexOf("/") == rawUrl.lastIndexOf("/"))) {

            return "/";

        }

        // cut from first /
        rawUrl = rawUrl.substring(rawUrl.indexOf("/"), rawUrl.length());

        // cut to last /
        rawUrl = rawUrl.substring(0, rawUrl.lastIndexOf("/") + 1);

        // remove excessive slashes
        if (rawUrl.indexOf("//") != -1)
            rawUrl = ExtendedFunctions.replace(rawUrl, "//", "/");

        return rawUrl;

    }

    /*
     * Private on account that rawUrl is already chopped to Ensured form
     * [protocol://][user:password@]x [domain:port][/path/][file.name]
     * x[?query][#anchor]
     */
    /**
     * Gets the file.
     * 
     * @param rawUrl the raw url
     * 
     * @return the file
     */
    private static String getFile(String rawUrl) {
        /* if of type domain */
        if (rawUrl.indexOf("/") == -1) {
            return DEFAULT_FILE;
        }

        String MARKER = "/";

        String chopped;

        chopped =
                rawUrl.substring(rawUrl.lastIndexOf(MARKER) + 1, rawUrl
                        .length());

        /*
         * case domain:port/something/file.name or
         * domain:port/something/filename/
         */
        if (chopped.length() == 0) {
            return DEFAULT_FILE;
        }

        /*
         * case if its large and contains stuff like a session id, then just
         * ignore all after ';'
         */
        if (chopped.length() >= 50 && chopped.indexOf(';') != -1) {

            return chopped.substring(0, chopped.indexOf(';'));
        } else {

            return chopped;
        }

    }

    /**
     * Gets the fragment.
     * 
     * @param rawUrl the raw url
     * 
     * @return the fragment
     */
    private static String getFragment(String rawUrl) {
        /* anchor hash must appear at end */
        if (rawUrl.indexOf("#") != -1) {
            rawUrl = rawUrl.substring(rawUrl.indexOf("#") + 1, rawUrl.length());

            return rawUrl;
        } else {
            return "";
        }
    }

    /**
     * Factory like method to produce a UrlPacket.<br>
     * Url produced is in form of:<br>
     * p:[http://]u:[]d:[opentracker.net]pt:[]p:[/path/]f:[file.html]q:[]a:[]
     * 
     * @param rawUrl the raw url
     * 
     * @return the url packet
     */
    public static synchronized UrlPacket getUrlPacket(String rawUrl) {

        // long now=0;
        queryset = false;

        // if(logger.isDebugEnabled())
        // now = System.currentTimeMillis();

        if (rawUrl == null)
            return null;

        if (rawUrl.equals(""))
            return null;
        if (rawUrl.equals("-"))
            return null;

        if (arm.containsKey(rawUrl))
            return arm.get(rawUrl);

        UrlPacket urlPacket = new UrlPacket(); // testPacket;

        _originalUrl = rawUrl;
        urlPacket.setOriginalUrl(_originalUrl);

        try {

            /*
             * Ensures form
             * [protocol://][user:password@][domain][/path/][file.name][?query][#anchor]
             */
            rawUrl = getWellFormedUrl(rawUrl);

            if (rawUrl.indexOf("://") == -1)
                throw new RuntimeException("Illegal url: " + rawUrl);

            urlPacket.setFragment(getFragment(rawUrl));

            /*
             * Chop anchor fragment off Ensures form
             * [protocol://][user:password@][domain][/path/][file.name][?query]
             * x[#anchor]
             */
            if (rawUrl.indexOf("#") != -1)
                rawUrl = rawUrl.substring(0, rawUrl.indexOf("#"));
            urlPacket.setQuery(getQuery(rawUrl));

            /*
             * Chop query off Ensures form
             * [protocol://][user:password@][domain][/path/][file.name]
             * x[?query][#anchor]
             */
            if (rawUrl.indexOf("?") != -1)
                rawUrl = rawUrl.substring(0, rawUrl.indexOf('?'));

            urlPacket.setProtocol(getProtocol(rawUrl));

            /*
             * Chop protocol off Ensures form [protocol://]x
             * [user:password@][domain][/path/][file.name] x[?query][#anchor]
             */
            if (rawUrl.indexOf("://") != -1)
                rawUrl =
                        rawUrl.substring(rawUrl.indexOf("://") + 3, rawUrl
                                .length());

            urlPacket.setUserPassword(getUserPassword(rawUrl));

            /*
             * Chop user off Ensures form [protocol://][user:password@]x
             * [domain:port][/path/][file.name] x[?query][#anchor]
             */
            if (rawUrl.indexOf("@") != -1
                    && rawUrl.indexOf("@") < rawUrl.indexOf("/"))
                rawUrl =
                        rawUrl.substring(rawUrl.indexOf("@") + 1, rawUrl
                                .length());

            urlPacket.setDomain(getDomain(rawUrl));

            urlPacket.setPort(getPort(rawUrl));

            /*
             * if we get a file without a dot or ending with a dot OR a slash
             * has been added to make it a directory OR the file name is 4
             * characters and there is no query then assume its a directory
             */
            String _tempFile = getFile(rawUrl);

            if ((_tempFile.indexOf(".") == -1
                    || _tempFile.lastIndexOf(".") == _tempFile.length() - 1
                    || _originalUrl.lastIndexOf("/") == _originalUrl.length() - 1 || _tempFile
                    .length() <= 4)
                    && queryset == false) {
                rawUrl = rawUrl.concat("/");
            }

            urlPacket.setPath(getPath(rawUrl));

            urlPacket.setFile(getFile(rawUrl));

            // if(logger.isDebugEnabled())
            // logger.debug("getUrlPacket took [".concat(
            // "" + (System.currentTimeMillis() - now)).concat("] ms "));

            arm.put(_originalUrl, urlPacket);
            return urlPacket;
        } catch (Exception e) {
            return null;
        }
    }

    // testing

    /**
     * The main method.
     * 
     * @param args the arguments
     */
    public static void main(String[] args) {

        String url =
                "http://www.google.com/search?hl=en&lr=&ie=UTF-8&q=the+is+the+test+for+a+test";

        url =
                "http://search.daum.net/cgi-binnsp/search.cgi?w=tot&q=%C0%CE%B5%B5%BF%A9%C7%E0+";
        url =
                "http://search.empas.com/search/all.html?a=w&s=&f=&z=A&q=%B8%DE%BD%C5%C0%FA+%C4%A3%B1%B8%C3%A3%B1%E2";
        url =
                "http://search.kvasir.no/query?q=Bil%E5terf%F6rs%E4ljare %2Bsverige&submit=S%F8k&what=web";
        url =
                "http://search.goo.ne.jp/web.jsp?TAB=&MT=%C2%E7%B2%F1%C6%C3%BD%B8+";
        url =
                "http://search.msn.com/results.aspx?FORM=MSNH&q=%EC%9B%B9%20%EB%AC%B8%EC%84%9C%20%20";
        url =
                "http://search.msn.co.kr/results.aspx?FORM=SMCRT&q=%20%EB%A9%94%EC%8B%A0%EC%A0%80%20%EC%B9%9C%EA%B5%AC%EC%B0%BE%EA%B8%B0";
        url =
                "http://search.msn.co.il/search/msnSearch?SearchSite=8&ps=0&searchType=MsnIndexSearch&first=1&q=%EE%E9%EC%E9%E5%F0%F8&x=24&y=9";
        url =
                "http://search.msn.co.il/search/IndexSearch/searchResults.jsp;jsessionid=aWRfSr8FzKv4?searchId=858938125&searchString=%EE%E9%EC%E9%E5%F0%F8&searchType=IndexSearch&timeout=20&command=results&page=1&startIndex=17&endIndex=34";
        url =
                "http://search.msn.co.jp/results.aspx?q=%83C%83M%83%8A%83X%97%AF%8Aw&FORM=potest&v=1&RS=CHECKED&CY=ja&CP=932";
        url =
                "http://search.msn.co.jp/results.aspx?q=%97%AF%8Aw&FORM=potest&v=1&RS=CHECKED&CY=ja&CP=932&x=21&y=11";
        url =
                "http://search.msn.co.jp/results.aspx?q=UC%83o%81%5B%83N%83%8C%81%5B&FORM=potest&v=1&RS=CHECKED&CY=ja&CP=932&x=7&y=7";
        url = "http://search.msn.co.jp/results.aspx?q=%E3%82%BB&FORM=SMCRT";
        url =
                "http://search.msn.co.jp/spresults.aspx?ps=ba%3d(0.9)0.1..0.1.%26co%3d(0.10)200.2.5.10.3.%26CY%3dja%26pn%3d1%26rd%3d1%26&q=%E8%90%BD%E5%90%88%E4%BF%A1%E5%BD%A6&ck_sc=1&ck_af=0";

        // url =
        // "http://search.goo.ne.jp/web.jsp?TAB=&MT=%C2%E7%B2%F1%C6%C3%BD%B8+";
        // url =
        // "http://aolsearch.jp.aol.com/search?query=%83C%83M%83%8A%83X%82%CC%91%E5%8Aw&first=31&last=40";
        // url =
        // "http://aolsearch.aol.co.uk/web?query=gollum%26location%3Duk%26isinit%3Dtrue%26submit.x%3D21%26submit.y%3D12&t0=1088825730928&ti=Gollum%20and%20One%20Ring%20Sculpture&si=www.otherlandtoys.co.uk";
        // url =
        // "http://www.google.ca/search?q=cache:t21KQfOj050J:www.ifsworld.com/us/news_events/events/medical_devices_webcast.asp%20%22medical%20devices%22%20%22on-demand%20seminar%22%20OR%20%22web%20seminar%22%20OR%20netseminar%20OR%20webcast%20OR%20webinar%20OR%20%22online%20seminar%22&hl=en&lr=lang_en|lang_fr";
        // url =
        // "http://www.google.de/search?q=leistungspr%C3%BCfstand&hl=de&lr=&ie=UTF-8&sa=N&tab=iw'";
        // url =
        // "http://www.google.co.jp/search?q=cache:xS3g7jIBOVkJ:www.globe-walkers.com/ohno/article/dairibo.htm%20%E4%BB%A3%E7%90%86%E6%AF%8D&hl=ja";
        // url =
        // "http://www.google.co.jp/search?q=cache:9WigqslJITwJ:www.globe-walkers.com/ohno/article/unabomber.htm
        // %E3%83%A6%E3%83%8A%E3%83%9C%E3%83%9E%E3%83%BC&hl=ja&lr=lang_ja&ie=UTF-8&inlang=ja";
        // url =
        // "http://www.google.co.jp/search?q=cache:xS3g7jIBOVkJ:www.globe-walkers.com/ohno/article/dairibo.htm%20%E4%BB%A3%E7%90%86%E6%AF%8D&hl=ja";
        // url =
        // "http://www.google.co.jp/search?hl=ja&inlang=ja&ie=Shift_JIS&c2coff=1&q=%83V%83J%83S%94%FC%8Fp%91%E5%8Aw&lr=";
        // url =
        // "http://www.google.co.jp/search?q=%83G%83%8A%83I%83b%83g%81E%83%8A%83%60%83%83%81%5B%83h%83%5C%83%93%81@%8B%A4%98a%93%7D&hl=ja&inlang=ja&ie=Shift_JIS";
        // url =
        // "http://www.google.co.il/search?hl=iw&inlang=iw&ie=ISO-8859-8-I&q=%F6%E9%EE%F8%E9%ED&btnG=%E7%E9%F4%E5%F9&meta";
        // url =
        // "http://www.google.co.kr/search?q=%EC%9B%B9+%EB%AC%B8%EC%84%9C++&ie=UTF-8&hl=ko&btnG=%EA%B5%AC%EA%B8%80+%EA%B2%80%EC%83%89&lr=";
        // url =
        // "http://www.google.co.kr/search?q=%EB%A9%94%EC%8B%A0%EC%A0%80+%EC%B9%9C%EA%B5%AC%EC%B0%BE%EA%B8%B0&ie=UTF-8&hl=ko&btnG=%EA%B5%AC%EA%B8%80+%EA%B2%80%EC%83%89&lr=";
        // url =
        // "http://www.google.co.hu/search?q=%D7%90%D7%91%D7%99%D7%A0%D7%95%2B%D7%9E%D7%9C%D7%9B%D7%A0%D7%95&hl=hu&lr=&ie=UTF-8&start=20&sa=N";
        // url =
        // "http://www.google.com/search?as_q=%D1%83%D0%BF%D1%80%D0%B0%D0%B2%D0%BB%D0%B5%D0%BD%D1%81%D0%BA%D0%B8
        // %D0%B8%D0%BD%D1%84%D0%BE%D1%80%D0%BC%D0%B0%D1%86%D0%B8%D0%BE%D0%BD%D0%BD%D0%B8
        // %D1%81%D0%B8%D1%81%D1%82%D0%B5%D0%BC%D0%B8&num=10&hl=bg&ie=UTF-8&btnG=Google
        // %D0%A2%D1%8A%D1%80%D1%81%D0%B5%D0%BD%D0%B5&as_epq=%D0%B2
        // %D0%B1%D0%B8%D0%B7%D0%BD%D0%B5%D1%81%D0%B0&as_oq=&as_eq=&lr=lang_bg&as_ft=i&as_filetype=&as_qdr=all&as_occt=any&as_dt=i&as_sitesearch=";
        // url = "http://www.google.com/search?q=herpes i
        // %C3%B6gat&hl=en&lr=&ie=UTF-8&start=20&sa=N";
        // url = "http://www.google.com/search?ie=UTF-8&oe=UTF-8&q=%22siegfried
        // line%22 washing 19";
        // url =
        // "http://www.google.com/search?hl=en&inlang=ja&lr=&ie=shift-jis&q=%8Aw";
        // url =
        // "http://www.google.com/search?hl=en&inlang=ja&lr=&ie=UTF-8&q=who%20uses%20google&btnG=Search";
        // url =
        // "http://cn.websearch.yahoo.com/search/web_cn?p=%CB%D1%CB%F7%C8%AB%B2%BF%CD%F8%D2%B3&lastkey=%25B7s%25A9_&lang=";
        // url =
        // "http://cn.websearch.yahoo.com/search/web_cn?p=%CB%D1%CB%F7%BC%F2%CC%E5%D6%D0%CE%C4%CD%F8%D2%B3&lastkey=%25CB%25D1%25CB%25F7%25C8%25AB%25B2%25BF%25CD%25F8%25D2%25B3&lang=";
        // url =
        // "http://cn.websearch.yahoo.com/search/web_cn?stype=&p=%C6%CF%CC%D1%D1%C02-1%CC%D4%CC%AD%BA%C9%C0%BC+%CF%A3%C0%B0%BD%F1%D2%B9%D5%BD%BD%DD%BF%CB&scch=on";
        // url =
        // "http://tw.search.yahoo.com/search/kimo?p=%A5%5B%AE%B3%A4j%B2%BE%A5%C1";
        // url =
        // "http://apps5.oingo.com/apps/domainpark/results.cgi?client=WORL2323&domain_name=atlantazoo.com&sid=008172837cd20000&pid=15824&ac=r&s=georgia";
        // url =
        // "http://kids.goo.ne.jp/cgi-bin/kidsgoo.cgi?keyword=%A5%B8%A5%A7%A5%F3%A5%AD%A5%F3%A5%B9%BB%E1
        // %CC%E4%C2%EA&base=10&SY=2&MD=2&FR=&IM=&DN=0";
        // url =
        // "http://store.yahoo.com/dvdinternational/plasma-art-under-water.html";
        // url = "http://search.yahoo.com/search?p=Auden %E2%80%9CMusee des
        // Beaux Arts%E2%80%9D &ei=UTF-8&fr=fp-tab-web-t&cop=mss&tab=";
        // url =
        // "http://kr.search.yahoo.com/search?p=%BB%FD%B0%A2%B8%B8%C5%AD+%BC%D3%B5%B5%C7%E2%BB%F3%BF%A1+%B5%B5%BF%F2%C0%CC+%B5%C9%B1%EE%BF%E4%3F&fr=kr-search_top&x=22&y=10";
        // url =
        // "http://kr.search.yahoo.com/search?p=%C0%CE%B5%B5%BF%A9%C7%E0&fr=kr-search_top&x=19&y=11";
        // url =
        // "http://search.yahoo.co.jp/spresults.aspx?ps=ba%3d(0.9)0.1..0.1.%26co%3d(0.10)200.2.5.10.3.%26CY%3dja%26pn%3d1%26rd%3d1%26&q=%E8%90%BD%E5%90%88%E4%BF%A1%E5%BD%A6&ck_sc=1&ck_af=0";
        // url =
        // "http://search.yahoo.com/search?fr=slv1-&p=Mar%eda%20Sharapova%20PICS";
        // url = "http://search.yahoo.co.jp/bin/search?p=%B1%D1%B2%F1%CF%C3";
        // url =
        // "http://search.yahoo.co.jp/bin/search?p=%83l%83o%83_%83J%83%8A%83t%83H%83%8B%83j%83A";
        // url =
        // "http://search.yahoo.co.jp/bin/search?p=%b1%d1%b8%ec%20%cc%f5&hc=5&hs=264&&b=21&h=ds";
        // url =
        // "http://search.yahoo.co.jp/bin/search?p=%A5%DE%A5%EB%A5%BF%A5%F3%A5%DE%A5%EB%A5%B8%A5%A7%A5%E9&src=top&search.x=15&searchy=16";
        // url = "http://search.yahoo.co.jp/bin/search?p=%A4%B5%A4%EC%A4%C6";
        // url =
        // "http://search.yahoo.co.jp/bin/search?p=%a5%a4%a5%ae%a5%ea%a5%b9%ce%b1%b3%d8";
        // url = "http://search.yahoo.co.jp/bin/query?p=%b1%e9%b7%e0
        // %a5%a2%a5%e1%a5%ea%a5%ab %c2%e7%b3%d8&hc=0&hs=0";
        // url =
        // "http://search.yahoo.co.jp/bin/query?p=%a5%c7%a5%b6%a5%a4%a5%ca%a1%bc%a5%ba%bd%bb%c2%f0%a4%c7%b2%f7%c5%ac%a4%cb%ca%eb%a4%e9%a4%bd%a4%a6&hc=0&hs=0";
        // url =
        // "http://search.yahoo.co.jp/bin/query?p=UCLA%a4%c8%a4%cf&hc=0&hs=0";
        // url =
        // "http://search.yahoo.co.jp/bin/query?p=%b1%d1%b2%f1%cf%c3%a5%d9%a5%e9&hc=0&hs=0";
        // url =
        // "http://search.yahoo.co.jp/bin/query?p=%a5%ab%a5%ea%a5%d5%a5%a9%a5%eb%a5%cb%a5%a2
        // %a5%d5%a5%ec%a5%ba%a5%ce %ce%b1%b3%d8&hc=0&hs=0";
        // url =
        // "http://search.yahoo.co.jp/bin/query?p=%d7%a2%c0%a5%cd%a7%b5%aa&hc=0&hs=0";
        // url = "http://my.yahoo.co.jp/bin/query?p=%c5%c5%c4%cc
        // %ce%b1%b3%d8&hc=0&hs=0&&b=41&h=p";
        // url =
        // "http://images.search.yahoo.com/search/images/view?back=http%3a//images.search.yahoo.com/search/images%3fsrch=1%26p=men%2bheadshot%26ei=UTF-8%26n=20%26fl=0&h=292&w=200&imgcurl=www.sercerphotography.com/headshots/men/photos/headshot_27.jpg&imgurl=www.sercerphotography.com/headshots/men/photos/headshot_27.jpg&name=%3cb%3eheadshot%3c/b%3e_27.jpg&p=men
        // headshot&rurl=http%3a//www.sercerphotography.com/headshots/men/headshots_5.htm&rcurl=http%3a//www.sercerphotography.com/headshots/men/headshots_5.htm&type=jpeg&no=14&tt=172";
        // /*not*/ url =
        // "http://bsearch.goo.ne.jp/imgdt.jsp?TURL=http://images-partners.google.com/images?q=tbn:yLW6VJ_VA68J:http://www.inplainsite.org/assets/images/Rock-stryper.jpg&client=nttx-images&IURL=http://www.inplainsite.org/assets/images/Rock-stryper.jpg&ISZ=17&IW=170&IH=136&TW=93&TH=75&EXT=jpg&PURL=http%3A%2F%2Fwww.inplainsite.org%2Fhtml%2Fchristian_rock_stars.html&TL=Rock-%3Cb%3Estryper%3C%2Fb%3E.jpg&ABST=Rock-%3Cb%3Estryper%3C%2Fb%3E.
        // http www inplainsite org assets images Rock %3Cb%3Estryper%3C%2Fb%3E
        // jpg g00g13
        // Christian&REFURL=http%3A%2F%2Fbsearch.goo.ne.jp%2Fimage.jsp%3FFT%3Dall%26FR%3D276%26IMGSZ%3Dsmall%7Cmedium%7Clarge%7Cxlarge%26IMGC%3Dall%26CK%3D0%26QGR%3D1%26REF%3Dindex.jsp%26MT%3D%25A3%25D3%25A3%25D4%25A3%25D2%25A3%25D9%25A3%25D0%25A3%25C5%25A3%25D2%26JP%3D1%26FILTER%3D1%26QGA%3D1%26SM%3DMC%26DC%3D12%26INSITE%3D0%26OCR%3D1&AN=i4&THITS=2,050&IDX=281&MT=%A3%D3%A3%D4%A3%D2%A3%D9%A3%D0%A3%C5%A3%D2&SM=MC&QGA=1&QGR=1&FT=all&DC=12&OCR=1&IMGSZ=small|medium|large|xlarge&IMGC=&CLICK=1";
        // url =
        // "http://msxml.webcrawler.com/info.wbcrwl/search/web/adult%20jokes/1/20/1/-/1/0/1/1/1/1/1/-/-/-/-/-/-/-/-/-/-/-/-/-/-/-/-/-/-/-/-/-/-/-/-/-/-/-/adult%20jokes&splash=unfiltered";
        // url =
        // "http://msxml.webcrawler.com/info.wbcrwl/search/web/adult%20webcam/1/20/1/-/1/0/1/1/1/1/1/-/-/-/-/-/-/-/-/-/-/-/-/-/-/-/-/-/-/-/-/-/-/-/-/-/-/-/adult%20webcam&splash=unfiltered";
        // url =
        // "http://msxml.webcrawler.com/info.wbcrwl/search/web/adult%20cam/1/20/1/-/1/0/1/1/1/1/1/-/-/-/-/-/-/-/-/-/-/-/-/-/-/-/-/-/-/-/-/-/-/-/-/-/-/-/adult%20cam&splash=unfiltered";
        url =
                "http://www.metacrawler.com/info.metac/search/web/hard%20cocks/1/20/1/-/1/0/1/1/1/1/1/-/-/-/-/-/-/-/-/-/-/-/-/-/-/-/-/-/-/-/-/-/-/-/-/-/-/-/hard%20cocks&splash=unfiltere";
        url =
                "http://search.msn.co.jp/results.aspx?q=%89p%89%EF%98b%81@%8Aw%8DZ&FORM=MSNH&v=1&RS=CHECKED&CY=ja&CP=932";
        url =
                "http://www.google.co.jp/search?hl=ja&inlang=ja&ie=Shift_JIS&q=%8C%C2%90l%8F%EE%95%F1%95%DB%8C%EC%8BK%92%E8%81@%89%F0%90%E0&lr=";
        url = "http://find.walla.co.il/?q=%FA%E5%F8%E4&w=%2F0";
        url =
                "http://search.rambler.ru/srch?words=%E8%E3%F0%FB%20%EA%EE%ED%EA%F3%F0%F1%FB&where=1";
        url =
                " http://msxml.excite.com/info.xcite/search/web/%2522pilot%2Blog%2Bbook%2522";
        url =
                "http://www.google.co.jp/search?hl=ja&inlang=ja&ie=Shift_JIS&q=%8C%C2%90l%8F%EE%95%F1%95%DB%8C%EC%8BK%92%E8%81@%89%F0%90%E0&lr=";
        url = "http://comnet.com/tiewroij/wfwef.fef";
        url =
                "http://search.goo.ne.jp/web.jsp?IE=sjis&MT=%83L%83%8B%83g&DC=100";
        url =
                "http://www.yandex.ru/yandsearch?text=%C3%E4%E5%20%F3%E7%ED%E0%F2%FC%20%EF%EE%F1%E5%F9%E0%E5%EC%EE%F1%F2%FC%20%F1%E0%E9%F2%E0&stype=www";
        url =
                "http://www.yandex.ru/yandsearch?ras=1&date=&text=%D0%BA%D0%BB%D1%8E%D1%87%D0%B5%D0%B2%D0%BE%D0%B5%20%D1%81%D0%BB%D0%BE%D0%B2%D0%BE%20%D0%B2%20URL&spcctx=notfar&zone=all&linkto=&wordforms=all&lang=all&within=0&from_day=&from_month=&from_year=&to_day=10&to_month=6&to_year=2005&mime=all&Link=&numdoc=50&site=http%3A%2F%2Fforum.searchengines.ru%2F&ds=";

        url =
                "http://tw.search.yahoo.com/search?p=%E7%A9%BA%E4%B8%AD%E8%8B%B1%E8%AA%9E%E6%95%99%E5%AE%A4%E7%B7%9A%E4%B8%8A%E6%94%B6%E8%81%BD&rs=1&ei=UTF-8&fl=0&meta=vc%3D&fr=fp-tab-web-t";

        url =
                "http://dir.yam.com/bin/search?dest=http%3A%2F%2Fdir.yam.com%2Fbin%2Fsearch&k=%AA%C5%A4%A4%AD%5E%BBy%B1%D0%AB%C7&t=site";
        url = "http://tw.dictionary.yahoo.com/search?p=%B2M%B0%A3"; /*
                                                                     * error
                                                                     * like in
                                                                     * SHIFT JIS
                                                                     */
        url =
                "http://search.msn.com.tw/results.aspx?cp=950&PI=9570&DI=37&FORM=MSNH&q=%A4j%AEa%BB%A1%AD%5E%BBy";
        url =
                "http://www.google.com.tw/search?lr=&hl=zh-TW&oe=UTF-8&ie=zh-TW&q=%A5%FE%A5%C1%AD%5E%C0%CB%A1i%AA%EC%AF%C5%A1j%B9G%AFu%BC%D2%C0%C0%B4%FA%C5%E7%281%29%B3S%AC%C3%AA%A9%28%AA%FECD%29&domains=http%3A%2F%2Fwww.54power.idv.tw";
        // url =
        // "http://is1.websearch.com/_1_9G5UWL04BL8K81__websrch.barweb.50007/search/web/Arcade%2BGame/1/20/1/-/0/0/0/1/1/1&X=1";
        // url =
        // "http://is1.websearch.com/_1_66KTP90O269WK__websrch.barweb.50019/search/web/Download%2BSims%2BFor%2BPC/1/20/1/-/0/0/0/1/1/1&X=1/";
        url =
                "http://search.yahoo.co.jp/search?p=%E3%83%95%E3%82%A3%E3%83%BC%E3%83%89%E3%82%B5%E3%83%83%E3%82%AF%E3%80%80%E3%80%80%E5%B0%82%E9%96%80%E5%BA%97&ei=UTF-8&fr=ybb&fl=0&x=wrt&meta=vc%3D";
        url =
                "http://search.yahoo.co.jp/search?p=%E3%83%95%E3%82%A3%E3%83%BC%E3%83%89%E3%82%B5%E3%83%83%E3%82%AF%E3%80%80%E3%80%80%E5%B0%82%E9%96%80%E5%BA%97&ei=UTF-8&fr=ybb&fl=0&x=wrt&meta=vc%3D";
        url =
                "http://search.yahoo.co.jp/search?p=%A5%A2%A5%F3%A5%C6%A5%A3%A1%BC%A5%AF%A5%AD%A5%EB%A5%C8&fr=top&src=top&search.x=26&search.y=9";
        url =
                "http://ocnsearch.goo.ne.jp/ocn.jsp?encode=euc&SM=MC&DC=10&IE=eucjp&MT=%a5%a2%a5%f3%a5%c6%a5%a3%a1%bc%a5%af%2b%a5%ea%a5%cd%a5%f3";
        url =
                "http://webindex.sanook.com/php/search.php?domains=www.sanook.com&cof=&use_desc=Y&use_keyword=Y&use_title=Y&use_url=Y&ps=20&adv=N&sw=WEBINDEX&q=%CD%CD%E0%C3%E9%B9%B7%EC&x=35&y=12";

        String test = "http://www.costcoconnection.com/connection/200512/?pg=8";
        System.out.println(test.substring(test.lastIndexOf("/", test
                .lastIndexOf("/") - 1) + 1, test.lastIndexOf("?") - 1));

        // url =
        // "http://uk.dir.yahoo.com/Regional/Countries/United_Kingdom/Health/Nutrition/";
        // url = "
        // file://localhost/C:/Documents%20and%20Settings/Yale/My%20Documents/104604.html";
        // url = UrlDecode.decode
        // url =
        // "http://www.domain.com/path/file.php?parentcat=92&parentcatname=Sexy
        // Lingerie&category=146&subcatname=Sets&prodref=321-616-195&ID=574&page=4&source=productscategory";
        //
        // url =
        // "http://www.cafedvd.com/cgi-bin/new/scan/MM=3b8320e3d5d58125cec1df13377ca1d2:45:59:15.html?mv_more_ip=1&mv_nextpage=results_detailed&pf=item&id=bbyaJ6Hc&mv_arg=";
        // url = "Illegal protocol:
        // mhtml:file://c:documents%20and%20settings\\local%20settings\\temporary%20internet%20files.mht!http://www.effenaar.nl/fnr_content_engine/Effenaar_act.php";

        // System.out.println(getUrlMap(url));

        // long now = System.currentTimeMillis();
        // for (int i=0; i<1000; i++)
        // {
        // getUrlMap(url);
        // }
        // System.out.println(System.currentTimeMillis() - now);
        //
        /* */
        // url =
        // "http://www.google.co.uk/search?as_q=hOLIDAYS%20IN%20FUERTEVENTURA&num=10&hl=en&ie=UTF-8&btnG=Google%20Search&as_epq=&as_oq=All%20inclusive&as_eq=&lr=lang_en&as_ft=i&as_filetype=&as_qdr=all&as_occt=any&as_dt=i&as_sitesearch=&safe=active";
        // url = "http://www.google.ca/search?q=urinate
        // standing&hl=en&lr=&safe=off&start=10&sa=N";
        // url =
        // "http://www.eniro.se/query?what=web&partnerid=leta_se&q=sk%E4rmbilder&what=web&lrn=177061453";
        url =
                "http://search.yahoo.co.jp/search?p=%E8%8B%B1%E6%96%87%E3%83%93%E3%82%B8%E3%83%8D%E3%82%B9E%E3%83%A1%E3%83%BC%E3%83%AB%E3%80%80%E3%83%9B%E3%83%86%E3%83%AB%E3%81%AE%E4%BA%88%E7%B4%84&ei=UTF-8&fr=ush-jp_dic&x=wrt";
        @SuppressWarnings("unused")
        UrlPacket up = getUrlPacket(url);


        // UrlPacket up = new UrlPacket(); //testPacket;

        // System.out.println("hostname: " + up.getHostname());
        // UrlPacket up2 = getUrlPacket(url);
        // if (up != null)
        // for (int i=0; i<20000; i++)
        // {
        // net.tinyelements.cache.util.UrlUtil.arm.put(""+i, new Integer(i));
        // }
        // System.out.println(up.toString());
        // System.out.println(up.getRootUrl());
        // System.out.println(up.toStringF());
        /*
         * long now = System.currentTimeMillis(); for (int i=0; i<1000; i++) {
         * up.getSearchTerms(); } System.out.println("time: " +
         * (System.currentTimeMillis() - now) + ", " + up.getSearchTerms()); now =
         * System.currentTimeMillis(); for (int i=0; i<1000; i++) {
         * TxtBean.getSearchString(up.toString()); } System.out.println("time: " +
         * (System.currentTimeMillis() - now));
         */
        // System.out.println(
        // up.toStringF()
        // );
        // System.out.println(
        // up.toString()
        // );
        //    
        // if (up != null)
        // System.out.println("up search: " +
        // up.isSearchEngine() + " , '" +
        // up.getSearchTerms(false) + "'"
        // );
        // if (up != null)
        // System.out.println("txt search: " +
        // up.isSearchEngine() + " , " +
        // TxtBean.getSearchString(up.toString())
        // );
        //
        // System.out.println(up.getTLD());
        /* */
        // System.out.println(
        // up.getOriginalUrl()
        // );
        //    
        /* */
        // System.out.println(
        // up.isMutated()
        // );
        /* */
        //
        // logger.warn(up.toString());
        // logger.warn(up.toStringF());
        // if (up.isMutated() == true) {
        // logger.warn("mutated: o:" + up.getOriginalUrl() + ", s:" +
        // up.toString() + ", e:" + up.toEncodedString() + "\no:" +
        // up.getOriginalUrl() + ", \ns:" + up.toString() + ", \ne:" +
        // up.toEncodedString());
        // }
        //            
        // System.out.println(
        // up.getKeyValueMap());
        // System.out.println(
        // up.getValueSet("/",UrlPacket.PATH));
        // System.out.println(
        // System.currentTimeMillis());
        // System.out.println(
        // getUrlPacket(url).toString().substring(0,
        // getUrlPacket(url).toString().lastIndexOf('/'))
        // );
        // now = System.currentTimeMillis();
        // for (int i=0; i<1000000; i++)
        // {
        // up.getValueSet("/",UrlPacket.PATH);
        // }
        // System.out.println(getUrlPacket(url).getValue("nid"));
        // System.out.println(up.getValue("city","=", "&",UrlPacket.QUERY,
        // false));
        // System.out.println(System.currentTimeMillis() - now);
        // now = System.currentTimeMillis();
        // for (int i=0; i<100; i++)
        // {
        // TxtBean.getValueInUrl(url,"nid");
        // }
        // System.out.println(System.currentTimeMillis() - now);
        // System.out.println(TxtBean.getValueInUrl(url,"nid"));
    }
}