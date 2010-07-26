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
package org.primefaces.ui.component.menubar;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.primefaces.ui.component.menuitem.MenuItem;
import org.primefaces.ui.component.submenu.Submenu;
import org.primefaces.ui.renderkit.CoreRenderer;

public class MenubarRenderer extends CoreRenderer{

	public void encodeEnd(FacesContext facesContext, UIComponent component) throws IOException{
		Menubar menubar = (Menubar) component;
		
		encodeMenuScript(facesContext, menubar);
		encodeMenuMarkup(facesContext, menubar);
	}

	private void encodeMenuScript(FacesContext facesContext, Menubar menubar) throws IOException{
		ResponseWriter writer = facesContext.getResponseWriter();
		String clientId = menubar.getClientId(facesContext);
		
		String menubarVar = createUniqueWidgetVar(facesContext, menubar);
		
		writer.startElement("script", null);
		writer.writeAttribute("type", "text/javascript", null);
		
		writer.write("YAHOO.util.Event.onContentReady(\"" + clientId + ":container\", function() {\n");
		writer.write(menubarVar + " = new YAHOO.widget.MenuBar(\"" + clientId + ":container\",{");
		
		writer.write("autosubmenudisplay:" + menubar.isAutoSubmenuDisplay());
		
		if(!menubar.getEffect().equals("NONE")) {
			writer.write(",effect: {effect: YAHOO.widget.ContainerEffect." + menubar.getEffect());
			
			if(menubar.getEffectDuration() != 0.25)
				writer.write(",duration:" + menubar.getEffectDuration() + "}");
			else
				writer.write(",duration: 0.25}");
		}
		
		writer.write("})\n;");
	
		writer.write(menubarVar + ".render();\n");
		
		writer.write("});\n");
		
		writer.endElement("script");	
	}

	private void encodeMenuMarkup(FacesContext facesContext, Menubar menubar) throws IOException{
		ResponseWriter writer = facesContext.getResponseWriter();
		String clientId = menubar.getClientId(facesContext);
		
		
		writer.startElement("div", null);
		writer.writeAttribute("id", clientId + ":container", null);
		writer.writeAttribute("class", "yuimenubar", null);
		
		writer.startElement("div", null);
		writer.writeAttribute("class", "bd", null);
		
		writer.startElement("ul", null);
		writer.writeAttribute("class", "first-of-type", null);
		
		List<UIComponent> children = menubar.getChildren();
		for (int i=0; i < children.size(); i++) {
			Submenu submenu = (Submenu) children.get(i);
			
			writer.startElement("li", null);
			if(i == 0)
				writer.writeAttribute("class", "yuimenubaritem first-of-type", null);
			else
				writer.writeAttribute("class", "yuimenubaritem", null);
			
			encodeSubmenu(facesContext, submenu);
			
			writer.endElement("li");
		}
		
		writer.endElement("ul");
		writer.endElement("div");
		writer.endElement("div");
	}
	
	private void encodeSubmenu(FacesContext facesContext, Submenu submenu) throws IOException{
		ResponseWriter writer = facesContext.getResponseWriter();
		
		writer.startElement("a", null);
		writer.writeAttribute("class", "yuimenubaritemlabel", null);
		if(submenu.getTitle() != null)
			writer.write(submenu.getTitle());
			
		writer.endElement("a");
		
		writer.startElement("div", null);
		writer.writeAttribute("class", "yuimenu", null);
		
		writer.startElement("div", null);
		writer.writeAttribute("class", "bd", null);
		writer.startElement("ul", null);
		
		encodeSubmenuItems(facesContext, submenu);
		
		writer.endElement("ul");
		writer.endElement("div");
		writer.endElement("div");
	}
	
	private void encodeSubmenuItems(FacesContext facesContext, Submenu submenu) throws IOException{
		ResponseWriter writer = facesContext.getResponseWriter();
		
		for (Iterator<UIComponent> iterator = submenu.getChildren().iterator(); iterator.hasNext();) {
			UIComponent child = (UIComponent) iterator.next();
			
			if(child instanceof MenuItem) {
				MenuItem menuItem = (MenuItem) child;
				
				writer.startElement("li", null);
				writer.writeAttribute("class", "yuimenuitem", null);
				
				writer.startElement("a", null);
				writer.writeAttribute("class", "yuimenuitemlabel", null);
				writer.writeAttribute("href", menuItem.getUrl(), null);
				
				if(menuItem.getTarget() != null)
					writer.writeAttribute("target", menuItem.getTarget(), null);
				
				if(menuItem.getLabel() != null)
					writer.write(menuItem.getLabel());
				
				writer.endElement("a");
				
				writer.endElement("li");
				
			} else if(child instanceof Submenu) {
				Submenu childSubmenu = (Submenu) child;
				
				encodeSubmenu(facesContext, childSubmenu);
			}
		}
	}
}