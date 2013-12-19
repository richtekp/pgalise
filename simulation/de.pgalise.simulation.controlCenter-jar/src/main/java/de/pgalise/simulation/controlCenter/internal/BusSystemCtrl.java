/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.pgalise.simulation.controlCenter.internal;

import de.pgalise.simulation.traffic.BusRoute;
import java.util.LinkedList;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

/**
 *
 * @author richter
 */
@ManagedBean
@RequestScoped
public class BusSystemCtrl {
	private List<BusRoute> busRoutes = new LinkedList<>();

	/**
	 * Creates a new instance of BusSystemCtrl
	 */
	public BusSystemCtrl() {
	}

	public List<BusRoute> getBusRoutes() {
		return busRoutes;
	}

	public void setBusRoutes(List<BusRoute> busRoutes) {
		this.busRoutes = busRoutes;
	}
	
	public void checkAll() {
		for(BusRoute busRoute : busRoutes) {
			busRoute.setUsed(true);
		}
	}
	
	public void checkNone() {
		for(BusRoute busRoute : busRoutes) {
			busRoute.setUsed(false);
		}
	}
	
}
