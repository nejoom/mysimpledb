<%@ page
    language="java"
    contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"
    import="org.apache.commons.fileupload.*"
    import="org.apache.commons.fileupload.servlet.ServletFileUpload"
    import="org.apache.commons.fileupload.disk.DiskFileItemFactory"
    import="org.apache.commons.io.FilenameUtils,java.util.*"
    import="java.io.File"
    import="java.lang.Exception"%><%--
 
  Copyright 2008-2009 Elements. All Rights Reserved.
 
  License version: CPAL 1.0
 
  The Original Code is mysimpledb.com code. Please visit mysimpledb.com to see how
  you can contribute and improve this software.
 
  The contents of this file are licensed under the Common Public Attribution
  License Version 1.0 (the "License"); you may not use this file except in
  compliance with the License. You may obtain a copy of the License at
 
  http://mysimpledb.com/license.
  
  http://commons.apache.org/fileupload/using.html
--%>
<%
    //Create a progress listener
    ProgressListener progressListener = new ProgressListener() {
        private long megaBytes = -1;

        public void update(long pBytesRead, long pContentLength,
                int pItems) {
            long mBytes = pBytesRead / 10000000;
            if (megaBytes == mBytes) {
                return;
            }
            megaBytes = mBytes;
            System.out.println("We are currently reading item "
                    + pItems);
            if (pContentLength == -1) {
                System.out.println("So far, " + pBytesRead
                        + " bytes have been read.");
            } else {
                System.out.println("So far, " + pBytesRead + " of "
                        + pContentLength + " bytes have been read.");
            }
        }
    };

    String SEPARATOR = System.getProperty("file.separator");

    String dirName = application.getRealPath(SEPARATOR + "export");

    //Check that we have a file upload request
    if (ServletFileUpload.isMultipartContent(request)) {

        // Create a new file upload handler, Create a factory for disk-based file items
        ServletFileUpload servletFileUpload =
                new ServletFileUpload(new DiskFileItemFactory());

        servletFileUpload.setProgressListener(progressListener);

        // Parse the request
        List fileItemsList = servletFileUpload.parseRequest(request);

        String optionalFileName = "";
        FileItem fileItem = null;

        // Process the uploaded items
        Iterator it = fileItemsList.iterator();
        while (it.hasNext()) {
            FileItem fileItemTemp = (FileItem) it.next();
            if (fileItemTemp.isFormField()) {

                String name = fileItemTemp.getFieldName();
                String value = fileItemTemp.getString();

                //Field name: fileItemTemp.getFieldName()
                //Field value: fileItemTemp.getString()
                if (fileItemTemp.getFieldName().equals("uploadFile"))
                    optionalFileName = fileItemTemp.getString();

            } else {

                String fieldName = fileItemTemp.getFieldName();
                String fileName = fileItemTemp.getName();
                String contentType = fileItemTemp.getContentType();
                boolean isInMemory = fileItemTemp.isInMemory();
                long sizeInBytes = fileItemTemp.getSize();

                fileItem = fileItemTemp;

                /* Save the uploaded file if its size is greater than 0. */
                if (fileItem.getSize() > 0) {
                    if (optionalFileName.trim().equals(""))
                        fileName = FilenameUtils.getName(fileName);
                    else
                        fileName = optionalFileName;

                    String saveFile = dirName + SEPARATOR + fileName;
                    File saveTo = new File(saveFile);

                    System.out.println("Try uploaded file:" + saveFile);

                    try {
                        fileItem.write(saveTo);
                        System.out.println("The uploaded file has "
                                + "been saved successfully.");
%>
You have successfully uploaded the file by the name of:
<%=fileName%>
<%
    //The uploaded file has been saved successfully.
                    } catch (Exception e) {
                        System.out.println("An error occurred when we "
                                + "tried to save the uploaded file.");
                        //An error occurred when we tried to save the uploaded file.
                    }
                }// size > 0
            }// is not form field
        }// while list of fields
    }//is multi-form
%>