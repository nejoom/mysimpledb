<%--
 
  Copyright 2008-2009 Elements. All Rights Reserved.
 
  License version: CPAL 1.0
 
  The Original Code is mysimpledb.com code. Please visit mysimpledb.com to see how
  you can contribute and improve this software.
 
  The contents of this file are licensed under the Common Public Attribution
  License Version 1.0 (the "License"); you may not use this file except in
  compliance with the License. You may obtain a copy of the License at
 
     http://mysimpledb.com/license.
     
     --%><!--  define the dialogs/ forms to be used -->
<div id="createItem">
<div class="hd">Please enter item name and attributes</div>
<div class="bd">
<form
    method="post"
    action="listItemNames.jsp?Action=createItem">
<p><label for="itemName">Item name:</label> <input
    type="text"
    name="itemName" /> [leave blank for <a
    target="_blank"
    href="http://www.asciiarmor.com/post/33736615/java-util-uuid-mini-faq">UUID</a>]




<hr />
<p><label for="keyName">Key name:</label><input
    type="text"
    name="keyName" />
<p><label for="value">Value:</label> <input
    type="text"
    name="value" /><input
    type="hidden"
    name="domainName"
    value="${domain}" />
</form>
</div>
</div>

<div id="createDomain">
<div class="hd">Please enter domain name</div>
<div class="bd">
<form
    method="get"
    action="listDomainNames.jsp?Action=createDomain"><label
    for="domainName">Domain name:</label> <input
    type="text"
    name="domainName" /></form>
</div>
</div>

<div id="importFile"></div>
<div id="deleteFile"></div>
<div id="deleteItem"></div>
<div id="deleteValueKey"></div>
<div id="deleteDomain"></div>
<div id="selectDialog"></div>
<div id="createDialog"></div>
<div id="alertDialog"></div>
<div id="OKDialog"></div>
