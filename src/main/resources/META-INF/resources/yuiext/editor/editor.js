if(PrimeFaces == undefined) var PrimeFaces = {};
if(PrimeFaces.widget == undefined) PrimeFaces.widget = {};

PrimeFaces.widget.Editor = function(id, config) {
	PrimeFaces.widget.Editor.superclass.constructor.call(this, id, config);
	
	if(config.resizable)
		this.setupResize(config.widthHeightController);
}

YAHOO.lang.extend(PrimeFaces.widget.Editor, YAHOO.widget.Editor,
{
	setupResize : function(widthHeightController) {
		this.on('editorContentLoaded', function() {
			
	        resize = new YAHOO.util.Resize(this.get('element_cont').get('element'), {
	            handles: ['br'],
	            autoRatio: true,
	            status: true,
	            proxy: true,
	            setSize: false
	        });

	        resize.on('startResize', function() {
	            this.hide();
	            this.set('disabled', true);
	        }, this, true);

	        resize.on('resize', function(args) {
	            var h = args.height;
	            var th = (this.toolbar.get('element').clientHeight + 2);
	            var dh = (this.dompath.clientHeight + 1);
	            var newH = (h - th - dh);
	            this.set('width', args.width + 'px');
	            this.set('height', newH + 'px');
	            this.set('disabled', false);
	            widthHeightController.value = args.width + ',' + newH;
	            this.show();
	        }, this, true);
	    });

	}
});