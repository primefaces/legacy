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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.primefaces.ui.renderkit.CoreRenderer;
import org.primefaces.ui.util.ComponentUtils;

public class DataTableRenderer extends CoreRenderer{

	final Logger logger = LoggerFactory.getLogger(DataTableRenderer.class);

	public void encodeEnd(FacesContext facesContext, UIComponent component) throws IOException{
		DataTable  dataTable = (DataTable) component;
		
		encodeDataTableScript(facesContext, dataTable);
		encodeDataTableMarkup(facesContext, dataTable);
	}
	
	private void encodeDataTableScript(FacesContext facesContext, DataTable dataTable) throws IOException{
		ResponseWriter writer = facesContext.getResponseWriter();
		
		String dataTableVar = createUniqueWidgetVar(facesContext, dataTable);
		String datasourceVar = dataTableVar + "_datasource";
		String columnDefVar = dataTableVar + "_columnDef";
		String cfgVar = dataTableVar + "_cfg";
		List<Column> columns = getColumns(dataTable);
		String clientId = dataTable.getClientId(facesContext);
		
		writer.startElement("script", dataTable);
		writer.writeAttribute("type", "text/javascript", null);
		
		writer.write("YAHOO.util.Event.onContentReady(\"" + clientId + "\", function() {\n");
		
		writer.write("var " + datasourceVar + " = new YAHOO.util.DataSource(YAHOO.util.Dom.get(\"" + clientId + "_table\"));\n");
		writer.write(datasourceVar + ".responseType = YAHOO.util.DataSource.TYPE_HTMLTABLE;\n");
		writer.write(datasourceVar + ".responseSchema = {fields:[");
		
		for (Iterator<Column> schemaIterator = columns.iterator(); schemaIterator.hasNext();) {
			writer.write("{key:\"" + schemaIterator.next().getId() + "\"}");
			
			if(schemaIterator.hasNext())
				writer.write(",");
		}
		
		writer.write("]};\n");
		
		writer.write("var " + columnDefVar + " = [");
		for (Iterator<Column> columnDefIterator = columns.iterator(); columnDefIterator.hasNext();) {
			Column column = columnDefIterator.next();
			
			writer.write("{key:\"" + column.getId()  + "\"");
			writer.write(",label:\"" + ComponentUtils.getStringValueToRender(facesContext, column.getFacet("header")) + "\"");
			
			if(column.isSortable())
				writer.write(",sortable:true");
			if(column.isResizable())
				writer.write(",resizeable:true");
	
				writer.write("}");
			
			if(columnDefIterator.hasNext())
				writer.write(",");
		}
		writer.write("];\n");
		
		if(dataTable.isPaginator()) {
			writer.write("var " + getPaginatorVar(datasourceVar)+" =  new YAHOO.widget.Paginator({\n");
			writer.write("rowsPerPage : " + dataTable.getRows());
			
			if(dataTable.getPaginatorTemplate() != null)
				writer.write(",template : \"" + dataTable.getPaginatorTemplate() + "\"\n");
			if(dataTable.getRowsPerPageTemplate() != null)
				writer.write(",rowsPerPageOptions : [" + dataTable.getRowsPerPageTemplate() + "]\n");
			writer.write("})\n");

			createPaginatorLabels(dataTable, writer, datasourceVar);
		}

		writer.write("var " + cfgVar + " = {");
		if(dataTable.isScrollable())
			writer.write("scrollable: true");
		else
			writer.write("scrollable: false");
			
		if(dataTable.getWidth() != null)
			writer.write(",width: \"" + dataTable.getWidth() + "\"");
		if(dataTable.getHeight() != null)
			writer.write(",height: \"" + dataTable.getHeight() + "\"");
		
		if(dataTable.isPaginator()) 
			writer.write(",paginator: " + getPaginatorVar(datasourceVar));

		writer.write("};\n");
		
		writer.write("var " + dataTableVar + " = new YAHOO.widget.DataTable(\"" + clientId + "\"," + columnDefVar + "," + datasourceVar + "," + cfgVar + ");\n");
		
		writer.write("});\n");
		
		writer.endElement("script");
	}

