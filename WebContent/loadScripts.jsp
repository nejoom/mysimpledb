<!-- Combo-handled YUI CSS files: -->
<link
    rel="stylesheet"
    type="text/css"
    href="http://yui.yahooapis.com/combo?2.7.0/build/reset-fonts-grids/reset-fonts-grids.css&2.7.0/build/base/base-min.css&2.7.0/build/assets/skins/sam/skin.css">
<!-- Combo-handled YUI JS files: -->
<script
    type="text/javascript"
    src="http://yui.yahooapis.com/combo?2.7.0/build/utilities/utilities.js&2.7.0/build/datasource/datasource-min.js&2.7.0/build/autocomplete/autocomplete-min.js&2.7.0/build/button/button-min.js&2.7.0/build/container/container-min.js"></script>
<link
    rel="stylesheet"
    type="text/css"
    href="/mysimpledb/assets/css/default.css">
<%--
<!-- Individual YUI CSS files --> 
<link rel="stylesheet" type="text/css" href="http://ajax.googleapis.com/ajax/libs/yui/2.7.0/build/reset-fonts-grids/reset-fonts-grids.css"> 
<link rel="stylesheet" type="text/css" href="http://ajax.googleapis.com/ajax/libs/yui/2.7.0/build/base/base-min.css"> 
<link rel="stylesheet" type="text/css" href="http://ajax.googleapis.com/ajax/libs/yui/2.7.0/build/assets/skins/sam/skin.css"> 
<!-- Individual YUI JS files --> 
<script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/yui/2.7.0/build/utilities/utilities.js"></script> 
<script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/yui/2.7.0/build/datasource/datasource-min.js"></script> 
<script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/yui/2.7.0/build/autocomplete/autocomplete-min.js"></script> 
<script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/yui/2.7.0/build/button/button-min.js"></script> 
<script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/yui/2.7.0/build/container/container-min.js"></script> 
--%>
<%-- 
<script
    type="text/javascript"
    src="http://alexgorbatchev.com/pub/sh/current/scripts/shCore.js"></script>
<script
    type="text/javascript"
    src="http://alexgorbatchev.com/pub/sh/current/scripts/shBrushCss.js"></script>
<script
    type="text/javascript"
    src="http://alexgorbatchev.com/pub/sh/current/scripts/shBrushJava.js"></script>
<script
    type="text/javascript"
    src="http://alexgorbatchev.com/pub/sh/current/scripts/shBrushJScript.js"></script>
<script
    type="text/javascript"
    src="http://alexgorbatchev.com/pub/sh/current/scripts/shBrushPlain.js"></script>
<script
    type="text/javascript"
    src="http://alexgorbatchev.com/pub/sh/current/scripts/shBrushSql.js"></script>
<script
    type="text/javascript"
    src="http://alexgorbatchev.com/pub/sh/current/scripts/shBrushXml.js"></script>
<script type="text/javascript">
SyntaxHighlighter.config.clipboardSwf = 'http://alexgorbatchev.com/pub/sh/current/scripts/clipboard.swf';
SyntaxHighlighter.all();</script>

<link
    type="text/css"
    rel="stylesheet"
    href="http://alexgorbatchev.com/pub/sh/current/styles/shCore.css" />
<link
    type="text/css"
    rel="stylesheet"
    href="http://alexgorbatchev.com/pub/sh/current/styles/shThemeDefault.css"
    id="shTheme" />
--%>

<link
    rel="stylesheet"
    type="text/css"
    href="/mysimpledb/assets/js/tooltip.css">

<script
    type="text/javascript"
    src="/mysimpledb/assets/js/tooltip.js"></script>

<script>

// defining a namespace seems to be essential for internet explorer
YAHOO.namespace("mysimpledb");

// Variable holding the div id of the element to work on. 
var divElement = "listDomainNames";

