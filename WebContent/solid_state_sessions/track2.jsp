<%-- sess_track2.jsp - session request counting/timestamping demonstration
--%>
<%@ page import="java.util.*"%>
<%@ taglib
    uri="http://java.sun.com/jstl/core"
    prefix="c"%>
<c:if test="${empty sessionScope.count}">
    <c:set
        var="count"
        scope="session"
        value="0" />
</c:if>
<c:set
    var="count"
    scope="session"
    value="${sessionScope.count+1}" />
<%
    ArrayList timestamp = (ArrayList) session.getAttribute("timestamp");
    if (timestamp == null)
        timestamp = new ArrayList();
    // add current timestamp to timestamp array, store result in session
    timestamp.add(new Date());
    session.setAttribute("timestamp", timestamp);
%>
<html>
<head>
<title>JSP Session Tracker 2</title>
</head>
<body bgcolor="white">
<p>This session has been active for <c:out
    value="${sessionScope.count}" /> requests.</p>
<p>The requests occurred at these times:</p>
<ul>
    <c:forEach
        var="t"
        items="${sessionScope.timestamp}">
        <li><c:out value="${t}" /></li>
    </c:forEach>
</ul>
<%-- has session limit of 10 requests been reached? --%>
<c:if test="${sessionScope.count ge 10}">
    <c:remove
        var="count"
        scope="session" />
    <c:remove
        var="timestamp"
        scope="session" />
</c:if>
</body>
</html>