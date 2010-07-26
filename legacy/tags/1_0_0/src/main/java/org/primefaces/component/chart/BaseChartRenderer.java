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
package org.primefaces.component.chart;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.el.ValueExpression;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.primefaces.component.chart.series.ChartSeries;
import org.primefaces.event.ItemSelectEvent;
import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.resource.ResourceUtils;
import org.primefaces.util.ComponentUtils;

public class BaseChartRenderer extends CoreRenderer {
	
	@Override
	public void decode(FacesContext facesContext, UIComponent component) {
		String clientId = component.getClientId(facesContext);
		Map<String,String> params = facesContext.getExternalContext().getRequestParameterMap();

		if(params.containsKey(clientId)) {
			int seriesIndex = Integer.parseInt(params.get("seriesIndex"));
			int itemIndex = Integer.parseInt(params.get("itemIndex"));
			
			component.queueEvent(new ItemSelectEvent(component, itemIndex, seriesIndex));
		}
	}

	public void encodeEnd(FacesContext facesContext, UIComponent component) throws IOException{
		UIChart chart = (UIChart) component;
		
		encodeResources(facesContext);
		encodeChartScript(facesContext, chart);
		encodeChartMarkup(facesContext, chart);
	}
	
	/**
	 * Each chart renderer should override this method
	 * 
	 * @param facesContext
	 * @param chart
	 * @throws IOException
	 */
	protected void encodeChartScript(FacesContext facesContext, UIChart chart) throws IOException {
		
	}
	
	protected List<ChartSeries> getSeries(UIChart chart) {
		List<UIComponent> children = chart.getChildren();
		List<ChartSeries> series = new ArrayList<ChartSeries>();
		
		for (UIComponent component : children) {
			if(component instanceof ChartSeries && component.isRendered())
				series.add((ChartSeries) component);	
		}
		
		return series;
	}
	
	protected String getChartVar(UIChart chart) {
		return createUniqueWidgetVar(FacesContext.getCurrentInstance(), chart);
	}
	
	protected String getDataSourceVar(UIChart chart) {
		return getChartVar(chart) + "_dataSource";
	}
	
	protected String getLocalDataVar(UIChart chart) {
		return getChartVar(chart) + "_data";
	}
	
	protected String getSeriesDefVar(UIChart chart) {
		return getChartVar(chart) + "_seriesDef";
	}
	
	protected String getFieldName(ValueExpression fieldExpression) {
		String expressionString = fieldExpression.getExpressionString();
		String expressionContent = expressionString.substring(2, expressionString.length() - 1);
		int firstIndex = expressionContent.indexOf("[");
		
		if(firstIndex != -1) {
			int lastIndex = expressionContent.indexOf("]");
			
			return expressionContent.substring(firstIndex + 1, lastIndex);
		} else {
			String[] tokens = expressionContent.split("\\.");
			
			return tokens[tokens.length-1];
		}	
	}
	
	protected void encodeResources(FacesContext facesContext) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		
		writer.startElement("script", null);
		writer.writeAttribute("type", "text/javascript", null);
		writer.write("YAHOO.widget.Chart.SWFURL = \"" + ResourceUtils.getResourceURL(facesContext,
						"/yui/charts/assets/charts.swf") + "\"");
		writer.endElement("script");
	}
	
	protected void encodeChartMarkup(FacesContext facesContext, UIChart chart) throws IOException{
		ResponseWriter writer = facesContext.getResponseWriter();
		
		writer.startElement("div", null);
		writer.writeAttribute("id", chart.getClientId(facesContext), null);
		writer.writeAttribute("style", "width:" + chart.getWidth() + ";height:" + chart.getHeight(), null);
		
		if(chart.getStyleClass() != null)
			writer.writeAttribute("class", chart.getStyleClass(), "styleClass");
		
			
		writer.endElement("div");
	}
	
	protected void encodeItemSelectEvent(FacesContext facesContext, UIChart chart) throws IOException {		
		ResponseWriter writer = facesContext.getResponseWriter();
		String clientId = chart.getClientId(facesContext);
		String formClientId = ComponentUtils.findParentForm(facesContext, chart).getClientId(facesContext);
	
		writer.write(getChartVar(chart) + ".subscribe('itemClickEvent', PrimeFaces.widget.ChartUtils.itemSelectHandler, {clientId:'" + clientId + "'");
		writer.write(",url:'" + getActionURL(facesContext) + "'");
		writer.write(",formId:'" + formClientId + "'");
		writer.write(",update:'" + ComponentUtils.findClientIds(facesContext, chart, chart.getUpdate()) + "'");
		
		if(chart.getOncomplete() != null)
			writer.write(",oncomplete: function() {" + chart.getOncomplete() + ";}");
		
		writer.write("})\n");
	}
	
	public void encodeChildren(FacesContext context, UIComponent component) throws IOException {
		//Do Nothing
	}
	
	public boolean getRendersChildren() {
		return true;
	}
}