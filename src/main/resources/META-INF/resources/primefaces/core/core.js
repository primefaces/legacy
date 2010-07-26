YAHOO.util.Event.addListener(window, "load", 
	function() {
		if(!YAHOO.util.Dom.hasClass(document.body, "yui-skin-sam"))
			YAHOO.util.Dom.addClass(document.body, "yui-skin-sam");
	}
);

if(PrimeFaces == undefined) var PrimeFaces = {};
if(PrimeFaces.core == undefined) PrimeFaces.core = {};

PrimeFaces.core.Utils = {

	//Utility method to help jQuery work with JSF clientIds
	escapeClientId : function(id) {
		return "#" + id.replace(/:/g,"\\:");
	}
};

