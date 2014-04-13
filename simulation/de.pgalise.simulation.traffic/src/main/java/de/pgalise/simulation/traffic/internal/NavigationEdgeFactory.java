/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.traffic.internal;

import de.pgalise.simulation.traffic.entity.TrafficEdge;
import de.pgalise.simulation.traffic.entity.TrafficNode;
import de.pgalise.simulation.traffic.entity.VehicleData;
import java.io.Serializable;
import org.jgrapht.EdgeFactory;

/**
 *
 * @author richter
 * @param <Y>
 * @param <X>
 * @param <D>
 */
public class NavigationEdgeFactory<Y extends TrafficNode, X extends TrafficEdge, D extends VehicleData>
  implements EdgeFactory<Y, X>, Serializable {

  private final Class<X> edgeClass;

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
