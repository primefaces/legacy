package org.primefaces.component.column;

import com.sun.facelets.tag.MetaRule;
import com.sun.facelets.tag.MetaRuleset;
import com.sun.facelets.tag.MethodRule;
import com.sun.facelets.tag.jsf.ComponentConfig;
import com.sun.facelets.tag.jsf.ComponentHandler;

public class ColumnHandler extends ComponentHandler {

	public ColumnHandler(ComponentConfig config) {
		super(config);
	}
	
	@SuppressWarnings("unchecked")
	protected MetaRuleset createMetaRuleset(Class type) { 
		MetaRuleset metaRuleset = super.createMetaRuleset(type); 
		Class[] paramList = new Class[]{Object.class, Object.class}; 
		
		MetaRule metaRule = new MethodRule("sortFunction", Integer.class, paramList); 
		metaRuleset.addRule(metaRule);
		
		return metaRuleset; 
	} 
}