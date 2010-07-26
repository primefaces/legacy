import org.primefaces.event.NodeSelectEvent;
import org.primefaces.event.NodeExpandEvent;
import org.primefaces.event.NodeCollapseEvent;
import javax.faces.component.UIComponent;
import java.util.Map;
import java.util.HashMap;
import org.primefaces.model.TreeNode;

	private Map<String,UITreeNode> nodes;

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
	
	public UITreeNode getUITreeNodeByType(String type) {
		if(nodes == null) {
			nodes = new HashMap<String,UITreeNode>();
			for(UIComponent child : getChildren()) {
				UITreeNode uiTreeNode = (UITreeNode) child;
				
				nodes.put(uiTreeNode.getType(), uiTreeNode);
			}
		}
		
		UITreeNode node = nodes.get(type);
		
		if(node == null)
			throw new javax.faces.FacesException("Unsupported tree node type:" + type);
		else
			return node;
	}
	
	public boolean hasSelection() {
		return getValueExpression("selection") != null;
	}
	
	public void processUpdates(FacesContext context) {
		super.processUpdates(context);
		TreeNode[] selection = this.getSelection();
		
		if(selection != null) {
			this.getValueExpression("selection").setValue(context.getELContext(), selection);
			setSelection(null);
		}
	}