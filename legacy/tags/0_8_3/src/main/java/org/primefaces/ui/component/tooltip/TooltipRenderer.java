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
package org.primefaces.ui.component.tooltip;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.primefaces.ui.renderkit.CoreRenderer;
import org.primefaces.ui.util.ComponentUtils;

public class TooltipRenderer extends CoreRenderer{

	public void encodeEnd(FacesContext facesContext, UIComponent component) throws IOException {
		Tooltip tooltip = (Tooltip) component;
		
		encodeTooltipWidget(facesContext, tooltip);
	}

	private void encodeTooltipWidget(FacesContext facesContext, Tooltip tooltip) throws IOException{
		ResponseWriter writer = facesContext.getResponseWriter();
		String clientId = tooltip.getClientId(facesContext);
		String parentClientId = tooltip.getParent().getClientId(facesContext);
		
		String value = ComponentUtils.getStringValueToRender(facesContext, tooltip);
		String tooltipVar = createUniqueWidgetVar(facesContext, tooltip);
		
		writer.startElement("script", null);
		writer.writeAttribute("type", "text/javascript", null);
		
		writer.write("YAHOO.util.Event.onContentReady('" + parentClientId + "', function() {\n");
		writer.write(tooltipVar + " = new YAHOO.widget.Tooltip(\"" + clientId + ":container\",");
		writer.write("{context:\"" + parentClientId + "\", text:\"" + value  + "\"");
		
		if(tooltip.getShowDelay() != 200) writer.write(",showdelay:" + tooltip.getShowDelay());
		if(tooltip.getHideDelay() != 250) writer.write(",hidedelay:" + tooltip.getHideDelay());
		if(tooltip.getAutoDismissDelay() != 5000) writer.write(",autodismissdelay:" + tooltip.getAutoDismissDelay());
		if(tooltip.isDisabled()) writer.write(",disabled:true");

		writer.write("});\n");
		
		writer.write("});\n");
				
		writer.endElement("script");
	}
}