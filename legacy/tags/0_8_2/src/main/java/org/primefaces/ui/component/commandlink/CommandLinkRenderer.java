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

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.event.ActionEvent;

import org.primefaces.ui.renderkit.CoreRenderer;
import org.primefaces.ui.util.ComponentUtils;

public class CommandLinkRenderer extends CoreRenderer {

	public void decode(FacesContext facesContext, UIComponent component) {
		CommandLink commandLink = (CommandLink) component;
		
		String clientId = commandLink.getClientId(facesContext) + ":commandLink";

		if ( facesContext.getExternalContext().getRequestParameterMap().containsKey(clientId))
			commandLink.queueEvent(new ActionEvent(commandLink));
	}


	@Override
	public void encodeEnd(FacesContext context, UIComponent component)
			throws IOException {
		ResponseWriter writer = context.getResponseWriter();
		writer.endElement("a");
	}

	public void encodeBegin(FacesContext facesContext, UIComponent component) throws IOException {
		CommandLink commandLink = (CommandLink) component;

		UIComponent uiform = ComponentUtils.findParentForm(facesContext, commandLink);
		if (uiform == null)
			throw new RuntimeException("Commandlink must be inside a <h:form> element");
		String formClientId = uiform.getClientId(facesContext);

		encodeCommandLinkMarkup(facesContext, commandLink,formClientId);
		encodeCommandLinkScript(facesContext, commandLink,formClientId);
	}

	private void encodeCommandLinkScript(FacesContext context,
			CommandLink commandLink, String formClientId) throws IOException {
		ResponseWriter writer = context.getResponseWriter();

		writer.startElement("script", commandLink);
		writer.writeAttribute("type", "text/javascript", null);
		writer.write("function addCommandLink(f, pvp) {\n");
		writer.write("addParams(f, pvp);\n");
		writer.write("var ft = f.target;\n");
		writer.write("f.submit();\n");
		writer.write("f.target = ft;\n");

		writer.write("};\n");
		writer.write("function addParams(f, pvp) {\n");
		writer.write("var adp = new Array();\n");
		writer.write("f.adp = adp;\n");
		writer.write("var ps = pvp.split(',');\n");
		writer.write("for (var i = 0,ii = 0;i < ps.length;i++,ii++) {\n");
		writer.write("	        var p = document.createElement(\"input\");\n");
		writer.write("   p.type = \"hidden\";\n");
		writer.write("  p.name = ps[i];\n");
		writer.write("  p.value = ps[i + 1];\n");
		writer.write("  f.appendChild(p);\n");
		writer.write("  adp[ii] = p;\n");
		writer.write("  i += 1;\n");
		writer.write("}\n");
		writer.write("};\n");

		writer.endElement("script");

	}

	protected void encodeCommandLinkMarkup(FacesContext facesContext,
			CommandLink commandLink, String formClientId) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		String clientId = commandLink.getClientId(facesContext)
				+ ":commandLink";

		writer.startElement("a", null);
		writer.writeAttribute("id", clientId, null);
		writer.writeAttribute("name", clientId, null);

		String onclick = "";
		if (commandLink.getAttributes().get("onclick") != null)
			onclick = commandLink.getAttributes().get("onclick") + ";";
		onclick += "addCommandLink( document.getElementById(\'" + formClientId	+ "\'), \'" + clientId + "\');return false;";
		writer.writeAttribute("onclick", onclick, null);
		writer.writeAttribute("href", "#", null);

		if (commandLink.getStyle() != null)
			writer.writeAttribute("style", commandLink.getStyle(), null);
		if (commandLink.getStyleClass() != null)
			writer.writeAttribute("class", commandLink.getStyleClass(), null);

	}

	@Override
	public boolean getRendersChildren() {
		return true;
	}
}