/**
 * Copyright: Elements, public domain
 * Author: e.moojen@chello.nl
 * Version: $Id$
 */
//editable variables
var _followMouse=true;
var _captureBox=true;
var _fadeTime=500;
var _onTimeout=500;
var _opacity=98;
var _opacityFrame=20;
var _offX=10;
var _offY=4; 


//init
var _d=document;
var _dbg=false; //debug
var _ua=navigator.userAgent;
var _dom = (_d.getElementById) ? true : false;
var _ns5 = ((_ua.indexOf("Gecko")>-1) && _dom);
var _ie5 = ((_ua.indexOf("MSIE")>-1) && _dom);
var _ns4 = (_d.layers && !_dom);
var _ie4 = (_d.all && !_dom);
var _saf = ((_ua.indexOf("safari")>-1) && _dom);
var _opa = ((_ua.indexOf("opera")>-1) && window._opa);
var _nody = (!_ns5 && !_ie4 && !_ie5);
var _tip, _css, _mouseX, _mouseY, _pause;
var _init = 0;
var _tipOn = false;
var _t1 = new Array()
var _fadeDone=false;
var _frz = false;
var _divarea;

//avoid errors
if (_nody) { event = "nope" }

function _ini(){

 if (_nody) return;

  //new div
 _tip = _d.createElement("div");
 _tip.setAttribute('id','tooltip');

 if (_captureBox){  
  if (_tip.addEventListener){
   _tip.addEventListener("mouseover", _wait, true);
   _tip.addEventListener("mouseout", _leave, true);
  } else if (_tip.attachEvent){
   _tip.attachEvent("onmouseover", _wait);
   _tip.attachEvent("onmouseout", _leave);        
  }
 }

 window._d.body.appendChild(_tip);

 _css = _tip.style;
 if (_saf) _css.position = 'relative';
 else _css.position = 'absolute'; //bugfix safari

 _css.zIndex = 100;

 if (_tip&&_followMouse){
   _d.onmousemove = _track;
 }
 _init = 1;
 _drgobj.init();
}


function elementById(x) { 
  if (document.getElementById) return document.getElementById(x); 
    else if (document.all) return document.all[x]; 
    else if (document.layers) return document.layers[x];
    else return null;
}

function _wait(evt){
 if(_pause || _frz) return;
 _escape();
 opacity('tooltip', getOp('tooltip'), _opacity, _onTimeout/2);
 if (_dbg) _tip.innerHTML+=",_wait";
 _pause=true;
 _tipOn = true;
}

function _leave(evt){
 if (_frz) return;
 if (_dbg) _tip.innerHTML+=",_leave";
 _pause=false;
 _hide();
}

function _escape(){
 if (_frz) return;
 if (_dbg) _tip.innerHTML+=",_escape";
 for(i = 0; i < _t1.length; i++)
  if (_t1[i]) clearTimeout(_t1[i]);
}

function _rnd(num, rlen) {
 var nnum = Math.round(num*Math.pow(10,rlen))/Math.pow(10,rlen);
 return nnum;
}

function getMaxX(coords) {
 var coordsArray=coords.split(",");
 var maxX = 0;
 for(i=0;i<coordsArray.length;i=i+2) {
  var value =  _rnd(coordsArray[i],1);
  if (value>maxX) {
   maxX = value;
  }
 }
 return maxX;
}
function getMinX(coords) {
 var minX;
 var coordsArray=coords.split(",");
 for(i=0;i<coordsArray.length;i=i+2) {
  var value =  _rnd(coordsArray[i],1);
  if (i==0) minX = value;
  if (value<minX) {
   minX = value;
  }
 }
 return minX;
}
function getMaxY(coords) {
 var coordsArray=coords.split(",");
 var maxY = 0;
 for(i=1;i<coordsArray.length;i=i+2) {
  var value =  _rnd(coordsArray[i],1);
  if (value>maxY) {
   maxY = value;
  }
 }
 return maxY;
}
function getMinY(coords) {
 var coordsArray=coords.split(",");
 var minY;
 for(i=1;i<coordsArray.length;i=i+2) {
  var value =  _rnd(coordsArray[i],1);
  if (i==1) minY = value;
  if (value<minY) {
   minY = value;
  }
 }
 return minY;
}

