/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.shared.city;

import de.pgalise.simulation.shared.city.NavigationNode;
import com.vividsolutions.jts.geom.Coordinate;
import de.pgalise.simulation.shared.persistence.Identifiable;

/**
 *
 * @param <N> 
 * @author richter
 */
public interface BusStop<N extends NavigationNode> extends Identifiable, NavigationNode {
	

	public Coordinate getLocation() ;

	public void setLocation(Coordinate location) ;

	/**
	 * @return the stopName
	 */
	public String getStopName() ;

	/**
	 * @param stopName the stopName to set
	 */
	public void setStopName(String stopName) ;

	public String getStopCode() ;

	public void setStopCode(String stopCode) ;

	public String getZoneId() ;

	public void setZoneId(String zoneId) ;

	public String getStopUrl() ;

	public void setStopUrl(String stopUrl) ;

	public String getLocationType() ;

	public void setLocationType(String locationType) ;

	public String getParentStation() ;

	public void setParentStation(String parentStation) ;

	public String getStopTimezone() ;

	public void setStopTimezone(String stopTimezone) ;

	public String getWheelchairBoarding() ;

	public void setWheelchairBoarding(String wheelchairBoarding) ;

}
