import org.primefaces.ui.event.tree.NodeSelectEvent;
import org.primefaces.ui.event.tree.NodeExpandEvent;
import org.primefaces.ui.event.tree.NodeCollapseEvent;

	public void broadcast(javax.faces.event.FacesEvent event) throws javax.faces.event.AbortProcessingException {
		super.broadcast(event);
		
		FacesContext facesContext = FacesContext.getCurrentInstance();
		MethodExpression me = null;

		if(event instanceof NodeSelectEvent) {
			me = getNodeSelectListener();
		} else if(event instanceof NodeExpandEvent) {
			me = getNodeExpandListener();
		} else if(event instanceof NodeCollapseEvent) {
			me = getNodeCollapseListener();
		}
		
		if (me != null) {
			me.invoke(facesContext.getELContext(), new Object[] {event});
		}
	}
	
	public void processPartialLifecycle(FacesContext facesContext) throws IOException {
		encodePartially(facesContext);
	}