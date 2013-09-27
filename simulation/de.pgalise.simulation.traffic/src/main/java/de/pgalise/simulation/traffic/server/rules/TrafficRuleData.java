/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.traffic.server.rules;

import de.pgalise.simulation.shared.exception.ExceptionMessages;
import de.pgalise.simulation.shared.city.NavigationEdge;
import de.pgalise.simulation.traffic.model.vehicle.Vehicle;
import de.pgalise.simulation.traffic.model.vehicle.VehicleData;

/**
 * Class that records all relevant data for a traffic rule 'transaction'
 *
 * @author Marcus
 */
public class TrafficRuleData {
	/**
	 * the considered {@link Vehicle}
	 */
	private final Vehicle<? extends VehicleData> vehicle;
	/**
	 * the {@link Edge} where the considered {@link Vehicle} is coming from
	 */
	private final NavigationEdge<?,?> from;
	/**
	 * the {@link Edge} to which the considered {@link Vehicle} is planning
	 * to go
	 */
	private final NavigationEdge<?,?> to;
	/**
	 * the {@link TrafficRuleCallback} which method are invoked on certain
	 * events
	 */
	private final TrafficRuleCallback callback;

	/**
	 * Creates a {@link TrafficRuleData} with the passed arguments.
	 *
	 * @param vehicle
	 *            the considered {@link Vehicle}
	 * @param from
	 *            the {@link Edge} where the considered {@link Vehicle} is
	 *            coming from
	 * @param to
	 *            the {@link Edge} to which the considered {@link Vehicle}
	 *            is planning to go
	 * @param callback
	 *            the {@link TrafficRuleCallback} which method are invoked
	 *            on certain events
	 * @throws IllegalArgumentException
	 *             if any of the passed arguments is null
	 */
	public TrafficRuleData(
		final Vehicle<? extends VehicleData> vehicle,
		final NavigationEdge<?,?> from,
		final NavigationEdge<?,?> to,
		final TrafficRuleCallback callback) throws IllegalArgumentException {
		if (vehicle == null) {
			throw new IllegalArgumentException(ExceptionMessages.getMessageForNotNull("vehicle"));
		}
		if (from == null) {
			throw new IllegalArgumentException(ExceptionMessages.getMessageForNotNull("from"));
		}
		if (to == null) {
			throw new IllegalArgumentException(ExceptionMessages.getMessageForNotNull("to"));
		}
		if (callback == null) {
			throw new IllegalArgumentException(ExceptionMessages.getMessageForNotNull("callback"));
		}
		this.vehicle = vehicle;
		this.from = from;
		this.to = to;
		this.callback = callback;
	}

	/**
	 * Returns the considered {@link Vehicle}.
	 *
	 * @return the considered {@link Vehicle}
	 */
	public Vehicle<? extends VehicleData> getVehicle() {
		return this.vehicle;
	}

	/**
	 * Returns the {@link Edge} where the considered {@link Vehicle} is
	 * coming from.
	 *
	 * @return the {@link Edge} where the considered {@link Vehicle} is
	 *         coming from
	 */
	public NavigationEdge<?,?> getFrom() {
		return this.from;
	}

	/**
	 * Returns the {@link Edge} to which the considered {@link Vehicle} is
	 * planning to go.
	 *
	 * @return the {@link Edge} to which the considered {@link Vehicle} is
	 *         planning to go
	 */
	public NavigationEdge<?,?> getTo() {
		return this.to;
	}

	/**
	 * Returns the {@link TrafficRuleCallback} which method are invoked on
	 * certain events.
	 *
	 * @return the {@link TrafficRuleCallback} which method are invoked on
	 *         certain events
	 */
	public TrafficRuleCallback getCallback() {
		return this.callback;
	}
	
}
