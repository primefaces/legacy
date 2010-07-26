if(PrimeFaces == undefined) var PrimeFaces = {};
if(PrimeFaces.widget == undefined) PrimeFaces.widget = {};

PrimeFaces.widget.DataTable = function(clientId, columnDef, dataSource, cfg) {
	PrimeFaces.widget.DataTable.superclass.constructor.call(this, clientId + "_container", columnDef, dataSource, cfg);
	this.clientId = clientId;
	this.formId = cfg.formId;
	
	if(cfg.selectionMode != undefined) {
		this.rowSelectParam = this.clientId + "_selectedRows";
		
		this.subscribe("rowMouseoverEvent", this.onEventHighlightRow);
        this.subscribe("rowMouseoutEvent", this.onEventUnhighlightRow);
        this.subscribe("rowClickEvent", this.handleRowClickEvent);
	}
	
	if(cfg.columnSelectionMode != undefined) {
		this.rowSelectParam = this.clientId + "_selectedRows";
		this.selectedRows = new Array();

		if(cfg.columnSelectionMode == 'single')
			this.subscribe("radioClickEvent", this.handleRadioClickEvent);
		else if(cfg.columnSelectionMode == 'multiple')
				this.subscribe("checkboxClickEvent", this.handleCheckboxClickEvent);
	}
	
	 for(var i=0; i < columnDef.length; i++) {
		 if(columnDef[i].filter) {
			 var columnKey = dataSource.responseSchema.fields[i].key;
			 
			 var columnHeader = '#' + this.getId() + "-th-" + columnKey;
			 var columnSelector = this.getId() + "-col-" + columnKey;
			
			 jQuery(columnHeader).append('<input type="text" onkeyup="PrimeFaces.widget.DataTableUtils.filterColumn(\'' + this.clientId + '\', this.value, \'' + columnSelector + '\')"/>');
		 }
	 }
}

YAHOO.lang.extend(PrimeFaces.widget.DataTable, YAHOO.widget.DataTable,
{
	handleRowClickEvent : function(event, target) {
		this.onEventSelectRow(event, target);
		
		document.getElementById(this.rowSelectParam).value = this.getSelectedRows().join(',');
	},
	
	handleRadioClickEvent : function(args) {
		 var radio = args.target,
		 record = this.getRecord(radio);
		 
		 document.getElementById(this.rowSelectParam).value = record.getId();
	},
	
	handleCheckboxClickEvent : function(args) {
		var checkbox = args.target,
		record = this.getRecord(checkbox)

       	if(checkbox.checked)
       		this.selectedRows.push(record.getId());
       	else {
       		var index = jQuery.inArray(record.getId(), this.selectedRows);
       		
       		this.selectedRows.splice(index,1);
       	}
       	
       	document.getElementById(this.rowSelectParam).value = this.selectedRows.join(',');
	},
	
	filter : function(text) {
		var jqClientId = PrimeFaces.core.Utils.escapeClientId(this.clientId);
		
		jQuery(jqClientId + " table .yui-dt-data").find('tr').hide();
		jQuery(jqClientId + " table .yui-dt-data").find('td:contains("' + text + '")').parents('tr').show();
	}
});

PrimeFaces.widget.DataTableUtils = {
		
	filterColumn : function(id, text, column) {
		var jqClientId = PrimeFaces.core.Utils.escapeClientId(id);
		
		jQuery(jqClientId + " table .yui-dt-data").find('tr').hide();
		jQuery(jqClientId + " table .yui-dt-data").find('td.' + column + ':contains("' + text + '")').parents('tr').show();
	},
	
	loadDynamicData : function(state, dt) {
		var params = "ajaxSource=" + dt.clientId,
		viewstate = PrimeFaces.ajax.AjaxUtils.encodeViewState();
		
		params = params + "&primefacesAjaxRequest=true";
		params = params + "&javax.faces.ViewState=" + viewstate;
		params = params + "&" + PrimeFaces.ajax.AjaxUtils.getFormParam(dt.formId);
		params = params + "&first=" + state.pagination.recordOffset;
		
		return params;
	},
	
	loadInitialData : function(clientId, formId) {
		var params = "ajaxSource=" + clientId,
		viewstate = PrimeFaces.ajax.AjaxUtils.encodeViewState();
		
		params = params + "&primefacesAjaxRequest=true";
		params = params + "&javax.faces.ViewState=" + viewstate;
		params = params + "&" + PrimeFaces.ajax.AjaxUtils.getFormParam(formId);
		
		return params;
	}
};