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

import com.vividsolutions.jts.geom.Coordinate;

/**
 * Wraps lat/lng with an ID, position and information if it's on a street or/and on a junction.
 * @author Timo
 */
public class Node {
	private boolean onStreet, onJunction;
	private String id;
	private Coordinate position;
	
	/**
	 * Constructor
	 * @param onStreet
	 * @param onJunction
	 * 			if onJuction, its also onStreet.
	 * @param id
	 * @param position
	 */
	public Node(boolean onStreet, boolean onJunction, String id, Coordinate position) {
		this.onStreet = onStreet;
		this.onJunction = onJunction;
		this.id = id;
		this.position = position;
	}
	
	public Coordinate getPosition() {
		return position;
	}
	
	public void setPosition(Coordinate position) {
		this.position = position;
	}

	public boolean isOnStreet() {
		return onStreet;
	}

	public void setOnStreet(boolean onStreet) {
		this.onStreet = onStreet;
	}

	public boolean isOnJunction() {
		return onJunction;
	}

	public void setOnJunction(boolean onJunction) {
		this.onJunction = onJunction;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
}
