/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.pgalise.simulation.controlCenter.ctrl.eventDialogs;

import de.pgalise.simulation.controlCenter.ctrl.BaseMapDialogCtrl;
import de.pgalise.simulation.controlCenter.ctrl.RandomVehiclesCtrl;
import de.pgalise.simulation.service.IdGenerator;
import de.pgalise.simulation.shared.event.EventList;
import de.pgalise.simulation.traffic.entity.TrafficNode;
import de.pgalise.simulation.traffic.event.AttractionTrafficEvent;
import de.pgalise.simulation.traffic.server.TrafficServerLocal;
import java.util.Arrays;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

/**
 *
 * @author richter
 */
@ManagedBean
@ViewScoped
public class AttractionDialogCtrl extends BaseMapDialogCtrl {
	private static final long serialVersionUID = 1L;
	private Date chosenStartTimestamp;
	private int chosenDurationMinutes = 45;
	private RandomVehiclesCtrl randomVehiclesCtrl;
	@EJB
	private TrafficServerLocal trafficServerLocal;
	@EJB
	private IdGenerator idGenerator;

	public AttractionDialogCtrl() {
		chosenStartTimestamp  = GregorianCalendar.getInstance().getTime();
	}

	public void setChosenStartTimestamp(Date chosenStartTimestamp) {
		this.chosenStartTimestamp = chosenStartTimestamp;
	}

	public Date getChosenStartTimestamp() {
		return chosenStartTimestamp;
	}

	public void setChosenDurationMinutes(int chosenDurationMinutes) {
		this.chosenDurationMinutes = chosenDurationMinutes;
	}

	public int getChosenDurationMinutes() {
		return chosenDurationMinutes;
	}

	public void setRandomVehiclesCtrl(RandomVehiclesCtrl randomVehiclesCtrl) {
		this.randomVehiclesCtrl = randomVehiclesCtrl;
	}

	public RandomVehiclesCtrl getRandomVehiclesCtrl() {
		return randomVehiclesCtrl;
	}
	
	public void saveAttraction() {
		TrafficNode node = trafficServerLocal.getTrafficGraphExtesions().getGraph().getNodeClosestTo(getCoordinate());
		trafficServerLocal.update(new EventList(idGenerator.getNextId(),
			new LinkedList<>(
				Arrays.asList(
					new AttractionTrafficEvent(
						trafficServerLocal,
						System.currentTimeMillis(),
						-1,
						chosenStartTimestamp.getTime(),
						chosenStartTimestamp.getTime()+chosenDurationMinutes*60,
						node,
						randomVehiclesCtrl.generateCreateRandomVehicleData()
					)
				)
			),
			serialVersionUID));
	}
}
