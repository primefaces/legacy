import java.util.Iterator;
import org.primefaces.model.LazyDataModel;
import javax.el.ValueExpression;
import javax.faces.component.UIComponent;
import javax.faces.component.UIColumn;
import javax.faces.model.DataModel;
import javax.el.ValueExpression;
import org.primefaces.component.column.Column;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.lang.StringBuffer;

	public void processDecodes(FacesContext facesContext) {
		if(isDynamic()) {
			super.processDecodes(facesContext);
		} else {
			int originalRows = getRows();
			setRows(getRowCount());
			super.processDecodes(facesContext);
			setRows(originalRows);
			
			//Update current page state for client side datatable
			Map<String,String> params = facesContext.getExternalContext().getRequestParameterMap();
			String clientId = getClientId(facesContext);
			String pageParam = clientId + "_page";
			
			if(isPaginator() && params.containsKey(pageParam)) {
				int page = Integer.parseInt(params.get(pageParam));
				setPage(page);
			}
		}
    }
	
	public void processUpdates(FacesContext isAjaxDataRequest) {
		super.processUpdates(isAjaxDataRequest);
		
		if(!isAjaxDataRequest(isAjaxDataRequest)) {
			ValueExpression selectionVE = this.getValueExpression("selection");
			if(selectionVE != null) {
				selectionVE.setValue(isAjaxDataRequest.getELContext(), this._selection);
				this._selection = null;
			}
		}
	}

	void loadLazyData() {
		DataModel model = getDataModel();
		if(model instanceof LazyDataModel) {
			LazyDataModel lazyModel = (LazyDataModel) model;
			lazyModel.setPageSize(getRows());
			lazyModel.setWrappedData(lazyModel.fetchLazyData(getFirst(), getRows()));
		}
	}
	
	private Map<String,ValueExpression> filterMap;
	
	public Map<String,ValueExpression> getFilterMap() {
		if(filterMap == null) {
			filterMap = new HashMap<String,ValueExpression>();
			
			for(Iterator<UIComponent> children = getChildren().iterator(); children.hasNext();) {
				UIComponent kid = children.next();
					
				if(kid.isRendered() && kid instanceof Column) {
					Column column = (Column) kid;
					
					if(column.getValueExpression("filterBy") != null) {
						filterMap.put(column.getClientId(FacesContext.getCurrentInstance()), column.getValueExpression("filterBy"));
					}
				}
			}
		}

		return filterMap;
	}
	
	public boolean hasFilter() {
		return getFilterMap().size() > 0;
	}
	
	public boolean isAjaxDataRequest(FacesContext facesContext) {
		return facesContext.getExternalContext().getRequestParameterMap().containsKey(this.getClientId(facesContext) + "_ajaxData");
	}
	
	private List<String> selected= new ArrayList<String>();
	
	public List<String> getSelected() {
		return selected;
	}
	
	public void setSelected(List<String> selected) {
		this.selected = selected;
	}
	
	public boolean isSelectionEnabled() {
		return this.getSelectionMode() != null;
	}
	
	public boolean isSingleSelectionMode() {
		String selectionMode = this.getSelectionMode();
		
		if(selectionMode != null)
			return selectionMode.equals("single") || selectionMode.equals("singlecell");
		else
			return false;
	}
	
	public boolean isCellSelection() {
		String selectionMode = this.getSelectionMode();
		
		if(selectionMode != null)
			return selectionMode.indexOf("cell") != -1;
		else
			return false;
	}
	
	public String getSelectedAsString() {
		StringBuffer buffer = new StringBuffer();
		for(Iterator<String> iter = selected.iterator();iter.hasNext();) {
			buffer.append(iter.next());
			
			if(iter.hasNext())
				buffer.append(",");
		}
		
		return buffer.toString();
	}
	
	 public UIColumn getColumnByClientId(String clientId) {
	    	FacesContext facesContext = FacesContext.getCurrentInstance();
	    	
	    	for(UIComponent kid : getChildren()) {
	    		if(kid.getClientId(facesContext).equals(clientId))
	    			return (UIColumn) kid;
	    	}
	    	
	    	return null;
	    }