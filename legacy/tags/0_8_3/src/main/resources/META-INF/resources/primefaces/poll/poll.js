if(PrimeFaces == undefined) var PrimeFaces = {};
if(PrimeFaces.widget == undefined) PrimeFaces.widget = {};

PrimeFaces.widget.Poll = function(cfg) {
	this.cfg = cfg;
	var url = PrimeFaces.ajax.AjaxUtils.encodeURL(cfg.url);
	var encodedViewState = PrimeFaces.ajax.AjaxUtils.encodeViewState();
	
	var params = "primefacesAjaxRequest=true&javax.faces.ViewState=" + encodedViewState;		//core params
	params = params + "&" + cfg.clientId + "=" + cfg.clientId;									//poll clientId
	params = params + "&update=" + cfg.update;													//components to update
	params = params + "&" + PrimeFaces.ajax.AjaxUtils.getFormParam(cfg.formId);					//mark parent form as submitted
	
	this.periodicalUpdater = new Ajax.PeriodicalUpdater(this.cfg.clientId, url, 
			{
				method:"post",
				parameters:params, 
				frequency:cfg.frequency, 
				decay:cfg.decay,
				onSuccess: PrimeFaces.ajax.AjaxResponse.success
			});
}

PrimeFaces.widget.Poll.prototype.start = function() {
	this.periodicalUpdater.start();
}

PrimeFaces.widget.Poll.prototype.stop = function() {
	this.periodicalUpdater.stop();
}