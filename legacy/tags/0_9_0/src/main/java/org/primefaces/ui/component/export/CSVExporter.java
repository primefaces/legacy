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
package org.primefaces.ui.component.export;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;

import javax.faces.component.UIColumn;
import javax.faces.component.UIComponent;
import javax.faces.component.UIData;
import javax.faces.component.ValueHolder;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletResponse;

public class CSVExporter extends Exporter {

	public void export(FacesContext facesContext, UIData table, String filename, int[] excludeColumns) throws IOException {
		HttpServletResponse response = (HttpServletResponse) facesContext.getExternalContext().getResponse();
    	
		PrintWriter writer = new PrintWriter(response.getOutputStream());
		
		List<UIColumn> columns = getColumnsToExport(table, excludeColumns);
    	int rows = table.getRowCount();
    	int index = table.getRowIndex();
    	
    	addColumnHeaders(writer, columns);
    	
    	
    	for (int i = 0; i < rows; i++) {
    		table.setRowIndex(i);
    		addColumnValues(writer, columns);
			writer.write("\n");
		}
    	
    	table.setRowIndex(index);	//restore row index
    	
    	response.setContentType("text/csv");
    	response.setHeader("Expires", "0");
        response.setHeader("Cache-Control","must-revalidate, post-check=0, pre-check=0");
        response.setHeader("Pragma", "public");
        response.setHeader("Content-disposition", "attachment;filename="+ filename + ".csv");
        
        writer.flush();
        writer.close();
        
        response.getOutputStream().flush();
	}
	
	private void addColumnValues(PrintWriter writer, List<UIColumn> columns) throws IOException {
		for (Iterator<UIColumn> iterator = columns.iterator(); iterator.hasNext();) {
			UIColumn column = (UIColumn) iterator.next();
			
			addColumnValue(writer, column.getChildren().get(0));
			
			if(iterator.hasNext())
				writer.write(",");
		}
	}

	private void addColumnHeaders(PrintWriter writer, List<UIColumn> columns) throws IOException {
		for (Iterator<UIColumn> iterator = columns.iterator(); iterator.hasNext();) {
			UIColumn column = (UIColumn) iterator.next();
			
			addColumnValue(writer, column.getHeader());
			
			if(iterator.hasNext())
				writer.write(",");
		}
		
		writer.write("\n");
    }
	
	private void addColumnValue(PrintWriter writer, UIComponent component) throws IOException {
        if (component instanceof ValueHolder) {
            Object value = ((ValueHolder)component).getValue();	
            
            if(value != null)
            	writer.write("\"" + value.toString() + "\"");
            else
            	writer.write("\"\"");
        }
	}
}
