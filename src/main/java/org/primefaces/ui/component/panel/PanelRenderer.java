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
package org.primefaces.ui.component.panel;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.primefaces.ui.renderkit.CoreRenderer;

public class PanelRenderer extends CoreRenderer {

	public void encodeEnd(FacesContext facesContext, UIComponent component) throws IOException {
		Panel panel = (Panel) component;
		
		encodePanel(facesContext, panel);
	}
	
	private void encodePanel(FacesContext facesContext, Panel panel) throws IOException{
		ResponseWriter writer = facesContext.getResponseWriter();
		
		writer.startElement("div", null);
		writer.writeAttribute("id", panel.getClientId(facesContext), null);
		writer.writeAttribute("class", Panel.PANEL_CLASS, null);
		
		encodeHeader(facesContext, panel);
		encodeContent(facesContext, panel);
		encodeFooter(facesContext, panel);
		if(panel.isToggleable()) {
			encodeToggler(facesContext, panel);
		}
		
		writer.endElement("div");
	}

	private void encodeHeader(FacesContext facesContext, Panel panel) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		
		writer.startElement("div", null);
		writer.writeAttribute("class", Panel.PANEL_HEADER_CLASS, null);
		
		if(panel.getHeader() != null)
			writer.write(panel.getHeader());
		
		writer.endElement("div");
	}
	
	private void encodeContent(FacesContext facesContext, Panel panel) throws IOException{
		ResponseWriter writer = facesContext.getResponseWriter();
		
		writer.startElement("div", null);
		writer.writeAttribute("class", Panel.PANEL_BODY_CLASS, null);
		
		renderChildren(facesContext, panel);
		
		writer.endElement("div");
	}
	
	private void encodeFooter(FacesContext facesContext, Panel panel) throws IOException{
		ResponseWriter writer = facesContext.getResponseWriter();
		
		if(panel.getFooter() == null)
			return;
		
		writer.startElement("div", null);
		writer.writeAttribute("class", Panel.PANEL_FOOTER_CLASS, null);
		
		writer.write(panel.getFooter());
		
		writer.endElement("div");
	}
	
	private void encodeToggler(FacesContext facesContext, Panel panel) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		String clientId = panel.getClientId(facesContext);
		
		String onclick = "jQuery(PrimeFaces.core.Utils.escapeClientId('" + clientId + " ." + 
						Panel.PANEL_BODY_CLASS + "')).slideToggle(" + panel.getToggleSpeed() + ");jQuery(this).toggleClass('" + Panel.PANEL_TOGGLER_COLLAPSED_CLASS + "')";
	
		writer.startElement("span", null);
		writer.writeAttribute("class", Panel.PANEL_TOGGLER_EXPANDED_CLASS, null);
		writer.writeAttribute("onclick", onclick, null);
		writer.endElement("span");
	}

	public void encodeChildren(FacesContext facesContext, UIComponent component) throws IOException {
		//Do nothing
	}
	
	public boolean getRendersChildren() {
		return true;
	}
}
