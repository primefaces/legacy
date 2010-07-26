package org.primefaces.ui.event.idlemonitor;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.FacesListener;

public interface IdleEventListener extends FacesListener {

	public void processIdleEvent(IdleEvent idleEvent) throws AbortProcessingException;
}