function init() {

	// Define various event handlers for Dialogs
	var handleSubmit = function() {
	    this.submit();
	    this.hide();
	};

	var handleCancel = function() {
	    this.cancel();
	    this.hide();
	};

	var handleSuccess = function(o) {
	    var response = o.responseText;
	    document.getElementById(divElement).innerHTML = response;
	    YAHOO.mysimpledb.wait.hide();
	    
	    // update the auto-complete box to make natural querying
	    if (document.getElementById("copySelect").value == "") {
	      document.getElementById("selectField").value = 
	        document.getElementById("selectField").getAttribute("default");
	    } 
	    //alert();
	    //highlight the xml debug
	    //SyntaxHighlighter.highlight();
	};

	var handleFailure = function(o) {
	    YAHOO.mysimpledb.wait.hide();
	    popAlertDialog("Handling submit failure: ", 
	        "Err code: " + o.status + ", " + o.statusText + ".<br />");
	};

	var callback = {
	    success:handleSuccess,
	    failure:handleFailure
	};
    
    // Initialize the temporary Panel to display while waiting
    YAHOO.mysimpledb.wait = new YAHOO.widget.Panel("wait", 
    { 
        width: "240px", 
        y: 20,
        fixedcenter: true, 
        close: false, 
        draggable: true, 
        zindex:4,
        modal: true,
        visible: false
    });

    YAHOO.mysimpledb.wait.setHeader("Processing at AWS's SimpleDB ...");
    YAHOO.mysimpledb.wait.setBody("<img src='http://l.yimg.com/a/i/us/per/gr/gp/rel_interstitial_loading.gif'/>");
    YAHOO.mysimpledb.wait.render(document.body);
    
    
    // Instantiate the alert dialog
    YAHOO.mysimpledb.alertDialog = new YAHOO.widget.SimpleDialog("alertDialog", { 
        modal: true,
        width : "25em",
        fixedcenter : true,
        visible : false, 
        constraintoviewport : true,
        buttons: [ 
        { 
            text:"Dang!",  
            handler:handleCancel
        } ]
    });

    // Validate the entries in the for
    YAHOO.mysimpledb.alertDialog.validate = function() {
      return true;
    };

    // Wire up the success and failure handlers
    YAHOO.mysimpledb.alertDialog.callback = callback;
    
    // Render the Dialog
    YAHOO.mysimpledb.alertDialog.render();
    
    // Instantiate the Dialog
    YAHOO.mysimpledb.deleteDomain = new YAHOO.widget.SimpleDialog("deleteDomain", { 
        modal: true,
        width : "20em",
        fixedcenter : true,
        visible : false, 
        constraintoviewport : true,
        buttons: [ 
        { 
            text:"Yes", 
            handler:handleSubmit, 
            isDefault:true 
        },
        { 
            text:"No",  
            handler:handleCancel
        } ]
    });

    // Validate the entries in the for
    YAHOO.mysimpledb.deleteDomain.validate = function() {
      YAHOO.mysimpledb.wait.show();
      return true;
    };

    // Wire up the success and failure handlers
    YAHOO.mysimpledb.deleteDomain.callback = callback;
    
    // Render the Dialog
    YAHOO.mysimpledb.deleteDomain.render();
    
    // Instantiate the Dialog
    YAHOO.mysimpledb.createDomain = new YAHOO.widget.Dialog("createDomain", { 
        modal: true,
        width : "30em",
        fixedcenter : true,
        visible : false, 
        constraintoviewport : true,
        buttons: [ 
        { 
            text:"Submit", 
            handler:handleSubmit, 
            isDefault:true 
        },
        { 
            text:"Cancel",  
            handler:handleCancel
        } ]
    });

    // Validate the entries in the form 
    YAHOO.mysimpledb.createDomain.validate = function() {
        var data = this.getData();
        if (data.domainName == "" ) {
            return false;
        } else {
            YAHOO.mysimpledb.wait.show();
            return true;
        }
    };

    // Wire up the success and failure handlers
    YAHOO.mysimpledb.createDomain.callback = callback;
    
    // Render the Dialog
    YAHOO.mysimpledb.createDomain.render();
    
    // Instantiate the Dialog
    YAHOO.mysimpledb.createItem = new YAHOO.widget.Dialog("createItem", { 
        modal: true,
        width : "30em",
        fixedcenter : true,
        visible : false, 
        constraintoviewport : true,
        buttons: [ 
        { 
            text:"Submit", 
            handler:handleSubmit, 
            isDefault:true 
        },
        { 
            text:"Cancel",  
            handler:handleCancel
        } ]
    });

    // Validate the entries in the form 
    YAHOO.mysimpledb.createItem.validate = function() {
        var data = this.getData();
        this.form.itemName.disabled=false;
        
        YAHOO.mysimpledb.wait.show();
        return true;
    };
    
    

    // Validate the entries in the form 
    YAHOO.mysimpledb.createItem.setItem = function(item, domain) {
        this.form.itemName.value=item;
        this.form.domainName.value=domain;
    };

    // Wire up the success and failure handlers
    YAHOO.mysimpledb.createItem.callback = callback;
    
    // Render the Dialog
    YAHOO.mysimpledb.createItem.render();
        
    // Instantiate the Dialog
    YAHOO.mysimpledb.deleteItem = new YAHOO.widget.SimpleDialog("deleteItem", { 
        modal: true,
        width : "20em",
        fixedcenter : true,
        visible : false, 
        constraintoviewport : true,
        buttons: [ 
        { 
            text:"Yes", 
            handler:handleSubmit, 
            isDefault:true 
        },
        { 
            text:"No",  
            handler:handleCancel
        } ]
    });

    // Validate the entries in the for
    YAHOO.mysimpledb.deleteItem.validate = function() {
      YAHOO.mysimpledb.wait.show();
      return true;
    };

    // Wire up the success and failure handlers
    YAHOO.mysimpledb.deleteItem.callback = callback;
    
    // Render the Dialog
    YAHOO.mysimpledb.deleteItem.render();
    
	// Instantiate the Dialog
    YAHOO.mysimpledb.deleteValueKey = new YAHOO.widget.SimpleDialog("deleteValueKey", { 
        modal: true,
        width : "20em",
        fixedcenter : true,
        visible : false, 
        constraintoviewport : true,
        buttons: [ 
        { 
            text:"Yes", 
            handler:handleSubmit, 
            isDefault:true 
        },
        { 
            text:"No",  
            handler:handleCancel
        } ]
    });

    // Validate the entries in the for
    YAHOO.mysimpledb.deleteValueKey.validate = function() {
      YAHOO.mysimpledb.wait.show();
      return true;
    };

    // Wire up the success and failure handlers
    YAHOO.mysimpledb.deleteValueKey.callback = callback;
    
    // Render the Dialog
    YAHOO.mysimpledb.deleteValueKey.render();

    // Instantiate the Dialog
    YAHOO.mysimpledb.createDialog = new YAHOO.widget.SimpleDialog("createDialog", { 
        modal: true,
        width : "20em",
        fixedcenter : true,
        visible : false, 
        constraintoviewport : true,
        buttons: [ 
        { 
            text:"Yes", 
            handler:handleSubmit, 
            isDefault:true 
        },
        { 
            text:"No",  
            handler:handleCancel
        } ]
    });

    // Validate the entries in the for
    YAHOO.mysimpledb.createDialog.validate = function() {
      YAHOO.mysimpledb.wait.show();
      return true;
    };

    // Wire up the success and failure handlers
    YAHOO.mysimpledb.createDialog.callback = callback;
    
    // Render the Dialog
    YAHOO.mysimpledb.createDialog.render();
    
    // Instantiate the Dialog
     YAHOO.mysimpledb.selectDialog = new YAHOO.widget.SimpleDialog("selectDialog", { 
        modal: true,
        width : "20em",
        fixedcenter : true,
        visible : false, 
        constraintoviewport : true,
        buttons: [ 
        { 
            text:"Yes", 
            handler:handleSubmit, 
            isDefault:true 
        },
        { 
            text:"No",  
            handler:handleCancel
        } ]
    });

    // Validate the entries in the for
    YAHOO.mysimpledb.selectDialog.validate = function() {
      YAHOO.mysimpledb.wait.show();
      return true;
    };

    // Wire up the success and failure handlers
    YAHOO.mysimpledb.selectDialog.callback = callback;
    
    // Render the Dialog
    YAHOO.mysimpledb.selectDialog.render();
    
    // Set up auto-complete form    
    YAHOO.example.BasicRemote = function() {
        // Use an XHRDataSource
        var oDS = new YAHOO.util.XHRDataSource("listSelects.jsp");
        // Set the responseType
        oDS.responseType = YAHOO.util.XHRDataSource.TYPE_TEXT;
        // Define the schema of the delimited results
        oDS.responseSchema = {
            recordDelim: "\n",
            fieldDelim: "\t"
        };
        // Enable caching
        oDS.maxCacheEntries = 15;
    
        // Instantiate the AutoComplete
        var oAC = new YAHOO.widget.AutoComplete("selectField", "myContainer", oDS);
        oAC.maxResultsDisplayed = 30;
        oAC.useShadow = true;
        // Container will expand and collapse vertically
        oAC.animVert = true;
        // Container animation will take 1/2 seconds to complete
        oAC.animSpeed = 0.5;
       �oAC.setHeader("=== Press  &lt;esc> to ignore history OR &lt;enter>/ arrows to choose ===");
        
        return {
            oDS: oDS,
            oAC: oAC
        };
    }();
}

