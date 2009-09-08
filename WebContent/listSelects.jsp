<%@ page
    language="java"
    contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"
    import="ac.elements.sdb.*,ac.elements.conf.*"%><%@ 
taglib
    uri="http://java.sun.com/jstl/core"
    prefix="c"%><%@ 
taglib
    uri="http://java.sun.com/jstl/fmt"
    prefix="fmt"%><%@  
taglib
    uri="http://ac.elements/jsp/jstl/functions"
    prefix="fn"%><%-- 
 --%>
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
    String query = request.getParameter("query");
    if (query == null)
        return;
    query = query + "%";

    String sql =
            "SELECT selectStatement FROM select_history WHERE modifiedTimeMillies "
                    + "IS NOT NULL AND selectStatement LIKE '"
                    + query
                    + "' AND hasItems='true' ORDER BY modifiedTimeMillies DESC";

    //System.out.println(sql);

    SimpleDBDataList checkList = exampleDB.getSelect(sql, null);

    //System.out.println(checkList);

    for (int i = 0; i < checkList.size(); i++) {
        out
                .println(checkList.get(i).get("selectStatement")
                        .toArray()[0]);
    }
%>