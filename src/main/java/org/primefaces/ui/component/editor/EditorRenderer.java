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
package org.primefaces.ui.component.editor;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.primefaces.ui.renderkit.CoreRenderer;
import org.primefaces.ui.util.ComponentUtils;

public class EditorRenderer extends CoreRenderer{
	
	public void decode(FacesContext facesContext, UIComponent component) {
		Editor editor = (Editor) component;
		String paramKey = editor.getClientId(facesContext) ;
		
		String submittedValue = (String) facesContext.getExternalContext().getRequestParameterMap().get(paramKey);
		editor.setSubmittedValue(submittedValue);
	}

	public void encodeEnd(FacesContext facesContext, UIComponent component) throws IOException {
		Editor editor = (Editor) component;

		encodeEditorWidget(facesContext, editor);
		encodeEditorMarkup(facesContext, editor);
	}
	
	private void encodeEditorMarkup(FacesContext facesContext, Editor editor) throws IOException{
		ResponseWriter writer = facesContext.getResponseWriter();
		String clientId = editor.getClientId(facesContext);
		String value = ComponentUtils.getStringValueToRender(facesContext, editor);
		
		writer.startElement("textarea", editor);
		writer.writeAttribute("id", clientId , null);
		writer.writeAttribute("name", clientId , null);
		if(value != null)
			writer.write(value);
		
		writer.endElement("textarea");
	}
	
	private void encodeEditorWidget(FacesContext facesContext, Editor editor) throws IOException{
		ResponseWriter writer = facesContext.getResponseWriter();
		String clientId = editor.getClientId(facesContext);
		
		String editorVariable = getEditorVar(editor);
		String cfgVariable = editorVariable + "_cfg";
		
		writer.startElement("script", editor);
		writer.writeAttribute("type", "text/javascript", null);
		
		writer.write("YAHOO.util.Event.onContentReady(\"" + clientId + "\", function() {\n");
		writer.write(cfgVariable + " = {\n");
		writer.write("width: '" + editor.getWidth() + "',");
		writer.write("height: '" + editor.getHeight() + "',");
		writer.write("handleSubmit : true");
		writer.write("};\n");
		writer.write(editorVariable + " = new YAHOO.widget.Editor(\"" + clientId + "\", " + cfgVariable + ");\n");
		writer.write(editorVariable + ".render();\n");
		writer.write("YAHOO.util.Event.onDOMReady(function(){ \n");
		writer.write("	if(! YAHOO.util.Dom.hasClass(document.body,'yui-skin-sam') )\n");
		writer.write("		YAHOO.util.Dom.addClass(document.body,'yui-skin-sam');\n");
		writer.write("	});");
		writer.write("});\n");
		
		writer.endElement("script");
	}
	
	private String getEditorVar(Editor editor) {
		if(editor.getWidgetVar() != null)
			return editor.getWidgetVar();
		else
			return "pf_editor_" + editor.getId();
	}
}