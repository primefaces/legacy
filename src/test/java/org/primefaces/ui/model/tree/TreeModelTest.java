package org.primefaces.ui.model.tree;

import static org.junit.Assert.*;

import org.junit.Test;

public class TreeModelTest {

	@Test
	public void rowCountReturnsTheNumberOfChildrenOfCurrentNode() {
		TreeNode root = new TreeNode("Parent");
		
		TreeNode child1 = new TreeNode("Child1");
		TreeNode child2 = new TreeNode("Child2");
		
		TreeNode child11 = new TreeNode("Child11");
		TreeNode child12 = new TreeNode("Child12");
		TreeNode child21 = new TreeNode("Child21");
		
		child1.addChild(child11);
		child1.addChild(child12);
		child2.addChild(child21);
		
		root.addChild(child1);
		root.addChild(child2);
		
		TreeModel model = new TreeModel(root);
		
		assertEquals(2, model.getRowCount());
		
		model.setRowIndex(0);
		
		assertEquals(2, model.getRowCount());
	}
}
