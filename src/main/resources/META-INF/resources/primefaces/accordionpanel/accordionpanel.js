if(PrimeFaces == undefined) var PrimeFaces = {};
if(PrimeFaces.widget == undefined) PrimeFaces.widget = {};

PrimeFaces.widget.AccordionPanel = function(clientId, tabs, cfg) {
	this.clientId = clientId;
	this.tabs = tabs;
	this.cfg = cfg;
}

PrimeFaces.widget.AccordionPanel.prototype.toggle = function(tabId, toggler) {
	var tabIndex = this.getTabIndex(tabId);
	var tab = this.getTab(tabIndex);
	var escapedTabId = PrimeFaces.core.Utils.escapeClientId(tab.id);
	
	jQuery(escapedTabId + ' .pf-accordiontab-bd').slideToggle(this.cfg.speed);
		
	if(tab.isExpanded()) {
		jQuery(toggler).removeClass('pf-accordiontab-toggler-expanded').addClass('pf-accordiontab-toggler-collapsed');
		tab.setExpanded(false);
	}
	else {
		jQuery(toggler).removeClass('pf-accordiontab-toggler-collapsed').addClass('pf-accordiontab-toggler-expanded');
		tab.setExpanded(true);		
	}
	
	if(!this.isMultipleSelection()) {				
		//close all other expanded tabs
		for(var i=0; i < this.tabs.length; i++) {
			var currentTab = this.tabs[i];
			
			if(currentTab != tab && currentTab.isExpanded()) {
				var escapedCurrentTabId = PrimeFaces.core.Utils.escapeClientId(currentTab.id);
				
				jQuery(escapedCurrentTabId + ' .pf-accordiontab-bd').slideUp(this.cfg.speed);
				jQuery(escapedCurrentTabId).children('.pf-accordiontab-toggler-expanded').removeClass('pf-accordiontab-toggler-expanded').addClass('pf-accordiontab-toggler-collapsed');
				currentTab.setExpanded(false);
			}
		}
	}
	
	//save tab states
	document.getElementById(this.clientId + '_state').value = this.getActiveTabIndexes();
}

PrimeFaces.widget.AccordionPanel.prototype.getTabIndex = function(tabId) {
	for(var i=0; i < this.tabs.length; i++) {
		if(this.tabs[i].id == tabId)
			return i;
	}
	
	return -1;
}

PrimeFaces.widget.AccordionPanel.prototype.getTab = function(index) {
	return this.tabs[index];
}

PrimeFaces.widget.AccordionPanel.prototype.isMultipleSelection = function() {
	return this.cfg.multipleSelection;
}

PrimeFaces.widget.AccordionPanel.prototype.getActiveTabIndexes = function() {
	var indexes = "";
	
	for(var i=0; i < this.tabs.length; i++) {
		if(this.tabs[i].isExpanded()) {
			if(indexes.length == 0)
				indexes = i;
			else
				indexes = indexes + "," + i;
		}
			
			
	}
	
	return indexes;
}

PrimeFaces.widget.AccordionTab = function(id, cfg) {
	this.id = id;
	this.cfg = cfg;
}

PrimeFaces.widget.AccordionTab.prototype.isExpanded = function() {
	return this.cfg.expanded;
}

PrimeFaces.widget.AccordionTab.prototype.setExpanded = function(value) {
	return this.cfg.expanded = value;
}