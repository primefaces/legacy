package org.primefaces.ui.event.fileupload;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.FacesListener;

public interface FileUploadListener extends FacesListener {

	public void processFileUpload(FileUploadEvent event) throws AbortProcessingException;
}
