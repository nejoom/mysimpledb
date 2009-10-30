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
package ac.elements.conversion;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.Locale;
import java.util.SimpleTimeZone;
import java.util.TimeZone;

import org.apache.log4j.Logger;

// TODO: Auto-generated Javadoc
/**
 * The Class TypeConverter.
 * 
 * @author <a href="mailto:eddie@tinyelements.com">Eddie Moojen</a>
 */
public class TypeConverter {

    /** The Constant log. */
    private final static Logger logger =
        Logger.getLogger(TypeConverter.class);

    // private static final String hexArray = "0123456789ABCDEF";

    private static final char HYPHEN = '-';

    private static final char UNDERSCORE = '_';

    /**
     * Gets the bool.
     * 
     * @param object
     *            the object
     * @param _default
     *            the _default
     * 
     * @return the bool
     * 
     * @throws NumberFormatException
     *             the number format exception
     */
    public static boolean getBool(Object object, boolean _default)
            throws NumberFormatException {

        if (object == null) {

            // logger.warn("Cannot convert 'null' returning " + _default);
            return _default;

        } else if (object instanceof Boolean) {

            return ((Boolean) object).booleanValue();

        } else if (object instanceof String) {

            return (new Boolean((String) object)).booleanValue();

        } else if (object instanceof Number) {

            return ((Number) object).intValue() != 0;

        }

        throw new NumberFormatException();

    }

    /**
     * Gets the boolean.
     * 
     * @param object
     *            the object
     * 
     * @return the boolean
     * 
     * @throws NumberFormatException
     *             the number format exception
     */
    public static Boolean getBoolean(Object object)
            throws NumberFormatException {

        if (object == null)
            return null;

        if (object instanceof Boolean) {
            return (Boolean) object;
        } else if (object instanceof String) {
            if (((String) object).toLowerCase().indexOf("null") != -1)
                return null;
            return new Boolean((String) object);
        } else if (object instanceof Number) {
            return new Boolean(((Number) object).intValue() != 0);
        }

        throw new NumberFormatException();
    }

    /**
     * Gets the boolean.
     * 
     * @param object
     *            the object
     * @param myBool
     *            the my bool
     * 
     * @return the boolean
     * 
     * @throws NumberFormatException
     *             the number format exception
     */
    public static boolean getBoolean(Object object, boolean myBool)
            throws NumberFormatException {
        Boolean tmpBool = getBoolean(object);
        if (tmpBool == null)
            return myBool;
        else
            return tmpBool.booleanValue();
    }

    /**
     * Gets the date.
     * 
     * @param object
     *            the object
     * 
     * @return the date
     */
    public static Date getDate(Object object) {

        if (object == null)
            return null;

        if (object instanceof Date) {
            return (Date) object;
        } else if (object instanceof Number) {
            return new Date(((Number) object).longValue());
        } else if (object instanceof String) {
            try {
                if (((String) object).toLowerCase().indexOf("null") != -1)
                    return null;
                DateFormat df = DateFormat.getDateInstance();
                return df.parse(object.toString());
            } catch (ParseException pe) {
                // logger.error("Cant find date in " + object);
                return new Date();
            }
        }

        // logger.error("Cant find date in " + object);
        return new Date();
        // throw new ParseException("Unknown object: " + object,
        // -1);
    }

    /**
     * Gets the double.
     * 
     * @param object
     *            the object
     * 
     * @return the double
     */
    public static Double getDouble(Object object) {

        if (object == null)
            return null;

        if (object instanceof Number) {
            return new Double(((Number) object).doubleValue());
        } else if (object instanceof String) {
            if (((String) object).toLowerCase().indexOf("null") != -1)
                return null;
            return new Double((String) object);
        } else if (object instanceof Date) {
            return new Double(((Date) object).getTime());
        }

        throw new NumberFormatException();

    }

    /**
     * Gets the double.
     * 
     * @param object
     *            the object
     * @param defaultDouble
     *            the default double
     * 
     * @return the double
     */
    public static double getDouble(Object object, double defaultDouble) {

        if (object == null)
            return defaultDouble;

        if (object instanceof Number) {
            return ((Number) object).floatValue();
        } else if (object instanceof Date) {
            return ((Date) object).getTime();
        } else if (object instanceof String) {
            try {
                return Double.parseDouble((String) object);
            } catch (NumberFormatException nfe) {
                return defaultDouble;
            }
        }

        return defaultDouble;

    }

