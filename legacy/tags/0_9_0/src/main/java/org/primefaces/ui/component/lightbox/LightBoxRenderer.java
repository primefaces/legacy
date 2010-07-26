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
package org.primefaces.ui.component.lightbox;

import java.io.IOException;
import java.util.Iterator;

import javax.faces.component.UIComponent;
import javax.faces.component.html.HtmlOutputLink;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.primefaces.ui.renderkit.CoreRenderer;

public class LightBoxRenderer extends CoreRenderer {

	@Override
	public void encodeBegin(FacesContext facesContext, UIComponent component) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		LightBox lightBox = (LightBox) component;
		String clientId = lightBox.getClientId(facesContext);
		String widgetVar = createUniqueWidgetVar(facesContext, lightBox);
		
		writer.startElement("script", null);
		writer.writeAttribute("type", "text/javascript", null);
		
		writer.write("YAHOO.util.Event.onContentReady('" + clientId + "', function() {\n");
		writer.write("var lightBoxTarget = PrimeFaces.core.Utils.escapeClientId('" + clientId + "')" + " + ' a';\n");
		writer.write(widgetVar + " = jQuery(lightBoxTarget).colorbox({");
		writer.write("transition:'" + lightBox.getTransition() + "'");
		encodeCFG(facesContext, lightBox);
		writer.write("});\n");
		writer.write("});\n");
		
		writer.endElement("script");
		
		writer.startElement("div", lightBox);
		writer.writeAttribute("id", clientId, "id");
		if(lightBox.getStyle() != null)
			writer.writeAttribute("style", lightBox.getStyle(), null);
		if(lightBox.getStyleClass() != null)
			writer.writeAttribute("class", lightBox.getStyleClass(), null);
		
		groupChildren(facesContext, lightBox);
	}
	
	private void encodeCFG(FacesContext facesContext, LightBox lightBox) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		String clientId = lightBox.getClientId(facesContext);
		
		if(lightBox.getSpeed() != 350) writer.write(",speed:" + lightBox.getSpeed());
		if(lightBox.getWidth() != null) writer.write(",width:'" + lightBox.getWidth() + "'");
		if(lightBox.getHeight() != null) writer.write(",height:'" + lightBox.getHeight() + "'");
		if(lightBox.isIframe()) writer.write(",iframe:true");
		if(lightBox.getFacet("inline") != null) {
			writer.write(",inline:true");
			writer.write(",href:'#" + clientId + "_inline'");
		}
		if(lightBox.getOpacity() != 0.85) writer.write(",opacity:" + lightBox.getOpacity());
		if(lightBox.isVisible()) writer.write(",open:true");
		if(lightBox.isSlideshow()) {
			writer.write(",slideshow:true");
			writer.write(",slideshowSpeed:" + lightBox.getSlideshowSpeed());
			
			if(lightBox.getSlideshowStartText() != null) writer.write(",slideshowStart:'" + lightBox.getSlideshowStartText() + "'");
			if(lightBox.getSlideshowStopText() != null) writer.write(",slideshowStop:'" + lightBox.getSlideshowStopText() + "'");
			if(!lightBox.isSlideshowAuto()) writer.write(",slideshowAuto:false");
		}
		if(!lightBox.isOverlayClose()) writer.write(",overlayClose:false");
		if(lightBox.getCurrentTemplate() != null) writer.write(",current:'" + lightBox.getCurrentTemplate() + "'");
	}

	@Override
	public void encodeEnd(FacesContext facesContext, UIComponent component) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		LightBox lightBox = (LightBox) component;
		String clientId = lightBox.getClientId(facesContext);
		
		if(lightBox.getFacet("inline") != null) {
			writer.startElement("div", null);
			writer.writeAttribute("style", "display:none", null);
			
			writer.startElement("div", null);
			writer.writeAttribute("id", clientId + "_inline", null);
			
			renderChild(facesContext, lightBox.getFacet("inline"));
			
			writer.endElement("div");
			writer.endElement("div");
		}
		
		writer.endElement("div");
	}
	
	private void groupChildren(FacesContext facesContext, LightBox lightBox) {
		String clientId = lightBox.getClientId(facesContext);
		
		for (Iterator<UIComponent> iterator = lightBox.getChildren().iterator(); iterator.hasNext();) {
			UIComponent kid = iterator.next();
			
			if(kid instanceof HtmlOutputLink)
				((HtmlOutputLink) kid).setRel(clientId);
		}
	}
}