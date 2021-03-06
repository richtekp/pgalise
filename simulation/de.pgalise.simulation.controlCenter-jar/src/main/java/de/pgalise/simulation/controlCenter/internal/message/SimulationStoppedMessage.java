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
 
package de.pgalise.simulation.controlCenter.internal.message;

/**
 * Send if the simulation is stopped. E.g. because the simulation time is over.
 * 
 * @author Timo
 */
public class SimulationStoppedMessage extends IdentifiableControlCenterMessage<Void> {

	/**
	 * Constructor
	 */
	public SimulationStoppedMessage() {
		super(null,MessageTypeEnum.SIMULATION_STOPPED, null);
	}

	/**
	 * Constructor
	 * 
	 * @param messageID
	 *            ID
	 */
	public SimulationStoppedMessage(Long messageID) {
		super(messageID, MessageTypeEnum.SIMULATION_STOPPED, null);
	}
}
