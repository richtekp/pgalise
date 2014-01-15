/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.traffic.service;

import de.pgalise.simulation.traffic.entity.CityInfrastructureData;
import com.vividsolutions.jts.geom.Envelope;
import de.pgalise.simulation.shared.entity.Building;
import de.pgalise.simulation.shared.JaxRSCoordinate;
import de.pgalise.simulation.shared.entity.NavigationNode;
import de.pgalise.simulation.shared.energy.EnergyProfileEnum;
import de.pgalise.simulation.traffic.TrafficGraph;
import de.pgalise.simulation.traffic.entity.TrafficNode;
import de.pgalise.simulation.traffic.entity.TrafficWay;
import de.pgalise.util.cityinfrastructure.BuildingEnergyProfileStrategy;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 *
 * @author richter
 */
public interface CityInfrastructureDataService {

	Envelope getBoundary();

	/**
	 * Returns the number of buildings.
	 *
	 * @param geolocation
	 * @param radiusInMeter
	 * @return
	 */
	Map<EnergyProfileEnum, List<Building>> getBuildings(
		JaxRSCoordinate geolocation,
		int radiusInMeter);

	/**
	 * Returns all buildings in the radius.
	 *
	 * @param centerPoint
	 * @param radiusInMeter
	 * @return
	 */
	List<Building> getBuildingsInRadius(JaxRSCoordinate centerPoint,
		int radiusInMeter);
	
	
	CityInfrastructureData createCityInfrastructureData() ;
	
	public NavigationNode getNearestNode(double latitude,
		double longitude);
		
	public NavigationNode getNearestStreetNode(double latitude,
		double longitude);
	
	NavigationNode getNearestJunctionNode(double latitude,
		double longitude);
	
	List<NavigationNode> getNodesInBoundary(Envelope boundary);
}