YAHOO.util.Event.onDOMReady(init);

function makeRequest(domain){
  //div element to update
  divElement = "listItemNames";

  //pop up the select value
  var select = document.getElementById("selectField");
  select.value = "select * from " + domain;
  
  YAHOO.mysimpledb.wait.show();

  popSelect();
  //var sUrl = "listItemNames.jsp?Action=exploreDomain&domainName="+domain;
  //var request = YAHOO.util.Connect.asyncRequest('GET', sUrl, callback);
};


function makeRequestSort(domain, items, where, order, limit){
  //div element to update
  divElement = "listItemNames";

  //pop up the select value
  var select = document.getElementById("selectField");
  select.value = "select " + items + " from " + domain + " " + where + 
    " " + order + " " + limit;
  
  YAHOO.mysimpledb.wait.show();

  popSelect();
  //var sUrl = "listItemNames.jsp?Action=exploreDomain&domainName="+domain;
  //var request = YAHOO.util.Connect.asyncRequest('GET', sUrl, callback);
};

window.popAlertDialog = function(head, body) {
  YAHOO.mysimpledb.alertDialog.setHeader(head); 
  YAHOO.mysimpledb.alertDialog.setBody(body); 
  YAHOO.mysimpledb.alertDialog.cfg.setProperty("icon",YAHOO.widget.SimpleDialog.ICON_BLOCK);
  YAHOO.mysimpledb.alertDialog.show();
};

