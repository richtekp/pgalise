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
 
package de.pgalise.util.graph.internal;

import java.util.ArrayList;
import java.util.List;

import de.pgalise.simulation.shared.geometry.Geometry;
import de.pgalise.simulation.shared.geometry.Rectangle;
import de.pgalise.util.btree.BNode;
import de.pgalise.util.btree.BTree;
import de.pgalise.util.graph.disassembler.Disassembler;

/**
 * Disassemble the graph
 * 
 * @author Mustafa
 * @version 1.0 (Mar 20, 2013)
 */
public class QuadrantDisassembler implements Disassembler {
	@Override
	public List<Geometry> disassemble(Geometry geometry, int numServers) {
		final List<Geometry> result = new ArrayList<Geometry>();

		BTree<Geometry> tree = this.getTree(geometry, numServers);
		for (int i = 0; i < tree.getLeafs().size(); i++) {
			Geometry geo = tree.getLeafs().get(i).getData();
			result.add(geo);
		}

		return result;
	}

	/**
	 * Returns the tree
	 * 
	 * @param rootData
	 *            Geometry
	 * @param numServers
	 *            Number of servers
	 * @return BTree
	 */
	public BTree<Geometry> getTree(Geometry rootData, int numServers) {
		BTree<Geometry> tree = new BTree<Geometry>();
		tree.getRoot().setData(rootData);
		if (numServers > 1) {
			do {
				BNode<Geometry> current = tree.getLowestLeaf();
				// log.debug("Lowest leaf: " + current.getData());
				if (current.getData().getWidth() >= current.getData().getHeight()) {
					// vertikal aufteilen
					current.setLeft(new BNode<Geometry>(new Rectangle(current.getData().getStartX(), current.getData()
							.getStartY(), current.getData().getStartX() + (current.getData().getWidth() / 2), current
							.getData().getStartY() + current.getData().getHeight())));
					current.setRight(new BNode<Geometry>(new Rectangle(current.getData().getStartX()
							+ (current.getData().getWidth() / 2), current.getData().getStartY(), (current.getData()
							.getWidth() / 2) + (current.getData().getWidth() / 2), current.getData().getStartY()
							+ current.getData().getHeight())));
					// log.debug(String.format("Splitting node (%s) vertical to (%s) and (%s) ", current.getData(),
					// current.getLeft().getData(), current.getRight().getData()));
				} else {
					// horizontal aufteilen
					current.setLeft(new BNode<Geometry>(new Rectangle(current.getData().getStartX(), current.getData()
							.getStartY(), current.getData().getStartX() + current.getData().getWidth(), current
							.getData().getStartY() + (current.getData().getHeight() / 2))));
					current.setRight(new BNode<Geometry>(new Rectangle(current.getData().getStartX(), current.getData()
							.getHeight() / 2, current.getData().getStartX() + current.getData().getWidth(), (current
							.getData().getHeight() / 2) + (current.getData().getHeight() / 2))));
					// log.debug(String.format("Splitting node (%s) horizontal to (%s) and (%s) ", current.getData(),
					// current.getLeft().getData(), current.getRight().getData()));
				}
			} while (tree.getLeafCount() < numServers);
		}
		return tree;
	}
}
