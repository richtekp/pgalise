/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.pgalise.simulation.controlCenter.ctrl.sensorDialogs;

import de.pgalise.simulation.energy.EnergyController;
import de.pgalise.simulation.sensorFramework.SensorManagerController;
import de.pgalise.simulation.sensorFramework.output.tcpip.TcpIpOutput;
import de.pgalise.simulation.service.IdGenerator;
import de.pgalise.simulation.service.RandomSeedService;
import de.pgalise.simulation.shared.city.Coordinate;
import de.pgalise.simulation.weather.service.WeatherController;
import java.io.Serializable;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
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
public class BaseSensorDialogCtrl implements Serializable {
	private MapModel mapModel;
	public final static LatLng DEFAULT_START_COORDINATE = new LatLng(36.879466,
		30.667648);
	public final static LatLng DEFAULT_END_COORDINATE = new LatLng(36.883707,
		30.689216);
	private Marker startMarker = new Marker(DEFAULT_START_COORDINATE,
		"Konyaalti");
	private Marker endMarker = new Marker(DEFAULT_END_COORDINATE,
		"Konyaalti");
	private Coordinate coordinate;
	private int chosenUpdateStep;
	@EJB
	private SensorManagerController sensorManagerController;
	@EJB
	private IdGenerator idGenerator;
	@EJB
	private TcpIpOutput output;
	@EJB
	private RandomSeedService randomSeedService;
	
	public BaseSensorDialogCtrl() {

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
		coordinate = new Coordinate(marker.getLatlng().getLat(),
				marker.getLatlng().getLng());
	}

	public void setChosenUpdateStep(int chosenUpdateStep) {
		this.chosenUpdateStep = chosenUpdateStep;
	}

	public int getChosenUpdateStep() {
		return chosenUpdateStep;
	}

	public Coordinate getCoordinate() {
		return coordinate;
	}

	/**
	 * @return the sensorManagerController
	 */
	public SensorManagerController getSensorManagerController() {
		return sensorManagerController;
	}

	/**
	 * @param sensorManagerController the sensorManagerController to set
	 */
	public void setSensorManagerController(
		SensorManagerController sensorManagerController) {
		this.sensorManagerController = sensorManagerController;
	}

	/**
	 * @return the idGenerator
	 */
	public IdGenerator getIdGenerator() {
		return idGenerator;
	}

	/**
	 * @param idGenerator the idGenerator to set
	 */
	public void setIdGenerator(
		IdGenerator idGenerator) {
		this.idGenerator = idGenerator;
	}

	/**
	 * @return the output
	 */
	public TcpIpOutput getOutput() {
		return output;
	}

	/**
	 * @param output the output to set
	 */
	public void setOutput(
		TcpIpOutput output) {
		this.output = output;
	}

	/**
	 * @return the randomSeedService
	 */
	public RandomSeedService getRandomSeedService() {
		return randomSeedService;
	}

	/**
	 * @param randomSeedService the randomSeedService to set
	 */
	public void setRandomSeedService(
		RandomSeedService randomSeedService) {
		this.randomSeedService = randomSeedService;
	}
}
