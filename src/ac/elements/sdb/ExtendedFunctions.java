/*
 */
package ac.elements.sdb;

import java.io.IOException;
import java.io.StringReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;
import org.apache.taglibs.standard.functions.Functions;
import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import ac.elements.parser.SimpleDBConverter;
import ac.elements.parser.UrlPacket;
import ac.elements.parser.UrlParser;

// TODO: Auto-generated Javadoc
/**
 * <p>
 * JSTL Functions extended
 * </p>
 * .
 */
public class ExtendedFunctions extends Functions {

    /** The Constant ELLIPSIS. */
    private final static String ELLIPSIS = "&hellip;";

    private final static Pattern doubleSpaceChars = Pattern.compile("\\s+");

    /**
     * Capitalize.
     * 
     * @param input
     *            the input
     * 
     * @return the string
     */
    public static String capitalize(String input) {
        if (input == null)
            return input;
        input = input.toLowerCase();
        return WordUtils.capitalize(input);
    }

    /**
     * Chop url.§
     * 
     * @param input
     *            the input
     * @param maxLength
     *            the max length
     * 
     * @return the string
     */
    public static String chopUrl(String input, int maxLength) {
        if (input.length() < maxLength) {
            return input;
        }

        UrlPacket urlPacket = UrlParser.getUrlPacket(input);

        String domain = urlPacket.getDomain();

        if (domain.length() > maxLength) {
            return domain.substring(0, maxLength).concat(ELLIPSIS);
        }

        String path = urlPacket.getPath();
        String file = urlPacket.getFile();
        String query = urlPacket.getQuery();
        if (query.length() > 0)
            query = "?" + query;

        if (domain.length() + path.length() + file.length() + query.length() < maxLength) {
            return domain + path + file + query;
        } else if (domain.length() + file.length() + query.length() < maxLength) {
            String token = "";
            if (path.length() > 0)
                token = ELLIPSIS + "/";
            return domain + token + file + query;
        } else if (domain.length() + file.length() < maxLength) {
            String token = "/";
            if (path.length() > 1)
                token = ELLIPSIS + "/";
            return domain + token + file;
        } else if (domain.length() + file.length() > maxLength) {
            String token = "/";

            if (path.length() > 1)
                token = ELLIPSIS + "/";

            int room = maxLength - domain.length();
            if (room < 0) {
                return domain;
            } else {
                String fileLeft = file.substring(0, room);
                return domain + token + fileLeft + ELLIPSIS;
            }
        }

        return domain;

    }

    /**
     * Escape html.
     * 
     * @param str
     *            the str
     * 
     * @return the string
     */
    public static String escapeHtml(String str) {
        return StringEscapeUtils.escapeHtml(str);
    }

    /**
     * Escape java.
     * 
     * @param str
     *            the str
     * 
     * @return the string
     */
    public static String escapeJava(String str) {
        return StringEscapeUtils.escapeJava(str);
    }

    /**
     * Escape java script.
     * 
     * @param input
     *            the input string
     * 
     * @return the string
     */
    public static String escapeJavaScript(String input) {
        // input = input.replace("\"", "\\x22");
        // input = input.replace("\'", "\\x27");
        return StringEscapeUtils.escapeJavaScript(input);
    }

    /**
     * Escape sql.
     * 
     * @param str
     *            the str
     * 
     * @return the string
     */
    public static String escapeSql(String str) {
        return StringEscapeUtils.escapeSql(str);
    }

