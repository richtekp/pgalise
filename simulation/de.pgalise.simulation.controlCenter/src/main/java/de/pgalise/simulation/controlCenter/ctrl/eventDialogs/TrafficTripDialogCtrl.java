/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.controlCenter.ctrl.eventDialogs;

import de.pgalise.simulation.controlCenter.ctrl.BaseMapTwoPointsMapDialogCtrl;
import de.pgalise.simulation.traffic.TrafficGraph;
import de.pgalise.simulation.traffic.entity.TrafficNode;
import de.pgalise.simulation.traffic.model.vehicle.Vehicle;
import de.pgalise.simulation.traffic.server.TrafficServerLocal;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

/**
 *
 * @author richter
 */
@ManagedBean
@ViewScoped
public class TrafficTripDialogCtrl extends BaseMapTwoPointsMapDialogCtrl {

	private static final long serialVersionUID = 1L;

	private Date chosenStartDate;
	@EJB
	private TrafficServerLocal<?> trafficServer;
	private List<Vehicle<?>> selectedVehilces = new LinkedList<>();
	@EJB
	private TrafficGraph trafficGraph;

	public TrafficTripDialogCtrl() {
	}

	public void setChosenStartDate(Date chosenStartDate) {
		this.chosenStartDate = chosenStartDate;
	}

	public Date getChosenStartDate() {
		return chosenStartDate;
	}

	public Set<Vehicle<?>> retrieveVehicles() {
		return trafficServer.getManagedVehicles();
	}

	public void setSelectedVehilces(
		List<Vehicle<?>> selectedVehilces) {
		this.selectedVehilces = selectedVehilces;
	}

	public List<Vehicle<?>> getSelectedVehilces() {
		return selectedVehilces;
	}

	public void saveTrafficTrip() {
		TrafficNode startNode = trafficGraph.getNodeClosestTo(getCoordinate());
		TrafficNode endNode = trafficGraph.getNodeClosestTo(getEndCoordinate());
		for (Vehicle<?> selectedVehicle : selectedVehilces) {
			trafficServer.takeVehicle(selectedVehicle,
				startNode,
				endNode,
				null);
		}

	}
}
