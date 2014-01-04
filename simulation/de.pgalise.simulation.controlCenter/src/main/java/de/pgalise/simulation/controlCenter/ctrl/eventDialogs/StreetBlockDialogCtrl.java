/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.pgalise.simulation.controlCenter.ctrl.eventDialogs;

import de.pgalise.simulation.controlCenter.ctrl.BaseMapDialogCtrl;
import de.pgalise.simulation.traffic.TrafficNode;
import de.pgalise.simulation.traffic.model.RoadBarrier;
import de.pgalise.simulation.traffic.server.TrafficServerLocal;
import java.util.Date;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

/**
 *
 * @author richter
 */
@ManagedBean
@RequestScoped
public class StreetBlockDialogCtrl extends BaseMapDialogCtrl {
	private static final long serialVersionUID = 1L;
	private Date chosenStartTimestamp;
	@EJB
	private TrafficServerLocal trafficServerLocal;
	private int chosenDuration;

	/**
	 * Creates a new instance of StreetBlockDialogCtrl
	 */
	public StreetBlockDialogCtrl() {
	}

	public void setChosenStartTimestamp(Date chosenStartTimestamp) {
		this.chosenStartTimestamp = chosenStartTimestamp;
	}

	public Date getChosenStartTimestamp() {
		return chosenStartTimestamp;
	}

	public int getChosenDuration() {
		return chosenDuration;
	}

	public void setChosenDuration(int chosenDuration) {
		this.chosenDuration = chosenDuration;
	}
	
	public void save() {
		TrafficNode node = trafficServerLocal.getTrafficGraphExtesions().getGraph().getNodeClosestTo(getCoordinate());
		trafficServerLocal.addNewRoadBarrier(new RoadBarrier(getIdGenerator().getNextId(),node,
			null,
			serialVersionUID,
			serialVersionUID));
	}
	
}
