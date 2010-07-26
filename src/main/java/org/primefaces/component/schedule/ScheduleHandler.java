/*
 * Copyright 2009 Prime Technology.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.primefaces.component.schedule;

import org.primefaces.event.ScheduleDateSelectEvent;
import org.primefaces.event.ScheduleEntrySelectEvent;

import com.sun.facelets.tag.MetaRuleset;
import com.sun.facelets.tag.MethodRule;
import com.sun.facelets.tag.jsf.ComponentConfig;
import com.sun.facelets.tag.jsf.ComponentHandler;

public class ScheduleHandler extends ComponentHandler{

	public ScheduleHandler(ComponentConfig config) {
		super(config);
	}
	
	@SuppressWarnings("unchecked")
	protected MetaRuleset createMetaRuleset(Class type) { 
		MetaRuleset metaRuleset = super.createMetaRuleset(type); 
		Class[] scheduleEntrySelectEventClass = new Class[]{ScheduleEntrySelectEvent.class};
		Class[] scheduleDateSelectEventClass = new Class[]{ScheduleDateSelectEvent.class};
		
		metaRuleset.addRule(new MethodRule("eventSelectListener", null, scheduleEntrySelectEventClass));
		metaRuleset.addRule(new MethodRule("dateSelectListener", null, scheduleDateSelectEventClass));
		
		return metaRuleset; 
	} 	
}
