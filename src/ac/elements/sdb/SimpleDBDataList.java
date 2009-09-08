/*
 * 
 */
package ac.elements.sdb;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

// TODO: Auto-generated Javadoc
/**
 * The Class SimpleDBDataList.
 */
public class SimpleDBDataList implements List<SimpleDBMap> {

    /** The backing dataList. */
    private final List<SimpleDBMap> dataList = new ArrayList<SimpleDBMap>();

    /** The next token. */
    private String nextToken;

    /** The requestId. */
    private String requestId;

    /** The boxUsage. */
    private String boxUsage;

    /** The domain name. */
    private String domainName;

    private long responseTime = 0;

    // private static final int MAX_ITEMS = 25;

    /**
     * Gets the next token. An opaque token indicating that more than
     * MaxNumberOfItems matched, the response size exceeded 1 megabyte, or the
     * execution time exceeded 5 seconds.
     * 
     * @return the next token
     */
    public String getNextToken() {
        return nextToken;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.List#add(int, java.lang.Object)
     */
    public void add(int index, SimpleDBMap element) {
        // if (index > MAX_ITEMS) {
        // throw new RuntimeException(
        // "Amazon documentation as of 30-07-2009 says: Too many "
        // + "items in a single call. Up to 25 items per "
        // + "call allowed. Won't add more!");
        // }
        dataList.add(index, element);
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.List#addAll(int, java.util.Collection)
     */
    public boolean addAll(int index, Collection<? extends SimpleDBMap> c) {
        return dataList.addAll(index, c);
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.List#get(int)
     */
    public SimpleDBMap get(int index) {
        return dataList.get(index);
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.List#indexOf(java.lang.Object)
     */
    public int indexOf(Object o) {
        return dataList.indexOf(o);
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.List#lastIndexOf(java.lang.Object)
     */
    public int lastIndexOf(Object o) {
        return dataList.lastIndexOf(o);
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.List#listIterator()
     */
    public ListIterator<SimpleDBMap> listIterator() {
        return dataList.listIterator();
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.List#listIterator(int)
     */
    public ListIterator<SimpleDBMap> listIterator(int index) {
        return dataList.listIterator(index);
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.List#remove(int)
     */
    public SimpleDBMap remove(int index) {
        return dataList.remove(index);
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.List#set(int, java.lang.Object)
     */
    public SimpleDBMap set(int index, SimpleDBMap element) {
        return dataList.set(index, element);
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.List#subList(int, int)
     */
    public List<SimpleDBMap> subList(int fromIndex, int toIndex) {
        return dataList.subList(fromIndex, toIndex);
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.List#add(java.lang.Object)
     */
    public boolean add(SimpleDBMap o) {
        // if (size() > MAX_ITEMS) {
        // throw new RuntimeException(
        // "Amazon documentation as of 30-07-2009 says: Too many "
        // + "items in a single call. Up to 25 items per "
        // + "call allowed. Won't add more!");
        // }
        return dataList.add(o);
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.List#addAll(java.util.Collection)
     */
    public boolean addAll(Collection<? extends SimpleDBMap> c) {
        return dataList.addAll(c);
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.List#clear()
     */
    public void clear() {
        dataList.clear();
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.List#contains(java.lang.Object)
     */
    public boolean contains(Object o) {
        return dataList.contains(o);
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.List#containsAll(java.util.Collection)
     */
    public boolean containsAll(Collection<?> c) {
        return dataList.containsAll(c);
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.List#isEmpty()
     */
    public boolean isEmpty() {
        return dataList.isEmpty();
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.List#iterator()
     */
    public Iterator<SimpleDBMap> iterator() {
        return dataList.iterator();
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.List#remove(java.lang.Object)
     */
    public boolean remove(Object o) {
        return dataList.remove(o);
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.List#removeAll(java.util.Collection)
     */
    public boolean removeAll(Collection<?> c) {
        return dataList.removeAll(c);
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.List#retainAll(java.util.Collection)
     */
    public boolean retainAll(Collection<?> c) {
        return dataList.retainAll(c);
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.List#size()
     */
    public int size() {
        return dataList.size();
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.List#toArray()
     */
    public Object[] toArray() {
        return dataList.toArray();
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.List#toArray(T[])
     */
    public <T> T[] toArray(T[] a) {
        return dataList.toArray(a);
    }

    /**
     * Sets the response time.
     * 
     * @param responseTime
     *            the response time
     */
    protected void setResponseTime(long responseTime) {
        this.responseTime = responseTime;
    }

    /**
     * Gets the response time.
     * 
     */
    public long getResponseTime() {
        return responseTime;
    }

    /**
     * Gets the domain name.
     * 
     * @return the domain name
     */
    public String getDomainName() {
        return domainName;
    }

    /**
     * Sets the domain name.
     * 
     * @param domainName
     *            the new domain name
     */
    public void setDomainName(String domainName) {
        this.domainName = domainName;
    }

    /**
     * Sets the nextToken.
     * 
     * @param nextToken
     *            the next token
     */
    protected void setNextToken(String nextToken) {
        this.nextToken = nextToken;
    }

    /**
     * Sets the requestId.
     * 
     * @param requestId
     *            the requestId
     */
    protected void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    /**
     * Sets the boxUsage.
     * 
     * @param boxUsage
     *            the boxUsage
     */
    protected void setBoxUsage(String boxUsage) {
        this.boxUsage = boxUsage;
    }

    /**
     * Gets the requestId.
     */
    public String getRequestId() {
        return requestId;
    }

    /**
     * Gets the boxUsage.
     */
    public String getBoxUsage() {
        return boxUsage;
    }

    private static final String newLine = System.getProperty("line.separator");

    public String toString() {
        String result = "";
        for (Iterator<SimpleDBMap> items = iterator(); items.hasNext();) {
            SimpleDBMap map = items.next();
            System.out.println(map.toStringF());
            result += map.toStringF() + newLine;
        }
        return result;
    }

    public List getItemNames() {
        Iterator mapIterator = dataList.iterator();
        List<String> itemNames = new ArrayList<String>();
        while(mapIterator.hasNext()) {
            SimpleDBMap map = (SimpleDBMap) mapIterator.next();
            String itemName = (String) map.getItemName();
            itemNames.add(itemName);
        }
        return itemNames;
    }

    public Set getAttributes() {
        Set allAttributes = new LinkedHashSet();
        Iterator mapIterator = dataList.iterator();
        List<String> itemNames = new ArrayList<String>();
        while(mapIterator.hasNext()) {
            SimpleDBMap map = (SimpleDBMap) mapIterator.next();
            Set attributes = map.keySet();
            allAttributes.addAll(attributes);
        }
        return allAttributes;
    }
}
