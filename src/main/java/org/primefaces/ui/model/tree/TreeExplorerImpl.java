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
package org.primefaces.ui.model.tree;

public class TreeExplorerImpl implements TreeExplorer {

	public TreeNode findTreeNode(String path, TreeModel model) {
		String[] paths = path.split("\\.");
		
		if(paths.length == 0)
			return null;
		
		int currentIndex = Integer.parseInt(paths[0]);
		model.setRowIndex(currentIndex);
		TreeNode currentNode  = (TreeNode) model.getWrappedData();

		if(paths.length == 1) {
			return currentNode;
		} 
		else {
			String childPath = path.substring(2);	//subpath
				
			return findTreeNode(childPath, new TreeModel(currentNode));
		}
	}
}