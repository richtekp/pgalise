/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.pgalise.simulation.traffic;

import de.pgalise.simulation.shared.persistence.AbstractIdentifiable;
import java.util.List;
import javax.persistence.Entity;

/**
 *
 * @author richter
 */
@Entity
public class BusLineInformation extends AbstractIdentifiable{
	private static final long serialVersionUID = 1L;
	
	private List<BusTrip> weekdayTripWayThere;
	private List<BusTrip> weekdayTripWayBack;
	private List<BusTrip> weekendTripWayThere;
	private List<BusTrip> weekendTripWayBack;

	public BusLineInformation() {
		super();
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
