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
package org.primefaces.ui.component.tree;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.servlet.ServletResponse;

import org.primefaces.ui.event.tree.NodeCollapseEvent;
import org.primefaces.ui.event.tree.NodeExpandEvent;
import org.primefaces.ui.event.tree.NodeSelectEvent;
import org.primefaces.ui.model.tree.TreeExplorer;
import org.primefaces.ui.model.tree.TreeExplorerImpl;
import org.primefaces.ui.model.tree.TreeModel;
import org.primefaces.ui.model.tree.TreeNode;
import org.primefaces.ui.model.tree.TreeNodeEvent;
import org.primefaces.ui.renderkit.CoreRenderer;
import org.primefaces.ui.renderkit.PartialRenderer;
import org.primefaces.ui.util.ComponentUtils;

public class TreeRenderer extends CoreRenderer implements PartialRenderer {
	
	private TreeExplorer treeExplorer;
	
	public TreeRenderer() {
		treeExplorer = new TreeExplorerImpl();
	}
	
	public void decode(FacesContext facesContext, UIComponent component) {
		Tree tree = (Tree) component;
		String clientId = tree.getClientId(facesContext);
		Map<String,String> params = facesContext.getExternalContext().getRequestParameterMap();
		
		if(params.containsKey(clientId + ":rowKey")) {
			String rowKey = params.get(clientId + ":rowKey");
			String event = params.get(clientId + ":event");
			
			TreeNode root = (TreeNode) tree.getValue();
			TreeNode currentNode = treeExplorer.findTreeNode(rowKey, new TreeModel(root));
			
			switch(TreeNodeEvent.valueOf(event)) {
				case SELECT:
					tree.queueEvent(new NodeSelectEvent(tree, currentNode));
				break;
				
				case EXPAND:
					currentNode.setExpanded(true);
					tree.queueEvent(new NodeExpandEvent(tree, currentNode));
				break;
					
				case COLLAPSE:
					currentNode.setExpanded(false);
					tree.queueEvent(new NodeCollapseEvent(tree, currentNode));
				break;
			}
		}
	}
	
	public void encodePartially(FacesContext facesContext, UIComponent component) throws IOException {
		Tree tree = (Tree) component;
		ResponseWriter writer = facesContext.getResponseWriter();
		Map<String,String> params = facesContext.getExternalContext().getRequestParameterMap();
		String clientId = tree.getClientId(facesContext);
		TreeNode root = (TreeNode) tree.getValue();
		
		String rowKey = params.get(clientId + ":rowKey");
		TreeNode currentNode = treeExplorer.findTreeNode(rowKey, new TreeModel(root));
		int rowIndex = 0;
		
		ServletResponse response = (ServletResponse) facesContext.getExternalContext().getResponse();
		response.setContentType("text/xml");
		
		writer.write("<nodes>");
		
		for(Iterator<TreeNode> iterator = currentNode.getChildren().iterator(); iterator.hasNext();) {
			TreeNode child = iterator.next();
			
			writer.write("<node>");
				writer.write("<label>" + child.toString() + "</label>");
				writer.write("<rowKey>" + rowKey + "." + rowIndex + "</rowKey>");
				writer.write("<isLeaf>" + child.isLeaf() + "</isLeaf>");
			writer.write("</node>");
			
			rowIndex ++;
		}
		
		writer.write("</nodes>");
	}

	public void encodeEnd(FacesContext facesContext, UIComponent component) throws IOException {
		Tree tree = (Tree) component;
		
		encodeTreeWidget(facesContext, tree);
		encodeTreeMarkup(facesContext, tree);
	}
	
	protected void encodeTreeWidget(FacesContext facesContext, Tree tree) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		String clientId = tree.getClientId(facesContext);
		String formClientId = null;
		String treeVar = getTreeVar(tree);
		TreeNode root = (TreeNode) tree.getValue();
		
		UIComponent parentForm = ComponentUtils.findParentForm(facesContext, tree);
		if(parentForm != null)
			formClientId = parentForm.getClientId(facesContext);
		else
			throw new FacesException("Tree:" + clientId + " needs to be enclosed in a form");
			
