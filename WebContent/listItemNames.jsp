<%@ page
    language="java"
    contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"
    errorPage="signature.jsp"
    import="java.util.*,ac.elements.sdb.*,ac.elements.conf.*,ac.elements.parser.*"%><%@ 
taglib
    uri="http://java.sun.com/jstl/core"
    prefix="c"%><%@ 
taglib
    uri="http://java.sun.com/jstl/fmt"
    prefix="fmt"%><%@  
taglib
    uri="http://ac.elements/jsp/jstl/functions"
    prefix="fn"%>
<%
    /**
     * The Access Key ID is associated with your AWS account. You include it in
     * AWS service requests to identify yourself as the sender of the request.
     * 
     * The Access Key ID is not a secret, and anyone could use your Access Key
     * ID in requests to AWS.
     */
    String accessKeyId =
            Configuration.getInstance().getValue("aws",
                    "AWSAccessKeyId");

    /**
     * To provide proof that you truly are the sender of the request, you also
     * include a digital signature calculated using your Secret Access Key.
     */
    String secretAccessKey =
            Configuration.getInstance().getValue("aws",
                    "SecretAccessKey");

    SimpleDBCollection exampleDB =
            new SimpleDBCollection(accessKeyId, secretAccessKey);

    String itemName = null;
    SimpleDBMap map = null;
    SimpleDBDataList list = null;
    String select = null;

    //System.out.println("domainName");
    String domainName = request.getParameter("domainName");
    int domainsSize = 0;
    if (domainName == null) {
        domainsSize = exampleDB.getDomainsAsList().size();
        if (domainsSize > 0) {
            domainName =
                    (String) exampleDB.getDomainsAsList().get(0)
                            .getItemName();
        }
    }
    request.setAttribute("domain", domainName);

    //map next/ previous token pairs
    String currentToken = request.getParameter("itemNextToken");

    // need to reformat to get put of new line characters 
    // (javascript dont like)
    if (currentToken != null) {
        currentToken = currentToken.replace('@', '\n');

        //in javascript sometimes the + get lost as spaces.
        currentToken = currentToken.replace(' ', '+');
    }

    //System.out.println("Actions");
    if (request.getParameter("Action") != null
            && request.getParameter("Action").equals("exploreDomain")) {
        //System.out.println("exploreDomain");
        //if we load this screen, then get rid of select in session
        session.setAttribute("select", null);
    } else if (request.getParameter("Action") != null
            && request.getParameter("Action").equals("createItem")
            && request.getParameter("itemName") != null) {

        //System.out.println("createItem");
        itemName = request.getParameter("itemName");
        String keyName = request.getParameter("keyName");
        String value = request.getParameter("value");

        map = new SimpleDBMap();
        map.put(keyName, value);
        //System.out.println(exampleDB.putAttributes(domainName,
        //        itemName, map));
        exampleDB.putAttributes(domainName, itemName, map);

        String itemNew = request.getParameter("itemNew");

        if (itemNew != null) {
            //if we load this screen, then get rid of select in session
            session.setAttribute("select", null);
        } else {

            //if there is a select statement in session then use it
            if (session.getAttribute("select") != null) {
                request.setAttribute("itemList", exampleDB.getSelect(
                        (String) session.getAttribute("select"), null));
            }

        }
    } else if (request.getParameter("Action") != null
            && request.getParameter("Action").equals("deleteItem")
            && request.getParameter("deleteItem") != null) {
        itemName = request.getParameter("deleteItem");

        //System.out.println("deleteItem");
        exampleDB.deleteItem(domainName, itemName);

        //if there is a select statement in session then use it
        if (session.getAttribute("select") != null) {
            request.setAttribute("itemList", exampleDB.getSelect(
                    (String) session.getAttribute("select"), null));
        }
    } else if (request.getParameter("Action") != null
            && request.getParameter("Action").equals("deleteValueKey")
            && request.getParameter("itemName") != null
            && request.getParameter("keyName") != null
            && request.getParameter("value") != null
            && request.getParameter("domainName") != null) {

        //System.out.println("deleteValueKey");
        itemName = request.getParameter("itemName");

        String keyName = request.getParameter("keyName");
        String value = request.getParameter("value");

        domainName = request.getParameter("domainName");
        map = new SimpleDBMap();
        map.put(keyName, value);
        map.setItemName(itemName);
        //System.out.println(exampleDB.deleteAttributes(domainName,
        //        itemName, map));
        exampleDB.deleteAttributes(domainName, map);

        //if there is a select statement in session then use it
        if (session.getAttribute("select") != null) {
            request.setAttribute("itemList", exampleDB.getSelect(
                    (String) session.getAttribute("select"), null));
        }

    } else if (request.getParameter("Action") != null
            && request.getParameter("Action").equals("select")
            && request.getParameter("select") != null) {

        //System.out.println("select");
        select = ExtendedFunctions.trim(request.getParameter("select"));
        //result = exampleDB.select(select, null);
        //request.setAttribute("result", result);

        //System.out.println("hi: " + currentToken);

        list = exampleDB.setExcecute(select, null, currentToken);

        domainName = list.getDomainName();

        request.setAttribute("domain", domainName);
        request.setAttribute("itemList", list);

        //save select statement between sessions
        session.setAttribute("select", select);

        // System.out.println(list.size());

        //check if we need to update or insert
        String check =
                "select * from select_history where selectStatement='"
                        + ExtendedFunctions.escapeSql(select)
                        + "' limit 1";

        SimpleDBDataList checkList = exampleDB.getSelect(check, null);

        int count = checkList.size();

        //System.out.println("count: " + count);

        // if select statement is not there, then put it there
        if (count == 0) {
            String itemKey = "" + System.currentTimeMillis();

            String keyName = "modifiedTimeMillies";
            String value = itemKey;
            map = new SimpleDBMap();
            map.put(keyName, value);

            keyName = "hasItems";
            if (list.size() > 0)
                value = "true";
            else
                value = "false";
            map.put(keyName, value);

            keyName = "selectStatement";
            value = select;
            map.put(keyName, value);

            //System.out.println(exampleDB.putAttributes(
            //        "select_history", itemKey, map));
            exampleDB.putAttributes("select_history", itemKey, map);
        } else {

            // otherwise delete it and then put it there
            Object itemKey = null;

            for (int i = 0; i < checkList.size(); i++) {
                itemKey = checkList.get(i).getItemName();
                exampleDB
                        .deleteItem("select_history", (String) itemKey);

                //System.out.println("deleted: " + itemKey);
            }
            String keyName = "modifiedTimeMillies";
            String value = "" + System.currentTimeMillis();
            map = new SimpleDBMap();
            map.put(keyName, value);

            keyName = "hasItems";
            if (list.size() > 0)
                value = "true";
            else
                value = "false";
            map.put(keyName, value);

            keyName = "selectStatement";
            value = select;
            map.put(keyName, value);

            //replace current item with modified tag
            //System.out.println(exampleDB.putAttributes(
            //        "select_history", itemKey, map));
            exampleDB.putAttributes("select_history", (String) itemKey,
                    map);
        }

        //System.out.println(select);
    }

    //System.out.println("itemList");
    //System.out.println("domainName: " + domainName);
    String restSql = "select * from " + domainName;
    if (request.getAttribute("itemList") == null) {
        System.out.println(exampleDB);
        list = exampleDB.setExcecute(restSql, null, null);
        request.setAttribute("itemList", list);
    }
    //System.out.println("itemList: " + request.getAttribute("itemList"));
    list = (SimpleDBDataList) request.getAttribute("itemList");

    //System.out.println("restSql: '" + restSql + "'");
    //System.out.println("list: " + list);
    //System.out.println("list: " + list.getAttributes());
    //System.out.println("tokens");
    HashMap<String, String> tokens =
            (HashMap<String, String>) session
                    .getAttribute("itemTokens");
    if (tokens == null)
        tokens = new HashMap<String, String>();
    if (currentToken == null) {
        currentToken = "";
    }
    tokens.put(list.getNextToken(), currentToken);
    session.setAttribute("itemTokens", tokens);

    String nextToken = list.getNextToken();
    String previousToken = tokens.get(currentToken);

    // need to reformat to get rid of new line characters 
    // (javascript dont like)
    if (nextToken != null) {
        System.out.println("remember: " + nextToken);
        nextToken = nextToken.replace('\n', '@');
    }
    if (previousToken != null) {
        previousToken = previousToken.replace('\n', '@');
    }
    if (select == null) {
        select = restSql;
    }
    Set attributes = new TreeSet(list.getAttributes());
    request.setAttribute("itemList", list);
    request.setAttribute("attributes", attributes);
    request.setAttribute("nextToken", nextToken);
    request.setAttribute("previousToken", previousToken);
    request.setAttribute("currentToken", currentToken);
    //System.out.println("finished processing");
    //System.out.println(attributes);
