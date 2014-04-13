/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.pgalise.simulation.traffic.entity.osm;

import de.pgalise.simulation.traffic.entity.TrafficEdge;
import de.pgalise.simulation.traffic.entity.TrafficWay;
import java.util.List;
import javax.persistence.Entity;

/**
 *
 * @author richter
 */
@Entity
public class OSMTrafficWay extends TrafficWay {
  private static final long serialVersionUID = 1L;
  private String osmId;

  public OSMTrafficWay(Long id, String osmId) {
    super(id);
    this.osmId = osmId;
  }

  public OSMTrafficWay(Long id,String osmId,
    List<TrafficEdge> edgeList,
    String streetname) {
    super(id,edgeList,
      streetname);
    this.osmId = osmId;
  }

  public void setOsmId(String osmId) {
    this.osmId = osmId;
  }

  public String getOsmId() {
    return osmId;
  }
}
