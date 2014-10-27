/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.traffic.entity;

import de.pgalise.simulation.shared.entity.Building;
import de.pgalise.simulation.shared.entity.Identifiable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;

/**
 * A data container for data of {@link CityInfrastructureManager}. All
 * referenced entity can be associated with multiple
 * <tt>CityInfrastructureData</tt>s.
 *
 * @author richter
 */
/*
- get boundary from city (add reference property if necessary)
- contains both information about has-a relations which might be separated to a 
class in shared module (and referenced in City class) and in traffic module 
(@TODO)
 */
@Entity
public class CityInfrastructureData extends Identifiable {

  private static final long serialVersionUID = 1L;

  @ManyToMany
  private Set<CycleWay> cycleWays = new HashSet<>();
  @ManyToMany
  private Set<TrafficWay> landUseWays = new HashSet<>();
  @ManyToMany
  private Set<MotorWay> motorWays = new HashSet<>();
  @ManyToMany
  private Set<MotorWay> motorWaysWithBusStops = new HashSet<>();
  @ManyToMany
  private Set<TrafficNode> junctionNodes = new HashSet<>();
  @ManyToMany
  private Set<TrafficNode> nodes = new HashSet<>();
  @ManyToMany
  private Set<TrafficNode> streetNodes = new HashSet<>();
  @ManyToMany
  private Set<TrafficWay> ways = new HashSet<>();

  @ManyToMany
  private Set<TrafficWay> cycleAndMotorways = new HashSet<>();
  @ManyToMany
  private Set<TrafficNode> roundAbouts = new HashSet<>();
  @ManyToMany
  private Set<Building> buildings = new HashSet<>();
  @ManyToMany
  private Set<BusStop> busStops = new HashSet<>();
  @ManyToMany
  private Set<BusRoute> busRoutes = new HashSet<>();

  public CityInfrastructureData() {
  }

  public CityInfrastructureData(Long id) {
    super(id);
  }

  public Set<TrafficNode> getRoundAbouts() {
    return roundAbouts;
  }

  public void setRoundAbouts(
    Set<TrafficNode> roundAbouts) {
    this.roundAbouts = roundAbouts;
  }

  public Set<TrafficWay> getWays() {
    return ways;
  }

  public void setWays(    Set<TrafficWay> ways) {
    this.ways = ways;
  }

  public void setStreetNodes(Set<TrafficNode> streetNodes) {
    this.streetNodes = streetNodes;
  }

  public void setNodes(Set<TrafficNode> nodes) {
    this.nodes = nodes;
  }

  public void setMotorWaysWithBusStops(
    Set<MotorWay> motorWaysWithBusStops) {
    this.motorWaysWithBusStops = motorWaysWithBusStops;
  }

  public void setMotorWays(
    Set<MotorWay> motorWays) {
    this.motorWays = motorWays;
  }

  public void setLandUseWays(
    Set<TrafficWay> landUseWays) {
    this.landUseWays = landUseWays;
  }

  public void setJunctionNodes(Set<TrafficNode> junctionNodes) {
    this.junctionNodes = junctionNodes;
  }

  public void setCycleWays(
    Set<CycleWay> cycleWays) {
    this.cycleWays = cycleWays;
  }

  public void setCycleAndMotorways(
    Set<TrafficWay> cycleAndMotorways) {
    this.cycleAndMotorways = cycleAndMotorways;
  }

  public Set<TrafficNode> getStreetNodes() {
    return streetNodes;
  }

  public Set<TrafficNode> getNodes() {
    return nodes;
  }

  public Set<MotorWay> getWaysWithBusStops() {
    return motorWaysWithBusStops;
  }

  public Set<MotorWay> getMotorWays() {
    return motorWays;
  }

  public Set<TrafficWay> getLandUseWays() {
    return landUseWays;
  }

  public Set<TrafficNode> getJunctionNodes() {
    return junctionNodes;
  }

  public Set<CycleWay> getCycleWays() {
    return cycleWays;
  }

  public Set<TrafficWay> getCycleAndMotorways() {
    return cycleAndMotorways;
  }

  public Set<BusStop> getBusStops() {
    return this.busStops;
  }

  public void setBusStops(Set<BusStop> busStops) {
    this.busStops = busStops;
  }

  public Set<Building> getBuildings() {
    return buildings;
  }

  public void setBuildings(Set<Building> buildings) {
    this.buildings = buildings;
  }

  public void setBusRoutes(Set<BusRoute> busRoutes) {
    this.busRoutes = busRoutes;
  }

  public Set<BusRoute> getBusRoutes() {
    return busRoutes;
  }

}
