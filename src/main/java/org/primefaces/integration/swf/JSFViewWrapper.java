/*
 * Copyright 2009-2010 Prime Technology.
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
package org.primefaces.integration.swf;

import java.io.IOException;
import java.io.Serializable;

import javax.faces.context.FacesContext;
import javax.faces.event.PhaseId;
import javax.faces.lifecycle.Lifecycle;

import org.springframework.faces.webflow.FlowFacesContext;
import org.springframework.faces.webflow.JsfUtils;
import org.springframework.faces.webflow.JsfView;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.RequestContext;
import org.springframework.webflow.execution.View;

/**
 * JSFViewWrapper fixed SWF's RenderResponse phase handling problems
 * 
 * Only used for Spring WebFlow Integration
 */
public class JSFViewWrapper implements View {
	
	private JsfView view;
	
	private Lifecycle lifecycle;
	
	private RequestContext context;
	
	public JSFViewWrapper(JsfView view, RequestContext context, Lifecycle lifecycle) {
		this.view = view;
		this.lifecycle = lifecycle;
		this.context = context;
	}

	public Event getFlowEvent() {
		return view.getFlowEvent();
	}

	public Serializable getUserEventState() {
		return view.getUserEventState();
	}

	public boolean hasFlowEvent() {
		return view.hasFlowEvent();
	}

	public void processUserEvent() {
		view.processUserEvent();
	}

	public void render() throws IOException {
		FacesContext facesContext = FlowFacesContext.newInstance(context, lifecycle);
		facesContext.setViewRoot(view.getViewRoot());
		try {
			JsfUtils.notifyBeforeListeners(PhaseId.RENDER_RESPONSE, lifecycle, facesContext);
			
			if(!facesContext.getResponseComplete()) {
				facesContext.getApplication().getViewHandler().renderView(facesContext, view.getViewRoot());
			}
			
			JsfUtils.notifyAfterListeners(PhaseId.RENDER_RESPONSE, lifecycle, facesContext);
		} finally {
			facesContext.responseComplete();
			facesContext.release();
		}
	}

	public void saveState() {
		view.saveState();
	}

	public boolean userEventQueued() {
		return view.userEventQueued();
	}
}