    /**
     * Gets the double.
     * 
     * @param object1
     *            the object1
     * @param object2
     *            the object2
     * 
     * @return the double
     */
    public static Double getDouble(Object object1, Object object2) {
        Double intTmp = getDouble(object1);
        if (intTmp == null)
            return getDouble(object2);
        else
            return intTmp;
    }

    /**
     * Gets the float.
     * 
     * @param object
     *            the object
     * 
     * @return the float
     */
    public static Float getFloat(Object object) {

        if (object == null)
            return null;

        if (object instanceof Number) {
            return new Float(((Number) object).floatValue());
        } else if (object instanceof String) {
            if (((String) object).toLowerCase().indexOf("null") != -1)
                return null;
            return new Float((String) object);
        } else if (object instanceof Date) {
            return new Float(((Date) object).getTime());
        }

        throw new NumberFormatException();

    }

    /**
     * Gets the float.
     * 
     * @param object
     *            the object
     * @param defaultFloat
     *            the default float
     * 
     * @return the float
     */
    public static float getFloat(Object object, float defaultFloat) {

        if (object == null)
            return defaultFloat;

        if (object instanceof Number) {
            return ((Number) object).floatValue();
        } else if (object instanceof String) {
            try {
                return Float.parseFloat((String) object);
            } catch (NumberFormatException nfe) {
                return defaultFloat;
            }
        } else if (object instanceof Date) {
            return new Float(((Date) object).getTime());
        }

        return defaultFloat;

    }

    /**
     * Gets the int.
     * 
     * @param object
     *            the object
     * @param _default
     *            the _default
     * 
     * @return the int
     * 
     * @throws NumberFormatException
     *             the number format exception
     */
    public static int getInt(Object object, int _default)
            throws NumberFormatException {
        if (object == null) {
            // logger.warn("Cannot convert 'null' returning " + _default);
            return _default;
        } else if (object instanceof Number) {
            return ((Number) object).intValue();
        } else if (object instanceof String) {
            try {
                return Integer.parseInt((String) object);
            } catch (NumberFormatException nfe) {
                // logger.warn("Cannot convert " + object +
                // ", returning " + _default);
                return _default;
            }
        }

        throw new NumberFormatException();

    }

    /**
     * Gets the integer.
     * 
     * @param object
     *            the object
     * 
     * @return the integer
     */
    public static Integer getInteger(Object object) {

        if (object == null)
            return null;

        if (object instanceof Number) {
            return new Integer(((Number) object).intValue());
        } else if (object instanceof String) {
            if (((String) object).toLowerCase().indexOf("null") != -1)
                return null;
            try {
                return new Integer((String) object);

            } catch (NumberFormatException nfe) {
                return null;
            }
        }

        throw new NumberFormatException();

    }

    /**
     * Gets the integer.
     * 
     * @param object1
     *            the object1
     * @param object2
     *            the object2
     * 
     * @return the integer
     */
    public static Integer getInteger(Object object1, Object object2) {
        Integer intTmp = getInteger(object1);
        if (intTmp == null)
            return getInteger(object2);
        else
            return intTmp;
    }

    /**
     * Gets the locale.
     * 
     * @param localeObject
     *            the locale object
     * @param defaultLocale
     *            the default locale
     * 
     * @return the locale
     */
    public static Locale getLocale(Object localeObject, Object defaultLocale) {
        String locale = null;

        if (defaultLocale == null)
            return null;

        if (localeObject == null)
            locale = defaultLocale.toString();
        else
            locale = localeObject.toString();

        if (localeObject instanceof Locale)
            return (Locale) localeObject;

        String language = locale;
        String country = null;
        String variant = null;

        int index = -1;
        if (((index = locale.indexOf(HYPHEN)) > -1)
                || ((index = locale.indexOf(UNDERSCORE)) > -1)) {

            language = locale.substring(0, index);
            country = locale.substring(index + 1);

            if (((index = country.indexOf(HYPHEN)) > -1)
                    || ((index = country.indexOf(UNDERSCORE)) > -1)) {

                country = locale.substring(0, index);
                variant = locale.substring(index + 1);

            }

        }

        if (country != null && variant == null) {
            return new Locale(language, country);
        } else if (country != null && variant != null) {
            return new Locale(language, country, variant);
        } else {
            return new Locale(language);
        }

    }

    /* get locale, returns default JVM locale if locale is null */
    /**
     * Gets the locale.
     * 
     * @param locale
     *            the locale
     * 
     * @return the locale
     */
    public static Locale getLocale(Object locale) {
        return getLocale(locale, Locale.getDefault().toString());
    }

