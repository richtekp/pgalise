/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.traffic.entity.osm;

import com.vividsolutions.jts.geom.Coordinate;
import de.pgalise.simulation.shared.entity.BaseCoordinate;
import de.pgalise.simulation.traffic.entity.BusStop;
import de.pgalise.simulation.traffic.entity.BusStopInformation;
import java.util.Objects;
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

  public OSMBusStop(
    String osmId,
    String stopName,
    BusStopInformation busStopInformation,
    Coordinate geoLocation) {
    super(
      stopName,
      busStopInformation,
      geoLocation);
    this.osmId = osmId;
  }

  public OSMBusStop(
    String osmId,
    String stopName,
    BusStopInformation busStopInformation,
    double x, double y) {
    super(
      stopName,
      busStopInformation,
      x,y);
    this.osmId = osmId;
  }

  public void setOsmId(String osmId) {
    this.osmId = osmId;
  }

  public String getOsmId() {
    return osmId;
  }
}
