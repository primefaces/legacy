package org.primefaces.ui.event.rating;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.FacesListener;

public interface RateEventListener extends FacesListener {

	public void processRateEvent(RateEvent rateEvent) throws AbortProcessingException;
}
