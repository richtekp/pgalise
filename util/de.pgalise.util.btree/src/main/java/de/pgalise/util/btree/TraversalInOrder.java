/* 
 * Copyright 2013 PG Alise (http://www.pg-alise.de/)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 */
 
package de.pgalise.util.btree;

public final class TraversalInOrder<T> implements TraversalStrategy<T> {
	private BTree<T> tree;

	public TraversalInOrder() {
	}

	public TraversalInOrder(BTree<T> tree) {
		this.tree = tree;
	}

	@Override
	public BTree<T> getTree() {
		return this.tree;
	}

	@Override
	public void setTree(BTree<T> tree) {
		this.tree = tree;
	}

	@Override
	public void traverse(Command<T> cmd) {
		this.traverse(cmd, this.tree.getRoot());
	}

	public void traverse(Command<T> cmd, BNode<T> node) {
		if (node.getLeft() != null) {
			this.traverse(cmd, node.getLeft());
		}

		cmd.execute(node);

		if (node.getRight() != null) {
			this.traverse(cmd, node.getRight());
		}
	}
}
