/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.traffic;

import com.vividsolutions.jts.geom.Coordinate;
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
 * @author richter
 */
public class OSMNavigationNode extends TrafficNode {
	private static final long serialVersionUID = 1L;
	private String OSMId;

	protected OSMNavigationNode() {
	}

	public OSMNavigationNode(String OSMId,
		Coordinate geoLocation) {
		super(geoLocation);
		this.OSMId = OSMId;
	}

	public void setOSMId(String OSMId) {
		this.OSMId = OSMId;
	}
	
	public String getOSMId() {
		return OSMId;
	}
}