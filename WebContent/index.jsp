<%@ page
    language="java"
    contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"
    errorPage="signature.jsp"
    import="ac.elements.sdb.*,ac.elements.conf.*"%><%@ 
taglib
    uri="http://java.sun.com/jstl/core"
    prefix="c"%><%@ 
taglib
    uri="http://java.sun.com/jstl/fmt"
    prefix="fmt" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
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

<center>SimpleDB Administrator User Interface</center>
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
</center> <!-- end footer --></div>
<!-- end document wrapper --></div>
<script type="text/javascript">
  var uservoiceJsHost = ("https:" == document.location.protocol) ? "https://uservoice.com" : "http://cdn.uservoice.com";
  document.write(unescape("%3Cscript src='" + uservoiceJsHost + "/javascripts/widgets/tab.js' type='text/javascript'%3E%3C/script%3E"))
</script>
<script type="text/javascript">
UserVoice.Tab.show({ 
  /* required */
  key: 'mysimpledb',
  host: 'mysimpledb.uservoice.com', 
  forum: '28433', 
  /* optional */
  alignment: 'right',
  background_color:'#aa0000', 
  text_color: 'white',
  hover_color: '#012f7d',
  lang: 'en'
})

UserVoice.Popin.setup({ 
  key: 'mysimpledb',
  host: 'mysimpledb.uservoice.com', 
  forum: 'general', 
  lang: 'en'
})
</script>
</body>
</html>