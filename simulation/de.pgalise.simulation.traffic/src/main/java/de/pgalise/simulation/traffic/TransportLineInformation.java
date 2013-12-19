/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.pgalise.simulation.traffic;

import de.pgalise.simulation.shared.persistence.AbstractIdentifiable;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.MappedSuperclass;
import javax.persistence.OneToMany;

/**
 *
 * @author richter
 */
@MappedSuperclass
public abstract class TransportLineInformation extends AbstractIdentifiable{
	private static final long serialVersionUID = 1L;
	
	@OneToMany
	private List<BusTrip> weekdayTripWayThere;
	@OneToMany
	private List<BusTrip> weekdayTripWayBack;
	@OneToMany
	private List<BusTrip> weekendTripWayThere;
	@OneToMany
	private List<BusTrip> weekendTripWayBack;
	private TransportLineType transportLineType;

	public TransportLineInformation() {
		super();
	}

	public TransportLineInformation(Long id,
		TransportLineType transportLineType) {
		super(id);
		this.transportLineType = transportLineType;
	}

	public void setTransportLineType(TransportLineType transportLineType) {
		this.transportLineType = transportLineType;
	}

	public TransportLineType getTransportLineType() {
		return transportLineType;
	}

	/**
	 * @return the weekdayTripWayThere
	 */
	public List<BusTrip> getWeekdayTripWayThere() {
		return weekdayTripWayThere;
	}

	/**
	 * @param weekdayTripWayThere the weekdayTripWayThere to set
	 */
	public void setWeekdayTripWayThere(List<BusTrip> weekdayTripWayThere) {
		this.weekdayTripWayThere = weekdayTripWayThere;
	}

	/**
	 * @return the weekdayTripWayBack
	 */
	public List<BusTrip> getWeekdayTripWayBack() {
		return weekdayTripWayBack;
	}

	/**
	 * @param weekdayTripWayBack the weekdayTripWayBack to set
	 */
	public void setWeekdayTripWayBack(List<BusTrip> weekdayTripWayBack) {
		this.weekdayTripWayBack = weekdayTripWayBack;
	}

	/**
	 * @return the weekendTripWayThere
	 */
	public List<BusTrip> getWeekendTripWayThere() {
		return weekendTripWayThere;
	}

	/**
	 * @param weekendTripWayThere the weekendTripWayThere to set
	 */
	public void setWeekendTripWayThere(List<BusTrip> weekendTripWayThere) {
		this.weekendTripWayThere = weekendTripWayThere;
	}

	/**
	 * @return the weekendTripWayBack
	 */
	public List<BusTrip> getWeekendTripWayBack() {
		return weekendTripWayBack;
	}

	/**
	 * @param weekendTripWayBack the weekendTripWayBack to set
	 */
	public void setWeekendTripWayBack(List<BusTrip> weekendTripWayBack) {
		this.weekendTripWayBack = weekendTripWayBack;
	}
}
