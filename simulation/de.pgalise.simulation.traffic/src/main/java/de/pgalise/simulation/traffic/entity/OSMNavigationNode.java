/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.traffic.entity;

import de.pgalise.simulation.shared.JaxRSCoordinate;
import javax.persistence.Entity;

/**
 *
 * @author richter
 */
@Entity
public class OSMNavigationNode extends TrafficNode {
	private static final long serialVersionUID = 1L;
	private String OSMId;

	protected OSMNavigationNode() {
	}

	public OSMNavigationNode(String OSMId,
		JaxRSCoordinate geoLocation) {
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