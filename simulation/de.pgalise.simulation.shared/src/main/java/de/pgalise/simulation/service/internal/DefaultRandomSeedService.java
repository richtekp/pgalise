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
 
package de.pgalise.simulation.service.internal;

import de.pgalise.simulation.service.RandomSeedService;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;

import de.pgalise.simulation.service.InitParameter;
import de.pgalise.simulation.shared.controller.StartParameter;
import de.pgalise.simulation.shared.controller.internal.AbstractController;
import de.pgalise.simulation.shared.event.Event;
import de.pgalise.simulation.shared.event.EventList;
import de.pgalise.simulation.shared.exception.InitializationException;
import javax.ejb.Local;
import javax.ejb.Remote;

/**
 * The default implementation of random seed service.
 * It uses the start time stamp and the hashcode of the class name
 * for the seed generation.
 * 
 * @author Timo
 */
@Lock(LockType.READ)
@Singleton(name = "de.pgalise.simulation.service.RandomSeedService", mappedName = "de.pgalise.simulation.service.RandomSeedService")
@Local(RandomSeedService.class)
@Remote(RandomSeedService.class)
public class DefaultRandomSeedService extends AbstractController<Event, StartParameter, InitParameter> implements RandomSeedService {

	/**
	 * simulation start timestamp
	 */
	private long startTimestamp;

	/**
	 * Default
	 */
	public DefaultRandomSeedService() {}

	@Override
	public long getSeed(String className) {

		return (this.startTimestamp + className.hashCode());
	}

	@Override
	public long getStartTimestamp() {
		return this.startTimestamp;
	}

	@Override
	public void init(long startTimestamp) {
		this.startTimestamp = startTimestamp;
	}

	@Override
	protected void onInit(InitParameter param) throws InitializationException {
	}

	@Override
	protected void onReset() {
	}

	@Override
	protected void onStart(StartParameter param) {
	}

	@Override
	protected void onStop() {
	}

	@Override
	protected void onResume() {
	}

	@Override
	protected void onUpdate(EventList<Event> simulationEventList) {
	}
}
