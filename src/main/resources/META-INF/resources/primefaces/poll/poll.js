if(PrimeFaces == undefined) var PrimeFaces = {};
if(PrimeFaces.widget == undefined) PrimeFaces.widget = {};

PrimeFaces.widget.Poll = function(cfg) {
	this.cfg = cfg;
	this.start();
}

PrimeFaces.widget.Poll.prototype.start = function() {
	var cfg = this.cfg;
	
	this.timer = setInterval(
			function() {
				PrimeFaces.ajax.AjaxRequest(cfg.url, 
							{formClientId:cfg.formClientId, partialSubmit:false}
							,"update=" + cfg.update + "&" + cfg.clientId + "=" + cfg.clientId);
		}
		,(cfg.frequency * 1000));
}

PrimeFaces.widget.Poll.prototype.stop = function() {
	clearInterval(this.timer);
}