%>
<input
    id="copySelect"
    value="${select}"
    type="hidden" />

<fieldset style="border: 2px ridge navy;"><legend>&nbsp;Showing
<%=list.size()%> item(s) in <b>${domain}</b> follow.&nbsp; Response
time: <%=list.getResponseTime()%>[ms] - BoxUsage: <%=list.getBoxUsage()%>
- <%=list.getBoxUsage() == null ? 0 : Math.round(Float
                    .parseFloat(list.getBoxUsage()) * 100000000f) / 100f%>&micro;$</legend>

<%
    if (domainsSize > 0) {
%><div align="right"><span class="jive-paginator">[ <a
    href="?Action=createItem"
    title="Click to add item"
    onclick="popCreateItem('', '${domain}');return false;">create
new item</a> ]&nbsp;&nbsp;</span></div>
<div align="right"><span class="jive-paginator">[<c:choose>
    <c:when test="${previousToken!=null}">
        <a
            href="?Action=select"
            title="Click to view previous list of items"
            onclick="popSelect('${previousToken}');return false;">previous</a>
    </c:when>
    <c:otherwise>previous</c:otherwise>
</c:choose>] [<c:choose>
    <c:when test="${nextToken!=null}">
        <a
            href="?Action=select"
            title="Click to view next list of items"
            onclick="popSelect('${nextToken}');return false;">next</a>
    </c:when>
    <c:otherwise>next</c:otherwise>
</c:choose>]&nbsp;&nbsp;</span></div>
<%
    }