    /**
     * Gets the long.
     * 
     * @param object
     *            the object
     * 
     * @return the long
     * 
     * @throws NumberFormatException
     *             the number format exception
     */
    public static Long getLong(Object object) throws NumberFormatException {

        if (object == null)
            return null;

        if (object instanceof Number) {
            return new Long(((Number) object).longValue());
        } else if (object instanceof String) {
            return new Long((String) object);
        } else if (object instanceof Date) {
            return new Long(((Date) object).getTime());
        }

        throw new NumberFormatException();

    }

    /**
     * Gets the long.
     * 
     * @param object
     *            the object
     * @param _default
     *            the _default
     * 
     * @return the long
     * 
     * @throws NumberFormatException
     *             the number format exception
     */
    public static long getLong(Object object, long _default)
            throws NumberFormatException {

        if (object == null) {
            // logger.warn("Cannot convert 'null' returning " + _default);
            return _default;
        } else if (object instanceof Number) {
            return ((Number) object).longValue();
        } else if (object instanceof String) {
            try {
                return Long.parseLong((String) object);
            } catch (NumberFormatException nfe) {
                // logger.warn("Cannot convert " + object +
                // ", returning " + _default);
                return _default;
            }
        } else if (object instanceof Date) {
            return ((Date) object).getTime();
        }

        throw new NumberFormatException();

    }

    /**
     * Gets the string.
     * 
     * @param object
     *            the object
     * @param defaultString
     *            the default string
     * 
     * @return the string
     */
    public static String getString(Object object, String defaultString) {

        if (object == null) {

            // logger.warn("Cannot convert " + object +
            // ", returning " + defaultString);

            return defaultString;
        } else
            return object.toString();

    }

    /**
     * Gets the time zone.
     * 
     * @param timeZone
     *            the time zone
     * @param defaultTimeZone
     *            the default time zone
     * 
     * @return the time zone
     */
    public static TimeZone getTimeZone(Object timeZone, Object defaultTimeZone) {
        if (timeZone instanceof TimeZone)
            return (SimpleTimeZone) timeZone;

        if (timeZone == null)
            if (defaultTimeZone != null)
                return TimeZone.getTimeZone(defaultTimeZone.toString());
            else
                return SimpleTimeZone.getDefault();

        try {
            return TimeZone.getTimeZone(timeZone.toString());
        } catch (Exception e) {
            return SimpleTimeZone.getDefault();
        }
    }

    /**
     * Gets the time zone.
     * 
     * @param timeZone
     *            the time zone
     * @param defaultTimeZone
     *            the default time zone
     * 
     * @return the time zone
     */
    public static TimeZone getTimeZone(Object timeZone) {
        return getTimeZone(timeZone, SimpleTimeZone.getDefault());
    }

    /**
     * R10.
     * 
     * @param d
     *            the d
     * 
     * @return the string
     */
    public static String r10(double d) {
        return r10(new Double(d));
    }

    /**
     * R10.
     * 
     * @param object
     *            the object
     * 
     * @return the string
     */
    public static String r10(Object object) {

        if (object == null)
            return null;

        double d;

        if (object instanceof Number) {
            d = new Double(((Number) object).doubleValue());
        } else if (object instanceof String) {
            if (((String) object).toLowerCase().indexOf("null") != -1)
                return null;
            d = new Double((String) object);
        } else {
            throw new NumberFormatException();
        }

        d = Math.round(d * 10);

        return "" + (d / 10);

    }

    public static String getUrl(String urlString) {
        int retries = 0;
        String result = "";
        while (true) {
            try {
                URL url = new URL(urlString);
                BufferedReader rdr =
                        new BufferedReader(new InputStreamReader(url
                                .openStream()));
                String line = rdr.readLine();
                while (line != null) {
                    result += line;
                    line = rdr.readLine();
                }
                return result;
            } catch (IOException ex) {
                if (retries == 5) {
                    logger.debug("Problem getting url content exhausted");
                    return result;
                } else {
                    logger.debug("Problem getting url content retrying...");
                    try {
                        Thread.sleep((int) Math.pow(2.0, retries) * 1000);
                    } catch (InterruptedException e) {
                    }
                    retries++;
                }
            }
        }
    }

    /**
     * Instantiates a new type converter.
     */
    private TypeConverter() {
    }

}// class
