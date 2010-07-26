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
package org.primefaces.ui.component.commandlink;

import java.io.IOException;

import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.event.ActionEvent;

import org.primefaces.ui.renderkit.CoreRenderer;
import org.primefaces.ui.util.ComponentUtils;
import org.primefaces.ui.util.HTML;

public class CommandLinkRenderer extends CoreRenderer {

	public void decode(FacesContext facesContext, UIComponent component) {		
		String clientId = component.getClientId(facesContext);

		if(facesContext.getExternalContext().getRequestParameterMap().containsKey(clientId))
			component.queueEvent(new ActionEvent(component));
	}

	@Override
	public void encodeEnd(FacesContext facesContext, UIComponent component) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		CommandLink commandLink = (CommandLink) component;
		String clientId = commandLink.getClientId(facesContext);

		UIComponent form = ComponentUtils.findParentForm(facesContext, commandLink);
		if(form == null) {
			throw new FacesException("Commandlink \"" + clientId + "\" must be inside a form element");
		}
		
		String formClientId = form.getClientId(facesContext);
		String ajaxRequest = getAjaxRequest(facesContext, commandLink, formClientId);

		writer.startElement("a", commandLink);
		writer.writeAttribute("id", clientId, "id");
		writer.writeAttribute("href", "#", null);
		
		String onclick = commandLink.getOnclick() != null ? commandLink.getOnclick() + ";" + ajaxRequest : ajaxRequest;
		writer.writeAttribute("onclick", onclick, "onclick");
		
		renderPassThruAttributes(facesContext, commandLink, HTML.LINK_ATTRS, HTML.CLICK_EVENT);

		if(commandLink.getStyleClass() != null)
			writer.writeAttribute("class", commandLink.getStyleClass(), null);
		
		renderChildren(facesContext, commandLink);
		
		writer.endElement("a");
	}

	//TODO: A common AjaxRequest builder sounds better
	private String getAjaxRequest(FacesContext facesContext, CommandLink link, String formClientId) {
		String clientId = link.getClientId(facesContext);
		
		StringBuilder req = new StringBuilder();
		req.append("PrimeFaces.ajax.AjaxRequest('");
		req.append(getActionURL(facesContext));
		req.append("',{");
		req.append("formClientId:'");
		req.append(formClientId);
		req.append("'");
		
		if(link.getOnstart() != null) {
			req.append(",onstart:function(){" + link.getOnstart() + ";}");
		}
		if(link.getOncomplete() != null) {
			req.append(",oncomplete:function(){" + link.getOncomplete() + ";}");
		}
		
		req.append("},");
		
		req.append("'update=");
		if(link.getUpdate() != null) {
			req.append(link.getUpdate());
		}
		else {
 			req.append(formClientId);
		}
		
		req.append("&");
		req.append(clientId);
		req.append("=");
		req.append(clientId);
		req.append("');");
		
		return req.toString();
	}

	@Override
	public boolean getRendersChildren() {
		return true;
	}

	@Override
	public void encodeChildren(FacesContext facesContext, UIComponent component) throws IOException {
		// Render children at encodeEnd
	}
}