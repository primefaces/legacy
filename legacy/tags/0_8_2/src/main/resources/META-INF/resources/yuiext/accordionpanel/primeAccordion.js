if(PrimeFaces == undefined) var PrimeFaces = {};
if(PrimeFaces.widget == undefined) PrimeFaces.widget = {};

PrimeFaces.widget.AccordionUtils = {

	addAccordionPanelChanged : function( hiddenInputId,index,multipleSelect) {
		var hiddenText = document.getElementById(hiddenInputId) 
		var selectedIndex = '-' + index + '-';
		var values = hiddenText.value;
		if( values != null || values != '' ){
			// if it is a close operation, the value is already in hidden input's value
			if( values.indexOf( selectedIndex ) > -1 ) 
				hiddenText.value = values.replace(selectedIndex, '');
			else{
				if( multipleSelect)
					hiddenText.value = hiddenText.value + selectedIndex;
				else
					hiddenText.value = selectedIndex;
			}
		}else{
			hiddenText.value = selectedIndex;		
		}
	}
};