window.popDeleteDomain = function(head, body, domain) {
  //div element to update
  divElement = "listDomainNames";
  YAHOO.mysimpledb.deleteDomain.setHeader(head); 
  YAHOO.mysimpledb.deleteDomain.setBody(body); 
  YAHOO.mysimpledb.deleteDomain.cfg.setProperty("icon",YAHOO.widget.SimpleDialog.ICON_WARN); 
  YAHOO.mysimpledb.deleteDomain.form.action="listDomainNames.jsp?Action=deleteDomain";
  YAHOO.mysimpledb.deleteDomain.form.deleteDomain.value = domain;
  //needed to realy post things, otherwise doesnt work (hard work).
  YAHOO.mysimpledb.deleteDomain.cfg.setProperty("postmethod", "async");
  //alert(YAHOO.mysimpledb.deleteDomain.body.innerHTML);
  YAHOO.mysimpledb.deleteDomain.show();
};

window.popRefreshDomains = function(nextToken) {
  //div element to update
  divElement = "listDomainNames";
  YAHOO.mysimpledb.wait.show();
  var sUrl = "listDomainNames.jsp?Action=exploreDomain&domainNextToken="+
    nextToken;
  //alert("refresh");
  var request = YAHOO.util.Connect.asyncRequest('GET', sUrl, callback);
};

window.popCreateDomain = function() {
  //div element to update
  divElement = "listDomainNames";
  YAHOO.mysimpledb.createDomain.show();
};

function getHiddenField(key, value){
  var i = document.createElement('input');
  i.setAttribute('type', 'hidden');
  i.setAttribute('id', key);
  i.setAttribute('name', key);
  i.setAttribute('value', value);
  return i;
}

window.popDeleteItem = function(head, body, item, domain) {
  //div element to update
  divElement = "listItemNames";
  YAHOO.mysimpledb.deleteItem.setHeader(head); 
  YAHOO.mysimpledb.deleteItem.setBody(body); 
  YAHOO.mysimpledb.deleteItem.cfg.setProperty("icon",YAHOO.widget.SimpleDialog.ICON_WARN); 
  YAHOO.mysimpledb.deleteItem.form.action="listItemNames.jsp?Action=deleteItem";
  YAHOO.mysimpledb.deleteItem.form.deleteItem.value = item;
  var element = getHiddenField("domainName", domain);
  YAHOO.mysimpledb.deleteItem.form.appendChild(element);
  //needed to realy post things, otherwise doesnt work (hard work).
  YAHOO.mysimpledb.deleteItem.cfg.setProperty("postmethod", "async");
  //alert(YAHOO.mysimpledb.deleteItem.body.innerHTML);
  YAHOO.mysimpledb.deleteItem.show();
};

