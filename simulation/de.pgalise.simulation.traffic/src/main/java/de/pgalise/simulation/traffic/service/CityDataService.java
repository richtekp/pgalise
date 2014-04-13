/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.traffic.service;

import com.vividsolutions.jts.geom.Envelope;
import de.pgalise.simulation.shared.entity.BaseCoordinate;
import de.pgalise.simulation.shared.energy.EnergyProfileEnum;
import de.pgalise.simulation.shared.entity.Building;
import de.pgalise.simulation.shared.entity.NavigationNode;
import de.pgalise.simulation.traffic.entity.TrafficCity;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 *
 * @author richter
 */
/*
OSM files or similar data sources should not produce CityInfrastructure objects, 
because they only provide information about traffic data and has has-a 
relationship to with City. City can contain all geograpical and infrastructure 
information very well.
*/
public interface CityDataService extends Serializable {

  TrafficCity createCity();
  
  //////////////////////////////
  //helper
  //////////////////////////////
  /**
   * Returns the number of buildings.
   *
   * @param geolocation
   * @param radiusInMeter
   * @return
   */
  Map<EnergyProfileEnum, List<Building>> getBuildings(
    BaseCoordinate geolocation,
    int radiusInMeter);

  /**
   * Returns all buildings in the radius.
   *
   * @param centerPoint
   * @param radiusInMeter
   * @return
   */
  List<Building> getBuildingsInRadius(BaseCoordinate centerPoint,
    int radiusInMeter);

  public NavigationNode getNearestNode(double latitude,
    double longitude);

  public NavigationNode getNearestStreetNode(double latitude,
    double longitude);

  NavigationNode getNearestJunctionNode(double latitude,
    double longitude);

  List<NavigationNode> getNodesInBoundary(Envelope boundary);
}
