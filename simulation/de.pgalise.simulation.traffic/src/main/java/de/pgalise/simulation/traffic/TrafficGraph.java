/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.traffic;

import com.vividsolutions.jts.algorithm.CGAlgorithms;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import de.pgalise.simulation.shared.JaxRSCoordinate;
import de.pgalise.simulation.shared.entity.Identifiable;
import de.pgalise.simulation.shared.entity.BaseCoordinate;
import de.pgalise.simulation.traffic.entity.TrafficEdge;
import de.pgalise.simulation.traffic.entity.TrafficNode;
import de.pgalise.simulation.traffic.entity.VehicleData;
import java.util.HashMap;
import java.util.Set;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import org.jgrapht.DirectedGraph;
import org.jgrapht.EdgeFactory;
import org.jgrapht.Graph;
import org.jgrapht.graph.AsWeightedGraph;
import org.jgrapht.graph.DefaultDirectedGraph;

/**
 *
 * @author richter
 */
public abstract class TrafficGraph extends AsWeightedGraph<TrafficNode, TrafficEdge>
  implements DirectedGraph<TrafficNode, TrafficEdge> {

  private static final long serialVersionUID = 1L;

  private Long id;
  private Graph<TrafficNode, TrafficEdge> delegator;

  public TrafficGraph(EdgeFactory<TrafficNode, TrafficEdge> edgeFactory) {
    super(
      new DefaultDirectedGraph<TrafficNode, TrafficEdge>(edgeFactory),
      new HashMap<TrafficEdge, Double>());
    //Don't use a subclass of WeightedGraph because the implementation will 
    //check the assertion that the edges are instances of DefaultDirectedEdge 
    //which is pure nonsense
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

  public TrafficNode getNodeClosestTo(JaxRSCoordinate position) {
    return getNodeClosestTo(position,
      Integer.MAX_VALUE);
  }

  public TrafficNode getNodeClosestTo(JaxRSCoordinate position,
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

  @Id
  public Long getId() {
    return this.id;
  }

  protected void setId(Long id) {
    this.id = id;
  }

  @OneToMany
  public Set<TrafficEdge> getEdgeSet() {
    return super.edgeSet();
  }

  public void setEdgeSet(Set<TrafficEdge> edgeSet) {
    super.edgeSet().addAll(edgeSet);
  }

  @OneToMany
  public Set<TrafficNode> getVertexSet() {
    return super.vertexSet();
  }

  public void setVertexSet(Set<TrafficNode> vertexSet) {
    super.vertexSet().addAll(vertexSet);
  }

  /**
   *
   * @return
   */
  public Geometry calulateBoundaries() {
    throw new UnsupportedOperationException();
  }

  private static class NavigationEdgeFactory<Y extends TrafficNode, X extends TrafficEdge, Z extends VehicleData>
    implements EdgeFactory<Y, X> {

    private Class<? extends X> edgeClass;

    NavigationEdgeFactory(Class<X> edgeClass) {
      this.edgeClass = edgeClass;
    }

    @Override
    public X createEdge(Y sourceVertex,
      Y targetVertex) {
      X retValue;
      try {
        retValue = edgeClass.newInstance();
      } catch (InstantiationException | IllegalAccessException ex) {
        throw new RuntimeException(ex);
      }
      retValue.setSource(sourceVertex);
      retValue.setTarget(targetVertex);
      return retValue;
    }
  }
}
