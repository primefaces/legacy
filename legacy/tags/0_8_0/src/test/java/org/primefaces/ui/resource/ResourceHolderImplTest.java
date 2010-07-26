package org.primefaces.ui.resource;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ResourceHolderImplTest {

	private ResourceHolderImpl resourceHolder;
	
	@Before
	public void setup() {
		resourceHolder = new ResourceHolderImpl();
	}
	
	@After
	public void teardown() {
		resourceHolder = null;
	}
	
	@Test
	public void shouldContainAddedResources() {
		resourceHolder.addResource("/yui/resource1.css");
		resourceHolder.addResource("/pf/resource2.js");
		
		assertEquals(2, resourceHolder.getResources().size());
		assertTrue(resourceHolder.getResources().contains("/yui/resource1.css"));
		assertTrue(resourceHolder.getResources().contains("/pf/resource2.js"));
	}
	
	@Test
	public void shouldNotAddSameResourceMoreThanOnce() {
		resourceHolder.addResource("/yui/resource1.css");
		resourceHolder.addResource("/pf/resource2.js");
		resourceHolder.addResource("/yui/resource1.css");
		
		assertEquals(2, resourceHolder.getResources().size());
		assertTrue(resourceHolder.getResources().contains("/yui/resource1.css"));
		assertTrue(resourceHolder.getResources().contains("/pf/resource2.js"));
	}
}
