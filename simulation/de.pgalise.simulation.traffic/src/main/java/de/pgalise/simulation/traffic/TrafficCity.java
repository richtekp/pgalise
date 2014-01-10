/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.traffic;

import de.pgalise.simulation.shared.city.BaseGeoInfo;
import de.pgalise.simulation.shared.city.City;
import javax.persistence.Entity;
import javax.persistence.OneToOne;

/**
 *
 * @author richter
 * @param <C>
 */
@Entity
public class TrafficCity<C extends TrafficInfrastructureData> extends City {

	private static final long serialVersionUID = 1L;

	@OneToOne
	private C cityInfrastructureData;

	public TrafficCity() {
	}

	public TrafficCity(
		String name,
		int population,
		int altitude,
		boolean nearRiver,
		boolean nearSea,
		BaseGeoInfo position,
		C cityInfrastructureData) {
		super(name,
			population,
			altitude,
			nearRiver,
			nearSea,
			position
		);
		this.cityInfrastructureData = cityInfrastructureData;
	}

	public C getCityInfrastructureData() {
		return cityInfrastructureData;
	}

	public void setCityInfrastructureData(C cityInfrastructureData) {
		this.cityInfrastructureData = cityInfrastructureData;
	}

}
