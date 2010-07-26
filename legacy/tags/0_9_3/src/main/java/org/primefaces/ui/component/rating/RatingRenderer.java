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
package org.primefaces.ui.component.rating;

import java.io.IOException;
import java.util.Map;

import javax.faces.FacesException;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.convert.ConverterException;

import org.primefaces.ui.event.rating.RateEvent;
import org.primefaces.ui.renderkit.CoreRenderer;
import org.primefaces.ui.util.ComponentUtils;

public class RatingRenderer extends CoreRenderer {
	
	@Override
	public void decode(FacesContext facesContext, UIComponent component) {
		Rating rating = (Rating) component;
		String clientId = rating.getClientId(facesContext);
		Map<String,String> params = facesContext.getExternalContext().getRequestParameterMap(); 
		
		boolean isRateEvent = params.containsKey(clientId);
		
		if(isRateEvent) {
			String rateValue = params.get("rating");
			
			if(rateValue.equals(""))
				rating.queueEvent(new RateEvent(rating, null));
			else
				rating.queueEvent(new RateEvent(rating, Double.valueOf(rateValue)));
		}
		else {
			String regularValue = (String) params.get(clientId + "_input");
			rating.setSubmittedValue(regularValue);
		}
	}

	@Override
	public void encodeEnd(FacesContext facesContext, UIComponent component) throws IOException {
		Rating rating = (Rating) component;
		
		encodeScript(facesContext, rating);
		encodeMarkup(facesContext, rating);
	}
	
	private void encodeScript(FacesContext facesContext, Rating rating) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		String clientId = rating.getClientId(facesContext);
		String ratingVar = createUniqueWidgetVar(facesContext, rating);
		String formClientId = null;
		
		UIComponent form = ComponentUtils.findParentForm(facesContext, rating);
		
		if(form != null)
			formClientId = form.getClientId(facesContext);
		else
			throw new FacesException("Rating:" + clientId + " needs to be enclosed in a form when using an rateListener");
		
		writer.startElement("script", null);
		writer.writeAttribute("type", "text/javascript", null);
		
		writer.write("PrimeFaces.core.Utils.onContentReady('" + clientId + "', function() {\n");
		
		writer.write(ratingVar + " = new PrimeFaces.widget.Rating('" + clientId +"'");
		writer.write(",{");
		if(rating.getRateListener() != null) {
			writer.write("hasRateListener:true");
			writer.write(",formClientId:'" + formClientId + "'");
			writer.write(",actionURL:'" + getActionURL(facesContext) + "'");
			if(rating.getUpdate() != null)
				writer.write(",update:'" + rating.getUpdate()+ "'");
		}
		writer.write("});\n");
		
		writer.write("});");
		
		writer.endElement("script");
	}
	
	private void encodeMarkup(FacesContext facesContext, Rating rating) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		String clientId = rating.getClientId(facesContext);
		Object value = rating.getValue();
		
		if(value != null && !(value instanceof Double)) {
			throw new FacesException("Rating component('" + clientId + "')'s value must be of type java.lang.Double");
		}
		
		Double ratingValue = (Double) value;
		writer.startElement("span", rating);
		writer.writeAttribute("id", clientId, "id");
		
		for(int i = 1; i <= rating.getStars(); i++) {
			writer.startElement("input", null);
			writer.writeAttribute("name", clientId + "_input", null);
			writer.writeAttribute("type", "radio", null);
			writer.writeAttribute("value", i, null);
			writer.writeAttribute("class", "pf-rating-star", null);

			if(ratingValue != null && ratingValue.intValue() == i)
				writer.writeAttribute("checked", "checked", null);
			
			if(rating.isDisabled())
				writer.writeAttribute("disabled", "disabled", null);
			
			writer.endElement("input");
		}
		
		writer.endElement("span");
	}

	@Override
	public Object getConvertedValue(FacesContext facesContext, UIComponent component, Object submittedValue) throws ConverterException {
		String value = (String) submittedValue;
	
		try {
			return Double.valueOf(value);
		}catch(NumberFormatException exception) {
			FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Conversion error", submittedValue + " is not a valid value for " + component.getClientId(facesContext));
			
			throw new ConverterException(msg);
		}
	}
}