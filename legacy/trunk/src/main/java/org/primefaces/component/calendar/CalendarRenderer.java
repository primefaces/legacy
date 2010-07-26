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
package org.primefaces.component.calendar;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.convert.ConverterException;

import org.primefaces.event.DateSelectEvent;
import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.resource.ResourceUtils;
import org.primefaces.util.ComponentUtils;

public class CalendarRenderer extends CoreRenderer{
	
	public void decode(FacesContext facesContext, UIComponent component) {
		Calendar calendar = (Calendar) component;
		Map<String, String> params = facesContext.getExternalContext().getRequestParameterMap();
		String param = calendar.getClientId(facesContext) + "_input";
		
		if(params.containsKey(param)) {
			calendar.setSubmittedValue(params.get(param));
		}
	}

	public void encodeEnd(FacesContext facesContext, UIComponent component) throws IOException {
		Calendar calendar = (Calendar) component;
		String value = CalendarUtils.getValueAsString(facesContext, calendar);
		
		encodeMarkup(facesContext, calendar, value);
		encodeScript(facesContext, calendar, value);
	}
	
	protected void encodeMarkup(FacesContext facesContext, Calendar calendar, String value) throws IOException{
		ResponseWriter writer = facesContext.getResponseWriter();
		String clientId = calendar.getClientId(facesContext);
		String inputId = clientId + "_input";
		
		writer.startElement("span", calendar);
		writer.writeAttribute("id", clientId, null);
		if(calendar.getStyle() != null) writer.writeAttribute("style", calendar.getStyle(), null);
		if(calendar.getStyleClass() != null) writer.writeAttribute("class", calendar.getStyleClass(), null);
		
		//popup container
		if(!calendar.isPopup()) {
			writer.startElement("div", null);
			writer.writeAttribute("id", clientId + "_inline", null);
			writer.endElement("div");
		}
		
		//input
		String type = calendar.isPopup() ? "text" : "hidden";
		
		writer.startElement("input", null);
		writer.writeAttribute("id", inputId, null);
		writer.writeAttribute("name", inputId, null);
		writer.writeAttribute("type", type, null);
		
		if(value != null)
			writer.writeAttribute("value", value, null);
		
		if(calendar.isPopup()) {
			if(calendar.getInputStyle() != null) writer.writeAttribute("style", calendar.getInputStyle(), null);
			if(calendar.getInputStyleClass() != null) writer.writeAttribute("class", calendar.getInputStyleClass(), null);
			if(calendar.isReadOnlyInputText()) writer.writeAttribute("readonly", "readonly", null);
			if(calendar.isDisabled()) writer.writeAttribute("disabled", "disabled", null);
		}

		writer.endElement("input");
		
		writer.endElement("span");
	}
	
	protected void encodeScript(FacesContext facesContext, Calendar calendar, String value) throws IOException{
		ResponseWriter writer = facesContext.getResponseWriter();
		String clientId = calendar.getClientId(facesContext);
		String widgetVar = createUniqueWidgetVar(facesContext, calendar);
		
		writer.startElement("script", null);
		writer.writeAttribute("type", "text/javascript", null);
		
		writer.write("jQuery(function(){");
		
		writer.write(widgetVar + " = new PrimeFaces.widget.Calendar('" + clientId + "', {");
		
		writer.write("popup:" + calendar.isPopup());
		writer.write(",locale:'" + calendar.calculateLocale(facesContext).toString() + "'");
		
		if(value != null) writer.write(",defaultDate:'" + value + "'");
		if(calendar.getPattern() != null) writer.write(",dateFormat:'" + CalendarUtils.convertPattern(calendar.getPattern()) + "'");
		if(calendar.getPages() != 1) writer.write(",numberOfMonths:" + calendar.getPages());
		if(calendar.getMindate() != null) writer.write(",minDate:'" +CalendarUtils.getDateAsString(calendar, calendar.getMindate() + "'"));
		if(calendar.getMaxdate() != null) writer.write(",maxDate:'" +CalendarUtils.getDateAsString(calendar, calendar.getMaxdate() + "'"));
		if(calendar.isShowButtonPanel()) writer.write(",showButtonPanel:true");
		if(calendar.isShowWeek()) writer.write(",showWeek:true");
		if(calendar.isDisabled()) writer.write(",disabled:true");
		
		if(calendar.isNavigator()) {
			writer.write(",changeMonth:true");
			writer.write(",changeYear:true");
		}
		
		if(calendar.getEffect() != null) {
			writer.write(",showAnim:'" + calendar.getEffect() + "'");
			writer.write(",duration:'" + calendar.getEffectDuration() + "'");
		}
		
		String showOn = calendar.getShowOn();
		if(showOn != null) {
			writer.write(",showOn:'" + showOn + "'");
			
			if(showOn.equalsIgnoreCase("button")) {
				String iconSrc = calendar.getPopupIcon() != null ? getResourceURL(facesContext, calendar.getPopupIcon()) : ResourceUtils.getResourceURL(facesContext, Calendar.POPUP_ICON);
				writer.write(",buttonImage:'" + iconSrc + "'");
				writer.write(",buttonImageOnly:" + calendar.isPopupIconOnly());
			}
		}
		
		if(calendar.isShowOtherMonths()) {
			writer.write(",showOtherMonths:true");
			writer.write(",selectOtherMonths:" + calendar.isSelectOtherMonths());
		}
		
		if(calendar.getSelectListener() != null) {
			UIComponent form = ComponentUtils.findParentForm(facesContext, calendar);
			if(form == null)
				throw new FacesException("Calendar \"" + calendar.getClientId(facesContext) + "\" must be enclosed with a form when using ajax selection.");
			
			writer.write(",formId:'" + form.getClientId(facesContext) + "'");
			writer.write(",url:'" + getActionURL(facesContext) + "'");
			writer.write(",hasSelectListener:true");
			
			if(calendar.getOnSelectUpdate() != null)
				writer.write(",onSelectUpdate:'" + ComponentUtils.findClientIds(facesContext, calendar, calendar.getOnSelectUpdate()) + "'");	
		}
		
		writer.write("});});");
		
		writer.endElement("script");
	}
	
	public Object getConvertedValue(FacesContext facesContext, UIComponent component, Object value) throws ConverterException {
		Calendar calendar = (Calendar) component;
		String submittedValue = (String) value;
		
		if(isValueBlank(submittedValue))
			return null;
		
		//Delegate to user supplied converter if defined
		if(calendar.getConverter() != null) {
			return calendar.getConverter().getAsObject(facesContext, calendar, submittedValue);
		}

		try {
			Date convertedValue;
			Locale locale = calendar.calculateLocale(facesContext);
			SimpleDateFormat format = new SimpleDateFormat(calendar.getPattern(), locale);
			format.setTimeZone(calendar.calculateTimeZone());
			
			convertedValue = format.parse(submittedValue);
			
			calendar.queueEvent(new DateSelectEvent(calendar, convertedValue));		//Queue a date select event for any listeners
			
			return convertedValue;

		} catch (ParseException e) {
			throw new ConverterException(e);
		}
	}
}