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
package org.primefaces.component;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.component.UIViewRoot;

/**
 * Custom UIViewRoot wrapper to be used only in partial tree processing
 */
public class PartialViewRoot extends UIViewRoot {

	private UIViewRoot base;
	
	private List<UIComponent> children;
	
	private List<UIComponent> parents;

	public PartialViewRoot() {
		children = new ArrayList<UIComponent>();
		parents = new ArrayList<UIComponent>();
	}
	
	public PartialViewRoot(UIViewRoot base) {
		this.base = base;
		children = new ArrayList<UIComponent>();
		parents = new ArrayList<UIComponent>();
	}

	public UIViewRoot getBase() {
		return base;
	}
	public void setBase(UIViewRoot base) {
		this.base = base;
	}

	@Override
	public List<UIComponent> getChildren() {
		return children;
	}
	
	public List<UIComponent> getParents() {
		return parents;
	}

	@Override
	public int getChildCount() {
		return children.size();
	}

	@Override
	public Locale getLocale() {
		return base.getLocale();
	}

	@Override
	public String getRenderKitId() {
		return base.getRenderKitId();
	}

	@Override
	public String getViewId() {
		return base.getViewId();
	}

	@Override
	public Map<String, Object> getAttributes() {
		return base.getAttributes();
	}
}