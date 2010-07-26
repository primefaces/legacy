package org.primefaces.ui.component.chart;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.el.ValueExpression;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.primefaces.ui.component.chart.series.ChartSeries;
import org.primefaces.ui.event.chart.ItemSelectEvent;
import org.primefaces.ui.renderkit.CoreRenderer;
import org.primefaces.ui.resource.ResourceUtils;
import org.primefaces.ui.util.ComponentUtils;

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
			if(component instanceof ChartSeries) {
				series.add((ChartSeries)component);
			}		
		}
		
		return series;
	}
	
	protected String getChartVar(UIChart chart) {
		if(chart.getWidgetVar() != null)
			return chart.getWidgetVar();
		else
			return "pf_chart_" + chart.getId();
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
		String[] tokens = expressionString.split("\\.");
		
		String lastToken = tokens[tokens.length-1];
		
		return lastToken.substring(0, lastToken.length() - 1);
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
		writer.write(",formClientId:'" + formClientId + "'");
		writer.write(",update:'" + chart.getUpdate() + "'");
		
		if(chart.getOncomplete() != null)
			writer.write(", onComplete: function() {" + chart.getOncomplete() + ";}");
		
		writer.write("})\n");
	}
	
	public void encodeChildren(FacesContext context, UIComponent component) throws IOException {
		//Do Nothing
	}
	
	public boolean getRendersChildren() {
		return true;
	}
}