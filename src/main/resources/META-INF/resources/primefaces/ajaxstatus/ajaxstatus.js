if(PrimeFaces == undefined) var PrimeFaces = {};
if(PrimeFaces.widget == undefined) PrimeFaces.widget = {};

PrimeFaces.widget.AjaxStatus = function() {}

PrimeFaces.widget.AjaxStatus.prototype.bindFacet = function(eventName, elToShow, elToHide) {
	jQuery("#" + elToShow).bind(eventName, function() {
		jQuery(this).show();
		
		if(elToHide != undefined)
			jQuery("#" + elToHide).hide();
	});
}

PrimeFaces.widget.AjaxStatus.prototype.bindEvent = function(eventName, fn) {
	jQuery().bind(eventName, fn);
}