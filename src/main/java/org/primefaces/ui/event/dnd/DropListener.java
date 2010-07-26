package org.primefaces.ui.event.dnd;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.FacesListener;

public interface DropListener extends FacesListener {

	public void processDragDrop(DragDropEvent event) throws AbortProcessingException;
}
