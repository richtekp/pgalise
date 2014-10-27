/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.pgalise.simulation.traffic.entity.osm;

import com.vividsolutions.jts.geom.Coordinate;
import de.pgalise.simulation.traffic.entity.TrafficNode;
import java.util.Objects;
import javax.persistence.Entity;

/**
 *
 * @author richter
 */
@Entity
public class OSMTrafficNode extends TrafficNode {
  private static final long serialVersionUID = 1L;
  private String osmId;

  public OSMTrafficNode() {
  }

  public OSMTrafficNode(
    String osmId,
    Coordinate geoLocation) {
    super(
      geoLocation);
    this.osmId = osmId;
  }

  public OSMTrafficNode(
    String osmId,
    double x, double y) {
    super(
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
