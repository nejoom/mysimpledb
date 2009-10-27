<%@ page
    language="java"
    contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"
    errorPage="signature.jsp"
    import="ac.elements.sdb.SimpleDBImplementationAsync,ac.elements.sdb.collection.SimpleDBDataList,ac.elements.sdb.collection.SimpleDBMap,ac.elements.parser.ExtendedFunctions,ac.elements.conf.Configuration,ac.elements.io.SaveAsFile,ac.elements.io.ImportFile,ac.elements.parser.SimpleDBParser,java.text.SimpleDateFormat,java.util.HashMap,java.util.Set,java.util.TreeSet,java.util.Date"%><%--
 
  Copyright 2008-2009 Elements. All Rights Reserved.
 
  License version: CPAL 1.0
 
  The Original Code is mysimpledb.com code. Please visit mysimpledb.com to see how
  you can contribute and improve this software.
 
  The contents of this file are licensed under the Common Public Attribution
  License Version 1.0 (the "License"); you may not use this file except in
  compliance with the License. You may obtain a copy of the License at
 
     http://mysimpledb.com/license.
     
     --%><%@ 
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

    String SEPARATOR = System.getProperty("file.separator");

    SimpleDBImplementationAsync exampleDB =
            new SimpleDBImplementationAsync(accessKeyId, secretAccessKey);

    String itemName = null;
    SimpleDBMap map = null;
    SimpleDBDataList simpleDBDataList = null;
    String select = null;

    //System.out.println("domainName");
    String domainName = request.getParameter("domainName");
    int domainsSize = exampleDB.getDomainsAsSimpleDBDataList().size();
    if (domainName == null) {

        if (domainsSize > 0) {
            domainName =
                    (String) exampleDB.getDomainsAsSimpleDBDataList()
                            .get(0).getItemName();
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
    if (request.getParameter("Action") != null) {
        if (request.getParameter("Action").equals("exploreDomain")) {

            //System.out.println("exploreDomain");
            //if we load this screen, then get rid of select in session
            session.setAttribute("select", null);

        } else if (request.getParameter("Action").equals("createItem")
                && request.getParameter("itemName") != null) {

            //System.out.println("createItem");
            itemName = request.getParameter("itemName");
            String keyName = request.getParameter("keyName");
            String value = request.getParameter("value");

            map = new SimpleDBMap();
            map.put(keyName, value);
            map.setItemName(itemName);

            exampleDB.putAttributes(domainName, map);

            String itemNew = request.getParameter("itemNew");

            if (itemNew != null) {

                //if we load this screen, then get rid of select in session
                session.setAttribute("select", null);

            } else {

                //if there is a select statement in session then use it
                if (session.getAttribute("select") != null) {
                    request.setAttribute("simpleDBDataList", exampleDB
                            .setSelect((String) session
                                    .getAttribute("select"), null));
                }

            }
        } else if (request.getParameter("Action").equals("deleteItem")
                && request.getParameter("deleteItem") != null) {
            itemName = request.getParameter("deleteItem");

            //System.out.println("deleteItem");
            exampleDB.deleteItem(domainName, itemName);

            //if there is a select statement in session then use it
            if (session.getAttribute("select") != null) {
                //todo: due to replication lag a double select works more often then not
                exampleDB.setSelect((String) session
                        .getAttribute("select"), null);
                request.setAttribute("simpleDBDataList", exampleDB.setSelect(
                        (String) session.getAttribute("select"), null));
            }
        } else if (request.getParameter("Action").equals(
                "deleteValueKey")
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
                //todo: due to replication lag a double select works more often then not
                exampleDB.setSelect((String) session
                        .getAttribute("select"), null);
                request.setAttribute("simpleDBDataList", exampleDB.setSelect(
                        (String) session.getAttribute("select"), null));
            }
            
        } else if (request.getParameter("Action").equals("select")
                && request.getParameter("select") != null) {

            //System.out.println("select");
            select =
                    ExtendedFunctions.trim(request
                            .getParameter("select"));
            //result = exampleDB.select(select, null);
            //request.setAttribute("result", result);

            //System.out.println("hi: " + currentToken);

            simpleDBDataList = exampleDB.setExcecute(select, null, currentToken);

            domainName = simpleDBDataList.getDomainName();

            request.setAttribute("domain", domainName);
            request.setAttribute("simpleDBDataList", simpleDBDataList);

            //save select statement between sessions
            session.setAttribute("select", select);

            // System.out.println(simpleDBDataList.size());

            //check if we need to update or insert
            String check =
                    "select * from select_history where selectStatement='"
                            + ExtendedFunctions.escapeSql(select)
                            + "' limit 1";

            SimpleDBDataList checkList =
                    exampleDB.setSelect(check, null);

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
                if (simpleDBDataList.size() > 0)
                    value = "true";
                else
                    value = "false";
                map.put(keyName, value);

                keyName = "selectStatement";
                value = select;
                map.put(keyName, value);
                map.setItemName(itemKey);

                //System.out.println(exampleDB.putAttributes(
                //        "select_history", itemKey, map));
                exampleDB.putAttributes("select_history", map);
            } else {

                // otherwise delete it and then put it there
                Object itemKey = null;

                for (int i = 0; i < checkList.size(); i++) {
                    itemKey = checkList.get(i).getItemName();
                    exampleDB.deleteItem("select_history",
                            (String) itemKey);

                    //System.out.println("deleted: " + itemKey);
                }
                String keyName = "modifiedTimeMillies";
                String value = "" + System.currentTimeMillis();
                map = new SimpleDBMap();
                map.put(keyName, value);

                keyName = "hasItems";
                if (simpleDBDataList.size() > 0)
                    value = "true";
                else
                    value = "false";
                map.put(keyName, value);

                keyName = "selectStatement";
                value = select;
                map.put(keyName, value);
                map.setItemName(itemKey);

                //replace current item with modified tag
                //System.out.println(exampleDB.putAttributes(
                //        "select_history", itemKey, map));
                exampleDB.putAttributes("select_history", map);
            }

            //System.out.println(select);
        } else if (request.getParameter("Action").equals("export")
                && request.getParameter("select") != null) {
            
            select =
                    ExtendedFunctions.trim(request
                            .getParameter("select"));

            String path =
                    application.getRealPath(SEPARATOR + "export"
                            + SEPARATOR);

            //the base for the file name is the domain name 
            String fileName = SimpleDBParser.getDomain(select);

            // Make a SimpleDateFormat for toString()'s output.
            SimpleDateFormat format =
                    new SimpleDateFormat(".yyyyMMdd.HHmmss");
            String timeStamped = format.format(new Date());
            fileName += timeStamped + ".sql";
            SaveAsFile.exportSelect(select, path, fileName,
                    accessKeyId, secretAccessKey);
            
%><jsp:forward page="listFiles.jsp" />
<%
        }
    } else if (request.getParameter("importFile") != null) {

        //System.out.println("importing");
        String path =
                application.getRealPath(SEPARATOR + "export"
                        + SEPARATOR);

        //the base for the file name is the domain name 
        String fileName = request.getParameter("importFile");

        ImportFile.importFile(path, fileName, accessKeyId,
                secretAccessKey);

    }
    
    // we should of processed an action above, if not then make on based
    // on the current domainName
    String restSql = "select * from " + domainName;
    if (request.getAttribute("simpleDBDataList") == null) {
        //System.out.println(exampleDB);
        simpleDBDataList = exampleDB.setExcecute(restSql, null, null);
        request.setAttribute("simpleDBDataList", simpleDBDataList);
    }

    simpleDBDataList = (SimpleDBDataList) request.getAttribute("simpleDBDataList");

    // itemTokens is 
    HashMap<String, String> tokens =
            (HashMap<String, String>) session
                    .getAttribute("itemTokens");
    if (tokens == null)
        tokens = new HashMap<String, String>();
    if (currentToken == null) {
        currentToken = "";
    }
    tokens.put(simpleDBDataList.getNextToken(), currentToken);
    session.setAttribute("itemTokens", tokens);

    String nextToken = simpleDBDataList.getNextToken();
    String previousToken = tokens.get(currentToken);

    // need to reformat to get rid of new line characters 
    // (javascript dont like)
    if (nextToken != null) {
        nextToken = nextToken.replace('\n', '@');
    }
    if (previousToken != null) {
        previousToken = previousToken.replace('\n', '@');
    }
    if (select == null) {
        select = restSql;
    }
    Set attributes = new TreeSet(simpleDBDataList.getAttributes());
    request.setAttribute("simpleDBDataList", simpleDBDataList);
    request.setAttribute("attributes", attributes);
    request.setAttribute("nextToken", nextToken);
    request.setAttribute("previousToken", previousToken);
    request.setAttribute("currentToken", currentToken);
    //System.out.println("finished processing");
    //System.out.println(attributes);
    //SaveAsFile.export(simpleDBDataList, "/Users/eddie/Documents/workspace/mysimpledb/", true);
