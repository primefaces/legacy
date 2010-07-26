package org.primefaces.event;

import javax.faces.component.UIComponent;
import javax.faces.event.FacesEvent;
import javax.faces.event.FacesListener;

public class RateEvent extends FacesEvent {

	private Double rating;

	public RateEvent(UIComponent component) {
		super(component);
	}
	
	public RateEvent(UIComponent component, Double rating) {
		super(component);
		this.rating = rating;
	}

	@Override
	public boolean isAppropriateListener(FacesListener listener) {
		return (listener instanceof RateEventListener);
	}

	@Override
	public void processListener(FacesListener listener) {
		((RateEventListener) listener).processRateEvent(this);
	}
	
	public Double getRating() {
		return rating;
	}

	public void setRating(Double rating) {
		this.rating = rating;
	}
}