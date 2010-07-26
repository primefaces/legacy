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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ResourceServlet extends HttpServlet{
	
	private Map<String,String> mimeTypes;
	
	public void init() throws ServletException {
		mimeTypes = new HashMap<String, String>();
		mimeTypes.put("css", "text/css");
		mimeTypes.put("js", "text/js");
		mimeTypes.put("jpg", "image/jpeg");
		mimeTypes.put("jpeg", "image/jpeg");
		mimeTypes.put("png", "image/png");
		mimeTypes.put("gif", "image/gif");
		mimeTypes.put("swf", "application/x-shockwave-flash");
	}

	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		String resourcePath = ResourceUtils.RESOURCE_FOLDER + getResourcePath(req.getRequestURI());
		String mimeType = getResourceContentType(resourcePath);
		
		int indice, tempIndice;
	    byte tempArr[];
	    byte mainArr[] = new byte[0];
	    byte byteArr[] = new byte[65535];
	    
	    try {
	        InputStream inputStream = ResourceServlet.class.getResourceAsStream(resourcePath);
	        URL url = ResourceServlet.class.getResource(resourcePath);
	        
	        if (url == null) {
	           getServletContext().log("Resource:" + resourcePath + " not found");
	        }
	        
	        if(mimeType.equals("text/css")) {
	        	handleCSSLoading(req, resp, inputStream);
	        } else {
		        resp.setContentType(mimeType);
		        resp.setStatus(200);
		        ServletOutputStream outputStream = resp.getOutputStream();
		              
		        for(indice = 0; (indice = inputStream.read(byteArr)) > 0;)  {
		          tempIndice = mainArr.length + indice;
		          tempArr = new byte[tempIndice];
		          System.arraycopy(mainArr, 0, tempArr, 0, mainArr.length);
		          System.arraycopy(byteArr, 0, tempArr, mainArr.length, indice);
		          mainArr = tempArr;
		        }
	
		        outputStream.write(mainArr);
		        outputStream.flush();
		        outputStream.close();
	        }
	    }
	    catch (Exception exception) {
	    	exception.printStackTrace();
	    }
	}

	private void handleCSSLoading(HttpServletRequest request, HttpServletResponse response, InputStream inputStream) throws UnsupportedEncodingException, IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
		OutputStreamWriter writer = new OutputStreamWriter(response.getOutputStream(), response.getCharacterEncoding());
		String line = null;
		
		while (null != (line = reader.readLine())) {
			String parsedLine = replaceRelativeUrl(request.getContextPath(), line);
			writer.write(parsedLine+"\n");
        }
		
		writer.flush();
		writer.close();
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

	public String replaceRelativeUrl(String contextPath, String input) {
        String replacement = contextPath + ResourceUtils.RESOURCE_VERSION_PATTERN;
        
        Pattern pattern = Pattern.compile(ResourceUtils.CSS_RESOURCE_PATTERN);

        Matcher matcher = pattern.matcher(input);
        return matcher.replaceAll(replacement);
	}
}