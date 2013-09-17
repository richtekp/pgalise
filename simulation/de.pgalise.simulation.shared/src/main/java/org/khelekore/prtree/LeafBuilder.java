package org.khelekore.prtree;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/** A builder of internal nodes used during bulk loading of a PR-Tree.
 *  A PR-Tree is build by building a pseudo R-Tree and grabbing the
 *  leaf nodes (and then repeating until you have just one root node).
 *  This class creates the leaf nodes without building the full pseudo tree.
 */
class LeafBuilder implements Serializable {
	private static final long serialVersionUID = -3463394628817358354L;
	private final int dimensions;
    private final int branchFactor;

    public LeafBuilder (int dimensions, int branchFactor) {
	this.dimensions = dimensions;
	this.branchFactor = branchFactor;
    }

    public <T, N> void buildLeafs (Collection<? extends T> ls,
				   NodeComparators<T> comparators,
				   NodeFactory<N> nf,
				   List<N> leafNodes) {
	List<NodeUsage<T>> nodes = new ArrayList<> (ls.size ());
	for (T t : ls) {
		nodes.add (new NodeUsage<> (t, 1));
	}

	Circle<Noder<T, N>> getters =
	    new Circle<> (dimensions * 2);

	for (int i = 0; i < dimensions; i++) {
		addGetterAndSplitter (nodes, comparators.getMinComparator (i),
				getters);
	}

	for (int i = 0; i < dimensions; i++) {
		addGetterAndSplitter (nodes, comparators.getMaxComparator (i),
				getters);
	}

	getLeafs (1, ls.size (), getters, nf, leafNodes);
    }

    private <T, N> void addGetterAndSplitter (List<NodeUsage<T>> nodes,
					      Comparator<T> tcomp,
					      Circle<Noder<T, N>> getters) {
	Comparator<NodeUsage<T>> comp = new NodeUsageComparator<> (tcomp);
	Collections.sort (nodes, comp);
	List<NodeUsage<T>> sortedNodes = new ArrayList<> (nodes);
	getters.add (new Noder<T, N> (sortedNodes));
    }

    private <T, N> void getLeafs (int id, int totalNumberOfElements,
				  Circle<Noder<T, N>> getters,
				  NodeFactory<N> nf, List<N> leafNodes) {
	List<Partition> partitionsToExpand = new ArrayList<> ();
	int[] pos = new int[2 * dimensions];
	partitionsToExpand.add (new Partition (id, totalNumberOfElements, pos));
	while (!partitionsToExpand.isEmpty ()) {
	    Partition p = partitionsToExpand.remove (0);
	    // Get the extreme nodes
	    getters.reset ();
	    for (int i = 0; i < getters.getNumElements (); i++) {
		int nodesToGet = Math.min (p.numElementsLeft, branchFactor);
		if (nodesToGet == 0) {
			break;
		}
		Noder<T, N> noder = getters.getNext ();
		leafNodes.add (noder.getNextNode (p, i, nodesToGet, nf));
		p.numElementsLeft -= nodesToGet;
	    }
	    // Split the rest of the elements
	    if (p.numElementsLeft > 0) {
		int splitPos = getSplitPos (p.id) % getters.getNumElements ();
		Noder<T, N> s = getters.get (splitPos);
		s.split (p, splitPos, p.numElementsLeft,
			 p.id, 2 * p.id, 2 * p.id + 1,
			 partitionsToExpand);
	    }
	}
    }

    private int getSplitPos (int n) {
	// id is generated by the power of twos so get biggest n where 2 ^ n < id
	// id: 1 -> splitPos 0, id: 2 -> 1, 3 -> 1, 4 -> 2, 5 -> 2, 7 -> 2, 8 -> 3
	int splitPos = 0;
	while (n >= 2) {
	    n >>= 1;
	    splitPos++;
	}
	return splitPos;
    }

    private static class NodeUsageComparator<T> 
	implements Comparator<NodeUsage<T>> {
	private Comparator<T> sorter;

	public NodeUsageComparator (Comparator<T> sorter) {
	    this.sorter = sorter;
	}

	@Override
	public int compare (NodeUsage<T> n1, NodeUsage<T> n2) {
	    return sorter.compare (n1.getData (), n2.getData ());
	}
    }

    private static class Noder<T, N> {
	private final List<NodeUsage<T>> data;

	private Noder (List<NodeUsage<T>> data) {
	    this.data = data;
	}

	/** Get the next node.
	 * @param p the Partition to get a node from
	 * @param gi the current getter index
	 * @param maxObjects use at most this many objects
	 * @param nf the NodeFactory used to create the nodes
	 * @return the next node
	 */
	private N getNextNode (Partition p, int gi, int maxObjects,
			       NodeFactory<N> nf) {

	    Object nodeData[] = new Object[maxObjects];
	    int s = data.size ();
	    for (int i = 0; i < maxObjects; i++) {
		while (p.currentPositions[gi] < s &&
		       isUsedNode (p, p.currentPositions[gi])) {
		    p.currentPositions[gi]++;
		}
		NodeUsage<T> nu = data.set (p.currentPositions[gi], null);
		nodeData[i] = nu.getData ();
		nu.use ();
	    }
	    
	    for (int i = 0; i < nodeData.length; i++) {
		if (nodeData[i] == null) {
		    for (int j = 0; j < data.size (); j++) {
					System.err.println (j + ": " + data.get (j));
				}
		    throw new NullPointerException ("Null data found at: " + i);
		}
	    }
	    return nf.create (nodeData);
	}

	private boolean isUsedNode (Partition p, int pos) {
	    NodeUsage<T> nu = data.get (pos);
	    return nu == null || nu.isUsed () || nu.getOwner () != p.id;
	}

	private void split (Partition p, int gi,
			    int nodesToMark, int fromId, int toId1, int toId2,
			    List<Partition> partitionsToExpand) {
	    int sizePart2 = nodesToMark / 2;
	    int sizePart1 = nodesToMark - sizePart2;
	    int startPos = p.currentPositions[gi];
	    int startPos2 = markPart (sizePart1, fromId, toId1, startPos);
	    markPart (sizePart2, fromId, toId2, startPos2);
	    partitionsToExpand.add (0, new Partition (toId1, sizePart1,
						      p.currentPositions));
	    int[] pos = p.currentPositions.clone ();
	    pos[gi] = startPos2;
	    partitionsToExpand.add (1, new Partition (toId2, sizePart2, pos));
	}

	private int markPart (int numToMark, int fromId, int toId, int startPos) {
	    NodeUsage<T> nu;
	    while (numToMark > 0) {
		while ((nu = data.get (startPos)) == null || 
		       nu.getOwner () != fromId) {
			startPos++;
		}
		nu.changeOwner (toId);
		numToMark--;
	    }
	    return startPos;
	}
    }

    private static class Partition {
	private final int id;
	private int numElementsLeft;
	private int[] currentPositions;

	public Partition (int id, int numElementsLeft, int[] currentPositions) {
	    this.id = id;
	    this.numElementsLeft = numElementsLeft;
	    this.currentPositions = currentPositions;
	}

	@Override public String toString () {
	    return getClass ().getSimpleName () + "{id: " + id + 
		", numElementsLeft: " + numElementsLeft +
		", currentPositions: " + Arrays.toString (currentPositions) +
		"}";
	}
    }
}
