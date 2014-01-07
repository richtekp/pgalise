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

import com.vividsolutions.jts.geom.Envelope;
import java.util.List;
import java.util.Map;

import de.pgalise.simulation.shared.energy.EnergyProfileEnum;
import de.pgalise.simulation.shared.persistence.AbstractIdentifiable;
import javax.persistence.MappedSuperclass;

/**
 * Contains streets, bus stops and landuse information. Its an interface,
 * because there can be different ways to fill it and implement the functions:
 * null {@link CityInfrastructureData#getNearestNode(double, double)}, {@link CityInfrastructureData#getNearestStreetNode(double, double)
 * and {@link CityInfrastructureData#getNodesInBoundary(Boundary)
 *
 * @author Timo
 */
@MappedSuperclass
public abstract class CityInfrastructureData<N extends NavigationNode, E extends NavigationEdge<N>, W extends Way<E, N>>
	extends AbstractIdentifiable {

	private static final long serialVersionUID = 1L;
	private List<W> cycleWays;
	private List<W> landUseWays;
	private List<W> motorWays;
	private List<W> motorWaysWithBusStops;
	private List<N> junctionNodes;
	private List<N> nodes;
	private List<N> streetNodes;
	private List<W> ways;
	private Envelope boundary;
	private List<W> cycleAndMotorways;

	public void setWays(
		List<W> ways) {
		this.ways = ways;
	}

	public void setStreetNodes(List<N> streetNodes) {
		this.streetNodes = streetNodes;
	}

	public void setNodes(List<N> nodes) {
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

	public void setJunctionNodes(List<N> junctionNodes) {
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

	public void setBoundary(Envelope boundary) {
		this.boundary = boundary;
	}

	public List<W> getWays() {
		return ways;
	}

	public List<N> getStreetNodes() {
		return streetNodes;
	}

	public List<N> getNodes() {
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

	public List<N> getJunctionNodes() {
		return junctionNodes;
	}

	public List<W> getCycleWays() {
		return cycleWays;
	}

	public List<W> getCycleAndMotorways() {
		return cycleAndMotorways;
	}

	public Envelope getBoundary() {
		return boundary;
	}

	/**
	 * Returns the number of buildings.
	 *
	 * @param geolocation
	 * @param radiusInMeter
	 * @return
	 */
	public abstract Map<EnergyProfileEnum, List<Building>> getBuildings(
		JaxRSCoordinate geolocation,
		int radiusInMeter);

	/**
	 * Returns all buildings in the radius.
	 *
	 * @param centerPoint
	 * @param radiusInMeter
	 * @return
	 */
	public abstract List<Building> getBuildingsInRadius(
		JaxRSCoordinate centerPoint,
		int radiusInMeter);

	/**
	 * Returns all nodes in the boundary.
	 *
	 * @param boundary
	 * @return
	 */
	public abstract List<NavigationNode> getNodesInBoundary(Envelope boundary);

	/**
	 * Returns the nearest used node.
	 *
	 * @param latitude
	 * @param longitude
	 * @return
	 */
	public abstract NavigationNode getNearestNode(double latitude,
		double longitude);

	/**
	 * Returns the nearest used street node.
	 *
	 * @param latitude
	 * @param longitude
	 * @return
	 */
	public abstract NavigationNode getNearestStreetNode(double latitude,
		double longitude);

	/**
	 * Returns the nearest used junction node.
	 *
	 * @param latitude
	 * @param longitude
	 * @return
	 */
	public abstract NavigationNode getNearestJunctionNode(double latitude,
		double longitude);
}
