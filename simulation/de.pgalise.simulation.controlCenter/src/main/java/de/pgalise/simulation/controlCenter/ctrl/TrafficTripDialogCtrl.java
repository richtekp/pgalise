/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.controlCenter.ctrl;

import de.pgalise.simulation.shared.city.Coordinate;
import de.pgalise.simulation.traffic.TrafficGraph;
import de.pgalise.simulation.traffic.TrafficNode;
import de.pgalise.simulation.traffic.model.vehicle.Vehicle;
import de.pgalise.simulation.traffic.server.TrafficServer;
import de.pgalise.simulation.traffic.server.TrafficServerLocal;
import java.io.Serializable;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import org.primefaces.event.map.MarkerDragEvent;
import org.primefaces.model.map.DefaultMapModel;
import org.primefaces.model.map.LatLng;
import org.primefaces.model.map.MapModel;
import org.primefaces.model.map.Marker;

/**
 *
 * @author richter
 */
@ManagedBean
@ViewScoped
public class TrafficTripDialogCtrl implements Serializable {

	private static final long serialVersionUID = 1L;

	private Date chosenStartDate;
	@EJB
	private TrafficServerLocal<?> trafficServer;
	private List<Vehicle<?>> selectedVehilces = new LinkedList<>();
	private MapModel mapModel;
	public final static LatLng DEFAULT_START_COORDINATE = new LatLng(36.879466,
		30.667648);
	public final static LatLng DEFAULT_END_COORDINATE = new LatLng(36.883707,
		30.689216);
	private Marker startMarker = new Marker(DEFAULT_START_COORDINATE,
		"Konyaalti");
	private Marker endMarker = new Marker(DEFAULT_END_COORDINATE,
		"Konyaalti");
	private Coordinate startCoordinate;
	private Coordinate endCoordinate;
	@EJB
	private TrafficGraph trafficGraph;

	public TrafficTripDialogCtrl() {

		mapModel = new DefaultMapModel();

		//Draggable  
		mapModel.addOverlay(startMarker);
		mapModel.addOverlay(endMarker);
		for (Marker marker : mapModel.getMarkers()) {
			marker.setDraggable(true);
		}
	}

	public MapModel getMapModel() {
		return mapModel;
	}

	public void setMapModel(MapModel mapModel) {
		this.mapModel = mapModel;
	}

	public void onMarkerDrag(MarkerDragEvent event) {
		Marker marker = event.getMarker();
		if (marker == startMarker) {
			startCoordinate = new Coordinate(marker.getLatlng().getLat(),
				marker.getLatlng().getLng());
		} else {
			endCoordinate = new Coordinate(marker.getLatlng().getLat(),
				marker.getLatlng().getLng());
		}
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
		TrafficNode startNode = trafficGraph.getNodeClosestTo(startCoordinate);
		TrafficNode endNode = trafficGraph.getNodeClosestTo(endCoordinate);
		for (Vehicle<?> selectedVehicle : selectedVehilces) {
			trafficServer.takeVehicle(selectedVehicle,
				startNode,
				endNode,
				null);
		}

	}
}
