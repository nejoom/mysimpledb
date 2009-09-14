<%@ page
    language="java"
    contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"
    import="ac.elements.sdb.*,ac.elements.conf.*"
    isErrorPage="true"%>
<%
    String DEFAULT_FILE = "aws"; //.proerties gets added auto
    String FILE_BASE =
            request.getSession().getServletContext().getRealPath(
                    "/WEB-INF/classes").concat("/");

    //main file
    String file = FILE_BASE + DEFAULT_FILE + ".properties";

    PropertiesParser pp = null;
    try {
        pp = PropertiesParser.loadProperties(file);
    } catch (Exception e) {
        System.out.println(e);
    }

    if (request.getParameter("AWSAccessKeyId") != null
            && request.getParameter("SecretAccessKey") != null) {

        pp.setProperty("AWSAccessKeyId", request
                .getParameter("AWSAccessKeyId"));
        pp.setProperty("SecretAccessKey", request
                .getParameter("SecretAccessKey"));

        PropertiesParser.saveProperties(pp, file, true);

        response.sendRedirect("index.jsp");

    }
%>
<br />
<h2 style="color: red"><%=exception != null ? exception.getLocalizedMessage()
                    : ""%></h2>
<br />
<hr />
<br />
<p>Please edit the properties file manually (see bottom of screen)
or enter information to submit to the server located at <%=request.getRemoteHost()%>.</p>
<p style="background-color: yellow">Please note that you will either need to restart the Tomcat
server, or if tomcat reloads properties files on the fly, you will need
to retry after some seconds before changes take effect.</p>
<form
    method="post"
    action="/mysimpledb/signature.jsp">
<fieldset><legend>Enter information</legend><!-- 

 -->
<p><label for="AWSAccessKeyId">AWSAccessKeyId <font
    color="darkred">*</font></label><input
    type="text"
    id="AWSAccessKeyId"
    name="AWSAccessKeyId"
    size="25"
    style="width: 165px;"
    value="<%=pp.getProperty("AWSAccessKeyId")%>" /></p>
<p><label for="SecretAccessKey">SecretAccessKey <font
    color="darkred">*</font></label><input
    type="password"
    id="SecretAccessKey"
    name="SecretAccessKey"
    size="25"
    style="width: 165px;" /></p>
<p>
<center><input
    type="submit"
    value="Submit to save to <%=file%>" /></center>
</fieldset>
</form>
<br />
<hr />
<br />
<p>You can also copy & paste the following path-file to an editor on
the server and edit manually: <input
    type="text"
    name="file"
    value="<%=file%>"></input></p>
<hr />
<br />

<p>Play following video to learn more and check out <a
    href="http://aws.amazon.com/simpledb/">Amazon's Simple DB</a> page:</p>
<object
    width="425"
    height="344">
    <param
        name="movie"
        value="http://www.youtube.com/v/mXrqDD9Rs3M&hl=en&fs=1&"></param>
    <param
        name="allowFullScreen"
        value="true"></param>
    <param
        name="allowscriptaccess"
        value="always"></param>
    <embed
        src="http://www.youtube.com/v/mXrqDD9Rs3M&hl=en&fs=1&"
        type="application/x-shockwave-flash"
        allowscriptaccess="always"
        allowfullscreen="true"
        width="425"
        height="344"></embed></object>