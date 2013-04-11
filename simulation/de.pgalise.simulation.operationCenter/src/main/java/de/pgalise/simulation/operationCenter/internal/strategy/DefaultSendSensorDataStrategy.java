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
 
package de.pgalise.simulation.operationCenter.internal.strategy;

import com.google.inject.Inject;

import de.pgalise.simulation.operationCenter.internal.OCWebSocketService;
/**
 * The default implementation of a send sensor data strategy sends every single sensor data.
 * @author Timo
 */
public class DefaultSendSensorDataStrategy extends SendSensorDataStrategy {

	/**
	 * Contructor
	 * @param webSocketService
	 */
	@Inject
	public DefaultSendSensorDataStrategy(OCWebSocketService webSocketService) {
		super(webSocketService);
	}

	@Override
	protected boolean isItTimeToSend(long timestamp) {
		return true;
	}
}
