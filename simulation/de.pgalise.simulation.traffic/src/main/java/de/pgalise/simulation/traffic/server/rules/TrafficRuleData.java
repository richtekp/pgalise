/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.traffic.server.rules;

import de.pgalise.simulation.shared.persistence.AbstractIdentifiable;
import de.pgalise.simulation.traffic.TrafficEdge;
import de.pgalise.simulation.traffic.model.vehicle.Vehicle;
import de.pgalise.simulation.traffic.model.vehicle.VehicleData;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

/**
 * Class that records all relevant data for a traffic rule 'transaction'
 *
 * @author Marcus
 */
@Entity
public class TrafficRuleData extends AbstractIdentifiable {

	private static final long serialVersionUID = 1L;
	private VehicleData vehicleData;
	@ManyToOne
	private TrafficEdge from;
	@ManyToOne
	private TrafficEdge to;
	@Transient
	//@TODO: how to persist callbacks??
	private TrafficRuleCallback trafficRuleCallback;

	protected TrafficRuleData() {
	}

	public TrafficRuleData(VehicleData vehicleData,
		TrafficEdge from,
		TrafficEdge to,
		TrafficRuleCallback trafficRuleCallback) {
		this.vehicleData = vehicleData;
		this.from = from;
		this.to = to;
		this.trafficRuleCallback = trafficRuleCallback;
	}

	/**
	 * Returns the considered {@link Vehicle}.
	 *
	 * @return the considered {@link Vehicle}
	 */
	public VehicleData getVehicleData() {
		return vehicleData;
	}

	/**
	 * Returns the {@link Edge} where the considered {@link Vehicle} is coming
	 * from.
	 *
	 * @return the {@link Edge} where the considered {@link Vehicle} is coming
	 * from
	 */
	public TrafficEdge getFrom() {
		return from;
	}

	/**
	 * Returns the {@link Edge} to which the considered {@link Vehicle} is
	 * planning to go.
	 *
	 * @return the {@link Edge} to which the considered {@link Vehicle} is
	 * planning to go
	 */
	public TrafficEdge getTo() {
		return to;
	}

	/**
	 * Returns the {@link TrafficRuleCallback} which method are invoked on certain
	 * events.
	 *
	 * @return the {@link TrafficRuleCallback} which method are invoked on certain
	 * events
	 */
	public TrafficRuleCallback getCallback() {
		return trafficRuleCallback;
	}

}
