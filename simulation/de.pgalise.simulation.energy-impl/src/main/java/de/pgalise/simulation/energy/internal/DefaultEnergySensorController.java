/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.energy.internal;

import de.pgalise.simulation.energy.EnergySensorControllerLocal;
import de.pgalise.simulation.energy.sensor.EnergySensor;
import de.pgalise.simulation.sensorFramework.Sensor;
import de.pgalise.simulation.service.InitParameter;
import de.pgalise.simulation.shared.controller.StartParameter;
import de.pgalise.simulation.shared.controller.internal.AbstractController;
import de.pgalise.simulation.shared.event.EventList;
import de.pgalise.simulation.shared.event.energy.EnergyEvent;
import de.pgalise.simulation.shared.exception.InitializationException;
import java.util.Set;
import javax.ejb.Stateful;

@Stateful
public class DefaultEnergySensorController extends AbstractController<EnergyEvent, StartParameter, InitParameter> implements EnergySensorControllerLocal {
	private Set<EnergySensor> sensorRegistry;
	
	@Override
	public void createSensor(EnergySensor sensor) {
		sensorRegistry.add(sensor);
	}

	@Override
	public void createSensors(
		Set<EnergySensor> sensors) {
		sensorRegistry.addAll(sensors);
	}

	@Override
	public void deleteSensor(EnergySensor sensor) {
		sensorRegistry.remove(sensor);
	}

	@Override
	public void deleteSensors(
		Set<EnergySensor> sensors) {
		sensorRegistry.removeAll(sensors);
	}

	@Override
	public boolean isActivated(EnergySensor sensor) {
		return sensorRegistry.contains(sensor) ? sensor.isActivated() : false;
	}

	@Override
	protected void onInit(InitParameter param) throws InitializationException {
	}

	@Override
	protected void onReset() {
		sensorRegistry.clear();
	}

	@Override
	protected void onStart(StartParameter param) {
		for(Sensor<?,?> sensor : sensorRegistry) {
			sensor.setActivated(true);
		}
	}

	@Override
	protected void onStop() {
		for(Sensor<?,?> sensor : sensorRegistry) {
			sensor.setActivated(false);
		}
	}

	@Override
	protected void onResume() {
	}

	@Override
	protected void onUpdate(
		EventList<EnergyEvent> simulationEventList) {
		for(EnergySensor energySensor : sensorRegistry) {
			energySensor.update(simulationEventList);
		}
	}

	@Override
	public Set<EnergySensor> getAllManagedSensors() {
		return sensorRegistry;
	}
}
