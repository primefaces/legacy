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

import org.springframework.binding.convert.ConversionService;
import org.springframework.binding.expression.Expression;
import org.springframework.binding.expression.ExpressionParser;
import org.springframework.faces.webflow.FlowLifecycle;
import org.springframework.faces.webflow.JsfViewFactoryCreator;
import org.springframework.webflow.engine.builder.BinderConfiguration;

/**
 * Custom JsfViewFactoryCreator to generate PrimeFaces ViewFactory
 * 
 * Only used for Spring WebFlow Integration
 */
public class ViewFactoryCreator extends JsfViewFactoryCreator {

	private Lifecycle lifecycle;
	
	@Override
	public ViewFactory createViewFactory(Expression viewIdExpression, ExpressionParser expressionParser, ConversionService conversionService, BinderConfiguration binderConfiguration) {	
		return new ViewFactory(viewIdExpression, getLifecycle());
	}
	
	private Lifecycle getLifecycle() {
		if (lifecycle == null) {
			lifecycle = FlowLifecycle.newInstance();
		}
		return lifecycle;
	}
}