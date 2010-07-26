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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.primefaces.ui.component.tabview.Tab;
import org.primefaces.ui.renderkit.CoreRenderer;

public class AccordionPanelRenderer extends CoreRenderer {

	public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
		AccordionPanel accordionPanel = (AccordionPanel) component;

		encodeAccordionPanelMarkup(context, accordionPanel);
	}

	private void encodeAccordionPanelMarkup(FacesContext context, AccordionPanel accordionPanel) throws IOException {
		ResponseWriter writer = context.getResponseWriter();
		String clientId = accordionPanel.getClientId(context);
		
		writer.startElement("div", null);
		writer.writeAttribute("id", clientId, null);
		
		if(accordionPanel.getStyleClass() != null)
			writer.writeAttribute("class", accordionPanel.getStyleClass(),"styleClass");
		
		writer.startElement("div", null);
		
		StringBuffer cmsClass = new StringBuffer("yui-cms-accordion");
		
		if(accordionPanel.isMultipleSelection()) cmsClass.append(" multiple");
		if(accordionPanel.isFade()) cmsClass.append(" fade");
		if(accordionPanel.isRollover()) cmsClass.append(" rollover");
		if(accordionPanel.getSpeed() != null) cmsClass.append(" " + accordionPanel.getSpeed());
		
		cmsClass.append(" fixIE");
		
		writer.writeAttribute("class", cmsClass.toString(), null);
		
		if(accordionPanel.isBounceOut()) 
			writer.writeAttribute("rel", "bounceOut", null);
		
		List<Integer> activeIndexes = getIndexes(getActiveTabIndex(context, accordionPanel));
		encodeChildrenTabs(context, accordionPanel,activeIndexes);

		writer.endElement("div");
		
		writer.endElement("div");

		String activeTabIndexHiddenFieldId = getActiveTabIndexHiddenFieldId(context,accordionPanel);
		writer.write("<input type=\"hidden\" " + "name='"
				+ activeTabIndexHiddenFieldId + "' " + "id='"
				+ activeTabIndexHiddenFieldId + "' " + "value='"
				+ getActiveTabIndex(context, accordionPanel) + "'/>");
	}

	private List<Integer> getIndexes(String activeTabIndex) {
		if( activeTabIndex == null || activeTabIndex == "")
			return new ArrayList<Integer>();
		
		List<Integer> indexes = new ArrayList<Integer>();
		String[] values = activeTabIndex.split("-");
		for ( int i = 0; i < values.length ;  i++ ){ 
			if( values[i] != null && values[i] != ""){
				try{
				indexes.add( Integer.parseInt( values[i] ) );
				}catch (NumberFormatException e) {
					continue;
				}
			}
		}
		return indexes;
	}

	private String getActiveTabIndex(FacesContext context, AccordionPanel accordionPanel) {
		String activeTabIndexHiddenFieldId = getActiveTabIndexHiddenFieldId(context, accordionPanel);
		String activeTabIndex = (String) context.getExternalContext().getRequestParameterMap().get(activeTabIndexHiddenFieldId);
		
		return activeTabIndex == null ? "" : activeTabIndex;
	}

	private String getActiveTabIndexHiddenFieldId(FacesContext context, AccordionPanel accordionPanel) {
		return accordionPanel.getClientId(context) + ":activeIndex";
	}

	private void encodeChildrenTabs(FacesContext facesContext, AccordionPanel accordionPanel, List<Integer> activeIndexes) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();

		int childIndex = 0;
		String activeTabIndexHiddenFieldId = getActiveTabIndexHiddenFieldId(facesContext, accordionPanel);

		for(Iterator<UIComponent> iterator = accordionPanel.getChildren().iterator(); iterator.hasNext();){
			UIComponent childComponent =iterator.next();
			
			if( childComponent instanceof Tab ){
				Tab tab = (Tab) childComponent;
				if(tab.isRendered()) {
					writer.startElement("div", null);
					
					String styleClass = "yui-cms-item yui-panel";
					if( activeIndexes.contains(childIndex))
						styleClass += " selected";
					writer.writeAttribute("class", styleClass , null);
					
					encodeTabHeader(writer, tab);
					encodeTabBody(facesContext, tab);
					encodeTabActions(writer, accordionPanel, activeTabIndexHiddenFieldId, childIndex);
					
					writer.endElement( "div" );
				}
				childIndex++;
			}
		}
	}

	private void encodeTabActions(ResponseWriter writer, AccordionPanel accordionPanel, String hiddenInputId, int childIndex) throws IOException {
		writer.startElement("div", null);
		writer.writeAttribute("class", "actions", null);
		writer.startElement("a", null);
		writer.writeAttribute("href", "#", null);
		writer.writeAttribute("class", "accordionToggleItem", null);
		writer.writeAttribute("onclick", "PrimeFaces.widget.AccordionUtils.addAccordionPanelChanged('" + hiddenInputId + "','" + childIndex + "'," + accordionPanel.isMultipleSelection()+ ")", null);
		writer.endElement("a");
		writer.endElement("div");
	}

	private void encodeTabBody(FacesContext facesContext, Tab tab) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		
		writer.startElement("div", null);
		writer.writeAttribute("class", "bd", null);
		
		writer.startElement("div", null);
		writer.writeAttribute("class", "fixed", null);
		
		renderChildren(facesContext, tab);
		
		writer.endElement("div");
		
		writer.endElement("div");
	}

	private void encodeTabHeader(ResponseWriter writer, Tab tab) throws IOException {
		writer.startElement("div", null);
		writer.writeAttribute("class", "hd", null);
		if(tab.getTitle() != null)
			writer.write(tab.getTitle());

		writer.endElement("div");
	}
	
	public void encodeChildren(FacesContext facesContext, UIComponent component) throws IOException {
		//Do nothing
	}

	public boolean getRendersChildren() {
		return true;
	}
}
