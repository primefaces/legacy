if(PrimeFaces == undefined) var PrimeFaces = {};
if(PrimeFaces.widget == undefined) PrimeFaces.widget = {};

PrimeFaces.widget.TabViewUtils = {
	
	contentTransition : function(newTab, oldTab) { 
		if (newTab.anim && newTab.anim.isAnimated()) {
			newTab.anim.stop(true);
		}

		newTab.set('contentVisible', true);
		YAHOO.util.Dom.setStyle(newTab.get('contentEl'), 'opacity', 0);

		newTab.anim = newTab.anim || new YAHOO.util.Anim(newTab.get('contentEl'));
		newTab.anim.attributes.opacity = {
			to :1
		};

		var hideContent = function() {
			oldTab.set('contentVisible', false);
			oldTab.anim.onComplete.unsubscribe(hideContent);
		};

		oldTab.anim = oldTab.anim || new YAHOO.util.Anim(oldTab.get('contentEl'));
		oldTab.anim.onComplete.subscribe(hideContent, this, true);
		oldTab.anim.attributes.opacity = {
			to :0
		};

		newTab.anim.animate();
		oldTab.anim.animate();
	},
	
	addTabClickListener : function(tabview, hiddenInputId) {
		
		function handleClick(e, tabs) {
			var activeIndex = tabs.get('activeIndex'); 
			document.getElementById(hiddenInputId).value = activeIndex;
		}
		
		for (var i=0; i < tabview.get('tabs').length; i=i+1)
		    tabview.getTab(i).addListener('click',handleClick, tabview); 
	}
}