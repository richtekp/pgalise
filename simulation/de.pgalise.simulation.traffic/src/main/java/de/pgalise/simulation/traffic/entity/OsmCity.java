/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.traffic.entity;

import de.pgalise.simulation.shared.entity.BaseGeoInfo;
import de.pgalise.simulation.traffic.entity.CityInfrastructureData;
import javax.persistence.Entity;

/**
 *
 * @author richter
 */
@Entity
public class OsmCity extends TrafficCity {

  private static final long serialVersionUID = 1L;
  private long osmId;

  public OsmCity() {
  }

  public OsmCity(long osmId,
    Long id,
    String name,
    int population,
    int altitude,
    boolean nearRiver,
    boolean nearSea,
    BaseGeoInfo position,
    CityInfrastructureData trafficInfrastructureData) {
    super(id,
      name,
      population,
      altitude,
      nearRiver,
      nearSea,
      position,
      trafficInfrastructureData);
    this.osmId = osmId;
  }

  public void setOsmId(long osmId) {
    this.osmId = osmId;
  }

  public long getOsmId() {
    return osmId;
  }
}
