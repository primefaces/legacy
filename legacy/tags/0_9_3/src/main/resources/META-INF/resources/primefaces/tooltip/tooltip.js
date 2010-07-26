if(PrimeFaces == undefined) var PrimeFaces = {};
if(PrimeFaces.widget == undefined) PrimeFaces.widget = {};

PrimeFaces.widget.Tooltip = function(options) {
	this.cfg = options;
	var target = "";
	
	if(options.global) {
		target = "*[title]";
	}else {
		target = PrimeFaces.core.Utils.escapeClientId(options.forComponent);
	}
	
	jQuery(target).qtip(this.cfg);
}