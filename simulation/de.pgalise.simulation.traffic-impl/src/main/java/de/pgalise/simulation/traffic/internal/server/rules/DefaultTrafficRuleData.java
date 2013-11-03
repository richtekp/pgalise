/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.traffic.internal.server.rules;

import de.pgalise.simulation.shared.city.DefaultNavigationNode;
import de.pgalise.simulation.shared.city.NavigationEdge;
import de.pgalise.simulation.shared.exception.ExceptionMessages;
import de.pgalise.simulation.shared.persistence.AbstractIdentifiable;
import de.pgalise.simulation.traffic.internal.DefaultTrafficEdge;
import de.pgalise.simulation.traffic.internal.DefaultTrafficNode;
import de.pgalise.simulation.traffic.internal.model.vehicle.BaseVehicle;
import de.pgalise.simulation.traffic.model.vehicle.VehicleData;
import de.pgalise.simulation.traffic.server.rules.TrafficRuleCallback;
import de.pgalise.simulation.traffic.server.rules.TrafficRuleData;

/**
 *
 * @param <D> 
 * @author richter
 */
public class DefaultTrafficRuleData<D extends VehicleData> extends AbstractIdentifiable implements TrafficRuleData<D, DefaultTrafficNode<D>, DefaultTrafficNode<D>, BaseVehicle<D>> {
	private static final long serialVersionUID = 1L;
	
	/**
	 * the considered {@link Vehicle}
	 */
	private final BaseVehicle<D> vehicle;
	/**
	 * the {@link Edge} where the considered {@link Vehicle} is coming from
	 */
	private final DefaultTrafficEdge<D> from;
	/**
	 * the {@link Edge} to which the considered {@link Vehicle} is planning
	 * to go
	 */
	private final DefaultTrafficEdge<D> to;
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
	public DefaultTrafficRuleData(
		final BaseVehicle<D> vehicle,
		final DefaultTrafficEdge<D> from,
		final DefaultTrafficEdge<D> to,
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
	@Override
	public BaseVehicle<D> getVehicle() {
		return this.vehicle;
	}

	/**
	 * Returns the {@link Edge} where the considered {@link Vehicle} is
	 * coming from.
	 *
	 * @return the {@link Edge} where the considered {@link Vehicle} is
	 *         coming from
	 */
	@Override
	public DefaultTrafficEdge<D> getFrom() {
		return this.from;
	}

	/**
	 * Returns the {@link Edge} to which the considered {@link Vehicle} is
	 * planning to go.
	 *
	 * @return the {@link Edge} to which the considered {@link Vehicle} is
	 *         planning to go
	 */
	@Override
	public DefaultTrafficEdge<D> getTo() {
		return this.to;
	}

	/**
	 * Returns the {@link TrafficRuleCallback} which method are invoked on
	 * certain events.
	 *
	 * @return the {@link TrafficRuleCallback} which method are invoked on
	 *         certain events
	 */
	@Override
	public TrafficRuleCallback getCallback() {
		return this.callback;
	}
	
}
