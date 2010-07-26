
	public void processDecodes(FacesContext context) {
		//Hack to make UIData process all rows for client side paging mode
		int originalRows = getRows();
		setRows(getRowCount());
		super.processDecodes(context);
		setRows(originalRows);
    }