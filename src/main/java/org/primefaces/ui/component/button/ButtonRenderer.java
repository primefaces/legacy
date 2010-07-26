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
package org.primefaces.ui.component.button;

import java.io.IOException;
import java.util.List;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.event.ActionEvent;

import org.primefaces.ui.component.confirmdialog.ConfirmDialog;
import org.primefaces.ui.renderkit.CoreRenderer;
import org.primefaces.ui.util.ComponentUtils;

public class ButtonRenderer extends CoreRenderer {

	public void decode(FacesContext facesContext, UIComponent component) {
		Button button = (Button) component;
		
		String clientId = button.getClientId(facesContext) + ":button";

		if (!button.getType().equalsIgnoreCase("reset") && facesContext.getExternalContext().getRequestParameterMap().containsKey(clientId))
			button.queueEvent(new ActionEvent(button));
	}

	public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
		Button button = (Button) component;

		encodeScript(context, button);
		encodeMarkup(context, button);
	}

	protected void encodeMarkup(FacesContext facesContext, Button button) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		String clientId = button.getClientId(facesContext);
		ConfirmDialog confirmDialog = getConfirmation(button);

		writer.startElement("div", null);
		writer.writeAttribute("id", clientId, "id");
		
		if(confirmDialog != null) {
			encodeButtonMarkup(facesContext, button, clientId + ":proxy", "button", (String) button.getValue());	//proxy button
			encodeConfirmDialogMarkup(facesContext, button, confirmDialog);
		} else {
			encodeButtonMarkup(facesContext, button, clientId + ":button", resolveType(button), (String) button.getValue());
		}
		
		writer.endElement("div");
	}
	
	protected ConfirmDialog getConfirmation(Button button) {
		List<UIComponent> kids = button.getChildren();
		
		for(UIComponent kid : kids) {
			if(kid instanceof ConfirmDialog)
				return (ConfirmDialog) kid;
		}
		
		return null;
	}
	
	protected void encodeConfirmDialogMarkup(FacesContext facesContext, Button button, ConfirmDialog confirmDialog) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		String clientId = button.getClientId(facesContext);
		
		writer.startElement("div", null);
		writer.writeAttribute("id", clientId + ":confirmationContainer", null);
		
		//header
		writer.startElement("div", null);
		writer.writeAttribute("class", "hd", null);
		if(confirmDialog.getHeader() != null)
			writer.write(confirmDialog.getHeader());
		
		writer.endElement("div");
		
		//body
		writer.startElement("div", null);
		writer.writeAttribute("class", "bd", null);
		
		if(confirmDialog.getMessage() != null)
			writer.write(confirmDialog.getMessage());

		writer.endElement("div");
		
		//footer
		writer.startElement("div", null);
		writer.writeAttribute("class", "ft", null);

		encodeButtonMarkup(facesContext, null, clientId + ":button", resolveType(button), confirmDialog.getYesLabel());
		encodeButtonMarkup(facesContext, null, clientId + ":noButton", "button", confirmDialog.getNoLabel());
		
		writer.endElement("div");
		
		writer.endElement("div");
	}
 	
	protected void encodeButtonMarkup(FacesContext facesContext, Button button, String id, String type, String label) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();

		writer.startElement("span", null);
		writer.writeAttribute("id", id + ":container", null);
		writer.writeAttribute("class", "yui-button yui-push-button", null);
		
		writer.startElement("span", null);
		writer.writeAttribute("class", "first-child", null);
		
		writer.startElement("button", null);
		writer.writeAttribute("id", id, null);
		writer.writeAttribute("name", id, null);
		writer.writeAttribute("type", type, null);
		
		if(button != null) {
			if(button.getStyle() != null)
				writer.writeAttribute("style", button.getStyle(), null);
			
			if(button.getStyleClass() != null)
				writer.writeAttribute("class", button.getStyleClass(), null);
		}
		
		if(label != null)
			writer.write(label);
		
		writer.endElement("button");
		
		writer.endElement("span");
		writer.endElement("span");
	}
	
	private void writeConfirmDialogCFGVariable(FacesContext facesContext, ConfirmDialog dialog, String cfgVariable) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		
		writer.write("var " + cfgVariable + " = {\n");
		writer.write("visible: false");
		writer.write(",icon: YAHOO.widget.SimpleDialog.ICON_" + dialog.getSeverity().toUpperCase());
		if(dialog.getWidth() != null) writer.write(",width: '" + dialog.getWidth() + "'");
		if(dialog.getHeight() != null) writer.write(",height: '" + dialog.getHeight() + "'");
		if(!dialog.isDraggable()) writer.write(",draggable: false");
		if(dialog.getUnderlay() != null && !dialog.getUnderlay().equalsIgnoreCase("shadow")) writer.write(",underlay: '" + dialog.getUnderlay() + "'");
		if(dialog.isFixedCenter()) writer.write(",fixedcenter: true");
		if(!dialog.isClose()) writer.write("close: false");
		if(dialog.isConstrainToViewport()) writer.write("constraintoviewport: true");
		if(dialog.getX() != -1) writer.write(",x:" + dialog.getX());
		if(dialog.getY() != -1) writer.write(",y:" + dialog.getY());
		if(dialog.isModal()) writer.write(",modal: true");
		if(dialog.getEffect() != null) writer.write(",effect:{effect:YAHOO.widget.ContainerEffect." + dialog.getEffect().toUpperCase() + ", duration: " + dialog.getEffectDuration() + "}");
		
		writer.write("};\n");
	}

	protected void encodeScript(FacesContext facesContext, Button button) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		String clientId = button.getClientId(facesContext);
		String buttonVar = getButtonVar(button);
		ConfirmDialog confirmDialog = getConfirmation(button);
		String confirmDialogVar = null;

		writer.startElement("script", button);
		writer.writeAttribute("type", "text/javascript", null);
		
		writer.write("YAHOO.util.Event.onContentReady(\"" + clientId + "\", function() {\n");
		
		if(confirmDialog != null) {
			confirmDialogVar = buttonVar + "_confirmDialog";
			String confirmDialogCFGVar = confirmDialogVar + "_cfg";
			writeConfirmDialogCFGVariable(facesContext, confirmDialog, confirmDialogCFGVar);
			writer.write(confirmDialogVar + " = new YAHOO.widget.SimpleDialog(\"" + clientId + ":confirmationContainer\", " + confirmDialogCFGVar + ");\n");
			writer.write(confirmDialogVar + ".render();");
			
			String proxyButtonVar = buttonVar + "_proxy";
			writer.write("var " + proxyButtonVar + "  = new YAHOO.widget.Button(\""+ clientId + ":proxy:container\");\n");
			writer.write(proxyButtonVar + ".addListener(\"click\", function(e){" + confirmDialogVar + ".show();});\n");
			
			String noButtonVar = buttonVar + "_no";
			writer.write("var " + noButtonVar + "  = new YAHOO.widget.Button(\""+ clientId + ":noButton:container\");\n");
			writer.write(noButtonVar + ".addListener(\"click\", function(e){" + confirmDialogVar + ".hide();});\n");
		}
		
		writer.write(buttonVar + " = new YAHOO.widget.Button(\""+ clientId + ":button:container\");\n");
		renderDomEvents(facesContext, button, buttonVar);
		
		if(button.isAsync() && button.getUpdate() != null) {
			writer.write("var ajaxRequestEvent = new PrimeFaces.ajax.AjaxRequestEvent(");
			writer.write("\"" + getActionURL(facesContext) + "\",");
			String parentFormClientId = ComponentUtils.findParentForm(facesContext, button).getClientId(facesContext);
			writer.write("{formClientId: \"" + parentFormClientId + "\",  partialSubmit: false");
			
			if(button.getOncomplete() != null) {
				writer.write(", onComplete: function() {" + button.getOncomplete() + ";}");
			} 
			
			writer.write("},");
			
			writer.write("\"update=" + button.getUpdate() + "&" + clientId + ":button=" + clientId + ":button&primefacesAjaxRequest=true\");\n");
			
			writer.write(buttonVar + ".addListener(\"click\", PrimeFaces.ajax.AjaxRequestEventHandler, ajaxRequestEvent);\n");
		}
		
		writer.write("});");

		writer.endElement("script");
	}
	
	protected String resolveType(Button button) {
		if(button.isAsync())
			return "button";
		else
			return button.getType();
	}

	protected String getButtonVar(Button button){
		if(button.getWidgetVar() != null)
			return button.getWidgetVar();
		else
			return "pf_button_" + button.getId();
	}
}