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
package org.primefaces.ui.component.imagecropper;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.convert.ConverterException;
import javax.imageio.ImageIO;
import javax.servlet.ServletContext;

import org.primefaces.ui.renderkit.CoreRenderer;

public class ImageCropperRenderer extends CoreRenderer{
	
	public void decode(FacesContext facesContext, UIComponent component) {
		String clientId = component.getClientId(facesContext);
		String submittedValue = facesContext.getExternalContext().getRequestParameterMap().get(clientId );
		
		((ImageCropper) component).setSubmittedValue(submittedValue);
	}

	public void encodeEnd(FacesContext facesContext, UIComponent component) throws IOException {
		ImageCropper cropper = (ImageCropper) component;

		encodeImageCropperMarkup(facesContext, cropper);
		encodeImageCropperScript(facesContext, cropper);
	}

	private void encodeImageCropperScript(FacesContext facesContext, ImageCropper cropper) throws IOException{
		ResponseWriter writer = facesContext.getResponseWriter();
		String cropperVar = createUniqueWidgetVar(facesContext, cropper);
		String clientId = cropper.getClientId(facesContext);

		writer.startElement("script", null);
		writer.writeAttribute("type", "text/javascript", null);		
		writer.write("YAHOO.util.Event.addListener(window, \"load\", function(e) {\n");
		writer.write(cropperVar + " = new YAHOO.widget.ImageCropper(\"" + clientId + ":img\"");

		if(cropper.getValue() != null) {
			writer.write(",{");
			CroppedImage croppedImage = (CroppedImage) cropper.getValue();
			writer.write("initialXY:[" + croppedImage.getLeft() + "," + croppedImage.getTop() + "],");
			writer.write("initWidth:" + croppedImage.getWidth() + ",");
			writer.write("initHeight:" + croppedImage.getHeight());
			writer.write("}");
		}
		writer.write(");\n");

		writer.write(cropperVar + ".on(\"moveEvent\", PrimeFaces.widget.ImageCropperUtils.attachedCroppedArea, {hiddenFieldId:\"" + clientId + "\"});\n");

		writer.write("});\n");

		writer.endElement("script");
	}
	
	private void encodeImageCropperMarkup(FacesContext facesContext, ImageCropper cropper) throws IOException{
		ResponseWriter writer = facesContext.getResponseWriter();
		String clientId = cropper.getClientId(facesContext);
		renderImage(facesContext, cropper, clientId);
		
		writer.startElement("input", null);
		writer.writeAttribute("type", "hidden", null);
		writer.writeAttribute("id", clientId, null);
		writer.writeAttribute("name", clientId, null);
		writer.writeAttribute("value", "", null);
		writer.endElement("input");
		
		writer.endElement("div");
	}
	
	public Object getConvertedValue(FacesContext facesContext, UIComponent component, Object submittedValue) throws ConverterException {
		ImageCropper cropper = (ImageCropper) component;
		String[] cropCoords = ((String)submittedValue).split("_");
		
		int y = Integer.parseInt(cropCoords[0]);
		int x = Integer.parseInt(cropCoords[1]);
		int w = Integer.parseInt(cropCoords[2]);
		int h = Integer.parseInt(cropCoords[3]);
		
		String format = getFormat(cropper.getImage());
		
		ServletContext servletContext = (ServletContext) facesContext.getExternalContext().getContext();
		String imagePath = servletContext.getRealPath("") + File.separator + cropper.getImage();
		
        BufferedImage outputImage;
		try {
			outputImage = ImageIO.read(new File(imagePath));
			BufferedImage cropped = outputImage.getSubimage(x, y, w, h);
			
			ByteArrayOutputStream croppedOutImage = new ByteArrayOutputStream();
	        ImageIO.write(cropped, format, croppedOutImage);
	        
	        return new CroppedImage(cropper.getImage(), croppedOutImage.toByteArray(), x, y, w, h);
		} catch (IOException e) {
			throw new ConverterException(e);
		}
	}

	private void renderImage(FacesContext facesContext, ImageCropper cropper, String clientId) throws IOException{
		ResponseWriter writer = facesContext.getResponseWriter();
		
		writer.startElement("img", null);
		writer.writeAttribute("id", clientId + ":img", null);
		writer.writeAttribute("src", facesContext.getExternalContext().getRequestContextPath() + File.separator + cropper.getImage(), null);
		writer.endElement("img");
	}
	
	private String getFormat(String path) {
		String[] pathTokens = path.split("\\.");
		
		return pathTokens[pathTokens.length -1];
	}
}