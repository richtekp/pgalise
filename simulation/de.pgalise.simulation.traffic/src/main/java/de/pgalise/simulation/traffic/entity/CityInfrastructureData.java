/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.traffic.entity;

import de.pgalise.simulation.shared.entity.Building;
import de.pgalise.simulation.shared.entity.Identifiable;
import java.util.LinkedList;
import java.util.List;
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
 get boundary from city (add reference property if necessary)
 */
@Entity
public class CityInfrastructureData extends Identifiable {

  private static final long serialVersionUID = 1L;

  @ManyToMany
  private List<TrafficWay> cycleWays = new LinkedList<>();
  @ManyToMany
  private List<TrafficWay> landUseWays = new LinkedList<>();
  @ManyToMany
  private List<TrafficWay> motorWays = new LinkedList<>();
  @ManyToMany
  private List<TrafficWay> motorWaysWithBusStops = new LinkedList<>();
  @ManyToMany
  private List<TrafficNode> junctionNodes = new LinkedList<>();
  @ManyToMany
  private List<TrafficNode> nodes = new LinkedList<>();
  @ManyToMany
  private List<TrafficNode> streetNodes = new LinkedList<>();
  @ManyToMany
  private List<TrafficWay> ways = new LinkedList<>();

  @ManyToMany
  private List<TrafficWay> cycleAndMotorways = new LinkedList<>();
  @ManyToMany
  private List<TrafficNode> roundAbouts = new LinkedList<>();
  @ManyToMany
  private List<Building> buildings = new LinkedList<>();
  @ManyToMany
  private List<BusStop> busStops = new LinkedList<>();

  public CityInfrastructureData() {
  }

  public CityInfrastructureData(Long id) {
    super(id);
  }

  public List<TrafficNode> getRoundAbouts() {
    return roundAbouts;
  }

  public void setRoundAbouts(
    List<TrafficNode> roundAbouts) {
    this.roundAbouts = roundAbouts;
  }

  public void setWays(
    List<TrafficWay> ways) {
    this.ways = ways;
  }

  public void setStreetNodes(List<TrafficNode> streetNodes) {
    this.streetNodes = streetNodes;
  }

  public void setNodes(List<TrafficNode> nodes) {
    this.nodes = nodes;
  }

  public void setMotorWaysWithBusStops(
    List<TrafficWay> motorWaysWithBusStops) {
    this.motorWaysWithBusStops = motorWaysWithBusStops;
  }

  public void setMotorWays(
    List<TrafficWay> motorWays) {
    this.motorWays = motorWays;
  }

  public void setLandUseWays(
    List<TrafficWay> landUseWays) {
    this.landUseWays = landUseWays;
  }

  public void setJunctionNodes(List<TrafficNode> junctionNodes) {
    this.junctionNodes = junctionNodes;
  }

  public void setCycleWays(
    List<TrafficWay> cycleWays) {
    this.cycleWays = cycleWays;
  }

  public void setCycleAndMotorways(
    List<TrafficWay> cycleAndMotorways) {
    this.cycleAndMotorways = cycleAndMotorways;
  }

  public List<TrafficWay> getWays() {
    return ways;
  }

  public List<TrafficNode> getStreetNodes() {
    return streetNodes;
  }

  public List<TrafficNode> getNodes() {
    return nodes;
  }

  public List<TrafficWay> getMotorWaysWithBusStops() {
    return motorWaysWithBusStops;
  }

  public List<TrafficWay> getMotorWays() {
    return motorWays;
  }

  public List<TrafficWay> getLandUseWays() {
    return landUseWays;
  }

  public List<TrafficNode> getJunctionNodes() {
    return junctionNodes;
  }

  public List<TrafficWay> getCycleWays() {
    return cycleWays;
  }

  public List<TrafficWay> getCycleAndMotorways() {
    return cycleAndMotorways;
  }

  public List<BusStop> getBusStops() {
    return this.busStops;
  }

  protected void setBusStops(List<BusStop> busStops) {
    this.busStops = busStops;
  }

  public List<Building> getBuildings() {
    return buildings;
  }

  public void setBuildings(List<Building> buildings) {
    this.buildings = buildings;
  }

}
