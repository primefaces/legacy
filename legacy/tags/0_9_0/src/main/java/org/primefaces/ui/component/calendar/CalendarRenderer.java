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
package org.primefaces.ui.component.calendar;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.convert.ConverterException;

import org.primefaces.ui.renderkit.CoreRenderer;
import org.primefaces.ui.resource.ResourceUtils;

public class CalendarRenderer extends CoreRenderer{
	
	private static String DEFAULT_POPUP_ICON = "/primefaces/calendar/calendar_icon.png";
	
	public void decode(FacesContext facesContext, UIComponent component) {
		Calendar calendar = (Calendar) component;
		String clientId = calendar.getClientId(facesContext);
		
		String submittedValue = facesContext.getExternalContext().getRequestParameterMap().get(clientId + ":input");
		
		calendar.setSubmittedValue(submittedValue);
	}

	public void encodeEnd(FacesContext facesContext, UIComponent component) throws IOException {
		Calendar calendar = (Calendar) component;
		
		encodeCalendarScript(facesContext, calendar);
		encodeCalendarMarkup(facesContext, calendar);
	}
	
	private void encodeCalendarScript(FacesContext facesContext, Calendar calendar) throws IOException{
		ResponseWriter writer = facesContext.getResponseWriter();
		String clientId = calendar.getClientId(facesContext);
		String calendarVar = createUniqueWidgetVar(facesContext, calendar);
		
		writer.startElement("script", null);
		writer.writeAttribute("type", "text/javascript", null);
		
		writer.write("YAHOO.util.Event.onContentReady(\"" + clientId + "\", function() {\n");
		
		if(isPopup(calendar)) {
			writer.write(calendarVar + "_popupOverlay = new YAHOO.widget.Overlay(\"" + clientId + ":popupContainer\", { visible: false });\n");
			writer.write(calendarVar + "_popupOverlay.render();\n");
		}

		if(calendar.getPages() > 1)
			writer.write(calendarVar + " = new YAHOO.widget.CalendarGroup(\"" + calendar.getId() + "\",\"" + clientId + ":container\", {PAGES:" + calendar.getPages() + "});\n");
		else
			writer.write(calendarVar + " = new YAHOO.widget.Calendar(\"" + calendar.getId() + "\",\"" + clientId + ":container\");\n");
		
		String selectionEvent =".subscribe(PrimeFaces.widget.CalendarUtils.selectEvent, {calendar:" + calendarVar + ",inputId:\"" + clientId + ":input\"";
		if(calendar.getSelection().equals("multiple"))
			selectionEvent += ", selection:\"multiple\"";
		else
			selectionEvent += ", selection:\"single\"";
	
		String delimiter = getPatternDelimeter(calendar);
		selectionEvent +=", delimiter:\"" + delimiter + "\"";
		selectionEvent +=", dayFieldIndex:" + getDateFieldPosition(calendar, delimiter, "d");
		selectionEvent +=", monthFieldIndex:" + getDateFieldPosition(calendar, delimiter, "M");
		selectionEvent +=", yearFieldIndex:" + getDateFieldPosition(calendar, delimiter, "y");
		
		if(isPopup(calendar))
			selectionEvent +=", popupOverlay:" + calendarVar + "_popupOverlay}, true);\n";
		else
			selectionEvent +="}, true);\n";

		writer.write(calendarVar + ".selectEvent" + selectionEvent);
		writer.write(calendarVar + ".deselectEvent" + selectionEvent);

		writer.write("PrimeFaces.widget.CalendarUtils.applyLocale(" + calendarVar + ",\"" + calendar.getLanguage() + "\");\n");
		
		if(calendar.getValue() != null) {
			if(calendar.getSelection().equalsIgnoreCase("single")) {
				Date value = (Date) calendar.getValue();
				String selectedDate = new SimpleDateFormat("MM/dd/yyyy").format(value);
				writer.write(calendarVar + ".cfg.setProperty(\"selected" + "\",\"" + selectedDate + "\");\n");
				writer.write(calendarVar + ".cfg.setProperty(\"pagedate\",\"" + getPageDate(selectedDate, false) + "\");\n");
			} else if(calendar.getSelection().equalsIgnoreCase("multiple")) {
				String selectedDates = getMultipleSelectionValues(calendar);
				writer.write(calendarVar + ".cfg.setProperty(\"selected" + "\",\"" + selectedDates + "\");\n");
				writer.write(calendarVar + ".cfg.setProperty(\"pagedate\",\"" + getPageDate(selectedDates, true) + "\");\n");
			}
		}
		
		if(calendar.isNavigator()) writer.write(calendarVar + ".cfg.setProperty(\"navigator\",true);\n");
		if(calendar.getSelection().equals("multiple")) writer.write(calendarVar + ".cfg.setProperty(\"multi_select\",true);\n");
		if(calendar.isClose()) writer.write(calendarVar + ".cfg.setProperty(\"close\", true);\n");
		if(calendar.getTitle() != null) writer.write(calendarVar + ".cfg.setProperty(\"title\",\"" + calendar.getTitle() + "\");\n");
		if(!calendar.isShowWeekdays()) writer.write(calendarVar + ".cfg.setProperty(\"show_weekdays\", false);\n");
		if(calendar.getMonthFormat() != null) writer.write(calendarVar + ".cfg.setProperty(\"locale_months\", \"" + calendar.getMonthFormat() + "\");\n");
		if(calendar.getWeekdayFormat() != null) writer.write(calendarVar + ".cfg.setProperty(\"locale_weekdays\", \"" + calendar.getWeekdayFormat() + "\");\n");
		if(calendar.getStartWeekday() != 0) writer.write(calendarVar + ".cfg.setProperty(\"start_weekday\", " + calendar.getStartWeekday() + ");\n");
		
		if(!isPopup(calendar))
			writer.write(calendarVar + ".render();\n");
		else
			writer.write("YAHOO.util.Event.addListener(\"" + clientId + ":popupButtonImage\", \"click\", function(e){" + calendarVar + ".render();" 
					+ calendarVar + ".show();" + calendarVar +"_popupOverlay.show();});");
		
		writer.write("});\n");
		
		writer.endElement("script");
	}

