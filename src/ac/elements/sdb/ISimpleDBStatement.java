package ac.elements.sdb;

public interface ISimpleDBStatement {

    /**
     * The Delete operation is a custom utility method that first selects a set
     * of ItemNames that match the query expression, and subsequently deletes
     * this set with the deleteItem method. Delete is similar to the standard
     * SQL DELETE statement.
     * 
     * <code>
     * delete from test where onekey='avalue'
     * </code>
     * 
     * @param deleteExpression
     *            the delete expression used to query the domain for itemNames.
     * 
     * @return the number of itemNames deleted as a SimpleDBDataList
     */
    public SimpleDBDataList setDelete(final String deleteExpression);

    /**
     * The Delete attribute operation is a custom utility method that first
     * selects a set of ItemNames that match the query expression, and
     * subsequently deletes the attributes with the deleteAttributes method.
     * Delete is a special case of the standard SQL DELETE statement.
     * 
     * <code>
     * delete (key1, `key2`) from test where onekey='avalue'
     * </code>
     * 
     * @param deleteExpression
     *            the delete expression used to query the domain for itemNames.
     * 
     * @return the number of itemNames deleted as a SimpleDBDataList
     */
    public SimpleDBDataList setDeleteAttributeWhere(String deleteExpression);

    /**
     * The Delete attribute operation is a mapping to the deleteAttribute action
     * and deletes the attributes expression with the deleteAttributes method.
     * Delete is a special case of the standard SQL DELETE statement.
     * 
     * <code>
     * delete itemName(key1=1.0f, `key2`='myvalue')
     * </code>
     * 
     * @param deleteExpression
     *            the delete expression used to query the domain for itemNames.
     * 
     * @return the number of itemNames deleted
     */
    public SimpleDBDataList setDeleteAttribute(String deleteExpression);

    /**
     * The Insert operation is a custom utility method that inserts a set of key
     * value pairs, by parsing the insert statement and uses the
     * batchPutAttributes method to insert the key value pairs. Unlike standard
     * SQL, the item name used is also given. If there is already an item name,
     * the key - value pair is added to the item name.
     * <p>
     * The quoting rules are the same as for the select statement.
     * <p>
     * If no itemName is given then a universally unique identifiers (UUID) is
     * generated for the item name, a guarantee for no collisions of the items
     * inserted. For the rest the insert is similar to the standard SQL INSERT
     * statement. Example strings follow (no new line characters should be
     * used):
     * 
     * <code>
     * insert into domain (`keyone`, keytwo) 
     *   values aItemName('value1a', 'value2a');
     * insert into domain (keyone, `keytwo`) 
     *   values ('value1a', 'value2a'), ('value1b', 'value2b');
     * </code>
     * 
     * @param insertExpression
     *            the insert expression used to generate key value pairs for the
     *            domain and the itemName.
     * 
     * @return the number of itemNames deleted as a SimpleDBDataList
     */
    public SimpleDBDataList setInsert(String insertExpression);

    /**
     * The Insert operation is a custom utility method that inserts a set of key
     * value pairs, by parsing the insert statement and uses the
     * batchPutAttributes method to insert the key value pairs. Unlike standard
     * SQL, the item name used is also given. If there is already an item name,
     * the key - value pair is added to the item name.
     * <p>
     * The quoting rules are the same as for the standard select statement
     * syntax.
     * <p>
     * If no itemName is given then a universally unique identifiers (UUID) is
     * generated for the item name, a guarantee for no collisions of the items
     * inserted. For the rest the insert is similar to the standard SQL INSERT
     * statement. Example strings follow (no new line characters should be
     * used):
     * 
     * <code>
     * insert into domain (`keyone`, keytwo) 
     *   values ('value1a', 'value2a') WHERE key1='value';
     * </code>
     * 
     * @param insertExpression
     *            the insert expression used to generate key value pairs for the
     *            domain and the itemName.
     * 
     * @return the number of itemNames inserted as a SimpleDBDataList
     */
    public SimpleDBDataList setInsertWhere(String insertExpression);

    /**
     * The Replace operation is a custom utility method that inserts a set of
     * key value pairs, if the key already exists it replaces it with the given
     * key value pair.
     * <p>
     * The code does this by parsing the replace statement and uses the
     * batchPutReplaceAttributes method to insert/ replace the key value pairs.
     * Unlike standard SQL, the item name used can also be given. If there is
     * already an item name, the key - value pair is replaced in the item name.
     * <p>
     * The quoting rules are the same as for the standard select statement
     * syntax.
     * <p>
     * If no itemName is given then a universally unique identifiers (UUID) is
     * generated for the item name, a guarantee for no collisions of the items
     * inserted. For the rest the replace is similar to the standard SQL REPLACE
     * statement. Example strings follow (no new line characters should be
     * used):
     * 
     * <code>
     * replace into domain (`keyone`, keytwo) 
     *   values aItemName('value1a', 'value2a');
     * replace into domain (keyone, `keytwo`) 
     *   values ('value1a', 'value2a'), ('value1b', 'value2b');
     * </code>
     * 
     * @param replaceExpression
     *            the replace expression used to generate key value pairs for
     *            the domain and the itemName.
     * 
     * @return the number of itemNames modified/ inserted as a SimpleDBDataList
     */
    public SimpleDBDataList setReplace(String replaceExpression);

    /**
     * The Replace operation is a custom utility method that replaces a set of
     * key value pairs, by parsing the replace statement and uses the
     * batchPutReplaceAttributes method to replace the key value pairs. Unlike
     * standard SQL.
     * <p>
     * The quoting rules are the same as for the standard select statement
     * syntax.
     * <p>
     * The replace is similar to the standard MYSQL REPLACE statement except
     * that it expects a where clause to select the itemNames to work on.
     * Example strings follow:
     * 
     * <code>
     * replace into domain (`keyone`, keytwo) 
     *   values ('value1a', 'value2a') WHERE key1='value';
     * </code>
     * 
     * @param replaceExpression
     *            the replace expression used to replace key value pairs.
     * 
     * @return the number of itemNames modified/ inserted as a SimpleDBDataList
     */
    public SimpleDBDataList setReplaceWhere(String replaceExpression);

}
