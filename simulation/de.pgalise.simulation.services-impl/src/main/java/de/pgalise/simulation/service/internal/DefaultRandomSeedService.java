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

import javax.ejb.Local;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Remote;
import javax.ejb.Singleton;

import de.pgalise.simulation.service.RandomSeedService;
import de.pgalise.simulation.service.RandomSeedServiceLocal;
import de.pgalise.simulation.shared.controller.InitParameter;
import de.pgalise.simulation.shared.controller.StartParameter;
import de.pgalise.simulation.shared.controller.internal.AbstractController;
import de.pgalise.simulation.shared.event.SimulationEventList;
import de.pgalise.simulation.shared.exception.InitializationException;

/**
 * The default implementation of random seed service.
 * It uses the start time stamp and the hashcode of the class name
 * for the seed generation.
 * 
 * @author Timo
 */
@Lock(LockType.READ)
@Singleton(name = "de.pgalise.simulation.service.RandomSeedService")
@Remote(RandomSeedService.class)
@Local(RandomSeedServiceLocal.class)
public class DefaultRandomSeedService extends AbstractController implements RandomSeedServiceLocal {

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

	public long getStartTimestamp() {
		return this.startTimestamp;
	}

	@Override
	public void init(long startTimestamp) {
		this.startTimestamp = startTimestamp;
	}

	@Override
	protected void onInit(InitParameter param) throws InitializationException {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	protected void onReset() {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	protected void onStart(StartParameter param) {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	protected void onStop() {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	protected void onResume() {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	protected void onUpdate(SimulationEventList simulationEventList) {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}
}
