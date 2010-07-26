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
package org.primefaces.ui.component.tabview;

import java.io.IOException;
import java.util.Iterator;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.primefaces.ui.renderkit.CoreRenderer;

public class TabViewRenderer extends CoreRenderer{

	public void encodeEnd(FacesContext facesContext, UIComponent component) throws IOException {
		TabView tabView = (TabView) component;
		
		encodeTabViewWidget(facesContext, tabView);
		encodeTabViewMarkup(facesContext, tabView);
	}

	private void encodeTabViewMarkup(FacesContext facesContext, TabView tabView) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		String clientId = tabView.getClientId(facesContext);
		
		int activeTabIndex = getActiveTabIndex( facesContext, tabView, clientId );
		String activeTabIndexHiddenFieldId = getActiveTabIndexHiddenFieldId(clientId);

		
		writer.startElement("div", tabView);
		writer.writeAttribute("id", clientId , null);
		writer.writeAttribute("class", "yui-navset", null);
		
		writer.startElement("ul", null);
		writer.writeAttribute("class", "yui-nav", null);
		
		for (int i = 0; i < tabView.getChildren().size(); i++) {
			Tab tab = (Tab) tabView.getChildren().get(i);
			
			if(tab.isRendered()) {
				writer.startElement("li", null);
				
				if( i == activeTabIndex )
					writer.writeAttribute("class", "selected", null);
				
				writer.startElement("a", null);
				writer.writeAttribute("href", "#" + tab.getClientId(facesContext), null);
				writer.startElement("em", null);
				writer.write(tab.getTitle());
				writer.endElement("em");
				writer.endElement("a");
				
				writer.endElement("li");
			}
		}
		
		writer.endElement("ul");
		
		writer.startElement("div", null);
		writer.writeAttribute("class", "yui-content", null);
		
		for (Iterator<UIComponent> iterator = tabView.getChildren().iterator(); iterator.hasNext();) {
			Tab tab = (Tab) iterator.next();
			
			if(tab.isRendered()) {
				writer.startElement("div", null);
				renderChildren(facesContext, tab);
				writer.endElement("div");
			}
		}
		
		writer.endElement("div");
		
		writer.endElement("div");
		
		writer.write("<input type=\"hidden\" " +
				"name='" + activeTabIndexHiddenFieldId + "' " +
				"id='" + activeTabIndexHiddenFieldId + "' " +
				"value='"+ activeTabIndex + "'/>");
	}

	private int getActiveTabIndex(FacesContext facesContext, TabView tabView, String clientId) {
		String activeTabIndexHiddenFieldId = getActiveTabIndexHiddenFieldId(clientId);
		String activeTabIndex = (String) facesContext.getExternalContext().getRequestParameterMap().get(activeTabIndexHiddenFieldId);
		
		if( activeTabIndex != null && activeTabIndex != "")
			tabView.setActiveIndex(Integer.parseInt(activeTabIndex));
	
		return tabView.getActiveIndex();
	}

	private void encodeTabViewWidget(FacesContext facesContext, TabView tabView) throws IOException{
		ResponseWriter writer = facesContext.getResponseWriter();
		String clientId = tabView.getClientId(facesContext);
		String tabViewVar = getTabViewVar(tabView);
		
		writer.startElement("script", null);
		writer.writeAttribute("type", "text/javascript", null);
		
		writer.write("YAHOO.util.Event.onContentReady(\"" + clientId + "\", function() {\n");
	    writer.write(tabViewVar + " = new YAHOO.widget.TabView(\"" + clientId + "\",{orientation:\"" + tabView.getOrientation() + "\"});");
	    
	    if(tabView.isContentTransition())
	    	writer.write(tabViewVar + ".contentTransition = PrimeFaces.widget.TabViewUtils.contentTransition;\n");

	    writer.write("PrimeFaces.widget.TabViewUtils.addTabClickListener(" + tabViewVar + ",'" + getActiveTabIndexHiddenFieldId(clientId) +"');");
		writer.write("});\n");
	    
	    writer.endElement("script");
	}
	
	private String getTabViewVar(TabView tabView) {
		if(tabView.getWidgetVar() != null)
			return tabView.getWidgetVar();
		else
			return "pf_tabView_" + tabView.getId();
	}

	private String getActiveTabIndexHiddenFieldId(String clientId) {
		return clientId + ":activeIndex";
	}
	
	public void encodeChildren(FacesContext facesContext, UIComponent component) throws IOException {
		//Do nothing
	}

	public boolean getRendersChildren() {
		return true;
	}
}
