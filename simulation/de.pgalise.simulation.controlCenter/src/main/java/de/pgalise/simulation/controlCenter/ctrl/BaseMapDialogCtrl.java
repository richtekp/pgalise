/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.pgalise.simulation.controlCenter.ctrl;

import de.pgalise.simulation.service.IdGenerator;
import de.pgalise.simulation.shared.JaxRSCoordinate;
import java.io.Serializable;
import javax.ejb.EJB;
import org.primefaces.event.map.MarkerDragEvent;
import org.primefaces.model.map.DefaultMapModel;
import org.primefaces.model.map.LatLng;
import org.primefaces.model.map.MapModel;
import org.primefaces.model.map.Marker;

/**
 *
 * @author richter
 */
public abstract class BaseMapDialogCtrl implements Serializable {
	private static final long serialVersionUID = 1L;
	private MapModel mapModel;
	public final static LatLng DEFAULT_START_COORDINATE = new LatLng(36.879466,
		30.667648);
	protected final Marker startMarker = new Marker(DEFAULT_START_COORDINATE,
		"Konyaalti");
	private JaxRSCoordinate coordinate;
	@EJB
	private IdGenerator idGenerator;
	
	public BaseMapDialogCtrl() {
		mapModel = new DefaultMapModel();

		//Draggable  
		mapModel.addOverlay(startMarker);
		startMarker.setDraggable(true);
	}

	public MapModel getMapModel() {
		return mapModel;
	}

	public void setMapModel(MapModel mapModel) {
		this.mapModel = mapModel;
	}

	public void onMarkerDrag(MarkerDragEvent event) {
		Marker marker = event.getMarker();
		coordinate = new JaxRSCoordinate(marker.getLatlng().getLat(),
				marker.getLatlng().getLng());
	}

	public JaxRSCoordinate getCoordinate() {
		return coordinate;
	}

	protected void setCoordinate(JaxRSCoordinate coordinate) {
		this.coordinate = coordinate;
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
}