	private void createPaginatorLabels(DataTable dataTable, ResponseWriter writer, String datasourceVar) throws IOException {

		if( dataTable.getPaginatorNextLabel() != null && dataTable.getPaginatorNextLabel() != "")
			writer.write(getPaginatorVar(datasourceVar) + ".setAttributeConfig('nextPageLinkLabel', { value : '" + dataTable.getPaginatorNextLabel()+"' });\n");
		else
			writer.write(getPaginatorVar(datasourceVar) + ".setAttributeConfig('nextPageLinkLabel', { value : '&nbsp;&gt;' });\n");

		if( dataTable.getPaginatorPreviousLabel() != null && dataTable.getPaginatorPreviousLabel() != "")
			writer.write(getPaginatorVar(datasourceVar) + ".setAttributeConfig('previousPageLinkLabel', { value : '" + dataTable.getPaginatorPreviousLabel()+"' });\n");
		else
			writer.write(getPaginatorVar(datasourceVar) + ".setAttributeConfig('previousPageLinkLabel', { value : '&nbsp;&lt;' });\n");
		
		if( dataTable.getPaginatorFirstLabel() != null && dataTable.getPaginatorFirstLabel() != "")
			writer.write(getPaginatorVar(datasourceVar) + ".setAttributeConfig('firstPageLinkLabel', { value : '" + dataTable.getPaginatorFirstLabel()+"' });\n");
		else
			writer.write(getPaginatorVar(datasourceVar) + ".setAttributeConfig('firstPageLinkLabel', { value : '&nbsp;&lt;&lt;' });\n");
			
		if( dataTable.getPaginatorLastLabel() != null && dataTable.getPaginatorLastLabel() != "")
			writer.write(getPaginatorVar(datasourceVar) + ".setAttributeConfig('lastPageLinkLabel', { value : '" + dataTable.getPaginatorLastLabel()+"' });\n");
		else
			writer.write(getPaginatorVar(datasourceVar) + ".setAttributeConfig('lastPageLinkLabel', { value : '&nbsp;&gt;&gt;' });\n");
	}

	private String getPaginatorVar(String datasourceVar) {
		return datasourceVar + "_paginator";
	}

	private void encodeDataTableMarkup(FacesContext facesContext, DataTable dataTable) throws IOException{
		ResponseWriter writer = facesContext.getResponseWriter();
		String clientId = dataTable.getClientId(facesContext);
		
		writer.startElement("div", null);
		writer.writeAttribute("id", clientId , null);
		
		writer.startElement("table", null);
		writer.writeAttribute("id", clientId + "_table", null);
		
		encodeTableHeader(facesContext, dataTable);
		encodeRowsMarkup(facesContext, dataTable);
		
		writer.endElement("table");
		
		writer.endElement("div");
	}

	private void encodeTableHeader(FacesContext facesContext, DataTable dataTable) throws IOException{
		ResponseWriter writer = facesContext.getResponseWriter();
		
		writer.startElement("thead", null);
		writer.startElement("tr", null);
		
		for (Iterator<UIComponent> iterator = dataTable.getChildren().iterator(); iterator.hasNext();) {
			UIComponent kid = iterator.next();
			
			if(kid instanceof Column) {
				Column column = (Column) kid; 
				
				writer.startElement("th", column);
				writer.write(ComponentUtils.getStringValueToRender(facesContext, column.getFacet("header")));
				writer.endElement("th");
			}
		}
		
		writer.endElement("tr");
		writer.endElement("thead");
	}
	
	private void encodeRowsMarkup(FacesContext facesContext, DataTable dataTable) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		
		writer.startElement("tbody", null);
		writer.write("\n");
		
		int rowCount = dataTable.getRowCount();
		
		for (int i = 0; i < rowCount; i++) {
			dataTable.setRowIndex(i);
			
			writer.startElement("tr", null);
			writer.write("\n");
			
			for (Iterator<UIComponent> iterator = dataTable.getChildren().iterator(); iterator.hasNext();) {
				UIComponent kid = iterator.next();
				
				if(kid instanceof Column) {
					Column column = (Column) kid; 
			
					writer.write("\t\n");
					writer.startElement("td", null);
				
					renderChildren(facesContext, column);
				
					writer.endElement("td");
					writer.write("\n");
				}
			}
			
			writer.endElement("tr");
		}
		
		dataTable.setRowIndex(-1);		//cleanup
		
		writer.endElement("tbody");
	}
	
	private List<Column> getColumns(DataTable dataTable) {
		List<Column> columns = new ArrayList<Column>();
		
		for (Iterator<UIComponent> iterator = dataTable.getChildren().iterator(); iterator.hasNext();) {
			UIComponent kid = (UIComponent) iterator.next();
			
			if(kid instanceof Column) {
				columns.add((Column) kid);
			}
		}
		
		return columns;
	}
	
	public void encodeChildren(FacesContext context, UIComponent component) throws IOException {
		//Do Nothing
	}
	
	public boolean getRendersChildren() {
		return true;
	}
}