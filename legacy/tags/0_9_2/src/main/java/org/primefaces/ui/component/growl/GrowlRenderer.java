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
package org.primefaces.ui.component.growl;

import java.io.IOException;
import java.util.Iterator;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.primefaces.ui.renderkit.CoreRenderer;
import org.primefaces.ui.resource.ResourceUtils;
import org.primefaces.ui.util.ComponentUtils;

public class GrowlRenderer extends CoreRenderer {

	public void encodeEnd(FacesContext facesContext, UIComponent component) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		Growl growl = (Growl) component;
		String clientId = growl.getClientId(facesContext);
		
		writer.startElement("script", null);
		writer.writeAttribute("type", "text/javascript", null);
		
		writer.write("YAHOO.util.Event.onContentReady('" + clientId + "', function() {\n");

		Iterator<FacesMessage> messages = growl.isGlobalOnly() ? facesContext.getMessages(null) : facesContext.getMessages();
		
		while(messages.hasNext()) {
			FacesMessage message = messages.next();
			String severityImage = getImage(facesContext, message);
			writer.write("jQuery.gritter.add({");
			
			if(growl.isShowSummary() && growl.isShowDetail()) 
				writer.write("title:'" + message.getSummary() + "',text:'" + message.getDetail() + "'");
			else if(growl.isShowSummary() && !growl.isShowDetail())
				writer.write("title:'',text:'" + message.getSummary() + "'");
			else if(!growl.isShowSummary() && growl.isShowDetail())
				writer.write("title:'',text:'" + message.getDetail() + "'");
			
			if(!ComponentUtils.isValueBlank(severityImage))
				writer.write(",image:'" + severityImage + "'");
			
			if(growl.isSticky())
				writer.write(",sticky:true");
			else
				writer.write(",sticky:false");
			
			if(growl.getLife() != 3000) writer.write(",time:" + growl.getLife());
			
			writer.write("});\n");	
		}
		
		writer.write("});\n");
		
		writer.endElement("script");
		
		writer.startElement("span", growl);
		writer.writeAttribute("id", clientId, "id");
		writer.endElement("span");
	}
	
	private String getImage(FacesContext facesContext, FacesMessage message) {
		if(message.getSeverity() == null)
			return "";
		else if(message.getSeverity().equals(FacesMessage.SEVERITY_INFO))
			return ResourceUtils.getResourceURL(facesContext, Growl.INFO_ICON);
		else if(message.getSeverity().equals(FacesMessage.SEVERITY_WARN))
			return ResourceUtils.getResourceURL(facesContext, Growl.WARN_ICON);
		else if(message.getSeverity().equals(FacesMessage.SEVERITY_ERROR))
			return ResourceUtils.getResourceURL(facesContext, Growl.ERROR_ICON);
		else if(message.getSeverity().equals(FacesMessage.SEVERITY_FATAL))
			return ResourceUtils.getResourceURL(facesContext, Growl.FATAL_ICON);
		else
			return "";
	}
}