/* 
 * Copyright 2013 PG Alise (http://www.pg-alise.de/)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 */
 
package de.pgalise.simulation.internal.event;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.pgalise.simulation.energy.EnergyController;
import de.pgalise.simulation.event.EventInitiator;
import de.pgalise.simulation.internal.DefaultFrontController;
import de.pgalise.simulation.service.ServiceDictionary;
import de.pgalise.simulation.service.Controller;
import de.pgalise.simulation.shared.controller.InitParameter;
import de.pgalise.simulation.service.SimulationComponent;
import de.pgalise.simulation.service.StatusEnum;
import de.pgalise.simulation.shared.controller.StartParameter;
import de.pgalise.simulation.shared.controller.internal.AbstractController;
import de.pgalise.simulation.shared.event.AbstractEvent;
import de.pgalise.simulation.shared.event.Event;
import de.pgalise.simulation.shared.event.EventList;
import de.pgalise.simulation.shared.event.energy.EnergyEvent;
import de.pgalise.simulation.traffic.event.AbstractTrafficEvent;
import de.pgalise.simulation.shared.event.weather.WeatherEvent;
import de.pgalise.simulation.shared.exception.InitializationException;
import de.pgalise.simulation.traffic.TrafficController;
import de.pgalise.simulation.visualizationcontroller.ControlCenterController;
import de.pgalise.simulation.visualizationcontroller.ControlCenterControllerLoader;
import de.pgalise.simulation.visualizationcontroller.OperationCenterController;
import de.pgalise.simulation.visualizationcontroller.OperationCenterControllerLoader;
import de.pgalise.simulation.weather.service.WeatherController;

/**
 * The default implementation of the {@link EventInitiator}. It starts a thread after start, which runs from 
 * {@link InitParameter#getStartTimestamp()} till {@link InitParameter#getEndTimestamp()} and updates the current timestamp after every
 * iteration with {@link InitParameter#getInterval()}. The elapsed real time between every interval steps is at least {@link InitParameter#getClockGeneratorInterval()}.
 * In every iteration the update functions of the {@link SimulationComponent} controllers is called with the {@link SimulationEventList} for the current interval. The
 * update order in every iteration is: {@link WeatherController}, {@link EnergyController}, {@link DefaultFrontController}, {@link OperationCenterController}, 
 * {@link ControlCenterControllerLoader} and {@link TrafficController}. 
 * @author Timo
 */
@Lock(LockType.READ)
@Singleton(name = "de.pgalise.simulation.simulationController.event.EventInitiator")
@Local
public class DefaultEventInitiator extends AbstractController<Event> implements EventInitiator {
	private static final String NAME = "EventInitiator";
	private static SimpleDateFormat sdf;

