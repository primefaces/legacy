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
package org.primefaces.ui.touch.component.rowgroup;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.primefaces.ui.renderkit.CoreRenderer;
import org.primefaces.ui.touch.component.rowgroup.RowGroup;
import org.primefaces.ui.touch.component.tableview.TableView;

public class RowGroupRenderer extends CoreRenderer {
	
	public void encodeBegin(FacesContext facesContext, UIComponent component) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		RowGroup rowGroup = (RowGroup) component;
		TableView tableView = (TableView) rowGroup.getParent();
		
		boolean regular = tableView.getDisplay().equals("regular");
		String titleSize =  regular ? "h4" : "h2";
		
		writer.startElement("span", rowGroup);
		if(regular) {
			writer.writeAttribute("class", "edgetoedge", null);
		}
		
		if(rowGroup.getTitle() != null) {
			writer.startElement(titleSize, null);
			writer.write(rowGroup.getTitle());
			writer.endElement(titleSize);
		}
		
		writer.startElement("ul", null);	
	}
	
	public void encodeEnd(FacesContext facesContext, UIComponent component) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		
		writer.endElement("ul");
		writer.endElement("span");
	}
}
