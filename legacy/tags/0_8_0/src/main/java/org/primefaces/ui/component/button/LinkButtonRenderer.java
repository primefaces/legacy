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
package org.primefaces.ui.component.button;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.primefaces.ui.renderkit.CoreRenderer;
import org.primefaces.ui.util.ComponentUtils;
import org.primefaces.ui.util.HTML;

public class LinkButtonRenderer extends CoreRenderer {

	public void encodeEnd(FacesContext facesContext, UIComponent component) throws IOException {
		LinkButton button = (LinkButton) component;

		encodeButtonScript(facesContext, button);
		encodeButtonMarkup(facesContext, button);
	}

	protected void encodeButtonMarkup(FacesContext facesContext, LinkButton button) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		String clientId = button.getClientId(facesContext);

		
		writer.startElement("a", button);
		writer.writeAttribute("id", clientId , null);
		writer.writeAttribute("href", button.getHref(), null);
		
		if(button.getTarget() != null) writer.writeAttribute("target", button.getTarget(), null);
		
		String value = ComponentUtils.getStringValueToRender(facesContext, button);
		if(value != null)
			writer.write(value);
		
		writer.endElement("a");
		
	}

	protected void encodeButtonScript(FacesContext facesContext, LinkButton button) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		String clientId = button.getClientId(facesContext);
		String buttonVar = getButtonVar(button);
		
		writer.startElement("script", button);
		writer.writeAttribute("type", "text/javascript", null);

		writer.write("YAHOO.util.Event.onContentReady(\"" + clientId + "\", function() {\n");		
		writer.write(buttonVar + " = new YAHOO.widget.Button(\"" + clientId  + "\");");
		renderDomEvents(facesContext, button, buttonVar);
		writer.write("});");

		writer.endElement("script");
	}
	
	private void renderDomEvents(FacesContext facesContext, LinkButton button, String buttonVar) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		
		for(String event : HTML.BUTTON_EVENTS) {			
			String eventHandler = (String) button.getAttributes().get(event);
			
			if(eventHandler != null)
				writer.write(buttonVar + ".addListener(\"" + event.substring(2, event.length()) + "\", function(e){" + eventHandler + ";});\n");
		}
	}

	protected String getButtonVar(LinkButton button){
		if(button.getWidgetVar() != null)
			return button.getWidgetVar();
		else
			return "pf_linkButton" + button.getId();
	}
}