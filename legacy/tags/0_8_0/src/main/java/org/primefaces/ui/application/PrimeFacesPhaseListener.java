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
package org.primefaces.ui.application;

import java.io.IOException;
import java.util.Map;

import javax.faces.component.ContextCallback;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;
import javax.faces.render.RenderKit;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.primefaces.ui.component.api.AjaxComponent;

public class PrimeFacesPhaseListener implements PhaseListener{

	public void afterPhase(PhaseEvent phaseEvent) {
		//Do nothing
	}
	
	public void beforePhase(PhaseEvent phaseEvent) {
		FacesContext facesContext = phaseEvent.getFacesContext();
		Map<String,String> params = facesContext.getExternalContext().getRequestParameterMap();
		
		boolean isAjaxRequest = params.containsKey("primefacesAjaxRequest");

		if(isAjaxRequest) {
			initPartialResponseWriter(facesContext);
			
			boolean isPPRRequest = !params.containsKey("ajaxSource");
			
			if(isPPRRequest) {
				String updateIds = params.get("update");
				String[] ids = updateIds.split(",");
				
				ServletResponse response = (ServletResponse) facesContext.getExternalContext().getResponse();
				response.setContentType("text/xml");
				
				ResponseWriter writer = facesContext.getResponseWriter();
				try {
					writer.write("<components>");
					
					for(String id : ids) {
						String clientId = id.trim();
						
						writer.write("<component>");
						writer.write("<id>" + clientId + "</id>");
						writer.write("<output>");
						writer.write("<![CDATA[");
						boolean found = facesContext.getViewRoot().invokeOnComponent(facesContext, clientId, RENDER_PARTIAL_RESPONSE);
						writer.write("]]>");
						writer.write("</output>");
						writer.write("</component>");
					}
					
					writer.write("</components>");
					
					facesContext.responseComplete();
				}catch(IOException exception) {
					exception.printStackTrace();
				}
			} else {
				String ajaxSource = params.get("ajaxSource");
				
				boolean found = facesContext.getViewRoot().invokeOnComponent(facesContext, ajaxSource, RENDER_PARTIAL_RESPONSE);
				
				facesContext.responseComplete();
			}
		}
	}

	public PhaseId getPhaseId() {
		return PhaseId.RENDER_RESPONSE;
	}
	
	public static final ContextCallback RENDER_PARTIAL_RESPONSE = new ContextCallback() {
		public void invokeContextCallback(FacesContext facesContext, UIComponent component) {
			try {
				if(component instanceof AjaxComponent) {
					((AjaxComponent) component).processPartialLifecycle(facesContext);
				}else {
					component.encodeAll(facesContext);
				}
			} catch (IOException e) {
				e.printStackTrace();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	};
	
	public void initPartialResponseWriter(FacesContext facesContext) {
		if(facesContext.getResponseWriter() != null)
			return;
		
		try {
			ServletResponse response = (ServletResponse) facesContext.getExternalContext().getResponse();
			ServletRequest request = (ServletRequest) facesContext.getExternalContext().getRequest();
			
			RenderKit renderKit = facesContext.getRenderKit();
			ResponseWriter responseWriter = renderKit.createResponseWriter(response.getWriter(), null, request.getCharacterEncoding());
			facesContext.setResponseWriter(responseWriter);
		}catch(IOException exception) {
			exception.printStackTrace();
		}
	}
}
