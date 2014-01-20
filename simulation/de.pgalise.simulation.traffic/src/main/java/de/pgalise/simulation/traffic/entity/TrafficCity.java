/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.traffic.entity;

import de.pgalise.simulation.shared.JaxRSCoordinate;
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

  /**
   *
   * @param id
   * @param name
   * @param population
   * @param altitude
   * @param nearRiver
   * @param nearSea
   * @param geoInfo
   * @param cityInfrastructureData
   * @throws NullPointerException if <tt>geoInfo</tt> is <code>null</code>
   */
  public TrafficCity(Long id,
    String name,
    int population,
    int altitude,
    boolean nearRiver,
    boolean nearSea,
    BaseGeoInfo geoInfo,
    CityInfrastructureData cityInfrastructureData) {
    super(id,
      name,
      population,
      altitude,
      nearRiver,
      nearSea,
      geoInfo
    );
    this.cityInfrastructureData = cityInfrastructureData;
  }

  public TrafficCity(Long id,
    String name,
    int population,
    int altitude,
    boolean nearRiver,
    boolean nearSea,
    BaseGeoInfo geoInfo,
    CityInfrastructureData cityInfrastructureData,
    JaxRSCoordinate referencePoint) {
    super(id,
      name,
      population,
      altitude,
      nearRiver,
      nearSea,
      geoInfo,
      referencePoint
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
