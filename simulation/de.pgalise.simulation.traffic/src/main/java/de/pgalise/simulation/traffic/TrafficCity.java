/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.traffic;

import de.pgalise.simulation.shared.city.BaseGeoInfo;
import de.pgalise.simulation.shared.city.City;
import de.pgalise.simulation.shared.city.CityInfrastructureData;
import javax.persistence.Entity;
import javax.persistence.OneToOne;

/**
 *
 * @author richter
 * @param <C>
 */
@Entity
public class TrafficCity extends City {

	private static final long serialVersionUID = 1L;

	@OneToOne
	private CityInfrastructureData cityInfrastructureData;

	public TrafficCity() {
	}

	public TrafficCity(
		String name,
		int population,
		int altitude,
		boolean nearRiver,
		boolean nearSea,
		BaseGeoInfo position,
		CityInfrastructureData cityInfrastructureData) {
		super(name,
			population,
			altitude,
			nearRiver,
			nearSea,
			position
		);
		this.cityInfrastructureData = cityInfrastructureData;
	}

	public CityInfrastructureData getCityInfrastructureData() {
		return cityInfrastructureData;
	}

	public void setCityInfrastructureData(
		CityInfrastructureData cityInfrastructureData) {
		this.cityInfrastructureData = cityInfrastructureData;
	}

}
