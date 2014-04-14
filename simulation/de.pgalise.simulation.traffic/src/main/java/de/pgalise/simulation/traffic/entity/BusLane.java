/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.pgalise.simulation.traffic.entity;

import java.util.List;
import javax.persistence.Entity;

/**
 * If a bus lane allows usage by bicycles, a {@link CycleWay} has to be added 
 * to the {@link TrafficGraph}.
 * @author richter
 */
@Entity
public class BusLane extends CycleWay {
  private static final long serialVersionUID = 1L;

  public BusLane() {
  }
  
  public BusLane(Long id) {
    super(id);
  }
  
  public BusLane(Long id, List<TrafficEdge> edgeList) {
    super(id,
      edgeList);
  }
}
