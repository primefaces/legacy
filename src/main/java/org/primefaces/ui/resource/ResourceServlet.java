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
package org.primefaces.ui.resource;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.primefaces.ui.resource.stream.CSSResourceStreamer;
import org.primefaces.ui.resource.stream.DefaultResourceStreamer;
import org.primefaces.ui.resource.stream.ResourceStreamer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * ResourceServlet is responsible for streaming resources like css, script, images and etc to the client.
 * Streaming is done via ResourceStreamers and resources are forced to be cached indefinitely using convenient response headers.
 */
public class ResourceServlet extends HttpServlet {
	
	private Logger logger = LoggerFactory.getLogger(ResourceServlet.class);
	
	private static Map<String,String> mimeTypes;
	
	private List<ResourceStreamer> resourceStreamers;
	
	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		
		initMimeTypes();
		initResourceStreamers();
	}

	private void initMimeTypes() {
		mimeTypes = new HashMap<String, String>();
		mimeTypes.put("css", "text/css");
		mimeTypes.put("js", "text/js");
		mimeTypes.put("jpg", "image/jpeg");
		mimeTypes.put("jpeg", "image/jpeg");
		mimeTypes.put("png", "image/png");
		mimeTypes.put("gif", "image/gif");
		mimeTypes.put("swf", "application/x-shockwave-flash");
	}
	
	private void initResourceStreamers() {
		resourceStreamers = new ArrayList<ResourceStreamer>();
		resourceStreamers.add(new DefaultResourceStreamer());
		resourceStreamers.add(new CSSResourceStreamer());
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String resourcePath = ResourceUtils.RESOURCE_FOLDER + getResourcePath(req.getRequestURI());
		String mimeType = getResourceContentType(resourcePath);
		
		if(logger.isDebugEnabled())
			logger.debug("Streaming resource:" + resourcePath);
	    
	    try {
	        InputStream inputStream = ResourceServlet.class.getResourceAsStream(resourcePath);
	        URL url = ResourceServlet.class.getResource(resourcePath);
	        
	        if(url == null) {
	        	if(logger.isErrorEnabled()) {
	        		logger.error("Resource:" + resourcePath + " not found");
	        	}
	        	
				resp.sendError(HttpServletResponse.SC_NOT_FOUND);

				return;
			}
	        
	        resp.setContentType(mimeType);
	        resp.setStatus(200);
	        setCaching(resp);
	        
	        streamResource(req, resp, mimeType, inputStream);
	        
	        if(logger.isDebugEnabled())
				logger.debug("Resource:" + resourcePath + " streamed succesfully");
	    }
	    catch (Exception exception) {
	    	if(logger.isErrorEnabled())
	    		logger.error("Error in streaming resource" + resourcePath + ":" + exception.getMessage());
	    }
	}

	private void streamResource(HttpServletRequest req, HttpServletResponse resp, String mimeType, InputStream inputStream) throws IOException {
		for(ResourceStreamer streamer : resourceStreamers) {
			if(streamer.isAppropriateStreamer(mimeType))
				streamer.stream(req, resp, inputStream);
		}
	}
	
	private void setCaching(HttpServletResponse response) {
		long now = System.currentTimeMillis();
		long oneYear = 31363200000L;
		
		response.setHeader("Cache-Control", "Public");
		response.setDateHeader("Expires", now + oneYear);
	}

	protected String getResourcePath(String requestURI) {
		int patternIndex = requestURI.indexOf(ResourceUtils.RESOURCE_VERSION_PATTERN);
		
		return requestURI.substring(patternIndex + ResourceUtils.RESOURCE_VERSION_PATTERN.length(), requestURI.length());
	}
	
	protected String getResourceContentType(String resourcePath) {
		String resourceFileExtension = getResourceFileExtension(resourcePath);
		
		return mimeTypes.get(resourceFileExtension);
	}
	
	protected String getResourceFileExtension(String resourcePath) {
		String parsed[] = resourcePath.split("\\.");
		
		return parsed[parsed.length -1];
	}
}