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

import de.pgalise.simulation.operationCenter.internal.OCWebSocketService;

/**
 * The interval send sensor data strategy sends the data of a whole interval to the user.
 * @author Timo
 */
public class IntervalSendSensorDataStrategy extends SendSensorDataStrategy {
	
	private long currentTimestamp;
	
	/**
	 * Constructor
	 * @param webSocketService
	 */
	public IntervalSendSensorDataStrategy(OCWebSocketService webSocketService) {
		super(webSocketService);
	}

	@Override
	public void init(long startTimestamp, long endTimestamp, long interval) {
		super.init(startTimestamp, endTimestamp, interval);
		this.currentTimestamp = startTimestamp;
	}

	@Override
	protected boolean isItTimeToSend(long timestamp) {
		/* Send when one update step is over: */
		if(this.currentTimestamp < timestamp) {
			this.currentTimestamp = timestamp;
			return true;
		}
		
		return false;
	}
}