function _show(evt,_div,_id) {

 if (_init==0) { _ini(); setOp(0, 'tooltip'); } 
 //bug fix ie when first event is on link
 if (_init==1) { _track(evt); _init = 2; }

 if (!_tip || _frz) return;

 _tipOn = true;

 if (_dbg) _tip.innerHTML+=",_show";

 _escape();
 if (_ie4||_ie5||_ns5){
   _tip.innerHTML = _d.getElementById(_div).innerHTML;
 }
 
 if (_dbg) _tip.innerHTML+=",_followMouse"+_followMouse;
 if (!_followMouse){
   if (_dbg) _tip.innerHTML+=",_position(evt)";
  _position(evt);
  
 } else {
  opacity('tooltip', getOp('tooltip'), _opacity, _onTimeout);
 }
 
 //highlight area
 evt.target = evt.target || evt.srcElement;
 if (_divarea == null || _divarea == undefined) {
 _divarea = evt.target.parentNode.getAttribute("name");
 }
 if (_divarea) {
   if (elementById(_divarea) != null) {
    coords = evt.target.getAttribute("coords");
    if(coords == null) return;
    var minX = getMinX(coords);
    var maxX = getMaxX(coords);
    
    var minY = getMinY(coords);
    var maxY = getMaxY(coords);
    
    glow = elementById(_divarea);
    glow.style.left=(minX) + "px"
    glow.style.top=(minY+1) + "px"
    glow.style.width=(maxX - minX + 1) + "px";
    glow.style.height=(maxY - minY - 1) + "px";
    glow.style.visibility="visible";
    
    glow.setAttribute("onmouseout",evt.target.getAttribute("onmouseout"));
    glow.setAttribute("onmouseover",evt.target.getAttribute("onmouseover"));
    if (evt.target.getAttribute("href")==null) {
      glow.parentNode.setAttribute("href", "#");
      glow.parentNode.style.cursor="default";
      
    } else {
      glow.parentNode.setAttribute("target", evt.target.getAttribute("target"));
      glow.parentNode.setAttribute("href", evt.target.getAttribute("href"));
      glow.parentNode.style.cursor="hand";
    }
    glow.parentNode.setAttribute("onclick", evt.target.getAttribute("onclick"));

   }
 }

}

function _track(evt) {

 if (_pause ||_frz) return;
 _mouseX = (_ns5)? evt.pageX: window.event.clientX + _d.body.scrollLeft;
 _mouseY = (_ns5)? evt.pageY: window.event.clientY + _d.body.scrollTop;

 if (_tipOn) _position(evt);
}

function _position(evt) {
 if (!_tipOn || _pause ||_frz) return;

 if (!_followMouse) {
   _mouseX = (_ns5)? evt.pageX: window.event.clientX + _d.body.scrollLeft;
   _mouseY = (_ns5)? evt.pageY: window.event.clientY + _d.body.scrollTop;
 }

 var tpWd = (_ie4||_ie5)? _tip.clientWidth: _tip.offsetWidth;
 var tpHt = (_ie4||_ie5)? _tip.clientHeight: _tip.offsetHeight;

 var winWd = (_ns5)? window.innerWidth-20+window.pageXOffset: 
   _d.body.clientWidth+_d.body.scrollLeft;

 var winHt = (_ns5)? window.innerHeight-20+window.pageYOffset: 
   _d.body.clientHeight+_d.body.scrollTop;

 // check mouse and position the _tip 
 if ((_mouseX+_offX+tpWd)>winWd) {
   if (_mouseX-(tpWd+_offX) > _offX) {
    lefttip = _mouseX-(tpWd+_offX);
   } else {
    lefttip = 5;
   }
 }
 else {
    lefttip = _mouseX+_offX;
 }
 if ((_mouseY+_offY+tpHt)>winHt) {
   if (_mouseY - (tpHt +_offY) > _offY) {
    toptip = _mouseY - (tpHt +_offY);
   } else {
    toptip = 5;
   }
 }
 else {
   toptip = _mouseY+_offY;
 }

 _css.left=lefttip+"px";
 _css.top=toptip+"px";


 if (_saf) _css.position = 'absolute';

 if (!_followMouse) {opacity('tooltip', getOp('tooltip'), _opacity, _onTimeout);}
 if (_dbg) _tip.innerHTML+=",_position";
}

