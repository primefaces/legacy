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
package org.primefaces.ui.touch.component.application;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.primefaces.ui.renderkit.CoreRenderer;
import org.primefaces.ui.resource.ResourceHolder;
import org.primefaces.ui.resource.ResourceUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ApplicationRenderer extends CoreRenderer {
	
	private static Logger logger = LoggerFactory.getLogger(ApplicationRenderer.class);

	public void encodeBegin(FacesContext facesContext, UIComponent component) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		Application application = (Application) component;
		ResourceHolder resourceHolder = ResourceUtils.getResourceHolder(facesContext);
		String widgetVar = createUniqueWidgetVar(facesContext, application);
		String themeRelativePath = "/jquery/plugins/jqtouch/themes/" + application.getTheme();
		String themeRealPath = ResourceUtils.getResourceURL(facesContext, themeRelativePath);
		
		writer.startElement("html", null);
		
		writer.startElement("head", null);
		
		renderCSSDependency(facesContext, themeRelativePath + "/theme.min.css");
		
		for(String resource : resourceHolder.getResources()) {
			if(resource.endsWith("css"))
				renderCSSDependency(facesContext, resource);
			else if(resource.endsWith("js"))
				renderScriptDependency(facesContext, resource);
			else
				logger.warn("Resource '{}' is queued for inclusion but it's not a supported type, only 'css' and 'js' files can be included.", resource);
				
			writer.write("\n");
		}
	
		writer.startElement("script", null);
		writer.writeAttribute("type", "text/javascript", null);
		
		writer.write(widgetVar + " = new PrimeFaces.touch.Application({");
		writer.write("themePath:'" + themeRealPath + "'");
		if(application.getIcon() != null)
			writer.write(",icon:'" + getResourceURL(facesContext, application.getIcon()) + "'");
		
		writer.write("});\n");
		
		writer.endElement("script");
		
		UIComponent meta = application.getFacet("meta");
		if(meta != null) {
			renderChild(facesContext, meta);
		}
		
		writer.endElement("head");
		
		writer.startElement("body", null);
	}
	
	public void encodeEnd(FacesContext facesContext, UIComponent component) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
				
		writer.endElement("body");
		writer.write("\n");
		writer.endElement("html");
	}
}