package org.primefaces.ui.event.fileupload;

import javax.faces.component.UIComponent;
import javax.faces.event.FacesEvent;
import javax.faces.event.FacesListener;

import org.primefaces.ui.model.fileupload.UploadedFile;

public class FileUploadEvent extends FacesEvent {

	private UploadedFile file;

	public FileUploadEvent(UIComponent component, UploadedFile file) {
		super(component);
		this.file = file;
	}

	@Override
	public boolean isAppropriateListener(FacesListener listener) {
		return (listener instanceof FileUploadListener);
	}

	@Override
	public void processListener(FacesListener listener) {
		((FileUploadListener) listener).processFileUpload(this);
	}
	
	public UploadedFile getFile() {
		return file;
	}
}
