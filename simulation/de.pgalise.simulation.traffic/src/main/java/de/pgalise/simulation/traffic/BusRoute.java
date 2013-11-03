/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.traffic;

import de.pgalise.simulation.shared.city.BusAgency;
import de.pgalise.simulation.traffic.BusTrip;
import de.pgalise.simulation.shared.persistence.Identifiable;
import java.util.List;

/**
 *
 * @author richter
 */
public interface BusRoute<T extends BusTrip> extends Identifiable {

	/**
	 * @return the routeShortName
	 */
	String getRouteShortName();

	/**
	 * @param routeShortName
	 *            the routeShortName to set
	 */
	void setRouteShortName(String routeShortName);
	/**
	 * @return the routeLongName
	 */
	String getRouteLongName();

	/**
	 * @param routeLongName
	 *            the routeLongName to set
	 */
	void setRouteLongName(String routeLongName);

	/**
	 * @return the routeType
	 */
	int getRouteType();
	/**
	 * @param routeType
	 *            the routeType to set
	 */
	void setRouteType(int routeType);

	boolean isUsed();

	void setUsed(boolean used);

	BusAgency getAgency();
	
	void setAgency(BusAgency agencyId);

	String getRouteDesc();

	void setRouteDesc(String routeDesc);

	String getRouteUrl();

	void setRouteUrl(String routeUrl);

	String getRouteColor();

	void setRouteColor(String routeColor);

	String getRouteTextColor();

	void setRouteTextColor(String routeTextColor);

	/**
	 * @return the weekdayTripWayThere
	 */
	List<T> getWeekdayTripWayThere();
	/**
	 * @param weekdayTripWayThere the weekdayTripWayThere to set
	 */
	void setWeekdayTripWayThere(List<T> weekdayTripWayThere);

	/**
	 * @return the weekdayTripWayBack
	 */
	List<T> getWeekdayTripWayBack();

	/**
	 * @param weekdayTripWayBack the weekdayTripWayBack to set
	 */
	void setWeekdayTripWayBack(List<T> weekdayTripWayBack);
	/**
	 * @return the weekendTripWayThere
	 */
	List<T> getWeekendTripWayThere();

	/**
	 * @param weekendTripWayThere the weekendTripWayThere to set
	 */
	void setWeekendTripWayThere(List<T> weekendTripWayThere);

	/**
	 * @return the weekendTripWayBack
	 */
	List<T> getWeekendTripWayBack();

	/**
	 * @param weekendTripWayBack the weekendTripWayBack to set
	 */
	void setWeekendTripWayBack(List<T> weekendTripWayBack);
}
