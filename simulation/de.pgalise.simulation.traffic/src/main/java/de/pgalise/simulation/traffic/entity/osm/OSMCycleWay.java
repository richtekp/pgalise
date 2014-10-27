/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.pgalise.simulation.traffic.entity.osm;

import de.pgalise.simulation.traffic.entity.CycleWay;
import de.pgalise.simulation.traffic.entity.TrafficEdge;
import java.util.List;
import javax.persistence.Entity;

/**
 *
 * @author richter
 */
@Entity
public class OSMCycleWay extends CycleWay {
  private static final long serialVersionUID = 1L;
  private String osmId;

  public OSMCycleWay() {
  }

  public OSMCycleWay(
    Long id,String osmId) {
    super(id);
    this.osmId = osmId;
  }

  public OSMCycleWay(String osmId,
    Long id,
    List<TrafficEdge> edgeList) {
    super(id,
      edgeList);
    this.osmId = osmId;
  }

  public String getOsmId() {
    return osmId;
  }

  public void setOsmId(String osmId) {
    this.osmId = osmId;
  }
}
