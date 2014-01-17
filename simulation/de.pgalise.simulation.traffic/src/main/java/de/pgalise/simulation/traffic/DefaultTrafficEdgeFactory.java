package de.pgalise.simulation.traffic;

import de.pgalise.simulation.service.IdGenerator;
import de.pgalise.simulation.traffic.entity.TrafficNode;
import de.pgalise.simulation.traffic.entity.TrafficEdge;
import de.pgalise.simulation.traffic.entity.VehicleData;
import org.jgrapht.EdgeFactory;

/**
 *
 * @param <D>
 * @author richter
 */
public class DefaultTrafficEdgeFactory<D extends VehicleData> implements
  EdgeFactory<TrafficNode, TrafficEdge> {

  private final IdGenerator idGenerator;

  public DefaultTrafficEdgeFactory(IdGenerator idGenerator) {
    this.idGenerator = idGenerator;
  }

  @Override
  public TrafficEdge createEdge(
    TrafficNode sourceVertex,
    TrafficNode targetVertex) {
    return new TrafficEdge(idGenerator.getNextId(),
      sourceVertex,
      targetVertex);
  }

}
