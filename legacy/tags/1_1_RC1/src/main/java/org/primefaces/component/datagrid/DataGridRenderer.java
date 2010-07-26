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
package org.primefaces.component.datagrid;

import java.io.IOException;
import java.util.Map;

import javax.faces.FacesException;
import javax.faces.application.StateManager;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.servlet.ServletResponse;

import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.renderkit.PartialRenderer;
import org.primefaces.util.ComponentUtils;
import org.primefaces.util.RendererUtils;

public class DataGridRenderer extends CoreRenderer implements PartialRenderer {
	
	public void decode(FacesContext facesContext, UIComponent component) {
		Map<String, String> params = facesContext.getExternalContext().getRequestParameterMap();
		DataGrid grid = (DataGrid) component;
		String clientId = grid.getClientId(facesContext);
		boolean isAjaxPaging = params.containsKey(clientId + "_ajaxPaging");
		
		if(isAjaxPaging) {
			grid.setFirst(Integer.valueOf(params.get(clientId + "_first")));
			grid.setRows(Integer.valueOf(params.get(clientId + "_rows")));
			grid.setPage(Integer.valueOf(params.get(clientId + "_page")));
		}
	}

	public void encodeEnd(FacesContext facesContext, UIComponent component) throws IOException{
		DataGrid grid = (DataGrid) component;
		
		encodeMarkup(facesContext, grid);
		encodeScript(facesContext, grid);
	}

	protected void encodeMarkup(FacesContext facesContext, DataGrid grid) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		String clientId = grid.getClientId(facesContext);
		boolean hasPaginator = grid.isPaginator();
		String paginatorPosition = grid.getPaginatorPosition();
		String styleClass = grid.getStyleClass() == null ? DataGrid.CONTAINER_CLASS : DataGrid.CONTAINER_CLASS + " " + grid.getStyleClass();
		
		writer.startElement("div", grid);
		writer.writeAttribute("id", clientId, "id");
		writer.writeAttribute("class", styleClass, "styleClass");
		
		if(hasPaginator && !paginatorPosition.equalsIgnoreCase("bottom")) {
			encodePaginatorContainer(facesContext, clientId + "_paginatorTop");
		}

		encodeTable(facesContext, grid, false);
		
		if(hasPaginator && !paginatorPosition.equalsIgnoreCase("top")) {
			encodePaginatorContainer(facesContext, clientId + "_paginatorBottom");
		}
		
