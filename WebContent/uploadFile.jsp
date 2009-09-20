<%@ page
    language="java"
    contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"
    import="java.io.*"%><%--
 
  Copyright 2008-2009 Elements. All Rights Reserved.
 
  License version: CPAL 1.0
 
  The Original Code is mysimpledb.com code. Please visit mysimpledb.com to see how
  you can contribute and improve this software.
 
  The contents of this file are licensed under the Common Public Attribution
  License Version 1.0 (the "License"); you may not use this file except in
  compliance with the License. You may obtain a copy of the License at
 
  http://mysimpledb.com/license.
     
     --%><%--

  http://www.roseindia.net/jsp/file_upload/Sinle_upload.xhtml.shtml

--%><%

    String SEPARATOR = System.getProperty("file.separator");

    //to get the content type information from JSP Request Header
    String contentType = request.getContentType();

    //here we are checking the content type is not equal to Null and as well 
    //as the passed data from mulitpart/ form-data is greater than or equal to 0
    if ((contentType != null)
            && (contentType.indexOf("multipart/form-data") >= 0)) {

        DataInputStream in =
                new DataInputStream(request.getInputStream());

        //we are taking the length of Content type data
        int formDataLength = request.getContentLength();
        byte dataBytes[] = new byte[formDataLength];
        int byteRead = 0;
        int totalBytesRead = 0;

        //this loop converting the uploaded file into byte code
        while (totalBytesRead < formDataLength) {
            byteRead =
                    in.read(dataBytes, totalBytesRead, formDataLength);
            totalBytesRead += byteRead;
        }

        String uploadFile = new String(dataBytes);

        //for saving the file name
        String saveFile =
                uploadFile.substring(uploadFile.indexOf("filename=\"")
                        + "filename=\"".length());

        saveFile = saveFile.substring(0, saveFile.indexOf("\n"));
        saveFile =
                saveFile.substring(saveFile.lastIndexOf("\\") + 1,
                        saveFile.indexOf("\""));

        int lastIndex = contentType.lastIndexOf("=");
        String boundary =
                contentType.substring(lastIndex + 1, contentType
                        .length());
        int pos;

        //extracting the index of uploadFile 
        pos = uploadFile.indexOf("filename=\"");
        pos = uploadFile.indexOf("\n", pos) + 1; // Skip "Content-Type:" line
        pos = uploadFile.indexOf("\n", pos) + 1; // Skip blank line
        pos = uploadFile.indexOf("\n", pos) + 1; // Skip blank line
        int boundaryLocation = uploadFile.indexOf(boundary, pos) - 4; // Position to boundary line

        int startPos =
                ((uploadFile.substring(0, pos)).getBytes()).length;
        int endPos =
                ((uploadFile.substring(0, boundaryLocation)).getBytes()).length;

        String path = application.getRealPath(SEPARATOR + "export");

        //FileOutputStream fos = new FileOutputStream(path + SEPARATOR + saveFile);

        //BufferedWriter osw =
        //        new BufferedWriter(new OutputStreamWriter(fos, "UTF8"));
        //PrintWriter pw = new PrintWriter(osw);
        //pw.write(dataBytes, startPos, (endPos - startPos));
        //pw.flush();
        //pw.close();

        // creating a new file with the same name and writing the content in new file
        FileOutputStream fileOut =
                new FileOutputStream(path + SEPARATOR + saveFile);
        fileOut.write(dataBytes, startPos, (endPos - startPos));
        fileOut.flush();
        fileOut.close();
%>
You have successfully uploaded the file by the name of:
</b>
<%=saveFile%>
<%
    }
%>