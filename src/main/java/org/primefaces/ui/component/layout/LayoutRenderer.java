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
package org.primefaces.ui.component.layout;

import java.io.IOException;
import java.util.Iterator;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.primefaces.ui.renderkit.CoreRenderer;

public class LayoutRenderer extends CoreRenderer {
	
	@Override
	public void encodeBegin(FacesContext facesContext, UIComponent component) throws IOException {
		Layout layout = (Layout) component;
		
		encodeScript(facesContext, layout);
		
		if(!layout.isFullPage()) {
			ResponseWriter writer = facesContext.getResponseWriter();
			
			writer.startElement("div", layout);
			writer.writeAttribute("id", layout.getClientId(facesContext), "id");
		}
	}

	@Override
	public void encodeEnd(FacesContext facesContext, UIComponent component) throws IOException {
		Layout layout = (Layout) component;
		
		if(!layout.isFullPage())
			facesContext.getResponseWriter().endElement("div");
	}

	private void encodeScript(FacesContext facesContext, Layout layout) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		String clientId = layout.getClientId(facesContext);
		String var = createUniqueWidgetVar(facesContext, layout);
		
		writer.startElement("script", null);
		writer.writeAttribute("type", "text/javascript", null);
		
		writer.write("YAHOO.util.Event.onDOMReady(function() {\n");
		
		writer.write(var + " = new YAHOO.widget.Layout(");
		if(!layout.isFullPage()) {
			writer.write("'" + clientId + "', {");

			if(shouldRenderAttribute(layout.getWidth())) writer.write("width:" + layout.getWidth() + ",");
			if(shouldRenderAttribute(layout.getHeight())) writer.write("height:" + layout.getHeight() + ",");
			if(shouldRenderAttribute(layout.getMinWidth())) writer.write("minWidth:" + layout.getMinWidth() + ",");
			if(shouldRenderAttribute(layout.getMinHeight())) writer.write("minHeight:" + layout.getMinHeight() + ",");
			
			writer.write("units:[\n");
		} else {
			writer.write("{units:[\n");
		}
		
		for (Iterator<UIComponent> units = layout.getChildren().iterator(); units.hasNext();) {
			LayoutUnit unit = (LayoutUnit) units.next();
			
			if(unit.isRendered()) {
				writer.write("{");
				writer.write("position:'" + unit.getPosition() + "'");
				writer.write(",body:'" + unit.getClientId(facesContext) + "'");
				
				if(unit.getWidth() != null) writer.write(",width:" + unit.getWidth());
				if(unit.getHeight() != null) writer.write(",height:" + unit.getHeight());
				if(unit.isResize()) writer.write(",resize:true");
				if(unit.isCollapse()) writer.write(",collapse:true");
				if(unit.isAnimate()) writer.write(",animate:true");
				if(unit.isScroll()) writer.write(",scroll:true");
				if(unit.getGutter() != null) writer.write(",gutter:'" + unit.getGutter() + "'");
				if(unit.getHeader() != null) writer.write(",header:'" +  unit.getHeader() + "'");
				if(unit.getFooter() != null) writer.write(",footer:'" +  unit.getFooter() + "'");
				
				writer.write("}");
			}
			
			if(units.hasNext())
				writer.write(",");
		}
		writer.write("]});\n");
		
		writer.write(var  + ".render();\n");
		
		writer.write("});\n");
		
		writer.endElement("script");
	}
}