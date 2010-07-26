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
	
	public final static String DEFAULT_H_THUMB = "thumb-n.gif";
	public final static String DEFAULT_V_THUMB = "thumb-e.gif";

	public void encodeEnd(FacesContext facesContext, UIComponent component) throws IOException {
		Slider slider = (Slider) component;
		
		encodeSliderWidget(facesContext, slider);
		encodeSliderMarkup(facesContext, slider);
	}
	
	private void encodeSliderMarkup(FacesContext facesContext, Slider slider) throws IOException{
		ResponseWriter writer = facesContext.getResponseWriter();
		String clientId = slider.getClientId(facesContext);
		
		String backgroundClass = slider.getType().equalsIgnoreCase("horizontal") ? "yui-h-slider" : "yui-v-slider";
		
		writer.startElement("div", slider);
		writer.writeAttribute("id", clientId , "id");
		writer.writeAttribute("class", backgroundClass, null);
		
		encodeThumbDiv(facesContext, slider);
		
		writer.endElement("div");
	}
	
	private void encodeSliderWidget(FacesContext facesContext, Slider slider) throws IOException{
		ResponseWriter writer = facesContext.getResponseWriter();
		String clientId = slider.getClientId(facesContext);
		String sliderVar = getSliderVar(slider);
		
		writer.startElement("script", slider);
		writer.writeAttribute("type", "text/javascript", null);
		
		writer.write("YAHOO.util.Event.onContentReady(\"" + clientId + "\", function() {\n");
		writer.write(sliderVar + " = " + createSliderConstructor(facesContext, slider));
		
		if(!slider.isAnimate())
			writer.write(sliderVar + ".animate = false;");
		
		UIComponent forComponent = ComponentUtils.findComponentById(facesContext, facesContext.getViewRoot(), slider.getFor());
		
		writer.write(sliderVar + ".setValue(" + ComponentUtils.getStringValueToRender(facesContext, forComponent) + ",true,true);\n");
		writer.write(sliderVar + ".subscribe(\"change\", function(offset) {\n");
		writer.write("\tif(offset != 0)");
		writer.write("\t\tdocument.getElementById('" + forComponent.getClientId(facesContext) + "').value = " + sliderVar + ".getValue();\n");
		writer.write("});\n");
		
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
			String defaultThumbImage = slider.getType().equals("horizontal") ? DEFAULT_H_THUMB : DEFAULT_V_THUMB;
			writer.writeAttribute("src", ResourceUtils.getResourceURL(facesContext, "/yui/slider/assets/" + defaultThumbImage), null);
		} else {
			writer.writeAttribute("src", slider.getThumbImage(), null);
		}
		
		writer.endElement("img");
		
		writer.endElement("div");
	}
	
	private String getSliderVar(Slider slider) {
		if(slider.getWidgetVar() != null)
			return slider.getWidgetVar();
		else
			return "pf_slider_" + slider.getId();
	}
	
	private String createSliderConstructor(FacesContext facesContext, Slider slider) {
		String clientId = slider.getClientId(facesContext);
		String type = slider.getType();
		StringBuffer buffer = new StringBuffer();
		String optionalAttributes = null;
		
		if(type.equals("horizontal")) {
			buffer.append("YAHOO.widget.Slider.getHorizSlider");
			optionalAttributes = slider.getLeft() + ", " + slider.getRight();
		}
		else if(type.equals("vertical")) {
			buffer.append("YAHOO.widget.Slider.getVertSlider");
			optionalAttributes = slider.getUp() + ", " + slider.getDown();
		}
		else {
			throw new IllegalArgumentException("Slider component with id:" + slider.getId() + " has an invalid type:" + type);
		}
			
		buffer.append("(\"" + clientId + "\", ");
		buffer.append("\"" + clientId + ":thumb\", ");
		buffer.append(optionalAttributes);
		buffer.append(", " + slider.getTickMarks() + ");\n");
		
		return buffer.toString();
	}
}