%>
<style>
/* Tables  
-----------------------------------------------------------------------------*/
table { /*border-spacing:  0;
    border-collapse:  collapse;
    border-color: #012f7d;*/
	border: none;
	margin: 5px;
}
</style>
<table>
    <%
        String token = "";
        String sqlAtt = SimpleDBParser.getAttributes(select);
        if (sqlAtt == null) {
            sqlAtt = " * ";
        }
        String sqlWhere = SimpleDBParser.getWhereClause(select);
        if (sqlWhere == null) {
            sqlWhere = "WHERE itemName() is not null";
        } else if (sqlWhere.indexOf("itemName()") == -1) {
            sqlWhere += " AND itemName() is not null";
        }
        String sqlOrder = SimpleDBParser.getOrderClause(select);
        if (sqlOrder == null) {
            sqlOrder = "ORDER BY itemName() ASC";
        } else if (sqlOrder.indexOf(" ASC") != -1) {
            sqlOrder = "ORDER BY itemName() DESC";
        } else if (sqlOrder.indexOf(" DESC") != -1) {
            sqlOrder = "ORDER BY itemName() ASC";
        }
        String sqlLimit = SimpleDBParser.getLimitClause(select);
        if (sqlLimit == null) {
            sqlLimit = "";
        } else {
            sqlLimit = "LIMIT " + sqlLimit;

        }
        if (select != null && select.indexOf("ORDER BY itemName()") != -1) {
            if (select.indexOf(" DESC") != -1) {
                token = "&darr;";
            } else {
                token = "&uarr;";
            }
        }
    %>
    <thead>
        <tr>
            <td>
            <%
                if (list.size() > 0) {
            %><span class="jive-paginator"><a
                title="Click to sort"
                href="?Action=exploreDomain&domainName=${domain}"
                onclick="makeRequestSort('${domain}','<%=sqlAtt.trim()%>','<%=sqlWhere.trim()%>','<%=sqlOrder.trim()%>', '<%=sqlLimit.trim()%>');return false;">
            itemName()</a> <%=token%></span> <%
     } else {
 %>[empty result set, no results]<%
     }
 %>
            </td>
            <c:forEach
                var="attribute"
                items="${attributes}">
                <%
                    token = "";
                        String att = (String) pageContext.getAttribute("attribute");
                        sqlWhere = SimpleDBParser.getWhereClause(select);
                        if (sqlWhere == null) {
                            sqlWhere = "WHERE `" + att + "` IS NOT NULL ";
                        } else if (sqlWhere.indexOf(att) == -1) {
                            sqlWhere += " AND `" + att + "` IS NOT NULL ";
                        }
                        sqlOrder = SimpleDBParser.getOrderClause(select);
                        if (sqlOrder == null) {
                            sqlOrder = "ORDER BY `" + att + "` ASC";
                        } else if (sqlOrder.indexOf(" ASC") != -1) {
                            sqlOrder = "ORDER BY `" + att + "` DESC";
                        } else if (sqlOrder.indexOf(" DESC") != -1) {
                            sqlOrder = "ORDER BY `" + att + "` ASC";
                        }
                        sqlLimit = SimpleDBParser.getLimitClause(select);
                        if (sqlLimit == null) {
                            sqlLimit = "";
                        } else {
                            sqlLimit = "LIMIT " + sqlLimit;
                        }
                        if (select != null
                                && select.indexOf("ORDER BY `" + att + "`") != -1) {
                            if (select.indexOf(" DESC") != -1) {
                                token = "&darr;";
                            } else {
                                token = "&uarr;";
                            }
                        }
                %>

                <td><span class="jive-paginator"><a
                    href="?Action=exploreDomain&domainName=${domain}"
                    title="Click to sort"
                    onclick="makeRequestSort('${domain}','<%=sqlAtt.trim()%>','<%=sqlWhere.trim()%>','<%=sqlOrder.trim()%>', '<%=sqlLimit.trim()%>');return false;">
                ${attribute}</a> <%=token%></span></td>
            </c:forEach>
        </tr>
    </thead>
    <tbody>
        <c:forEach
            items="${itemList}"
            var="item"
            varStatus="status">
            <tr>
                <td><span class="jive-paginator">[<a
                    href="?Action=deleteItem"
                    title="Click to delete item"
                    onclick="popDeleteItem('Delete item ${fn:escUniJs(item)}', 'Are you very sure?', '${fn:escUniJs(item)}', '${fn:escUniJs(domain)}');return false;">x</a>]
                [<a
                    href="?Action=createItem"
                    title="Click to add key value pair"
                    onclick="popCreateItem('${fn:escUniJs(item)}', '${fn:escUniJs(domain)}');return false;">+</a>]</span>
                ${item} <!--  
            
            Loop through attributes (entry)

      --></td>
                <c:forEach
                    var="attribute"
                    items="${attributes}">
                    <td><c:forEach
                        var="value"
                        items="${item[attribute]}">
                    <span class="jive-paginator">[<a
                            href="?Action=deleteValueKey"
                            title="Click to delete key value pair (${value.class}: ${fn:decodeEscUniJs(value)})"
                            onclick="popDeleteValueKey('Are you sure?', 'Delete attribute ${fn:escUniJs(attribute)}?', '${fn:escUniJs(attribute)}', '${fn:decodeEscUniJs(value)}', '${fn:escUniJs(item)}', '${domain}');return false;">x</a>]</span>
                        <!--   ${attribute}: -->${value}
                        </c:forEach></td>
                </c:forEach>
            </tr>
        </c:forEach>
    </tbody>
</table>
</fieldset>

<!-- fieldset style="border: 2px ridge navy;"><legend>&nbsp;Construct
<b>select</b> statement [ <a
    href="http://docs.amazonwebservices.com/AmazonSimpleDB/2009-04-15/DeveloperGuide/index.html?UsingSelect.html"
    title="Click for help"
    target="_blank">help</a> ]&nbsp;</legend>
<div align="center"><textarea
    style="width: 90%;"
    id="select1">
<c:out value='${select}'>
select * from ${domain}  [where expression] [sort_instructions] [limit limit]
</c:out>
    </textarea></div>
<div align="right">[ <a
    href="?Action=submitSelect"
    title="Click to submit the above select statement"
    onclick="popSelect();return false;">submit select statement</a> ]</div>
</fieldset-->

<!-- fieldset style="border: 2px ridge navy;"><legend><b>&nbsp;Debug:</b>&nbsp;</legend>
<div align="left">Action: <%=request.getParameter("Action")%>,
Domain: <%=domainName%> itemName: <%=itemName%> Map: <%=map%><br />
${select}</div>
</ -->

<!-- pre class="brush: xml">
${fn:formatXml(tempxml)}
${fn:formatXml(result)}
</pre -->