	@Override
	public EventList<?> getEventList() {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	/**
	 * Event thread
	 * 
	 * @author Timo
	 * @version 1.0
	 */
	private class EventThread extends Thread {
		private long lastLogInfo = -1;
		private long lastLogDebug = -1;
		
		@Override
		@SuppressWarnings("SleepWhileInLoop")
		public void run() {
			while (DefaultEventInitiator.this.getStatus() == StatusEnum.STARTED) {
				if (DefaultEventInitiator.this.currentTimestamp <= DefaultEventInitiator.this.endTimestamp) {
					long startTimestamp;
					synchronized (DefaultEventInitiator.this.lockThreadLoop) {
						startTimestamp = new Date().getTime();

						// Log
						if(lastLogInfo == -1 || (currentTimestamp-lastLogInfo)>=300000) {
							log.info("Current simulation time: "
									+ sdf.format(new Date(DefaultEventInitiator.this.currentTimestamp)));
							lastLogInfo = currentTimestamp;
						}
						
						if(lastLogDebug == -1 || (currentTimestamp-lastLogDebug)>=60000) {
							log.debug("Current simulation time: "
									+ sdf.format(new Date(DefaultEventInitiator.this.currentTimestamp)));
							lastLogDebug = currentTimestamp;
						}

						/* WeatherService / Controller */
						List<WeatherEvent> weatherEventList;
						synchronized (weatherEventMap) {
							weatherEventList = DefaultEventInitiator.this.weatherEventMap
									.get(DefaultEventInitiator.this.currentTimestamp);
							DefaultEventInitiator.this.weatherEventMap
									.remove(DefaultEventInitiator.this.currentTimestamp);
						}
						if (weatherEventList == null) {
							weatherEventList = new ArrayList<>();
						}

						DefaultEventInitiator.this.serviceDictionary.getController(WeatherController.class).update(
								new EventList<>(weatherEventList, DefaultEventInitiator.this.currentTimestamp,
										UUID.randomUUID()));

						/* Update EnergyController: */
						List<EnergyEvent> energyEventList;
						synchronized (DefaultEventInitiator.this.energyEventMap) {
							energyEventList = DefaultEventInitiator.this.energyEventMap
									.get(DefaultEventInitiator.this.currentTimestamp);
							DefaultEventInitiator.this.energyEventMap
									.remove(DefaultEventInitiator.this.currentTimestamp);
						}
						if (energyEventList == null) {
							energyEventList = new ArrayList<>();
						}

						DefaultEventInitiator.this.serviceDictionary.getController(EnergyController.class).update(
								new EventList(energyEventList, DefaultEventInitiator.this
										.getCurrentTimestamp(), UUID.randomUUID()));

						/*
						 * FrontController...
						 */
						for(Controller<?> c : DefaultEventInitiator.this.frontControllerList) {
							c.update(new EventList(new ArrayList<>(),
									DefaultEventInitiator.this.currentTimestamp, UUID.randomUUID()));
						}

						/* Traffic */
						List<Event> trafficEventList;
						synchronized (trafficEventMap) {
							trafficEventList = DefaultEventInitiator.this.trafficEventMap
									.get(DefaultEventInitiator.this.currentTimestamp);
							DefaultEventInitiator.this.trafficEventMap
									.remove(DefaultEventInitiator.this.currentTimestamp);
						}
						if (trafficEventList == null) {
							trafficEventList = new ArrayList<>();
						}
						/* Update the operation center. It needs only the traffic events. */
						DefaultEventInitiator.this.operationCenterController.update(new EventList<>(
								new ArrayList<>(trafficEventList), DefaultEventInitiator.this.currentTimestamp, UUID
										.randomUUID()));
						
						/* Update the control center. It needs only the time: */
						DefaultEventInitiator.this.controlCenterController.update(new EventList(new ArrayList<AbstractEvent>(), 
								DefaultEventInitiator.this.currentTimestamp, UUID.randomUUID()));

						DefaultEventInitiator.this.serviceDictionary.getController(TrafficController.class).update(
								new EventList(new ArrayList<>(trafficEventList),
										DefaultEventInitiator.this.currentTimestamp, UUID.randomUUID()));

						/* update time */
						synchronized (DefaultEventInitiator.this.lockTimestamp) {
							DefaultEventInitiator.this.currentTimestamp += DefaultEventInitiator.this.interval;
						}
					}
					/* Perform clock generator interval: */
					long remainTime = DefaultEventInitiator.this.clockGeneratorInterval - (new Date().getTime() - startTimestamp);
					if (remainTime >= 0 && remainTime < DefaultEventInitiator.this.clockGeneratorInterval) {
						try {
							Thread.sleep(remainTime);
						} catch (InterruptedException e) {
							log.warn(e.getLocalizedMessage());
						}
					}
				} else {
					DefaultEventInitiator.this.onStop();
					return;
				}
			}
		}
	}

	/**
	 * Logger
	 */
	private static final Logger log = LoggerFactory.getLogger(DefaultEventInitiator.class);

	/**
	 * Map with timestamp and future energy events
	 */
	private final Map<Long, List<EnergyEvent>> energyEventMap = new HashMap<>();

	/**
	 * Event thread
	 */
	private Thread eventThread;

	/**
	 * Lock object
	 */
	private final Object lockThreadLoop, lockTimestamp;

	private long currentTimestamp, endTimestamp, interval, clockGeneratorInterval;

	/**
	 * Map with timestamp and traffic events
	 */
	private final Map<Long, List<Event>> trafficEventMap = new HashMap<>();

	/**
	 * Map with timestamp and weather events
	 */
	private final Map<Long, List<WeatherEvent>> weatherEventMap = new HashMap<>();

	@EJB
	private ServiceDictionary serviceDictionary;
	
	@EJB
	private OperationCenterControllerLoader operationCenterControllerLoader;
	
	@EJB
	private ControlCenterControllerLoader controlCenterControllerLoader;

	private OperationCenterController operationCenterController;
	
	private ControlCenterController controlCenterController;
	
	private List<Controller<?>> frontControllerList;

	/**
	 * Default constructor
	 */
	public DefaultEventInitiator() {
		this.lockThreadLoop = new Object();
		this.lockTimestamp = new Object();
		sdf = new SimpleDateFormat();
		sdf.applyPattern( "dd.MM.yy/HH:mm:ss" );
	}

	/**
	 * Automatically called on post construct
	 */
	@PostConstruct
	public void onPostConstruct() {
		this.controlCenterController = this.controlCenterControllerLoader.loadControlCenterController();
		this.operationCenterController = this.operationCenterControllerLoader.loadOperationCenterController();
	}
	
	@Override
	public Thread getEventThread() {
		return this.eventThread;
	}

	/**
	 * Adds a new energy events to the energy event map.
	 * 
	 * @param energyEvent
	 *            Energy event
	 * @param timestamp
	 *            Timestamp
	 */
	private void addEnergyEvent(EnergyEvent energyEvent, long timestamp) {
		synchronized (this.energyEventMap) {
			long bestTimestamp = this.findBestTimestamp(timestamp);
			List<EnergyEvent> energyEventList = this.energyEventMap.get(bestTimestamp);

			if (energyEventList == null) {
				energyEventList = new ArrayList<>();
				this.energyEventMap.put(bestTimestamp, energyEventList);
			}

			energyEventList.add(energyEvent);
		}
	}

	/**
	 * Adds a new traffic event to the traffic event map.
	 * 
	 * @param trafficEvent
	 *            Traffic event
	 * @param timestamp
	 *            timestamp
	 */
	private void addTrafficEvent(Event trafficEvent, long timestamp) {
		synchronized (this.trafficEventMap) {
			long bestTimestamp = this.findBestTimestamp(timestamp);
			List<Event> trafficEventList = this.trafficEventMap.get(bestTimestamp);

			if (trafficEventList == null) {
				trafficEventList = new ArrayList<>();
				this.trafficEventMap.put(bestTimestamp, trafficEventList);
			}

			trafficEventList.add(trafficEvent);
		}
	}

	/**
	 * Adds a new weather event to the weather event map.
	 * 
	 * @param weatherEvent
	 *            Weather event
	 * @param timestamp
	 *            Timestamp
	 */
	private void addWeatherEvent(WeatherEvent weatherEvent, long timestamp) {
		synchronized (this.weatherEventMap) {
			long bestTimestamp = this.findBestTimestamp(timestamp);
			List<WeatherEvent> weatherEventList = this.weatherEventMap.get(bestTimestamp);

			if (weatherEventList == null) {
				weatherEventList = new ArrayList<>();
				this.weatherEventMap.put(bestTimestamp, weatherEventList);
			}

			weatherEventList.add(weatherEvent);
		}
	}

	/**
	 * Gives the best matching timestamp in the interval.
	 * 
	 * @param timestamp
	 *            Timestamp
	 * @param currentTimestamp
	 *            Actual timestamp
	 * @param interval
	 *            Interval
	 * @return best matching timestamp
	 */
	private long findBestTimestamp(long timestamp) {
		synchronized (this.lockTimestamp) {
			long bestTimestamp = this.currentTimestamp;
			for (; timestamp > bestTimestamp; bestTimestamp += this.interval) {
				
			}

			if (bestTimestamp == this.currentTimestamp) {
				return this.currentTimestamp + this.interval;
			}

			return bestTimestamp;
		}
	}

	/**
	 * Resets the event thread and starts it
	 */
	private void startEventThread() {
		this.eventThread = new EventThread();
		this.eventThread.start();
	}

	/**
	 * Stops the event thread
	 */
	private void stopEventThread() {
		if (this.eventThread != null && this.eventThread.isAlive()) {
			try {
				this.eventThread.interrupt();
			} catch (Exception e) {
				log.error(e.getLocalizedMessage(), e);
			}
		}
	}

	@Override
	public void addSimulationEventList(EventList<?> simulationEventList) {
		if ((simulationEventList.getEventList() != null) && !simulationEventList.getEventList().isEmpty()) {
			for (Event event : simulationEventList.getEventList()) {

				/*
				 * Add new events here!
				 */

				if (event instanceof AbstractTrafficEvent) {
					this.addTrafficEvent((AbstractTrafficEvent) event, simulationEventList.getTimestamp());
				} else if (event instanceof WeatherEvent) {
					this.addWeatherEvent((WeatherEvent) event, simulationEventList.getTimestamp());
				} else if (event instanceof EnergyEvent) {
					this.addEnergyEvent((EnergyEvent) event, simulationEventList.getTimestamp());
				}
			}
		}
	}

	@Override
	protected void onStart(StartParameter param) {
		this.startEventThread();
	}

	@Override
	protected void onStop() {
		this.stopEventThread();
	}

	@Override
	protected void onUpdate(EventList<Event> simulationEventList) {
		this.addSimulationEventList(simulationEventList);
	}

	@Override
	protected void onInit(InitParameter param) throws InitializationException {
		this.endTimestamp = param.getEndTimestamp();
		this.interval = param.getInterval();
		this.clockGeneratorInterval = param.getClockGeneratorInterval();
		this.currentTimestamp = param.getStartTimestamp();
	}

	@Override
	protected void onReset() {

	}

	@Override
	public long getCurrentTimestamp() {
		return this.currentTimestamp;
	}

	@Override
	public void setOperationCenterController(OperationCenterController operationCenterController) {
		this.operationCenterController = operationCenterController;
	}

	/**
	 * Use this only for J-Unit tests.
	 * 
	 * @param serviceDictionary
	 */
	public void _setServiceDictionary(ServiceDictionary serviceDictionary) {
		this.serviceDictionary = serviceDictionary;
	}

	@Override
	protected void onResume() {
		// Nothing to do

	}

	@Override
	public void setControlCenterController(
			ControlCenterController controlCenterController) {
		this.controlCenterController = controlCenterController;
	}

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public void setFrontController(List<Controller<?>> controller) {
		this.frontControllerList = controller;
	}
}
