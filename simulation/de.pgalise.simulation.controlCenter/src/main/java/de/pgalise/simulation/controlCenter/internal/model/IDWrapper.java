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
 
package de.pgalise.simulation.controlCenter.internal.model;

import java.util.List;
import java.util.UUID;

/**
 * Wrapps integer ids and UUIDs. This is needed, if the server creates random
 * vehicles and the client needs to know the used IDs.
 * @author Timo
 */
public class IDWrapper {
	private List<Integer> integerIDList;
	private List<UUID> uuidList;
	
	/**
	 * Constructor
	 * @param integerIDList
	 * 			all used integer IDs
	 * @param uuidList
	 * 			all used UUIDs
	 */
	public IDWrapper(List<Integer> integerIDList, List<UUID> uuidList) {
		this.integerIDList = integerIDList;
		this.uuidList = uuidList;
	}

	public List<Integer> getIntegerIDList() {
		return integerIDList;
	}

	public void setIntegerIDList(List<Integer> integerIDList) {
		this.integerIDList = integerIDList;
	}

	public List<UUID> getUuidList() {
		return uuidList;
	}

	public void setUuidList(List<UUID> uuidList) {
		this.uuidList = uuidList;
	}
}
