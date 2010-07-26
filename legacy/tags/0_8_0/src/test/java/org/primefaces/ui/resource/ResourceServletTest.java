package org.primefaces.ui.resource;

import static org.junit.Assert.*;

import javax.servlet.ServletException;

import org.junit.Test;

public class ResourceServletTest {

	@Test
	public void shouldResolveResourcePath() {
		ResourceServlet rs = new ResourceServlet();
		String requestURI = "/myapp/primefaces_resources/0.8.0/yui/button/button.css";
		
		assertEquals("/yui/button/button.css", rs.getResourcePath(requestURI));
 	}
	
	@Test
	public void shouldResolveResourceFileExtension() {
		ResourceServlet rs = new ResourceServlet();
		
		assertEquals("css", rs.getResourceFileExtension("/myapp/primefaces_resources/0.8.0/yui/button/button.css"));
		assertEquals("js", rs.getResourceFileExtension("/myapp/primefaces_resources/0.8.0/yui/button/button.js"));
		assertEquals("png", rs.getResourceFileExtension("/myapp/primefaces_resources/0.8.0/yui/calendar/calendar.png"));
 	}
	
	@Test
	public void shouldResolveResponseContentType() throws ServletException{
		ResourceServlet rs = new ResourceServlet();
		rs.init();
		
		assertEquals("text/css", rs.getResourceContentType("/myapp/primefaces_resources/0.8.0/yui/button/button.css"));
		assertEquals("text/js", rs.getResourceContentType("/myapp/primefaces_resources/0.8.0/yui/button/button.js"));
		assertEquals("image/png", rs.getResourceContentType("/myapp/primefaces_resources/0.8.0/yui/calendar/calendar.png"));
	}
	
	@Test
	public void shouldReplaceRelativeURLsInCSSResources() {
		ResourceServlet rs = new ResourceServlet();
		
		String input = "url(primefaces_resources:url:/yui/button/button.png)";
		
		String output = rs.replaceRelativeUrl("/myapp", input);
		assertEquals("url(/myapp/primefaces_resources/0.8.0/yui/button/button.png)", output);
	}
}
