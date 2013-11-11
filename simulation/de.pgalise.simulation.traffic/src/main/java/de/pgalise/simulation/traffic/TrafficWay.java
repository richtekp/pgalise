/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.traffic;

import de.pgalise.simulation.shared.city.Way;
import java.util.List;
import org.jgrapht.DirectedGraph;

/**
 *
 * @author richter
 */
public class TrafficWay extends Way<TrafficEdge,TrafficNode>{
	private static final long serialVersionUID = 1L;
	
	protected TrafficWay() {}

	public TrafficWay(
		List<TrafficEdge> edgeList,
		String streetname) {
		super(edgeList,
			streetname);
	}
	
}
