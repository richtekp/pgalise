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
 
package de.pgalise.simulation.traffic.server.scheduler;

import de.pgalise.simulation.traffic.server.scheduler.Scheduler.Modus;

/**
 * To prohibit other components to manipulate the schedule plan
 * during the iterating through the expired items the Administration class was introduced.
 * The owner of the Administration object to a Scheduler gets privileged access
 * and is therefore the only one who can change its access type (to READ or WRITE).
 * 
 * @see Scheduler
 * @see AccessorPattern
 * @author mustafa
 *
 */
public class Administration {
	private Scheduler scheduler;
	
	public Administration(Scheduler scheduler) {
		this.scheduler = scheduler;
	}
	public Scheduler getScheduler() {
		return this.scheduler;
	}
	public void changeModus(Modus modus) {
		scheduler.changeModus(modus);
	}
}
