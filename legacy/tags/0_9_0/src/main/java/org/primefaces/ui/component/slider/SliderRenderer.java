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
package org.primefaces.ui.component.slider;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.primefaces.ui.renderkit.CoreRenderer;
import org.primefaces.ui.resource.ResourceUtils;
import org.primefaces.ui.util.ComponentUtils;

public class SliderRenderer extends CoreRenderer{
	
	private final static String DEFAULT_H_THUMB = "thumb-n.gif";
	private final static String DEFAULT_V_THUMB = "thumb-e.gif";
	private final static String DEFAULT_V_BACKGROUND_CLASS = "yui-v-slider";
	private final static String DEFAULT_H_BACKGROUND_CLASS = "yui-h-slider";
	
	public void encodeEnd(FacesContext facesContext, UIComponent component) throws IOException {
		Slider slider = (Slider) component;
		
		encodeSliderWidget(facesContext, slider);
		encodeSliderMarkup(facesContext, slider);
	}
	
	private void encodeSliderMarkup(FacesContext facesContext, Slider slider) throws IOException{
		ResponseWriter writer = facesContext.getResponseWriter();
		String clientId = slider.getClientId(facesContext);
		
		String backgroundClass = isHorizontal(slider) ? DEFAULT_H_BACKGROUND_CLASS : DEFAULT_V_BACKGROUND_CLASS;
		
		writer.startElement("div", slider);
		writer.writeAttribute("id", clientId , "id");
		writer.writeAttribute("class", backgroundClass, null);
		
		encodeThumbDiv(facesContext, slider);

		writer.endElement("div");
	}

	private void encodeSliderWidget(FacesContext facesContext, Slider slider) throws IOException{
		ResponseWriter writer = facesContext.getResponseWriter();
		String clientId = slider.getClientId(facesContext);
		String sliderVar = createUniqueWidgetVar(facesContext, slider);
		
		UIComponent forComponent = ComponentUtils.findComponentById(facesContext, facesContext.getViewRoot(), slider.getFor());
		String forComponentClientId = forComponent.getClientId(facesContext);
		String valueToRender = ComponentUtils.getStringValueToRender(facesContext, forComponent);
		int bgWidth = slider.getMaxValue() - slider.getMinValue();
		int minMaxDiff = slider.getMaxValue() - slider.getMinValue();
		
		writer.startElement("script", slider);
		writer.writeAttribute("type", "text/javascript", null);
		
		writer.write("YAHOO.util.Event.onContentReady(\"" + clientId + "\", function() {\n");
		writer.write(sliderVar + " = " + createSliderConstructor(facesContext, slider));
		
		if(!slider.isAnimate())
			writer.write(sliderVar + ".animate = false;");
		
		writer.write(sliderVar + ".setValue((" + valueToRender + " - (" + slider.getMinValue() +")) / ( " + minMaxDiff + " / "+ bgWidth +" ) ,true,true);\n");
		
		writer.write(sliderVar + ".subscribe(\"change\", PrimeFaces.widget.SliderUtils.handleSlide, {minValue:" + slider.getMinValue() + ", elId: '" + forComponentClientId + 
																								"', minMaxDiff:" + minMaxDiff + ", bgWidth:" + bgWidth + "});\n");
		
		writer.write("});\n");
		
		writer.endElement("script");
	}

	private void encodeThumbDiv(FacesContext facesContext, Slider slider) throws IOException{
		ResponseWriter writer = facesContext.getResponseWriter();
		
		writer.startElement("div", slider);
		writer.writeAttribute("id", slider.getClientId(facesContext) + ":thumb", "id");
		writer.writeAttribute("class", "yui-slider-thumb", null);
		
		writer.startElement("img", slider);

		if(slider.getThumbImage() == null) {
			String defaultThumbImage = isHorizontal(slider) ? DEFAULT_H_THUMB : DEFAULT_V_THUMB;
			writer.writeAttribute("src", ResourceUtils.getResourceURL(facesContext, "/yui/slider/assets/" + defaultThumbImage), null);
		} else {
			writer.writeAttribute("src", slider.getThumbImage(), null);
		}
		
		writer.endElement("img");
		
		writer.endElement("div");
	}
	
	private boolean isHorizontal(Slider slider) {
		String type = slider.getType();
		if(!(type.equals("horizontal") || type.equals("vertical")))
			throw new IllegalArgumentException(type + " is not a valid type for slider component: " + slider.getClientId(FacesContext.getCurrentInstance()));
			
		return slider.getType().equals("horizontal");
	}
	
	private String createSliderConstructor(FacesContext facesContext, Slider slider) {
		String clientId = slider.getClientId(facesContext);
		String type = slider.getType();
		StringBuffer buffer = new StringBuffer();
		int bgWidth = slider.getMaxValue() - slider.getMinValue();
		
		if(type.equals("horizontal")) {
			buffer.append("YAHOO.widget.Slider.getHorizSlider");
		}
		else if(type.equals("vertical")) {
			buffer.append("YAHOO.widget.Slider.getVertSlider");
		}
		else {
			throw new IllegalArgumentException("Slider component with id:" + slider.getId() + " has an invalid type:" + type);
		}
			
		buffer.append("(\"" + clientId + "\", ");
		buffer.append("\"" + clientId + ":thumb\", ");
		buffer.append("0," + bgWidth);
		buffer.append(", " + slider.getTickMarks() + ");\n");
		
		return buffer.toString();
	}
}
