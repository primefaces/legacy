package org.primefaces.component.tree;

import java.util.List;

import org.primefaces.event.NodeCollapseEvent;
import org.primefaces.event.NodeExpandEvent;
import org.primefaces.event.NodeSelectEvent;

import com.sun.facelets.tag.MetaRuleset;
import com.sun.facelets.tag.MethodRule;
import com.sun.facelets.tag.jsf.ComponentConfig;
import com.sun.facelets.tag.jsf.ComponentHandler;

public class TreeHandler extends ComponentHandler{

	public TreeHandler(ComponentConfig config) {
		super(config);
	}
	
	@SuppressWarnings("unchecked")
	protected MetaRuleset createMetaRuleset(Class type) { 
		MetaRuleset metaRuleset = super.createMetaRuleset(type); 
		Class[] selectEventClasses = new Class[]{NodeSelectEvent.class};
		Class[] expandEventClasses = new Class[]{NodeExpandEvent.class};
		Class[] collapseEventClasses = new Class[]{NodeCollapseEvent.class};
		
		metaRuleset.addRule(new MethodRule("nodeSelectListener", List.class, selectEventClasses));
		metaRuleset.addRule(new MethodRule("nodeExpandListener", List.class, expandEventClasses));
		metaRuleset.addRule(new MethodRule("nodeCollapseListener", List.class, collapseEventClasses));
		
		return metaRuleset; 
	} 
}