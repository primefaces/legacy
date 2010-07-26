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
		
		if(panel.getStyleClass() != null)
			writer.writeAttribute("class", panel.getStyleClass(), null);
		else
			writer.writeAttribute("class", "p-panel", null);
		
		encodeHeader(facesContext, panel);
		encodeContent(facesContext, panel);
		encodeFooter(facesContext, panel);
		
		writer.endElement("div");
	}

	private void encodeHeader(FacesContext facesContext, Panel panel) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		
		writer.startElement("div", null);
		
		if(panel.getHeaderClass() != null)
			writer.writeAttribute("class", panel.getHeaderClass(), null);
		else
			writer.writeAttribute("class", "p-panel-header", null);
		
		if(panel.getHeader() != null)
			writer.write(panel.getHeader());
		
		writer.endElement("div");
	}
	
	private void encodeContent(FacesContext facesContext, Panel panel) throws IOException{
		ResponseWriter writer = facesContext.getResponseWriter();
		
		writer.startElement("div", null);
		
		if(panel.getContentClass() != null)
			writer.writeAttribute("class", panel.getContentClass(), null);
		else
			writer.writeAttribute("class", "p-panel-content", null);
		
		renderChildren(facesContext, panel);
		
		writer.endElement("div");
	}
	
	private void encodeFooter(FacesContext facesContext, Panel panel) throws IOException{
		ResponseWriter writer = facesContext.getResponseWriter();
		
		if(panel.getFooter() == null)
			return;
		
		writer.startElement("div", null);
		
		if(panel.getContentClass() != null)
			writer.writeAttribute("class", panel.getContentClass(), null);
		else
			writer.writeAttribute("class", "p-panel-footer", null);
		
		writer.write(panel.getFooter());
		
		writer.endElement("div");
	}

	public void encodeChildren(FacesContext facesContext, UIComponent component) throws IOException {
		//Do nothing
	}
	
	public boolean getRendersChildren() {
		return true;
	}
}
