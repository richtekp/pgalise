/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.traffic.entity.osm;

import de.pgalise.simulation.shared.entity.BaseCoordinate;
import de.pgalise.simulation.traffic.entity.TrafficNode;
import javax.persistence.Entity;

/**
 *
 * @author richter
 */
@Entity
public class OSMNavigationNode extends TrafficNode {

  private static final long serialVersionUID = 1L;
  private String OSMId;

  protected OSMNavigationNode() {
  }

  public OSMNavigationNode(
    String OSMId,
    BaseCoordinate geoLocation) {
    super(
      geoLocation);
    this.OSMId = OSMId;
  }

  public void setOSMId(String OSMId) {
    this.OSMId = OSMId;
  }

  public String getOSMId() {
    return OSMId;
  }
}
