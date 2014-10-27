/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.pgalise.simulation.traffic.entity;

import de.pgalise.simulation.shared.entity.Way;
import java.util.List;
import javax.persistence.Entity;

/**
 *
 * @author richter
 */
/*
distungish types of ways (for cars, bicycles, buses) with subclasses in order 
to holds type safe collections in traffic graph to facilitate routing and 
routing related tasks
*/
@Entity
public class CycleWay extends TrafficWay {
  private static final long serialVersionUID = 1L;

  public CycleWay() {
  }
  
  public CycleWay(Long id) {
    super(id);
  }

	public CycleWay(Long id,
		List<TrafficEdge> edgeList) {
		super(id,edgeList);
	}
  
}
