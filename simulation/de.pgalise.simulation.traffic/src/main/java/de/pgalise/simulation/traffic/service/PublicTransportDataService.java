/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.traffic.service;

import de.pgalise.simulation.traffic.TrafficGraph;
import de.pgalise.simulation.traffic.entity.BusRoute;
import de.pgalise.simulation.traffic.entity.BusStop;
import de.pgalise.simulation.traffic.entity.CityInfrastructureData;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Set;

/**
 *
 * @author richter
 * @param <I>
 */
public interface PublicTransportDataService<I extends InputStream> extends Serializable {

  public Set<BusStop> getBusStops();
  
  public Set<BusRoute> getBusRoutes();
  
  public void parse(I in) throws IOException;

  /**
   * insert <tt>busStops</tt> into <tt>cityInfrastructureData</tt>
   *
   * @param cityInfrastructureData
   * @param busStops
   * @param trafficGraph
   * @see CityInfrastructureData#getMotorWaysWithBusStops()
   */
  /*
   this makes the parsing process of the bus stop information 
   independent from the parsing of the geo information, but keeps 
   CityInfrastructureData together (if bus system is changed, it 
   shouldn't be necessary to parse again)
   */
  public void insertBusStops(CityInfrastructureData cityInfrastructureData,
    Set<BusStop> busStops,
    TrafficGraph trafficGraph);
}
