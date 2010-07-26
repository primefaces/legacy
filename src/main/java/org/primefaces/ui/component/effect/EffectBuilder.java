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
package org.primefaces.ui.component.effect;

import org.primefaces.ui.model.JSObjectBuilder;

public class EffectBuilder implements JSObjectBuilder {

	private StringBuffer buffer;
	private boolean firstOptionProvided = false;
	
	public EffectBuilder(String type) {
		buffer = new StringBuffer();
		buffer.append("new Effect.");
		buffer.append(resolveEffectType(type));
	}
	
	public EffectBuilder forComponent(String parentClientId) {
		buffer.append("(\"");
		buffer.append(parentClientId);
		buffer.append("\",{");
		
		return this;
	}
	
	public EffectBuilder withOption(String name, String value) {
		if(firstOptionProvided)
			buffer.append(",");
		else
			firstOptionProvided = true;
		
		buffer.append(name);
		buffer.append(":");
		buffer.append(value);
			
		return this;
	}
	
	public String build() {
		buffer.append("});");
		
		return buffer.toString();
	}
	
	private String resolveEffectType(String type) {
		if(type == null || type.equals(""))
			throw new IllegalArgumentException(type + " is not a valid effect type");
			
		return type.substring(0, 1).toUpperCase() + type.substring(1);
	}
}
