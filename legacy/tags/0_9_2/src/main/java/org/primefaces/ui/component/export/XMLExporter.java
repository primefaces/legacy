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
import java.util.ArrayList;
import java.util.List;

import javax.el.MethodExpression;
import javax.faces.component.UIColumn;
import javax.faces.component.UIComponent;
import javax.faces.component.UIData;
import javax.faces.component.ValueHolder;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletResponse;

public class XMLExporter extends Exporter {

	public void export(FacesContext facesContext, UIData table, String filename, int[] excludeColumns, MethodExpression preProcessor, MethodExpression postProcessor) throws IOException {
		HttpServletResponse response = (HttpServletResponse) facesContext.getExternalContext().getResponse();
    	
		PrintWriter writer = new PrintWriter(response.getOutputStream());
		
		List<UIColumn> columns = getColumnsToExport(table, excludeColumns);
    	int rows = table.getRowCount();
    	List<String> headers = getHeaderTexts(table);
    	String var = table.getVar().toLowerCase();
    	
    	writer.write("<?xml version=\"1.0\"?>\n");
    	writer.write("<" + table.getId() + ">\n");
    	
    	for (int i = 0; i < rows; i++) {
    		table.setRowIndex(i);
    		
    		writer.write("\t<" + var + ">\n");
    		addColumnValues(writer, columns, headers);
    		writer.write("\t</" + var + ">\n");
		}
    	
    	writer.write("</" + table.getId() + ">");
    	
    	table.setRowIndex(-1);
    	
    	response.setContentType("text/xml");
    	response.setHeader("Expires", "0");
        response.setHeader("Cache-Control","must-revalidate, post-check=0, pre-check=0");
        response.setHeader("Pragma", "public");
        response.setHeader("Content-disposition", "attachment;filename="+ filename + ".xml");
        
        writer.flush();
        writer.close();
        
        response.getOutputStream().flush();
	}
	
	private void addColumnValues(PrintWriter writer, List<UIColumn> columns, List<String> headers) throws IOException {
		for (int i = 0; i < columns.size(); i++) {
			UIColumn column = columns.get(i);
			
			if(column.isRendered())
				addColumnValue(writer, column.getChildren(), headers.get(i));
		}
	}
	
	private List<String> getHeaderTexts(UIData data) {
		List<String> headers = new ArrayList<String>();
		 
        for (int i = 0; i < data.getChildCount(); i++) {
            UIComponent child = (UIComponent) data.getChildren().get(i);
            
            if (child instanceof UIColumn && child.isRendered()) {
            	UIComponent header = ((UIColumn) child).getHeader();
            	
            	if(header instanceof ValueHolder && header.isRendered()) {
            		Object value = ((ValueHolder) header).getValue();
            		
            		headers.add(value.toString());
            	}
            }
        }
        return headers;
	}
	
	private void addColumnValue(PrintWriter writer, List<UIComponent> components, String header) throws IOException {
		StringBuffer buffer = new StringBuffer();
		String tag = header.toLowerCase();
		writer.write("\t\t<" + tag + ">");

		for(UIComponent component : components) {
			if(component instanceof ValueHolder && component.isRendered()) {
				Object value = ((ValueHolder) component).getValue();

				if(value != null) {
					buffer.append(value.toString());
				}
			}
		}

		writer.write(buffer.toString());
		
		writer.write("</" + tag + ">\n");
	}
}