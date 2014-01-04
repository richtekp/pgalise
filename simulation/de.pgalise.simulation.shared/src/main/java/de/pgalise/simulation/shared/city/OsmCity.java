/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.pgalise.simulation.shared.city;

import javax.persistence.Entity;

/**
 *
 * @author richter
 */
@Entity
public class OsmCity extends City {
	private static final long serialVersionUID = 1L;
	private long osmId;

	public OsmCity() {
	}

	public OsmCity(long osmId,
		String name,
		int population,
		int altitude,
		boolean nearRiver,
		boolean nearSea,
		BaseGeoInfo position) {
		super(name,
			population,
			altitude,
			nearRiver,
			nearSea,
			position);
		this.osmId = osmId;
	}

	public void setOsmId(long osmId) {
		this.osmId = osmId;
	}

	public long getOsmId() {
		return osmId;
	}
}
