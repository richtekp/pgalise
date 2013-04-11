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

public class BNode<T> {
	private T data;
	private BNode<T> left;
	private int numChildren;
	private BNode<T> parent;
	private BNode<T> right;

	public BNode() {

	}

	public BNode(T data) {
		this.data = data;
	}

	public T getData() {
		return this.data;
	}

	public int getHeight() {
		int result = 0;
		BNode<T> hangler = this;
		while (hangler.parent != null) {
			hangler = hangler.parent;
			result++;
		}
		return result;
	}

	public BNode<T> getLeft() {
		return this.left;
	}

	public int getNumChildren() {
		return this.numChildren;
	}

	public BNode<T> getParent() {
		return this.parent;
	}

	public BNode<T> getRight() {
		return this.right;
	}

	public boolean isLeaf() {
		return ((this.getLeft() == null) && (this.getRight() == null));
	}

	public void setData(T data) {
		this.data = data;
	}

	public void setLeft(BNode<T> left) {
		this.left = left;
		this.numChildren = 0;
		if (this.left != null) {
			this.left.parent = this;
			this.numChildren++;
		}
		if (this.right != null) {
			this.numChildren++;
		}
	}

	public void setParent(BNode<T> parent) {
		this.parent = parent;
	}

	public void setRight(BNode<T> right) {
		this.right = right;
		this.numChildren = 0;
		if (this.left != null) {
			this.numChildren++;
		}
		if (this.right != null) {
			this.right.parent = this;
			this.numChildren++;
		}
	}
}
