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
package org.primefaces.ui.component.spinner;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;

import org.primefaces.ui.renderkit.CoreRenderer;
import org.primefaces.ui.util.ComponentUtils;
import org.primefaces.ui.util.HTML;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SpinnerRenderer extends CoreRenderer {

	private Logger logger = LoggerFactory.getLogger(SpinnerRenderer.class);
	
	@Override
	public void decode(FacesContext facesContext, UIComponent component) {
		Spinner spinner = (Spinner) component;
		String clientId = spinner.getClientId(facesContext);
		
		String submittedValue = (String) facesContext.getExternalContext().getRequestParameterMap().get(clientId);
		spinner.setSubmittedValue(submittedValue);
	}
	
	@Override
	public void encodeEnd(FacesContext facesContext, UIComponent component) throws IOException {
		Spinner spinner = (Spinner) component;
		
		encodeScript(facesContext, spinner);
		encodeMarkup(facesContext, spinner);
	}
	
	private void encodeScript(FacesContext facesContext, Spinner spinner) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		String clientId = spinner.getClientId(facesContext);
		int fractionDigits = calculateFractionDigits(spinner);
		
		writer.startElement("script", null);
		writer.writeAttribute("type", "text/javascript", null);
		
		writer.write("YAHOO.util.Event.onContentReady('" + clientId + "', function() {\n");
		
		writer.write("jQuery(PrimeFaces.core.Utils.escapeClientId('" + clientId + "')).spinner({");
		writer.write("stepping:" + spinner.getStepFactor());
		if(fractionDigits != -1) {
			writer.write(",decimals:" + fractionDigits);
		}
		writer.write("});\n");
	
		writer.write("});");
		
		writer.endElement("script");
	}
	
	private int calculateFractionDigits(Spinner spinner) {
		double stepping = spinner.getStepFactor();
		if(stepping != 1) {
			String[] steppingFormat = String.valueOf(stepping).split("\\.");
			
			return steppingFormat[1].length();
		}
		
		return -1;
	}
	
	private void encodeMarkup(FacesContext facesContext, Spinner spinner) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		String clientId = spinner.getClientId(facesContext);
		
		writer.startElement("input", null);
		writer.writeAttribute("id", clientId, null);
		writer.writeAttribute("name", clientId, null);
		writer.writeAttribute("type", "text", null);
		
		String valueToRender = ComponentUtils.getStringValueToRender(facesContext, spinner);
		if(valueToRender != null) {
			writer.writeAttribute("value", valueToRender , null);
		}
		
		renderPassThruAttributes(facesContext, spinner, HTML.INPUT_TEXT_ATTRS);
		
		writer.endElement("input");
	}

	@Override
	public Object getConvertedValue(FacesContext facesContext, UIComponent component, Object submittedValue) throws ConverterException {
		Spinner spinner = (Spinner) component;
		String value = (String) submittedValue;
		Converter converter = spinner.getConverter();
		
		//first ask the converter
		if(converter != null) {
			return converter.getAsObject(facesContext, spinner, value);
		}
		//Try to guess
		else {
			Class<?> valueType = spinner.getValueExpression("value").getType(facesContext.getELContext());
			Converter converterForType = facesContext.getApplication().createConverter(valueType);
			
			if(converterForType != null) {
				return converterForType.getAsObject(facesContext, spinner, value);
			}
		}
		
		logger.debug("No converter found for spinner:'{}'", spinner.getClientId(facesContext));
		
		return value;
	}
}