function _cancel(){
 _escape();
 opacity('tooltip', 1, 0, 0);
 _tipOn=false;
 _pause=false;
 _frz=false;
}

function _freeze(){
 if (_dbg) _tip.innerHTML+=",_freeze";
 _frz=true;
}

function _unfreeze(){
 if (_dbg) _tip.innerHTML+=",_unfreeze";
 _frz=false;
}

function _hide() {
 if (_pause || _frz) return;
 if (_dbg) _tip.innerHTML+=",_hide";
 _escape();
 opacity('tooltip', getOp('tooltip'), 0, _fadeTime);
 _tipOn=false;
}

//source: http://brainerror.net/scripts/javascript/blendtrans/
//licence: public domain
function opacity(id, opSt, opEn, millisec) {

 //speed / frame
 var speed = Math.round(millisec / _opacity);
 var timer = 5;

 //direction
 if(opSt > opEn) {
  for(i = opSt; i >= opEn; i--) {
   _t1[i]=setTimeout((i==opEn)?"setOp(" + i + ",'" + id + "');_tip.innerHTML='';":"setOp(" + i + ",'" + id + "');",(timer * speed));
   timer++;
  }
 } else if(opSt < opEn) {
  for(i = opSt; i <= opEn; i++)
   {
   _t1[i]=setTimeout("setOp(" + i + ",'" + id + "');",(timer * speed));
   timer++;
  }
  
 }
}

//opacity
function setOp(op, id) {
 var obj = _d.getElementById(id).style; 
 obj.opacity = (op / 100);
 obj.MozOpacity = (op / 100);
 obj.KhtmlOpacity = (op / 100);
 obj.filter = "alpha(opacity=" + op + ")";
 
 if (_d.getElementById(_divarea) != null && _divarea != null && op<_opacityFrame) {
 var obj = _d.getElementById(_divarea).style; 
 obj.opacity = (op / 100);
 obj.MozOpacity = (op / 100);
 obj.KhtmlOpacity = (op / 100);
 obj.filter = "alpha(opacity=" + op + ")";
 }
 
}

function getOp(id) {
 //standard
 var curOp = _opacity;
    
 //set & get opacity
 if(_d.getElementById(id).style.opacity < _opacity) {
  curOp = _d.getElementById(id).style.opacity * 100;
 }

 return curOp;
}

/***********************************************
* Drag and Drop Script: Dynamic Drive (http://www.dynamicdrive.com)
* This notice MUST stay intact for legal use
* Visit http://www.dynamicdrive.com/ for this script and 100s more.
***********************************************/
var _drgobj= {
 z: 0, x: 0, y: 0, offx : null, offy : null, _tgt : null, _drgapr : 0,
  init:function() {
  _d.onmousedown=this.drag
  _d.onmouseup=function(){this._drgapr=0}
 },
 drag:function(e) {
  var obj=window.event?window.event : e
  this._tgt=window.event?event.srcElement : e.target
  if (this._tgt.className=="drag") {
   this._drgapr=1
   if (isNaN(parseInt(_tip.style.left))) {
    _tip.style.left=0
   }
   if (isNaN(parseInt(_tip.style.top))) {
    _tip.style.top=0
   }
   this.offx=parseInt(_tip.style.left)
   this.offy=parseInt(_tip.style.top)
   this.x=obj.clientX
   this.y=obj.clientY
   if (obj.preventDefault)
     obj.preventDefault()
   _d.onmousemove=_drgobj.move
  }
 },
 move:function(e) {
  _track(e);
  var obj=window.event? window.event : e
  if (this._drgapr==1){
  _tip.style.left=this.offx+obj.clientX-this.x+"px"
  _tip.style.top=this.offy+obj.clientY-this.y+"px"
  return false
  }
 }
}
