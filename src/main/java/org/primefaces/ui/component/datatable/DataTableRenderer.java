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
package org.primefaces.ui.component.datatable;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.primefaces.ui.renderkit.CoreRenderer;
import org.primefaces.ui.renderkit.PartialRenderer;
import org.primefaces.ui.util.ComponentUtils;

public class DataTableRenderer extends CoreRenderer implements PartialRenderer {

	@Override
	public void decode(FacesContext facesContext, UIComponent component) {
		DataTable dataTable = (DataTable) component;
		
		if(dataTable.getSelectionMode() != null || dataTable.getColumnSelectionMode() != null) {
			Map<String,String> params = facesContext.getExternalContext().getRequestParameterMap();
			String rowSelectParam = dataTable.getClientId(facesContext) + "_selectedRows";
			
			if(params.containsKey(rowSelectParam)) {
				String rowSelectParamValue = params.get(rowSelectParam);
				
				if(ComponentUtils.isValueBlank(rowSelectParamValue)) {
					dataTable.setSelection(null);
					
					return;
				}
					
				String[] rowSelectValues = rowSelectParamValue.split(",");
				Object[] data = new Object[rowSelectValues.length];
				
				for(int i = 0; i < rowSelectValues.length; i++) {
					String rowELIndex = rowSelectValues[i].trim().substring(7);
					int rowIndex = Integer.parseInt(rowELIndex);
					dataTable.setRowIndex(rowIndex);
					
					data[i] = dataTable.getRowData();
				}
				
				dataTable.setSelection(data);
			}
			
			dataTable.setRowIndex(-1);	//clean	
		}
	}
	
	public void encodePartially(FacesContext facesContext, UIComponent component) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		DataTable dataTable = (DataTable) component;
		String clientId = dataTable.getClientId(facesContext);
		String first = facesContext.getExternalContext().getRequestParameterMap().get("first");
		if(first != null) {
			dataTable.setFirst(Integer.parseInt(first));
		}
		
		writer.startElement("table", null);
		writer.writeAttribute("id", clientId + "_table", null);
		
		encodeHeaders(facesContext, dataTable);
		encodeRows(facesContext, dataTable);
		
