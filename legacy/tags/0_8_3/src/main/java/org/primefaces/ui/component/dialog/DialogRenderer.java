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
package org.primefaces.ui.component.dialog;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.primefaces.ui.renderkit.CoreRenderer;

public class DialogRenderer extends CoreRenderer{

	public void encodeEnd(FacesContext facesContext, UIComponent component) throws IOException {
		Dialog dialog = (Dialog) component;
		
		encodeDialogScript(facesContext, dialog);
		encodeDialogMarkup(facesContext, dialog);
	}
	
	public void encodeChildren(FacesContext facesContext, UIComponent component) throws IOException {
		//Do nothing
	}

	public boolean getRendersChildren() {
		return true;
	}

	private void encodeDialogScript(FacesContext facesContext, Dialog dialog) throws IOException{
		ResponseWriter writer = facesContext.getResponseWriter();
		String clientId = dialog.getClientId(facesContext);
		
		String dialogVar = createUniqueWidgetVar(facesContext, dialog);
		String cfgVariable = dialogVar + "_cfg";
		
		writer.startElement("script", null);
		writer.writeAttribute("type", "text/javascript", null);
		
		writer.write("YAHOO.util.Event.onContentReady(\"" + clientId + "\", function() {\n");
		
		writeCFGVariable(facesContext, dialog, cfgVariable);

		writer.write(dialogVar + " = new YAHOO.widget.Panel(\"" + clientId + "\", " + cfgVariable + " );\n");
		writer.write(dialogVar + ".render();");
		writer.write("});\n");
		
		writer.endElement("script");
	}

	private void writeCFGVariable(FacesContext facesContext, Dialog dialog, String cfgVariable) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		
		writer.write("var " + cfgVariable + " = {\n");
		writer.write("visible:" + dialog.isVisible());
		if(dialog.getWidth() != null) writer.write(",width: '" + dialog.getWidth() + "'");
		if(dialog.getHeight() != null) writer.write(",height: '" + dialog.getHeight() + "'");
		if(!dialog.isDraggable()) writer.write(",draggable: false");
		if(dialog.getUnderlay() != null && !dialog.getUnderlay().equalsIgnoreCase("shadow")) writer.write(",underlay: '" + dialog.getUnderlay() + "'");
		if(dialog.isFixedCenter()) writer.write(",fixedcenter: true");
		if(!dialog.isClose()) writer.write(",close: false");
		if(dialog.isConstrainToViewport()) writer.write(",constraintoviewport: true");
		if(dialog.getX() != -1) writer.write(",x:" + dialog.getX());
		if(dialog.getY() != -1) writer.write(",y:" + dialog.getY());
		if(dialog.getEffect() != null) {
			writer.write(",effect:{effect:YAHOO.widget.ContainerEffect." + dialog.getEffect().toUpperCase() + ", duration: " + dialog.getEffectDuration() + "}");
		}
		if(dialog.isModal()) {
			writer.write(",zindex:4");
			writer.write(",modal: true");
		}
		
		writer.write("};\n");
	}

	private void encodeDialogMarkup(FacesContext facesContext, Dialog dialog) throws IOException{
		ResponseWriter writer = facesContext.getResponseWriter();
		String clientId = dialog.getClientId(facesContext);
		
		writer.startElement("div", null);
		writer.writeAttribute("id", clientId , null);
		
		encodeHeader(facesContext, dialog);
		encodeBody(facesContext, dialog);
		encodeFooter(facesContext, dialog);
		
		writer.endElement("div");
	}
	
	private void encodeHeader(FacesContext facesContext, Dialog dialog) throws IOException{
		ResponseWriter writer = facesContext.getResponseWriter();
		
		writer.startElement("div", null);
		writer.writeAttribute("class", "hd", null);
		if(dialog.getHeader() != null)
			writer.write(dialog.getHeader());
			
		writer.endElement("div");
	}
	
	private void encodeBody(FacesContext facesContext, Dialog dialog) throws IOException{
		ResponseWriter writer = facesContext.getResponseWriter();
		
		writer.startElement("div", null);
		writer.writeAttribute("class", "bd", null);
		renderChildren(facesContext, dialog);
		writer.endElement("div");
	}

	private void encodeFooter(FacesContext facesContext, Dialog dialog) throws IOException{
		ResponseWriter writer = facesContext.getResponseWriter();
		
		writer.startElement("div", null);
		writer.writeAttribute("class", "ft", null);
		if(dialog.getFooter() != null)
			writer.write(dialog.getFooter());
		
		writer.endElement("div");
	}
}