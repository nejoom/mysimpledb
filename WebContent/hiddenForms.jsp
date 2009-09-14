
<!--  define the dialogs/ forms to be used -->
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

<div id="deleteItem"></div>
<div id="deleteValueKey"></div>
<div id="deleteDomain"></div>
<div id="selectDialog"></div>
<div id="createDialog"></div>
<div id="alertDialog"></div>
