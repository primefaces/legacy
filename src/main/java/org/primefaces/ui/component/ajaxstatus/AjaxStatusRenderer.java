/*
 * Copyright 2009 Prime Technology.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
		String var = createUniqueWidgetVar(facesContext, status);
		UIComponent start = status.getFacet("start");
		UIComponent complete = status.getFacet("complete");
		
		writer.startElement("script", null);
		writer.writeAttribute("type", "text/javascript", null);
		
		writer.write("YAHOO.util.Event.onContentReady(\"" + clientId + "\", function() {\n");
		
		writer.write(var + " = new PrimeFaces.widget.AjaxStatus();\n");
		if(status.getOnstart() != null)
			writer.write(var + ".bindEvent('ajaxStart',function(){" + status.getOnstart() + "});\n");
		else if(start != null)
			writer.write(var + ".bindFacet('ajaxStart', '" + clientId + "_start', '" + clientId + "_complete');\n");
		
		if(status.getOncomplete() != null)
			writer.write(var + ".bindEvent('ajaxComplete',function(){" + status.getOncomplete() + "});\n");
		if(complete != null)
			writer.write(var + ".bindFacet('ajaxComplete', '" + clientId + "_complete', '" + clientId + "_start');\n");
		
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
		writer.writeAttribute("id", clientId + "_" + facetName, null);
		writer.writeAttribute("style", "display:none", null);
		
		UIComponent facet = status.getFacet(facetName);
		if(facet != null) {
			renderChild(facesContext, facet);
		}
		
		writer.endElement("span");
	}
}