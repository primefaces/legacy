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
import java.util.List;

import javax.faces.component.UIColumn;
import javax.faces.component.UIComponent;
import javax.faces.component.UIData;
import javax.faces.component.ValueHolder;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

public class ExcelExporter extends Exporter {

	public void export(FacesContext facesContext, UIData table, String filename, int[] excludeColumns) throws IOException {    	
    	HSSFWorkbook wb = new HSSFWorkbook();
    	HSSFSheet sheet = wb.createSheet();
    	List<UIColumn> columns = getColumnsToExport(table, excludeColumns);
    	int numberOfColumns = columns.size();
    	int tableSize = table.getRowCount();
    	int index = table.getRowIndex();
    	
    	addColumnHeaders(sheet, columns);
    	
    	for (int i = 0; i < tableSize; i++) {
    		table.setRowIndex(i);
			HSSFRow row = sheet.createRow(i+1);
			for (int j = 0; j < numberOfColumns; j++) {
				UIColumn column = columns.get(j);
				addColumnValue(row, column.getChildren().get(0), j);
			}
		}
    	
    	table.setRowIndex(index);	//restore row index
    	
    	writeExcelToResponse(((HttpServletResponse)facesContext.getExternalContext().getResponse()), wb, filename);
	}
	
	private void addColumnHeaders(HSSFSheet sheet, List<UIColumn> columns) {
        HSSFRow rowHeader = sheet.createRow(0);

        for (int i = 0; i < columns.size(); i++) {
            UIColumn column = (UIColumn) columns.get(i);
            addColumnValue(rowHeader, column.getHeader(), i);
        }
    }

    private void addColumnValue(HSSFRow rowHeader, UIComponent component, int index) {
        HSSFCell cell = rowHeader.createCell(index);

        if (component instanceof ValueHolder) {
            Object value = ((ValueHolder)component).getValue();	//TODO: String'e cevir convertarla
            
            if(value != null)
            	cell.setCellValue(new HSSFRichTextString(value.toString()));
            else
            	cell.setCellValue(new HSSFRichTextString());
        }
    }
    
    private void writeExcelToResponse(HttpServletResponse response, HSSFWorkbook generatedExcel, String filename) throws IOException {
        response.setContentType("application/vnd.ms-excel");
        response.setHeader("Expires", "0");
        response.setHeader("Cache-Control","must-revalidate, post-check=0, pre-check=0");
        response.setHeader("Pragma", "public");
        response.setHeader("Content-disposition", "attachment;filename="+ filename + ".xls");

        generatedExcel.write(response.getOutputStream());
    }  
}