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
package org.primefaces.component.menubar;

import java.io.IOException;
import java.util.Iterator;

import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.primefaces.component.menuitem.MenuItem;
import org.primefaces.component.submenu.Submenu;
import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.ComponentUtils;

public class MenubarRenderer extends CoreRenderer {

	public void encodeEnd(FacesContext facesContext, UIComponent component) throws IOException{
		Menubar menubar = (Menubar) component;
		
		if(menubar.isDynamic() && !isPostBack()) {
			menubar.buildMenuFromModel();
		}
		
		encodeMarkup(facesContext, menubar);
		encodeScript(facesContext, menubar);
	}

	protected void encodeScript(FacesContext facesContext, Menubar menubar) throws IOException{
		ResponseWriter writer = facesContext.getResponseWriter();
		String clientId = menubar.getClientId(facesContext);
		String menubarVar = createUniqueWidgetVar(facesContext, menubar);
		
		writer.startElement("script", null);
		writer.writeAttribute("type", "text/javascript", null);

		writer.write(menubarVar + " = new YAHOO.widget.MenuBar('" + clientId + "',{");
		
		writer.write("autosubmenudisplay:" + menubar.isAutoSubmenuDisplay());
		
		if(!menubar.getEffect().equals("NONE")) {
			writer.write(",effect: {effect: YAHOO.widget.ContainerEffect." + menubar.getEffect());
			
			if(menubar.getEffectDuration() != 0.25)
				writer.write(",duration:" + menubar.getEffectDuration() + "}");
			else
				writer.write(",duration: 0.25}");
		}
		
		if(menubar.getZindex() != Integer.MAX_VALUE) writer.write(",zIndex:" + menubar.getZindex());
		
		writer.write("})\n;");
	
		writer.write(menubarVar + ".render();\n");

		writer.endElement("script");	
	}

	protected void encodeMarkup(FacesContext facesContext, Menubar menubar) throws IOException{
		ResponseWriter writer = facesContext.getResponseWriter();
		String clientId = menubar.getClientId(facesContext);
		
		writer.startElement("div", null);
		writer.writeAttribute("id", clientId, null);
		writer.writeAttribute("class", "yuimenubar", null);
		
		writer.startElement("div", null);
		writer.writeAttribute("class", "bd", null);
		
		writer.startElement("ul", null);
		writer.writeAttribute("class", "first-of-type", null);
		
		boolean firstSubmenu = true;
		for(UIComponent child : menubar.getChildren()) {
			Submenu submenu = (Submenu) child;
			
			if(submenu.isRendered()) {
				String styleClass = firstSubmenu ? "yuimenubaritem first-of-type" : "yuimenubaritem";
				writer.startElement("li", null);
				writer.writeAttribute("class", styleClass , null);
				
				encodeSubmenu(facesContext, submenu);
				
				writer.endElement("li");
				
				firstSubmenu = false;
			}
		}
		
		writer.endElement("ul");
		writer.endElement("div");
		writer.endElement("div");
	}
	
	protected void encodeSubmenu(FacesContext facesContext, Submenu submenu) throws IOException{
		ResponseWriter writer = facesContext.getResponseWriter();
		UIComponent labelFacet = submenu.getFacet("label");
		String clientId = submenu.getClientId(facesContext);

		String labelStyleClass = submenu.getParent() instanceof Menubar ? "yuimenubaritemlabel" : "yuimenuitemlabel";
		
		if(labelFacet == null) {
			String href = submenu.getChildCount() > 0 ? "#" + clientId : "#";
			
			writer.startElement("a", null);
			if(submenu.getLabelStyle() != null) writer.writeAttribute("style", labelStyleClass, null);
			if(submenu.getLabelStyleClass() != null) {
				labelStyleClass = labelStyleClass + " " + submenu.getLabelStyleClass();
			}
			writer.writeAttribute("class", labelStyleClass, null);
			writer.writeAttribute("href", href, null);
			
			if(submenu.getLabel() != null) writer.write(submenu.getLabel());
			
			writer.endElement("a");
		} else {
			encodeMenuItem(facesContext, (MenuItem) labelFacet, labelStyleClass);
		}
		
		if(submenu.getChildCount() > 0) {
			writer.startElement("div", null);
			writer.writeAttribute("id", clientId, null);
			writer.writeAttribute("class", "yuimenu", null);
			
			writer.startElement("div", null);
			writer.writeAttribute("class", "bd", null);
			writer.startElement("ul", null);
			
			encodeSubmenuItems(facesContext, submenu);
			
			writer.endElement("ul");
			writer.endElement("div");
			writer.endElement("div");
		}
	}
	
