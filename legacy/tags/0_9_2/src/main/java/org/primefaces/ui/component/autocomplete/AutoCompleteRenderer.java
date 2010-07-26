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
package org.primefaces.ui.component.autocomplete;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import javax.el.MethodExpression;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.primefaces.ui.renderkit.CoreRenderer;
import org.primefaces.ui.renderkit.PartialRenderer;
import org.primefaces.ui.util.ComponentUtils;

public class AutoCompleteRenderer extends CoreRenderer implements PartialRenderer{

	public void decode(FacesContext facesContext, UIComponent component) {
		AutoComplete autoComplete = (AutoComplete) component;
		String paramKey = autoComplete.getClientId(facesContext) + ":input";
		
		String submittedValue = (String) facesContext.getExternalContext().getRequestParameterMap().get(paramKey);
		autoComplete.setSubmittedValue(submittedValue);
	}

	public void encodeEnd(FacesContext facesContext, UIComponent component) throws IOException {
		AutoComplete autoComplete = (AutoComplete) component;
		
		encodeAutoCompleteScript(facesContext, autoComplete);
		encodeAutoCompleteMarkup(facesContext, autoComplete);
	}

	@SuppressWarnings("unchecked")
	public void encodePartially(FacesContext facesContext, UIComponent component) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		AutoComplete autoComplete = (AutoComplete) component;
		
		MethodExpression me = autoComplete.getCompleteMethod();
		String query = facesContext.getExternalContext().getRequestParameterMap().get("autoCompleteQuery");
		
		List<String> results = (List<String>) me.invoke(facesContext.getELContext(), new Object[]{query});
		writer.write("{");
		writer.write("\"AutoCompleteResponse\" : {");
		writer.write("\"Results\" : [");
		
		for (Iterator<String> iterator = results.iterator(); iterator.hasNext();) {
			String result = iterator.next();
			writer.write("{\"value\":\"" + result + "\"}");
			
			if(iterator.hasNext())
				writer.write(",");
		}
		writer.write("]");
		writer.write("}");
		writer.write("}");
	}
	
	protected void encodeAutoCompleteMarkup(FacesContext facesContext, AutoComplete autoComplete) throws IOException{
		ResponseWriter writer = facesContext.getResponseWriter();
		String clientId = autoComplete.getClientId(facesContext);
		
		writer.startElement("div", null);
		writer.writeAttribute("id", clientId, null);
		
		writer.startElement("input", null);
		writer.writeAttribute("id", clientId + ":input", null);
		writer.writeAttribute("name", clientId + ":input", null);
		writer.writeAttribute("type", "text", null);
		
		String valueToRender = ComponentUtils.getStringValueToRender(facesContext, autoComplete);
		if(valueToRender != null)
			writer.writeAttribute("value", valueToRender , null);
		
		writer.endElement("input");
		
		writer.startElement("div", null);
		writer.writeAttribute("id", clientId + ":container", null);
		writer.endElement("div");
		
		writer.endElement("div");
	}
	
	protected void encodeAutoCompleteScript(FacesContext facesContext, AutoComplete autoComplete) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		String clientId = autoComplete.getClientId(facesContext);
		String autoCompleteVar = createUniqueWidgetVar(facesContext, autoComplete);
		String datasourceVar = autoCompleteVar + "_datasource";
		String actionURL = getActionURL(facesContext);
		
		if(actionURL.indexOf("?") == -1)
			actionURL = actionURL + "?ajaxSource=" + clientId;
		else
			actionURL = actionURL + "&ajaxSource=" + clientId;

		writer.startElement("script", null);
		writer.writeAttribute("type", "text/javascript", null);
		
		writer.write("YAHOO.util.Event.onContentReady(\"" + clientId + "\", function() {\n");
		
		writer.write("var " + datasourceVar + " = new YAHOO.util.XHRDataSource(\"" + actionURL + "\");\n");
		writer.write(datasourceVar + ".responseType = YAHOO.util.XHRDataSource.TYPE_JSON;\n");
		writer.write(datasourceVar + ".connMethodPost = true;\n");
		writer.write(datasourceVar + ".responseSchema = {");
		writer.write("resultsList : \"AutoCompleteResponse.Results\",");
		writer.write("fields:[{key: \"value\"}]");
		writer.write("};\n");
		writer.write(autoCompleteVar + " = new YAHOO.widget.AutoComplete(\"" + clientId + ":input\",\"" + clientId + ":container\"," + datasourceVar +");\n");
		writer.write(autoCompleteVar + ".queryQuestionMark = false;\n");
		writer.write(autoCompleteVar + ".generateRequest = PrimeFaces.widget.AutoCompleteUtils.generateRequest;\n");
		
		if(autoComplete.isAnimHoriz()) writer.write(autoCompleteVar + ".animHoriz = true;\n");
		if(!autoComplete.isAnimVert()) writer.write(autoCompleteVar + ".animVert = false;\n");
		if(autoComplete.getAnimSpeed() != 0.3) writer.write(autoCompleteVar + ".animSpeed = " + autoComplete.getAnimSpeed() + ";\n");
		if(autoComplete.getMaxResults() != 10) writer.write(autoCompleteVar + ".maxResultsDisplayed = " + autoComplete.getMaxResults() + ";\n");
		if(autoComplete.getMinQueryLength() != 1) writer.write(autoCompleteVar + ".minQueryLength = " + autoComplete.getMinQueryLength() + ";\n");
		if(autoComplete.getQueryDelay() != 0.2) writer.write(autoCompleteVar + ".queryDelay = " + autoComplete.getQueryDelay() + ";\n");
		if(!autoComplete.isAutoHighlight()) writer.write(autoCompleteVar + ".autoHighlight = false;\n");
		if(autoComplete.isUseShadow()) writer.write(autoCompleteVar + ".useShadow = true;\n");
		if(autoComplete.isTypeAhead()) writer.write(autoCompleteVar + ".typeAhead = true;\n");
		if(autoComplete.getTypeAheadDelay() != 0.5) writer.write(autoCompleteVar + ".typeAheadDelay = " + autoComplete.getTypeAheadDelay() + ";\n");
		
		writer.write("});");

		writer.endElement("script");
	}
}