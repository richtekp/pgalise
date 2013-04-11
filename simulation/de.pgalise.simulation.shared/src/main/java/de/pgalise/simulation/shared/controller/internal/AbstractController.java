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

import de.pgalise.simulation.shared.controller.Controller;
import de.pgalise.simulation.shared.controller.InitParameter;
import de.pgalise.simulation.shared.controller.StartParameter;
import de.pgalise.simulation.shared.event.SimulationEventList;
import de.pgalise.simulation.shared.exception.InitializationException;

/**
 * Skeleton class for the implementation of a Controller. When using this class it's not necessary anymore to handle the
 * different states of a controller.
 * 
 * @author Mustafa
 * @version 1.0
 */
public abstract class AbstractController implements Controller {

	/**
	 * Logger
	 */
	private static final Logger log = LoggerFactory.getLogger(AbstractController.class);

	/**
	 * Current status
	 */
	private StatusEnum status = StatusEnum.INIT;

	@Override
	public final void init(InitParameter param) throws InitializationException, IllegalStateException {
		switch (status) {
			case INIT:
//				log.info("Initializing "+getName()+" ...");
				onInit(param);
				status = StatusEnum.INITIALIZED;

				// Log
				log.info(getName() + " initialized");
				break;
			default:
				throw new IllegalStateException(getName()
						+ " can not be initialized in its current state: " + status);
		}
	}

	@Override
	public final void reset() throws IllegalStateException {
		switch (status) {
			case INITIALIZED:
			case STOPPED:
//				log.info("Resetting "+getName()+" ...");
				onReset();
				status = StatusEnum.INIT;

				// Log
				log.info(getName() + " reset");
				break;
			default:
				throw new IllegalStateException(getName()
						+ " can not be reset in its current state: " + status);
		}
	}

	@Override
	public final void start(StartParameter param) throws IllegalStateException {
		switch (status) {
			case INITIALIZED:
//				log.info("Starting "+getName()+" ...");
				onStart(param);
				status = StatusEnum.STARTED;

				// Log
				log.info(getName() + " started");
				break;
			case STOPPED:
//				log.info("Resuming "+getName()+" ...");
				onResume();
				status = StatusEnum.STARTED;

				// Log
				log.info(getName() + " resumed");
				break;
			default:
				throw new IllegalStateException(getName()
						+ " can not be started in its current state: " + status);
		}
	}

	@Override
	public final void stop() throws IllegalStateException {
		switch (status) {
			case STARTED:
//				log.info("Stopping "+getName()+" ...");
				onStop();
				status = StatusEnum.STOPPED;

				// Log
				log.info(getName() + " stopped");
				break;
			default:
				throw new IllegalStateException(getName()
						+ " can not be stopped in its current state: " + status);
		}
	}

	@Override
	public StatusEnum getStatus() {
		return status;
	}

	/**
	 * Should not be used by sub classes. For testing purpose only.
	 * 
	 * @param status
	 */
	public void setStatus(StatusEnum status) {
		this.status = status;
	}

	@Override
	public final void update(SimulationEventList simulationEventList) throws IllegalStateException {
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
	 * Implementation specific method. Will be automatically called when initializing this controller.
	 * 
	 * @param param
	 * @see Controller#init(InitParameter)
	 * @throws InitializationException
	 */
	protected abstract void onInit(InitParameter param) throws InitializationException;

	/**
	 * Implementation specific method. Will be automatically called when resetting this controller.
	 */
	protected abstract void onReset();

	/**
	 * Implementation specific method. Will be automatically called when starting this controller.
	 * 
	 * @param param
	 * @see Controller#start(StartParameter)
	 */
	protected abstract void onStart(StartParameter param);

	/**
	 * Implementation specific method. Will be automatically called when stopping this controller.
	 * 
	 * @see Controller#stop()
	 */
	protected abstract void onStop();

	/**
	 * Implementation specific method. Will be automatically called when this controller is started after it has been
	 * stopped.
	 */
	protected abstract void onResume();

	/**
	 * Implementation specific method. Will be automatically called when updating this controller.
	 * 
	 * @param simulationEventList
	 * @see Controller#update(SimulationEventList)
	 */
	protected abstract void onUpdate(SimulationEventList simulationEventList);
}
