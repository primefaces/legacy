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
package org.primefaces.ui.renderkit;

import java.io.IOException;
import java.util.Iterator;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.Renderer;

import org.primefaces.ui.resource.ResourceUtils;
import org.primefaces.ui.util.HTML;

public class CoreRenderer extends Renderer {
	
	private final static String WIDGET_VAR_SUFFIX = "_widget";
	
	protected void renderScriptDependency(FacesContext facesContext, String scriptPath) throws IOException{
		ResponseWriter writer = facesContext.getResponseWriter();
		
		writer.startElement("script", null);
		writer.writeAttribute("type", "text/javascript", null);
		writer.writeAttribute("src", ResourceUtils.getResourceURL(facesContext, scriptPath), null);
		writer.endElement("script");
	}
	
	protected void renderCSSDependency(FacesContext facesContext, String cssPath) throws IOException{
		ResponseWriter writer = facesContext.getResponseWriter();
		
		writer.startElement("link", null);
		writer.writeAttribute("rel", "stylesheet", null);
		writer.writeAttribute("type", "text/css", null);
		writer.writeAttribute("href", ResourceUtils.getResourceURL(facesContext, cssPath), null);
		writer.endElement("link");
	}
	
	protected void renderChildren(FacesContext facesContext, UIComponent component) throws IOException {
		for (Iterator<UIComponent> iterator = component.getChildren().iterator(); iterator.hasNext();) {
			UIComponent child = (UIComponent) iterator.next();
			renderChild(facesContext, child);
		}
	}

	protected void renderChild(FacesContext facesContext, UIComponent child) throws IOException {
		if (!child.isRendered()) {
			return;
		}

		child.encodeBegin(facesContext);
		
		if (child.getRendersChildren()) {
			child.encodeChildren(facesContext);
		} else {
			renderChildren(facesContext, child);
		}
		child.encodeEnd(facesContext);
	}
	
	protected String getActionURL(FacesContext facesContext) {
		String actionURL = facesContext.getApplication().getViewHandler().getActionURL(facesContext, facesContext.getViewRoot().getViewId());
		String url = facesContext.getExternalContext().encodeResourceURL(actionURL);
		
		return url;
	}
	
	public boolean isPostback(FacesContext facesContext) {
		return facesContext.getRenderKit().getResponseStateManager().isPostback(facesContext);
	}

	protected void renderDomEvents(FacesContext facesContext, UIComponent component, String var) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		
		for(String event : HTML.DOM_EVENTS) {			
			String eventHandler = (String) component.getAttributes().get(event);
			
			if(eventHandler != null)
				writer.write(var + ".addListener(\"" + event.substring(2, event.length()) + "\", function(e){" + eventHandler + ";});\n");
		}
	}
	
	/**
	 * Generates a unique javascript variable name if component has a client side widget.
	 * Portlet namespaces are considered if externalcontext is a portlet environment. Algorith works as follows.
	 *
	 * If there's a user provided widgetVar, it's returned without further processing.
	 * ":"s in client id is replace with underscore since ":" is invalid for js variable names.
	 * WIDGET_VAR_SUFFIX is added to the formatted client id.
	 * 
	 * Finally variable name is encoded with ExternalContext.encodeNamespace to make sure it's also unique in a portal
	 *  
	 * @param facesContext
	 * @param component
	 * 
	 * @return
	 */
	protected String createUniqueWidgetVar(FacesContext facesContext, UIComponent component) {
		String widgetVar = (String) component.getAttributes().get("widgetVar");
		if(widgetVar != null)
			return widgetVar;
		
		String formattedClientId = component.getClientId(facesContext).replaceAll(":", "_");
		String variableName = formattedClientId + WIDGET_VAR_SUFFIX;
		
		return facesContext.getExternalContext().encodeNamespace(variableName);
	}
}
