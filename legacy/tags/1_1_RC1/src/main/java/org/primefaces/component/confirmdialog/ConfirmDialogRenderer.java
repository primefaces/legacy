/*
 * Copyright 2010 Prime Technology.
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
package org.primefaces.component.confirmdialog;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.primefaces.renderkit.CoreRenderer;

public class ConfirmDialogRenderer extends CoreRenderer {

	@Override
	public void encodeEnd(FacesContext facesContext, UIComponent component) throws IOException {
		ConfirmDialog dialog = (ConfirmDialog) component;
		
		encodeMarkup(facesContext, dialog);
		encodeScript(facesContext, dialog);
	}

	protected void encodeMarkup(FacesContext facesContext, ConfirmDialog dialog) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		String clientId = dialog.getClientId(facesContext);
		
		writer.startElement("div", null);
		writer.writeAttribute("id", clientId , null);
		if(dialog.getHeader() != null) {
			writer.writeAttribute("title", dialog.getHeader(), null);
		}
		
		//body		
		writer.startElement("p", null);
		
		//severity
		writer.startElement("span", null);
		writer.writeAttribute("style", "float: left; margin: 0pt 7px 20px 0pt;", null);
		writer.writeAttribute("class", "ui-icon ui-icon-" + dialog.getSeverity(), null);
		writer.endElement("span");
		
		if(dialog.getMessage() != null) {
			writer.write(dialog.getMessage());
		}
		writer.endElement("p");
		
		//buttons
		writer.startElement("div", null);
		writer.writeAttribute("id", clientId + "_buttons", null);
		renderChildren(facesContext, dialog);
		writer.endElement("div");
		
		writer.endElement("div");
	}

	protected void encodeScript(FacesContext facesContext, ConfirmDialog dialog) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		String clientId = dialog.getClientId(facesContext);
		String var = createUniqueWidgetVar(facesContext, dialog);
		
		writer.startElement("script", dialog);
		writer.writeAttribute("type", "text/javascript", null);
		
		writer.write(var + " = new PrimeFaces.widget.ConfirmDialog('" + clientId + "', {");
		
		writer.write("minHeight:0");
		
		if(dialog.getStyleClass() != null) writer.write(",dialogClass:'" + dialog.getStyleClass() + "'");
		if(dialog.getWidth() != 300) writer.write(",width:" + dialog.getWidth());
		if(dialog.getHeight() != Integer.MIN_VALUE) writer.write(",height:" + dialog.getHeight());
		if(!dialog.isDraggable()) writer.write(",draggable:false");
		if(dialog.isModal()) writer.write(",modal: true");
		if(dialog.getZindex() != 1000) writer.write(",zIndex:" + dialog.getZindex());
		if(dialog.getShowEffect() != null) writer.write(",show:'" + dialog.getShowEffect() + "'");
		if(dialog.getHideEffect() != null) writer.write(",hide:'" + dialog.getHideEffect() + "'");
		
		//Position
		String position = dialog.getPosition();	
		if(position != null) {
			if(position.contains(","))
				writer.write(",position:[" + position + "]");
			else
				writer.write(",position:'" + position + "'");
		}
		
		writer.write("});");

		writer.endElement("script");
	}
	
	public void encodeChildren(FacesContext context, UIComponent component) throws IOException {
		//Do Nothing
	}
	
	public boolean getRendersChildren() {
		return true;
	}
}