    /**
     * Escape unicode for javascript use.
     * 
     * @param unicode
     *            the unicode to be escaped
     * 
     * @return the resulting string used in javascript quotes
     */
    public static String escUniJs(String unicode) {
        unicode = unicode.replace("\\", "\\x5c");
        unicode = unicode.replace("\"", "\\x22");
        unicode = unicode.replace("'", "\\x27");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < unicode.length(); i++) {
            char c = unicode.charAt(i);
            if (c <= 0x7E) {
                sb.append(c);
            } else {
                sb.append(String.format("\\u%04X", (int) c));
            }
        }
        return sb.toString();
    }

    /**
     * Decode the object and then escape the unicode for javascript use.
     * 
     * @param unicode
     *            the unicode to be escaped
     * 
     * @return the resulting string used in javascript quotes
     */
    public static String decodeEscUniJs(Object object) {
        String unicode = SimpleDBConverter.encodeValue(object);
        unicode = unicode.replace("\\", "\\x5c");
        unicode = unicode.replace("\"", "\\x22");
        unicode = unicode.replace("'", "\\x27");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < unicode.length(); i++) {
            char c = unicode.charAt(i);
            if (c <= 0x7E) {
                sb.append(c);
            } else {
                sb.append(String.format("\\u%04X", (int) c));
            }
        }
        return sb.toString();
    }

    public static String formatBytes(String byteString) {

        int intBytes = 0;
        try {
            intBytes = Integer.parseInt(byteString);
        } catch (NumberFormatException e) {
            System.out.println(e);
            return byteString;
        }
        if (!byteString.equals("" + intBytes)) {
            System.out.println(intBytes + "!=" + byteString);
            return byteString;
        }
        double bytes = (double) intBytes;
        double KB = bytes / 1024d;
        double MB = bytes / (1024d * 1024d);
        double GB = bytes / (1024d * 1024d * 1024d);
        if (GB > 1d)
            return (Math.round(GB * 10d) / 10d) + " Gb";
        if (MB > 1d)
            return (Math.round(MB * 10d) / 10d) + " Mb";
        if (KB > 1d)
            return (Math.round(KB * 10d) / 10d) + " Kb";
        else
            return intBytes + " bytes";
    }

    public static String formatXml(String xml) {

        if (xml == null || xml.trim().equals("")) {
            return null;
        }

        SAXBuilder builder = new SAXBuilder();
        Document document = null;
        try {
            document = (Document) builder.build(new StringReader(xml));
        } catch (JDOMException e) {
            e.printStackTrace();
            return xml;
        } catch (IOException e) {
            e.printStackTrace();
            return xml;
        } catch (Exception e) {
            e.printStackTrace();
            return xml;
        }

        XMLOutputter out = new XMLOutputter();
        out.setFormat(Format.getPrettyFormat());
        xml = out.outputString(document);
        xml = ExtendedFunctions.escapeXml(xml);

        return (xml);
    }

    /**
     * The main method.
     * 
     * @param args
     *            the arguments
     */
    public static void main(String[] args) {

        String sql =
                "select * from domain where `this`='test' and this2=\"test\"";

        System.out.println(ExtendedFunctions.escapeSql(sql));

        String unformattedXml =
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?><QueryMessage\n"
                        + "        xmlns=\"http://www.SDMX.org/resources/SDMXML/schemas/v2_0/message\"\n"
                        + "        xmlns:query=\"http://www.SDMX.org/resources/SDMXML/schemas/v2_0/query\">\n"
                        + "    <Query>\n"
                        + "        <query:CategorySchemeWhere>\n"
                        + "   \t\t\t\t\t         <query:AgencyID>ECB\n\n\n\n</query:AgencyID>\n"
                        + "        </query:CategorySchemeWhere>\n"
                        + "    </Query>\n\n\n\n\n" + "</QueryMessage>";

        System.out.println(ExtendedFunctions.formatXml(unformattedXml));

        String company = "international business MACHINES. iag is here.";
        System.out.println(ExtendedFunctions.capitalize(company));

        String url = "\u1087\u1088\u1077\u1076\u1089";

        System.out.println(url);
        System.out.println(ExtendedFunctions.escUniJs(url));
        System.out.println(ExtendedFunctions.toUnicodeHTML(url));
        ExtendedFunctions.trim(url);
        url =
                "http://news.google.com/news?hl=en&client=safari&rls=en-us&q=get%20part%20of%20paragraph%20words%20google%20algorithm&um=1&ie=UTF-8&sa=N&tab=wn";
        System.out.println(ExtendedFunctions.chopUrl(url, 30));
        url = "http://www.pcplus.co.uk/node/3061/";
        System.out.println(ExtendedFunctions.chopUrl(url, 30));
        url =
                "http://www.opent.net/forum/security-problem-with-ot-script-on-ssl-t159.html";
        System.out.println(ExtendedFunctions.chopUrl(url, 30));
        System.out.println(ExtendedFunctions
                .escapeJavaScript("escapeJavas'\"cript"));
        System.err.println(StringUtils.abbreviate("Take time off working", 0,
                10));
        System.err.println(StringUtils.capitalize("how is vandersar doing?"));
        String unescapedJava = "Are you \" for real?";
        System.err.println(StringEscapeUtils.escapeJava(unescapedJava));

        String unescapedJavaScript = "What's in a name?";
        System.err.println(ExtendedFunctions
                .escapeJavaScript(unescapedJavaScript));

        String unescapedSql = "Mc'Williams";
        System.err.println(StringEscapeUtils.escapeSql(unescapedSql));

        String unescapedXML = "<data>";
        System.err.println(StringEscapeUtils.escapeXml(unescapedXML));

        String unescapedHTML = "the data is <data>";
        System.err.println(StringEscapeUtils.escapeHtml(unescapedHTML));
        System.err.println(WordUtils.capitalize(unescapedHTML));
        System.err.println(WordUtils.swapCase(unescapedHTML));
    }

    /**
     * Within a specified input string, replaces all strings that match a
     * regular expression pattern with a specified replacement string.
     * 
     * @param input
     *            the input string (eg "aabfooaabfooabfoob")
     * @param regex
     *            the regular expression (eg "a*b")
     * @param replace
     *            the replacement string (eg "-")
     * 
     * @return the resulting string (eg "-foo-foo-foo-")
     */
    public static String regex(String input, String regex, String replace) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(input);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(sb, replace);
        }
        return sb.toString();
    }

    /**
     * 
     * This method ensures that the output String has only valid XML unicode
     * characters as specified by the
     * 
     * XML 1.0 standard. For reference, please see the
     * 
     * standard. This method will return an empty String if the input is null or
     * empty.
     * 
     * 
     * @author Donoiu Cristian, GPL
     * 
     * @param The
     *            String whose non-valid characters we want to remove.
     * 
     * @return The in String, stripped of non-valid characters.
     * @author 
     *         http://cse-mjmcl.cse.bris.ac.uk/blog/2007/02/14/1171465494443.html
     */
    public static String stripNonValidXML(String s) {
        // Used to hold the output.
        StringBuilder out = new StringBuilder();

        // Used to reference the current character.
        int codePoint;

        // This is actualy one unicode character,
        // represented by two code units!!!.
        // String ss = "\ud801\udc00";
        // System.out.println(ss.codePointCount(0, ss.length()));// See: 1

        int i = 0;

        while (i < s.length()) {

            // System.out.println("i=" + i);

            // This is the unicode code of the character.
            codePoint = s.codePointAt(i);

            // Consider testing larger ranges first to improve speed.
            if ((codePoint == 0x9) || (codePoint == 0xA) || (codePoint == 0xD)
                    || ((codePoint >= 0x20) && (codePoint <= 0xD7FF))
                    || ((codePoint >= 0xE000) && (codePoint <= 0xFFFD))
                    || ((codePoint >= 0x10000) && (codePoint <= 0x10FFFF))) {

                out.append(Character.toChars(codePoint));

            }

            // Increment with the number of code units(java chars) needed to
            // represent a Unicode char.
            i += Character.charCount(codePoint);

        }

        return out.toString();

    }

    /**
     * Swap case.
     * 
     * @param str
     *            the str
     * 
     * @return the string
     */
    public static String swapCase(String str) {
        return WordUtils.swapCase(str);
    }

    /**
     * Trim a sentence also replacing all double spaces by single ones.
     * 
     * @param str
     *            the str
     * 
     * @return the string
     */
    public static String trimSentence(String str) {

        // replace new line characters
        String text = str.replace('\n', ' ').replace('\r', ' ');

        // replace double spaces by single ones
        text = doubleSpaceChars.matcher(str).replaceAll(" ");

        // trim
        text = text.trim();
        return text;
    }

    /**
     * Trim all occurences of the supplied character from the given String.
     * 
     * @param str
     *            the String to check
     * @param ter
     *            the character to be trimmed
     * @return the trimmed String
     */
    public static String trimCharacter(String str, char character) {
        if (str == null || str.length() == 0) {
            return str;
        }
        StringBuffer buf = new StringBuffer(str);
        while (buf.length() > 0 && buf.charAt(0) == character) {
            buf.deleteCharAt(0);
        }

        while (buf.length() > 0 && buf.charAt(buf.length() - 1) == character) {
            buf.deleteCharAt(buf.length() - 1);
        }
        return buf.toString();
    }

    /**
     * To unicode html.
     * 
     * @param unicode
     *            the unicode
     * 
     * @return the string
     */
    public static String toUnicodeHTML(String unicode) {
        StringBuffer sb = new StringBuffer(8);
        char c;
        for (int i = 0; i < unicode.length(); i++) {
            c = unicode.charAt(i);
            int value = (int) c;
            String val = "" + value;
            String padding = "0000";
            val = padding.substring(val.length()) + val;
            if (c <= 0x7E)
                sb.append(c);
            else
                sb.append("&#" + val + ";");
        }
        return sb.toString();
    }

    /**
     * Uncapitalize.
     * 
     * @param str
     *            the str
     * 
     * @return the string
     */
    public static String uncapitalize(String str) {
        return WordUtils.uncapitalize(str);
    }

    /**
     * Unescape html.
     * 
     * @param str
     *            the str
     * 
     * @return the string
     */
    public static String unescapeHtml(String str) {
        return StringEscapeUtils.unescapeHtml(str);
    }

    /**
     * Unescape java.
     * 
     * @param str
     *            the str
     * 
     * @return the string
     */
    public static String unescapeJava(String str) {
        return StringEscapeUtils.unescapeJava(str);
    }

    /**
     * Unescape java script.
     * 
     * @param str
     *            the str
     * 
     * @return the string
     */
    public static String unescapeJavaScript(String str) {
        return StringEscapeUtils.unescapeJavaScript(str);
    }

    /**
     * Unescape xml.
     * 
     * @param str
     *            the str
     * 
     * @return the string
     */
    public static String unescapeXml(String str) {
        return StringEscapeUtils.unescapeXml(str);
    }

}