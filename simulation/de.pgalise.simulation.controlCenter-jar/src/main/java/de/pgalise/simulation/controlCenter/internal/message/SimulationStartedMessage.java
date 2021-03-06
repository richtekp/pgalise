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
 * If the simulation could be started.
 * 
 * @author Timo
 */
public class SimulationStartedMessage extends IdentifiableControlCenterMessage<Void> {

	/**
	 * Constructor
	 * 
	 * @param messageID
	 *            ID
	 * @param content
	 *            Wrapps integer ids and UUIDs which are used. This is useful,
	 *            because the client can update its own used ids with this.
	 */
	public SimulationStartedMessage(Long messageID) {
		super(messageID, MessageTypeEnum.SIMULATION_RUNNING, null);
	}
}
