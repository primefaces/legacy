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
package org.primefaces.ui.component.export;

import java.io.IOException;

import javax.el.ELException;
import javax.el.MethodExpression;
import javax.el.ValueExpression;
import javax.faces.FacesException;
import javax.faces.component.ActionSource;
import javax.faces.component.UIComponent;

import com.sun.facelets.FaceletContext;
import com.sun.facelets.FaceletException;
import com.sun.facelets.tag.TagAttribute;
import com.sun.facelets.tag.TagConfig;
import com.sun.facelets.tag.TagHandler;
import com.sun.facelets.tag.jsf.ComponentSupport;

public class DataExporterTagHandler extends TagHandler {

	private final TagAttribute target;
	private final TagAttribute type;
	private final TagAttribute fileName;
	private final TagAttribute excludeColumns;
	private final TagAttribute preProcessor;
	private final TagAttribute postProcessor;

	public DataExporterTagHandler(TagConfig tagConfig) {
		super(tagConfig);
		this.target = getRequiredAttribute("target");
		this.type = getRequiredAttribute("type");
		this.fileName = getRequiredAttribute("fileName");
		this.excludeColumns = getAttribute("excludeColumns");
		this.preProcessor = getAttribute("preProcessor");
		this.postProcessor = getAttribute("postProcessor");
	}

	public void apply(FaceletContext faceletContext, UIComponent parent) throws IOException, FacesException, FaceletException, ELException {
		if (ComponentSupport.isNew(parent)) {
			ValueExpression targetVE = target.getValueExpression(faceletContext, Object.class);
			ValueExpression typeVE = type.getValueExpression(faceletContext, Object.class);
			ValueExpression fileNameVE = fileName.getValueExpression(faceletContext, Object.class);
			ValueExpression excludeColumnsVE = null;
			MethodExpression preProcessorME = null;
			MethodExpression postProcessorME = null;
			
			if(excludeColumns != null) {
				excludeColumnsVE = excludeColumns.getValueExpression(faceletContext, Object.class);
			}
			if(preProcessor != null) {
				preProcessorME = preProcessor.getMethodExpression(faceletContext, null, new Class[]{Object.class});
			}
			if(postProcessor != null) {
				postProcessorME = postProcessor.getMethodExpression(faceletContext, null, new Class[]{Object.class});
			}
			
			ActionSource actionSource = (ActionSource) parent;
			actionSource.addActionListener(new DataExporter(targetVE, typeVE, fileNameVE, excludeColumnsVE, preProcessorME, postProcessorME));
		}
	}

}

