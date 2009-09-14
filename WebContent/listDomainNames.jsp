<%@ page
    language="java"
    contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"
    errorPage="error.jsp"
    import="ac.elements.sdb.*,ac.elements.conf.*,java.util.HashMap"%><%@ 
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

    if (request.getParameter("Action") != null
            && request.getParameter("Action").equals("createDomain")
            && request.getParameter("domainName") != null) {
        String domainName = request.getParameter("domainName");
        exampleDB.createDomain(domainName);
    } else if (request.getParameter("Action") != null
            && request.getParameter("Action").equals("deleteDomain")
            && request.getParameter("deleteDomain") != null) {
        String domainName = request.getParameter("deleteDomain");
        exampleDB.deleteDomain(domainName);
    } else if (request.getParameter("Action") != null
            && request.getParameter("Action").equals("select")
            && request.getParameter("select") != null) {

        String select = ExtendedFunctions.trim(request.getParameter("select"));
        //result = exampleDB.select(select, null);
        //request.setAttribute("result", result);

        exampleDB.setExcecute(select, null);

    }

    SimpleDBDataList sdbList = exampleDB.getDomainsAsList();

    String domainName = request.getParameter("domainName");
    if (domainName == null && sdbList.size()>0) {
        domainName = (String) sdbList.get(0).getItemName();
    }

    //map next/ previous token pairs
    String currentToken = request.getParameter("domainNextToken");
    HashMap<String, String> tokens =
            (HashMap<String, String>) session.getAttribute("domainTokens");
    if (tokens == null)
        tokens = new HashMap<String, String>();
    if (currentToken == null) {
        currentToken = "";
    }
    tokens.put(sdbList.getNextToken(), currentToken);
    session.setAttribute("domainTokens", tokens);

    request.setAttribute("domainList", sdbList);
    request.setAttribute("nextToken", sdbList.getNextToken());
    request.setAttribute("previousToken", tokens.get(currentToken));
    request.setAttribute("currentToken", currentToken);
%>
<fieldset style="border: 2px ridge navy;"><legend><b>&nbsp;Domains:</b></legend>
<div align="right"><span class="jive-paginator">[ <a
    href="?Action=createDomain"
    title="Click to add domain"
    onclick="popCreateDomain();return false;">create new domain</a>
]&nbsp;&nbsp;</span></div>
<div align="right"><span class="jive-paginator">[<c:choose>
    <c:when test="${previousToken!=null}">
        <a
            href="?Action=exploreDomain&domainNextToken=${previousToken}"
            title="Click to view previous domains"
            onclick="popRefreshDomains('${previousToken}');return false;">previous</a>
    </c:when>
    <c:otherwise>previous</c:otherwise>
</c:choose>] [<c:choose>
    <c:when test="${nextToken!=null}">
        <a
            href="?Action=exploreDomain&domainNextToken=${nextToken}"
            title="Click to view next domains"
            onclick="popRefreshDomains('${nextToken}');return false;">next</a>
    </c:when>
    <c:otherwise>next</c:otherwise>
</c:choose>]&nbsp;&nbsp;</span></div>
<ol>
    <c:forEach
        items="${domainList}"
        var="domain"
        varStatus="status">
        <li><span class="jive-paginator">[<a
            href="?Action=deleteDomain"
            title="Click to delete domain"
            onclick="popDeleteDomain('Delete Domain: ${domain}', 'Are you very sure?', '${domain}');return false;">del</a>]
        <a
            href="?Action=exploreDomain&domainName=${domain}"
            onmouseout="_hide();"
            onmouseover="_show(event,'id${status.count}');"
            onclick="makeRequest('${domain}');return false;"><c:out
            value="${domain}" /></a></span>
    </c:forEach>
</ol>
</fieldset>

<!-- display tooltip metadata -->
<c:forEach
    items="${domainList}"
    var="domain"
    varStatus="status">
    <div
        id="id${status.count}"
        style="display: none">
    <div class="dialog">
    <div class="content">
    <div class="t"></div>
    <c:set
        var="domain"
        value="${domain}"
        scope="request" />
    <div class="label-content">
    <center><b>DomainMetaData: &nbsp;&nbsp;</b></center>
    <hr />
    Domain: <b>${domain}</b><br /><%
        request.setAttribute("metaData", exampleDB
                    .getMetaDataAsMap(request.getAttribute("domain")
                            .toString()));
    %><c:forEach
        items="${metaData}"
        var="entry">${entry.key}: 
        <b><c:choose>
            <c:when test="${fn:indexOf(entry.key,'Bytes')!=-1}">${fn:formatBytes(entry.value)}</c:when>
            <c:otherwise>${entry.value}</c:otherwise>
        </c:choose></b>
        <br />
    </c:forEach>
    <hr />
    <center>Click link to explore</center>
    </div>
    </div>
    <div class="b">
    <div></div>
    </div>
    </div>
    </div>
</c:forEach>

<!-- fieldset style="border: 2px ridge navy;"><legend><b>&nbsp;Debug:</b></legend>
<div align="right">Action: <%=request.getParameter("Action")%>,
Domain: <%=domainName%></div>
</fieldset -->