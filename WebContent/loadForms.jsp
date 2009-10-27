<%--
 
  Copyright 2008-2009 Elements. All Rights Reserved.
 
  License version: CPAL 1.0
 
  The Original Code is mysimpledb.com code. Please visit mysimpledb.com to see how
  you can contribute and improve this software.
 
  The contents of this file are licensed under the Common Public Attribution
  License Version 1.0 (the "License"); you may not use this file except in
  compliance with the License. You may obtain a copy of the License at
 
     http://mysimpledb.com/license.
     
     --%><fieldset style="border: 2px ridge navy;"><legend>&nbsp;Construct
<b>query</b> <span class="jive-paginator">[ <a
    href="http://docs.amazonwebservices.com/AmazonSimpleDB/2009-04-15/DeveloperGuide/index.html?UsingSelect.html"
    title="Click for help"
    target="_blank">help</a> ]&nbsp;[ <a
    href=#
    "
    title="Click to create domain that retains your sql history"
    onclick="document.getElementById('selectField').value='create domain select_history';return false">create
domain</a> ]&nbsp;[ <a
    href="#"
    onmouseout="_hide();"
    onmouseover="_show(event,'select');"
    onclick="document.getElementById('selectField').value='select * from test';return false">select</a>
| <a
    href="#"
    title="Click to view example syntax"
    onmouseout="_hide();"
    onmouseover="_show(event,'insert');"
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
    target="_blank"
    title="Click to download tomcat war">download tomcat war</a> | <a
    title="Click to view source on github.com"
    target="_blank"
    href="http://github.com/nejoom/mysimpledb/tree/master">view github
source</a> | <a
    href="#"
    title="Click to give feedback on how to improve (thanks!)"
    onclick="UserVoice.Popin.show(); return false;">feedback</a> ]</span> </legend>

<div align="right"><span class="jive-paginator">[ <a
    href="?Action=submitSelect"
    title="Click to submit the below select statement"
    onclick="if(document.getElementById('selectField').value.toLowerCase().indexOf('create domain ')==-1){popSelect();return false;}else{popCreate();return false;}">press
enter to submit</a> ]&nbsp;</span></div>


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
    <input name="fallbackSelect" id="fallbackSelect" type="hidden"></input>
<div
    id="myContainer"
    style="margin: 5px;"></div>
</div>

</fieldset>

<!-- display tooltip metadata -->
<div
    id="select"
    style="display: none">
<div class="dialog">
<div class="content">
<div class="t"></div>
<div class="label-content">
<center><b>Select</b></center>
The Select operation returns a set of Attributes for ItemNames
that match the query expression. Select is similar to the standard SQL
SELECT statement. see <a
    target="blank"
    href="http://docs.amazonwebservices.com/AmazonSimpleDB/2009-04-15/DeveloperGuide/index.html?UsingSelect.html">Amazon
docs</a>
<hr />
<center>Click link to explore</center>
</div>
</div>
<div class="b">
<div></div>
</div>
</div>
</div>



<div
    id="insert"
    style="display: none">
<div class="dialog">
<div class="content">
<div class="t"></div>
<div class="label-content">
<center><b>Insert</b></center>

<p>The Insert operation is a custom utility method that inserts a
set of key value pairs, by parsing the insert statement and uses the
batchPutAttributes method to insert the key value pairs. Unlike standard
SQL, the item name used is also given. If there is already an item name,
the key - value pair is added to the item name.
<p>The quoting rules are the same as for the select statement. If no
quotes are used, then the value is either a number or an unquoted
string.</p>
<p>If the value is a number then Negative Numbers Offsets, Zero
Padding algorithms are used. This is a java centric implementation and
works for integers (eg. 123), longs (123l), floats (1.23) and doubles
(1.23d). Please see <a
    target="_blank"
    href="http://docs.amazonwebservices.com/AmazonSimpleDB/2009-04-15/DeveloperGuide/index.html?NumericalData.html">Amazon's
manual</a> for a more details, or refer to the source of <a
    target="_blank"
    href="http://github.com/nejoom/mysimpledb/blob/master/src/ac/elements/parser/SimpleDBConverter.java">SimpleDBConverter</a>.</p>
<code> insert into test (`keyone`, keytwo, keythree, keyfour)
values (1.3, 2, 9l, 12.231d) </code><br /><br />
<p>If no itemName is given then a universally unique identifiers
(UUID) is generated for the item name, a guarantee for no collisions of
the items inserted:</p>
<code> insert into test (`keyone`, keytwo) values ('generate
uuid', 'value2a')</code><br />
<br />
<p>The itemName can also be the <b>first</b> column name, this
simplifies things when importing the sql into for instance mysql:</p>

<code> insert into test (<b>`itemName()`</b>, keyone, keytwo)
values ('aItemName', 'value1a', 'value2a')</code><br />
<br />
<p>For the rest the insert is similar to the standard SQL INSERT
statement. Example strings follow (no new line characters should be
used):</p>

<code>insert into test (keyone, `keytwo`) values ('value1a',
'value2a'), ('value1b5', 'value2b')</code><br />
<hr />
<center>Click link to explore</center>
</div>
</div>
<div class="b">
<div></div>
</div>
</div>
</div>

