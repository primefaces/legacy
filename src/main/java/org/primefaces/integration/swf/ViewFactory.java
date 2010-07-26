/*
 * Copyright 2009-2010 Prime Technology.
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
package org.primefaces.integration.swf;

import javax.faces.lifecycle.Lifecycle;

import org.springframework.binding.expression.Expression;
import org.springframework.faces.webflow.JsfView;
import org.springframework.faces.webflow.JsfViewFactory;
import org.springframework.webflow.execution.RequestContext;
import org.springframework.webflow.execution.View;

/**
 * Custom JsfViewFactoryCreator to generate JSFViewWrappers
 * 
 * Only used for Spring WebFlow Integration
 */
public class ViewFactory extends JsfViewFactory {

	private Lifecycle lifecycle;
	
	public ViewFactory(Expression viewIdExpression, Lifecycle lifecycle) {
		super(viewIdExpression, lifecycle);
		
		this.lifecycle = lifecycle;
	}

	@Override
	public View getView(RequestContext context) {
		return new JSFViewWrapper((JsfView) super.getView(context), context, lifecycle);
	}
}