		writer.endElement("table");
	}
	
	public void encodeEnd(FacesContext facesContext, UIComponent component) throws IOException{
		DataTable table = (DataTable) component;
		
		encodeScript(facesContext, table);
		encodeMarkup(facesContext, table);
	}
	
	private void encodeScript(FacesContext facesContext, DataTable dataTable) throws IOException{
		ResponseWriter writer = facesContext.getResponseWriter();
		String clientId = dataTable.getClientId(facesContext);
		String widgetVar = createUniqueWidgetVar(facesContext, dataTable);
		
		writer.startElement("script", dataTable);
		writer.writeAttribute("type", "text/javascript", null);
		
		writer.write("PrimeFaces.core.Utils.onContentReady('" + clientId + "', function() {\n");
		
		String columnDefVar = encodeColumnDefinition(facesContext, dataTable, widgetVar);
		String datasourceVar = encodeDatasource(facesContext, dataTable, widgetVar);
	
		writer.write(widgetVar + " = new PrimeFaces.widget.DataTable('" + clientId + "'," + columnDefVar + "," + datasourceVar + ", {");
		
		encodeConfig(facesContext, dataTable);
		
		writer.write("});\n});\n");
		
		writer.endElement("script");
	}
	
	private String encodeDatasource(FacesContext facesContext, DataTable dataTable, String var) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		String clientId = dataTable.getClientId(facesContext);
		String datasourceVar = var + "_datasource";
		boolean dynamic = dataTable.isDynamic();
		
		if(dynamic) {
			writer.write("var " + datasourceVar + " = new YAHOO.util.DataSource('" + getActionURL(facesContext) + "');\n");
			writer.write(datasourceVar + ".connMethodPost = true;\n");
		}	
		else {
			writer.write("var " + datasourceVar + " = new YAHOO.util.DataSource(YAHOO.util.Dom.get('" + clientId + "_table'));\n");
		}
		
		writer.write(datasourceVar + ".responseType = YAHOO.util.DataSource.TYPE_HTMLTABLE;\n");
		writer.write(datasourceVar + ".responseSchema = {fields:[");
		
		for(Iterator<UIComponent> children = dataTable.getChildren().iterator(); children.hasNext();) {
			UIComponent kid = children.next();
			
			if(kid.isRendered() && kid instanceof Column) {
				Column column = (Column) kid;
				
				if(column.getSelectionMode() == null) {
					writer.write("{key:'" + kid.getId() + "'}");
					
					if(children.hasNext())
						writer.write(",");
				}
			}
		}
		
		writer.write("]");
		
		writer.write("};\n");
		
		return datasourceVar;
	}
	
	private String encodeColumnDefinition(FacesContext facesContext, DataTable dataTable, String datatableVar) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		String columnDefVar = datatableVar + "_columnDef";
		
		writer.write("var " + columnDefVar + " = [");
		for(Iterator<UIComponent> children = dataTable.getChildren().iterator(); children.hasNext();) {
			UIComponent kid = children.next();
			
			if(kid.isRendered() && kid instanceof Column) {
				Column column = (Column) kid;
				
				writer.write("{key:'" + column.getId()  + "'");
				if(column.getFacet("header") != null)
					writer.write(",label:'" + ComponentUtils.getStringValueToRender(facesContext, column.getFacet("header")) + "'");
				else
					writer.write(",label:''");
				
				if(column.isSortable()) writer.write(",sortable:true");
				if(column.isResizable()) writer.write(",resizeable:true");
				if(column.isFilter()) writer.write(",filter:true");
				if(column.getSelectionMode() != null) {
					String selector = column.getSelectionMode().equals("single") ? "radio" : "checkbox";
					
					writer.write(",formatter:'" + selector + "'");
				}
				
				writer.write("}");
				
				if(children.hasNext())
					writer.write(",");
			}
		}
		writer.write("];\n");
		
		return columnDefVar;
	}
	
	private void encodeConfig(FacesContext facesContext, DataTable dataTable) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		
		if(dataTable.isScrollable())
			writer.write("scrollable:true");
		else
			writer.write("scrollable:false");
			
		if(dataTable.getWidth() != null) writer.write(",width:'" + dataTable.getWidth() + "'");
		if(dataTable.getHeight() != null) writer.write(",height:'" + dataTable.getHeight() + "'");
		
		if(dataTable.isPaginator()) {
			writer.write(",paginator:new YAHOO.widget.Paginator({\n");
			writer.write("rowsPerPage:" + dataTable.getRows());
			writer.write(",totalRecords:" + dataTable.getRowCount());
			
			if(dataTable.getPaginatorTemplate() != null) writer.write(",template:'" + dataTable.getPaginatorTemplate() + "'");
			if(dataTable.getRowsPerPageTemplate() != null) writer.write(",rowsPerPageOptions : [" + dataTable.getRowsPerPageTemplate() + "]");
			if(dataTable.getFirstPageLinkLabel() != null) writer.write(",firstPageLinkLabel:'" + dataTable.getFirstPageLinkLabel() + "'");
			if(dataTable.getPreviousPageLinkLabel() != null) writer.write(",previousPageLinkLabel:'" + dataTable.getPreviousPageLinkLabel() + "'");
			if(dataTable.getNextPageLinkLabel() != null) writer.write(",nextPageLinkLabel:'" + dataTable.getNextPageLinkLabel() + "'");
			if(dataTable.getLastPageLinkLabel() != null) writer.write(",lastPageLinkLabel:'" + dataTable.getLastPageLinkLabel() + "'");
			
			writer.write("})\n");
		}
		
		if(dataTable.getEmptyMessage() != null) writer.write(",MSG_EMPTY : '" + dataTable.getEmptyMessage() + "'");
		
		if(dataTable.getSelectionMode() != null) {
			String mode = dataTable.getSelectionMode().equals("multiple") ? "standard" : "single";
			writer.write(",selectionMode:'" + mode + "'");
		}
		
		if(dataTable.getColumnSelectionMode() != null) {
			writer.write(",columnSelectionMode:'" + dataTable.getColumnSelectionMode() + "'");
		}
		
		if(dataTable.isDynamic()) {
			String clientId = dataTable.getClientId(facesContext);
			String formClientId = ComponentUtils.findParentForm(facesContext, dataTable).getClientId(facesContext);
			
			writer.write(",formId:'" + formClientId + "'");
			writer.write(",dynamicData:true");
			writer.write(",generateRequest:PrimeFaces.widget.DataTableUtils.loadDynamicData");
			writer.write(",initialRequest:PrimeFaces.widget.DataTableUtils.loadInitialData('" + clientId + "','" + formClientId + "')");
		}
	}

	private void encodeMarkup(FacesContext facesContext, DataTable dataTable) throws IOException{
		ResponseWriter writer = facesContext.getResponseWriter();
		String clientId = dataTable.getClientId(facesContext);
		
		writer.startElement("div", null);
		writer.writeAttribute("id", clientId , null);
		
		writer.startElement("div", null);
		writer.writeAttribute("id", clientId + "_container", null);
		
		if(!dataTable.isDynamic()) {
			writer.startElement("table", null);
			writer.writeAttribute("id", clientId + "_table", null);
			
			encodeHeaders(facesContext, dataTable);
			encodeRows(facesContext, dataTable);
			
			writer.endElement("table");
		}
		
		writer.endElement("div");
		
		String rowSelectParam = clientId + "_selectedRows";
		writer.startElement("input", null);
		writer.writeAttribute("type", "hidden", null);
		writer.writeAttribute("id", rowSelectParam, null);
		writer.writeAttribute("name", rowSelectParam, null);
		writer.endElement("input");
		
		writer.endElement("div");
	}

	private void encodeHeaders(FacesContext facesContext, DataTable dataTable) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		
		writer.startElement("thead", null);
		writer.startElement("tr", null);
		
		for (Iterator<UIComponent> iterator = dataTable.getChildren().iterator(); iterator.hasNext();) {
			UIComponent kid = iterator.next();
			
			if(kid.isRendered() && kid instanceof Column) {
				Column column = (Column) kid; 
				
				if(column.getSelectionMode() == null) {
					writer.startElement("th", column);
					if(column.getFacet("header") != null) {
						renderChild(facesContext, column.getFacet("header"));
					}
					writer.endElement("th");
				}
			}
		}
		
		writer.endElement("tr");
		writer.endElement("thead");
	}
	
	private void encodeRows(FacesContext facesContext, DataTable dataTable) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		boolean dynamic = dataTable.isDynamic();
		
		writer.startElement("tbody", null);
		writer.write("\n");
		
		if(dataTable.isLazy()) {
			dataTable.loadLazyData();
		}
		
		int rowCount = dynamic ? dataTable.getRows() : dataTable.getRowCount();
		int first = dynamic ? dataTable.getFirst() : 0;
		int i = first;
		
		while(i < (first + rowCount)) {
			dataTable.setRowIndex(i);
			
			writer.startElement("tr", null);
			writer.write("\n");
			
			for(Iterator<UIComponent> iterator = dataTable.getChildren().iterator(); iterator.hasNext();) {
				UIComponent kid = iterator.next();
				
				if(kid.isRendered() && kid instanceof Column) {
					Column column = (Column) kid;
					if(column.getSelectionMode() == null) {
						writer.write("\t\n");
						writer.startElement("td", null);
					
						renderChildren(facesContext, column);
					
						writer.endElement("td");
						writer.write("\n");
					}
				}
			}
			
			writer.endElement("tr");
			
			i++;
		}
		
		dataTable.setRowIndex(-1);		//cleanup
		
		writer.endElement("tbody");
	}
	
	public void encodeChildren(FacesContext context, UIComponent component) throws IOException {
		//Do Nothing
	}
	
	public boolean getRendersChildren() {
		return true;
	}
}