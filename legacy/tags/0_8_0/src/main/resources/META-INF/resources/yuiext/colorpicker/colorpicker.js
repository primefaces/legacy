if(PrimeFaces == undefined) var PrimeFaces = {};
if(PrimeFaces.widget == undefined) PrimeFaces.widget = {};

PrimeFaces.widget.ColorPickerUtils = {
		
	selectColor : function(event, obj) {
		var color = event.newValue;
		var value = color[0] + "," + color[1] + "," + color[2];
		
		document.getElementById(obj.hiddenInputId).value = value;
		
		YAHOO.util.Dom.setStyle(obj.currentColorDisplay, "backgroundColor", "rgb(" + value + ")"); 
	},
	
	toggleColorPicker : function(event, obj) {
		var visible = obj.panel.cfg.getProperty("visible"); 
		
		if(visible)
			obj.panel.hide();
		else
			obj.panel.show();
	}
};