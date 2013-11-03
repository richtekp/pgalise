/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.traffic.server.sensor;

import com.vividsolutions.jts.geom.Coordinate;
import de.pgalise.simulation.sensorFramework.AbstractSensor;
import de.pgalise.simulation.sensorFramework.output.Output;
import de.pgalise.simulation.shared.event.Event;
import de.pgalise.simulation.traffic.TrafficEdge;
import de.pgalise.simulation.traffic.TrafficNode;
import de.pgalise.simulation.traffic.model.vehicle.Vehicle;
import de.pgalise.simulation.traffic.model.vehicle.VehicleData;
import de.pgalise.simulation.traffic.server.eventhandler.TrafficEvent;

/**
 *
 * @param <N> 
 * @param <E> 
 * @param <D> 
 * @param <V> 
 * @param <F> 
 * @param <G> 
 * @author richter
 */
public abstract class AbstractStaticTrafficSensor<
	N extends TrafficNode<N,E,D,V>, 
	E extends TrafficEdge<N,E,D,V>, 
	D extends VehicleData, 
	V extends Vehicle<D,N,E,V> 
> extends AbstractSensor<TrafficEvent<N,E,D,V>> implements StaticTrafficSensor<N,E,D,V> {
	
	private N node;

	/**
	 * Constructor
	 * 
	 * @param output
	 *            Output of the sensor
	 * @param sensorId
	 *            ID of the sensor
	 * @param position
	 *            Position of the sensor
	 * @param updateLimit
	 *            Update limit
	 * @throws IllegalArgumentException
	 */
	protected AbstractStaticTrafficSensor(N node, Output output, Coordinate position, int updateLimit)
			throws IllegalArgumentException {
		super(output, position, updateLimit);
		this.node = node;
	}

	/**
	 * @param output
	 *            Output of the sensor
	 * @param sensorId
	 *            ID of the sensor
	 * @param position
	 *            Position of the sensor
	 * @throws IllegalArgumentException
	 */
	protected AbstractStaticTrafficSensor(N node, Output output, Coordinate position) throws IllegalArgumentException {
		super(output, position);
		this.node = node;
	}

	/**
	 * Register vehicle on the node
	 * 
	 * @param vehicle
	 *            Vehicle
	 */
	public abstract void vehicleOnNodeRegistered(V vehicle);

	public void setNodeId(N node) {
		this.node = node;
	}

	public N getNodeId() {
		return node;
	}
}