		writer.startElement("script", null);
		writer.writeAttribute("type", "text/javascript", null);
		
		writer.write("YAHOO.util.Event.onContentReady(\"" + clientId + "\", function() {\n");
		writer.write(treeVar + " = new PrimeFaces.widget.TreeView(\"" + clientId + ":container\", [\n");
		
		int rowIndex = 0;
		for (Iterator<TreeNode> iterator = root.getChildren().iterator(); iterator.hasNext();) {
			encodeTreeNode(facesContext, tree, iterator.next(), String.valueOf(rowIndex));
			rowIndex ++;
			
			if(iterator.hasNext())
				writer.write(",");
		}

		writer.write("]);\n");
		
		writer.write(treeVar + ".setToggleMode(\"" + tree.getToggleMode() + "\");\n");
		
		if(tree.getExpandAnim() != null)
			writer.write(treeVar + ".setExpandAnim(YAHOO.widget.TVAnim." + tree.getExpandAnim() + ");\n");
		if(tree.getCollapseAnim() != null)
			writer.write(treeVar + ".setCollapseAnim(YAHOO.widget.TVAnim." + tree.getCollapseAnim() + ");\n");
		
		if(!isClientToggling(tree)) {
			writer.write(treeVar + ".clientId = \"" + clientId + "\";\n");
			writer.write(treeVar + ".actionURL = \"" + getActionURL(facesContext) + "\";\n");
			writer.write(treeVar + ".rowKeyFieldId = \"" + clientId + ":rowKey\";\n");
			writer.write(treeVar + ".eventFieldId = \"" + clientId + ":event\";\n");
			writer.write(treeVar + ".formId = \"" + formClientId + "\";\n");
		}
		
		writer.write(treeVar + ".render();\n");
		writer.write("});");

		writer.endElement("script");
	}
	
	protected void encodeTreeNode(FacesContext facesContext, Tree tree, TreeNode node, String rowKey) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		int rowIndex = 0;
		
		writer.write("{type:\"text\", label:\"" + node.toString() + "\", rowKey:\"" + rowKey + "\"");
		
		if(node.isLeaf())
			writer.write(",isLeaf:true");
		
		if(isClientToggling(tree) && !node.isLeaf()) {
			writer.write(",children:[");
			
			for (Iterator<TreeNode> iterator = node.getChildren().iterator(); iterator.hasNext();) {
				String childRowKey = rowKey + "." + rowIndex;
				encodeTreeNode(facesContext, tree, iterator.next(), childRowKey);
				
				rowIndex ++;
				
				if(iterator.hasNext())
					writer.write(",");
			}
			
			writer.write("]");
		}
		
		writer.write("}");
	}
	
	protected void encodeTreeMarkup(FacesContext facesContext, Tree tree) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		String clientId = tree.getClientId(facesContext);
		
		writer.startElement("div", tree);
		writer.writeAttribute("id", tree.getClientId(facesContext), null);
		
		encodeHiddenInput(facesContext, clientId + ":rowKey");
		encodeHiddenInput(facesContext, clientId + ":event");
		
		writer.startElement("div", tree);
		writer.writeAttribute("id", tree.getClientId(facesContext) + ":container", null);		
		writer.endElement("div");
		
		writer.endElement("div");
	}
	
	protected void encodeHiddenInput(FacesContext facesContext, String id) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		
		writer.startElement("input", null);
		writer.writeAttribute("id", id, null);
		writer.writeAttribute("name", id, null);
		writer.writeAttribute("type", "hidden", null);
		writer.endElement("input");
	}
	
	protected String getTreeVar(Tree tree){
		if(tree.getWidgetVar() != null)
			return tree.getWidgetVar();
		else
			return "pf_tree_" + tree.getId();
	}
	
	protected boolean isClientToggling(Tree tree) {
		String toggleMode = tree.getToggleMode();
		
		if(toggleMode == null)
			return true;
		else
			return toggleMode.equalsIgnoreCase("client");
	}
}