%><input
    id="copySelect"
    value="${select}"
    type="hidden" />

<fieldset style="border: 2px ridge navy;"><legend>&nbsp;Showing
<%=simpleDBDataList.size()%> item(s) in <b>${domain}</b> follow.&nbsp; Response
time: <%=simpleDBDataList.getResponseTime()%>[ms] - BoxUsage: <%=simpleDBDataList.getBoxUsage()%>
- <%=simpleDBDataList.getBoxUsage() == null ? 0 : Math.round(Float
                    .parseFloat(simpleDBDataList.getBoxUsage()) * 100000000f) / 100f%>&micro;$</legend>

<%
    if (domainsSize > 0) {
%> <script>
document.getElementById("fallbackSelect").value="<%=restSql%>";
</script>
<table width="100%">
    <tr>
        <td style="border-width: 0px;">
        <div
            style="margin-top: 5px"
            align="left"><span class="jive-paginator">[ <a
            href="?Action=export"
            title="Click to export the select statement that generated the below table"
            onclick="if(document.getElementById('selectField').value.toLowerCase().indexOf('[limit limit]')==-1){popExport();return false;}">export</a>
        ]&nbsp;[ <a
            href="?Action=viewExport"
            title="Click to view exported files or import files"
            onclick="listExport();return false;"">view exports/
        import</a> ]</span></div>
        </td>
        <td style="border-width: 0px;">
        <div align="right"><span class="jive-paginator">[ <a
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
        </td>
    </tr>
</table>
<%
    }
%>
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
                if (simpleDBDataList.size() > 0) {
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
            items="${simpleDBDataList}"
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
