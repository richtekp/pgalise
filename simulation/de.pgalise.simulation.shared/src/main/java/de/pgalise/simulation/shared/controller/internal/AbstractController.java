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
package de.pgalise.simulation.shared.controller.internal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.pgalise.simulation.service.Controller;
import de.pgalise.simulation.service.ControllerStatusEnum;
import de.pgalise.simulation.service.InitParameter;
import de.pgalise.simulation.shared.controller.StartParameter;
import de.pgalise.simulation.shared.event.Event;
import de.pgalise.simulation.shared.event.EventList;
import de.pgalise.simulation.shared.exception.InitializationException;
import de.pgalise.simulation.shared.entity.Identifiable;

/**
 * Skeleton class for the implementation of a Controller. When using this class
 * it's not necessary anymore to handle the different states of a controller.
 *
 * @param <E>
 * @param <S>
 * @param <I>
 * @author Mustafa
 * @version 1.0
 */
public abstract class AbstractController<E extends Event, S extends StartParameter, I extends InitParameter>
	extends Identifiable implements Controller<E, S, I> {

	/**
	 * Logger
	 */
	private static final Logger log = LoggerFactory.getLogger(
		AbstractController.class);
	private static final long serialVersionUID = 1L;

	/**
	 * Current status
	 */
	private ControllerStatusEnum status = ControllerStatusEnum.INIT;

	@Override
	public void init(I param) {
		switch (status) {
			case INIT:
				try {
					//				log.info("Initializing "+getName()+" ...");
					onInit(param);
				} catch (InitializationException ex) {
					throw new RuntimeException(ex);
				}
				status = ControllerStatusEnum.INITIALIZED;

				// Log
				log.info(getName() + " initialized");
				break;
			default:
				throw new IllegalStateException(getName()
					+ " can not be initialized in its current state: " + status);
		}
	}

	@Override
	public void reset() throws IllegalStateException {
		switch (status) {
			case INITIALIZED:
			case STOPPED:
//				log.info("Resetting "+getName()+" ...");
				onReset();
				status = ControllerStatusEnum.INIT;

				// Log
				log.info(getName() + " reset");
				break;
			default:
				throw new IllegalStateException(getName()
					+ " can not be reset in its current state: " + status);
		}
	}

	@Override
	public void start(S param) throws IllegalStateException {
		switch (status) {
			case INIT:
				throw new IllegalStateException(
					"The controller is still in INIT state which means that initialization has not taken place, is currently running on another thread or failed before");
			case INITIALIZED:
//				log.info("Starting "+getName()+" ...");
				onStart(param);
				status = ControllerStatusEnum.STARTED;

				// Log
				log.info(getName() + " started");
				break;
			case STOPPED:
//				log.info("Resuming "+getName()+" ...");
				onResume();
				status = ControllerStatusEnum.STARTED;

				// Log
				log.info(getName() + " resumed");
				break;
			default:
				throw new IllegalStateException(getName()
					+ " can not be started in its current state: " + status);
		}
	}

	@Override
	public void stop() throws IllegalStateException {
		switch (status) {
			case STARTED:
//				log.info("Stopping "+getName()+" ...");
				onStop();
				status = ControllerStatusEnum.STOPPED;

				// Log
				log.info(getName() + " stopped");
				break;
			default:
				throw new IllegalStateException(getName()
					+ " can not be stopped in its current state: " + status);
		}
	}

	@Override
	public ControllerStatusEnum getStatus() {
		return status;
	}

	/**
	 * Should not be used by sub classes. For testing purpose only.
	 *
	 * @param status
	 */
	public void setStatus(ControllerStatusEnum status) {
		this.status = status;
	}

	@Override
	public void update(EventList<E> simulationEventList) throws IllegalStateException {
		switch (status) {
			case STARTED:
//				log.debug("Updating "+getName() +" on simulation time "+simulationEventList.getTimestamp());
				onUpdate(simulationEventList);
				break;
			default:
				throw new IllegalStateException(getName()
					+ " can not be updated in its current state: " + status);
		}
	}

	/**
	 * Implementation specific method. Will be automatically called when
	 * initializing this controller.
	 *
	 * @param param
	 * @see Controller#init(InitParameter)
	 * @throws InitializationException
	 */
	protected abstract void onInit(I param) throws InitializationException;

	/**
	 * Implementation specific method. Will be automatically called when resetting
	 * this controller.
	 */
	protected abstract void onReset();

	/**
	 * Implementation specific method. Will be automatically called when starting
	 * this controller.
	 *
	 * @param param
	 * @see Controller#start(StartParameter)
	 */
	protected abstract void onStart(S param);

	/**
	 * Implementation specific method. Will be automatically called when stopping
	 * this controller.
	 *
	 * @see Controller#stop()
	 */
	protected abstract void onStop();

	/**
	 * Implementation specific method. Will be automatically called when this
	 * controller is started after it has been stopped.
	 */
	protected abstract void onResume();

	/**
	 * Implementation specific method. Will be automatically called when updating
	 * this controller.
	 *
	 * @param simulationEventList
	 * @throws IllegalArgumentException if <tt>simulationEventList</tt> is <code>null</code>
	 * @see Controller#update(SimulationEventList)
	 */
	protected abstract void onUpdate(EventList<E> simulationEventList) throws IllegalArgumentException;

	@Override
	public String getName() {
		return this.getClass().getName();
	}
}
