/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.traffic.entity;

import de.pgalise.simulation.traffic.entity.BusStop;
import de.pgalise.simulation.traffic.entity.BusStopInformation;
import de.pgalise.simulation.shared.JaxRSCoordinate;
import javax.persistence.Entity;

/**
 *
 * @author richter
 */
@Entity
public class OSMBusStop extends BusStop {
	private static final long serialVersionUID = 1L;
	private String osmId;

	protected OSMBusStop() {
	}

	public OSMBusStop(String osmId,
		String stopName,
		BusStopInformation busStopInformation, JaxRSCoordinate geoLocation) {
		super(stopName,
			busStopInformation, geoLocation);
		this.osmId = osmId;
	}

	public void setOsmId(String osmId) {
		this.osmId = osmId;
	}

	public String getOsmId() {
		return osmId;
	}
}
