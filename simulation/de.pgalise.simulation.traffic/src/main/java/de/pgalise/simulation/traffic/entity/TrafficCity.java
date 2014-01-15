/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.traffic.entity;

import de.pgalise.simulation.shared.entity.BaseGeoInfo;
import de.pgalise.simulation.shared.entity.City;
import de.pgalise.simulation.traffic.entity.CityInfrastructureData;
import javax.persistence.Entity;
import javax.persistence.OneToOne;

/**
 *
 * @author richter
 */
@Entity
public class TrafficCity extends City {

  private static final long serialVersionUID = 1L;

  @OneToOne
  private CityInfrastructureData cityInfrastructureData;

  public TrafficCity() {
  }

  public TrafficCity(Long id,
    String name,
    int population,
    int altitude,
    boolean nearRiver,
    boolean nearSea,
    BaseGeoInfo position,
    CityInfrastructureData cityInfrastructureData) {
    super(id,
      name,
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