	private void encodeCalendarMarkup(FacesContext facesContext, Calendar calendar) throws IOException{
		ResponseWriter writer = facesContext.getResponseWriter();
		String clientId = calendar.getClientId(facesContext);
		
		writer.startElement("div", calendar);
		writer.writeAttribute("id", clientId, null);
		
		encodeInputField(facesContext, calendar, clientId);
		
		if(isPopup(calendar)) {
			encodePopupButtonMarkup(facesContext,clientId);
			encodePopupContainer(facesContext, clientId);
		}
		else {
			encodeCalendarContainer(facesContext, clientId);
		}
		
		writer.endElement("div");
	}
	
	private void encodePopupContainer(FacesContext facesContext, String clientId) throws IOException{
		ResponseWriter writer = facesContext.getResponseWriter();
		
		writer.startElement("div", null);
		writer.writeAttribute("id", clientId + ":popupContainer", null);
		
		encodeCalendarContainer(facesContext, clientId);
		
		writer.endElement("div");
	}
	
	private void encodeCalendarContainer(FacesContext facesContext, String clientId) throws IOException{
		ResponseWriter writer = facesContext.getResponseWriter();
		
		writer.startElement("div", null);
		writer.writeAttribute("id", clientId +":container" , null);
		writer.endElement("div");
	}
	
