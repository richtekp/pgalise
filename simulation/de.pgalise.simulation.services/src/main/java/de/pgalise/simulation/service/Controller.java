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
 
package de.pgalise.simulation.service;

import de.pgalise.simulation.shared.controller.StartParameter;
import de.pgalise.simulation.shared.event.Event;
import de.pgalise.simulation.shared.event.EventList;
import de.pgalise.simulation.shared.exception.InitializationException;

/**
 * A simulation is divided into different sections (e.g. energy, traffic or weather).
 * Responsible for a particular section is a simulation component named Controller.
 * On each simulation step a Controller will be asked to update its part of the simulation.
 * The next simulation step will be processed when all Controller have been updated.
 *   
 * @param <E> 
 * @author mustafa
 *
 */
public interface Controller<E extends Event> extends SimulationComponent<E>, Service {
	/**
	 * Initializes this controller.
	 * Implementations can use this method to initialize default values
	 * or initialize dependencies.<br/><br/>
	 * 
	 * Changes its state to INITIALIZED.
	 * 
	 * @param param some parameter for initialization
	 * @throws InitializationException when an error occurred during the initialization
	 * @throws IllegalStateException when the state of this controller is not INIT
	 */
	public void init(InitParameter param) throws InitializationException, IllegalStateException;
	/**
	 * Resets this controller to its initial state.<br/><br/>
	 * Changes its state to INIT.
	 * 
	 * @throws IllegalStateException when the state of this controller is INIT or STARTED.
	 */
	public void reset() throws IllegalStateException;
	
	/**
	 * Starts this controller. From here on this controller has
	 * to be ready to get simulation updates.<br/>
	 * If the state of this controller is STOPPED it will resume its previous state.
	 * The start parameter will be ignored in this case.<br/><br/>
	 * 
	 * Changes its state to STARTED.
	 * 
	 * @param param some start parameter
	 * @throws IllegalStateException when the state of this controller is not INITIALIZED or STOPPED
	 */
	public void start(StartParameter param) throws IllegalStateException;
	
	/**
	 * Stops this controller. Its current state will be frozen.
	 * From here on it can not receive any simulation updates.
	 * Can be started again through {@link #start(StartParameter)}.
	 * <br/><br/>
	 * 
	 * Changes its state to STOPPED.
	 * 
	 * @throws IllegalStateException when the state of this controller is not STARTED
	 */
	public void stop() throws IllegalStateException;
	
	/**
	 * Called on each simulation step in order to update this controller's part of the simulation.
	 * 
	 * @param simulationEventList events to be processed on this update step
	 * @throws IllegalStateException when the state of this controller is not STARTED
	 */
	@Override
	public void update(EventList<E> simulationEventList) throws IllegalStateException;
	
	/**
	 * Returns this controller's current state.
	 * 
	 * @return current state
	 */
	public StatusEnum getStatus();
	
	/**
	 * @return name of this controller
	 */
	public String getName();
}
