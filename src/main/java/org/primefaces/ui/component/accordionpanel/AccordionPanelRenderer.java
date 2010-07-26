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
package org.primefaces.ui.component.accordionpanel;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.primefaces.ui.component.tabview.Tab;
import org.primefaces.ui.renderkit.CoreRenderer;

public class AccordionPanelRenderer extends CoreRenderer {

	public void encodeEnd(FacesContext facesContext, UIComponent component) throws IOException {
		AccordionPanel accordionPanel = (AccordionPanel) component;
		
		//restore tab states
		String clientId = accordionPanel.getClientId(facesContext);
		String activeTabIndex = facesContext.getExternalContext().getRequestParameterMap().get(clientId + "_state");
		if(activeTabIndex != null) {
			accordionPanel.setActiveIndex(activeTabIndex);
		}

		encodeScript(facesContext, accordionPanel);
		encodeMarkup(facesContext, accordionPanel);
	}

	private void encodeScript(FacesContext facesContext, AccordionPanel accordionPanel) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		String clientId = accordionPanel.getClientId(facesContext);
		String var = createUniqueWidgetVar(facesContext, accordionPanel);
		
		writer.startElement("script", null);
		writer.writeAttribute("type", "text/javascript", null);
		
		writer.write("PrimeFaces.core.Utils.onContentReady('" + clientId + "', function() {\n");
		writer.write(var + " = new PrimeFaces.widget.AccordionPanel('" + clientId + "',");
		
		//Tabs
		writer.write("[");
		for(int i=0; i < accordionPanel.getChildCount(); i++) {
			UIComponent child = accordionPanel.getChildren().get(i);
			
			if(child instanceof Tab) {
				String tabClientId = clientId + "_" + i;

				writer.write("new PrimeFaces.widget.AccordionTab('" + tabClientId + "'");
				if(accordionPanel.isActive(i))
					writer.write(",{expanded:true})");
				else
					writer.write(",{expanded:false})");
				
				if(i != accordionPanel.getChildCount() -1)
					writer.write(",");
			}
		}
		writer.write("],");
		
		//Config
		writer.write("{");
		writer.write("speed:" + accordionPanel.getSpeed());
		writer.write(",multipleSelection:" + accordionPanel.isMultipleSelection());
		writer.write("});\n");
		
		writer.write("});");
		
		writer.endElement("script");
	}

	private void encodeMarkup(FacesContext facesContext, AccordionPanel accordionPanel) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		String clientId = accordionPanel.getClientId(facesContext);
		
		writer.startElement("div", null);
		writer.writeAttribute("id", clientId, null);
		
		String styleClass = accordionPanel.getStyleClass() != null ? "pf-accordionpanel " + accordionPanel.getStyleClass() : "pf-accordionpanel";
		
		if(accordionPanel.getStyle() != null) {
			writer.writeAttribute("style", accordionPanel.getStyle(),"style");
		}
		if(accordionPanel.getStyleClass() != null) {
			writer.writeAttribute("class", styleClass,"styleClass");
		}
		
		encodeTabs(facesContext, accordionPanel);
		encodeStateHolder(facesContext, accordionPanel);
		
		writer.endElement("div");
	}
	
	private void encodeStateHolder(FacesContext facesContext, AccordionPanel accordionPanel) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		String clientId = accordionPanel.getClientId(facesContext);
		String stateHolderId = clientId + "_state"; 
		
		writer.startElement("input", null);
		writer.writeAttribute("type", "hidden", null);
		writer.writeAttribute("id", stateHolderId, null);
		writer.writeAttribute("name", stateHolderId, null);
		
		if(accordionPanel.getActiveIndex() != null) {
			writer.writeAttribute("value", accordionPanel.getActiveIndex(), null);
		}
		writer.endElement("input");
	}
	
	private void encodeTabs(FacesContext facesContext, AccordionPanel accordionPanel) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		String clientId = accordionPanel.getClientId(facesContext);
		String var = createUniqueWidgetVar(facesContext, accordionPanel);
		
		for(int i=0 ; i < accordionPanel.getChildCount(); i++) {
			UIComponent child = accordionPanel.getChildren().get(i);
			
			if(child instanceof Tab) {
				Tab tab = (Tab) child;
				
				if(tab.isRendered()) {
					String tabClientId = clientId + "_" + i;
					boolean active = accordionPanel.isActive(i);
					
					writer.startElement("div", null);
					writer.writeAttribute("id", tabClientId, null);
					writer.writeAttribute("class", "pf-accordiontab", null);
					
					//Header
					writer.startElement("div", null);
					writer.writeAttribute("class", "pf-accordiontab-hd", null);
					if(tab.getTitle() != null) {
						writer.write(tab.getTitle());
					}
					writer.endElement("div");
					
					//Body
					writer.startElement("div", null);
					writer.writeAttribute("class", "pf-accordiontab-bd", null);
					if(!active) {
						writer.writeAttribute("style", "display:none", null);
					}
					
					renderChildren(facesContext, tab);
					writer.endElement("div");
					
					//Toggler
					String togglerOnclick = var + ".toggle('" + tabClientId + "', this);";
					writer.startElement("span", null);
					if(active)
						writer.writeAttribute("class", "pf-accordiontab-toggler-expanded", null);
					else
						writer.writeAttribute("class", "pf-accordiontab-toggler-collapsed", null);
						
					writer.writeAttribute("onclick", togglerOnclick, null);
					writer.endElement("span");
					
					writer.endElement("div");
				}
			}
		}
	}

	public void encodeChildren(FacesContext facesContext, UIComponent component) throws IOException {
		//Do nothing
	}

	public boolean getRendersChildren() {
		return true;
	}
}