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
package org.primefaces.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.el.ValueExpression;
import javax.faces.component.EditableValueHolder;
import javax.faces.component.UIComponent;
import javax.faces.component.UIForm;
import javax.faces.component.UISelectItem;
import javax.faces.component.UISelectItems;
import javax.faces.component.ValueHolder;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.model.SelectItem;

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
	 * - If it's an input component, submitted value is checked first since it'd be the value to be used in case validation errors
	 * terminates jsf lifecycle
	 * - Finally the value of the component is retrieved from backing bean and if there's a converter, converted value is returned
	 * 
	 * - If the component is not a value holder, toString of component is used to support Facelets UIInstructions.
	 * 
	 * @param context			FacesContext instance
	 * @param component			UIComponent instance whose value will be returned
	 * @return					End text
	 */
	public static String getStringValueToRender(FacesContext facesContext, UIComponent component) {
		if(component instanceof ValueHolder) {
			
			if(component instanceof EditableValueHolder) {
				Object submittedValue = ((EditableValueHolder) component).getSubmittedValue();
				if (submittedValue != null) {
					return submittedValue.toString();
				}
			}

			ValueHolder valueHolder = (ValueHolder) component;
			Object value = valueHolder.getValue();
			if(value == null)
				return "";
			
			//first ask the converter
			if(valueHolder.getConverter() != null) {
				return valueHolder.getConverter().getAsString(facesContext, component, value);
			}
			//Try to guess
			else {
				ValueExpression expr = component.getValueExpression("value");
				if(expr != null) {
					Class<?> valueType = expr.getType(facesContext.getELContext());
					if(valueType != null) {
						Converter converterForType = facesContext.getApplication().createConverter(valueType);
					
						if(converterForType != null)
							return converterForType.getAsString(facesContext, component, value);
					}
				}
			}
			
			//No converter found just return the value as string
			return value.toString();
		} else {
			//This would get the plain texts on UIInstructions when using Facelets
			String value = component.toString();
			
			if(value != null)
				return value.trim();
			else
				return "";
		}
	}
	
	/**
	 * Resolves the end text to render by using a specified value
	 * 
	 * @param context			FacesContext instance
	 * @param component			UIComponent instance whose value will be returned
	 * @return					End text
	 */
	public static String getStringValueToRender(FacesContext facesContext, UIComponent component, Object value) {
		if(value == null)
			return null;
		
		ValueHolder valueHolder = (ValueHolder) component;
		
		Converter converter = valueHolder.getConverter();
		if(converter != null) {
			return converter.getAsString(facesContext, component, value);
		}
		else {
			ValueExpression expr = component.getValueExpression("value");
			if(expr != null) {
				Class<?> valueType = expr.getType(facesContext.getELContext());
				Converter converterForType = facesContext.getApplication().createConverter(valueType);
				
				if(converterForType != null)
					return converterForType.getAsString(facesContext, component, value);
			}
		}
		
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

	public static List<SelectItem> createSelectItems(UIComponent component) {
		List<SelectItem> items = new ArrayList<SelectItem>();
		Iterator<UIComponent> children = component.getChildren().iterator();
		
		while(children.hasNext()) {
			UIComponent child = children.next();
			
			if(child instanceof UISelectItem) {
				UISelectItem selectItem = (UISelectItem) child;
				
				items.add(new SelectItem(selectItem.getItemValue(), selectItem.getItemLabel()));
			} else if(child instanceof UISelectItems) {
				Object selectItems = ((UISelectItems) child).getValue();
			
				if(selectItems instanceof SelectItem[]) {
					SelectItem[] itemsArray = (SelectItem[]) selectItems;
					
					for(SelectItem item : itemsArray)
						items.add(new SelectItem(item.getValue(), item.getLabel()));
					
				} else if(selectItems instanceof Collection) {
					Collection<SelectItem> collection = (Collection<SelectItem>) selectItems;
					
					for(SelectItem item : collection)
						items.add(new SelectItem(item.getValue(), item.getLabel()));
				}
			}  
		}
		
		return items;
	}

	public static String escapeJQueryId(String id) {
		return "#" + id.replaceAll(":", "\\\\:");
	}
}