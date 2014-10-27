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

import de.pgalise.util.btree.TraversalStrategy.Command;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * Tests for btree
 *
 * @author Timo
 * @version 1.0 (Oct 1, 2012)
 */
public class BTreeTest {

  @SuppressWarnings({"rawtypes", "unchecked"})
  @Test
  public void countNodes() {
    BTree tree = new BTree();
    assertEquals(1,
      tree.getNodeCount());

    tree.getRoot().setLeft(new BNode());
    assertEquals(2,
      tree.getNodeCount());

    tree.getRoot().setRight(new BNode());
    assertEquals(3,
      tree.getNodeCount());

    tree.getRoot().setLeft(new BNode());
    tree.getRoot().getLeft().setLeft(new BNode());
    assertEquals(4,
      tree.getNodeCount());
  }

  @Test
  public void lowestLeaf() {
    /**
     * a b c d e f
     */
    BTree<String> tree = new BTree<>(new TraversalInOrder<String>());
    tree.getRoot().setData("a");
    assertEquals("a",
      tree.getLowestLeaf().getData());

    tree.getRoot().setLeft(new BNode<>("b"));
    assertEquals("b",
      tree.getLowestLeaf().getData());

    tree.getRoot().setRight(new BNode<>("c"));
    tree.getRoot().getLeft().setLeft(new BNode<>("d"));
    assertEquals("c",
      tree.getLowestLeaf().getData());

    tree.getRoot().getLeft().setRight(new BNode<>("e"));
    assertEquals("c",
      tree.getLowestLeaf().getData());

    tree.getRoot().getRight().setLeft(new BNode<>("f"));
    assertEquals("d",
      tree.getLowestLeaf().getData());
  }

  @Test
  public void nodeHeight() {
    /**
     * a b c d e f g
     */
    BTree<String> tree = new BTree<>(new TraversalInOrder<String>());
    tree.getRoot().setData("a");
    assertEquals(0,
      tree.getRoot().getHeight());

    tree.getRoot().setLeft(new BNode<>("b"));
    assertEquals(1,
      tree.getRoot().getLeft().getHeight());

    tree.getRoot().setRight(new BNode<>("c"));
    assertEquals(1,
      tree.getRoot().getRight().getHeight());

    tree.getRoot().getLeft().setLeft(new BNode<>("d"));
    assertEquals(2,
      tree.getRoot().getLeft().getLeft().getHeight());

    tree.getRoot().getLeft().getLeft().setLeft(new BNode<>("g"));
    assertEquals(3,
      tree.getRoot().getLeft().getLeft().getLeft().getHeight());

    tree.getRoot().getLeft().setRight(new BNode<>("e"));
    assertEquals(2,
      tree.getRoot().getLeft().getRight().getHeight());

    tree.getRoot().getRight().setLeft(new BNode<>("f"));
    assertEquals(2,
      tree.getRoot().getRight().getLeft().getHeight());
  }

  @Test
  public void traverseInOrder() {
    /**
     * a b c d e f
     */
    BTree<String> tree = new BTree<>(new TraversalInOrder<String>());
    tree.getRoot().setData("a");
    tree.getRoot().setLeft(new BNode<>("b"));
    tree.getRoot().setRight(new BNode<>("c"));
    tree.getRoot().getLeft().setLeft(new BNode<>("d"));
    tree.getRoot().getLeft().setRight(new BNode<>("e"));
    tree.getRoot().getRight().setLeft(new BNode<>("f"));

    final StringBuilder str = new StringBuilder(16);
    tree.traverse(new Command<String>() {

      @Override
      public void execute(BNode<String> node) {
        str.append(node.getData()); // dbeafc, falls korrekt
      }
    });

    assertEquals("dbeafc",
      str.toString());
  }
}
