<%@ page
    language="java"
    contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"
    import="org.apache.log4j.Logger"
    errorPage="signature.jsp"%><%--
 
  Copyright 2008-2009 Elements. All Rights Reserved.
 
  License version: CPAL 1.0
 
  The Original Code is mysimpledb.com code. Please visit mysimpledb.com to see how
  you can contribute and improve this software.
 
  The contents of this file are licensed under the Common Public Attribution
  License Version 1.0 (the "License"); you may not use this file except in
  compliance with the License. You may obtain a copy of the License at
 
     http://mysimpledb.com/license.
     
     --%><%!/** The Constant log. */
    private final static Logger log = Logger.getLogger("index.jsp");%><%@ 
taglib
    uri="http://java.sun.com/jstl/core"
    prefix="c"%><%@ 
taglib
    uri="http://java.sun.com/jstl/fmt"
    prefix="fmt"%>
<%
    log.debug("Entering index.jsp");
%><!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
<head>
<meta
    http-equiv="Content-Type"
    content="text/html; charset=ISO-8859-1">
<title>MySimpleDB Administrator User Interface</title>
<jsp:include page="loadScripts.jsp" />
</head>
<body class="yui-skin-sam">
<div id="doc3"><!-- document wrapper -->
<div id="hd"><!-- header --> <%-- 
Hi <%=session.getAttribute("name")%><br />
Count: <%=session.getAttribute("count")%> Date: <%=new java.util.Date(session.getCreationTime())%>
Id: <%=session.getId()%>| Last access: <%=new java.util.Date(session.getLastAccessedTime())%>
--%>



<h1>
<center>SimpleDB Administrator User Interface</center>
</h1>
<hr />
<jsp:include page="loadForms.jsp" /> <!-- end header --></div>
<div id="bd"><!-- body yui -->
<div class="yui-gf"><!-- 1/3 2/3 layout -->
<div class="yui-u first"><!-- 1/3 first layout -->
<div id="listDomainNames"><jsp:include page="listDomainNames.jsp" /></div>
<!-- end 1/3 first layout --></div>
<div class="yui-u"><!-- 2/3 first layout -->
<div id="listItemNames"><jsp:include page="listItemNames.jsp" /></div>
<!-- end 2/3 first layout --></div>
<!-- end 1/3 2/3 layout --></div>
<!-- end body yui --></div>
<div id="ft"><!-- footer -->
<hr />
<jsp:include page="hiddenForms.jsp" />
<center>Copyright elements (c)/ <a href="http://mysimpledb.com">mysimpledb.com</a>
</center>
<!-- end footer --></div>
<!-- end document wrapper --></div>
</body>
</html>