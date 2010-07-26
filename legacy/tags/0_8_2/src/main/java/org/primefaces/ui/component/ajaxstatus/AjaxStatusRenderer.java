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
		if(start != null) {
			writer.write("onCreate: function(){\n");
			if(complete != null) {
				writer.write("$('" + clientId + ":complete').hide();\n");
			}
			writer.write("$('" + clientId + ":start').show();\n");
			writer.write("},\n");
		}
		
		if(complete != null) {
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
		
		writer.startElement("span", null);
		writer.writeAttribute("id", clientId + ":start", null);
		writer.writeAttribute("style", "display:none", null);
		
		UIComponent start = status.getFacet("start");
		if(start != null) {
			renderChild(facesContext, start);
		}
		
		writer.endElement("span");
		
		writer.startElement("span", null);
		writer.writeAttribute("id", clientId + ":complete", null);
		writer.writeAttribute("style", "display:none", null);
		
		UIComponent complete = status.getFacet("complete");
		if(complete != null) {
			renderChild(facesContext, complete);
		}
		
		writer.endElement("span");
		
		writer.endElement("span");
	}	
}