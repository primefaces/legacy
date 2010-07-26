if(PrimeFaces == undefined) var PrimeFaces = {};
if(PrimeFaces.widget == undefined) PrimeFaces.widget = {};

PrimeFaces.widget.ImageSwitcher = function(clientId, cfg, images) {
	
	this.imgClientId = PrimeFaces.core.Utils.escapeClientId(clientId);
	this.cfg = cfg;
	this.images = images;
	this.imgIdx = 0;

	for(var i=0; i<images.length;i++) {
		jQuery.ImagePreload(images[i]);
	}
	
	if(this.cfg.slideshowAuto)
		this.startSlideshow();
}

PrimeFaces.widget.ImageSwitcher.prototype.switchImage = function() {
	if(!jQuery(this.imgClientId).ImageAnimating()){  
        jQuery(this.imgClientId).ImageSwitch(
        						{
        							Type: this.cfg.effect,  
        							NewImage:this.images[this.imgIdx], 
        							Speed:this.cfg.speed  
                                });
    }
}

PrimeFaces.widget.ImageSwitcher.prototype.startSlideshow = function() {
	var imageSwitcher = this;
	this.animation = setInterval(
							function(){
								imageSwitcher.next();
							}, this.cfg.slideshowSpeed);
}

PrimeFaces.widget.ImageSwitcher.prototype.stopSlideshow = function() {
	clearInterval(this.animation); 
}

PrimeFaces.widget.ImageSwitcher.prototype.next = function() {
	if(this.imgIdx == (this.images.length - 1))
		this.imgIdx = 0;
	else
		this.imgIdx++;
	
	this.switchImage();
}

PrimeFaces.widget.ImageSwitcher.prototype.previous = function() {
	if(this.imgIdx == 0)
		this.imgIdx = this.images.length - 1;
	else
		this.imgIdx--;
	
	this.switchImage();
}