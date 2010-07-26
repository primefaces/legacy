if(PrimeFaces == undefined) var PrimeFaces = {};
if(PrimeFaces.widget == undefined) PrimeFaces.widget = {};

PrimeFaces.widget.Uploader = function(containerId, cfg) {
	PrimeFaces.widget.Uploader.superclass.constructor.call(this, containerId, null, false);
	this.cfg = cfg;
	this.addListener('contentReady', this.handleContentReady); 
	this.addListener('fileSelect', this.handleFileSelect); 
	this.addListener('uploadStart', this.handleUploadStart); 
	this.addListener('uploadProgress', this.handleUploadProgress);
	this.addListener('uploadCancel', this.handleUploadCancel);
	this.addListener('uploadComplete', this.handleUploadComplete); 
	this.addListener('uploadCompleteData', this.handleUploadCompleteData);
	this.addListener('uploadError', this.handleUploadError);
}

YAHOO.lang.extend(PrimeFaces.widget.Uploader, YAHOO.widget.Uploader,
{
	files: {},
	
	handleContentReady : function(event) {
		this.setAllowMultipleFiles(this.cfg.multiple);
		this.createDataTable();
		
		if(this.cfg.fileFilters.length > 0)
			this.setFileFilters(this.cfg.fileFilters);
	},
	
	handleFileSelect : function(event) {
		if(!this.isMultipleUpload()) {
			this.clearFilesInTable();
			this.files = {};
		}
		
		for(var item in event.fileList) {
			var fileItem = event.fileList[item];
			
			if(this.files[fileItem.id] == undefined) {
				var status = "<div id=\"" + this.cfg.clientId + "_" +  fileItem.id + "\">0%</div>"; 
				this.files[fileItem.id] = {id:fileItem.id, name:fileItem.name, progress:status, uploaded:false};
				this.dataTable.addRow(this.files[fileItem.id]);
			}
		}
		
		jQuery(PrimeFaces.core.Utils.escapeClientId(this.cfg.tableId)).show();
	},
	
	handleUpload : function() {
		var clientId = this.cfg.clientId;
		var viewstate = document.getElementById('javax.faces.ViewState').value;
		var formClientId = this.cfg.formClientId;
		var jsessionid = YAHOO.util.Cookie.get("JSESSIONID");
		var url = this.cfg.url;
		
		var uploadParams = {
				'javax.faces.ViewState' : viewstate,
				'primefacesAjaxRequest' : true
		};
		
		var isMyfaces = document.getElementsByName("pf-fileupload-form_SUBMIT").length == 1;
		if(isMyfaces)
			uploadParams['pf-fileupload-form_SUBMIT'] = 1;
		else
			uploadParams['pf-fileupload-form'] = 'pf-fileupload-form';
		
		if(this.cfg.update != undefined) {
			uploadParams['update'] = this.cfg.update;
		}
		
		if(url.indexOf('jsessionid') == -1) {
			url = url + ";jsessionid="+ jsessionid;
		}
		
		for(var item in this.files) {
			var file = this.files[item];
			
			if(!file.uploaded) {
				this.upload(file.id, url, 'POST', uploadParams, clientId);
				file.uploaded = true;
			}
		}
	},
	
	handleUploadProgress : function(event) {
		var percentage = Math.round(100*(event["bytesLoaded"]/event["bytesTotal"]));
		var progressIndicator = document.getElementById(this.cfg.clientId + "_" + event["id"]);
		progressIndicator.innerHTML = percentage + "%";
	},
	
	handleUploadCancel: function(event) {
		
	},
	
	handleUploadError : function(event) {
		
	},
	
	handleUploadStart : function(event) {
		
	},
	
	handleUploadComplete : function(event) {
		
	},
	
	handleUploadCompleteData : function(event) {
		if(this.cfg.update != undefined) {
			var responseXML = {};
			responseXML.documentElement = new DOMParser().parseFromString(event.data,"text/xml");

			PrimeFaces.ajax.AjaxResponse(responseXML);
		}
	},
	
	handleClearFiles : function() {
		this.clearFilesInTable();
		this.files = {};
		this.clearFileList();
	},
	
	createDataTable : function() {
		var tableContainerId = this.cfg.tableId;
		var columnDefs = [
		        {key:"name", label: "File Name"},
		        {key:"progress", label: "Progress"}
		    ];
		
		var datasource = new YAHOO.util.DataSource([]);
		datasource.responseType = YAHOO.util.DataSource.TYPE_JSARRAY;
		datasource.responseSchema = {
		     fields: ["id","name", "progress"]
		 };
		
		this.dataTable = new YAHOO.widget.DataTable(tableContainerId, columnDefs, datasource);
		jQuery(PrimeFaces.core.Utils.escapeClientId(tableContainerId)).hide();
		jQuery(PrimeFaces.core.Utils.escapeClientId(tableContainerId) + " thead").css("display","none");	//hide header
	},
	
	clearFilesInTable : function() {
		jQuery(PrimeFaces.core.Utils.escapeClientId(this.cfg.tableId)).hide();
		this.dataTable.deleteRows(0, this.getFileCount());
	},
	
	isMultipleUpload : function() {
		return this.cfg.multiple;
	},
	
	getFileCount : function() {
		var count = 0;
		
		for(var item in this.files) {
			count++;
		}
		
		return count;
	},
	
	getFormSubmitMarker : function() {
		var formClientId = this.cfg.formClientId;
		var myfacesFormSubmitMarker = formClientId + "_SUBMIT";
		
		
		
		if(isMyfaces)
			return "1"; 
		else
			return formClientId;
	}
});