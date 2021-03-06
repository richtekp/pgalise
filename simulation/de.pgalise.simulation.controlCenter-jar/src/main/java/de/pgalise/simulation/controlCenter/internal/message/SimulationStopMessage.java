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
 * This message is send, if the user wants to stop the simulation.
 * 
 * @author dhoeting
 * @author Timo
 */
public class SimulationStopMessage extends IdentifiableControlCenterMessage<Void> {
	/**
	 * Default
	 */
	public SimulationStopMessage() {
		super(null,MessageTypeEnum.SIMULATION_STOP, null);
	}

	/**
	 * Constructor
	 * 
	 * @param messageID
	 *            ID
	 */
	public SimulationStopMessage(Long messageID) {
		super(messageID,
			MessageTypeEnum.SIMULATION_STOP,
			null);
	}
}