	private void encodeInputField(FacesContext facesContext, Calendar calendar, String clientId) throws IOException{
		ResponseWriter writer = facesContext.getResponseWriter();
		
		writer.startElement("input", null);
		writer.writeAttribute("id", clientId + ":input", null);
		writer.writeAttribute("name", clientId + ":input", null);
		
		if(isPopup(calendar))
			writer.writeAttribute("type", "text", null);
		else
			writer.writeAttribute("type", "hidden", null);
			
		if(calendar.getValue() != null) {
			if(calendar.getSelection().equalsIgnoreCase("single")) {
				Date value = (Date) calendar.getValue();
				SimpleDateFormat format = new SimpleDateFormat(calendar.getPattern());
				writer.writeAttribute("value", format.format(value), null);
				
			}else if(calendar.getSelection().equalsIgnoreCase("multiple")) {
				writer.writeAttribute("value", getMultipleSelectionValues(calendar), null);
			}
		}
			
		writer.endElement("input");
	}

	private void encodePopupButtonMarkup(FacesContext facesContext, String clientId) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		
		writer.startElement("img", null);
		writer.writeAttribute("id", clientId + ":popupButtonImage", null);
		writer.writeAttribute("name", clientId + ":popupButtonImage", null);
		writer.writeAttribute("src", ResourceUtils.getResourceURL(facesContext, DEFAULT_POPUP_ICON), null);
		writer.writeAttribute("style", "cursor:pointer;margin:0;padding:0;", null);
		
		writer.endElement("img");
	}

	public Object getConvertedValue(FacesContext context, UIComponent component, Object submittedValue) throws ConverterException {
		Calendar calendar = (Calendar) component;
		SimpleDateFormat format = new SimpleDateFormat(calendar.getPattern());
		
		try {
			if(calendar.getSelection().equalsIgnoreCase("single")) {
				Date convertedValue;
				
				convertedValue = format.parse((String)submittedValue);
				calendar.setSubmittedValue(convertedValue);
				
				return convertedValue;
			}
			else if(calendar.getSelection().equalsIgnoreCase("multiple")) {
				String[] datesAsString = ((String) submittedValue).split(",");
				
				Date[] dates = new Date[datesAsString.length];			
				
				for (int i = 0; i < datesAsString.length; i++) {
					dates[i] = format.parse(datesAsString[i]);
				}
				
				return dates;
			} else
				throw new IllegalArgumentException("Selection mode: " + calendar.getSelection() + " is not valid, use either 'single' or 'multiple'");
		
		} catch (ParseException e) {
			throw new ConverterException(e);
		}
		
	}
	
	protected boolean isPopup(Calendar calendar) {
		return calendar.getMode().equalsIgnoreCase("popup");
	}
	
	protected String getMultipleSelectionValues(Calendar calendar) {
		if(calendar.getValue() == null)
			return null;
		
		Date[] dates = (Date[]) calendar.getValue();
		SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");
		String datesAsString = "";
		
		for (int i = 0; i < dates.length; i++) {
			if(i == 0)
				datesAsString = format.format(dates[i]);
			else
				datesAsString = datesAsString + "," + format.format(dates[i]);
		}
		
		return datesAsString;
	}
	
	protected String getPageDate(String date, boolean multiple) {
		String selectedDate;
		
		if(multiple)
			selectedDate = date.split(",")[0];
		else 
			selectedDate = date;
			
		String[] tokens = selectedDate.split("/");	
		return tokens[0] + "/" + tokens[2];
	}
	
	protected String getPatternDelimeter(Calendar calendar) {
		String pattern = calendar.getPattern();
		
		return pattern.split("[A-Za-z]+")[1];
	}
	
	protected int getDateFieldPosition(Calendar calendar, String delimiter, String fieldPrefix) {
		String pattern = calendar.getPattern();
		
		//Special character
		if(delimiter.equals("."))
			delimiter = "\\.";
		
		String[] dateFields = pattern.split(delimiter);
			
		for (int i = 0; i < dateFields.length; i++) {
			if(dateFields[i].startsWith(fieldPrefix))
				return i + 1;							//widget uses 1 instead of 0 as first position
		}
			
		return -1;
	}
}
