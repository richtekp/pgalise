/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.pgalise.simulation.traffic.entity;

import de.pgalise.simulation.shared.entity.Way;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.MappedSuperclass;

/**
 *
 * @author richter
 */
@Entity
public class TrafficWay extends Way<TrafficEdge, TrafficNode> {
  private static final long serialVersionUID = 1L;

  public TrafficWay() {
    super();
  }
  
  public TrafficWay(Long id) {
    super(id);
  }
  
  public TrafficWay(Long id, List<TrafficEdge> edgeList) {
    super(id, edgeList);
  }
  
}
