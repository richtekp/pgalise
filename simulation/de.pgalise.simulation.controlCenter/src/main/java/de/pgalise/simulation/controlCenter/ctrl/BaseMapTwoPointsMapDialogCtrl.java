/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.pgalise.simulation.controlCenter.ctrl;

import de.pgalise.simulation.shared.city.Coordinate;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import org.primefaces.event.map.MarkerDragEvent;
import org.primefaces.model.map.LatLng;
import org.primefaces.model.map.Marker;

/**
 *
 * @author richter
 */
public abstract class BaseMapTwoPointsMapDialogCtrl extends BaseMapDialogCtrl {
	public final static LatLng DEFAULT_END_COORDINATE = new LatLng(36.883707,
		30.689216);
	private static final long serialVersionUID = 1L;
	protected final Marker endMarker = new Marker(DEFAULT_END_COORDINATE,
		"Konyaalti");
	private Coordinate endCoordinate;

	/**
	 * Creates a new instance of BaseMapTwoPointsMapDialogCtrl
	 */
	public BaseMapTwoPointsMapDialogCtrl() {
		getMapModel().addOverlay(endMarker);
		endMarker.setDraggable(true);
	}

	protected void setEndCoordinate(Coordinate endCoordinate) {
		this.endCoordinate = endCoordinate;
	}

	public Coordinate getEndCoordinate() {
		return endCoordinate;
	}

	@Override
	public void onMarkerDrag(MarkerDragEvent event) {
		Marker marker = event.getMarker();
		if(marker == startMarker) {
			setCoordinate(new Coordinate(marker.getLatlng().getLat(),
				marker.getLatlng().getLng()));
		}else {
			endCoordinate = new Coordinate(marker.getLatlng().getLat(),
				marker.getLatlng().getLng());
		}
	}
	
}
