/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.traffic;

import de.pgalise.simulation.shared.city.NavigationNode;

/**
 *
 * @author richter
 */
public interface TrafficTrip {
	

	public NavigationNode getStartNode() ;

	public void setStartNode(NavigationNode startNode) ;

	public NavigationNode getTargetNode() ;

	public void setTargetNode(NavigationNode targetNode) ;

	public long getStartTime() ;

	public void setStartTime(long startTime) ;
}
