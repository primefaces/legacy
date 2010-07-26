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
package org.primefaces.ui.webapp.filter;

import java.io.File;
import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.primefaces.ui.webapp.MultipartRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileUploadFilter implements Filter {
	
	private final static Logger logger = LoggerFactory.getLogger(FileUploadFilter.class);

	private final static String THRESHOLD_SIZE_PARAM = "thresholdSize";
	
	private final static String UPLOAD_DIRECTORY_PARAM = "uploadDirectory";
	
	private String thresholdSize;
	
	private String uploadDir;
	
	public void init(FilterConfig filterConfig) throws ServletException {
		thresholdSize = filterConfig.getInitParameter(THRESHOLD_SIZE_PARAM);
		uploadDir = filterConfig.getInitParameter(UPLOAD_DIRECTORY_PARAM);
		
		logger.debug("FileUploadFilter initiated successfully");
	}
	
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
		HttpServletRequest httpServletRequest = (HttpServletRequest) request;
		boolean isMultipart = ServletFileUpload.isMultipartContent(httpServletRequest);
		
		if(isMultipart) {
			logger.debug("Parsing file upload request");
			
			DiskFileItemFactory diskFileItemFactory = new DiskFileItemFactory();
			if(thresholdSize != null) {
				diskFileItemFactory.setSizeThreshold(Integer.valueOf(thresholdSize));
			}
			if(uploadDir != null) {
				diskFileItemFactory.setRepository(new File(uploadDir));
			}
				
			ServletFileUpload servletFileUpload = new ServletFileUpload(diskFileItemFactory);
			MultipartRequest multipartRequest = new MultipartRequest(httpServletRequest, servletFileUpload);
			
			logger.debug("File upload request parsed succesfully, continuing with filter chain with a wrapped multipart request");
			
			filterChain.doFilter(multipartRequest, response);
		} else {
			filterChain.doFilter(request, response);
		}
	}

	public void destroy() {
		logger.debug("Destroying FileUploadFilter");
	}

}
