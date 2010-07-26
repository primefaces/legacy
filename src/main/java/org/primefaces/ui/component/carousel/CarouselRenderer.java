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
package org.primefaces.ui.component.carousel;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.primefaces.ui.renderkit.CoreRenderer;

public class CarouselRenderer extends CoreRenderer{

	public void encodeEnd(FacesContext facesContext, UIComponent component) throws IOException {
		Carousel carousel = (Carousel) component;
		
		encodeCarouselWidget(facesContext, carousel);
		encodeCarouselMarkup(facesContext, carousel);
	}
	
	private void encodeCarouselWidget(FacesContext facesContext, Carousel carousel) throws IOException{
		ResponseWriter writer = facesContext.getResponseWriter();
		String clientId = carousel.getClientId(facesContext);
		String carouselVar = getCarouselVar(carousel);
		
		writer.startElement("script", null);
		writer.writeAttribute("type", "text/javascript", null);
		
		writer.write("YAHOO.util.Event.onContentReady(\"" + clientId + "\", function() {\n");
		writer.write(carouselVar + " = new YAHOO.widget.Carousel(\"" + clientId + "\");\n");
		
		if(carousel.isCircular()) writer.write(carouselVar + ".set(\"isCircular\", true);\n");
		if(carousel.isVertical()) writer.write(carouselVar + ".set(\"isVertical\", true);\n");
		if(carousel.getNumVisible() != 3) writer.write(carouselVar + ".set(\"numVisible\"," + carousel.getNumVisible() + ");\n");
		if(carousel.getFirstVisible() != 0) writer.write(carouselVar + ".set(\"firstVisible\"," + carousel.getFirstVisible() + ");\n");
		if(carousel.getSelectedItem() != 0) writer.write(carouselVar + ".set(\"selectedItem\"," + carousel.getSelectedItem() + ");\n");
		if(carousel.getAutoplay() != 0) writer.write(carouselVar + ".set(\"autoPlay\"," + carousel.getAutoplay() + ");\n");
		if(carousel.getScrollIncrement() != 1) writer.write(carouselVar + ".set(\"scrollIncrement\"," + carousel.getScrollIncrement()+ ");\n");
		if(carousel.getRevealAmount() != 0) writer.write(carouselVar + ".set(\"revealAmount\"," + carousel.getRevealAmount() + ");\n");
		if(carousel.isAnimate()) {
			writer.write(carouselVar + ".set(\"animation\", {speed:" + carousel.getSpeed());
			if(carousel.getEffect() != null)
				writer.write(",effect:YAHOO.util.Easing." + carousel.getEffect() + "});\n");
			else
				writer.write("});\n");
		}
		
		writer.write(carouselVar + ".render();\n");
		writer.write(carouselVar + ".show();\n");
		writer.write("});\n");
		
		writer.endElement("script");
	}

	private void encodeCarouselMarkup(FacesContext facesContext, Carousel carousel) throws IOException{
		ResponseWriter writer = facesContext.getResponseWriter();
		String clientId = carousel.getClientId(facesContext);
		Collection value = (Collection) carousel.getValue();
		
		writer.startElement("div", null);
		writer.writeAttribute("id", clientId , null);
		
		writer.startElement("ol", null);
		
		for (Iterator dataIter = value.iterator(); dataIter.hasNext();) {
			Object currentData = (Object) dataIter.next();
			facesContext.getExternalContext().getRequestMap().put(carousel.getVar(), currentData);
			
			writer.startElement("li", null);
			renderChildren(facesContext, carousel);
			writer.endElement("li");
		}
		
		facesContext.getExternalContext().getRequestMap().remove(carousel.getVar());
		
		writer.endElement("ol");
		writer.endElement("div");
	}

	public void encodeChildren(FacesContext facesContext, UIComponent component) throws IOException {
		//Do nothing
	}

	public boolean getRendersChildren() {
		return true;
	}
	
	private String getCarouselVar(Carousel carousel) {
		if(carousel.getWidgetVar() != null)
			return carousel.getWidgetVar();
		else
			return "pf_carousel_" + carousel.getId();
	}
}
