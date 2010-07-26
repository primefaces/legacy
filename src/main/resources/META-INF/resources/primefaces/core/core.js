YAHOO.util.Event.addListener(window, "load", 
	function() {
		if(!YAHOO.util.Dom.hasClass(document.body, "yui-skin-sam"))
			YAHOO.util.Dom.addClass(document.body, "yui-skin-sam");
	}
);