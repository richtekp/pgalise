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
 
package de.pgalise.simulation.shared.city;

import java.util.List;
import java.util.Map;

import de.pgalise.simulation.shared.energy.EnergyProfileEnum;
import de.pgalise.simulation.shared.city.JaxRSCoordinate;
import de.pgalise.simulation.shared.city.Boundary;
import de.pgalise.simulation.shared.persistence.AbstractIdentifiable;
import de.pgalise.simulation.shared.persistence.Identifiable;
import javax.persistence.MappedSuperclass;

/**
 * Contains streets, bus stops and landuse information.
 * Its an interface, because there can be different ways to fill it and
 * implement the functions:
 * {@link CityInfrastructureData#getNearestNode(double, double)}, {@link CityInfrastructureData#getNearestStreetNode(double, double)
 * and {@link CityInfrastructureData#getNodesInBoundary(Boundary)
 * 
 * @author Timo
 */
@MappedSuperclass
public abstract class CityInfrastructureData<W extends Way> extends AbstractIdentifiable {
	private List<W> cycleWays;
	private List<W> landUseWays;
	private List<W> motorWays;
	private List<W> motorWaysWithBusStops;
	private List<NavigationNode> junctionNodes;
	private List<NavigationNode> nodes;
	private List<NavigationNode> streetNodes;
	private List<Way<?,?>> ways;
	private Boundary boundary;
	private List<W> cycleAndMotorways;  

	public void setWays(
		List<Way<?,?>> ways) {
		this.ways = ways;
	}

	public void setStreetNodes(List<NavigationNode> streetNodes) {
		this.streetNodes = streetNodes;
	}

	public void setNodes(List<NavigationNode> nodes) {
		this.nodes = nodes;
	}

	public void setMotorWaysWithBusStops(
		List<W> motorWaysWithBusStops) {
		this.motorWaysWithBusStops = motorWaysWithBusStops;
	}

	public void setMotorWays(
		List<W> motorWays) {
		this.motorWays = motorWays;
	}

	public void setLandUseWays(
		List<W> landUseWays) {
		this.landUseWays = landUseWays;
	}

	public void setJunctionNodes(List<NavigationNode> junctionNodes) {
		this.junctionNodes = junctionNodes;
	}

	public void setCycleWays(
		List<W> cycleWays) {
		this.cycleWays = cycleWays;
	}

	public void setCycleAndMotorways(
		List<W> cycleAndMotorways) {
		this.cycleAndMotorways = cycleAndMotorways;
	}

	public void setBoundary(Boundary boundary) {
		this.boundary = boundary;
	}

	public List<Way<?,?>> getWays() {
		return ways;
	}

	public List<NavigationNode> getStreetNodes() {
		return streetNodes;
	}

	public List<NavigationNode> getNodes() {
		return nodes;
	}

	public List<W> getMotorWaysWithBusStops() {
		return motorWaysWithBusStops;
	}

	public List<W> getMotorWays() {
		return motorWays;
	}

	public List<W> getLandUseWays() {
		return landUseWays;
	}

	public List<NavigationNode> getJunctionNodes() {
		return junctionNodes;
	}

	public List<W> getCycleWays() {
		return cycleWays;
	}

	public List<W> getCycleAndMotorways() {
		return cycleAndMotorways;
	}

	public Boundary getBoundary() {
		return boundary;
	}
	
	/**
	 * Returns the number of buildings.
	 * @param geolocation
	 * @param radiusInMeter
	 * @return
	 */
	public abstract Map<EnergyProfileEnum, List<Building>> getBuildings(JaxRSCoordinate geolocation, int radiusInMeter);
	
	/**
	 * Returns all buildings in the radius.
	 * @param centerPoint
	 * @param radiusInMeter
	 * @return
	 */
	public abstract List<Building> getBuildingsInRadius(JaxRSCoordinate centerPoint, int radiusInMeter);
	
	/**
	 * Returns all nodes in the boundary.
	 * @param boundary
	 * @return
	 */
	public abstract List<NavigationNode> getNodesInBoundary(Boundary boundary);

	/**
	 * Returns the nearest used node.
	 * 
	 * @param latitude
	 * @param longitude
	 * @return
	 */
	public abstract NavigationNode getNearestNode(double latitude, double longitude);

	/**
	 * Returns the nearest used street node.
	 * @param latitude
	 * @param longitude
	 * @return
	 */
	public abstract NavigationNode getNearestStreetNode(double latitude, double longitude);
	
	/**
	 * Returns the nearest used junction node.
	 * @param latitude
	 * @param longitude
	 * @return
	 */
	public abstract NavigationNode getNearestJunctionNode(double latitude, double longitude);
}
