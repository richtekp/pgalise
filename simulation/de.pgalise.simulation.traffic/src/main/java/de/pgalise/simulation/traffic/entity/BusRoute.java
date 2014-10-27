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
package de.pgalise.simulation.traffic.entity;

import de.pgalise.simulation.traffic.TransportLineTypeEnum;
import de.pgalise.simulation.traffic.entity.TransportLineInformation;
import de.pgalise.simulation.traffic.entity.gtfs.GTFSBusAgency;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.MapKeyColumn;

/**
 * Contains information about a bus route. The ID, long name, short, name, the
 * route type and a flag, if it's used or not.
 * 
 * Bus routes are directed in one way (compliant with OSM) in order to make it 
 * possible to differentiate different course in different directions.
 *
 * @author Lena
 */
@Entity
@ManagedBean
@SessionScoped
public class BusRoute extends TransportLineInformation
{

	/**
	 * Serial
	 */
	private static final long serialVersionUID = 3115299698580903433L;

	/**
	 * Short name
	 */
	private String routeShortName;

	/**
	 * Long name
	 */
	private String routeLongName;

	/**
	 * Used in simulation
	 */
	private Boolean used;

	private BusData bus;
  /**
   * Mapping between minutes after Montag 00:00 and the bus stop which is 
   * served at this time
   */
  @Embedded
  private Map<BusStop,Set<Integer>> schedule = new HashMap<>();

	@Column(name = "ROUTE_DESC")
	private String routeDesc;
  @ManyToMany
  private List<BusStop> busStops = new LinkedList<>();
  @ManyToMany
  private List<TrafficEdge> edgeList = new LinkedList<>();

	/**
	 * Default constructor
	 */
	protected BusRoute() {
	}
  
  public BusRoute(Long id) {
    super(id, TransportLineTypeEnum.BUS);
  }

	public BusRoute(Long id,
					String routeShortName,
					String routeLongName) {
		this(id);
		this.routeShortName = routeShortName;
		this.routeLongName = routeLongName;
  }

	public BusRoute(Long id,
					String routeShortName,
					String routeLongName,
          boolean used) {
		this(id);
		this.routeShortName = routeShortName;
		this.routeLongName = routeLongName;
    this.used = used;
  }

	public BusRoute(Long id,
					String routeShortName,
					String routeLongName,
          List<BusStop> initialBusStops,
          List<TrafficEdge> initialEdgeList) {
		this(id,
						routeShortName,
						routeLongName);
    this.busStops = initialBusStops;
    this.edgeList = initialEdgeList;
  }

	public BusRoute(Long id,
					String routeShortName,
					String routeLongName,
          boolean used,
          List<BusStop> initialBusStops,
          List<TrafficEdge> initialEdgeList) {
    this(id,
      routeShortName,
      routeLongName,
      initialBusStops,
      initialEdgeList);
	}

  public void setSchedule(Map<BusStop,Set<Integer>> schedule) {
    this.schedule = schedule;
  }

  public Map<BusStop,Set<Integer>> getSchedule() {
    return schedule;
  }

	/**
	 * @return the routeShortName
	 */
	public String getRouteShortName() {
		return routeShortName;
	}

	/**
	 * @param routeShortName the routeShortName to set
	 */
	public void setRouteShortName(String routeShortName) {
		this.routeShortName = routeShortName;
	}

	/**
	 * @return the routeLongName
	 */
	public String getRouteLongName() {
		return routeLongName;
	}

	/**
	 * @param routeLongName the routeLongName to set
	 */
	public void setRouteLongName(String routeLongName) {
		this.routeLongName = routeLongName;
	}

	public Boolean getUsed() {
		return used;
	}

	public void setUsed(Boolean used) {
		this.used = used;
	}

  /**
   * @return the routeDesc
   */
  public String getRouteDesc() {
    return routeDesc;
  }

  /**
   * @param routeDesc the routeDesc to set
   */
  public void setRouteDesc(String routeDesc) {
    this.routeDesc = routeDesc;
  }

  public void setEdgeList(List<TrafficEdge> edgeList) {
    this.edgeList = edgeList;
  }

  public List<TrafficEdge> getEdgeList() {
    return edgeList;
  }

  public void setBusStops(List<BusStop> busStops) {
    this.busStops = busStops;
  }

  public List<BusStop> getBusStops() {
    return busStops;
  }
}
