
<fieldset style="border: 2px ridge navy;"><legend>&nbsp;Construct
<b>query</b> statement <span class="jive-paginator">[ <a
    href="http://docs.amazonwebservices.com/AmazonSimpleDB/2009-04-15/DeveloperGuide/index.html?UsingSelect.html"
    title="Click for help"
    target="_blank">help</a> ]&nbsp;[ <a
    href=#
    "
    title="Click to create domain that retains your sql history"
    onclick="document.getElementById('selectField').value='create domain select_history';return false">create
domain</a> ]&nbsp;[ <a
    href="#"
    title="Click to view example syntax"
    onclick="document.getElementById('selectField').value='select * from test';return false">select</a>
| <a
    href="#"
    title="Click to view example syntax"
    onclick="document.getElementById('selectField').value='insert into test (key1, key2, newkey1) values (1, 2, 3), (2, 3, 4), item(3, 4, 5), item(\'string1\', \'string2\', \'string3\')';return false">insert</a>
| <a
    href="#"
    title="Click to view example syntax"
    onclick="document.getElementById('selectField').value='insert into test (newkey1, newkey2) values (1, 2) where key1=1';return false">insert
where *</a> | <a
    href="#"
    title="Click to view example syntax"
    onclick="document.getElementById('selectField').value='replace into test (key1, key2) values item(5, 6), item1(9,1)';return false">replace</a>
| <a
    href="#"
    title="Click to view example syntax"
    onclick="document.getElementById('selectField').value='replace into test (key1, key2) values (5, 6) where key1=1';return false">replace
where *</a> | <a
    href="#"
    title="Click to view example syntax"
    onclick="document.getElementById('selectField').value='delete from test where key1 <= 2';return false">delete
*</a> | <a
    href="#"
    title="Click to view example syntax"
    onclick="document.getElementById('selectField').value='delete item(key2=6) from test';return false">delete
att</a> | <a
    href="#"
    title="Click to view example syntax"
    onclick="document.getElementById('selectField').value='delete (key2=6, key1) from test where newkey1 = \'string3\'';return false">delete
att where *</a> ] [ <a
    href="mysimpledb.war"
    title="Click to download tomcat war">tomcat war</a> | <a
    title="Click to view source on github.com"
    href="http://github.com/nejoom/mysimpledb/tree/master">github
source</a> | <a
    href="#"
    title="Click to give feedback on how to improve (thanks!)"
    onclick="UserVoice.Popin.show(); return false;">feedback</a> ] </legend>

<div align="right">[ <a
    href="?Action=submitSelect"
    title="Click to submit the above select statement"
    onclick="if(document.getElementById('selectField').value.toLowerCase().indexOf('create domain ')==-1){popSelect();return false;}else{popCreate();return false;}">press
enter to submit</a> ]&nbsp;</div>
</span>

<div style="width: 99%; align: center; padding-bottom: 2em;"><input
    name="select"
    id="selectField"
    type="text"
    style="color: #999; height: 20px; border-style: solid; border-width: 1px; margin: 5px; border-color: darkred;"
    value="select * from domainName  [where expression] [sort_instructions] [limit limit]"
    onKeyPress="key=window.event.keyCode;if(key==13){popSelect();return false;}"
    onfocus="if(this.value==this.getAttribute('default'))this.value='';YAHOO.util.Dom.setStyle(this, 'color', '#000');"
    onblur="if(YAHOO.lang.trim(this.value)==''){this.value=this.getAttribute('default');YAHOO.util.Dom.setStyle(this, 'color', '#999');}"
    default="select * from domainName  [where expression] [sort_instructions] [limit limit]">
<div
    id="myContainer"
    style="margin: 5px;"></div>
</div>
<br />
</fieldset>