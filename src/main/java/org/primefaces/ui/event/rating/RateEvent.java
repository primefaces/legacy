package org.primefaces.ui.event.rating;

import javax.faces.component.UIComponent;
import javax.faces.event.FacesEvent;
import javax.faces.event.FacesListener;

public class RateEvent extends FacesEvent {

	private double rating;

	public RateEvent(UIComponent component) {
		super(component);
	}
	
	public RateEvent(UIComponent component, double rating) {
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
	
	public double getRating() {
		return rating;
	}

	public void setRating(double rating) {
		this.rating = rating;
	}
}