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
package org.primefaces.application;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.event.ActionListener;

import org.primefaces.context.RequestContext;

/**
 * Releases current RequestContext in thread local if a redirect happens after processing actions
 */
public class CleanupActionListener implements ActionListener {

	private static final Logger logger = Logger.getLogger(CleanupActionListener.class.getName());
	
	private ActionListener base;
	
	public CleanupActionListener(ActionListener base) {
		this.base = base;
	}
	
	public void processAction(ActionEvent actionEvent) throws AbortProcessingException {
		base.processAction(actionEvent);
		
		if(FacesContext.getCurrentInstance().getResponseComplete()) {
			if(logger.isLoggable(Level.FINE)) {
				logger.fine("Releasing RequestContext after redirect");
			}
			
			RequestContext.getCurrentInstance().release();
		}
	}

}
