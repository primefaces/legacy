package org.primefaces.ui.component.ajaxstatus;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.primefaces.ui.renderkit.CoreRenderer;

public class AjaxStatusRenderer extends CoreRenderer {

	public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
		AjaxStatus status = (AjaxStatus) component;

		encodeAjaxStatusScript(context, status);
		encodeAjaxStatusMarkup(context, status);
	}

	protected void encodeAjaxStatusScript(FacesContext facesContext, AjaxStatus status) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		String clientId = status.getClientId(facesContext);
		UIComponent start = status.getFacet("start");
		UIComponent complete = status.getFacet("complete");
		
		writer.startElement("script", null);
		writer.writeAttribute("type", "text/javascript", null);
		
		writer.write("YAHOO.util.Event.onContentReady(\"" + clientId + "\", function() {\n");
		
		writer.write("Ajax.Responders.register({\n");
		
		if(status.getOnstart() != null) {
			writer.write("onCreate: function(){\n");
			writer.write(status.getOnstart());
			writer.write("},\n");
		} else if(start != null) {
			writer.write("onCreate: function(){\n");
			if(complete != null) {
				writer.write("$('" + clientId + ":complete').hide();\n");
			}
			writer.write("$('" + clientId + ":start').show();\n");
			writer.write("},\n");
		}
		
		if(status.getOncomplete() != null) {
			writer.write("onComplete: function(){\n");
			writer.write(status.getOncomplete());
			writer.write("}\n");
		} else if(complete != null) {
			writer.write("onComplete: function(){\n");
			if(start != null) {
				writer.write("$('" + clientId + ":start').hide();\n");
			}
			writer.write("$('" + clientId + ":complete').show();\n");
			writer.write("}");
		}
		writer.write("});\n");
		
		writer.write("});");
		writer.endElement("script");

	}

	protected void encodeAjaxStatusMarkup(FacesContext facesContext, AjaxStatus status) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		String clientId = status.getClientId(facesContext);
		
		writer.startElement("span", null);
		writer.writeAttribute("id", clientId, null);
		
		encodeStatusFacetMarkup(facesContext, status, "start");
		encodeStatusFacetMarkup(facesContext, status, "complete");
		
		writer.endElement("span");
	}
	
	private void encodeStatusFacetMarkup(FacesContext facesContext, AjaxStatus status, String facetName) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		String clientId = status.getClientId(facesContext);
		
		writer.startElement("span", null);
		writer.writeAttribute("id", clientId + ":" + facetName, null);
		writer.writeAttribute("style", "display:none", null);
		
		UIComponent facet = status.getFacet(facetName);
		if(facet != null) {
			renderChild(facesContext, facet);
		}
		
		writer.endElement("span");
	}
}