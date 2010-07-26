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
package org.primefaces.component.menu;

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

public class MenuRenderer extends CoreRenderer{

	public void encodeEnd(FacesContext facesContext, UIComponent component) throws IOException {
		Menu menu = (Menu) component;
		
		if(menu.isDynamic() && !isPostBack()) {
			menu.buildMenuFromModel();
		}
		
		encodeMarkup(facesContext, menu);
		encodeScript(facesContext, menu);
	}

	protected void encodeScript(FacesContext facesContext, Menu menu) throws IOException{
		ResponseWriter writer = facesContext.getResponseWriter();
		String clientId = menu.getClientId(facesContext);
		String menuVar = createUniqueWidgetVar(facesContext, menu);
		
		writer.startElement("script", null);
		writer.writeAttribute("type", "text/javascript", null);

		writer.write(menuVar + " = new YAHOO.widget.Menu('" + clientId + "', {");
		
		if(menu.getPosition().equalsIgnoreCase("static")) {
			writer.write("position:'static'");
		}
		else if(menu.getPosition().equalsIgnoreCase("dynamic")) {
			writer.write("position:'dynamic'");
			if(menu.isVisible()) writer.write(",visible:true");
			if(!menu.isClickToHide()) writer.write(",clicktohide:false");
			if(menu.isKeepOpen()) writer.write(",keepopen:true");
			if(menu.getX() != -1) writer.write(",x:" + menu.getX());
			if(menu.getY() != -1) writer.write(",y:" + menu.getY());
			if(menu.isFixedCenter()) writer.write(",fixedcenter:true");
			if(!menu.isConstraintToViewport()) writer.write(",constrainttoviewport:false");
			if(menu.getShowDelay() != 250) writer.write(",showdelay:" + menu.getShowDelay());
			if(menu.getHideDelay() != 0) writer.write(",hidedelay:" + menu.getHideDelay());
			if(menu.getSubmenuHideDelay() != 250) writer.write(",submenuhidedelay:" + menu.getSubmenuHideDelay());
			if(menu.getContext() != null) writer.write(",context:[" + menu.getContext() + "]");
			if(menu.getZindex() != Integer.MAX_VALUE) writer.write(",zIndex:" + menu.getZindex());
		}
		
		if(menu.isAutoSubmenuDisplay() == false) writer.write(",autosubmenudisplay:false");
		
		if(!menu.getEffect().equals("NONE")) {
			writer.write(",effect: {effect: YAHOO.widget.ContainerEffect." + menu.getEffect());
			
			if(menu.getEffectDuration() != 0.25)
				writer.write(",duration:" + menu.getEffectDuration() + "}");
			else
				writer.write(",duration: 0.25}");
		}
		
		writer.write("});\n");
		
		writer.write(menuVar + ".render();\n");
		
		writer.endElement("script");	
	}

	protected void encodeMarkup(FacesContext facesContext, Menu menu) throws IOException{
		ResponseWriter writer = facesContext.getResponseWriter();
		String clientId = menu.getClientId(facesContext);
		String styleClass = menu.getStyleClass() == null ? "yuimenu" : "yuimenu " + menu.getStyleClass(); 
		
		writer.startElement("div", null);
		writer.writeAttribute("id", clientId, null);
		writer.writeAttribute("class", styleClass, null);
		if(menu.getStyle() != null) writer.writeAttribute("style", menu.getStyle(), null);
		
		writer.startElement("div", null);
		writer.writeAttribute("class", "bd", null);
		
		if(menu.isTiered())
			renderTieredMenu(facesContext, menu);
		else
			encodeRegularMenu(facesContext, menu);
		
		writer.endElement("div");
				
		writer.endElement("div");
	}
	
	protected void renderTieredMenu(FacesContext facesContext, Menu menu) throws IOException{
		ResponseWriter writer = facesContext.getResponseWriter();
		
		writer.startElement("ul", null);
		writer.writeAttribute("class", "first-of-type", null);
		
		for(UIComponent child : menu.getChildren()) {
			encodeTieredSubmenu(facesContext, (Submenu) child);
		}
		
		writer.endElement("ul");
	}

