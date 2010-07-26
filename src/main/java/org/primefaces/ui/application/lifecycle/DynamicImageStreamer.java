package org.primefaces.ui.application.lifecycle;

import java.io.IOException;
import java.util.Map;

import javax.el.ELContext;
import javax.el.ValueExpression;
import javax.faces.FacesException;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;
import javax.servlet.http.HttpServletResponse;

import org.primefaces.ui.model.io.StreamedContent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DynamicImageStreamer implements PhaseListener {

	public final static String DYNAMICIMAGE_PARAM = "primefacesDynamicImage";
	public final static String CONTENTTYPE_PARAM = "contentType";
	
	private Logger logger = LoggerFactory.getLogger(DynamicImageStreamer.class);

	public void afterPhase(PhaseEvent phaseEvent) {
		FacesContext facesContext = phaseEvent.getFacesContext();
		Map<String,String> params = facesContext.getExternalContext().getRequestParameterMap();
		
		if(params.containsKey(DYNAMICIMAGE_PARAM)) {
			ELContext elContext = facesContext.getELContext();
			String expression = params.get(DYNAMICIMAGE_PARAM);
			ValueExpression ve = facesContext.getApplication().getExpressionFactory().createValueExpression(elContext, "#{" + expression + "}", StreamedContent.class);
			StreamedContent content = (StreamedContent) ve.getValue(elContext);
			
			if(content != null) {
				logger.debug("Streaming image {}", ve.getExpressionString());
			
				HttpServletResponse response = (HttpServletResponse) facesContext.getExternalContext().getResponse();	//TODO: Portlet support
			
				try {
					response.setContentType(content.getContentType());
					
					byte[] buffer = new byte[2048];
			
					int length;
					while ((length = (content.getStream().read(buffer))) >= 0) {
						response.getOutputStream().write(buffer, 0, length);
					}
					
					response.setStatus(200);
					content.getStream().close();
					response.getOutputStream().flush();
					facesContext.responseComplete();
				}catch (IOException e) {
					logger.error("IO Error in streaming image {}", ve.getExpressionString());
					logger.error(e.getMessage());
				}
			}
		}
	}
	
	public void beforePhase(PhaseEvent phaseEvent) {
		//Nothing to do here
	}

	public PhaseId getPhaseId() {
		return PhaseId.RESTORE_VIEW;
	}
}