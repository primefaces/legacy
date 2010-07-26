if(PrimeFaces == undefined) var PrimeFaces = {};
if(PrimeFaces.widget == undefined) PrimeFaces.widget = {};

PrimeFaces.widget.AutoCompleteUtils = {
		
	generateRequest : function(generateRequest) {
		var viewstateValue = document.getElementById('javax.faces.ViewState').value;
		var re = new RegExp("\\+", "g");
		var encodedViewState = viewstateValue.replace(re, "\%2B");
		
		return "&autoCompleteQuery=" + generateRequest + "&javax.faces.ViewState=" + encodedViewState + "&primefacesAjaxRequest=true";
	}
};