	protected void encodeRegularMenu(FacesContext facesContext, Menu menu) throws IOException{
		ResponseWriter writer = facesContext.getResponseWriter();
		boolean firstSubMenu = true;
		
		for(UIComponent child : menu.getChildren()) {
			Submenu submenu = (Submenu) child;
			
			if(submenu.isRendered()) {
				//Submenu title
				if(submenu.getLabel() != null) {
					writer.startElement("h6", null);
					String labelStyleClass = submenu.getLabelStyleClass();

					if(firstSubMenu && labelStyleClass == null) {
						writer.writeAttribute("class", "first-of-type", null);
					} else if(firstSubMenu && labelStyleClass != null) {
						writer.writeAttribute("class", "first-of-type " + labelStyleClass, null);
					} else if(!firstSubMenu && labelStyleClass != null) {
						writer.writeAttribute("class", labelStyleClass, null);
					}
						
					if(submenu.getLabelStyle() != null) 
						writer.writeAttribute("style", submenu.getLabelStyle(), null);
			
					writer.write(submenu.getLabel());
						
					writer.endElement("h6");
				}
					
				//Submenu content
				writer.startElement("ul", null);
				encodeSubmenuItems(facesContext, submenu);
				writer.endElement("ul");
				
				firstSubMenu = false;
			}		
		}	
	}
	
	protected void encodeTieredSubmenu(FacesContext facesContext, Submenu submenu) throws IOException{
		ResponseWriter writer = facesContext.getResponseWriter();
		String styleClass = submenu.getLabelStyleClass() == null ? "yuimenuitemlabel" : "yuimenuitemlabel " + submenu.getLabelStyleClass();
		String clientId = submenu.getClientId(facesContext);
		
		writer.startElement("li", null);
		writer.writeAttribute("class", "yuimenuitem", null);
		
			writer.startElement("a", null);
			writer.writeAttribute("href", "#" + clientId, null);
			writer.writeAttribute("class", styleClass, null);
			
			if(submenu.getLabel() != null)
				writer.write(submenu.getLabel());
			
			writer.endElement("a");
			
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
		
		writer.endElement("li");
	}

	protected void encodeSubmenuItems(FacesContext facesContext, Submenu submenu) throws IOException{
		ResponseWriter writer = facesContext.getResponseWriter();
		
		for(Iterator<UIComponent> iterator = submenu.getChildren().iterator(); iterator.hasNext();) {
			UIComponent child = (UIComponent) iterator.next();
			
			if(child instanceof MenuItem && child.isRendered()) {
				MenuItem menuItem = (MenuItem) child;
				String menuItemClientId = menuItem.getClientId(facesContext);
				String labelStyleClass = menuItem.getStyleClass() == null ? "yuimenuitemlabel" : "yuimenuitemlabel " + menuItem.getStyleClass();
				String icon = menuItem.getIcon() != null ? "background:url(" + getResourceURL(facesContext, menuItem.getIcon()) + ") no-repeat 1%;" : null;
				
				writer.startElement("li", null);
				writer.writeAttribute("class", "yuimenuitem", null);
				
				if(menuItem.getChildCount() > 0) {
					renderChildren(facesContext, menuItem);
				} else {
					writer.startElement("a", null);
					writer.writeAttribute("id", menuItemClientId, null);
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
						
						UIComponent form = ComponentUtils.findParentForm(facesContext, submenu);
						if(form == null) {
							throw new FacesException("Menu must be inside a form element");
						}
						
						String formClientId = form.getClientId(facesContext);
						String command = menuItem.isAjax() ? buildAjaxRequest(facesContext, menuItem, formClientId, menuItemClientId) : buildNonAjaxRequest(facesContext, menuItem, formClientId, menuItemClientId);
						
						command = menuItem.getOnclick() == null ? command : menuItem.getOnclick() + ";" + command;
						
						writer.writeAttribute("onclick", command, null);
					}
					
					//Label is deprecated
					if(menuItem.getLabel() != null) writer.write(menuItem.getLabel());
					if(menuItem.getValue() != null) writer.write((String) menuItem.getValue());
					
					writer.endElement("a");
				}
				
				writer.endElement("li");
			} else if(child instanceof Submenu && child.isRendered()) {				
				encodeTieredSubmenu(facesContext, (Submenu) child);
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