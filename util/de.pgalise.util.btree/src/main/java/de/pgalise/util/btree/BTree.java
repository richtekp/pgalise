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

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import de.pgalise.util.btree.TraversalStrategy.Command;

public class BTree<T> {
	private BNode<T> root;
	private TraversalStrategy<T> traversalStrategy;

	public BTree() {
		this.root = new BNode<T>();
		this.traversalStrategy = new TraversalInOrder<T>();
		this.traversalStrategy.setTree(this);
	}

	public BTree(TraversalStrategy<T> traversalStrategy) {
		this.root = new BNode<T>();
		this.traversalStrategy = traversalStrategy;
		this.traversalStrategy.setTree(this);
	}

	public int getLeafCount() {
		return this.getLeafs().size();
	}

	public List<BNode<T>> getLeafs() {
		final List<BNode<T>> list = new LinkedList<>();
		this.traverse(new Command<T>() {

			@Override
			public void execute(BNode<T> node) {
				if (node.isLeaf()) {
					list.add(node);
				}
			}

		});
		return list;
	}

	public BNode<T> getLowestLeaf() {
		List<BNode<T>> list = new LinkedList<BNode<T>>();
		list.clear();
		this.computeList(list, this.getRoot());
		final int minHeight = Collections.min(list, new Comparator<BNode<T>>() {
			@Override
			public int compare(BNode<T> o1, BNode<T> o2) {
				return o1.getHeight() - o2.getHeight();
			}
		}).getHeight();

		List<BNode<T>> tmp = new LinkedList<BNode<T>>();
		for (final BNode<T> node : list) {
			if (node.getHeight() == minHeight) {
				tmp.add(node);
			}
		}
		list = tmp;

		return list.get(0);
	}

	public int getNodeCount() {
		return this.countNodes(this.getRoot());
	}

	public BNode<T> getRoot() {
		return this.root;
	}

	public void setRoot(BNode<T> root) {
		this.root = root;
	}

	public void traverse(Command<T> cmd) {
		this.traversalStrategy.traverse(cmd);
	}

	private void computeList(List<BNode<T>> list, BNode<T> node) {
		if (node.getLeft() != null) {
			this.computeList(list, node.getLeft());
		}
		if (node.isLeaf()) {
			list.add(node);
		}
		if (node.getRight() != null) {
			this.computeList(list, node.getRight());
		}
	}

	private int countNodes(BNode<T> node) {
		return 1 + (node.getLeft() != null ? this.countNodes(node.getLeft()) : 0)
				+ (node.getRight() != null ? this.countNodes(node.getRight()) : 0);
	}
}
