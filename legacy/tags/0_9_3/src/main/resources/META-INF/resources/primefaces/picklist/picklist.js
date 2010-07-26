if(PrimeFaces == undefined) var PrimeFaces = {};
if(PrimeFaces.widget == undefined) PrimeFaces.widget = {};

PrimeFaces.widget.PickList = function(id, config) {
	this.id = id;
	this.config = config;
	this.sourceId = PrimeFaces.core.Utils.escapeClientId(id + "_source");
	this.targetId = PrimeFaces.core.Utils.escapeClientId(id + "_target");
	this.sourceList = PrimeFaces.core.Utils.escapeClientId(id + "_sourceList");
	this.targetList = PrimeFaces.core.Utils.escapeClientId(id + "_targetList");
}

PrimeFaces.widget.PickList.prototype.add = function() {
	jQuery(this.sourceId + " > option:selected").appendTo(this.targetId);
	
	this.saveState();
}

PrimeFaces.widget.PickList.prototype.addAll = function() {
	jQuery(this.sourceId + " > option").appendTo(this.targetId);
	
	this.saveState();
}

PrimeFaces.widget.PickList.prototype.remove = function() {
	jQuery(this.targetId + " > option:selected").appendTo(this.sourceId);
	
	this.saveState();
}

PrimeFaces.widget.PickList.prototype.removeAll = function() {
	jQuery(this.targetId + " > option").appendTo(this.sourceId);
	
	this.saveState();
}

PrimeFaces.widget.PickList.prototype.saveState = function() {
	var sourceList = this.sourceList;
	var targetList = this.targetList;
	
	jQuery(sourceList).val('');
	jQuery(targetList).val('');
	
	jQuery(this.sourceId + ' > option').each(function(i) {
			jQuery(sourceList).val(jQuery(sourceList).val() + ";" + this.value); 
		}
	);
	
	jQuery(this.targetId + ' > option').each(function(i) {
			jQuery(targetList).val(jQuery(targetList).val() + ";" + this.value);  
	}
);
}