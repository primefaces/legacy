if(PrimeFaces == undefined) var PrimeFaces = {};
if(PrimeFaces.ajax == undefined) PrimeFaces.ajax = {};

PrimeFaces.ajax.AjaxUtils = {

	encodeURL : function(url) {
		if(url.indexOf("jsessionid") == -1) {
			var sessionId = YAHOO.util.Cookie.get("JSESSIONID");
			
			if(sessionId != null)
				return url + "?jsessionid=" + sessionId;
		}
		
		return url;
	},
	
	encodeViewState : function() {
		var viewstateValue = document.getElementById("javax.faces.ViewState").value;
		var re = new RegExp("\\+", "g");
		var encodedViewState = viewstateValue.replace(re, "\%2B");
		
		return encodedViewState;
	},
	
	getFormParam : function(formId) {
		var mojarraFormId = formId;
		var myfacesFormId = formId + "_SUBMIT";
		
		//unfortunately implementations behave differently when marking form as submitted
		var isMyfaces = document.getElementsByName(myfacesFormId).length == 1;
		
		if(isMyfaces)
			return myfacesFormId + "=1"; 
		else
			return mojarraFormId + "=" + mojarraFormId;
	}
};

PrimeFaces.ajax.AjaxRequestEvent = function(url,cfg,parameters) {
	this.url = url;
	this.cfg = cfg;
	this.parameters = parameters;
}

//Command components like buttons should attach this function to on* events to initiate an ajax request
PrimeFaces.ajax.AjaxRequestEventHandler = function(domEvent, ajaxRequestEvent) {
	PrimeFaces.ajax.AjaxRequest(ajaxRequestEvent.url, ajaxRequestEvent.cfg, ajaxRequestEvent.parameters);
}

PrimeFaces.ajax.AjaxRequest = function(url,cfg,parameters) {
	var encodedURL = PrimeFaces.ajax.AjaxUtils.encodeURL(url);
	var formParams = null;

	if(cfg.partialSubmit == true) {
		formParams = PrimeFaces.ajax.AjaxUtils.getFormParam(cfg.formClientId);																	//form id to mark form as submitted
		formParams = formParams + "&javax.faces.ViewState=" + PrimeFaces.ajax.AjaxUtils.encodeViewState();						//viewstate
		
		if(cfg.ajaxifiedComponent != undefined)
			formParams = formParams + "&" + cfg.ajaxifiedComponent + "=" + document.getElementById(cfg.ajaxifiedComponent).value;	//ajaxified component to partially process
	}
	else {
		formParams = Form.serialize(document.getElementById(cfg.formClientId));
	}
	
	var params = formParams + "&" + parameters;
	
	if(cfg.onComplete == undefined) {
		new Ajax.Request(encodedURL,
			{
				method:'post',
				parameters: params,
				onSuccess: PrimeFaces.ajax.AjaxResponse.success,
				onFailure: PrimeFaces.ajax.AjaxResponse.failure
			});
	} else {
		new Ajax.Request(encodedURL,
			{
				method:'post',
				parameters: params,
				onSuccess: PrimeFaces.ajax.AjaxResponse.success,
				onFailure: PrimeFaces.ajax.AjaxResponse.failure,
				onComplete: cfg.onComplete
			});
	}
}

PrimeFaces.ajax.AjaxResponse = {};

PrimeFaces.ajax.AjaxResponse.success = function(response) {
	var xmlDoc = response.responseXML.documentElement;
	var components = xmlDoc.getElementsByTagName("component");
	
	for(var i=0;i<components.length;i++) {
		var clientId = components[i].childNodes[0].firstChild.data;
		var output = components[i].childNodes[1].firstChild.data;
		
		Element.replace(clientId, output);
	}
}

PrimeFaces.ajax.AjaxRequest.failure = function(response) {
	
}