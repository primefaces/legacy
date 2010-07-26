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
package org.primefaces.ui.component.resources;

import java.io.IOException;

import javax.el.ValueExpression;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.primefaces.ui.renderkit.CoreRenderer;
import org.primefaces.ui.resource.ResourceHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ResourcesRenderer extends CoreRenderer {

	private static Logger logger = LoggerFactory.getLogger(ResourcesRenderer.class);
	
	public void encodeEnd(FacesContext facesContext, UIComponent component) throws IOException {
		ResourceHolder resourceQueue = getResourceHolder(facesContext);
		ResponseWriter writer = facesContext.getResponseWriter();
		Resources resources = (Resources) component;
		String excludedResources = resources.getExclude();
		
		writer.write("\n");
		
		for(String resource : resourceQueue.getResources()) {
			if(isResourceExcluded(excludedResources, resource))
				continue;
			
			if(resource.endsWith("css"))
				renderCSSDependency(facesContext, resource);
			else if(resource.endsWith("js"))
				renderScriptDependency(facesContext, resource);
			else
				logger.warn("Resource '{}' is queued for inclusion but it's not a supported type, only 'css' and 'js' files can be included.", resource);
				
			
			writer.write("\n");
		}
		
		writer.write("\n");
	}
	
	private boolean isResourceExcluded(String excludedResources, String resource) {
		if(excludedResources == null)
			return false;
		else
			return excludedResources.indexOf(resource) != -1;
		
	}
	
	protected ResourceHolder getResourceHolder(FacesContext facesContext) {
		ValueExpression ve = facesContext.getApplication().getExpressionFactory().createValueExpression(facesContext.getELContext(), "#{primeFacesResourceHolder}", ResourceHolder.class);
		
		return (ResourceHolder) ve.getValue(facesContext.getELContext());
	}
}