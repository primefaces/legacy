if(PrimeFaces == undefined) var PrimeFaces = {};
if(PrimeFaces.touch == undefined) PrimeFaces.touch = {};

PrimeFaces.touch.Application = function(config) {
	this.cfg = config;
	var themePath = config.themePath;
	this.cfg.preloadImage = [themePath + '/img/chevron_white.png',
	                         themePath + '/img/bg_row_select.gif',
	                         themePath + '/img/back_button.png',
	                         themePath + '/img/back_button_clicked.png',
	                         themePath + '/img/button_clicked.png',
	                         themePath + '/img/grayButton.png',
	                         themePath + '/img/whiteButton.png',
	                         themePath + '/img/loading.gif'];
	
     jQuery.jQTouch(this.cfg);
}