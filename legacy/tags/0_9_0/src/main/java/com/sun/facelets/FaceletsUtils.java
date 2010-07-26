package com.sun.facelets;

import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

public class FaceletsUtils {
	
	/**
	 * Initializes the Facelets StateWriter
	 * 
	 * @param facesContext
	 */
	public static void initStateWriter(FacesContext facesContext) {
		ResponseWriter writer = facesContext.getResponseWriter();
		
		new StateWriter(writer, 1024);
	}
	
	/**
	 * Figures out if Facelets is used as the viewhandler
	 * 
	 * @param facesContext
	 */
	public static boolean isFaceletsEnabled(FacesContext facesContext) {
		String suffix = facesContext.getExternalContext().getInitParameter("javax.faces.DEFAULT_SUFFIX");
		
		if(suffix == null)
			return false;
		else 
			return suffix.equalsIgnoreCase(".xhtml");
	}
}
