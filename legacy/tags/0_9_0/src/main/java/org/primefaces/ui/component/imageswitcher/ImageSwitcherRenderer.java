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
package org.primefaces.ui.component.imageswitcher;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.faces.component.UIComponent;
import javax.faces.component.UIGraphic;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.primefaces.ui.renderkit.CoreRenderer;

public class ImageSwitcherRenderer extends CoreRenderer {
	
	public void encodeEnd(FacesContext facesContext, UIComponent component) throws IOException {
		ImageSwitcher imageSwitcher = (ImageSwitcher) component;
		
		encodeScript(facesContext, imageSwitcher);
		encodeMarkup(facesContext, imageSwitcher);
	}

	private void encodeScript(FacesContext facesContext, ImageSwitcher imageSwitcher) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		String clientId = imageSwitcher.getClientId(facesContext);
		String widgetVar = createUniqueWidgetVar(facesContext, imageSwitcher);
		String imageClientId = imageSwitcher.getChildren().get(0).getClientId(facesContext);
			
		writer.startElement("script", null);
		writer.writeAttribute("type", "text/javascript", null);
		
		writer.write("YAHOO.util.Event.onContentReady('" + clientId + "', function() {\n");

		writer.write(widgetVar + " = new PrimeFaces.widget.ImageSwitcher('" + imageClientId + "',{");
		writer.write("effect:'" + imageSwitcher.getEffect() + "'");
		writer.write(",speed:" + imageSwitcher.getSpeed());
		writer.write(",slideshowSpeed:" + imageSwitcher.getSlideshowSpeed());
		writer.write(",slideshowAuto:" + imageSwitcher.isSlideshowAuto());
		writer.write("},");
		writer.write(getImagesAsJSArray(facesContext, imageSwitcher));
		writer.write(");\n");

		writer.write("});");

		writer.endElement("script");
	}
	
	private List<String> getImages(FacesContext facesContext, ImageSwitcher imageSwitcher) {
		List<String> images = new ArrayList<String>();
		
		for(UIComponent child : imageSwitcher.getChildren()) {
			if(child instanceof UIGraphic) {
				UIGraphic image = (UIGraphic) child;
				
				if(image.isRendered())
					images.add(getResourceURL(facesContext, image.getUrl()));
			}
		}
		
		return images;
	}
 	
	private void encodeMarkup(FacesContext facesContext, ImageSwitcher imageSwitcher) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		String clientId = imageSwitcher.getClientId(facesContext);
		
		writer.startElement("span", imageSwitcher);
		writer.writeAttribute("id", clientId, "id");
		UIComponent firstChild = imageSwitcher.getChildren().get(0);
		renderChild(facesContext, firstChild);
		writer.endElement("span");
	}
	
	private String getImagesAsJSArray(FacesContext facesContext, ImageSwitcher imageSwitcher) {
		List<String> images = getImages(facesContext, imageSwitcher);
		StringBuilder array = new StringBuilder();
		array.append("[");
	
		for (Iterator<String> iterator = images.iterator(); iterator.hasNext();) {
			array.append("'");
			array.append(iterator.next());
			array.append("'");
			
			if(iterator.hasNext())
				array.append(",");
		}
		
		array.append("]");
		
		return array.toString();
	}

	@Override
	public void encodeChildren(FacesContext facesContext, UIComponent component) throws IOException {
		// Render children in encodeEnd
	}

	@Override
	public boolean getRendersChildren() {
		return true;
	}
}