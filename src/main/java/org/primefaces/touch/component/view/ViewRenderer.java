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
package org.primefaces.touch.component.view;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.touch.component.navbarcontrol.NavBarControl;
import org.primefaces.util.ComponentUtils;

public class ViewRenderer extends CoreRenderer {

	public void encodeBegin(FacesContext facesContext, UIComponent component) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		View view = (View) component;
		
		writer.startElement("div", view);
		writer.writeAttribute("id", view.getClientId(facesContext), "id");
		
		//Title
		writer.startElement("div", null);
		
		writer.writeAttribute("class", "toolbar", null);
		writer.startElement("h1", null);
		if(view.getTitle() != null)
			writer.write(view.getTitle());
		writer.endElement("h1");
		
		encodeNavBarControls(facesContext, view);
		
		writer.endElement("div");
	}
	
	public void encodeEnd(FacesContext facesContext, UIComponent component) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		
		writer.endElement("div");
	}
	
	private void encodeNavBarControls(FacesContext facesContext, View view) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		NavBarControl leftControl = (NavBarControl) view.getFacet("leftNavBar");
		NavBarControl rightControl = (NavBarControl) view.getFacet("rightNavBar");

		if(leftControl != null) {
			String href="#";
			String viewId = leftControl.getView();
			String styleClass = leftControl.getType() != null ? leftControl.getType() : "";
			if(leftControl.getEffect() != null) {
				styleClass = styleClass + " " + leftControl.getEffect();
			}
			
			writer.startElement("a", null);
			writer.writeAttribute("class", styleClass, null);		
			if(viewId != null) {
				if(viewId.equals("home"))
					href = href + "home";
				else
					href = href + ComponentUtils.findComponentById(facesContext, facesContext.getViewRoot(), leftControl.getView()).getClientId(facesContext);
			}
			
			writer.writeAttribute("href", href, null);
			
			if(leftControl.getLabel() != null) {
				writer.write(leftControl.getLabel());
			}
			
			writer.endElement("a");
		}
		
		if(rightControl != null) {
			String href="#";
			String viewId = rightControl.getView();
			String styleClass = rightControl.getType() != null ? rightControl.getType() : "";
			if(rightControl.getEffect() != null) {
				styleClass = styleClass + " " + rightControl.getEffect();
			}
			
			writer.startElement("a", null);
			writer.writeAttribute("class", styleClass, null);
			if(viewId != null) {
				if(viewId.equals("home"))
					href = href + "home";
				else
					href = href + ComponentUtils.findComponentById(facesContext, facesContext.getViewRoot(), rightControl.getView()).getClientId(facesContext);
			}
			
			writer.writeAttribute("href", href, null);
			
			if(rightControl.getLabel() != null) {
				writer.write(rightControl.getLabel());
			}
			
			writer.endElement("a");
		}
	}
}
