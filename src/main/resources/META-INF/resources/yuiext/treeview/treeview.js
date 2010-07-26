if(PrimeFaces == undefined) var PrimeFaces = {};
if(PrimeFaces.widget == undefined) PrimeFaces.widget = {};

PrimeFaces.widget.TreeView = function(id, config) {
	PrimeFaces.widget.TreeView.superclass.constructor.call(this, id, config);
	
	this.subscribe("labelClick", this.labelClickListener);
	this.subscribe("expand", this.expandListener);
	this.subscribe("collapse", this.collapseListener);
}

YAHOO.lang.extend(PrimeFaces.widget.TreeView, YAHOO.widget.TreeView,
{
	TOGGLE_MODE_CLIENT : "client",
	TOGGLE_MODE_ASYNC : "async",
	
	clientId : null,
		
	rowKeyFieldId : null,
	
	eventFieldId : null,
	
	formId : null,
	
	toggleMode : null,
	
	actionURL : null,
	
	labelClickListener : function(node) {
		document.getElementById(this.eventFieldId).value = "SELECT";
		document.getElementById(this.rowKeyFieldId).value = node.data.rowKey;
	},
	
	expandListener : function(node) {
		document.getElementById(this.eventFieldId).value = "EXPAND";
		document.getElementById(this.rowKeyFieldId).value = node.data.rowKey;
	},
	
	collapseListener : function(node) {
		document.getElementById(this.eventFieldId).value = "COLLAPSE";
		document.getElementById(this.rowKeyFieldId).value = node.data.rowKey;
	},
	
	setToggleMode : function(mode) {
		this.toggleMode = mode;
		if(mode == this.TOGGLE_MODE_ASYNC)
			 this.setDynamicLoad(this.doDynamicLoadNodeRequest);
	},

	doDynamicLoadNodeRequest : function(node, fnLoadComplete) {
		var url = PrimeFaces.ajax.AjaxUtils.encodeURL(this.tree.actionURL);
		var viewstate = PrimeFaces.ajax.AjaxUtils.encodeViewState();
		
		//postdata
		var params = "ajaxSource=" + this.tree.clientId;
		params = params + "&primefacesAjaxRequest=true";
		params = params + "&" + this.tree.rowKeyFieldId + "=" + document.getElementById(this.tree.rowKeyFieldId).value;
		params = params + "&" + this.tree.eventFieldId + "=" + document.getElementById(this.tree.eventFieldId).value;
		params = params + "&javax.faces.ViewState=" + viewstate;
		params = params + "&" + this.tree.formId + "=" + this.tree.formId;
		
		var callback = {
		  success: this.tree.handleDynamicLoadSuccess,
		  failure: this.tree.handleDynamicLoadFailure,
		  argument: {
			"node": node,
			"fnLoadComplete": fnLoadComplete
			}
		};

		YAHOO.util.Connect.asyncRequest('POST', url, callback, params);
	},
	
	handleDynamicLoadSuccess : function(response) {
		var node = response.argument.node;
		var xmlDoc = response.responseXML.documentElement;
		var nodes = xmlDoc.getElementsByTagName("node");
		
		for(var i=0; i < nodes.length; i++) {
			var labelValue = nodes[i].childNodes[0].firstChild.data;
			var rowKeyValue = nodes[i].childNodes[1].firstChild.data;
			var isLeafStringValue = nodes[i].childNodes[2].firstChild.data;
			var isLeafValue = (isLeafStringValue == "true") ? true : false;
			
			var nodeData = {label: labelValue, rowKey: rowKeyValue, isLeaf: isLeafValue};
			
			var tempNode = new YAHOO.widget.TextNode(nodeData, node, false);
		}
		
		response.argument.fnLoadComplete();
	},
	
	handleDynamicLoadFailure : function(response) {
		alert("Failed:" + response.responseText);
	}
});