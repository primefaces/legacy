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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import javax.faces.component.UIColumn;
import javax.faces.component.UIComponent;
import javax.faces.component.UIData;
import javax.faces.component.ValueHolder;
import javax.faces.context.FacesContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

public class PDFExporter extends Exporter {

	public void export(FacesContext facesContext, UIData table, String filename, int[] excludeColumns) throws IOException {    	
		List<UIColumn> columns = getColumnsToExport(table, excludeColumns);
    	int numberOfColumns = columns.size();
    	int tableSize = table.getRowCount();
    	int index = table.getRowIndex();
    	PdfPTable pdfTable = new PdfPTable(numberOfColumns);
    	Font font = FontFactory.getFont("HELVETICA", "CP1254");
    	Font headerFont = FontFactory.getFont("HELVETICA", "CP1254", Font.DEFAULTSIZE, Font.BOLD);
    	
    	addColumnHeaders(pdfTable, columns, headerFont);
    	
    	for (int i = 0; i < tableSize; i++) {
    		table.setRowIndex(i);
			for (int j = 0; j < numberOfColumns; j++) {
				UIColumn column = columns.get(j);
				addColumnValue(pdfTable, column.getChildren().get(0), j, font);
			}
		}
    	
    	table.setRowIndex(index);	//restore row index
    	
    	try {
			writePDFToResponse(((HttpServletResponse)facesContext.getExternalContext().getResponse()), pdfTable, filename);
		} catch (DocumentException e) {
			throw new IOException(e);
		}
	}
	
	private void addColumnHeaders(PdfPTable pdfTable, List<UIColumn> columns, Font font) {
        for (int i = 0; i < columns.size(); i++) {
            UIColumn column = (UIColumn) columns.get(i);
            addColumnValue(pdfTable, column.getHeader(), i, font);
        }
	}

    private void addColumnValue(PdfPTable pdfTable, UIComponent component, int index, Font font) {
        if (component instanceof ValueHolder) {
            Object value = ((ValueHolder)component).getValue();	
            
            if(value != null)
            	pdfTable.addCell(new Paragraph(value.toString(), font)); //TODO: String'e cevir convertarla
            else
            	pdfTable.addCell(new Paragraph("", font));
        }
    }
    
    private void writePDFToResponse(HttpServletResponse response, PdfPTable pdfTable, String fileName) throws IOException, DocumentException {
        Document document = new Document(PageSize.A4.rotate());
        document.setPageSize(PageSize.A4);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter.getInstance(document, baos);
        document.open();
        document.add(pdfTable);
        document.close();
        
    	response.setContentType("application/pdf");
    	response.setHeader("Expires", "0");
        response.setHeader("Cache-Control","must-revalidate, post-check=0, pre-check=0");
        response.setHeader("Pragma", "public");
        response.setHeader("Content-disposition", "attachment;filename="+ fileName + ".pdf");
        response.setContentLength(baos.size());
        
        ServletOutputStream out = response.getOutputStream();
        baos.writeTo(out);
        out.flush();
        
    }
  
    public UIComponent findComponentById(FacesContext context, UIComponent root, String id) {
		UIComponent component = null;
		
		for (int i = 0; i < root.getChildCount() && component == null; i++) {
			UIComponent child = (UIComponent) root.getChildren().get(i);
			component = findComponentById(context, child, id);
		}

		if (root.getId() != null) {
			if (component == null && root.getId().equals(id)) {
				component = root;
			}
		}
		return component;
	}
}