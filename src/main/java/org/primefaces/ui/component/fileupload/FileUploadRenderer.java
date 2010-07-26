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
package org.primefaces.ui.component.fileupload;

import java.io.IOException;

import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.apache.commons.fileupload.FileItem;
import org.primefaces.ui.event.fileupload.FileUploadEvent;
import org.primefaces.ui.model.fileupload.DefaultUploadedFile;
import org.primefaces.ui.model.fileupload.UploadedFile;
import org.primefaces.ui.renderkit.CoreRenderer;
import org.primefaces.ui.resource.ResourceUtils;
import org.primefaces.ui.util.ComponentUtils;
import org.primefaces.ui.webapp.MultipartRequest;

public class FileUploadRenderer extends CoreRenderer {
	
	public void decode(FacesContext facesContext, UIComponent component) {
		FileUpload fileUpload = (FileUpload) component;
		String clientId = fileUpload.getClientId(facesContext);
		
		if(facesContext.getExternalContext().getRequest() instanceof MultipartRequest) {
			MultipartRequest request = (MultipartRequest) facesContext.getExternalContext().getRequest();
			
			FileItem file = request.getFileItem(clientId);
			
			if(file != null) {
				UploadedFile uploadedFile = new DefaultUploadedFile(file);
				fileUpload.queueEvent(new FileUploadEvent(fileUpload, uploadedFile));
			}
		}
	}

	public void encodeEnd(FacesContext facesContext, UIComponent component) throws IOException {
		FileUpload fileUpload = (FileUpload) component;
		
		encodeFileUploadScript(facesContext, fileUpload);
		encodeFileUploadMarkup(facesContext, fileUpload);
	}

	private void encodeFileUploadScript(FacesContext facesContext, FileUpload fileUpload) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		String clientId = fileUpload.getClientId(facesContext);
		String actionURL = getActionURL(facesContext);
		String uploadVar = createUniqueWidgetVar(facesContext, fileUpload);
		
		UIComponent parentForm = ComponentUtils.findParentForm(facesContext, fileUpload);
		if(parentForm == null) {
			throw new FacesException("FileUpload component:" + clientId + " needs to be enclosed in a form");
		}
		String formClientId = parentForm.getClientId(facesContext);
		
		writer.startElement("script", null);
		writer.writeAttribute("type", "text/javascript", null);
		
		writer.write("YAHOO.widget.Uploader.SWFURL = '" + ResourceUtils.getResourceURL(facesContext, "/yui/uploader/assets/uploader.swf") + "';\n");
		
		writer.write("YAHOO.util.Event.onDOMReady(function () { \n");
		writer.write("var uiLayer = YAHOO.util.Dom.getRegion('" + clientId + "_browseTriggerContainer');\n");
		writer.write("var overlay = YAHOO.util.Dom.get('" + clientId + "_uploaderOverlay');\n");
		writer.write("YAHOO.util.Dom.setStyle(overlay, 'width', uiLayer.right-uiLayer.left + \"px\");\n");
		writer.write("YAHOO.util.Dom.setStyle(overlay, 'height', uiLayer.bottom-uiLayer.top + \"px\");");
		writer.write("});");
	
		writer.write("YAHOO.util.Event.onContentReady('" + clientId + "', function() {\n");	
		writer.write(uploadVar + " = new PrimeFaces.widget.Uploader('" + clientId + "_uploaderOverlay'" +
								", {url:'" + actionURL + "', clientId:'" + clientId + "', formClientId:'" + formClientId + "'" +
								", multiple:" + fileUpload.isMultiple() + ",tableId:'" + clientId + "_dataTable'");
		
		if(fileUpload.getUpdate() != null) {
			writer.write(", update:'" + fileUpload.getUpdate() + "'");
		}
		
		encodeFileFilters(facesContext, fileUpload);
		
		writer.write("});\n");						
		writer.write("});\n");
		
