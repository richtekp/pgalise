/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.pgalise.simulation.controlCenter.ctrl;

import de.pgalise.simulation.controlCenter.MapParsedState;
import java.util.Date;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

/**
 *
 * @author richter
 */
@ManagedBean
@RequestScoped
public class SimulationController {
	private ConnectionState connectionState;
	private SimulationState simulationState;
	private Date simulationDate;
	private MapParsedState mapParsedState;

	/**
	 * Creates a new instance of SimulationController
	 */
	public SimulationController() {
	}
	
	public void startSimulation() {
		throw new UnsupportedOperationException();
	}
	
	public void stopSimulation() {
		//handle confirm dialog on client
		throw new UnsupportedOperationException();
	}
	
	public ConnectionState getConnectionState() {
		return connectionState;
	}

	public void setConnectionState(ConnectionState connectionState) {
		this.connectionState = connectionState;
	}

	public void setSimulationState(SimulationState simulationState) {
		this.simulationState = simulationState;
	}

	public SimulationState getSimulationState() {
		return simulationState;
	}

	public void setSimulationDate(Date simulationDate) {
		this.simulationDate = simulationDate;
	}

	public Date getSimulationDate() {
		return simulationDate;
	}

	public void setMapParsedState(MapParsedState oSMParsedState) {
		this.mapParsedState = oSMParsedState;
	}

	public MapParsedState getMapParsedState() {
		return mapParsedState;
	}
	
}
