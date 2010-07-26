import org.primefaces.ui.event.fileupload.FileUploadEvent;

	public void broadcast(javax.faces.event.FacesEvent event) throws javax.faces.event.AbortProcessingException {
		super.broadcast(event);
		
		FacesContext facesContext = FacesContext.getCurrentInstance();
		MethodExpression me = getFileUploadListener();
		
		if (me != null) {
			me.invoke(facesContext.getELContext(), new Object[] {event});
		}
	}