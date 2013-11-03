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

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import de.pgalise.simulation.shared.energy.EnergyProfileEnum;
import com.vividsolutions.jts.geom.Coordinate;
import de.pgalise.simulation.shared.city.Boundary;

/**
 * Contains streets, bus stops and landuse information.
 * Its an interface, because there can be different ways to fill it and
 * implement the functions:
 * {@link CityInfrastructureData#getNearestNode(double, double)}, {@link CityInfrastructureData#getNearestStreetNode(double, double)
 * and {@link CityInfrastructureData#getNodesInBoundary(Boundary)
 * 
 * @author Timo
 */
public interface CityInfrastructureData extends Serializable {
	/**
	 * Returns a list of BusStops.
	 * 
	 * @return
	 */
	public List<BusStop> getBusStops();

	/**
	 * Returns all cycle ways. Some of the nodes can be instances of @TrafficLight.
	 * 
	 * @return
	 */
	public List<Way<?, ?>> getCycleWays();

	/**
	 * Returns a list of land use areas e.g. industrial parks.
	 * 
	 * @return
	 */
	public List<Way<?,?>> getLandUseWays();

	/**
	 * Returns a list of streets for cars. Some of the nodes can be instances of @TrafficLight.
	 * 
	 * @return
	 */
	public List<Way<?, ?>> getMotorWays();

	/**
	 * Returns a list of streets for cars with busstops as nodes. Some of the nodes can be instances of @TrafficLight.
	 * 
	 * @return
	 */
	public List<Way<?, ?>> getMotorWaysWithBusstops();

	/**
	 * Returns the nearest used node.
	 * 
	 * @param latitude
	 * @param longitude
	 * @return
	 */
	public NavigationNode getNearestNode(double latitude, double longitude);

	/**
	 * Returns the nearest used street node.
	 * @param latitude
	 * @param longitude
	 * @return
	 */
	public NavigationNode getNearestStreetNode(double latitude, double longitude);
	
	/**
	 * Returns the nearest used junction node.
	 * @param latitude
	 * @param longitude
	 * @return
	 */
	public NavigationNode getNearestJunctionNode(double latitude, double longitude);
	
	/**
	 * Returns a list with all used junction nodes.
	 * @return
	 */
	public List<NavigationNode> getJunctionNodes();
	
	/**
	 * Returns every used node. Some of the nodes can be instances of @TrafficLight.
	 * @return
	 */
	public List<NavigationNode> getNodes();
	
	/**
	 * Returns every street node.
	 * @return
	 */
	public List<NavigationNode> getStreetNodes();

	/**
	 * Returns a list of nodes with roundabouts.
	 * 
	 * @return
	 */
	public List<NavigationNode> getRoundAbouts();

	/**
	 * Returns all ways. Some of the nodes can be instances of @TrafficLight.
	 * 
	 * @return
	 */
	public List<Way<?, ?>> getWays();
	
	/**
	 * Returns the boundary of the map.
	 * @return
	 */
	public Boundary getBoundary();
	
	/**
	 * Returns the number of buildings.
	 * @param geolocation
	 * @param radiusInMeter
	 * @return
	 */
	public Map<EnergyProfileEnum, List<Building>> getBuildings(Coordinate geolocation, int radiusInMeter);
	
	/**
	 * Returns all buildings in the radius.
	 * @param centerPoint
	 * @param radiusInMeter
	 * @return
	 */
	public List<Building> getBuildingsInRadius(Coordinate centerPoint, int radiusInMeter);
	
	/**
	 * Returns all nodes in the boundary.
	 * @param boundary
	 * @return
	 */
	public List<NavigationNode> getNodesInBoundary(Boundary boundary);
	
	/**
	 * Returns all ways for cars and bikes.
	 * @return
	 */
	public List<Way<?, ?>> getCycleAndMotorways();
}
