/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.traffic.entity;

import de.pgalise.simulation.traffic.entity.TrafficNode;
import de.pgalise.simulation.traffic.entity.TrafficEdge;
import de.pgalise.simulation.shared.entity.Way;
import java.util.List;
import javax.persistence.Entity;

/**
 * 
 * @author richter
 */
@Entity
public class MotorWay extends CycleWay {

	private static final long serialVersionUID = 1L;
  /**
   * The name of the street (don't mix with the wayIdentifier property)
   */
	private String streetName;

	protected MotorWay() {
	}
  
  public MotorWay(Long id) {
    super(id);
  }

	public MotorWay(Long id,
		List<TrafficEdge> edgeList,
		String streetName) {
		super(id,edgeList);
    this.streetName = streetName;
	}

	public void applyMaxSpeed(int maxSpeed) {
		for (TrafficEdge edge : getEdgeList()) {
			if (edge.getMaxSpeed() != -1) {
				throw new IllegalStateException(String.format(
					"oneWay flag of edge %s already set (flags on all edges have to be null before a whole way can be set to one way)",
					edge));
			}
			edge.setMaxSpeed(maxSpeed);
		}
	}

	public String getStreetName() {
		return this.streetName;
	}

	public void setStreetName(String streetname) {
		this.streetName = streetname;
	}

}