window.popCreateItem = function(item, domain) {
  //div element to update
  divElement = "listItemNames";
  if (item != "") {
    
    //remove any new item elements, cause its not
    var element = document.getElementById("itemNew");
    if (element) 
      YAHOO.mysimpledb.createItem.form.removeChild(element);

    YAHOO.mysimpledb.createItem.setItem(item, domain);
    YAHOO.mysimpledb.createItem.form.itemName.disabled=true;
  } else {
    var element = getHiddenField("itemNew", "true");
    YAHOO.mysimpledb.createItem.form.appendChild(element);
    YAHOO.mysimpledb.createItem.setItem("", domain);  
    YAHOO.mysimpledb.createItem.form.itemName.disabled=false;
  }
  YAHOO.mysimpledb.createItem.show();
};

window.popDeleteValueKey = function(head, body, key, value, item, domain) {
  //div element to update
  divElement = "listItemNames";
  YAHOO.mysimpledb.deleteValueKey.setHeader(head); 
  YAHOO.mysimpledb.deleteValueKey.setBody(body); 
  YAHOO.mysimpledb.deleteValueKey.cfg.setProperty("icon",YAHOO.widget.SimpleDialog.ICON_WARN); 
  YAHOO.mysimpledb.deleteValueKey.form.action="listItemNames.jsp?Action=deleteValueKey";

  var element = getHiddenField("domainName", domain);
  YAHOO.mysimpledb.deleteValueKey.form.appendChild(element);
  element = getHiddenField("itemName", item);
  YAHOO.mysimpledb.deleteValueKey.form.appendChild(element);
  element = getHiddenField("keyName", key);
  YAHOO.mysimpledb.deleteValueKey.form.appendChild(element);
  element = getHiddenField("value", value);
  YAHOO.mysimpledb.deleteValueKey.form.appendChild(element);
  //needed to realy post things, otherwise doesnt work (hard work).
  YAHOO.mysimpledb.deleteValueKey.cfg.setProperty("postmethod", "async");
  //alert(YAHOO.mysimpledb.deleteValueKey.body.innerHTML);
  YAHOO.mysimpledb.deleteValueKey.show();
};

window.popCreate = function() { 
  var element = YAHOO.mysimpledb.createDialog.form;
  
  //remove any new item elements, cause its not
  while (element.firstChild) {
    element.removeChild(element.firstChild);
  }

  //needed to realy post things, otherwise doesnt work (hard work).
  YAHOO.mysimpledb.createDialog.cfg.setProperty("postmethod", "async");
  //div element to update
  divElement = "listDomainNames";
  YAHOO.mysimpledb.createDialog.form.action="listDomainNames.jsp?Action=select";
  //alert(YAHOO.mysimpledb.selectDialog.body.innerHTML);
  var select = document.getElementById("selectField");
  var element = getHiddenField("select", select.value);
  //alert(select.value);
  YAHOO.mysimpledb.createDialog.form.appendChild(element);

  
  //alert(select.value);
  //alert(YAHOO.mysimpledb.selectDialog.body.innerHTML);
  YAHOO.mysimpledb.createDialog.submit();
	  
};
    
window.popSelect = function(cursorToken) { 

  
  var element = YAHOO.mysimpledb.selectDialog.form;
  
  //remove any new item elements, cause its not
  while (element.firstChild) {
    element.removeChild(element.firstChild);
  }

  //needed to realy post things, otherwise doesnt work (hard work).
  YAHOO.mysimpledb.selectDialog.cfg.setProperty("postmethod", "async");
  //div element to update
  divElement = "listItemNames";
  YAHOO.mysimpledb.selectDialog.form.action="listItemNames.jsp?Action=select";
  //alert(YAHOO.mysimpledb.selectDialog.body.innerHTML);
  var select = document.getElementById("selectField");
  var element = getHiddenField("select", select.value);
  
  //alert(select.value);
  YAHOO.mysimpledb.selectDialog.form.appendChild(element);

  if (cursorToken) {
	  YAHOO.mysimpledb.selectDialog.form.action+="&itemNextToken="+cursorToken;
      //alert(cursorToken);
  }
  
  //alert(select.value);
  //alert(YAHOO.mysimpledb.selectDialog.body.innerHTML);
  YAHOO.mysimpledb.selectDialog.submit();
  
};
</script>