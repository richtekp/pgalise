/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.traffic.server.rules;

import de.pgalise.simulation.shared.exception.ExceptionMessages;
import de.pgalise.simulation.shared.city.NavigationEdge;
import de.pgalise.simulation.traffic.TrafficEdge;
import de.pgalise.simulation.traffic.TrafficNode;
import de.pgalise.simulation.traffic.model.vehicle.Vehicle;
import de.pgalise.simulation.traffic.model.vehicle.VehicleData;

/**
 * Class that records all relevant data for a traffic rule 'transaction'
 *
 * @param <D> 
 * @param <N> 
 * @param <E> 
 * @param <V> 
 * @author Marcus
 */
public interface TrafficRuleData<
	D extends VehicleData,
	N extends TrafficNode<N,E,D,V>, 
	E extends TrafficEdge<N,E,D,V>, 
	V extends Vehicle<D,N,E,V>> {

	/**
	 * Returns the considered {@link Vehicle}.
	 *
	 * @return the considered {@link Vehicle}
	 */
	public V getVehicle();

	/**
	 * Returns the {@link Edge} where the considered {@link Vehicle} is
	 * coming from.
	 *
	 * @return the {@link Edge} where the considered {@link Vehicle} is
	 *         coming from
	 */
	public E getFrom();

	/**
	 * Returns the {@link Edge} to which the considered {@link Vehicle} is
	 * planning to go.
	 *
	 * @return the {@link Edge} to which the considered {@link Vehicle} is
	 *         planning to go
	 */
	public E getTo() ;

	/**
	 * Returns the {@link TrafficRuleCallback} which method are invoked on
	 * certain events.
	 *
	 * @return the {@link TrafficRuleCallback} which method are invoked on
	 *         certain events
	 */
	public TrafficRuleCallback getCallback() ;
	
}
