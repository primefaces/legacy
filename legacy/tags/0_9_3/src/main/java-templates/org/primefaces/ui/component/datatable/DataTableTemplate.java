import java.util.Iterator;
import org.primefaces.ui.model.data.LazyDataModel;

import javax.faces.component.UIComponent;

import org.primefaces.ui.component.datatable.Column;

	public void processDecodes(FacesContext context) {
		//Hack to make UIData process all rows for client side paging mode
		int originalRows = getRows();
		setRows(getRowCount());
		super.processDecodes(context);
		setRows(originalRows);
    }
	
	public void processUpdates(FacesContext context) {
		super.processUpdates(context);
		
		if(this._selection != null)
			this.getValueExpression("selection").setValue(context.getELContext(), this._selection);
	}
	
	private String columnSelectionMode = null;
	
	public String getColumnSelectionMode() {
		if(columnSelectionMode == null) { 
			for(Iterator<javax.faces.component.UIComponent> children = getChildren().iterator(); children.hasNext();) {
				javax.faces.component.UIComponent kid = children.next();
				
				if(kid.isRendered() && kid instanceof Column) {
					Column column = (Column) kid;
					
					if(column.getSelectionMode() != null) {
						columnSelectionMode = column.getSelectionMode();
					}
				}
			}
		}
		
		return columnSelectionMode;
	}
	
	void loadLazyData() {
		LazyDataModel lazyModel = (LazyDataModel) getDataModel();
		lazyModel.setPageSize(getRows());
		
		lazyModel.setWrappedData(lazyModel.fetchLazyData(getFirst(), getRows()));
	}