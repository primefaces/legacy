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
package org.primefaces.ui.component.uiajax;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.event.ActionEvent;

import org.primefaces.ui.renderkit.CoreRenderer;
import org.primefaces.ui.util.ComponentUtils;

public class UIAjaxRenderer extends CoreRenderer {

	public void decode(FacesContext facesContext, UIComponent component) {
		UIAjax ajax = (UIAjax) component;
		
		String clientId = ajax.getClientId(facesContext);
		
		if(facesContext.getExternalContext().getRequestParameterMap().containsKey(clientId)) {
			ajax.queueEvent(new ActionEvent(ajax));
		}
	}
	
	public void encodeEnd(FacesContext facesContext, UIComponent component) throws IOException {
		UIAjax ajax = (UIAjax) component;

		encodeUIAjaxScript(facesContext, ajax);
	}

	protected void encodeUIAjaxScript(FacesContext facesContext, UIAjax ajax) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		UIComponent parent = ajax.getParent();
		String parentClientId = parent.getClientId(facesContext);
		String clientId = ajax.getClientId(facesContext);
		
		writer.startElement("script", null);
		writer.writeAttribute("type", "text/javascript", null);
		
		writer.write("YAHOO.util.Event.onContentReady(\"" + parentClientId + "\", function() {\n");
		
		writer.write("var ajaxRequestEvent = new PrimeFaces.ajax.AjaxRequestEvent(");
		writer.write("\"" + getActionURL(facesContext) + "\",");
		String parentFormClientId = ComponentUtils.findParentForm(facesContext, ajax).getClientId(facesContext);
		writer.write("{formClientId: \"" + parentFormClientId + "\", partialSubmit: " + ajax.isPartialSubmit() + ", ajaxifiedComponent:\"" + parentClientId + "\"");
		if(ajax.getOncomplete() != null) {
			writer.write(", onComplete: function() {" + ajax.getOncomplete() + ";}");
		} 
	
		writer.write("},");
		writer.write("\"update=" + ajax.getUpdate() + "&" + clientId + "=" + clientId + "&primefacesAjaxRequest=true\");\n");
		writer.write("YAHOO.util.Event.addListener(\"" + parentClientId + "\", \"" + ajax.getEvent() + "\", PrimeFaces.ajax.AjaxRequestEventHandler, ajaxRequestEvent);\n");
		
		writer.write("});\n");
		writer.endElement("script");
	}
}