		writer.endElement("div");
	}
	
	protected void encodeScript(FacesContext facesContext, DataGrid grid) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		String clientId = grid.getClientId(facesContext);
		String widgetVar = createUniqueWidgetVar(facesContext, grid);
		
		UIComponent form = ComponentUtils.findParentForm(facesContext, grid);
		if(form == null) {
			throw new FacesException("DataGrid : \"" + clientId + "\" must be inside a form element");
		}
		
		writer.startElement("script", null);
		writer.writeAttribute("type", "text/javascript", null);

		writer.write(widgetVar + " = new PrimeFaces.widget.DataGrid('" + clientId + "',{");
		writer.write("url:'" + getActionURL(facesContext) + "'");
		writer.write(",formId:'" + form.getClientId(facesContext) + "'");
		
		if(grid.isPaginator()) {				
			writer.write(",paginator:new YAHOO.widget.Paginator({");
			writer.write("rowsPerPage:" + grid.getRows());
			writer.write(",totalRecords:" + grid.getRowCount());
			writer.write(",initialPage:" + grid.getPage());
			
			if(grid.getPageLinks() != 10) writer.write(",pageLinks:" + grid.getPageLinks());
			if(grid.getPaginatorTemplate() != null) writer.write(",template:'" + grid.getPaginatorTemplate() + "'");
			if(grid.getRowsPerPageTemplate() != null) writer.write(",rowsPerPageOptions : [" + grid.getRowsPerPageTemplate() + "]");
			if(grid.getFirstPageLinkLabel() != null) writer.write(",firstPageLinkLabel:'" + grid.getFirstPageLinkLabel() + "'");
			if(grid.getPreviousPageLinkLabel() != null) writer.write(",previousPageLinkLabel:'" + grid.getPreviousPageLinkLabel() + "'");
			if(grid.getNextPageLinkLabel() != null) writer.write(",nextPageLinkLabel:'" + grid.getNextPageLinkLabel() + "'");
			if(grid.getLastPageLinkLabel() != null) writer.write(",lastPageLinkLabel:'" + grid.getLastPageLinkLabel() + "'");
			if(grid.getCurrentPageReportTemplate() != null) writer.write(",pageReportTemplate:'" + grid.getCurrentPageReportTemplate() + "'");
			if(!grid.isPaginatorAlwaysVisible()) writer.write(",alwaysVisible:false");
			
			String paginatorPosition = grid.getPaginatorPosition();
			String paginatorContainer = null;
			if(paginatorPosition.equals("both"))
				paginatorContainer = clientId + "_paginatorTop','" + clientId + "_paginatorBottom" ;
			else if(paginatorPosition.equals("top"))
				paginatorContainer = clientId + "_paginatorTop";
			else if(paginatorPosition.equals("bottom"))
				paginatorContainer = clientId + "_paginatorBottom";
			
			writer.write(",containers:['" + paginatorContainer + "']");
			
			writer.write("})");
			
			if(grid.isEffect()) {
				writer.write(",effect:true");
				writer.write(",effectSpeed:'" + grid.getEffectSpeed() + "'");
			}
		}
		
		writer.write("});");
		
		writer.endElement("script");
	}
	
	protected void encodeTable(FacesContext facesContext, DataGrid grid, boolean hidden) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		String clientId = grid.getClientId(facesContext);
		int columns = grid.getColumns();
		int rowIndex = grid.getFirst();
		int numberOfRowsToRender = (grid.getRows() != 0 ? grid.getRows() : grid.getRowCount()) / columns;
		
		writer.startElement("table", grid);
		writer.writeAttribute("id", clientId + "_table", "id");
		writer.writeAttribute("class", DataGrid.TABLE_CLASS, null);
		if(hidden)
			writer.writeAttribute("style", "display:none", null);
			
		writer.startElement("tbody", null);
		
		for(int i=0; i < numberOfRowsToRender; i++) {
			writer.startElement("tr", null);
			writer.writeAttribute("class", DataGrid.TABLE_ROW_CLASS, null);
			
			for(int j=0; j < columns; j++) {
				grid.setRowIndex(rowIndex);
				
				writer.startElement("td", null);
				writer.writeAttribute("class", DataGrid.TABLE_COLUMN_CLASS, null);
				if(grid.isRowAvailable()) {
					renderChildren(facesContext, grid);
					rowIndex++;
				}
				writer.endElement("td");
			}
			
			writer.endElement("tr");
			
			if(!grid.isRowAvailable())
				break;
		}
		
		grid.setRowIndex(-1);	//cleanup
		
		writer.endElement("tbody");
		writer.endElement("table");
	}
	
	protected void encodePaginatorContainer(FacesContext facesContext, String id) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		
		writer.startElement("div", null);
		writer.writeAttribute("id", id, "id");
		writer.writeAttribute("class", "ui-paginator ui-widget-header ui-corner-all", null);
		writer.endElement("div");
	}

	public void encodePartially(FacesContext facesContext, UIComponent component) throws IOException {
		DataGrid grid = (DataGrid) component;
		
		ServletResponse response = (ServletResponse) facesContext.getExternalContext().getResponse();
		response.setContentType("text/xml");
		
		ResponseWriter writer = facesContext.getResponseWriter();
		try {
			writer.write("<?xml version=\"1.0\" encoding=\"" + response.getCharacterEncoding() + "\"?>");
			writer.write("<partial-response>");
			
			//Tab content
			writer.write("<table>");
			RendererUtils.startCDATA(facesContext);
			
			encodeTable(facesContext, grid, (grid.isPaginator() && grid.isEffect()));
			
			RendererUtils.endCDATA(facesContext);
			writer.write("</table>");
	
			//State
			writer.write("<state>");
			RendererUtils.startCDATA(facesContext);
			
			StateManager stateManager = facesContext.getApplication().getStateManager();
			stateManager.writeState(facesContext, stateManager.saveView(facesContext));
			
			RendererUtils.endCDATA(facesContext);
			writer.write("</state>");
			
			writer.write("</partial-response>");
		}catch(IOException exception) {
			exception.printStackTrace();
		}

		facesContext.responseComplete();
	}

	public void encodeChildren(FacesContext context, UIComponent component) throws IOException {
		//Do Nothing
	}
	
	public boolean getRendersChildren() {
		return true;
	}
}
