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

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.UUID;

/**
 * The Class SimpleDBMap.
 */
@SuppressWarnings("unchecked")
public class SimpleDBMap implements Map {

    /** The m. */
    Map<String, LinkedHashSet<Object>> map =
            new TreeMap<String, LinkedHashSet<Object>>();

    private Object itemName;

    /**
     * The main method.
     * 
     * @param args
     *            the arguments
     */
    public static void main(String[] args) {
        SimpleDBMap map = new SimpleDBMap();
        map.put("one", "one");
        map.put("one", "one");
        map.put("one", "two");
        map.put("two", "two");
        map.put("three", "two");
        map.put("four", null);
        map.put("four", "asd");
        System.out.println(map.containsKey("four"));
        System.out.println(map.get("one"));
        map.clear();
        System.out.println(map.get("one"));
        System.out.println(map.containsValue(null));

        map.put("longmax", Long.MAX_VALUE);
        map.put("longmin", Long.MIN_VALUE);
        System.out.println(map.get("longmax"));
        System.out.println(map.get("longmin"));
        // System.out
        // .println(decodeValue((String) map.get("longmax").toArray()[0]));
        // System.out
        // .println(decodeValue((String) map.get("longmin").toArray()[0]));
        System.out.println("************");
        map.put("longmax", 6l);
        map.put("longmin", 7l);
        System.out.println(map.get("longmax"));
        System.out.println(map.get("longmin"));
        map.put("intmax", Integer.MAX_VALUE);
        map.put("intmin", Integer.MIN_VALUE);
        System.out.println(map.get("intmax"));
        System.out.println(map.get("intmin"));
        // System.out
        // .println(decodeValue((String) map.get("intmax").toArray()[0]));
        // System.out
        // .println(decodeValue((String) map.get("intmin").toArray()[0]));
        System.out.println("************");
        map.put("shortmax", Short.MAX_VALUE);
        map.put("shortmin", Short.MIN_VALUE);
        System.out.println(map.get("shortmax"));
        System.out.println(map.get("shortmin"));

        map.put("floatmax", 345.4353678d);
        map.put("floatmax4", 345.4353379d);
        map.put("floatmin", Double.MIN_VALUE);
        map.put("floatmin3", 0d);
        map.put("floatmax2", Double.MAX_VALUE / 89);
        System.out.println("floatmax2" + Double.MAX_VALUE / 89);
        map.put("floatmin2", Double.MAX_VALUE / 4);
        map.put("floatmax1", -Double.MAX_VALUE / 4);
        System.out.println("floatmax1" + (-Double.MAX_VALUE / 4));
        map.put("floatmin1", -Double.MIN_VALUE);
        System.out.println(map.get("floatmax1"));
        System.out.println(map.get("floatmin1"));
        System.out.println(map.get("floatmin3"));
        System.out.println(map.get("floatmin"));
        System.out.println(map.get("floatmax2"));
        System.out.println(map.get("floatmin2"));
        System.out.println(map.get("floatmax"));
        System.out.println(map.get("floatmax4"));
        // System.out
        // .println(decodeValue((String) map.get("floatmax2").toArray()[0]));
        // System.out
        // .println(decodeValue((String) map.get("floatmax1").toArray()[0]));
        // System.out
        // .println(decodeValue((String) map.get("floatmin3").toArray()[0]));
        System.out.println("************");

    }

    /**
     * Constructs a new HashMap with the same mappings as the specified Map.
     * 
     * @param map
     *            the map whose mappings are to be placed in this map.
     */
    public SimpleDBMap(SimpleDBMap map) {
        this.map = new TreeMap(map);
        this.setItemName(map.getItemName());
    }

    public SimpleDBMap() {
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.Map#get(java.lang.Object)
     */
    public LinkedHashSet<Object> get(Object key) {
        return map.get(key);
    }

    /*
     * If value is null, this operation is ignored
     * 
     * @see java.util.Map#put(java.lang.Object, java.lang.Object)
     */
    public LinkedHashSet<Object> put(Object key, Object value) {
        LinkedHashSet<Object> l = map.get(key);
        if (value != null) {
            if (l == null)
                map.put((String) key, l = new LinkedHashSet<Object>());
            l.add(value);
        }
        return l;

    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.Map#clear()
     */
    public void clear() {
        map.clear();
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.Map#containsKey(java.lang.Object)
     */
    public boolean containsKey(Object key) {
        return map.containsKey(key);
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.Map#containsValue(java.lang.Object)
     */
    public boolean containsValue(Object value) {

        Set<String> set = map.keySet();

        Iterator<String> it = ((Set<String>) set).iterator();

        while (it.hasNext()) {
            String key = it.next();
            LinkedHashSet<Object> valueSet = map.get(key);
            if (valueSet.contains(value))
                return true;
        }
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.Map#entrySet()
     */
    public Set<Map.Entry<String, LinkedHashSet<Object>>> entrySet() {
        return map.entrySet();
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.Map#isEmpty()
     */
    public boolean isEmpty() {
        return map.isEmpty();
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.Map#keySet()
     */
    public Set<String> keySet() {
        return map.keySet();
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.Map#remove(java.lang.Object)
     */
    public Object remove(Object key) {
        return map.remove(key);
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.Map#size()
     */
    public int size() {
        return map.size();
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.Map#values()
     */
    public Collection<LinkedHashSet<Object>> values() {
        return map.values();
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.Map#putAll(java.util.Map)
     */
    public void putAll(Map t) {
        map.putAll(t);
    }

    /**
     * Gets the item name.
     * 
     * @return the item name, if the item name null then one is generated and
     *         returned.
     */
    public Object getItemName() {
        if (itemName == null) {
            itemName = UUID.randomUUID().toString();
        }
        return itemName;
    }

    /*
     */
    public void setItemName(Object itemName) {
        this.itemName = itemName;
    }

    public String toString() {
        if (itemName == null)
            return map.toString();
        else
            return itemName.toString();
    }

    public String toStringF() {
        if (itemName == null)
            return map.toString();
        else
            return itemName + ":" + map.toString();
    }

}