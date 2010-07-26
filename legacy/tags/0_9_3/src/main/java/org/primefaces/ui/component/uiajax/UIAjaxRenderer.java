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

import javax.faces.FacesException;
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
		
		if(facesContext.getExternalContext().getRequestParameterMap().containsKey(clientId))
			ajax.queueEvent(new ActionEvent(ajax));
	}
	
	public void encodeEnd(FacesContext facesContext, UIComponent component) throws IOException {
		UIAjax ajax = (UIAjax) component;
		ResponseWriter writer = facesContext.getResponseWriter();
		UIComponent parent = ajax.getParent();
		String parentClientId = parent.getClientId(facesContext);
		String formClientId = null;
		String clientId = ajax.getClientId(facesContext);
		UIComponent form = ComponentUtils.findParentForm(facesContext, ajax);
		
		if(form != null)
			formClientId = ComponentUtils.findParentForm(facesContext, ajax).getClientId(facesContext);
		else
			throw new FacesException("UIAjax:" + clientId + " needs to be enclosed in a form");
		
		writer.startElement("script", null);
		writer.writeAttribute("type", "text/javascript", null);
		
		writer.write("PrimeFaces.core.Utils.onContentReady('" + parentClientId + "', function() {\n");
		writer.write("var ajaxRequestEvent = new PrimeFaces.ajax.AjaxRequestEvent(");
		writer.write("'" + getActionURL(facesContext) + "',");
		
		writer.write("{formClientId:'" + formClientId + "', partialSubmit:" + ajax.isPartialSubmit() + ", ajaxifiedComponent:'" + parentClientId + "'");
		if(ajax.getOnstart() != null) {
			writer.write(",onstart: function() {" + ajax.getOnstart() + ";}");
		}
		if(ajax.getOncomplete() != null) {
			writer.write(",oncomplete: function() {" + ajax.getOncomplete() + ";}");
		}
		writer.write("},");
		
		writer.write("'update=");
		if(ajax.getUpdate() != null)
			writer.write(ajax.getUpdate());
		else
			writer.write(formClientId);
		
		writer.write("&" + clientId + "=" + clientId + "');\n");
		writer.write("YAHOO.util.Event.addListener('" + parentClientId + "', '" + ajax.getEvent() + "', PrimeFaces.ajax.AjaxRequestEventHandler, ajaxRequestEvent);\n");
		writer.write("});\n");
		writer.endElement("script");
	}
}