/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.traffic.internal;

import com.vividsolutions.jts.algorithm.CGAlgorithms;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import de.pgalise.simulation.shared.entity.BaseCoordinate;
import de.pgalise.simulation.shared.entity.NavigationEdge;
import de.pgalise.simulation.shared.entity.NavigationNode;
import de.pgalise.simulation.traffic.TrafficGraph;
import de.pgalise.simulation.traffic.entity.TrafficEdge;
import de.pgalise.simulation.traffic.entity.TrafficNode;
import java.util.Set;

/**
 * a graph of {@link NavigationNode}s connected with {@link NavigationEdge}s.
 * {@link DirectPosition2D}s are saved in a prefix tree in order to ensure
 * search of closest nodes to arbitrary positions using {@link  #getElementClosestTo(org.geotools.geometry.DirectPosition2D, double)
 * }. A <tt>TrafficGraph</tt> does not handle vehicle management which is done
 * by a {@link TrafficManager}.
 *
 * @author richter
 */
/*
 * implemenation notes:
 * - it's a lot of work to implement listening to boundary changes because every add and remove operation to the graph has to be listened to and almost every method has to be extended (edge and vertex sets are backed up by the graph). Therefore boundaries are always calculated and not updated at every add or remove operation.
 * - this is not an Entity for both practical and data redundancy reasons (Graph doesn't have a default constructor) (information is completely managed by nodes and edges), but it's an Identifiable nevertheless
 */
public class DefaultTrafficGraph extends TrafficGraph {

  private static final long serialVersionUID = 1L;

  private Long id;

  public DefaultTrafficGraph() {
    super(new NavigationEdgeFactory<>(TrafficEdge.class));
  }

  /**
   *
   * @param position
   * @param distanceTolerance can be set to {@link Double#MAX_VALUE} in order to
   * ignore
   * @return <ol><li> a {@link NavigationNode} if the distance to a node in this
   * graph is <= <tt>distanceTolerance</tt></li>
   * <li>a {@link NavigationEdge} if the distance to an edge in this graph is
   * less than <tt>distanceTolerance</tt>	and 1. doesn't apply</li>
   * <li><code>null</code> if 1. and 2. don't apply
   */
  @Override
  public Object getElementClosestTo(BaseCoordinate position,
    double distanceTolerance) {
    //@TODO: remove brute force search
    TrafficNode minDistanceNode = null;
    double minDistance = Double.MAX_VALUE;
    for (TrafficNode node : this.vertexSet()) {
      double distance = node.distance(position);
      if (distance <= distanceTolerance && distance < minDistance) {
        minDistanceNode = node;
      }
    }
    if (minDistanceNode != null) {
      return minDistanceNode;
    }
    minDistance = Double.MAX_VALUE;
    TrafficEdge minDistanceEdge = null;
    for (TrafficEdge edge : this.edgeSet()) {
      BaseCoordinate node0Position = edge.getSource();
      BaseCoordinate node1Position = edge.getTarget();
      Coordinate node0Coordinates = new Coordinate(node0Position.
        getX(),
        node0Position.getY());
      Coordinate node1Coordinates = new Coordinate(node1Position.
        getX(),
        node1Position.getY());
      Coordinate positionCoordines = new Coordinate(position.getX(),
        position.getY());
      double distance = CGAlgorithms.distancePointLine(positionCoordines,
        node0Coordinates,
        node1Coordinates);
      if (distance <= distanceTolerance && distance < minDistance) {
        minDistanceEdge = edge;
      }
    }
    return minDistanceEdge; //if it is null it fulfills the last condition;
  }

  @Override
  public TrafficNode getNodeClosestTo(BaseCoordinate position) {
    return getNodeClosestTo(position,
      Integer.MAX_VALUE);
  }

  @Override
  public TrafficNode getNodeClosestTo(BaseCoordinate position,
    int distanceTolerance) {

    TrafficNode minDistanceNode = null;
    double minDistance = Double.MAX_VALUE;
    for (TrafficNode node : this.vertexSet()) {
      double distance = node.distance(position);
      if (distance <= distanceTolerance && distance < minDistance) {
        minDistanceNode = node;
      }
    }
    return minDistanceNode;
  }

  @Override
  public Long getId() {
    return this.id;
  }

  @Override
  protected void setId(Long id) {
    this.id = id;
  }

  @Override
  public Set<TrafficEdge> getEdgeSet() {
    return super.edgeSet();
  }

  @Override
  public void setEdgeSet(Set<TrafficEdge> edgeSet) {
    super.edgeSet().addAll(edgeSet);
  }

  @Override
  public Set<TrafficNode> getVertexSet() {
    return super.vertexSet();
  }

  @Override
  public void setVertexSet(Set<TrafficNode> vertexSet) {
    super.vertexSet().addAll(vertexSet);
  }

  /**
   *
   * @return
   */
  @Override
  public Geometry calulateBoundaries() {
    throw new UnsupportedOperationException();
  }


}
