/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.traffic.entity.osm;

import com.vividsolutions.jts.geom.Coordinate;
import de.pgalise.simulation.shared.entity.BaseBoundary;
import de.pgalise.simulation.shared.entity.BaseCoordinate;
import de.pgalise.simulation.traffic.entity.CityInfrastructureData;
import de.pgalise.simulation.traffic.entity.TrafficCity;
import java.util.List;
import javax.persistence.Entity;

/**
 *
 * @author richter
 */
@Entity
public class OSMTrafficCity extends TrafficCity {

  private static final long serialVersionUID = 1L;
  private long osmId;

  public OSMTrafficCity() {
  }

  public OSMTrafficCity(long osmId,
    Long id,
    String name,
    int population,
    int altitude,
    boolean nearRiver,
    boolean nearSea,
          BaseBoundary boundary,
    CityInfrastructureData trafficInfrastructureData) {
    super(id,
      name,
      population,
      altitude,
      nearRiver,
      nearSea,
      boundary,
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
