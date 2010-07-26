package org.primefaces.ui.model.tree;

import static org.junit.Assert.*;

import org.junit.Test;

public class TreeNodeTest {

	@Test
	public void shouldAddChildNodes() {
		TreeNode node = new TreeNode("Parent");
		
		node.addChild(new TreeNode("Child1"));
		node.addChild(new TreeNode("Child2"));
		
		assertEquals(2, node.getChildCount());
	}
	
	@Test
	public void shouldHaveParent() {		
		TreeNode root = new TreeNode("Parent");
		
		TreeNode child1 = new TreeNode("Child1");
		TreeNode child11 = new TreeNode("Child11");
		child1.addChild(child11);
		
		root.addChild(child1);
		
		assertEquals(root, child1.getParent());
		assertEquals(child1, child11.getParent());
	}
}
