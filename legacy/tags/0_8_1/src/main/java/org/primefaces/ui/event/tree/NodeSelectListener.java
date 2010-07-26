package org.primefaces.ui.event.tree;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.FacesListener;

public interface NodeSelectListener extends FacesListener {

    public void processNodeSelect(NodeSelectEvent event) throws AbortProcessingException;
}
