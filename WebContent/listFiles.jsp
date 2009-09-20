<%@ page
    language="java"
    contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"
    import="java.io.*"
    import="java.util.*"
    import="java.text.SimpleDateFormat"
    import="ac.elements.sdb.*,ac.elements.conf.*,java.util.HashMap"%><%--
 
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

<%!/**
     * Wrapperclass to wrap an OutputStream around a Writer
     */
    class Writer2Stream extends OutputStream {

        Writer out;

        Writer2Stream(Writer w) {
            super();
            out = w;
        }

        public void write(int i) throws IOException {
            out.write(i);
        }

        public void write(byte[] b) throws IOException {
            for (int i = 0; i < b.length; i++) {
                int n = b[i];
                //Convert byte to ubyte
                n = ((n >>> 4) & 0xF) * 16 + (n & 0xF);
                out.write(n);
            }
        }

        public void write(byte[] b, int off, int len) throws IOException {
            for (int i = off; i < off + len; i++) {
                int n = b[i];
                n = ((n >>> 4) & 0xF) * 16 + (n & 0xF);
                out.write(n);
            }
        }
    } //End of class Writer2Stream

    /**
     */
    static File[] orderFileList(File[] fileObjects, String sort) {

        Comparator<Object> reverse = Collections.reverseOrder();
        TreeMap<Object, ArrayList<File>> tm =
                new TreeMap<Object, ArrayList<File>>(reverse);

        // need to organize things for non unique keys to sort
        for (int i = 0; i < fileObjects.length; i++) {
            Object key = null;
            if (sort != null && sort.equals("name")) {
                key = fileObjects[i].getName();
            } else if (sort != null && sort.equals("size")) {
                key = fileObjects[i].length();
            } else {
                key = fileObjects[i].lastModified();
            }
            if (tm.get(key) == null) {
                ArrayList<File> al = new ArrayList<File>();
                al.add(fileObjects[i]);
                tm.put(key, al);
            } else {
                tm.get(key).add(fileObjects[i]);
            }
        }
        File[] files = new File[fileObjects.length];
        int i = 0;
        for (Iterator<Object> iterator = tm.keySet().iterator(); iterator
                .hasNext();) {
            ArrayList<File> arrayFiles = tm.get(iterator.next());
            for (Iterator<File> iterator2 = arrayFiles.iterator(); iterator2
                    .hasNext();) {
                files[i++] = (File) iterator2.next();
            }
        }

        return files;
    }

    /**
     * Copies all data from in to out
     *  @param in the input stream
     *  @param out the output stream
     *  @param buffer copy buffer
     */
    static void copyStreamsWithoutClose(InputStream in, OutputStream out,
            byte[] buffer) throws IOException {
        int b;
        while ((b = in.read(buffer)) != -1)
            out.write(buffer, 0, b);
    }%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta
    http-equiv="Content-Type"
    content="text/html; charset=ISO-8859-1">
<title>Index of Files</title>
</head>
<body>
<%
    String SEPARATOR = System.getProperty("file.separator");

    if (request.getParameter("deleteFile") != null) {
        String filePath = request.getParameter("deleteFile");
        File delete = new File(filePath);
        //System.out.println(filePath);
        if (!delete.canWrite() || !delete.delete()) {
            //throw new Exception("No delete");
        }
    }

    String file =
            application.getRealPath(SEPARATOR + "export" + SEPARATOR);

    File f = new File(file);
    File[] fileObjects =
            orderFileList(f.listFiles(), request
                    .getParameter("sortfile"));

    // Make a SimpleDateFormat for toString()'s output.
    SimpleDateFormat format =
            new SimpleDateFormat("MMM dd yyyy HH:mm:ss");

    if (request.getParameter("downfile") != null) {
        String filePath = request.getParameter("downfile");
        File download = new File(filePath);
        System.out.println(filePath);
        if (download.exists() && download.canRead()) {
            response.setContentType("application/octet-stream");
            response.setHeader("Content-Disposition",
                    "attachment;filename=\"" + download.getName()
                            + "\"");
            response.setContentLength((int) download.length());
            BufferedInputStream fileInput =
                    new BufferedInputStream(new FileInputStream(
                            download));
            byte buffer[] = new byte[8 * 1024];
            out.clearBuffer();
            OutputStream out_s = new Writer2Stream(out);
            copyStreamsWithoutClose(fileInput, out_s, buffer);
            fileInput.close();
            out_s.flush();
        }
        return;
    }
%>

<fieldset style="border: 2px ridge navy;"><legend>&nbsp;<b>Index
of exported files:</b></legend>

<p><%=file%></p>

<form
    action=""
    enctype="multipart/form-data"
    method="post"
    id="uploadFile"><input
    type="file"
    name="uploadFile" /> <input
    type="button"
    id="uploadButton"
    value="Upload"
    onclick="uploadButtonClick()" /></form>
<div
    style="visibility: hidden; margin-bottom: 10px;"
    id="indicator">Uploading... <img
    src="/mysimpledb/assets/img/indicator.gif" /></div>
<table
    class="none"
    border="0">
    <tr>
        <td style="border-width: 0px; padding-left: 5px">&nbsp;</td>
        <td
            align="right"
            style="border-width: 0px; adding-left: 5px"><b>Size:</b></td>
        <td style="border-width: 0px; padding-left: 5px"><b>Last
        modified:</b></td>
        <td style="border-width: 0px; padding-left: 5px">&nbsp;</td>
        <td style="border-width: 0px; padding-left: 5px"><b>File
        name:</b></td>
    </tr>
    <%
        for (int i = 0; i < fileObjects.length; i++) {
            if (!fileObjects[i].isDirectory()) {
                request.setAttribute("size", Long.toString(fileObjects[i]
                        .length()));
                request.setAttribute("fileName", fileObjects[i].getName());
                request.setAttribute("filePath", fileObjects[i]
                        .getAbsolutePath());
                Date date = new Date(fileObjects[i].lastModified());
                String timeStamped = format.format(date);
    %>

    <tr>
        <td style="border-width: 0px; padding-left: 5px"><a
            href="?Action=deleteFile"
            title="Click to delete file"
            onclick="popDeleteFile('Delete file ${fn:escUniJs(fileName)}', 'Are you sure you want to delete this file?', '${fn:escUniJs(filePath)}');return false;"><img
            src="/mysimpledb/assets/img/delete16.gif"></img></a>
        <td
            align="right"
            style="border-width: 0px; padding-left: 5px">${fn:formatBytes(size)}</td>
        <td style="border-width: 0px; padding-left: 5px"><%=timeStamped%></td>
        <td style="border-width: 0px; padding-left: 5px"><a
            title="Download file"
            href="<%=request.getRequestURI()%>?downfile=<%=fileObjects[i].getAbsolutePath()%>"><img
            src="/mysimpledb/assets/img/download16.gif"></img></a></td>
        <td style="border-width: 0px; padding-left: 5px"><a
            title="Open file in new window"
            target="_blank"
            href="/mysimpledb/export/<%=fileObjects[i].getName()%>"><%=fileObjects[i].getName()%></a></td>
        <td style="border-width: 0px; padding-left: 5px"><a
            title="Import sql statements"
            href="?Action=importFile"
            onclick="popImportFile('Import file ${fn:escUniJs(fileName)}', 'Are you sure you want to import this file into simpleDB?', '${fn:escUniJs(fileName)}');return false;"><img
            src="/mysimpledb/assets/img/import16.gif"></img></a></td>
    </tr>
    <%
        }
        }
    %>

</table>
</fieldset>


</body>
</html>