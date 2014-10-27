/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.pgalise.simulation.traffic.entity.osm;

import de.pgalise.simulation.traffic.entity.BusRoute;
import de.pgalise.simulation.traffic.entity.BusStop;
import de.pgalise.simulation.traffic.entity.TrafficEdge;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

/**
 *
 * @author richter
 */
@Entity
public class OSMBusRoute extends BusRoute {
  private static final long serialVersionUID = 1L;
  @Enumerated(EnumType.ORDINAL)
  private OSMBusRouteTypeEnum oSMbusRouteType;

  protected OSMBusRoute() {
  }
  
  public OSMBusRoute(Long id) {
    super(id);
  }

  public OSMBusRoute(OSMBusRouteTypeEnum oSMbusRouteType,
    Long id) {
    super(id);
    this.oSMbusRouteType = oSMbusRouteType;
  }

  public OSMBusRoute(OSMBusRouteTypeEnum oSMbusRouteType,
    Long id,
    String routeShortName,
    String routeLongName,
    boolean used,
    List<BusStop> busStops,
    List<TrafficEdge> edgeList) {
    super(id,
      routeShortName,
      routeLongName,
      used,
      busStops,
      edgeList);
    this.oSMbusRouteType = oSMbusRouteType;
  }

  public OSMBusRoute(OSMBusRouteTypeEnum oSMbusRouteType,
    Long id,
    String routeShortName,
    String routeLongName,
    List<BusStop> busStops,
    List<TrafficEdge> initialEdgeList) {
    super(id,
      routeShortName,
      routeLongName,
      busStops,
      initialEdgeList);
    this.oSMbusRouteType = oSMbusRouteType;
  }

  public OSMBusRoute(OSMBusRouteTypeEnum oSMbusRouteType,
    Long id,
    String routeShortName,
    String routeLongName) {
    super(id,
      routeShortName,
      routeLongName);
    this.oSMbusRouteType = oSMbusRouteType;
  }
}
