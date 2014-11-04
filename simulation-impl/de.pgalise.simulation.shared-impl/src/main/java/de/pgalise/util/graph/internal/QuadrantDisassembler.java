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

import com.vividsolutions.jts.geom.Envelope;
import java.util.ArrayList;
import java.util.List;

import com.vividsolutions.jts.geom.Geometry;
import de.pgalise.util.btree.BNode;
import de.pgalise.util.btree.BTree;
import de.pgalise.util.graph.disassembler.Disassembler;
import javax.ejb.Local;
import javax.ejb.Stateless;
import org.geotools.geometry.jts.JTS;

/**
 * Disassemble the graph
 *
 * @author Mustafa
 * @version 1.0 (Mar 20, 2013)
 */
@Stateless
@Local(Disassembler.class)
public class QuadrantDisassembler implements Disassembler {

  @Override
  public List<Geometry> disassemble(Geometry geometry,
    int numServers) {
    final List<Geometry> result = new ArrayList<>();

    BTree<Geometry> tree = this.getTree(geometry,
      numServers);
    for (int i = 0; i < tree.getLeafs().size(); i++) {
      Geometry geo = tree.getLeafs().get(i).getData();
      result.add(geo);
    }

    return result;
  }

  /**
   * Returns the tree
   *
   * @param rootData Geometry
   * @param numServers Number of servers
   * @return BTree
   */
  public BTree<Geometry> getTree(Geometry rootData,
    int numServers) {
    BTree<Geometry> tree = new BTree<>();
    tree.getRoot().setData(rootData);
    if (numServers > 1) {
      do {
        BNode<Geometry> current = tree.getLowestLeaf();
        // log.debug("Lowest leaf: " + current.getData());
        if (current.getData().getEnvelopeInternal().getWidth() >= current.
          getData().getEnvelopeInternal().getHeight()) {
          // vertikal aufteilen
          current.setLeft(
            new BNode<Geometry>(
              JTS.toGeometry(
                new Envelope(
                  current.getData().getEnvelopeInternal().getMinX(),
                  current.getData().getEnvelopeInternal().getMinX()
                  + (current.getData().getEnvelopeInternal().getWidth() / 2),
                  current.getData().getEnvelopeInternal().getMinY(),
                  current.getData().getEnvelopeInternal().getMinY()
                  + current.getData().getEnvelopeInternal().getHeight()
                )
              )
            )
          );
          current.setRight(
            new BNode<Geometry>(
              JTS.toGeometry(
                new Envelope(
                  current.getData().getEnvelopeInternal().getMinX()
                  + (current.getData().getEnvelopeInternal().getWidth() / 2),
                  (current.getData().getEnvelopeInternal().getWidth() / 2)
                  + (current.getData().getEnvelopeInternal().getWidth() / 2),
                  current.getData().getEnvelopeInternal().getMinY(),
                  current.getData().getEnvelopeInternal().getMinY()
                  + current.getData().getEnvelopeInternal().getHeight()
                )
              )
            )
          );
          // log.debug(String.format("Splitting node (%s) vertical to (%s) and (%s) ", current.getData(),
          // current.getLeft().getData(), current.getRight().getData()));
        } else {
          // horizontal aufteilen
          current.setLeft(
            new BNode<Geometry>(
              JTS.toGeometry(
                new Envelope(
                  current.getData().getEnvelopeInternal().getMinX(),
                  current.getData().getEnvelopeInternal().getMinX()
                  + current.getData().getEnvelopeInternal().getWidth(),
                  current.getData().getEnvelopeInternal().getMinY(),
                  current.getData().getEnvelopeInternal().getMinY()
                  + (current.getData().getEnvelopeInternal().getHeight() / 2)
                )
              )
            )
          );
          current.setRight(
            new BNode<Geometry>(
              JTS.toGeometry(
                new Envelope(
                  current.getData().getEnvelopeInternal().getMinX(),
                  current.getData().getEnvelopeInternal().getMinX()
                  + current.getData().getEnvelopeInternal().getWidth(),
                  current.getData().getEnvelopeInternal().getHeight() / 2,
                  (current.getData().getEnvelopeInternal().getHeight() / 2)
                  + (current.getData().getEnvelopeInternal().getHeight() / 2)
                )
              )
            )
          );
          // log.debug(String.format("Splitting node (%s) horizontal to (%s) and (%s) ", current.getData(),
          // current.getLeft().getData(), current.getRight().getData()));
        }
      } while (tree.getLeafCount() < numServers);
    }
    return tree;
  }
}
