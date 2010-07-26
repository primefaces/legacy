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
package org.primefaces.ui.component.chart.pie;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import javax.el.ValueExpression;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.primefaces.ui.renderkit.CoreRenderer;
import org.primefaces.ui.resource.ResourceUtils;

public class PieChartRenderer extends CoreRenderer{

	public void encodeEnd(FacesContext facesContext, UIComponent component) throws IOException{
		PieChart chart = (PieChart) component;
		
		encodeResources(facesContext);
		encodeChartWidget(facesContext, chart);
		encodeChartMarkup(facesContext, chart);
	}

	private void encodeChartWidget(FacesContext facesContext, PieChart chart) throws IOException{
		ResponseWriter writer = facesContext.getResponseWriter();
		String clientId = chart.getClientId(facesContext);
		
		String chartVariable = getChartVar(chart);
		String categoryFieldName = getFieldName(chart.getValueExpression("categoryField"));
		String dataFieldName = getFieldName(chart.getValueExpression("dataField"));
		
		writer.startElement("script", null);
		writer.writeAttribute("type", "text/javascript", null);
		
		writer.write("YAHOO.util.Event.onContentReady(\"" + clientId + "\", function() {\n");
		
		writer.write("var " + chartVariable + "_data = [" );
		
		List list = (List) chart.getValue();
		for (Iterator iterator = list.iterator(); iterator.hasNext();) {
			Object object = (Object) iterator.next();
			facesContext.getExternalContext().getRequestMap().put(chart.getVar(), object);
			
			String categoryFieldValue = chart.getValueExpression("categoryField").getValue(facesContext.getELContext()).toString();
			String dataFieldValue = chart.getValueExpression("dataField").getValue(facesContext.getELContext()).toString();
			
			writer.write("{" + categoryFieldName + ":'" + categoryFieldValue + "'," + dataFieldName + ":" + dataFieldValue + "}");
			
			if(iterator.hasNext())
				writer.write(",\n");
		}
		
		writer.write("];\n");
		
		writer.write("var " + chartVariable +  "_dataSource = new YAHOO.util.DataSource(" + chartVariable + "_data);\n");
		writer.write(chartVariable + "_dataSource.responseType=YAHOO.util.DataSource.TYPE_JSARRAY;\n");
		writer.write(chartVariable + "_responseSchema = {fields:[\"" + categoryFieldName + "\",\"" + dataFieldName + "\"]};\n");
		writer.write(chartVariable + " = new YAHOO.widget.PieChart(\"" + chart.getClientId(facesContext) + "\", " + chartVariable + "_dataSource," 
					+ "{categoryField:\"" + categoryFieldName + "\",dataField:\"" + dataFieldName + "\"");
		
		if(chart.getStyle() != null) {
			writer.write(",style:" + chart.getStyle() + "");
		}
		
		writer.write("});\n");
		
		writer.write("});\n");

		writer.endElement("script");
	}
	
	protected void encodeChartMarkup(FacesContext facesContext, PieChart chart) throws IOException{
		ResponseWriter writer = facesContext.getResponseWriter();
		
		writer.startElement("div", null);
		writer.writeAttribute("id", chart.getClientId(facesContext), null);
		
		if(chart.getStyleClass() != null)
			writer.writeAttribute("class", chart.getStyleClass(), "styleClass");
			
		writer.endElement("div");
	}
	
	private String getChartVar(PieChart chart) {
		if(chart.getWidgetVar() != null)
			return chart.getWidgetVar();
		else
			return "pf_pieChart_" + chart.getId();
	}
	
	private String getFieldName(ValueExpression fieldExpression) {
		String expressionString = fieldExpression.getExpressionString();
		String[] tokens = expressionString.split("\\.");
		
		String lastToken = tokens[tokens.length-1];
		
		return lastToken.substring(0, lastToken.length() - 1);
	}

	protected void encodeResources(FacesContext facesContext) throws IOException{
		ResponseWriter writer = facesContext.getResponseWriter();
		writer.startElement("script", null);
		writer.writeAttribute("type", "text/javascript", null);
		writer.write("YAHOO.widget.Chart.SWFURL = \"" + ResourceUtils.getResourceURL(facesContext, "/yui/charts/assets/charts.swf") + "\"");
		writer.endElement("script");
	}
	
	/**
	 * Gets a css based styles and returns a javascript object
	 * 
	 * @param style
	 * @return formatted style
	 */
	protected String formatStyle(String style) {
		String formatted = style.replaceAll(";", ",");
		
		if(formatted.endsWith(","))
			return formatted.substring(0, formatted.length() -1);
		else
			return formatted;
	}
}