		writer.endElement("script");
	}

	private void encodeFileFilters(FacesContext facesContext, FileUpload fileUpload) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		boolean hasFilter = false;
		
		writer.write(",fileFilters:[");
		for(UIComponent child : fileUpload.getChildren()) {
			if(child instanceof FileFilter) {
				FileFilter filter = (FileFilter) child;
			
				if(hasFilter) {
					writer.write(",");
				}
					
				writer.write("{description:'" + filter.getName() + "',extensions:'" + filter.getExtensions() + "'}");
				hasFilter = true;
			}
		}
		writer.write("]");
		
	}

	private void encodeFileUploadMarkup(FacesContext facesContext, FileUpload fileUpload) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		String clientId = fileUpload.getClientId(facesContext);
		String uploadVar = createUniqueWidgetVar(facesContext, fileUpload);
		
		UIComponent uploadTrigger = fileUpload.getFacet("upload");
		ComponentUtils.decorateAttribute(uploadTrigger, "onclick", uploadVar + ".handleUpload();return false;");
		
		UIComponent clearTrigger = fileUpload.getFacet("clear");
		if(clearTrigger != null){
			ComponentUtils.decorateAttribute(clearTrigger, "onclick", uploadVar + ".handleClearFiles();return false;");
		}
		
		writer.startElement("div", null);
		writer.writeAttribute("id", clientId, "id");
		if(fileUpload.getStyle() != null) {
			writer.writeAttribute("style", fileUpload.getStyle(), null);
		}
		
		if(fileUpload.getStyleClass() != null) {
			writer.writeAttribute("class", fileUpload.getStyleClass(), null);
		}
		
		encodeBrowse(facesContext, fileUpload);
		encodeTrigger(facesContext, fileUpload, uploadTrigger, "uploadTriggerContainer", fileUpload.getUploadStyle(), fileUpload.getUploadStyleClass());
		
		if(clearTrigger != null) {
			encodeTrigger(facesContext, fileUpload, clearTrigger, "clearTriggerContainer", fileUpload.getClearStyle(), fileUpload.getClearStyleClass());
		}
			
		encodeDataTable(facesContext, fileUpload);
			
		writer.endElement("div");
	}
	
	private void encodeBrowse(FacesContext facesContext, FileUpload fileUpload) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		String clientId = fileUpload.getClientId(facesContext);
		
		writer.startElement("div", null);
		writer.writeAttribute("id", clientId + "_uploaderContainer", "id");
		
			if(fileUpload.getBrowseStyle() != null) {
				writer.writeAttribute("style", fileUpload.getBrowseStyle(), null);
			}
			
			if(fileUpload.getBrowseStyleClass() != null) {
				writer.writeAttribute("class", fileUpload.getBrowseStyleClass(), null);
			}
		
			writer.startElement("div", null);
			writer.writeAttribute("id", clientId + "_uploaderOverlay", null);
			writer.writeAttribute("style", "position:absolute; z-index:2", null);
			writer.endElement("div");
			
			writer.startElement("div", null);
			writer.writeAttribute("id", clientId + "_browseTriggerContainer", null);
			writer.writeAttribute("style", "z-index:1", null);
				renderChild(facesContext, fileUpload.getFacet("browse"));
			writer.endElement("div");
		
		writer.endElement("div");
	}
	
	private void encodeTrigger(FacesContext facesContext, FileUpload fileUpload, UIComponent trigger, String containerSuffix, String style, String styleClass) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		String clientId = fileUpload.getClientId(facesContext);
		
		writer.startElement("div", null);
		writer.writeAttribute("id", clientId + "_" + containerSuffix, "id");
		
		if(fileUpload.getClearStyle() != null)
			writer.writeAttribute("style", style, null);
			
		if(fileUpload.getClearStyleClass() != null)
			writer.writeAttribute("class", styleClass, null);

		renderChild(facesContext, trigger);
			
		writer.endElement("div");
	}
	
	private void encodeDataTable(FacesContext facesContext, FileUpload fileUpload) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		String clientId = fileUpload.getClientId(facesContext);
		
		writer.startElement("div", null);
		writer.writeAttribute("id", clientId + "_dataTable", null);
		writer.writeAttribute("style", "clear:both", null);
		writer.endElement("div");
	}
}