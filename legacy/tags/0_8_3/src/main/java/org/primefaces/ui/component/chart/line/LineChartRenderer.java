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
package org.primefaces.ui.component.chart.line;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.el.ValueExpression;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.servlet.ServletResponse;

import org.primefaces.ui.component.chart.BaseChartRenderer;
import org.primefaces.ui.component.chart.UIChart;
import org.primefaces.ui.component.chart.series.ChartSeries;
import org.primefaces.ui.renderkit.PartialRenderer;

public class LineChartRenderer extends BaseChartRenderer implements PartialRenderer {
	
	@Override
	protected void encodeChartScript(FacesContext facesContext, UIChart chart) throws IOException{
		ResponseWriter writer = facesContext.getResponseWriter();
		String clientId = chart.getClientId(facesContext);
		String xfieldName = getFieldName(chart.getValueExpression("xfield"));
		List<ChartSeries> series = getSeries(chart);
		
		writer.startElement("script", null);
		writer.writeAttribute("type", "text/javascript", null);
		
		writer.write("YAHOO.util.Event.onContentReady(\"" + clientId + "\", function() {\n");
		
		if(!chart.isLive())
			encodeLocalData(facesContext, chart, xfieldName, series);
		
		encodeDataSource(facesContext, chart, xfieldName, series);
		encodeSeriesDef(facesContext, chart, series);
		encodeChartWidget(facesContext, chart, clientId, xfieldName);
			
		writer.write("});\n");
		
		writer.endElement("script");
	}
	
	private void encodeLocalData(FacesContext facesContext, UIChart chart, String xfieldName, List<ChartSeries> series) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		
		writer.write(getLocalDataVar(chart) + " = [" );
		
		Collection<?> value = (Collection<?>) chart.getValue();
		for (Iterator<?> iterator = value.iterator(); iterator.hasNext();) {
			facesContext.getExternalContext().getRequestMap().put(chart.getVar(), iterator.next());
			
			String xfieldValue = chart.getValueExpression("xfield").getValue(facesContext.getELContext()).toString();	//TODO: Use converter if any
			
			writer.write("{" + xfieldName + ":\"" + xfieldValue + "\"");
			
			for (ChartSeries axis : series) {
				ValueExpression ve = axis.getValueExpression("value");
				String fieldName = getFieldName(axis.getValueExpression("value"));
				
				writer.write("," + fieldName + ":" + ve.getValue(facesContext.getELContext()).toString());	//TODO: Use converter if any
			}			
			
			writer.write("}");
			
			if(iterator.hasNext())
				writer.write(",\n");
		}
		
		writer.write("];\n");
	}
	
	private void encodeDataSource(FacesContext facesContext, UIChart chart, String xfieldName, List<ChartSeries> series) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		String dataSourceVar = getDataSourceVar(chart);
		
		if(chart.isLive()) {
			writer.write("var " + dataSourceVar +  " = new YAHOO.util.DataSource(\"" + getActionURL(facesContext) + "\");\n");
			writer.write(dataSourceVar + ".connMethodPost = true;\n");
			writer.write(dataSourceVar + ".responseType=YAHOO.util.DataSource.TYPE_JSON;\n");
			writer.write(dataSourceVar + ".responseSchema = {resultsList:\"LiveChartDataResponse.Data\", fields:[\"" + xfieldName + "\"");
		} else {
			writer.write("var " + dataSourceVar +  " = new YAHOO.util.DataSource(" + getLocalDataVar(chart) + ");\n");
			writer.write(dataSourceVar + ".responseType=YAHOO.util.DataSource.TYPE_JSARRAY;\n");
			writer.write(dataSourceVar + ".responseSchema = {fields:[\"" + xfieldName + "\"");
		}
		
		for (ChartSeries axis : series)
			writer.write(",\"" + getFieldName(axis.getValueExpression("value")) + "\"");
		
		writer.write("]};\n");
	}
	
	private void encodeSeriesDef(FacesContext facesContext, UIChart chart, List<ChartSeries> series) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		
		writer.write("var " + getSeriesDefVar(chart) + " = [");
		for (int i=0; i<series.size();i++) {
			ChartSeries currentSeries = series.get(i);
			String fieldName = getFieldName(currentSeries.getValueExpression("value"));
			writer.write("{displayName:\"" + currentSeries.getLabel() + "\", yField:\"" + fieldName + "\"");
			
			if(currentSeries.getStyle() != null) {
				writer.write(",style:" + currentSeries.getStyle());
			}
			
			writer.write("}");
			
			if(i != series.size() -1)
				writer.write(",");
		}
		writer.write("];\n");
	}
	
	private void encodeChartWidget(FacesContext facesContext, UIChart chart, String clientId, String xfieldName) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		
		writer.write(getChartVar(chart) + " = new YAHOO.widget.LineChart(\"" + clientId + "\"," + getDataSourceVar(chart) + ",{");
		writer.write("xField:\"" + xfieldName + "\"");
		writer.write(",series:" + getSeriesDefVar(chart) );
		
		if(chart.getWmode() != null)
			writer.write(",wmode:\"" + chart.getWmode() + "\"");
		
		if(chart.isLive()) {
			writer.write(",polling:" + chart.getRefreshInterval());
			writer.write(",request:PrimeFaces.widget.ChartUtils.createPollingParams(\"" + clientId + "\")");
		}
		
		if(chart.getStyle() != null) {
			writer.write(",style:" + chart.getStyle() + "");
		}
		
		writer.write("});\n");
		
		if(chart.getItemSelectListener() != null)
			encodeItemSelectEvent(facesContext, chart);
	}
	
	public void encodePartially(FacesContext facesContext, UIComponent component) throws IOException {
		((ServletResponse) facesContext.getExternalContext().getResponse()).setContentType("application/json");
		ResponseWriter writer = facesContext.getResponseWriter();
		LineChart chart = (LineChart) component;
		String xfieldName = getFieldName(chart.getValueExpression("xfield"));
		List<ChartSeries> series = getSeries(chart);
		
		writer.write("{");
		writer.write("\"LiveChartDataResponse\" : {\n");
		writer.write("\t\"Data\" : [\n");
		
		Collection<?> value = (Collection<?>) chart.getValue();
		for (Iterator<?> iterator = value.iterator(); iterator.hasNext();) {
			facesContext.getExternalContext().getRequestMap().put(chart.getVar(), iterator.next());
			
			String xfieldValue = chart.getValueExpression("xfield").getValue(facesContext.getELContext()).toString();	//TODO: Use converter if any
			
			writer.write("{\"" + xfieldName + "\":\"" + xfieldValue + "\"");
			
			for (ChartSeries axis : series) {
				ValueExpression ve = axis.getValueExpression("value");
				String fieldName = getFieldName(axis.getValueExpression("value"));
				
				writer.write(",\"" + fieldName + "\":" + ve.getValue(facesContext.getELContext()).toString());	//TODO: Use converter if any
			}			
			
			writer.write("}");
			
			if(iterator.hasNext())
				writer.write(",\n");
		}
		
		writer.write("\t]\n");
		writer.write("}\n");
		writer.write("}");
	}
}
