/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.traffic.entity;

import de.pgalise.simulation.shared.entity.BaseCoordinate;
import de.pgalise.simulation.shared.entity.City;
import java.util.List;
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

  public TrafficCity(Long id, CityInfrastructureData cityInfrastructureData) {
    super(id);
    this.cityInfrastructureData = cityInfrastructureData;
  }

  /**
   *
   * @param id
   * @param name
   * @param population
   * @param altitude
   * @param nearRiver
   * @param nearSea
   * @param referencePoint
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
    BaseCoordinate referencePoint,
    List<BaseCoordinate> geoInfo,
    CityInfrastructureData cityInfrastructureData) {
    super(id,
      name,
      population,
      altitude,
      nearRiver,
      nearSea,
      referencePoint,
      geoInfo
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
