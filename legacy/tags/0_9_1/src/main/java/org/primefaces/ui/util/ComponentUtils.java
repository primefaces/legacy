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
package org.primefaces.ui.util;

import javax.faces.component.EditableValueHolder;
import javax.faces.component.UIComponent;
import javax.faces.component.UIForm;
import javax.faces.component.ValueHolder;
import javax.faces.context.FacesContext;

public class ComponentUtils {
	
	private static String TRINIDAD_FORM_FAMILY = "org.apache.myfaces.trinidad.Form";
	
	public static UIComponent findComponentById(FacesContext context, UIComponent root, String id) {
		UIComponent component = null;
		
		for (int i = 0; i < root.getChildCount() && component == null; i++) {
			UIComponent child = (UIComponent) root.getChildren().get(i);
			component = findComponentById(context, child, id);
		}

		if (root.getId() != null) {
			if (component == null && root.getId().equals(id)) {
				component = root;
			}
		}
		return component;
	}
	
	/**
	 * Algorithm works as follows;
	 * - If the component is not a value holder, an exception is thrown
	 * - If it's an input component, submitted value is checked first since it'd be the value to be used in case validation erros
	 * terminates jsf lifecycle
	 * - Finally the value of the component is retrieved from backing bean and if there's a converter, converted value is returned
	 * 
	 * @param context			FacesContext instance
	 * @param component			UIComponent instance whose value will be returned
	 * @return					Value of the component
	 */
	public static String getStringValueToRender(FacesContext context, UIComponent component) {
		if (!(component instanceof ValueHolder))
			throw new IllegalArgumentException("Component : " + component.getId() + "is not a ValueHolder");

		if (component instanceof EditableValueHolder) {
			Object submittedValue = ((EditableValueHolder) component).getSubmittedValue();
			if (submittedValue != null) {
				return submittedValue.toString();
			}
		}

		Object value = ((ValueHolder) component).getValue();
		if(value == null)
			return null;
			
		if(((ValueHolder)component).getConverter() != null)
			return ((ValueHolder)component).getConverter().getAsString(context, component, value);
		else
			return value.toString();
	}

	public static boolean isValueEmpty(String value) {
		if (value == null || "".equals(value))
			return true;
		
		return false;
	}
	
	public static boolean isValueBlank(String value) {
		if(value == null)
			return true;
		
		return value.trim().equals("");
	}

	public static UIComponent findParentForm(FacesContext context, UIComponent component) {
		UIComponent parent = component.getParent();
		
		while(parent != null) {
			if(parent instanceof UIForm)
				return parent;
			
			if(parent.getFamily().equals(TRINIDAD_FORM_FAMILY))
				return parent;
			
			parent = parent.getParent();
		}
		
		return null;
	}
	
	public static void decorateAttribute(UIComponent component, String attribute, String value) {
		String attributeValue = (String) component.getAttributes().get(attribute);
		
		if(attributeValue != null) {
			if(attributeValue.indexOf(value) == -1) {
				String decoratedValue = attributeValue + ";" + value;
				
				component.getAttributes().put(attribute, decoratedValue);
			}
		} else {
				component.getAttributes().put(attribute, value);
		}
	}
}