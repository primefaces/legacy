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
package org.primefaces.ui.component.resizable;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.primefaces.ui.renderkit.CoreRenderer;

public class ResizableRenderer extends CoreRenderer {

	public void encodeEnd(FacesContext facesContext, UIComponent component) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		Resizable resizable = (Resizable) component;
		String resizableVar = getResizableVar(resizable);
		String parentClientId = resizable.getParent().getClientId(facesContext);

		writer.startElement("script", resizable);
		writer.writeAttribute("type", "text/javascript", null);

		writer.write("YAHOO.util.Event.addListener(window, \"load\", function(e) {\n");
		
		writer.write(resizableVar + " = new YAHOO.util.Resize(\""+ parentClientId + "\",{");
		writer.write("proxy:" + resizable.isProxy());
		if(resizable.isStatus()) writer.write(",status:" + resizable.isStatus());
		if(resizable.isKnobHandles()) writer.write(",knobHandles: true");
		if(resizable.isGhost()) writer.write(",ghost: true");
		if(resizable.isAnimate()) {
			writer.write(",animate:true");
			writer.write(",animateDuration:" + resizable.getAnimateDuration());
			writer.write(",animateEasing:YAHOO.util.Easing." + resizable.getEffect());
		}
		if(resizable.getHandles() != null) {
			if(resizable.getHandles().equals("all"))
				writer.write(",handles:\"all\"");
			else
				writer.write(",handles:" + convertHandlesToJSArray(resizable.getHandles()) + "");
		}
		
		writer.write("});\n");
		
		writer.write("});\n");
		
		writer.endElement("script");
	}
	
	private String getResizableVar(Resizable resizable) {
		if (resizable.getWidgetVar() != null)
			return resizable.getWidgetVar();
		else
			return "pf_resizer_" + resizable.getId();
	}

	private String convertHandlesToJSArray(String value) {
		String[] tokens = value.split(",");
		StringBuffer buffer = new StringBuffer();
		buffer.append("[");
		
		for(String s : tokens) {
			buffer.append("\"");
			buffer.append(s);
			buffer.append("\"");
		}
		
		buffer.append("]");
		
		return buffer.toString();
	}
}