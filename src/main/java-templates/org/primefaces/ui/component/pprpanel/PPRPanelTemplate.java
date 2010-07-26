	public void processPartialLifecycle(FacesContext facesContext) throws IOException {
		//processDecodes(facesContext);
		//processValidators(facesContext);
		//processUpdates(facesContext);
		encodePartially(facesContext);
	}