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

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.js.ajax.AjaxHandler;
import org.springframework.js.ajax.SpringJavascriptAjaxHandler;

/**
 * PrimeFaces implementation of SpringJavascriptAjaxHandler
 * 
 * Only used for Spring WebFlow Integration
 */
public class PrimeFacesAjaxHandler implements AjaxHandler {

	private AjaxHandler delegate = new SpringJavascriptAjaxHandler();
	
	public boolean isAjaxRequest(HttpServletRequest req, HttpServletResponse resp) {
		boolean isAjax = req.getParameterMap().containsKey("primefacesPartialRequest");
		
		if(isAjax)
			return true;
		else
			return delegate.isAjaxRequest(req, resp);
	}

	public void sendAjaxRedirect(String targetURL, HttpServletRequest req, HttpServletResponse resp, boolean popup) throws IOException {
		if(isAjaxRequest(req, resp))
			resp.sendRedirect(targetURL);
		else
			delegate.sendAjaxRedirect(targetURL, req, resp, popup);
	}
}