	protected void encodeMenuItem(FacesContext facesContext, MenuItem menuItem, String labelStyleClass) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		
		if(menuItem.getChildCount() > 0) {
			renderChildren(facesContext, menuItem);
		} else {
			String clientId = menuItem.getClientId(facesContext);
			String icon = menuItem.getIcon() != null ? "background:url(" + getResourceURL(facesContext, menuItem.getIcon()) + ") no-repeat 1%;" : null;
			if(menuItem.getStyleClass() != null) {
				labelStyleClass = labelStyleClass + " " + menuItem.getStyleClass();
			}
			
			writer.startElement("a", null);
			writer.writeAttribute("id", clientId, null);
			writer.writeAttribute("class", labelStyleClass, null);
			
			if(menuItem.getStyle() != null && icon != null)
				writer.writeAttribute("style", icon + menuItem.getStyle(), null);
			else if(menuItem.getStyle() == null && icon != null)
				writer.writeAttribute("style", icon, null);
			else if(menuItem.getStyle() != null && icon == null)
				writer.writeAttribute("style", menuItem.getStyle() , null);
			
			if(menuItem.getUrl() != null) {
				writer.writeAttribute("href", getResourceURL(facesContext, menuItem.getUrl()), null);
				if(menuItem.getOnclick() != null) writer.writeAttribute("onclick", menuItem.getOnclick(), null);
				if(menuItem.getTarget() != null) writer.writeAttribute("target", menuItem.getTarget(), null);
			} else {
				writer.writeAttribute("href", "javascript:void(0)", null);
				
				UIComponent form = ComponentUtils.findParentForm(facesContext, menuItem);
				if(form == null) {
					throw new FacesException("Menubar must be inside a form element");
				}
				
				String formClientId = form.getClientId(facesContext);
				String command = menuItem.isAjax() ? buildAjaxRequest(facesContext, menuItem, formClientId, clientId) : buildNonAjaxRequest(facesContext, menuItem, formClientId, clientId);
				
				command = menuItem.getOnclick() == null ? command : menuItem.getOnclick() + ";" + command;
				
				writer.writeAttribute("onclick", command, null);
			}
			
			//Label is deprecated
			if(menuItem.getLabel() != null) writer.write(menuItem.getLabel());
			if(menuItem.getValue() != null) writer.write((String) menuItem.getValue());
			
			if(menuItem.getHelpText() != null) {
				writer.startElement("em", null);
				writer.writeAttribute("class", "helptext", null);
				writer.write(menuItem.getHelpText());
				writer.endElement("em");
			}
			
			writer.endElement("a");
		}
	}
	
	protected void encodeSubmenuItems(FacesContext facesContext, Submenu submenu) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		
		for (Iterator<UIComponent> iterator = submenu.getChildren().iterator(); iterator.hasNext();) {
			UIComponent child = (UIComponent) iterator.next();
			
			if(child.isRendered()) {
				writer.startElement("li", null);
				writer.writeAttribute("class", "yuimenuitem", null);
				
				if(child instanceof MenuItem) {
					MenuItem menuItem = (MenuItem) child;
					encodeMenuItem(facesContext, menuItem, "yuimenuitemlabel");
				} else if(child instanceof Submenu) {
					Submenu childSubmenu = (Submenu) child;
					encodeSubmenu(facesContext, childSubmenu);
				}
				
				writer.endElement("li");
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